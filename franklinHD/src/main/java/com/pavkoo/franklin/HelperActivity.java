package com.pavkoo.franklin;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.common.FranklinApplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HelperActivity extends ParentActivity {
	private ImageView ivHelperHome;
	private ImageView ivHelperToolbar;
	private ImageView ivmainExample;
	private ViewFlipper flipperTop;
	private ViewFlipper flipperbottom;
	private TextView tvHelperBottomHint;
	private TextView tvHelperBottomHint2;
	private TextView tvHelperTopHint;
	private TextView tvHelperTopHint2;
	private TextView tvmainExample;
	private LinearLayout llhelperMain2;
	private LinearLayout llhelperMainReverse;
	private LinearLayout llhelperMainReverseInner;
	private RadioButton gpbHelperTodayNo;
	private RelativeLayout llappTodayHelperPopupComment;
	private EditText txtHelperComment;
	private TextView tvPopupHelperCommentHint;
	private TextView tvmainSpot;
	private TextView tvmainSpot2;
	private TextView tvHelperOK;
	private ImageView ivmainSpot;
	private TextView tvHelperSkip;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_helper);
		tvHelperSkip = (TextView) findViewById(R.id.tvHelperSkip);
		ivHelperHome = (ImageView) findViewById(R.id.ivHelperHome);
		ivHelperToolbar = (ImageView) findViewById(R.id.ivHelperToolbar);
		ivmainExample = (ImageView) findViewById(R.id.ivmainExample);
		flipperTop = (ViewFlipper) findViewById(R.id.flipperTop);
		flipperbottom = (ViewFlipper) findViewById(R.id.flipperbottom);
		tvHelperBottomHint = (TextView) findViewById(R.id.tvHelperBottomHint);
		tvHelperBottomHint2 = (TextView) findViewById(R.id.tvHelperBottomHint2);
		tvHelperTopHint = (TextView) findViewById(R.id.tvHelperTopHint);
		tvHelperTopHint2 = (TextView) findViewById(R.id.tvHelperTopHint2);
		tvmainExample = (TextView) findViewById(R.id.tvmainExample);
		llhelperMain2 = (LinearLayout) findViewById(R.id.llhelperMain2);
		llhelperMainReverse = (LinearLayout) findViewById(R.id.llhelperMainReverse);
		llhelperMainReverseInner = (LinearLayout) findViewById(R.id.llhelperMainReverseInner);
		llappTodayHelperPopupComment = (RelativeLayout) findViewById(R.id.llappTodayHelperPopupComment);
		txtHelperComment = (EditText) findViewById(R.id.txtHelperComment);
		tvPopupHelperCommentHint = (TextView) findViewById(R.id.tvPopupHelperCommentHint);
		gpbHelperTodayNo = (RadioButton) findViewById(R.id.gpbHelperTodayNo);
		tvmainSpot = (TextView) findViewById(R.id.tvmainSpot);
		tvmainSpot2 = (TextView) findViewById(R.id.tvmainSpot2);
		tvHelperOK = (TextView) findViewById(R.id.tvHelperOK);
		ivmainSpot = (ImageView) findViewById(R.id.ivmainSpot);
	}

	@Override
	protected void initViewEvent() {
		super.initViewEvent();
		ivHelperHome.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				ivHelperHome.setAnimation(null);
				ivHelperToolbar.setVisibility(View.VISIBLE);
				if (Build.VERSION.SDK_INT > 12) {
					ivHelperHome
							.animate()
							.rotation(315)
							.setDuration(
									FranklinApplication.AnimationDurationShort)
							.setInterpolator(new DecelerateInterpolator())
							.start();
					ivHelperToolbar
							.animate()
							.scaleX(1)
							.setDuration(
									FranklinApplication.AnimationDurationShort)
							.setInterpolator(new OvershootInterpolator())
							.start();
				} else {
					ViewHelper.setScaleX(ivHelperToolbar, 1);
				}
				tvHelperTopHint2.setText(HelperActivity.this
						.getString(R.string.thisisMenu));
				flipperTop.showNext();
				ivHelperHome.setEnabled(false);
				tvHelperBottomHint2.setText(R.string.helper2);
				flipperbottom.showNext();
			}
		});

		ivHelperToolbar.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				ivHelperToolbar.setEnabled(false);
				if (Build.VERSION.SDK_INT > 12) {
					ivHelperHome
							.animate()
							.rotation(0)
							.setDuration(
									FranklinApplication.AnimationDurationShort)
							.setInterpolator(new DecelerateInterpolator())
							.scaleX(0)
							.scaleY(0)
							.setDuration(
									FranklinApplication.AnimationDurationShort)
							.setInterpolator(new AccelerateInterpolator())
							.start();
					ivHelperToolbar
							.animate()
							.scaleX(0)
							.setDuration(
									FranklinApplication.AnimationDurationShort)
							.setInterpolator(new AccelerateInterpolator())
							.start();
				} else {
					ViewHelper.setScaleX(ivHelperHome, 0);
					ViewHelper.setScaleY(ivHelperHome, 0);
					ViewHelper.setScaleX(ivHelperToolbar, 0);
				}

				tvHelperTopHint.setText(R.string.reflectioneveryday);
				flipperTop.showNext();

				tvHelperBottomHint.setText(R.string.helper3);
				flipperbottom.showNext();

				Animation anim = new AlphaAnimation(0, 1);
				anim.setInterpolator(new LinearInterpolator());
				anim.setDuration(500);
				ivmainExample.setAnimation(anim);
				ivmainExample.setVisibility(View.VISIBLE);
				anim.start();
				anim.setStartOffset(100);
				tvmainExample.setAnimation(anim);
				tvmainExample.setVisibility(View.VISIBLE);
				anim.start();
			}
		});

		ivmainExample.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				ivmainExample.setEnabled(false);
				if (Build.VERSION.SDK_INT > 12) {
					ObjectAnimator visToInvis = ObjectAnimator.ofFloat(
							llhelperMain2, "rotationY", 0f, 90f);
					visToInvis.setDuration(400);
					Interpolator accelerator = new AccelerateInterpolator();
					visToInvis.setInterpolator(accelerator);
					final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(
							llhelperMainReverse, "rotationY", -90f, 0f);
					invisToVis.setDuration(400);
					Interpolator decelerator = new DecelerateInterpolator();
					invisToVis.setInterpolator(decelerator);
					visToInvis.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator anim) {
							llhelperMain2.setVisibility(View.GONE);
							invisToVis.start();
							llhelperMainReverse.setVisibility(View.VISIBLE);
						}
					});
					visToInvis.start();
				} else {
					llhelperMain2.setVisibility(View.GONE);
					ViewHelper.setRotationY(llhelperMainReverse, 0);
					llhelperMainReverse.setVisibility(View.VISIBLE);
				}

				tvHelperTopHint2.setText(R.string.reflectioning);
				flipperTop.showNext();
				ivHelperHome.setEnabled(false);
				tvHelperBottomHint2.setText(R.string.chosebad);
				flipperbottom.showNext();
			}
		});

		gpbHelperTodayNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gpbHelperTodayNo.setEnabled(false);
				llappTodayHelperPopupComment.setVisibility(View.VISIBLE);

				Animation anim = AnimationUtils.loadAnimation(
						HelperActivity.this, R.anim.push_up_in);
				anim.setDuration(1000);
				tvPopupHelperCommentHint.setAnimation(anim);
				tvPopupHelperCommentHint.setVisibility(View.VISIBLE);
				tvPopupHelperCommentHint.getAnimation().start();

				tvHelperTopHint.setText(R.string.inputReflection);
				flipperTop.showNext();

				tvHelperBottomHint.setText(R.string.clickInput);
				flipperbottom.showNext();
			}
		});
		txtHelperComment.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				txtHelperComment.setEnabled(false);
				if (hasFocus) {
					Animation gone = AnimationUtils.loadAnimation(
							HelperActivity.this, R.anim.push_left_out_helper);
					gone.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							llhelperMainReverse.setVisibility(View.GONE);
						}
					});
					llhelperMainReverseInner.startAnimation(gone);

					tvHelperTopHint2.setText(R.string.getBlackSpot);
					flipperTop.showNext();

					tvHelperBottomHint2.setText(R.string.thisisfranklin);
					flipperbottom.showNext();

					Animation animleftup = AnimationUtils.loadAnimation(
							HelperActivity.this, R.anim.push_left_in_and_up);
					tvmainSpot.setAnimation(animleftup);
					tvmainSpot.setVisibility(View.VISIBLE);
					tvmainSpot.getAnimation().start();

					Animation animleftup2 = AnimationUtils.loadAnimation(
							HelperActivity.this, R.anim.push_left_in_and_up);
					animleftup2.setStartOffset(200);
					tvmainSpot2.setAnimation(animleftup2);
					tvmainSpot2.setVisibility(View.VISIBLE);
					tvmainSpot2.getAnimation().start();

					Animation animleftup3 = AnimationUtils.loadAnimation(
							HelperActivity.this, R.anim.push_left_in_and_up);
					animleftup3.setStartOffset(400);
					ivmainSpot.setAnimation(animleftup3);
					ivmainSpot.setVisibility(View.VISIBLE);
					ivmainSpot.getAnimation().start();

					Animation animleftup4 = AnimationUtils.loadAnimation(
							HelperActivity.this, R.anim.push_left_in_and_up);
					animleftup4.setStartOffset(600);
					tvHelperOK.setAnimation(animleftup4);
					tvHelperOK.setVisibility(View.VISIBLE);
					tvHelperOK.getAnimation().start();
				}
			}
		});

		tvHelperOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMain();
			}
		});

		tvHelperSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMain();
			}
		});
	}

	private void startMain() {
		Intent mainIntent = new Intent(HelperActivity.this, MainActivity.class);
		HelperActivity.this.startActivity(mainIntent);
		HelperActivity.this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	protected void initViewData() {
		super.initViewData();
		ivHelperToolbar.setVisibility(View.INVISIBLE);
		Animation anim = new RotateAnimation(0, 720,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(4000);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setRepeatMode(Animation.REVERSE);
		ivHelperHome.startAnimation(anim);

		flipperTop.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_up_in));
		flipperTop.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_up_out));

		flipperbottom.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in_helper));
		flipperbottom.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out_helper));
		ViewHelper.setRotationY(llhelperMainReverse, -90);
	}

}
