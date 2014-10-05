package com.pavkoo.franklin;

import java.util.Date;
import java.util.Random;

import com.pavkoo.franklin.common.ApplicationConfig;
import com.pavkoo.franklin.common.UtilsClass;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends ParentActivity {
	private TextView tvSplash;
	private ImageView ivSplash;

	private final int START_SPLASH_OFFSET = 3000;
	private ApplicationConfig config;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_splash);
		tvSplash = (TextView) findViewById(R.id.tvSplash);
		ivSplash = (ImageView) findViewById(R.id.ivSplash);
	}

	@Override
	protected void initViewData() {
		getApp().loadData();
		config = getApp().getAppCon();
		if (!config.isFrist()) {
			selectImage();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (config.isFrist() || !config.isIsselfConfiged()) {
						Intent setIntent = new Intent(SplashActivity.this, SettingActivity.class);
						setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
						SplashActivity.this.startActivity(setIntent);
					} else {
						Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
						SplashActivity.this.startActivity(mainIntent);
					}
					SplashActivity.this.finish();
				}
			}, START_SPLASH_OFFSET);
		}else{
			Intent tutoriIntent = new Intent(SplashActivity.this,TutorialsActivity.class);
			startActivity(tutoriIntent);
			finish();
		}
	}

	private void selectImage() {
		if (config.isFrist()) {
			ivSplash.setImageResource(R.drawable.treesmall);
			tvSplash.setText(R.string.welcome11);
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
		if (getApp().getWelcomes().size()>0) {
			Random r = new Random();
			tvSplash.setText(getApp().getWelcomes().get(r.nextInt(getApp().getWelcomes().size() - 1)));
		} else {
			tvSplash.setText(R.string.welcome11);
		}
	}
}
