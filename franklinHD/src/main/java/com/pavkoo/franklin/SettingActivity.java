package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pavkoo.franklin.common.ApplicationConfig;
import com.pavkoo.franklin.common.Comment;
import com.pavkoo.franklin.common.FranklinApplication;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import com.pavkoo.franklin.controls.SettingSystemHelpDialog;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends FragmentActivity {
	private int[] titleList = {R.string.titleTenperance, R.string.titleSilence, R.string.titleOrder, R.string.titleResolution,
			R.string.titleFrugality, R.string.titleIndustry, R.string.titleSincerity, R.string.titleJustice, R.string.titleModeration,
			R.string.titleCleanliness, R.string.titleTranquillity, R.string.titleChastity, R.string.titleHumility};
	private int[] decriptionList = {R.string.titleTenperanceDes, R.string.titleSilenceDes, R.string.titleOrderDes,
			R.string.titleResolutionDes, R.string.titleFrugalityDes, R.string.titleIndustryDes, R.string.titleSincerityDes,
			R.string.titleJusticeDes, R.string.titleModerationDes, R.string.titleCleanlinessDes, R.string.titleTranquillityDes,
			R.string.titleChastityDes, R.string.titleHumilityDes};
	private int[] mottoList = {R.string.titleTenperanceMotto, R.string.titleSilenceMotto, R.string.titleOrderMotto,
			R.string.titleResolutionMotto, R.string.titleFrugalityMotto, R.string.titleIndustryMotto, R.string.titleSincerityMotto,
			R.string.titleJusticeMotto, R.string.titleModerationMotto, R.string.titleCleanlinessMotto, R.string.titleTranquillityMotto,
			R.string.titleChastityMotto, R.string.titleHumilityMotto};
	private int[] welcomeList = {R.string.welcome1, R.string.welcome2, R.string.welcome3, R.string.welcome4, R.string.welcome5,
			R.string.welcome11, R.string.welcome12, R.string.welcome13, R.string.welcome14, R.string.welcome15, R.string.welcome16};

	private RadioGroup rgSetting;
	private FragmentManager fmManager;
	private FranklinApplication app;
	private AnimMessage amMessage;
	private TextView tvSettingDone;
	private TextView tvSettingRestore;
	private TextView tvSettingHelp;

	private RadioButton rbSettingProjectItem;
	private RadioButton rbSettingCycle;
	private RadioButton rbSettingWelcome;
	private RadioButton rbAppSetting;
	private long touchTime;
	private boolean finishModifyfalg;
	private final int WaitTime = 4000;

	public AnimMessage getAmMessage() {
		return amMessage;
	}

	public FranklinApplication getApp() {
		return app;
	}

	private ApplicationConfig config;
	private boolean cycleClicked;

	@Override
	protected void onCreate(Bundle argments) {
		super.onCreate(argments);
		setContentView(R.layout.activity_setting);
		cycleClicked = false;
		app = (FranklinApplication) this.getApplication();
		Intent intent = this.getIntent();
		/*
		 * 0 - rbSettingProjectItem 1 - rbSettingCycle 2 - rbSettingWelcome
		 */
		int startMode = R.id.rbSettingProjectItem;
		if (intent != null) {
			startMode = intent.getIntExtra("STARTMODE", R.id.rbSettingProjectItem);
			finishModifyfalg = intent.getBooleanExtra("FinishModify", false);
		}
		config = getApp().getAppCon();
		initAppWithDefaultData(false);
		fmManager = getSupportFragmentManager();
		rgSetting = (RadioGroup) findViewById(R.id.rgSetting);
		tvSettingRestore = (TextView) findViewById(R.id.tvSettingRestore);
		tvSettingDone = (TextView) findViewById(R.id.tvSettingDone);
		tvSettingHelp = (TextView) findViewById(R.id.tvSettingHelp);
		rbSettingProjectItem = (RadioButton) findViewById(R.id.rbSettingProjectItem);
		rbSettingCycle = (RadioButton) findViewById(R.id.rbSettingCycle);
		rbSettingWelcome = (RadioButton) findViewById(R.id.rbSettingWelcome);
		rbAppSetting = (RadioButton) findViewById(R.id.rbAppSetting);
		new SettingSystemHelpDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		rgSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				amMessage.reset();
				Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
				changeFragment(fragment);
			}
		});
		tvSettingRestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				restoreDefault();
			}
		});
		tvSettingHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (rgSetting.getCheckedRadioButtonId()) {
					case R.id.rbSettingProjectItem :
						amMessage.showMessage(getString(R.string.dragToChangeCycle));
						break;
					case R.id.rbSettingCycle :
						amMessage.showMessage(getString(R.string.hlepCycle));
						break;
					case R.id.rbSettingWelcome :
						amMessage.showMessage(getString(R.string.helpwelcome));
						break;
				}
			}
		});
		amMessage = (AnimMessage) findViewById(R.id.llSettingMessage);
		((RadioButton) findViewById(startMode)).setChecked(true);
	}

	public void changeFragment(Fragment fragment) {
		if (app.getMorals() == null) {
			amMessage.showMessage(getString(R.string.havenoitem));
			rbSettingProjectItem.setChecked(true);
			return;
		}
		FragmentTransaction transaction = fmManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		transaction.replace(R.id.flSettingContent, fragment);
		transaction.commit();
		fmManager.executePendingTransactions();
		iniViewData();
	}

	public static class FragmentFactory {
		public static Fragment getInstanceByIndex(int index) {
			Fragment fragment = null;
			switch (index) {
				case R.id.rbSettingProjectItem :
					fragment = new SettingProjectItemFragment();
					break;
				case R.id.rbSettingCycle :
					fragment = new SettingCycleFragment();
					break;
				case R.id.rbSettingWelcome :
					fragment = new SettingWelcomeFragment();
					break;
				case R.id.rbAppSetting :
					fragment = new SettingSystemFragment();
					break;
			}
			return fragment;
		}
	}

	public void iniViewData() {
		if (config.isFrist() && !config.isProjectStarted()) {
			rbAppSetting.setVisibility(View.GONE);
			switch (rgSetting.getCheckedRadioButtonId()) {
				case R.id.rbSettingProjectItem :
					tvSettingDone.setText(R.string.next);
					tvSettingDone.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (app.getMorals().size() < 1) {
								amMessage.showMessage(getString(R.string.havenoitem));
								return;
							}
							rbSettingCycle.setChecked(true);
						}
					});
					break;
				case R.id.rbSettingCycle :
					tvSettingDone.setText(R.string.next);
					tvSettingDone.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (!cycleClicked) {
								amMessage.showMessage(getString(R.string.cannotchange4), AnimMessageType.WARNING);
								cycleClicked = true;
								return;
							}
							rbSettingWelcome.setChecked(true);
						}
					});
					break;
				case R.id.rbSettingWelcome :
					finishSetting();
					break;
			}
		} else {
			tvSettingRestore.setVisibility(View.GONE);
			finishSetting();
			switch (rgSetting.getCheckedRadioButtonId()) {
				case R.id.rbSettingProjectItem :
					rbSettingWelcome.setVisibility(View.GONE);
					rbAppSetting.setVisibility(View.GONE);
					rbSettingCycle.setVisibility(View.GONE);
					rbSettingProjectItem.setVisibility(View.VISIBLE);
					break;
				case R.id.rbSettingWelcome :
					rbSettingProjectItem.setVisibility(View.GONE);
					rbAppSetting.setVisibility(View.GONE);
					rbSettingCycle.setVisibility(View.GONE);
					rbSettingWelcome.setVisibility(View.VISIBLE);
					break;
				case R.id.rbAppSetting :
					rbSettingProjectItem.setVisibility(View.GONE);
					rbSettingWelcome.setVisibility(View.GONE);
					rbSettingCycle.setVisibility(View.GONE);
					tvSettingHelp.setVisibility(View.GONE);
					rbAppSetting.setVisibility(View.VISIBLE);
					break;
			}
		}
	}

	private void finishSetting() {
		tvSettingDone.setText(R.string.done);
		tvSettingDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (config.isFrist() || !config.isProjectStarted()) {
					config.setFrist(false);
					config.setIsselfConfiged(true);
					if (!config.isProjectStarted()) {
						config.setProjectStarted(true);
					}
					if (config.isFrist()) {
						config.setFirstUse(new Date());
					}
					getApp().saveAppConfig(config);
					buildAllCycleDate();
					getApp().initSaveData();
					Intent helperIntent = new Intent(SettingActivity.this, HelperActivity.class);
					startActivity(helperIntent);
					finish();
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				} else {
					switch (rgSetting.getCheckedRadioButtonId()) {
						case R.id.rbSettingProjectItem :
							if (app.getMorals().size() < 1) {
								amMessage.showMessage(getString(R.string.havenoitem));
								return;
							}
							if (finishModifyfalg) {
								getApp().updateMorals(true);
							} else {
								getApp().updateMorals();
							}

							break;
						case R.id.rbSettingWelcome :
							getApp().updateMottos();
							break;
					}
					Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
					SettingActivity.this.startActivity(mainIntent);
					SettingActivity.this.finish();
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
	}

	public void restoreDefault() {
		getApp().getMgr().restoretodefault();
		initAppWithDefaultData(true);
		buildAllCycleDate();
		((RadioButton) findViewById(R.id.rbSettingProjectItem)).setChecked(true);
		SettingProjectItemFragment setfgm = (SettingProjectItemFragment) fmManager.findFragmentById(R.id.flSettingContent);
		if (setfgm != null) {
			setfgm.ReStore();
		}
		amMessage.showMessage(getString(R.string.restoreToDefault), AnimMessage.AnimMessageType.WARNING);
	}
	private void initAppWithDefaultData(boolean force) {
		if (config == null) {
			config = getApp().forceCreateAppCon();
		}
		if (config.isDefaultSaved() && !force)
			return;
		List<Moral> morals = new ArrayList<Moral>();
		Moral moral;
		for (int i = 0; i < titleList.length; i++) {
			moral = new Moral();
			moral.setTitle(getResources().getString(titleList[i]));
			moral.setTitleDes(getResources().getString(decriptionList[i]));
			moral.setTitleMotto(getResources().getString(mottoList[i]));
			moral.setCycle(7);
			morals.add(moral);
		}
		this.getApp().saveMorals(morals);

		List<Comment> comments = new ArrayList<Comment>();
		this.getApp().saveComments(comments);

		List<String> welcomes = new ArrayList<String>();
		for (int i = 0; i < welcomeList.length; i++) {
			welcomes.add(getResources().getString(welcomeList[i]));
		}
		this.getApp().saveWelcomes(welcomes);
		config.setDefaultSaved(true);
		this.getApp().saveAppConfig(config);
	}

	public void restore() {
		config.setFrist(true);
		config.setIsselfConfiged(false);
		config.setProjectStarted(false);
		config.setFirstUse(new Date());
		this.getApp().saveAppConfig(config);
	}

	// 计算每个周期将要花费的时间，并设置起周期
	public void buildAllCycleDate() {
		List<Moral> morals = this.getApp().getMorals();
		UtilsClass.reArrangeDate(morals);
	}

	// 计算每个周期将要花费的时间，并设置起周期
	public void buildAllCycleDateNew() {
		Date end = null;
		Date begin = null;
		Calendar calendar = Calendar.getInstance();
		List<Moral> morals = this.getApp().getMorals();
		if (morals == null) {
			return;
		}
		int startIndex = morals.size();
		for (int i = 0; i < morals.size(); i++) {
			Moral moral = morals.get(i);
			if (moral.isFinished()) {
				continue;
			}
			// this code must be executed
			if (moral.getCurrentDayInCycle() != 0) {
				calendar.setTime(moral.getEndDate());
				calendar.add(Calendar.DATE, 1);
				startIndex = i + 1;
				break;
			}
		}
		for (int i = startIndex; i < morals.size(); i++) {
			Moral moral = morals.get(i);
			begin = calendar.getTime();
			if (i == startIndex) {
				begin = calendar.getTime();
			} else {
				calendar.add(Calendar.DATE, 1);
				begin = calendar.getTime();
			}
			calendar.add(Calendar.DATE, moral.getCycle() - 1); // -1 means day
																// between
																// should sub 1
			end = calendar.getTime();
			moral.setStartDate(begin);
			moral.setEndDate(end);
		}
	}

	@Override
	public void onBackPressed() {
		long currentPressed = System.currentTimeMillis();
		if ((currentPressed - touchTime) >= WaitTime) {
			amMessage.showMessage(getString(R.string.pressToExit), AnimMessageType.Hint);
			touchTime = currentPressed;
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
