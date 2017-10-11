package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.SignRecords;
import com.pavkoo.franklin.common.UtilsClass;

public class BlemishReport extends View {
	private static final int ColTitleSpace = 5;
	private static final int RowTitleSpace = 5;
	private static float DotSpace = 5;
	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals) {
		this.morals = morals;
	}

	private Date current;

	private HashMap<String, List<SignRecords>> newWeekData;

	public HashMap<String, List<SignRecords>> getNewWeekData() {
		return newWeekData;
	}

	public void setNewWeekData(HashMap<String, List<SignRecords>> newWeekData) {
		this.newWeekData = newWeekData;
	}

	private Paint mPaintText;
	private Paint mPaintColTitle;
	private Paint mPaint;

	private int mRow;
	private int mCol;
	private float density;

	private float mDivideWidth;

	private float mCellWidth;
	private float mCellHeight;

	private float mTitleWidth;
	private float mTitleHeight;

	private final String dotColor = "#4a5b53";// 2a2f36
	private final String futurebg = "#e4e9e2";

	private List<String> mRowTitles;
	private SparseArray<List<Boolean>> mRowCsBuffer; // mRowTitles 与
														// mRowCsBuffer 一一对应
	private SparseArray<List<Rect>> mRectArea;
	private int mColorCount;

	private GestureDetector mDetector;

	private ValueAnimator scaleAnim;
	float frac = 1;
	private Rect dirtyRect;
	private OnBlackSpotClicked onBlackSpotClicked;

	public OnBlackSpotClicked getOnBlackSpotClicked() {
		return onBlackSpotClicked;
	}

	public void setOnBlackSpotClicked(OnBlackSpotClicked onBlackSpotClicked) {
		this.onBlackSpotClicked = onBlackSpotClicked;
	}

	public BlemishReport(Context context) {
		super(context);
		initView();
	}

	public BlemishReport(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BlemishReport(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		mRow = 0;
		mCol = 0;
		DisplayMetrics dm = new DisplayMetrics();
		dm = this.getContext().getApplicationContext().getResources().getDisplayMetrics();
		density = dm.density;
		mDivideWidth = 2;
		mCellWidth = 0;
		mCellHeight = 0;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintColTitle = new Paint(Paint.ANTI_ALIAS_FLAG);

		mPaintColTitle.setColor(Color.BLACK);
		mPaintColTitle.setTextSize(10 * density);
		mPaintColTitle.setTypeface(Typeface.SERIF);

		mPaintText.setTextSize(10 * density);

		mRowTitles = new ArrayList<String>();
		mRectArea = new SparseArray<List<Rect>>();
		mRowCsBuffer = new SparseArray<List<Boolean>>();
		mDetector = new GestureDetector(getContext(), mListener);

		scaleAnim = ValueAnimator.ofFloat(2, 1);
		scaleAnim.setInterpolator(new OvershootInterpolator());
		scaleAnim.setDuration(1000);
		scaleAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				frac = (Float) arg0.getAnimatedValue();
				ViewCompat.postInvalidateOnAnimation(BlemishReport.this);
			}
		});

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calcDrawproperty();
	}

	private void calcDrawproperty() {
		if (!canDraw())
			return;
		mRow = getRowCount();
		mCol = getItemCycle();
		mTitleWidth = getRowTitleWidth();
		mTitleHeight = getColTitleHeight();
		mCellWidth = ((float) getMeasuredWidth() - mTitleWidth) / mCol;
		mCellHeight = ((float) getMeasuredHeight() - mTitleHeight) / mRow;

		DotSpace = (float) (mCellWidth * 0.1);
		mColorCount = morals.get(mRow - 1).getCurrentDayInCycle();
		freshCS();
		cacheingRect();
	}

	private void showAnimation(Rect r) {
		dirtyRect = r;
		scaleAnim.start();
	}

	private void cacheingRect() {
		mRectArea.clear();
		float left = 0;
		float top = 0;
		float cellRight = 0;
		float cellbottom = 0;
		for (int i = 0; i < mRow; i++) {
			if (mRectArea.get(i) == null) {
				mRectArea.put(i, new ArrayList<Rect>());
			}
			for (int j = 0; j < mCol; j++) {
				left = mTitleWidth + j * mCellWidth;
				top = mTitleHeight + i * mCellHeight;
				cellRight = left + mCellWidth - mDivideWidth;
				cellbottom = top + mCellHeight - mDivideWidth;
				mRectArea.get(i).add(new Rect((int) left, (int) top, (int) cellRight, (int) cellbottom));
			}
		}
	}
	private int getRowCount() {
		int count = 0;
		mRowTitles.clear();
		mRowCsBuffer.clear();
		for (int i = 0; i < morals.size(); i++) {
			if (morals.get(i).isFinished() || morals.get(i).isDoing()) {
				++count;
				String title = morals.get(i).getTitle();
				if (UtilsClass.isEng()) {
					title = UtilsClass.shortString(title);
				}
				mRowTitles.add(title);
				mRowCsBuffer.put(i, new ArrayList<Boolean>());
			} else
				break;
		}
		return count;
	}
	private int getItemCycle() {
		if (morals.size() > 0) {
			return morals.get(0).getCycle();
		}
		return 7;
	}

	private float getRowTitleWidth() {
		float maxwidth = 0;
		float temp = 0;
		for (int i = 0; i < mRowTitles.size(); i++) {
			String title = mRowTitles.get(i);
			temp = mPaintText.measureText(title);
			if (maxwidth < temp) {
				maxwidth = temp;
			}
		}
		return maxwidth + RowTitleSpace;
	}

	private float getColTitleHeight() {
		FontMetrics fm = mPaintText.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent) + ColTitleSpace;
	}

	private void freshCS() {
		for (int i = 0; i < mRowTitles.size(); i++) {
			mRowCsBuffer.get(i).clear();
			for (int j = 0; j < mCol; j++) {
				if (j < mColorCount) {
					CheckState cs = UtilsClass.getNewWeekCSByDay(newWeekData, morals.get(i), current, j);
					mRowCsBuffer.get(i).add(false);
					if (cs != CheckState.DONE) {
						mRowCsBuffer.get(i).set(mRowCsBuffer.get(i).size() - 1, true);
					}
				} else {
					mRowCsBuffer.get(i).add(false);
				}
			}
		}
	}

	@Override
	public void invalidate() {
		freshCS();
		super.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (morals == null)
			return;
		float left = 0;
		float top = 0;

		// draw Row Title
		for (int i = 0; i < mRowTitles.size(); i++) {
			top = mCellHeight * i;
			mPaintText.setColor(Color.parseColor(CommonConst.colors[i % CommonConst.colors.length]));
			canvas.drawText(mRowTitles.get(i), left, top + mCellHeight + ColTitleSpace + 10 * density, mPaintText);
		}

		// draw Col Title
		top = 0;
		for (int i = 0; i < mCol; i++) {
			left = i * mCellWidth;
			canvas.drawText(String.valueOf(i + 1), left + mTitleWidth, top + mTitleHeight - ColTitleSpace, mPaintColTitle);
		}

		for (int i = 0; i < mRow; i++) {
			for (int j = 0; j < mCol; j++) {
				if (j < mColorCount) {
					mPaint.setColor(Color.parseColor(CommonConst.colorBg[i % CommonConst.colorBg.length]));
				} else {
					mPaint.setColor(Color.parseColor(futurebg));
				}
				canvas.drawRect(mRectArea.get(i).get(j), mPaint);
				if (mRowCsBuffer.get(i).get(j)) {
					mPaint.setColor(Color.parseColor(dotColor));
					Rect r = mRectArea.get(i).get(j);
					drawDot(canvas, mPaint, r.left, r.top, r.right, r.bottom);
				}
			}
		}
	}

	private void drawDot(Canvas canvas, Paint paint, float left, float top, float right, float bottom) {
		float width = right - left;
		float height = bottom - top;
		float squre = 0;
		squre = Math.min(width, height);
		float cx = (left + right) / 2;
		float cy = (top + bottom) / 2;
		if (scaleAnim.isRunning() && dirtyRect.contains((int) cx, (int) cy)) {
			squre = squre - 2 * DotSpace * frac;
		} else {
			squre = squre - 2 * DotSpace;
		}
		canvas.drawCircle(cx, cy, squre / 2, paint);
	}
	private boolean canDraw() {
		return morals == null ? false : true;
	}

	public Date getCurrent() {
		return current;
	}

	public void setCurrent(Date current) {
		this.current = current;
	}

	private SimpleOnGestureListener mListener = new SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			Log.i("SimpleOnGestureListener", "triggle");
			float x = e.getX();
			float y = e.getY();
			for (int i = 0; i < mRow; i++) {
				for (int j = 0; j < mColorCount; j++) {
					if (mRectArea.get(i).get(j).contains((int) x, (int) y)) {
						if (mRowCsBuffer.get(i).get(j)) {
							showAnimation(mRectArea.get(i).get(j));
							if (onBlackSpotClicked != null) {
								SignRecords sr = UtilsClass.getNewWeekCSByDayCreate(newWeekData, morals.get(i), current, j, true);
								onBlackSpotClicked.OnBlackSpotClickedCallBack(sr);
							}
						}
						return super.onDown(e);
					}
				}
			}
			return super.onDown(e);
		}
	};

	public interface OnBlackSpotClicked {
		void OnBlackSpotClickedCallBack(SignRecords sr);
	}
}
