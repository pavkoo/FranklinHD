package com.pavkoo.franklin.common;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.util.Log;

public class FranklinApplication extends Application {

	private static FranklinApplication singleton;
	public static int AnimationDuration = 500;
	public static int AnimationDurationShort = 400;

	private ApplicationConfig appCon;
	private List<Moral> morals;
	private List<Comment> comments;
	private List<String> welcomes;

	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

	public List<String> getWelcomes() {
		return welcomes;
	}

	private SharePreferenceService mPreference;

	public ApplicationConfig getAppCon() {
		return appCon;
	}
	
	public ApplicationConfig forceCreateAppCon(){
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
	}

	public void loadData() {
		appCon = mPreference.loadAppconfig();
		morals = mPreference.loadMorals();
		comments = mPreference.loadComments();
		welcomes = mPreference.loadWelcomes();
	}

	public void saveData(boolean async) {
		if (async) {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					appCon = mPreference.saveAppConfig(appCon);
					morals = mPreference.saveMorals(morals);
					comments = mPreference.saveComments(comments);
					welcomes = mPreference.saveWelcomes(welcomes);
				}
			});
		} else {
			appCon = mPreference.saveAppConfig(appCon);
			morals = mPreference.saveMorals(morals);
			comments = mPreference.saveComments(comments);
			welcomes = mPreference.saveWelcomes(welcomes);
		}
	}

	public void saveData() {
		saveData(false);
	}

	public void saveMorals(final List<Moral> moralList, boolean async) {
		if (async) {
			fixedThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					morals = mPreference.saveMorals(moralList);
				}
			});
		} else {
			morals = mPreference.saveMorals(moralList);
		}
	}

	public void saveMorals(final List<Moral> moralList) {
		saveMorals(moralList, false);
	}
	
	//重新开始新的周期
	public void markMailStone(){
		int appHistoryCount = appCon.getHistoryCount();
		mPreference.saveHistoryMorals(morals,appHistoryCount);
		appHistoryCount++;
		appCon.setHistoryCount(appHistoryCount);
		appCon.setProjectStarted(false);
		saveAppConfig(appCon);

		UtilsClass.reArrangeDate(morals);
		for(int i =0;i<morals.size();i++){
			morals.get(i).reSet();
			Log.i("Day", "Start:"+morals.get(i).getStartDate().toString() + "------------ End:"+morals.get(i).getEndDate().toString());
		}
		saveMorals(morals);
		mPreference.saveHistoryComments(comments, appHistoryCount);
		comments.clear();
		saveComments(comments);
	}

	public void saveComments(final List<Comment> commentList, boolean async) {
		if (async) {
			fixedThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					comments = mPreference.saveComments(commentList);
				}
			});
		} else {
			comments = mPreference.saveComments(commentList);
		}
	}

	public void saveComments(final List<Comment> commentList) {
		saveComments(commentList, false);
	}

	public void saveWelcomes(final List<String> welcomeList, boolean async) {
		if (async) {
			fixedThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					welcomes = mPreference.saveWelcomes(welcomeList);
				}
			});
		} else {
			welcomes = mPreference.saveWelcomes(welcomeList);
		}
	}

	public void saveWelcomes(final List<String> welcomeList) {
		saveWelcomes(welcomeList, false);
	}

	public void saveAppConfig(final ApplicationConfig appConfig, boolean async) {
		if (async) {
			fixedThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					appCon = mPreference.saveAppConfig(appConfig);
				}
			});
		} else {
			appCon = mPreference.saveAppConfig(appConfig);
		}
	}

	public void saveAppConfig(final ApplicationConfig appConfig) {
		saveAppConfig(appConfig, false);
	}
}
