package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.SignRecords;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.TodayDialog.DialogState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
//import android.widget.ImageView;
import android.widget.LinearLayout;

public class Today extends FrameLayout implements IUpdateViewCallback,
		IUpdateTextCallBack {

	private LinearLayout llBackground;
	private LinearLayout llContent;
	private Flipper FlipperNumber;
	private CircleImageView ivBg;
	private TodayDialog mDialog;

	public ArcDrawable arcBackground;
	private Bitmap bmpSmale;
	private Bitmap bmpSad;
	private String todayDialogTitle;
	private static final int IMAGEDOWNLOADED = 1;
	@SuppressLint("HandlerLeak")
	private Handler myHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case IMAGEDOWNLOADED:
				Bitmap bmp = (Bitmap) msg.obj;
				ivBg.setImageBitmap(bmp);
				arcBackground.changeBackground();
				break;
			default:
				break;
			}
		}
	};

	public String getTodayDialogTitle() {
		return todayDialogTitle;
	}

	public void setTodayDialogTitle(String todayDialogTitle) {
		this.todayDialogTitle = todayDialogTitle;
		mDialog.setDialogTitle(todayDialogTitle);
	}

	// private Animation ambmpBgScale;

	private HashMap<String, List<SignRecords>> newWeekData;

	public HashMap<String, List<SignRecords>> getNewWeekData() {
		return newWeekData;
	}

	public void setNewWeekData(HashMap<String, List<SignRecords>> newWeekData) {
		this.newWeekData = newWeekData;
	}

	public void updateUIByMoral(int index) {
		int mainColor = Color.parseColor(CommonConst.colors[index
				% CommonConst.colors.length]);
		int mainColorLight = Color.parseColor(CommonConst.colorBg[index
				% CommonConst.colorBg.length]);
		arcBackground.setCycleBGColor(mainColorLight);
		arcBackground.setCycleColor(mainColor);
		mDialog.updateUIByMoral(index);
	}

	private IUpdateMoralSelectState updateSelectState;

	public IUpdateMoralSelectState getUpdateSelectState() {
		return updateSelectState;
	}

	public void setUpdateSelectState(IUpdateMoralSelectState updateSelectState) {
		this.updateSelectState = updateSelectState;
	}

	private IUpdateTextCallBack updateText;

	protected IUpdateTextCallBack getUpdateText() {
		return updateText;
	}

	public void setUpdateText(IUpdateTextCallBack updateText) {
		this.updateText = updateText;
	}

	public Today(Context context) {
		super(context);
		setupEvent();
	}

	public Today(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		setupEvent();
	}

	public int getCurrentShowing() {
		return arcBackground.getCurrentShowing();
	}

	public void updateViewByMoral(Moral moral) {
		arcBackground.setAngleValue(-90);
		Animation roatate = AnimationUtils.loadAnimation(getContext(),
				R.anim.today_rotate);
		llBackground.startAnimation(roatate);
		ViewHelper.setRotation(llBackground, -90);
		arcBackground.setToday(moral);
		if (newWeekData.get(String.valueOf(moral.getId())) == null) {
			List<SignRecords> newWeekSignList = new ArrayList<SignRecords>();
			newWeekData.put(String.valueOf(moral.getId()), newWeekSignList);
		}
		arcBackground
				.setSignlist(newWeekData.get(String.valueOf(moral.getId())));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		llBackground.addView(arcBackground, lp);
		FlipperNumber.setCurrentValue(0);
		FlipperNumber.setTargetValue(moral.getCurrentDayInCycle());
		FlipperNumber.startAnimationText();
	}

	private void initView() {

		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);

		li.inflate(R.layout.today_control, this, true);
		iniPopupWindow(li);

		bmpSmale = BitmapFactory.decodeResource(getResources(),
				R.drawable.minsmiley);
		bmpSmale = RotateBitmap(bmpSmale, 90);
		bmpSad = BitmapFactory
				.decodeResource(getResources(), R.drawable.minsad);
		bmpSad = RotateBitmap(bmpSad, 90);
		llBackground = (LinearLayout) findViewById(R.id.llBackground);
		arcBackground = new ArcDrawable(getContext());
		arcBackground.setBmpSad(bmpSad);
		arcBackground.setBmpSmale(bmpSmale);
		arcBackground.updateFontSizeCallback = this;
		arcBackground.setUpdateText(this);
		llContent = (LinearLayout) findViewById(R.id.llContent);
		FlipperNumber = new Flipper(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		llContent.addView(FlipperNumber, params);
		FlipperNumber.setUpdateText(arcBackground);
		ivBg = (CircleImageView) findViewById(R.id.ivBg);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ConnectivityManager connManager = (ConnectivityManager) Today.this
						.getContext().getSystemService(
								Context.CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (mWifi.isConnected() && !UtilsClass.isEng()) {
					Bitmap bmp = UtilsClass.downloadBingImage();
					if (bmp != null) {
						Message msg = new Message();
						msg.obj = bmp;
						msg.what = IMAGEDOWNLOADED;
						myHandle.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	private static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	private void iniPopupWindow(LayoutInflater li) {
		mDialog = new TodayDialog(getContext(),
				android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public void updateTextCallBack(String value, int position) {
		// ivBg.startAnimation(ambmpBgScale);
		if (getUpdateText() != null) {
			updateText.updateTextCallBack(value, position);
		}
	}

	private void setupEvent() {
		FlipperNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ivBg.startAnimation(ambmpBgScale);
				SignRecords sr = arcBackground.getShowingSignRecordsCreate(
						arcBackground.getCurrentShowing() - 1, true);
				mDialog.setSr(sr);
				mDialog.showState(DialogState.DSSelection);
			}
		});
		Runnable longPress = new Runnable() {
			@Override
			public void run() {
				// ivBg.startAnimation(ambmpBgScale);
				SignRecords sr = arcBackground.getShowingSignRecordsCreate(
						arcBackground.getCurrentShowing() - 1, true);
				mDialog.setSr(sr);
				mDialog.showState(DialogState.DSNote);
			}
		};
		FlipperNumber.setLongPressRunnable(longPress);
		mDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mDialog.isResultChanged()) {

					arcBackground.setSignlist(newWeekData.get(String
							.valueOf(arcBackground.getToday().getId())));
					arcBackground.invalidate();
					if (updateSelectState != null) {
						updateSelectState.updateTextCallBack();
					}
				}
			};
		});
	}

	@Override
	public void callUpdateFontSize() {
		if (llContent == null)
			return;
		float marginRight = this.getWidth()
				- arcBackground.getCircleRect().right;
		float marginBottom = this.getHeight()
				- arcBackground.getCircleRect().bottom;
		LayoutParams lp = (LayoutParams) llContent.getLayoutParams();
		lp.setMargins((int) arcBackground.getCircleRect().left,
				(int) arcBackground.getCircleRect().top, (int) marginRight,
				(int) marginBottom);
		llContent.requestLayout();
		FlipperNumber.updateTextSize(arcBackground.getAdaptedFontSize());
		Log.i("llBackground width",
				String.valueOf(Today.this.llBackground.getWidth()));
		Log.i("llBackground height",
				String.valueOf(Today.this.llBackground.getHeight()));
		Animation roatate = AnimationUtils.loadAnimation(getContext(),
				R.anim.today_rotate);
		llBackground.startAnimation(roatate);
		if (ivBg != null) {
			ivBg.setLayoutParams(lp);
			ivBg.requestLayout();
		}
	}

	private static class ArcDrawable extends View implements
			IUpdateTextCallBack {
		public ArcDrawable(Context context) {
			super(context);
			initView();
		}

		public ArcDrawable(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView();
		}

		private void initView() {
			mOffsetAngle = 0;
			mCycle = 0;
			setmCurrentShowing(0);
			displayValue = "0";
			mAdapedRect = new RectF();
			setArcDoneColor(Color.parseColor("#990134"));
			setArcUnDoneColor(Color.parseColor("#3b4556"));
			setCycleBGColor(Color.parseColor("#bfe9c0"));
			setCycleColor(Color.parseColor("#2bb24c"));
			paint = new Paint();
			paint.setAntiAlias(true);
		}

		private Paint paint;
		private static double PI = 3.1415926f;

		// the width of the arc,which means the period of entire Cycle
		private static float ARC_STROKEWIDTH = 8;

		// the width of the cycle
		public static float CYCLE_STROKEWIDTH = 4;

		private static float SPACE_STROKEWIDTH = 0.5f;
		public static float SPACE_CYCLE_STROKEWIDTH = 4;
		// we have to set a padding ,in case of the boarder will be clipped
		private static float INNERPADDING = 32;

		// 偏移0度角的值
		private float mOffsetAngle;
		// 选择当前控控件中的中心的圆圈的矩形
		private RectF mAdapedRect;
		private RectF mCircleRect;

		public RectF getCircleRect() {
			return mCircleRect;
		}

		private int mArcDoneColor;
		private int mArcUnDoneColor;

		private String displayValue;
		private Bitmap mBmpSmale;

		protected Bitmap getBmpSmale() {
			return mBmpSmale;
		}

		protected void setBmpSmale(Bitmap mBmpSmale) {
			this.mBmpSmale = mBmpSmale;
		}

		private Moral today;

		public Moral getToday() {
			return today;
		}

		protected Bitmap getBmpSad() {
			return mBmpSad;
		}

		protected void setBmpSad(Bitmap mBmpSad) {
			this.mBmpSad = mBmpSad;
		}

		private Bitmap mBmpSad;

		protected int getArcUnDoneColor() {
			return mArcUnDoneColor;
		}

		private int mCurrentShowing;

		protected void setArcUnDoneColor(int mArcUnDoneColor) {
			this.mArcUnDoneColor = mArcUnDoneColor;
		}

		private int mCycleColor;
		private int mCycleBGColor;

		private float arcAngle;
		// cycle of the day count
		private int mCycle;

		protected int getCycle() {
			return mCycle;
		}

		private int arcSize;

		private List<SignRecords> newWeekSignList;

		public void setSignlist(List<SignRecords> signList) {
			this.newWeekSignList = signList;
		}

		protected void setCycle(int mCycle) {
			arcAngle = 360.0f / mCycle; // degree of per arc;
			this.mCycle = mCycle;
		}

		protected int getCycleBGColor() {
			return mCycleBGColor;
		}

		public void setCycleBGColor(int mCycleBGColor) {
			this.mCycleBGColor = mCycleBGColor;
			this.invalidate();
		}

		private float mAdaptedFontSize;

		private IUpdateViewCallback updateFontSizeCallback;

		public float getAdaptedFontSize() {
			return mAdaptedFontSize;
		}

		protected int getArcDoneColor() {
			return mArcDoneColor;
		}

		protected void setArcDoneColor(int mArcColor) {
			this.mArcDoneColor = mArcColor;
			this.invalidate();
		}

		protected int getCycleColor() {
			return mCycleColor;
		}

		public void setCycleColor(int mCycleColor) {
			this.mCycleColor = mCycleColor;
			this.invalidate();
		}

		private IUpdateTextCallBack updateText;

		protected IUpdateTextCallBack getUpdateText() {
			return updateText;
		}

		protected void setUpdateText(IUpdateTextCallBack updateText) {
			this.updateText = updateText;
		}

		protected RectF getAdapedRect() {
			return mAdapedRect;
		}

		protected void setAngleValue(float mAngleValue) {
			invalidate();
		}

		private boolean animastarted;
		private short cyclealpha = 255;

		private Handler mHandler = new Handler();
		private Runnable mTick = new Runnable() {
			public void run() {
				if (cyclealpha > 70) {
					ArcDrawable.this.invalidate();
					mHandler.postDelayed(this, 200); // 20ms == 60fps
					cyclealpha -= 5;
					return;
				}
				cyclealpha = 70;
			}
		};

		public void changeBackground() {
			animastarted = true;
			mHandler.removeCallbacks(mTick);
			mHandler.postDelayed(mTick, 4000);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			RectF oval = new RectF(this.getLeft(), getTop(), getRight(),
					getBottom());
			if (oval.isEmpty()) {
				if (mAdapedRect == null) {
					mAdapedRect = new RectF(oval);
				}
				return;
			}
			// 找到一个画圆圈的正方形
			if (oval.width() > oval.height()) {
				float offsetx = (oval.width() - oval.height()) / 2;
				oval.left = oval.left + offsetx;
				oval.right = oval.left + oval.height();
			} else if (oval.width() < oval.height()) {
				float offsety = (oval.height() - oval.width()) / 2;
				oval.top = oval.top + offsety;
				oval.bottom = oval.top + oval.width();
			}
			mAdapedRect = new RectF(oval);
			mAdapedRect.inset(ArcDrawable.INNERPADDING,
					ArcDrawable.INNERPADDING);
			mCircleRect = new RectF(mAdapedRect);
			mCircleRect.inset(CYCLE_STROKEWIDTH + SPACE_CYCLE_STROKEWIDTH,
					SPACE_CYCLE_STROKEWIDTH + CYCLE_STROKEWIDTH);
			adaptFontSize();
			Log.i("cycle width", String.valueOf(oval.width()));
			Log.i("cycle height", String.valueOf(oval.height()));
		}

		private void drawArc(Canvas canvas, Paint paint, RectF oval) {

			if (getCycle() == 0)
				return;
			float startAngle = mOffsetAngle;
			float sweepAngle = arcAngle - SPACE_STROKEWIDTH;
			int color = 0;

			for (int i = 0; i < arcSize; i++) {
				CheckState cs = getCSByStartDay(i);
				color = getArcColor(cs);
				paint.setColor(color);
				canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
				startAngle += arcAngle;
			}
		}

		private int getArcColor(CheckState check) {
			int color = 0;
			switch (check) {
			case DONE:
				color = getArcDoneColor();
				break;
			case UNDONE:
				color = getArcUnDoneColor();
				break;
			case UNKNOW:
				color = getCycleBGColor();
				break;
			default:
				break;
			}
			return color;
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Log.i("Today", "ondraw call");
			// 3 setp to draw
			// 1.draw angle
			// 2.draw cycle
			// 3.draw bitmap face
			if (getAdapedRect().isEmpty())
				return;
			if (newWeekSignList == null)
				return;
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(ARC_STROKEWIDTH);
			RectF oval = new RectF();
			oval.set(getAdapedRect());
			// 1.draw angle
			drawArc(canvas, paint, oval);
			oval.inset(CYCLE_STROKEWIDTH + SPACE_CYCLE_STROKEWIDTH,
					SPACE_CYCLE_STROKEWIDTH + CYCLE_STROKEWIDTH);
			paint.setStrokeWidth(CYCLE_STROKEWIDTH);
			paint.setColor(getCycleColor());
			paint.setStyle(Paint.Style.FILL);
			// 2.draw cycle
			if (animastarted) {
				paint.setAlpha(cyclealpha);
			}
			canvas.drawCircle(oval.centerX(), oval.centerY(), oval.width() / 2,
					paint);
			paint.setAlpha(255);
			// 3.draw bitmap face
			drawFace(canvas, paint, oval);
		}

		private void drawFace(Canvas canvas, Paint paint, RectF oval) {
			oval.inset(-(CYCLE_STROKEWIDTH + SPACE_CYCLE_STROKEWIDTH),
					-(SPACE_CYCLE_STROKEWIDTH + CYCLE_STROKEWIDTH));
			CheckState current = getCSByStartDay(getCurrentShowing() - 1);
			if (current == CheckState.UNKNOW)
				return;
			Bitmap bitmap;
			if (current == CheckState.DONE) {
				bitmap = this.getBmpSmale();
			} else {
				bitmap = this.getBmpSad();
			}
			float CycleCenterX = oval.centerX();
			float CycleCenterY = oval.centerY();
			float Radius = oval.width() / 2;
			float angle = (arcAngle * (2 * (getCurrentShowing()) - 1)) * 0.5f;
			float arcCenterX = (float) (Radius * Math.cos(PI / 180 * angle))
					+ CycleCenterX;
			float arcCenterY = (float) (Radius * Math.sin(PI / 180 * angle))
					+ CycleCenterY;

			if (arcCenterX < CycleCenterX) {
				arcCenterX = arcCenterX - bitmap.getWidth();
			}
			if (arcCenterY < CycleCenterY) {
				arcCenterY = arcCenterY - bitmap.getWidth();
			}

			canvas.drawBitmap(bitmap, arcCenterX, arcCenterY, paint);
		}

		private void adaptFontSize() {
			RectF oval = new RectF();
			oval.set(getAdapedRect());
			oval.inset(oval.width() / 4, oval.height() / 4);
			Rect rect = new Rect();
			paint.setTextSize(10);
			String temp = "0";
			if (!displayValue.equals("")) {
				temp = displayValue;
			}
			paint.getTextBounds(temp, 0, 1, rect);
			int i = 10;
			while (rect.width() < oval.width() && rect.height() < oval.height()) {
				i++;
				paint.setTextSize(i);
				paint.getTextBounds(temp, 0, 1, rect);
			}
			mAdaptedFontSize = i - 2;
			if (updateFontSizeCallback != null) {
				updateFontSizeCallback.callUpdateFontSize();
			}
		}

		@Override
		public void updateTextCallBack(String value, int position) {
			setmCurrentShowing(position);
			displayValue = value;
			this.invalidate();
			if (getUpdateText() != null) {
				updateText.updateTextCallBack(value, position);
			}
		}

		public int getCurrentShowing() {
			return mCurrentShowing;
		}

		public void setmCurrentShowing(int mCurrentShowing) {
			this.mCurrentShowing = mCurrentShowing;
		}

		public void setToday(Moral today) {
			this.today = today;
			this.arcSize = today.getCurrentDayInCycle();
			this.mCurrentShowing = today.getCurrentDayInCycle();
			setCycle(today.getCycle());
		}

		public SignRecords getShowingSignRecordsCreate(int index,
				boolean createNew) {
			Date current = today.getStartDate();
			current = UtilsClass.subDate(current, -index);
			SignRecords sr = null;
			for (int i = 0; i < this.newWeekSignList.size(); i++) {
				if (current.compareTo(newWeekSignList.get(i).getInputDate()) == 0) {
					sr = newWeekSignList.get(i);
					break;
				}
			}
			if (createNew && sr == null) {
				sr = new SignRecords();
				sr.setMoarlIndex(today.getId());
				sr.setInputDate(current);
			}
			return sr;
		}

		private CheckState getCSByStartDay(int index) {
			SignRecords sr = getShowingSignRecordsCreate(index, false);
			if (sr == null) {
				return CheckState.UNKNOW;
			} else {
				return sr.getCs();
			}
		}
	}

}
