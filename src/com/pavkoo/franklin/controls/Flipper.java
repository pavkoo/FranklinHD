package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class Flipper extends ViewGroup {

	private LinearLayout view1;
	private LinearLayout view2;
	private LinearLayout view3;

	private IUpdateTextCallBack updateText;
	protected IUpdateTextCallBack getUpdateText() {
		return updateText;
	}

	protected void setUpdateText(IUpdateTextCallBack updateText) {
		this.updateText = updateText;
	}

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
	private final static String nomore="-.-";
	// 当前触摸状态
	private int touchState = TOUCH_STATE_REST;

	private boolean lock = true;

	private float lastMotionX;
	private float startMotionX;
	private float invisiableLeft;

	private Context context;
	private List<LinearLayout> views;
	private List<TextView> textViews;
	private Typeface typeFace;

	public List<TextView> getTextViews() {
		return textViews;
	}

	// 是否移动了
	private boolean isMoved;
	public boolean isMoved() {
		return isMoved;
	}

	// 长按的runnable
	private Runnable mLongPressRunnable;
	public Runnable getLongPressRunnable() {
		return mLongPressRunnable;
	}

	public void setLongPressRunnable(Runnable mLongPressRunnable) {
		this.mLongPressRunnable = mLongPressRunnable;
	}

	// 移动的阈值
	private static final int TOUCH_SLOP = 25;

	public int width;

	public int height;

	private int mCurrentValue;

	int sign = 0;

	protected int getCurrentValue() {
		return mCurrentValue;
	}

	protected void setCurrentValue(int mCurrentValue) {
		this.mCurrentValue = mCurrentValue;
		updateText();
	}

	protected int getTargetValue() {
		return mTargetValue;
	}

	protected void setTargetValue(int mTargetValue) {
		this.mTargetValue = mTargetValue;
		updateText();
	}

	private int mTargetValue;

	private static final int GROWTEXTNUMBER = 1;
	private Handler textHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GROWTEXTNUMBER:
				if (mCurrentValue == mTargetValue) {
					updateText();
					lock = false;
					return;
				}
				textViews.get(1).setText(String.valueOf(++mCurrentValue));
				this.sendEmptyMessageDelayed(GROWTEXTNUMBER, 100);
				break;
			default:
				break;
			}
		}
	};
	

	public Flipper(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private TextView makeTextView() {
		TextView tv = new TextView(context);
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setTextColor(getResources().getColor(R.color.white_app_txt_white));
		tv.setTypeface(typeFace);
		return tv;
	}

	private void init() {
		float width = 100;
		views = new ArrayList<LinearLayout>();
		textViews = new ArrayList<TextView>();
//		typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/CaviarDreams.ttf");
//		typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/riesling.ttf");
//		typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/NexaLight.otf");
		typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/QuicksandLight.otf");
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		 lp.setMargins((int) -width, 0, (int) -width, 0);
		
		view1 = new LinearLayout(context);
		view1.setGravity(Gravity.CENTER);
		view1.setLayoutParams(lp);
		this.addView(view1);
		TextView tv = makeTextView();
		view1.addView(tv);
		views.add(view1);
		textViews.add(tv);

		view2 = new LinearLayout(context);
		view2.setGravity(Gravity.CENTER);
		view2.setLayoutParams(lp);
		TextView tv2 = makeTextView();
		view2.addView(tv2);
		this.addView(view2);
		views.add(view2);
		textViews.add(tv2);

		view3 = new LinearLayout(context);
		view3.setGravity(Gravity.CENTER);
		view3.setLayoutParams(lp);
		TextView tv3 = makeTextView();
		view3.addView(tv3);
		this.addView(view3);
		views.add(view3);
		textViews.add(tv3);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());

		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mCurrentValue = 0;
		mTargetValue = 0;
	}

	public void updateTextSize(float size) {
		for (int i = 0; i < textViews.size(); i++) {
			final TextView child = textViews.get(i);
			child.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
		}
		invalidate();
	}

	private void updateText() {
		String previous = (mCurrentValue > 1) ? String.valueOf((mCurrentValue - 1)) : nomore;
		String next = (mCurrentValue < mTargetValue) ? String.valueOf((mCurrentValue + 1)) : nomore;
		textViews.get(0).setText(previous);
		textViews.get(1).setText(String.valueOf(mCurrentValue));
		textViews.get(2).setText(next);
		invalidate();
	}

	// Add = true 增加 ，否则就是减小
	private void prepareText(Boolean add) {
		Log.i("prepareText",String.valueOf(add));
		if (add) {
			mCurrentValue++;
			String next = (mCurrentValue < mTargetValue) ? String.valueOf((mCurrentValue + 1)) : nomore;
			TextView tv = (TextView) views.get(2).getChildAt(0);
			tv.setText(next);
			Log.i("prepareText","Next Set to "+String.valueOf(next));
		} else {
			mCurrentValue--;
			String previous = (mCurrentValue > 1) ? String.valueOf((mCurrentValue - 1)) : nomore;
			TextView tv = (TextView) views.get(0).getChildAt(0);
			tv.setText(previous);
			Log.i("prepareText","previous Set to "+String.valueOf(previous));
		}
		if (!updateText.equals(null)){
			updateText.updateTextCallBack(String.valueOf(mCurrentValue), mCurrentValue);
		}
	}

	public void startAnimationText() {
		textHandler.sendEmptyMessageDelayed(GROWTEXTNUMBER, 3000);
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
		invisiableLeft = this.width / 3;
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
		Log.i("flipper","ondraw called in flipper");
		// 水平从左到右放置
		int count = views.size();
		for (int i = 0; i < count; i++) {
			View child = views.get(i);
			drawChild(canvas, child, getDrawingTime());
		}
	}

	private boolean isInCycle(float x, float y) {
		Point p = new Point((int) x, (int) y);
		Point cycleCenter = new Point((int) (getWidth() / 2 + getLeft()), (int) (getHeight() / 2 + getTop()));
		float Radius = getWidth() / 2;
		float distence = (float) Math.sqrt(Math.pow(Math.abs(p.x - cycleCenter.x), 2) + Math.pow(Math.abs(p.y - cycleCenter.y), 2));
		return distence <= Radius;
	}

	private void updateAlpha(View child) {
		float alpha = 0;
		float distence;
		if (Math.abs(child.getLeft()) > this.getMeasuredWidth()) {
			distence = Math.abs(child.getLeft()) - child.getMeasuredWidth();
		} else {
			distence = Math.abs(child.getLeft());
		}
		alpha = distence / invisiableLeft;
		if (alpha > 1)
			alpha = 1;
		alpha = 1 - alpha;
		// if (alpha < 0.3) alpha = 0.3f;
		ViewHelper.setAlpha(child, alpha);
	}

	
	private void postCheckForLongPress(){
		if (getLongPressRunnable()!=null){
			postDelayed(mLongPressRunnable,1500);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.i("onTouchEvent", "onTouchEvent called !");
		if (!lock) {
			final int action = ev.getAction();
			final float x = ev.getX();
			final float y = ev.getY();
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}

			mVelocityTracker.addMovement(ev);

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (isInCycle(x, y)) {
					super.onTouchEvent(ev);
					postCheckForLongPress();
				}
				if (touchState == TOUCH_STATE_REST) {
					if (!isInCycle(x, y)) {
						return false;
					}
					lastMotionX = x;
					startMotionX = x;
					touchState = TOUCH_STATE_MOVING;
					isMoved = false;
				}

				break;
			case MotionEvent.ACTION_MOVE:
				if (touchState == TOUCH_STATE_MOVING) {
					float offsetX = x - lastMotionX;

					if (isMoved) {
						lastMotionX = x;

						final int count = views.size();
						// from left to right
						for (int i = 0; i < count; i++) {
							final View child = views.get(i);
							if (child.getVisibility() != View.GONE) {
								final int childWidth = child.getMeasuredWidth();
								int childLeft = child.getLeft() + (int) offsetX;
								child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
								childLeft += childWidth;
								updateAlpha(child);
							}
						}
					} else if (Math.abs(offsetX) > TOUCH_SLOP) {
						// 移动超过阈值，则表示移动了
						isMoved = true;
						removeCallbacks(mLongPressRunnable);
					}
				}
				Log.i("onTouchEvent", "MOVING!");
				break;
			case MotionEvent.ACTION_UP:// 放开时
				// 释放了
				removeCallbacks(mLongPressRunnable);

				if (isMoved) {
					if (touchState == TOUCH_STATE_MOVING) {
						touchState = TOUCH_STATE_SLOWING;
						sign = 0;
						final VelocityTracker velocityTracker = mVelocityTracker;
						// 计算当前速度
						velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
						// x方向的速度
						int velocityX = (int) velocityTracker.getXVelocity();

						float deltaX = startMotionX - ev.getX();
						boolean canLeft = views.get(1).getLeft()>0;
						boolean canRight = views.get(1).getLeft()<0;						
						boolean canFlip = Math.abs(deltaX) > views.get(1).getWidth() / 4;
						Log.i("ActionUP", "velocityX:"+String.valueOf(velocityX));
						if (velocityX > SNAP_VELOCITY && mCurrentValue > 1 && canFlip && canLeft)// 足够的能力向左
						{
							sign = 1;
							Log.i("enough to move left", "true");
						} else if (velocityX < -SNAP_VELOCITY && mCurrentValue < mTargetValue && canFlip && canRight)// 足够的能力向右
						{
							sign = -1;
							Log.i("enough to move right", "right");
						} else {
							sign = 0;
							Log.i("can't  move", "");
						}
						moveToFitView(sign);
						if (mVelocityTracker != null) {
							mVelocityTracker.recycle();
							mVelocityTracker = null;
						}
					}
				} else {
					touchState = TOUCH_STATE_REST;
					return super.onTouchEvent(ev);
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
	int ovv = 80;

	private void moveView() {
		Log.i("moveView","offset:"+String.valueOf(offset));
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
				updateAlpha(child);
			}

			if (mAnimationHandler == null) {
				mAnimationHandler = new FlipAnimationHandler();
			}
			mAnimationHandler.sleep(1);
		} else {
			ovv = 80;
			touchState = TOUCH_STATE_REST;
			if (sign == 1) {
				prepareText(false);
			} else if (sign == -1) {
				prepareText(true);
			}
		}
	}

	class FlipAnimationHandler extends Handler {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			Flipper.this.moveView();
		}

		public void sleep(long millis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), millis);
		}
	}

	private boolean swapView(int sign) {
		Log.i("swapView", String.valueOf(sign));
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