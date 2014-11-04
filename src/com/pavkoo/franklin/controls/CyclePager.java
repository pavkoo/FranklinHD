package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressLint("HandlerLeak")
public class CyclePager extends ViewGroup {
	// 速度跟踪
	private VelocityTracker mVelocityTracker;
	private int mMaximumVelocity;

	// 手势临界速度，当速度超过这个时切换到下一屏
	private static final int SNAP_VELOCITY = 100;

	// 停止状态
	private final static int TOUCH_STATE_REST = 0;
	// 滚动状态
	private final static int TOUCH_STATE_MOVING = 1;
	// 减速停止状态
	private final static int TOUCH_STATE_SLOWING = 2;

	// 当前触摸状态
	private int touchState = TOUCH_STATE_REST;

	private boolean lock = false;

	private float lastMotionX;
	private float lastMotionY;

	private List<LinearLayout> views;
	// 是否移动了
	private boolean isMoved;
	// 长按的runnable
	private Runnable mLongPressRunnable;
	// 移动的阈值
	private static final int TOUCH_SLOP = 10;

	public int width;

	public int height;
	
	private IUpdateViewCallback updateViewCallBack;

	public IUpdateViewCallback getUpdateViewCallBack() {
		return updateViewCallBack;
	}

	public void setUpdateViewCallBack(IUpdateViewCallback updateViewCallBack) {
		this.updateViewCallBack = updateViewCallBack;
	}

	public CyclePager(Context context) {
		super(context);
		views = new ArrayList<LinearLayout>();
	}
	
