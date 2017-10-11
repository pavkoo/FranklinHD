package com.pavkoo.franklin;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pavkoo.franklin.common.UtilsClass;

public class FinishActivity extends ParentActivity {
	private LinearLayout llFinishVictory;
	private ImageView ivFinishDone;
	private TextView tvFinishStore;
	// private TextView tvFinishModify;
	private TextView tvFinishShare;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_finish);
		llFinishVictory = (LinearLayout) findViewById(R.id.llFinishVictory);
		ivFinishDone = (ImageView) findViewById(R.id.ivFinishDone);
		tvFinishStore = (TextView) findViewById(R.id.tvFinishStore);
		// tvFinishModify = (TextView) findViewById(R.id.tvFinishModify);
		tvFinishShare = (TextView) findViewById(R.id.tvFinishShare);
	}

	@Override
	protected void initViewEvent() {
		super.initViewEvent();
		tvFinishStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FinishActivity.this.getApp().markMailStone();
				getApp().getAppCon().setProjectStarted(true);
				getApp().saveAppConfig(getApp().getAppCon(), true);
				Intent mainIntent = new Intent(FinishActivity.this, MainActivity.class);
				FinishActivity.this.startActivity(mainIntent);
				FinishActivity.this.finish();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		// tvFinishModify.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// FinishActivity.this.getApp().markMailStone();
		// getApp().getAppCon().setProjectStarted(true);
		// getApp().saveAppConfig(getApp().getAppCon(), true);
		// Intent setIntent = new Intent(FinishActivity.this,
		// SettingActivity.class);
		// setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
		// setIntent.putExtra("FinishModify", true);
		// FinishActivity.this.startActivity(setIntent);
		// FinishActivity.this.finish();
		// overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		// }
		// });

		tvFinishShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UtilsClass.shareMsg(FinishActivity.this, getString(R.string.Mainshare), getString(R.string.virtueDone), FinishActivity.this
						.getWindow().getDecorView());
			}
		});

		Animation amin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
		int awardCount = getApp().getAppCon().getHistoryCount();
		for (int i = 0; i < awardCount; i++) {
			ImageView ivAward = new ImageView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 10, 10, 10);
			ivAward.setLayoutParams(lp);
			ivAward.setImageResource(R.drawable.victory);
			ivAward.startAnimation(amin);
			llFinishVictory.addView(ivAward);
		}
		ivFinishDone.startAnimation(amin);
	}
}
