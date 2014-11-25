package com.pavkoo.franklin.common;

import java.util.Date;
import java.util.List;
import android.app.Application;
import android.util.Log;

public class FranklinApplication extends Application {

	private static FranklinApplication singleton;
	public static int AnimationDuration = 500;
	public static int AnimationDurationShort = 400;

	private DBManager mgr;
	public DBManager getMgr() {
		return mgr;
	}

	private ApplicationConfig appCon;
	private List<Moral> morals;
	private List<Comment> comments;
	private List<String> welcomes;
	private List<SignRecords> signRecordList;

	public List<String> getWelcomes() {
		return welcomes;
	}

	private SharePreferenceService mPreference;

	public ApplicationConfig getAppCon() {
		return appCon;
	}

	public ApplicationConfig forceCreateAppCon() {
		appCon = mPreference.loadAppconfig();
		return appCon;
	}

	public List<Moral> getMorals() {
		return morals;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public SharePreferenceService getmPreference() {
		return mPreference;
	}

	public static FranklinApplication getInstance() {
		return singleton;
	}

	public SharePreferenceService getPreference() {
		return mPreference;
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		singleton = this;
		mPreference = new SharePreferenceService(this);
		mgr = new DBManager(this);
	}

	public void loadData() {
		// old version use morals saved in SharePreference ,new version use
		// SQLite DB
		morals = mPreference.loadMorals();
		if (morals != null) {
			appCon = mPreference.loadAppconfig();
			morals = mPreference.loadMorals();
			comments = mPreference.loadComments();
			welcomes = mPreference.loadWelcomes();
			mgr.importAppCon(appCon);
			mgr.importMottos(welcomes);
			mgr.importComms(comments);
			mgr.importMorals(morals);
			mgr.importSign(morals);
			mPreference.deleteAllFile();
		}
		if (mgr.needIniApp()) {
			appCon = new ApplicationConfig();
			appCon.setFrist(true);
			appCon.setIsselfConfiged(false);
			appCon.setProjectStarted(false);
			appCon.setDefaultSaved(false);
			appCon.setFirstUse(new Date());
			appCon.setLastUse(new Date());
			appCon.setHistoryCount(0);
			mgr.importAppCon(appCon);
		}
		loadDataFromDb();
	}

	private void loadDataFromDb() {
		appCon = mgr.loadConfig();
	}

	public void initSaveData() {
		saveAppConfig(appCon,true);
		saveComments(comments,true);
		saveMorals(morals,true);
		saveWelcomes(welcomes,true);
		setSignRecordList(mgr.loadSignRecord());
	}
	
	public void updateMorals(){
		mgr.updateMorals(morals);
		morals = mgr.loadMorals();
	}
	
	public void updateMottos(){
		mgr.updateMottos(welcomes);
		welcomes =  mgr.loadMottos();
	}

	// 重新开始新的周期
	public void markMailStone() {
		int appHistoryCount = appCon.getHistoryCount();
		mPreference.saveHistoryMorals(morals, appHistoryCount);
		appHistoryCount++;
		appCon.setHistoryCount(appHistoryCount);
		appCon.setProjectStarted(false);
		saveAppConfig(appCon);

		UtilsClass.reArrangeDate(morals);
		for (int i = 0; i < morals.size(); i++) {
			morals.get(i).reSet();
			Log.i("Day", "Start:" + morals.get(i).getStartDate().toString()
					+ "------------ End:"
					+ morals.get(i).getEndDate().toString());
		}
		saveMorals(morals);
		mPreference.saveHistoryComments(comments, appHistoryCount);
		comments.clear();
		saveComments(comments);
	}

	public void saveComments(List<Comment> commentList) {
		saveComments(commentList, false);
	}

	public void saveComments(List<Comment> commentList, boolean toDB) {
		if (toDB) {
			mgr.importComms(commentList);
			commentList = mgr.loadComment();
		}
		comments = commentList;
	}

	public void saveWelcomes(final List<String> welcomeList) {
		saveWelcomes(welcomeList, false);
	}

	public void saveWelcomes(final List<String> welcomeList, boolean toDB) {
		if (toDB) {
			mgr.importMottos(welcomeList);
		}
		welcomes = welcomeList;
	}

	public void saveAppConfig(final ApplicationConfig appConfig) {
		saveAppConfig(appConfig, false);
	}

	public void saveAppConfig(final ApplicationConfig appConfig, boolean toDB) {
		if (toDB) {
			mgr.updateConfig(appConfig);
		}
		appCon = appConfig;
	}

	public void saveMorals(List<Moral> moralList) {
		saveMorals(moralList, false);
	}

	public void saveMorals(List<Moral> moralList, boolean toDB) {
		if (toDB) {
			mgr.importMorals(moralList);
			moralList = mgr.loadMorals();
		}
		morals = moralList;
	}

	public List<SignRecords> getSignRecordList() {
		return signRecordList;
	}

	public void setSignRecordList(List<SignRecords> signRecordList) {
		this.signRecordList = signRecordList;
	}
}