	public CyclePager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		views = new ArrayList<LinearLayout>();
	}

	public CyclePager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setHapticFeedbackEnabled(false);
		views = new ArrayList<LinearLayout>();
	}
	

	public void init(LinearLayout view1, LinearLayout view2, LinearLayout view3) {		
		view1.setTag(0);
		this.addView(view1);
		views.add(view1);

		view2.setTag(1);
		this.addView(view2);
		views.add(view2);

		view3.setTag(2);
		this.addView(view3);
		views.add(view3);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = views.size();
		for (int i = 0; i < count; i++) {
			final View child = views.get(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}

		int finalWidth, finalHeight;
		finalWidth = measureWidth(widthMeasureSpec);
		finalHeight = measureHeight(heightMeasureSpec);

		this.width = finalWidth;
		this.height = finalHeight;
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = specSize;
		}

		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = specSize;
		}
		return result;
	}
	
	public int getCurrent(){
		if (views==null||views.size()!=3) return -1;
		int tag = (Integer) views.get(1).getTag();
		return tag;   //0左 1 中 2 右
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = -1;
		final int count = views.size();
		// 水平从左到右放置
		for (int i = 0; i < count; i++) {
			final View child = views.get(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				if (childLeft == -1) {
					childLeft = -childWidth;
				}
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	// 绘制子元素
	@Override
	protected void onDraw(Canvas canvas) {
		Log.i("CyclePager","onDraw");
		// 水平从左到右放置
		int count = views.size();
		for (int i = 0; i < count; i++) {
			View child = views.get(i);
			drawChild(canvas, child, getDrawingTime());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!lock) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);

			final int action = ev.getAction();
			final float x = ev.getX();
			final float y = ev.getY();

			switch (action) {
				case MotionEvent.ACTION_DOWN :// 按下去
					if (touchState == TOUCH_STATE_REST) {
						// 记录按下去的的x坐标
						lastMotionX = x;
						touchState = TOUCH_STATE_MOVING;

						isMoved = false;
					}

					break;
				case MotionEvent.ACTION_MOVE :// 拖动时
					if (touchState == TOUCH_STATE_MOVING) {
						float offsetX = x - lastMotionX;
						float offsetY = y - lastMotionY;

						if (isMoved) {
							lastMotionX = x;
							lastMotionY = y;

							final int count = views.size();
							// 水平从左到右放置
							for (int i = 0; i < count; i++) {
								final View child = views.get(i);
								if (child.getVisibility() != View.GONE) {
									final int childWidth = child.getMeasuredWidth();
									int childLeft = child.getLeft() + (int) offsetX;
									child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
									childLeft += childWidth;
								}
							}
						} else if (Math.abs(offsetX) > TOUCH_SLOP || Math.abs(offsetY) > TOUCH_SLOP) {
							// 移动超过阈值，则表示移动了
							isMoved = true;
							removeCallbacks(mLongPressRunnable);
						}
					}

					break;
				case MotionEvent.ACTION_UP :// 放开时
					// 释放了
					removeCallbacks(mLongPressRunnable);

					if (isMoved) {
						if (touchState == TOUCH_STATE_MOVING) {
							touchState = TOUCH_STATE_SLOWING;
							int sign = 0;
							final VelocityTracker velocityTracker = mVelocityTracker;
							// 计算当前速度
							velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
							// x方向的速度
							int velocityX = (int) velocityTracker.getXVelocity();
							if (velocityX > SNAP_VELOCITY)// 足够的能力向左
							{
								sign = 1;
								Log.i("enough to move left", "true");
							} else if (velocityX < -SNAP_VELOCITY)// 足够的能力向右
							{
								sign = -1;
								Log.i("enough to move right", "right");
							} else {
								sign = 0;
							}
							moveToFitView(sign);
							if (mVelocityTracker != null) {
								mVelocityTracker.recycle();
								mVelocityTracker = null;
							}

						}
					}

					break;
			}
		}
		return true;
	}

	int offset = 0;
	private void moveToFitView(int sign) {
		swapView(sign);
		if (true) {
			View view1 = views.get(1);
			int left = view1.getLeft();
			// int offset=0;
			if (left != 0) {
				offset = -1 * left;
			}

			moveView();
		}
	}

	FlipAnimationHandler mAnimationHandler;
	int ovv = 70;
	private void moveView() {
		final int count = views.size();

		if (offset != 0) {
			int ov = 0;
			if (offset > 0) {
				ov = ovv;
			} else {
				ov = -1 * ovv;
			}
			ovv = ovv - 3;
			if (ovv < 1) {
				ovv = 3;
			}
			if (Math.abs(offset) < Math.abs(ov)) {
				ov = offset;
				offset = 0;

			} else {
				offset = offset - ov;
			}

			// 水平从左到右放置
			for (int i = 0; i < count; i++) {
				final View child = views.get(i);
				final int childWidth = child.getMeasuredWidth();
				int childLeft = child.getLeft() + ov;
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}

			if (mAnimationHandler == null) {
				mAnimationHandler = new FlipAnimationHandler();
			}
			mAnimationHandler.sleep(1);
		} else {
			ovv = 70;
			touchState = TOUCH_STATE_REST;
			if (updateViewCallBack !=null){
				updateViewCallBack.callUpdateFontSize();
			}
		}
	}

	class FlipAnimationHandler extends Handler {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			CyclePager.this.moveView();
		}

		public void sleep(long millis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), millis);
		}
	}

	private boolean swapView(int sign) {
		boolean b = false;
		if (sign == -1)// 向左
		{
			View view0 = views.get(0);
			if (view0.getLeft() <= -1 * view0.getMeasuredWidth()) {
				swapViewIndex(sign);

				View view2 = views.get(1);
				View view3 = views.get(2);
				int childWidth = view2.getMeasuredWidth();
				int childLeft = view2.getLeft() + childWidth;
				view3.layout(childLeft, 0, childLeft + view3.getMeasuredWidth(), view3.getMeasuredHeight());
				b = true;
			}
		} else if (sign == 1)// 向右
		{
			View view3 = views.get(2);
			if (view3.getLeft() > view3.getMeasuredWidth()) {
				swapViewIndex(sign);

				View view1 = views.get(0);
				View view2 = views.get(1);
				int childRight = view2.getLeft();
				int childLeft = childRight - view1.getMeasuredWidth();
				view1.layout(childLeft, 0, childRight, view1.getMeasuredHeight());
				b = true;
			}
		}

		return b;
	}

	private void swapViewIndex(int sign) {
		if (sign == -1)// 向左
		{
			LinearLayout v = views.remove(0);
			views.add(v);
		} else if (sign == 1)// 向右
		{
			LinearLayout v = views.remove(views.size() - 1);
			views.add(0, v);
		}
	}
}