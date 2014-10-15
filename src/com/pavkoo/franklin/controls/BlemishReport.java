package com.pavkoo.franklin.controls;

import java.util.List;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

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
	

	private Paint mPaint;

	private int mRow;
	private int mCol;
	private float density;

	private float mDivideWidth;

	private float mCellWidth;
	private float mCellHeight;

	private float mTitleWidth;
	private float mTitleHeight;

	private final String dotColor= "#4a5b53";//2a2f36
	private final String futurebg = "#e4e9e2";
	
	

	private void calcDrawproperty() {
		if (!canDraw()) return ;
		mRow = getRowCount();
		mCol = getItemCycle();
		mTitleWidth = getRowTitleWidth();
		mTitleHeight = getColTitleHeight();
		mCellWidth = ((float) getMeasuredWidth() - mTitleWidth) / mCol;
		mCellHeight = ((float) getMeasuredHeight() - mTitleHeight) / mRow;
		DotSpace = (float) (mCellWidth * 0.1);
	}

	private int getRowCount() {
		int count = 0;
		for (int i = 0; i < morals.size(); i++) {
			if (morals.get(i).isFinished())
				++count;
			else
				break;
		}
		// 已经完成的加一个正在进行的
		return ++count;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calcDrawproperty();
		Log.i("BlemishReport","onSizeChanged");
	}

	private int getItemCycle() {
		if (morals.size() > 0) {
			return morals.get(0).getCycle();
		}
		return 7;
	}

	private float getRowTitleWidth() {
		mPaint.setTextSize(10 * density);
		float maxwidth = 0;
		float temp = 0;
		for (int i = 0; i < morals.size(); i++) {
			temp = mPaint.measureText(morals.get(i).getTitle());
			if (maxwidth < temp) {
				maxwidth = temp;
			}
			if (!morals.get(i).isFinished()) {
				break;
			}
		}
		return maxwidth + RowTitleSpace;
	}

	private float getColTitleHeight() {
		mPaint.setTextSize(10 * density);
		FontMetrics fm = mPaint.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent) + ColTitleSpace;
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
		// DisplayMetrics dm = new DisplayMetrics();
		// this.getDisplay().getMetrics(dm);
		// float d = dm.density;
		mDivideWidth = 2;
		mCellWidth = 0;
		mCellHeight = 0;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		DisplayMetrics dm = new DisplayMetrics();
		dm = this.getContext().getApplicationContext().getResources().getDisplayMetrics();
		density = dm.density;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//TODO:性能修改啊
		Log.i("ondraw call","blemish");
		if (morals == null)
			return;
		float left = 0;
		float top = 0;

		// draw Row Title
		mPaint.setTextSize(10 * density);
		for (int i = 0; i < mRow; i++) {
			top = mCellHeight * i;
			mPaint.setColor(Color.parseColor(CommonConst.colors[i % CommonConst.colors.length]));
			canvas.drawText(morals.get(i).getTitle(), left, top + mCellHeight + ColTitleSpace + 10 * density, mPaint);
		}

		// draw Col Title
		top = 0;
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(10 * density);
		mPaint.setTypeface(Typeface.SERIF);
		for (int i = 0; i < mCol; i++) {
			left = i * mCellWidth;
			canvas.drawText(String.valueOf(i + 1), left + mTitleWidth, top + mTitleHeight - ColTitleSpace, mPaint);
		}
		int lastj = morals.get(mRow - 1).getCurrentDayInCycle();
		int jtotal = 0;
		float cellRight = 0;
		float cellbottom = 0;
		for (int i = 0; i < mRow; i++) {
			// if (i == mRow - 1) {
			jtotal = lastj;
			// } else
			// jtotal = mCol;
			for (int j = 0; j < mCol; j++) {
				left = mTitleWidth + j * mCellWidth;
				top = mTitleHeight + i * mCellHeight;
				cellRight = left + mCellWidth - mDivideWidth;
				cellbottom = top + mCellHeight - mDivideWidth;
				if (j < jtotal) {
					mPaint.setColor(Color.parseColor(CommonConst.colorBg[i % CommonConst.colorBg.length]));
				} else {
					mPaint.setColor(Color.parseColor(futurebg));
				}
				canvas.drawRect(left, top, cellRight, cellbottom, mPaint);
				if (j < jtotal && morals.get(i).getHistorySelected(j + 1) != CheckState.DONE) {
					mPaint.setColor(Color.parseColor(dotColor));
					drawDot(canvas, mPaint, left, top, cellRight, cellbottom);
				}
			}
		}
	}

	private void drawDot(Canvas canvas, Paint paint, float left, float top, float right, float bottom) {
		float width = right - left;
		float height = bottom - top;
		float squre = 0;
		if (width > height) {
			squre = height - 2 * DotSpace;
			canvas.drawCircle((left + right) / 2, (top + bottom) / 2, squre / 2, paint);
		} else {
			squre = width - 2 * DotSpace;
			canvas.drawCircle((left + right) / 2, (top + bottom) / 2, squre / 2, paint);
		}
	}
	
	
	private boolean canDraw(){
		return morals==null?false:true; 
	}
}
