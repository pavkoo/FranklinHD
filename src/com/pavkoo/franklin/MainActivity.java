package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.Comment;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.FranklinApplication;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import com.pavkoo.franklin.controls.BlemishReport;
import com.pavkoo.franklin.controls.BlemishReportTotalDialog;
import com.pavkoo.franklin.controls.BlemishReportTrendDialog;
import com.pavkoo.franklin.controls.CommentAdapter;
import com.pavkoo.franklin.controls.CyclePager;
import com.pavkoo.franklin.controls.IRemoveComment;
import com.pavkoo.franklin.controls.IUpdateMoralSelectState;
import com.pavkoo.franklin.controls.IUpdateTextCallBack;
import com.pavkoo.franklin.controls.IUpdateViewCallback;
import com.pavkoo.franklin.controls.PredicateLayout;
import com.pavkoo.franklin.controls.ScrollingTextView;
import com.pavkoo.franklin.controls.Today;
import com.pavkoo.franklin.controls.TodayDialog;
import com.pavkoo.franklin.controls.TodayDialog.DialogState;
import com.umeng.fb.FeedbackAgent;

public class MainActivity extends ParentActivity implements
		IUpdateTextCallBack, IUpdateViewCallback, IUpdateMoralSelectState,
		IRemoveComment {

	private enum ViewState {
		HOME, // 主页
		SETTING, // 设置页面
		HELP, // 欢迎界面
		CONTACTME // 联系我界面
	};

	private String[] colors = { "#61ca63", "#61c7ca", "#7561ca", "#9861ca",
			"#bb61ca", "#ca61b5", "#ca6193", "#ca6171", "#ca7561", "#ca9861",
			"#cabb61", "#b5ca61" };

	private CyclePager cycleContent;
	private LinearLayout llCycleToday;
	private LinearLayout llCycleReport;
	private LinearLayout llCycleComments;
	private LinearLayout llTitle;
	private View viewIndicatiorLeft;
	private View viewIndicatiorCenter;
	private View viewIndicatiorRight;
	private ViewState viewState;
	private TextView txtMainShare;
	private TableRow trHomeToolBar;
	private ImageView ivHome;
	private ImageView ivCommentNomore;
	private TextView tvMainTitleDescrible;
	private ScrollingTextView tvMainTitleMotto;
	private TextView tvMainTitle;
	private TextView tvMainDate;
	private PredicateLayout grpReview;
	private ListView lvComment;
	private ViewFlipper vfMainFlopper;
	private TextView tvMainTime;
	private TextView tvMainTimeHide;
	private TextView tvCycleReprotAppCount;
	private TextView tvCycleReportUserCheckedCount;
	private TextView tvCycleHistoryTitle;
	private TextView tvCycleCommentTitle;
	private TextView txtSetting;
	private TextView txtContactMe;
	private TextView txtMotto;
	private Animation indicatorAnim;
	private Animation shakeAnim;

	private Boolean mMenuExpanded = true;
	private TodayDialog dialog;
	private Today today;
	private List<Moral> morals;
	private List<Comment> comments;
	private CommentAdapter cadapter;
	private Moral todayMoral;
	private BlemishReport blemishReport;
	private AnimMessage amMessage;
	private List<TextView> olderList;
	private String todayDialogTitle;
	private BlemishReportTotalDialog totalDialog;
	private BlemishReportTrendDialog trendDialog;
	private boolean needSave;

	private long touchTime;
	private final int WaitTime = 4000;
	private int mainColor;
	private static final int FranklinNotifyId = 9527;
	private NotificationManager notifyMgr;

	@SuppressLint("NewApi")
	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		viewState = ViewState.HOME;
		llTitle = (LinearLayout) findViewById(R.id.llTitle);
		ivHome = (ImageView) findViewById(R.id.ivHome);
		txtSetting = (TextView) findViewById(R.id.txtSetting);
		trHomeToolBar = (TableRow) findViewById(R.id.trHomeToolBar);
		tvMainTitleDescrible = (TextView) findViewById(R.id.tvMainTitleDescrible);
		tvMainTitle = (TextView) findViewById(R.id.tvMainTitle);
		tvMainTitleMotto = (ScrollingTextView) findViewById(R.id.tvMainTitleMotto);
		viewIndicatiorLeft = findViewById(R.id.viewIndicatiorLeft);
		viewIndicatiorCenter = findViewById(R.id.viewIndicatiorCenter);
		viewIndicatiorRight = findViewById(R.id.viewIndicatiorRight);
		txtContactMe = (TextView) findViewById(R.id.txtContactMe);
		tvMainDate = (TextView) findViewById(R.id.tvMainDate);
		txtMotto = (TextView) findViewById(R.id.txtMotto);
		txtMainShare = (TextView) findViewById(R.id.txtMainShare);
		needSave = false;
		indicatorAnim = AnimationUtils.loadAnimation(this,
				R.anim.indicator_scale);
		amMessage = (AnimMessage) findViewById(R.id.amMainMessage);
		vfMainFlopper = (ViewFlipper) findViewById(R.id.vfMainFlopper);
		tvMainTime = (TextView) findViewById(R.id.tvMainTime);
		tvMainTimeHide = (TextView) findViewById(R.id.tvMainTimeHide);
		vfMainFlopper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		vfMainFlopper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		cycleContent = (CyclePager) findViewById(R.id.clcpagerContent);
		cycleContent.setUpdateViewCallBack(this);
		llCycleToday = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.cycle_today_selector, null);
		llCycleReport = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.cycle_history_report, null);
		llCycleComments = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.cycle_history_comments, null);
		ivCommentNomore = (ImageView) llCycleComments
				.findViewById(R.id.ivCommentNomore);
		blemishReport = (BlemishReport) llCycleReport
				.findViewById(R.id.blemishReport);
		tvCycleReprotAppCount = (TextView) llCycleReport
				.findViewById(R.id.tvCycleReprotAppCount);
		tvCycleHistoryTitle = (TextView) llCycleReport
				.findViewById(R.id.tvCycleHistoryTitle);
		tvCycleCommentTitle = (TextView) llCycleComments
				.findViewById(R.id.tvCycleCommentTitle);
		tvCycleReprotAppCount.startAnimation(shakeAnim);
		tvCycleReprotAppCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				totalDialog.show();
			}
		});
		tvCycleReportUserCheckedCount = (TextView) llCycleReport
				.findViewById(R.id.tvCycleReportUserCheckedCount);
		today = (Today) llCycleToday.findViewById(R.id.today);
		today.setUpdateText(this);
		today.setUpdateSelectState(this);
		grpReview = (PredicateLayout) llCycleToday.findViewById(R.id.grpReview);
		cycleContent.init(llCycleComments, llCycleToday, llCycleReport);
		lvComment = (ListView) llCycleComments.findViewById(R.id.lvComment);
		lvComment.setDivider(null);
		showIndicator();
		txtSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(MainActivity.this,
						SettingActivity.class);
				setIntent.putExtra("STARTMODE", R.id.rbAppSetting);
				MainActivity.this.startActivity(setIntent);
				MainActivity.this.finish();
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
		txtContactMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
				agent.startFeedbackActivity();
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				// 不再使用邮件反馈
				// Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
				// Uri uri = Uri.parse(getString(R.string.mailto));
				// emailIntent.setData(uri);
				// try {
				// MainActivity.this.startActivity(Intent.createChooser(
				// emailIntent, getString(R.string.chooseEmail)));
				// } catch (ActivityNotFoundException e) {
				// amMessage.showMessage(getString(R.string.cantSendEmail),
				// AnimMessage.AnimMessageType.WARNING);
				// }
			}
		});
		txtMotto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent splashIntent = new Intent(MainActivity.this,
						SplashActivity.class);
				splashIntent.putExtra("flag", true);
				MainActivity.this.startActivity(splashIntent);
				MainActivity.this.finish();
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
		txtMainShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int pos = cycleContent.getCurrent();
				switch (pos) {
				case 0:
					UtilsClass.shareMsg(MainActivity.this,
							getString(R.string.Mainshare),
							getString(R.string.shareReflections),
							MainActivity.this.getWindow().getDecorView());
					break;
				case 1:
					UtilsClass.shareMsg(MainActivity.this,
							getString(R.string.Mainshare),
							getString(R.string.ImGetBetter), MainActivity.this
									.getWindow().getDecorView());
					break;

				case 2:
					UtilsClass.shareMsg(MainActivity.this,
							getString(R.string.Mainshare),
							getString(R.string.shareReport), MainActivity.this
									.getWindow().getDecorView());
					break;
				default:
					break;
				}

			}
		});

		iniDialog();
		ivHome.setTag(viewState);
		ivHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewState state = (ViewState) ivHome.getTag();
				if (ivHome.getAnimation() != null) {
					ivHome.getAnimation().cancel();
				}
				if (trHomeToolBar.getAnimation() != null) {
					trHomeToolBar.getAnimation().cancel();
				}
				switch (state) {
				case HOME:
					if (mMenuExpanded) {
						MainActivity.this.getApp();
						if (Build.VERSION.SDK_INT > 12) {
							trHomeToolBar
									.animate()
									.scaleX(0)
									.setDuration(
											FranklinApplication.AnimationDurationShort)
									.setInterpolator(
											new AccelerateInterpolator())
									.start();
							ivHome.animate()
									.rotation(0)
									.setDuration(
											FranklinApplication.AnimationDurationShort)
									.setInterpolator(
											new DecelerateInterpolator())
									.start();
						} else {
							ViewHelper.setScaleX(trHomeToolBar, 0);
						}
					} else {
						if (Build.VERSION.SDK_INT > 12) {
							trHomeToolBar
									.animate()
									.scaleX(1)
									.setDuration(
											FranklinApplication.AnimationDurationShort)
									.setInterpolator(
											new OvershootInterpolator())
									.start();
							ivHome.animate()
									.rotation(315)
									.setDuration(
											FranklinApplication.AnimationDurationShort)
									.setInterpolator(
											new DecelerateInterpolator())
									.start();
						} else {
							ViewHelper.setScaleX(trHomeToolBar, 1);
						}

					}
					mMenuExpanded = !mMenuExpanded;
					break;
				default:
					break;
				}
			}
		});
		if (Build.VERSION.SDK_INT > 10) {
			tvMainTitleMotto
					.addOnLayoutChangeListener(new OnLayoutChangeListener() {

						@Override
						public void onLayoutChange(View v, int left, int top,
								int right, int bottom, int oldLeft, int oldTop,
								int oldRight, int oldBottom) {
							LayoutParams params = (LayoutParams) v
									.getLayoutParams();
							params.width = right - left;
							params.height = bottom - top;
							params.weight = 0;
							v.removeOnLayoutChangeListener(this);
							v.setLayoutParams(params);
						}
					});
		}
	}

	private void showIndicator() {
		ViewHelper.setAlpha(viewIndicatiorLeft, 0.4f);
		ViewHelper.setAlpha(viewIndicatiorCenter, 0.4f);
		ViewHelper.setAlpha(viewIndicatiorRight, 0.4f);
		if (viewIndicatiorLeft.getAnimation() != null) {
			viewIndicatiorLeft.setAnimation(null);
		}
		if (viewIndicatiorCenter.getAnimation() != null) {
			viewIndicatiorCenter.setAnimation(null);
		}
		if (viewIndicatiorRight.getAnimation() != null) {
			viewIndicatiorRight.setAnimation(null);
		}
		int pos = cycleContent.getCurrent();
		switch (pos) {
		case 0:
			viewIndicatiorLeft.startAnimation(indicatorAnim);
			ViewHelper.setAlpha(viewIndicatiorLeft, 0.8f);
			break;
		case 1:
			viewIndicatiorCenter.startAnimation(indicatorAnim);
			ViewHelper.setAlpha(viewIndicatiorCenter, 0.8f);
			break;
		case 2:
			viewIndicatiorRight.startAnimation(indicatorAnim);
			ViewHelper.setAlpha(viewIndicatiorRight, 0.8f);
			break;
		default:
			break;
		}
		indicatorAnim.startNow();
	}

	private void iniDialog() {
		dialog = new TodayDialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				int dayoffset = today.getCurrentShowing();
				TextView v = (TextView) MainActivity.this.dialog
						.getExtraObject();
				Moral moral = morals.get(v.getId());
				CheckState cs = updateCheckState((TextView) v,
						((TodayDialog) dialog).getCheckState(), false);
				moral.setHistorySelected(dayoffset, cs);
				if (((TodayDialog) dialog).isNewComment()) {
					moral.getComments().set(moral.getComments().size() - 1,
							((TodayDialog) dialog).getNewCommentIndex());
				}
				MainActivity.this.getApp().saveMorals(morals);
			}
		});
		totalDialog = new BlemishReportTotalDialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		trendDialog = new BlemishReportTrendDialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
	}

	private void iniReport() {
		blemishReport.setMorals(morals);
		tvCycleReprotAppCount.setText(String.valueOf(getApp().getAppCon()
				.getUseAppCount()));
		tvCycleReportUserCheckedCount.setText(String
				.valueOf(getTotalCheckted()));
		tvCycleReportUserCheckedCount.setClickable(false);
		if (morals.size() > 0) {
			int cycleSize = morals.get(0).getStateList().size()
					/ morals.get(0).getCycle();
			if (cycleSize >= 1) {
				tvCycleReportUserCheckedCount.setClickable(true);
				tvCycleReportUserCheckedCount.startAnimation(shakeAnim);
				tvCycleReportUserCheckedCount
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								trendDialog.show();
							}
						});
			}
		}
	}

	private int getTotalCheckted() {
		int total = 0;
		for (int i = 0; i < morals.size(); i++) {
			total += morals.get(i).getCheckedCount();
		}
		return total;
	}

	@Override
	protected void initViewData() {
		morals = getApp().getMorals();
		comments = getApp().getComments();
		if (!initMorals()) {
			return;
		}
		tvMainTitle.setText(todayMoral.getTitle());
		tvMainTitleDescrible.setText(todayMoral.getTitleDes());
		tvMainTitleMotto.setText(todayMoral.getTitleMotto());
		today.setMoral(todayMoral);
		totalDialog.setMorals(morals, mainColor);
		trendDialog.setMorals(morals);
		iniReport();
		initGroupReview(todayMoral.getCurrentDayInCycle() - 1);
		initComment();
		updateCommentDate();
	}

	private void notifyToday(boolean cancel) {
		if (cancel) {
			notifyMgr.cancel(FranklinNotifyId);
			return;
		}
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(
				this, SplashActivity.class), 0);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getString(R.string.doyoudotaday))
				.setContentTitle(
						getString(R.string.todaySubject)
								+ todayMoral.getTitle())
				.setContentText(
						getString(R.string.todo) + todayMoral.getTitleDes())
				.setContentIntent(pIntent);
		Notification notify = mBuilder.build();
		notify.icon = R.drawable.ic_launcher;
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		notify.tickerText = getString(R.string.app_name);
		notify.when = System.currentTimeMillis();
		notifyMgr.notify(FranklinNotifyId, notify);
	}

	@SuppressLint("NewApi")
	private boolean initMorals() {
		needSave = false;
		if (morals == null) {
			amMessage.showMessage(getString(R.string.errortorestore),
					AnimMessageType.ERROR);
			return false;
		}
		for (int i = 0; i < morals.size(); i++) {
			Moral m = morals.get(i);
			Log.i("Day", "Start:" + m.getStartDate().toString()
					+ "------------ End:" + m.getEndDate().toString());
		}
		Date now = new Date(System.currentTimeMillis());
		int unsetStateDay = 0;
		boolean dayPassed = false;
		for (int i = 0; i < morals.size(); i++) {
			Moral m = morals.get(i);
			int enddaycount = (int) UtilsClass.dayCount(m.getEndDate(), now);
			int startoffset = (int) UtilsClass.dayCount(m.getStartDate(), now) + 1;// 1
																					// means
			// today

			if (enddaycount <= 0) {
				todayMoral = m;
				m.setCurrentDayInCycle(startoffset);
				m.setFinished(false);
				if (startoffset < 0) {
					dayPassed = true;
					break;
				}
				for (int j = i + 1; j < morals.size(); j++) {
					morals.get(j).setFinished(false);
					morals.get(j).setCurrentDayInCycle(0);
				}
			} else {
				m.setFinished(true);
				m.setCurrentDayInCycle(0);
			}
			unsetStateDay = startoffset - m.getStateList().size();
			if (unsetStateDay > 0) {
				needSave = true;
				for (int j = 0; j < unsetStateDay; j++) {
					m.getStateList().add(CheckState.UNKNOW);
					m.getComments().add(-1);
				}
			} else if (unsetStateDay < 0) {
				needSave = true;
				int delCount = Math.abs(unsetStateDay);
				while (delCount > 0) {
					m.getStateList().remove(m.getStateList().size() - 1);
					m.getComments().remove(m.getComments().size() - 1);
					delCount--;
				}
			}
			if (enddaycount <= 0) {
				break;
			}
		}
		if (todayMoral == null) {
			Intent finishIntent = new Intent(MainActivity.this,
					FinishActivity.class);
			startActivity(finishIntent);
			finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			return false;
		}
		if (dayPassed) {
			amMessage.showMessage(getString(R.string.todayhavepassed),
					AnimMessageType.ERROR);
			return false;
		}
		if (todayMoral.getCurrentDayInCycle() == 1) {
			int finishedindex = morals.indexOf(todayMoral);
			if (finishedindex == 0) {
				amMessage.showMessage(
						String.format(
								getResources().getString(
										R.string.startNewMoralInfo),
								todayMoral.getCycle(), todayMoral.getTitle()),
						3000);
			} else {
				amMessage.showMessage(String.format(
						getResources().getString(R.string.startNewMoralInfo2),
						morals.get(finishedindex - 1).getTitle(),
						todayMoral.getCycle(), todayMoral.getTitle()), 3000);
			}
		} else {
			amMessage.showMessage(String.format(
					getResources().getString(R.string.beginMoral),
					todayMoral.getTitle(), todayMoral.getCurrentDayInCycle()),
					3000);
		}
		if (unsetStateDay > 3) {
			amMessage.showMessage(String.format(
					getResources().getString(R.string.manyDaynotuse),
					unsetStateDay), AnimMessageType.ERROR, 33000);
			if (Build.VERSION.SDK_INT > 11) {
				ValueAnimator colorAnim = ObjectAnimator
						.ofInt(today,
								"backgroundColor",
								getResources().getColor(
										R.color.white_app_bg_secondary),
								getResources().getColor(
										R.color.white_app_warning),
								getResources().getColor(
										R.color.white_app_bg_secondary));
				colorAnim.setDuration(15000);
				colorAnim.setStartDelay(6000);
				colorAnim.setEvaluator(new ArgbEvaluator());
				colorAnim.start();
			}
		}
		if (needSave) {
			this.getApp().saveMorals(morals);
		}
		if (todayMoral.getTodaySelected() == CheckState.UNKNOW) {
			notifyToday(false);
		}
		updateUIByMoral(morals.indexOf(todayMoral));

		return true;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void initGroupReview(int selectedIndex) {
		olderList = new ArrayList<TextView>();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int padding = (int) (20 * density);
		int txtColor = getResources().getColor(R.color.white_app_txt_white);
		for (int i = 0; i < morals.size(); i++) {
			if (!morals.get(i).isFinished())
				break;
			TextView rb = new TextView(this);
			GradientDrawable sd = (GradientDrawable) getResources()
					.getDrawable(R.drawable.review_background_ring);
			sd.setColor(Color.parseColor(colors[i % colors.length]));
			if (Build.VERSION.SDK_INT >= 16) {
				rb.setBackground(sd);
			} else {
				rb.setBackgroundDrawable(sd);
			}
			String title = morals.get(i).getTitle();
			if (UtilsClass.isEng()) {
				title = UtilsClass.shortString(title);
			}
			rb.setText(title);
			rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			rb.setTextColor(txtColor);
			updateCheckState(rb, morals.get(i).getTodaySelected(), false);
			rb.setId(i);
			rb.setClickable(true);
			rb.setPadding(padding, padding, padding, padding);
			rb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckState cs = morals.get(v.getId()).getHistorySelected(
							today.getCurrentShowing());
					cs = updateCheckState((TextView) v, cs, true);
					morals.get(v.getId()).setHistorySelected(
							today.getCurrentShowing(), cs);
					MainActivity.this.getApp().saveMorals(morals);
				}
			});
			rb.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					CheckState cs = CheckState.UNDONE;
					dialog.setCheckState(cs);
					dialog.showState(DialogState.DSNote);
					dialog.setExtraObject(v);
					return true;
				}
			});
			grpReview.addView(rb,
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			olderList.add(rb);
		}
	}

	private CheckState updateCheckState(TextView tv, CheckState cs,
			boolean reverse) {
		switch (cs) {
		case UNKNOW:
			if (reverse) {
				TransitionDrawable tsDrawTop = (TransitionDrawable) tv
						.getCompoundDrawables()[1];
				if (tsDrawTop == null) {
					tsDrawTop = (TransitionDrawable) getResources()
							.getDrawable(R.drawable.review_select_transition);
					tsDrawTop.setCrossFadeEnabled(true);
					tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop,
							null, null);
				}
				return CheckState.DONE;
			} else {
				tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
						null);
			}
			break;
		case DONE:
			TransitionDrawable tsDrawTop = (TransitionDrawable) tv
					.getCompoundDrawables()[1];
			if (tsDrawTop == null) {
				tsDrawTop = (TransitionDrawable) getResources().getDrawable(
						R.drawable.review_select_transition);
				tsDrawTop.setCrossFadeEnabled(true);
				tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop,
						null, null);
			} else {
				tsDrawTop.resetTransition();
			}
			if (reverse) {
				tsDrawTop.startTransition(400);
				return CheckState.UNDONE;
			}
			break;
		case UNDONE:
			TransitionDrawable tsDrawTop2 = (TransitionDrawable) tv
					.getCompoundDrawables()[1];
			if (tsDrawTop2 == null) {
				tsDrawTop2 = (TransitionDrawable) getResources().getDrawable(
						R.drawable.review_select_transition);
				tsDrawTop2.setCrossFadeEnabled(true);
				tsDrawTop2.startTransition(0);
				tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop2,
						null, null);
			} else {
				tsDrawTop2.resetTransition();
				tsDrawTop2.startTransition(0);
			}
			if (reverse) {
				tsDrawTop2.reverseTransition(400);
				return CheckState.DONE;
			}
			break;
		default:
			break;
		}
		return CheckState.UNKNOW;
	}

	private void initComment() {
		cadapter = new CommentAdapter(this.getApplicationContext(), mainColor);
		cadapter.setOnRemoveComment(MainActivity.this);
		lvComment.setAdapter(cadapter);
	}

	private void updateCommentDate() {
		Collections.sort(comments, Collections.reverseOrder());
		cadapter.setComments(comments);
		if (cadapter.getComments().size() == 0) {
			ivCommentNomore.setVisibility(View.VISIBLE);
		} else {
			ivCommentNomore.setVisibility(View.GONE);
		}
	}

	@Override
	public void updateTextCallBack(String value, int position) {
		String time = "";
		int dayoffset = todayMoral.getCurrentDayInCycle() - position;
		Date showDate = new Date();
		showDate = UtilsClass.subDate(showDate, dayoffset);
		if (dayoffset == 0) {
			time = "";
		} else if (dayoffset == 1) {
			time = getString(R.string.yesterday);
		} else if (dayoffset == 2) {
			time = getString(R.string.dayBeforeYesterday);
		} else {
			time = String.valueOf(dayoffset) + getString(R.string.moreDay);
		}
		tvMainDate.setText(UtilsClass.dateToString(showDate));
		tvMainDate.setVisibility(View.VISIBLE);
		if (time != "") {
			todayDialogTitle = String.format(
					getResources().getString(
							R.string.todayControlPopupTitleFormat), time,
					todayMoral.getTitle());
		} else {
			todayDialogTitle = getResources().getString(
					R.string.today_control_popup_title);
		}
		today.setTodayDialogTitle(todayDialogTitle);
		dialog.setDialogTitle(todayDialogTitle);
		if (vfMainFlopper.getCurrentView() == tvMainTimeHide) {
			tvMainTime.setText(time);
		} else {
			tvMainTimeHide.setText(time);
		}

		vfMainFlopper.showNext();
		updateOlderList(position);
		updateCommentDate();
	}

	private void updateOlderList(int selectedDay) {
		// olderList 和 morals list 一定是包含与一一对应关系
		for (int i = 0; i < olderList.size(); i++) {
			updateCheckState(olderList.get(i), morals.get(i)
					.getHistorySelected(selectedDay), false);
		}
	}

	@Override
	public void updateTextCallBack() {
		MainActivity.this.getApp().saveMorals(morals);
		notifyToday(true);
	}

	@Override
	public void callUpdateFontSize() {
		amMessage.reset();
		blemishReport.invalidate();
		totalDialog.setMorals(morals, mainColor);
		trendDialog.setMorals(morals);
		updateCommentDate();
		tvCycleReprotAppCount.setText(String.valueOf(getApp().getAppCon()
				.getUseAppCount()));
		tvCycleReportUserCheckedCount.setText(String
				.valueOf(getTotalCheckted()));
		showIndicator();
	}

	@Override
	public void onBackPressed() {
		long currentPressed = System.currentTimeMillis();
		if ((currentPressed - touchTime) >= WaitTime) {
			amMessage.showMessage(getString(R.string.pressToExit),
					AnimMessageType.Hint);
			touchTime = currentPressed;
		} else {
			super.onBackPressed();
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void updateUIByMoral(int index) {
		mainColor = Color.parseColor(CommonConst.colors[index
				% CommonConst.colors.length]);
		tvMainTitle.setBackgroundColor(mainColor);
		today.updateUIByMoral(index);
		dialog.updateUIByMoral(index);
		totalDialog.updateUIByMoral(index);
		trendDialog.updateUIByMoral(index);
		llTitle.setBackgroundColor(mainColor);
		GradientDrawable gd = (GradientDrawable) getResources().getDrawable(
				R.drawable.review_ring);
		gd.setColor(mainColor);
		if (Build.VERSION.SDK_INT >= 16) {
			viewIndicatiorLeft.setBackground(gd);
			viewIndicatiorCenter.setBackground(gd);
			viewIndicatiorRight.setBackground(gd);
		} else {
			viewIndicatiorLeft.setBackgroundDrawable(gd);
			viewIndicatiorCenter.setBackgroundDrawable(gd);
			viewIndicatiorRight.setBackgroundDrawable(gd);
		}
		tvCycleReprotAppCount.setTextColor(mainColor);
		tvCycleReportUserCheckedCount.setTextColor(mainColor);
		tvCycleHistoryTitle.setTextColor(mainColor);
		tvCycleCommentTitle.setTextColor(mainColor);
		ivHome.setImageResource(getHomeImageIndexByMoral(index));
		trHomeToolBar.setBackgroundResource(getHomeToolBarRes(index));
	}

	private int getHomeImageIndexByMoral(int index) {
		int outIndex = index + 1;
		int res = 0;
		switch (outIndex) {
		case 1:
			res = R.drawable.homecolor1;
			break;
		case 2:
			res = R.drawable.homecolor2;
			break;
		case 3:
			res = R.drawable.homecolor3;
			break;
		case 4:
			res = R.drawable.homecolor4;
			break;
		case 5:
			res = R.drawable.homecolor5;
			break;
		case 6:
			res = R.drawable.homecolor6;
			break;
		case 7:
			res = R.drawable.homecolor7;
			break;
		case 8:
			res = R.drawable.homecolor8;
			break;
		case 9:
			res = R.drawable.homecolor9;
			break;
		case 10:
			res = R.drawable.homecolor10;
			break;
		case 11:
			res = R.drawable.homecolor11;
			break;
		case 12:
			res = R.drawable.homecolor12;
			break;
		default:
			res = R.drawable.home2;
		}
		return res;
	}

	private int getHomeToolBarRes(int index) {
		int outIndex = index + 1;
		int res = 0;
		switch (outIndex) {
		case 1:
			res = R.drawable.toolbarcolor1;
			break;
		case 2:
			res = R.drawable.toolbarcolor2;
			break;
		case 3:
			res = R.drawable.toolbarcolor3;
			break;
		case 4:
			res = R.drawable.toolbarcolor4;
			break;
		case 5:
			res = R.drawable.toolbarcolor5;
			break;
		case 6:
			res = R.drawable.toolbarcolor6;
			break;
		case 7:
			res = R.drawable.toolbarcolor7;
			break;
		case 8:
			res = R.drawable.toolbarcolor8;
			break;
		case 9:
			res = R.drawable.toolbarcolor9;
			break;
		case 10:
			res = R.drawable.toolbarcolor10;
			break;
		case 11:
			res = R.drawable.toolbarcolor11;
			break;
		case 12:
			res = R.drawable.toolbarcolor12;
			break;
		default:
			res = R.drawable.toolbar;
		}
		return res;
	}

	@Override
	public void onRemoveComment(int index) {
		Comment com = cadapter.getComments().get(index);
		int removeIndex = comments.indexOf(com);
		if (removeIndex != -1) {
			comments.get(removeIndex).setRemoved(true);
		}
		getApp().saveComments(comments);
		updateCommentDate();
	}
}
