package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.Comment;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.FranklinApplication;
import com.pavkoo.franklin.common.Moral;
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
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
//import android.widget.ImageView;
import android.widget.LinearLayout;

public class Today extends FrameLayout implements IUpdateViewCallback, IUpdateTextCallBack {


	private LinearLayout llBackground;
	private LinearLayout llContent;
	private Flipper FlipperNumber;
//	private ImageView ivBg;
	private TodayDialog mDialog;

	public ArcDrawable arcBackground;
	private Bitmap bmpSmale;
	private Bitmap bmpSad;
	private String todayDialogTitle;
	private FranklinApplication app;

	public String getTodayDialogTitle() {
		return todayDialogTitle;
	}

	public void setTodayDialogTitle(String todayDialogTitle) {
		this.todayDialogTitle = todayDialogTitle;
		mDialog.setDialogTitle(todayDialogTitle);
	}

//	private Animation ambmpBgScale;

	private Moral moral;

	public Moral getMoral() {
		return moral;
	}
	
	public void updateUIByMoral(int index){
		int mainColor =Color.parseColor(CommonConst.colors[index % CommonConst.colors.length]);
		int mainColorLight =Color.parseColor(CommonConst.colorBg[index % CommonConst.colorBg.length]);
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

	public void setMoral(Moral moral) {
		this.moral = moral;
		updateViewByMoral();
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

	private void updateViewByMoral() {
		arcBackground.setAngleValue(-90);
		Animation roatate = AnimationUtils.loadAnimation(getContext(), R.anim.today_rotate);
		llBackground.startAnimation(roatate);
		ViewHelper.setRotation(llBackground, -90);
		arcBackground.setCycle(moral.getCycle());
		arcBackground.setHistoryCheckList(moral.getStateList());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); 
		llBackground.addView(arcBackground,lp);
		//llBackground.setImageResource(R.drawable.treebig);
//		llBackground.setBackground(arcBackground);
//		if (Build.VERSION.SDK_INT >= 16) {
//			llBackground.setBackground(arcBackground);
//		} else {
//			llBackground.setBackgroundDrawable(arcBackground);
//		}
		//llBackground.invalidateDrawable(arcBackground);
		FlipperNumber.setCurrentValue(0);
		FlipperNumber.setTargetValue(moral.getCurrentDayInCycle());
		FlipperNumber.startAnimationText();
	}

	private void initView() {

		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);

		li.inflate(R.layout.today_control, this, true);
		iniPopupWindow(li);

		bmpSmale = BitmapFactory.decodeResource(getResources(), R.drawable.minsmiley);
		bmpSmale = RotateBitmap(bmpSmale, 90);
		bmpSad = BitmapFactory.decodeResource(getResources(), R.drawable.minsad);
		bmpSad = RotateBitmap(bmpSad, 90);
		llBackground = (LinearLayout) findViewById(R.id.llBackground);
		arcBackground = new ArcDrawable(getContext());
		arcBackground.setBmpSad(bmpSad);
		arcBackground.setBmpSmale(bmpSmale);
		arcBackground.updateFontSizeCallback = this;
		arcBackground.setUpdateText(this);
		llContent = (LinearLayout) findViewById(R.id.llContent);
		FlipperNumber = new Flipper(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		llContent.addView(FlipperNumber, params);
		FlipperNumber.setUpdateText(arcBackground);
		app = (FranklinApplication) this.getContext().getApplicationContext();
//		ivBg = (ImageView) findViewById(R.id.ivBg);
//		ambmpBgScale = AnimationUtils.loadAnimation(getContext(), R.anim.today_bg_scale);
//		ivBg.setAnimation(ambmpBgScale);
	}

	private static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	private void iniPopupWindow(LayoutInflater li) {
		mDialog = new TodayDialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public void updateTextCallBack(String value, int position) {
//		ivBg.startAnimation(ambmpBgScale);
		if (getUpdateText() != null) {
			updateText.updateTextCallBack(value, position);
		}
	}

	private void setupEvent() {
		FlipperNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				ivBg.startAnimation(ambmpBgScale);
				mDialog.setCheckState(moral.getHistorySelected(getCurrentShowing()));
				mDialog.showState(DialogState.DSSelection);
			}
		});
		Runnable longPress = new Runnable() {
			@Override
			public void run() {
//				ivBg.startAnimation(ambmpBgScale);
				mDialog.setCheckState(moral.getHistorySelected(getCurrentShowing()));
				mDialog.showState(DialogState.DSNote);
			}
		};
		FlipperNumber.setLongPressRunnable(longPress);
		mDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mDialog.isResultChanged()) {
					if (mDialog.isNewComment()) {
						moral.setHistorySelected(getCurrentShowing(), mDialog.getCheckState(), mDialog.getNewCommentIndex());
					} else {
						int commentIndex = moral.getCommentIndex(getCurrentShowing());
						if (commentIndex!=-1){
							Comment c = app.getComments().get(commentIndex);
							c.setRemoved(true);
							app.saveComments(app.getComments());
						}
						moral.setHistorySelected(getCurrentShowing(), mDialog.getCheckState());
					}
					arcBackground.setHistoryCheckList(moral.getStateList());
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
		float marginRight = this.getWidth() - arcBackground.getAdapedRect().right;
		float marginBottom = this.getHeight() - arcBackground.getAdapedRect().bottom;
		LayoutParams lp = (LayoutParams) llContent.getLayoutParams();
		lp.setMargins((int) arcBackground.getAdapedRect().left, (int) arcBackground.getAdapedRect().top, (int) marginRight,
				(int) marginBottom);
		llContent.requestLayout();
		FlipperNumber.updateTextSize(arcBackground.getAdaptedFontSize());
		Log.i("llBackground width", String.valueOf(Today.this.llBackground.getWidth()));
		Log.i("llBackground height", String.valueOf(Today.this.llBackground.getHeight()));
		Animation roatate = AnimationUtils.loadAnimation(getContext(), R.anim.today_rotate);
		llBackground.startAnimation(roatate);
	}

	private static class ArcDrawable extends View implements IUpdateTextCallBack {
		public ArcDrawable(Context context) {
			super(context);
			initView();
		}
		
		public ArcDrawable(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView();
		}
		
		private void initView(){
			mOffsetAngle = 0;
			mCycle = 0;
			mCurrentShowing = 0;
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
		private static float CYCLE_STROKEWIDTH = 4;

		private static float SPACE_STROKEWIDTH = 0.5f;
		private static float SPACE_CYCLE_STROKEWIDTH = 4;
		// we have to set a padding ,in case of the boarder will be clipped
		private static float INNERPADDING = 32;

		// 偏移0度角的值
		private float mOffsetAngle;
		// 选择当前控控件中的中心的圆圈的矩形
		private RectF mAdapedRect;

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

		public int getCurrentShowing() {
			return mCurrentShowing;
		}

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

		// the last child means today
		private List<CheckState> mHistoryCheckList;

		@SuppressWarnings("unused")
		public List<CheckState> getHistoryCheckList() {
			return mHistoryCheckList;
		}

		public void setHistoryCheckList(List<CheckState> HistoryCheckList) {
			if (mHistoryCheckList == null) {
				this.mHistoryCheckList = new ArrayList<CheckState>();
			}
			this.mHistoryCheckList.clear();
//			int lastIndex = HistoryCheckList.size() % mCycle;
//			lastIndex = HistoryCheckList.size() - lastIndex;
			for (int i = 0; i < HistoryCheckList.size(); i++) {
				this.mHistoryCheckList.add(HistoryCheckList.get(i));
			}
			if (mCurrentShowing == 0) {
				mCurrentShowing = mHistoryCheckList.size();
			}
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
		
		

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			RectF oval = new RectF(this.getLeft(),getTop(),getRight(),getBottom());
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
			mAdapedRect.inset(ArcDrawable.INNERPADDING, ArcDrawable.INNERPADDING);
			adaptFontSize();
			Log.i("cycle width", String.valueOf(oval.width()));
			Log.i("cycle height", String.valueOf(oval.height()));
		}

		private void drawArc(Canvas canvas, Paint paint, RectF oval) {
			
			if (getCycle() == 0)
				return;
			// draw History
			int size = mHistoryCheckList.size() % mCycle;
			if (size==0){
				size = mCycle;
			}
			float startAngle = mOffsetAngle;
			float sweepAngle = arcAngle - SPACE_STROKEWIDTH;
			int color = 0;
			for (int i = 0; i < size; i++) {
				color = getArcColor(mHistoryCheckList.get(i));
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
			Log.i("Today","ondraw call");
			// 3 setp to draw
			// 1.draw angle
			// 2.draw cycle
			// 3.draw bitmap face
			if (getAdapedRect().isEmpty())
				return;
			if (mHistoryCheckList == null)
				return;
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(ARC_STROKEWIDTH);
			RectF oval = new RectF();
			oval.set(getAdapedRect());
			// 1.draw angle
			drawArc(canvas, paint, oval);
			oval.inset(CYCLE_STROKEWIDTH + SPACE_CYCLE_STROKEWIDTH, SPACE_CYCLE_STROKEWIDTH + CYCLE_STROKEWIDTH);
			paint.setStrokeWidth(CYCLE_STROKEWIDTH);
			paint.setColor(getCycleColor());
			paint.setStyle(Paint.Style.FILL);
			// 2.draw cycle
			canvas.drawCircle(oval.centerX(), oval.centerY(), oval.width() / 2, paint);
			// 3.draw bitmap face
			drawFace(canvas, paint, oval);
		}

		private void drawFace(Canvas canvas, Paint paint, RectF oval) {
			oval.inset(-(CYCLE_STROKEWIDTH + SPACE_CYCLE_STROKEWIDTH), -(SPACE_CYCLE_STROKEWIDTH + CYCLE_STROKEWIDTH));
			CheckState current = mHistoryCheckList.get(mCurrentShowing - 1);
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
			float angle = (arcAngle * (2 * (mCurrentShowing) - 1)) * 0.5f;
			float arcCenterX = (float) (Radius * Math.cos(PI / 180 * angle)) + CycleCenterX;
			float arcCenterY = (float) (Radius * Math.sin(PI / 180 * angle)) + CycleCenterY;

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
			mCurrentShowing = position;
			displayValue = value;
			this.invalidate();
			if (getUpdateText() != null) {
				updateText.updateTextCallBack(value, position);
			}
		}
	}

}
