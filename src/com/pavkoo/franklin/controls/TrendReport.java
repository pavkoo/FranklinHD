package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TrendReport extends View {
	private Paint mPaint;
	private Path path;
	private float spacex;
	private float spacey;
	private List<Double> points;

	private Moral moral;

	public Moral getMoral() {
		return moral;
	}

	public void setMoral(Moral moral) {
		this.moral = moral;
		this.invalidate();
		points.clear();
		if (moral == null)
			return;
		int cycleCount = moral.getCycleCount();
		for (int i = 0; i < cycleCount; i++) {
			points.add((double) moral.getCheckRate(i));
		}
	}

	public TrendReport(Context context) {
		super(context);
		initView();
	}

	public TrendReport(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TrendReport(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		path = new Path();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeWidth(2.0f);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		points = new ArrayList<Double>();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float targetx = 0;
		float targety = 0;
		float currentx = 0;
		float currenty = spacey;
		mPaint.setColor(Color.parseColor("#7d8187"));
		canvas.drawLine(0, 0,getMeasuredWidth(), 0, mPaint);
		canvas.drawLine(0, getMeasuredHeight(),getMeasuredWidth(), getMeasuredHeight(), mPaint);
		mPaint.setColor(Color.parseColor("#3a4456"));
		for (int i = 0; i < points.size(); i++) {
			path.moveTo(currentx, currenty);
			targetx = spacex * (i + 1);
			targety =spacey - (float) (spacey * points.get(i));
			path.quadTo((currentx + targetx) / 2, (currenty + targety) / 2, targetx, targety);
			currentx = targetx;
			currenty = targety;
		}
		canvas.drawPath(path, mPaint);
		targetx = 0;
		targety = 0;
		currentx = 0;
		currenty = spacey;
		mPaint.setColor(Color.parseColor("#61ca63"));
		for (int i = 0; i < points.size(); i++) {
			targetx = spacex * (i + 1);
			targety =spacey - (float) (spacey * points.get(i));
			canvas.drawCircle(targetx, targety,4, mPaint);
			currentx = targetx;
			currenty = targety;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		getSpace();
	}

	private void getSpace() {
		if (points.size() == 0) {
			spacex = this.getMeasuredWidth();
		} else {
			spacex = this.getMeasuredWidth() / (points.size()); // begin
																	// from 0
		}
		spacey = this.getMeasuredHeight() - 4 ;
		Log.i("TrendReport", "width:"+String.valueOf(spacex));
		Log.i("TrendReport", "heigh:"+String.valueOf(spacey));
	}

}

