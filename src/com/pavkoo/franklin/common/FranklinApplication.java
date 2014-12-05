package com.pavkoo.franklin.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Application;

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
	private HashMap<String, List<SignRecords>> newWeek;

	public HashMap<String, List<SignRecords>> getNewWeek() {
		return newWeek;
	}
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
		morals = mgr.loadMorals();
		comments = mgr.loadComment();
		welcomes = mgr.loadMottos();
	}

	public void initSaveData() {
		saveAppConfig(appCon, true);
		saveComments(comments, true);
		saveMorals(morals, true);
		saveWelcomes(welcomes, true);
	}

	public void updateMorals(boolean deleteAll) {
		mgr.updateMorals(morals, deleteAll);
		morals = mgr.loadMorals();
	}
	public void updateMorals() {
		updateMorals(false);
	}

	public void updateMottos() {
		mgr.updateMottos(welcomes);
		welcomes = mgr.loadMottos();
	}

	public void loadThisWeek(Date startDate, Date endDate) {
		newWeek = mgr.getNewWeekSing(startDate, endDate);
	}

	// 重新开始新的周期
	public void markMailStone() {
		int appHistoryCount = appCon.getHistoryCount();
		mPreference.saveHistoryMorals(morals, appHistoryCount);
		appHistoryCount++;
		appCon.setHistoryCount(appHistoryCount);
		appCon.setProjectStarted(false);
		saveAppConfig(appCon, true);

		UtilsClass.reArrangeDate(morals);
		updateMorals(true);
		mPreference.saveHistoryComments(comments, appHistoryCount);
		// mgr.clearComment();
		// this.comments.clear();
		List<SignRecords> slist = mgr.loadAllSginRecord();
		mPreference.saveHistorySignRecords(slist, appHistoryCount);
		mgr.clearSignrecord();
	}

	public void saveComments(List<Comment> commentList) {
		saveComments(commentList, false);
	}

	public void saveComments(List<Comment> commentList, boolean toDB) {
		if (toDB) {
			mgr.importComms(commentList);
			comments = mgr.loadComment();
			return;
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
			morals = mgr.loadMorals();
			return;
		}
		morals = moralList;
	}

}
