package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.pavkoo.franklin.common.ApplicationConfig;
import com.pavkoo.franklin.common.FranklinApplication;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.SplashFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends FragmentActivity {
	private ImageView ivSplash;
	private ImageView ivCloud;
	private ImageView ivCloud2;
	private LinearLayout llSpVictory;
	private ViewPager splashPager;
	private TextView tvSplashGo;
	private TextView tvSplashHint;
	private WelcomesAdapter adapter;
	private final int START_SPLASH_OFFSET = 4000;
	private ApplicationConfig config;
	private FranklinApplication app;
	private int initWelcomes = -1;
	private boolean stopAnimation = false;
	private Handler myHandle;
	private Runnable myRunable;
	private boolean fromMain = false;
	private List<String> showWelcomes;

	public FranklinApplication getApp() {
		return app;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle args = this.getIntent().getExtras();
		if (args != null) {
			fromMain = args.getBoolean("flag");
		}
		app = (FranklinApplication) this.getApplication();
		setContentView(R.layout.activity_splash);
		ivSplash = (ImageView) findViewById(R.id.ivSplash);
		ivCloud = (ImageView) findViewById(R.id.ivCloud);
		ivCloud2 = (ImageView) findViewById(R.id.ivCloud2);
		llSpVictory = (LinearLayout) findViewById(R.id.llSpVictory);
		splashPager = (ViewPager) findViewById(R.id.splashPager);
		tvSplashGo = (TextView) findViewById(R.id.tvSplashGo);
		tvSplashHint = (TextView) findViewById(R.id.tvSplashHint);
		initViewData();
	}

	protected void initViewData() {
		if (!fromMain) {
			getApp().loadData();
			config = getApp().getAppCon();
			if (!config.isFrist()) {
				selectImage();
				myHandle = new Handler();
				myRunable = new Runnable() {
					@Override
					public void run() {
						changeActivity();
					}
				};
				myHandle.postDelayed(myRunable, START_SPLASH_OFFSET);
			} else {
				Intent tutoriIntent = new Intent(SplashActivity.this,
						TutorialsActivity.class);
				startActivity(tutoriIntent);
				finish();
			}
		} else {
			config = getApp().getAppCon();
			selectImage();
			showGO();
		}
	}

	private void showGO() {
		if (myHandle != null) {
			myHandle.removeCallbacks(myRunable);
		}
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.fade_bottom_in);
		tvSplashGo.setAnimation(anim);
		tvSplashGo.setVisibility(View.VISIBLE);
		tvSplashHint.setVisibility(View.GONE);
		tvSplashGo.setEnabled(true);
		anim.start();
	}

	private void changeActivity() {
		if (config.isFrist() || !config.isIsselfConfiged()) {
			Intent setIntent = new Intent(SplashActivity.this,
					SettingActivity.class);
			setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
			SplashActivity.this.startActivity(setIntent);
		} else {
			Intent mainIntent = new Intent(SplashActivity.this,
					MainActivity.class);
			SplashActivity.this.startActivity(mainIntent);
		}
		SplashActivity.this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private void selectImage() {
		showWelcomes = getApp().getWelcomes();
		if (showWelcomes != null) {
			//do not save temp R.string.welcome11 to SD cards
			if (showWelcomes.size() == 0) {
				showWelcomes = new ArrayList<String>();
			}
		}
		if (showWelcomes.size() == 0) {
			showWelcomes.add(getString(R.string.welcome11));
		}
		adapter = new WelcomesAdapter(getSupportFragmentManager(), showWelcomes);
		splashPager.setAdapter(adapter);
		if (config==null){
			ivSplash.setImageResource(R.drawable.treesmall);
			return;
		}
		if (config.isFrist() || getApp().getMorals() == null) {
			ivSplash.setImageResource(R.drawable.treesmall);
			return;
		}
		Date firstuse = config.getFirstUse();
		int last = getApp().getMorals().size() - 1;
		Date endTerm = getApp().getMorals().get(last).getEndDate();
		float duration = UtilsClass.dayCount(firstuse, endTerm);
		float currentUse = UtilsClass.dayCount(firstuse, new Date());
		float perDuiration = duration / 3;
		if (currentUse <= perDuiration) {
			ivSplash.setImageResource(R.drawable.treesmall);
		} else if (currentUse > perDuiration * 2) {
			ivSplash.setImageResource(R.drawable.treebig);
		} else {
			ivSplash.setImageResource(R.drawable.treecenter);
		}

		if (showWelcomes.size() > 1) {
			Random r = new Random();
			initWelcomes = r.nextInt(showWelcomes.size());
			splashPager.setCurrentItem(initWelcomes);
		}

		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right_repeat);
		anim.setDuration(30000);
		ivCloud.startAnimation(anim);
		Animation anim2 = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right_repeat);
		anim2.setDuration(15000);
		ivCloud2.startAnimation(anim2);

		int awardCount = getApp().getAppCon().getHistoryCount();
		if (awardCount > 0) {
			llSpVictory.setVisibility(View.VISIBLE);
			Animation amin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
			for (int i = 0; i < awardCount; i++) {
				ImageView ivAward = new ImageView(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.setMargins(10, 10, 10, 10);
				ivAward.setLayoutParams(lp);
				ivAward.setImageResource(R.drawable.victory);
				llSpVictory.addView(ivAward);
				ivAward.startAnimation(amin);
			}
		}

		splashPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (!stopAnimation && !fromMain) {
					stopAnimation = true;
					showGO();
				}
			}
		});
		tvSplashGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeActivity();
			}
		});
	}

	private class WelcomesAdapter extends FragmentStatePagerAdapter {
		private List<String> welcomeList;

		public WelcomesAdapter(FragmentManager manager, List<String> welcomes) {
			super(manager);
			this.welcomeList = welcomes;
		}

		@Override
		public Fragment getItem(int arg0) {
			return SplashFragment.init(welcomeList.get(arg0));
		}

		@Override
		public int getCount() {
			return welcomeList.size();
		}
	}
}
