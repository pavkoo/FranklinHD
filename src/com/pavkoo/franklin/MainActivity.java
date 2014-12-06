package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
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
import com.pavkoo.franklin.common.SignRecords;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import com.pavkoo.franklin.controls.BlemishReport;
import com.pavkoo.franklin.controls.BlemishReport.OnBlackSpotClicked;
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
import com.umeng.fb.FeedbackAgent;

public class MainActivity extends ParentActivity
		implements
			IUpdateTextCallBack,
			IUpdateViewCallback,
			IUpdateMoralSelectState,
			IRemoveComment {

	private enum ViewState {
		HOME, // 主页
		SETTING, // 设置页面
		HELP, // 欢迎界面
		CONTACTME // 联系我界面
	};

	private String[] colors = {"#61ca63", "#61c7ca", "#7561ca", "#9861ca", "#bb61ca", "#ca61b5", "#ca6193", "#ca6171", "#ca7561",
			"#ca9861", "#cabb61", "#b5ca61"};

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
	private Today today;
	private List<Moral> morals;
	private List<Comment> comments;
	private HashMap<String, List<SignRecords>> newWeek;
	private List<SignRecords> signlist;
	private CommentAdapter cadapter;
	private Moral todayMoral;
	private BlemishReport blemishReport;
	private AnimMessage amMessage;
	private List<TextView> olderList;
	private String todayDialogTitle;
	private BlemishReportTotalDialog totalDialog;
	private BlemishReportTrendDialog trendDialog;

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
		indicatorAnim = AnimationUtils.loadAnimation(this, R.anim.indicator_scale);
		amMessage = (AnimMessage) findViewById(R.id.amMainMessage);
		vfMainFlopper = (ViewFlipper) findViewById(R.id.vfMainFlopper);
		tvMainTime = (TextView) findViewById(R.id.tvMainTime);
		tvMainTimeHide = (TextView) findViewById(R.id.tvMainTimeHide);
		vfMainFlopper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		vfMainFlopper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		cycleContent = (CyclePager) findViewById(R.id.clcpagerContent);
		cycleContent.setUpdateViewCallBack(this);
		llCycleToday = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.cycle_today_selector, null);
		llCycleReport = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.cycle_history_report, null);
		llCycleComments = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.cycle_history_comments, null);
		ivCommentNomore = (ImageView) llCycleComments.findViewById(R.id.ivCommentNomore);
		blemishReport = (BlemishReport) llCycleReport.findViewById(R.id.blemishReport);
		tvCycleReprotAppCount = (TextView) llCycleReport.findViewById(R.id.tvCycleReprotAppCount);
		tvCycleHistoryTitle = (TextView) llCycleReport.findViewById(R.id.tvCycleHistoryTitle);
		tvCycleCommentTitle = (TextView) llCycleComments.findViewById(R.id.tvCycleCommentTitle);
		tvCycleReprotAppCount.startAnimation(shakeAnim);
		tvCycleReprotAppCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SparseIntArray doneRate = getApp().getMgr().getDoneSignCount();
				totalDialog.iniTotalData(morals, mainColor, doneRate);
				totalDialog.show();
			}
		});
		tvCycleReportUserCheckedCount = (TextView) llCycleReport.findViewById(R.id.tvCycleReportUserCheckedCount);
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
				Intent setIntent = new Intent(MainActivity.this, SettingActivity.class);
				setIntent.putExtra("STARTMODE", R.id.rbAppSetting);
				MainActivity.this.startActivity(setIntent);
				MainActivity.this.finish();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		txtContactMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
				agent.startFeedbackActivity();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
				Intent splashIntent = new Intent(MainActivity.this, SplashActivity.class);
				splashIntent.putExtra("flag", true);
				MainActivity.this.startActivity(splashIntent);
				MainActivity.this.finish();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		txtMainShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int pos = cycleContent.getCurrent();
				switch (pos) {
					case 0 :
						UtilsClass.shareMsg(MainActivity.this, getString(R.string.Mainshare), getString(R.string.shareReflections),
								MainActivity.this.getWindow().getDecorView());
						break;
					case 1 :
						UtilsClass.shareMsg(MainActivity.this, getString(R.string.Mainshare), getString(R.string.ImGetBetter),
								MainActivity.this.getWindow().getDecorView());
						break;

					case 2 :
						UtilsClass.shareMsg(MainActivity.this, getString(R.string.Mainshare), getString(R.string.shareReport),
								MainActivity.this.getWindow().getDecorView());
						break;
					default :
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
					case HOME :
						if (mMenuExpanded) {
							MainActivity.this.getApp();
							if (Build.VERSION.SDK_INT > 12) {
								trHomeToolBar.animate().scaleX(0).setDuration(FranklinApplication.AnimationDurationShort)
										.setInterpolator(new AccelerateInterpolator()).start();
								ivHome.animate().rotation(0).setDuration(FranklinApplication.AnimationDurationShort)
										.setInterpolator(new DecelerateInterpolator()).start();
							} else {
								ViewHelper.setScaleX(trHomeToolBar, 0);
							}
						} else {
							if (Build.VERSION.SDK_INT > 12) {
								trHomeToolBar.animate().scaleX(1).setDuration(FranklinApplication.AnimationDurationShort)
										.setInterpolator(new OvershootInterpolator()).start();
								ivHome.animate().rotation(315).setDuration(FranklinApplication.AnimationDurationShort)
										.setInterpolator(new DecelerateInterpolator()).start();
							} else {
								ViewHelper.setScaleX(trHomeToolBar, 1);
							}

						}
						mMenuExpanded = !mMenuExpanded;
						break;
					default :
						break;
				}
			}
		});
		if (Build.VERSION.SDK_INT > 10) {
			tvMainTitleMotto.addOnLayoutChangeListener(new OnLayoutChangeListener() {

				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
						int oldBottom) {
					LayoutParams params = (LayoutParams) v.getLayoutParams();
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
			case 0 :
				viewIndicatiorLeft.startAnimation(indicatorAnim);
				ViewHelper.setAlpha(viewIndicatiorLeft, 0.8f);
				break;
			case 1 :
				viewIndicatiorCenter.startAnimation(indicatorAnim);
				ViewHelper.setAlpha(viewIndicatiorCenter, 0.8f);
				break;
			case 2 :
				viewIndicatiorRight.startAnimation(indicatorAnim);
				ViewHelper.setAlpha(viewIndicatiorRight, 0.8f);
				break;
			default :
				break;
		}
		indicatorAnim.startNow();
	}

	private void iniDialog() {
		totalDialog = new BlemishReportTotalDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		trendDialog = new BlemishReportTrendDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
	}

	private void iniReport() {
		blemishReport.setMorals(morals);
		blemishReport.setNewWeekData(newWeek);
		blemishReport.setCurrent(todayMoral.getStartDate());
		blemishReport.setOnBlackSpotClicked(new OnBlackSpotClicked() {

			@Override
			public void OnBlackSpotClickedCallBack(SignRecords sr) {
				String msg = "";
				String date = UtilsClass.dateToString(sr.getInputDate());
				int moralid = sr.getMoarlIndex();
				int index = UtilsClass.getIndexMorals(morals, moralid);
				String moraltitle = morals.get(index).getTitle();

				if (sr.getCommentIndex() == -1) {
					msg = String.format(getString(R.string.reportclickhaveno), date, moraltitle);
				} else {
					int CommentIndex = UtilsClass.getIndexComments(comments, sr.getCommentIndex());
					String content = comments.get(CommentIndex).getContent();
					msg = String.format(getString(R.string.reportclickabout), date, moraltitle, content);
				}
				amMessage.showMessage(msg, AnimMessageType.LONGINFO);
			}
		});
		int allUse = getApp().getAppCon().getUseAppCount();
		int allSign = getApp().getMgr().getTotoalSignCount();
		tvCycleReprotAppCount.setText(String.valueOf(allUse));
		tvCycleReportUserCheckedCount.setText(String.valueOf(allSign));
		tvCycleReportUserCheckedCount.setClickable(false);
		if (morals.size() > 0) {
			int cycleSize = allUse / morals.get(0).getCycle();
			if (cycleSize >= 1) {
				tvCycleReportUserCheckedCount.setClickable(true);
				tvCycleReportUserCheckedCount.startAnimation(shakeAnim);
				tvCycleReportUserCheckedCount.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						signlist = getApp().getMgr().loadSignedRecord();
						trendDialog.setData(morals, signlist);
						trendDialog.show();
					}
				});
			}
		}
	}
	@Override
	protected void initViewData() {
		morals = getApp().getMorals();
		comments = getApp().getComments();
		if (!initMorals()) {
			return;
		}
		newWeek = getApp().getNewWeek();
		tvMainTitle.setText(todayMoral.getTitle());
		tvMainTitleDescrible.setText(todayMoral.getTitleDes());
		tvMainTitleMotto.setText(todayMoral.getTitleMotto());
		today.setNewWeekData(newWeek);
		today.updateViewByMoral(todayMoral);
		iniReport();
		initGroupReview(todayMoral.getCurrentDayInCycle());
		initComment();
		updateCommentDate();
	}

	private void notifyToday(boolean cancel) {
		if (cancel) {
			notifyMgr.cancel(FranklinNotifyId);
			return;
		}
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), 0);
		mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker(getString(R.string.doyoudotaday))
				.setContentTitle(getString(R.string.todaySubject) + todayMoral.getTitle())
				.setContentText(getString(R.string.todo) + todayMoral.getTitleDes()).setContentIntent(pIntent);
		Notification notify = mBuilder.build();
		notify.icon = R.drawable.ic_launcher;
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		notify.tickerText = getString(R.string.app_name);
		notify.when = System.currentTimeMillis();
		notifyMgr.notify(FranklinNotifyId, notify);
	}

	@SuppressLint("NewApi")
	private boolean initMorals() {
		if (getApp().getMgr().isBeforeTraining()) {
			amMessage.showMessage(getString(R.string.todayhavepassed), AnimMessageType.ERROR);
			return false;
		}

		if (getApp().getMgr().isAfterTraning()) {
			Intent finishIntent = new Intent(MainActivity.this, FinishActivity.class);
			startActivity(finishIntent);
			finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			return false;
		}

		int id = getApp().getMgr().getCurrentMoralId();
		for (int i = 0; i < morals.size(); i++) {
			Moral m = morals.get(i);
			m.setFinished(true);
			m.setCurrentDayInCycle(0);
			if (morals.get(i).getId() == id) {
				todayMoral = m;
				Date now = new Date(System.currentTimeMillis());
				int startoffset = (int) UtilsClass.dayCount(m.getStartDate(), now) + 1;
				todayMoral.setCurrentDayInCycle(startoffset);
				todayMoral.setFinished(false);
				getApp().loadThisWeek(todayMoral.getStartDate(), todayMoral.getEndDate());
				break;
			}
		}

		if (todayMoral.getCurrentDayInCycle() == 1) {
			int finishedindex = morals.indexOf(todayMoral);
			if (finishedindex == 0) {
				amMessage.showMessage(
						String.format(getResources().getString(R.string.startNewMoralInfo), todayMoral.getCycle(), todayMoral.getTitle()),
						3000);
			} else {
				amMessage.showMessage(String.format(getResources().getString(R.string.startNewMoralInfo2), morals.get(finishedindex - 1)
						.getTitle(), todayMoral.getCycle(), todayMoral.getTitle()), 3000);
			}
		} else {
			amMessage.showMessage(
					String.format(getResources().getString(R.string.beginMoral), todayMoral.getTitle(), todayMoral.getCurrentDayInCycle()),
					3000);
		}
		int unsetStateDay = getApp().getMgr().lastReflectDate();
		if (unsetStateDay > 3) {
			amMessage.showMessage(String.format(getResources().getString(R.string.manyDaynotuse), unsetStateDay), AnimMessageType.ERROR,
					33000);
			if (Build.VERSION.SDK_INT > 11) {
				ValueAnimator colorAnim = ObjectAnimator.ofInt(today, "backgroundColor",
						getResources().getColor(R.color.white_app_bg_secondary), getResources().getColor(R.color.white_app_error),
						getResources().getColor(R.color.white_app_bg_secondary));
				colorAnim.setDuration(15000);
				colorAnim.setStartDelay(6000);
				colorAnim.setEvaluator(new ArgbEvaluator());
				colorAnim.start();
			}
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
			GradientDrawable sd = (GradientDrawable) getResources().getDrawable(R.drawable.review_background_ring);
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
			CheckState cs = UtilsClass.getNewWeekCSByDay(newWeek, morals.get(i), todayMoral.getStartDate(),
					todayMoral.getCurrentDayInCycle() - 1);
			updateCheckState(rb, cs, false);
			rb.setId(morals.get(i).getId());
			rb.setClickable(true);
			rb.setPadding(padding, padding, padding, padding);
			rb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = UtilsClass.getIndexMorals(morals, v.getId());
					SignRecords sr = UtilsClass.getNewWeekCSByDayCreate(newWeek, morals.get(index), todayMoral.getStartDate(),
							today.getCurrentShowing() - 1, true);
					if (sr.getCs() == CheckState.UNKNOW) {
						sr.setCommentIndex(-1);
						sr.setCs(updateCheckState((TextView) v, sr.getCs(), true));
						getApp().getMgr().insertNew(sr);
						if (getApp().getNewWeek().get(String.valueOf(sr.getMoarlIndex())) == null) {
							ArrayList<SignRecords> slist = new ArrayList<SignRecords>();
							slist.add(sr);
							getApp().getNewWeek().put(String.valueOf(sr.getMoarlIndex()), slist);
						} else {
							getApp().getNewWeek().get(String.valueOf(sr.getMoarlIndex())).add(sr);
						}
					} else {
						sr.setCs(updateCheckState((TextView) v, sr.getCs(), true));
						getApp().getMgr().updateSr(sr);
					}
				}
			});
			// TODO:暂时就不提供长按了 setOnLongClickListener
			grpReview.addView(rb, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			olderList.add(rb);
		}
	}
	private CheckState updateCheckState(TextView tv, CheckState cs, boolean reverse) {
		switch (cs) {
			case UNKNOW :
				if (reverse) {
					TransitionDrawable tsDrawTop = (TransitionDrawable) tv.getCompoundDrawables()[1];
					if (tsDrawTop == null) {
						tsDrawTop = (TransitionDrawable) getResources().getDrawable(R.drawable.review_select_transition);
						tsDrawTop.setCrossFadeEnabled(true);
						tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop, null, null);
					}
					return CheckState.DONE;
				} else {
					tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
				break;
			case DONE :
				TransitionDrawable tsDrawTop = (TransitionDrawable) tv.getCompoundDrawables()[1];
				if (tsDrawTop == null) {
					tsDrawTop = (TransitionDrawable) getResources().getDrawable(R.drawable.review_select_transition);
					tsDrawTop.setCrossFadeEnabled(true);
					tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop, null, null);
				} else {
					tsDrawTop.resetTransition();
				}
				if (reverse) {
					tsDrawTop.startTransition(400);
					return CheckState.UNDONE;
				}
				break;
			case UNDONE :
				TransitionDrawable tsDrawTop2 = (TransitionDrawable) tv.getCompoundDrawables()[1];
				if (tsDrawTop2 == null) {
					tsDrawTop2 = (TransitionDrawable) getResources().getDrawable(R.drawable.review_select_transition);
					tsDrawTop2.setCrossFadeEnabled(true);
					tsDrawTop2.startTransition(0);
					tv.setCompoundDrawablesWithIntrinsicBounds(null, tsDrawTop2, null, null);
				} else {
					tsDrawTop2.resetTransition();
					tsDrawTop2.startTransition(0);
				}
				if (reverse) {
					tsDrawTop2.reverseTransition(400);
					return CheckState.DONE;
				}
				break;
			default :
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
			todayDialogTitle = String.format(getResources().getString(R.string.todayControlPopupTitleFormat), time, todayMoral.getTitle());
		} else {
			todayDialogTitle = getResources().getString(R.string.today_control_popup_title);
		}
		today.setTodayDialogTitle(todayDialogTitle);
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
		for (int i = 0; i < olderList.size(); i++) {
			int index = UtilsClass.getIndexMorals(morals, olderList.get(i).getId());
			CheckState cs = UtilsClass.getNewWeekCSByDay(newWeek, morals.get(index), todayMoral.getStartDate(),
					today.getCurrentShowing() - 1);
			updateCheckState(olderList.get(i), cs, false);
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
		updateCommentDate();
		tvCycleReprotAppCount.setText(String.valueOf(getApp().getAppCon().getUseAppCount()));
		tvCycleReportUserCheckedCount.setText(String.valueOf(getApp().getMgr().getTotoalSignCount()));
		showIndicator();
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

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void updateUIByMoral(int index) {
		mainColor = Color.parseColor(CommonConst.colors[index % CommonConst.colors.length]);
		tvMainTitle.setBackgroundColor(mainColor);
		today.updateUIByMoral(index);
		totalDialog.updateUIByMoral(index);
		trendDialog.updateUIByMoral(index);
		llTitle.setBackgroundColor(mainColor);
		GradientDrawable gd = (GradientDrawable) getResources().getDrawable(R.drawable.review_ring);
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
			case 1 :
				res = R.drawable.homecolor1;
				break;
			case 2 :
				res = R.drawable.homecolor2;
				break;
			case 3 :
				res = R.drawable.homecolor3;
				break;
			case 4 :
				res = R.drawable.homecolor4;
				break;
			case 5 :
				res = R.drawable.homecolor5;
				break;
			case 6 :
				res = R.drawable.homecolor6;
				break;
			case 7 :
				res = R.drawable.homecolor7;
				break;
			case 8 :
				res = R.drawable.homecolor8;
				break;
			case 9 :
				res = R.drawable.homecolor9;
				break;
			case 10 :
				res = R.drawable.homecolor10;
				break;
			case 11 :
				res = R.drawable.homecolor11;
				break;
			case 12 :
				res = R.drawable.homecolor12;
				break;
			default :
				res = R.drawable.home2;
		}
		return res;
	}

	private int getHomeToolBarRes(int index) {
		int outIndex = index + 1;
		int res = 0;
		switch (outIndex) {
			case 1 :
				res = R.drawable.toolbarcolor1;
				break;
			case 2 :
				res = R.drawable.toolbarcolor2;
				break;
			case 3 :
				res = R.drawable.toolbarcolor3;
				break;
			case 4 :
				res = R.drawable.toolbarcolor4;
				break;
			case 5 :
				res = R.drawable.toolbarcolor5;
				break;
			case 6 :
				res = R.drawable.toolbarcolor6;
				break;
			case 7 :
				res = R.drawable.toolbarcolor7;
				break;
			case 8 :
				res = R.drawable.toolbarcolor8;
				break;
			case 9 :
				res = R.drawable.toolbarcolor9;
				break;
			case 10 :
				res = R.drawable.toolbarcolor10;
				break;
			case 11 :
				res = R.drawable.toolbarcolor11;
				break;
			case 12 :
				res = R.drawable.toolbarcolor12;
				break;
			default :
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
		getApp().getMgr().updateComment(com);
		updateCommentDate();
	}
}
