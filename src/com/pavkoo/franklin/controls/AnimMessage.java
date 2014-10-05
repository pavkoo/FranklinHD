package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnimMessage extends LinearLayout {
	private LinearLayout llMessageBg;
	private TextView tvMessage;
	private ValueAnimator inAnimation;
	private ValueAnimator colorAnimation;
	private boolean showing;
	private final int CLOSEMESSAGE = 0;
	private final int INIMESSAGE = 1;
	private final int HINTTIMEOUT = 2000;
	private final int INFOTIMEOUT = 15000;
	private final int WARNINGTIMEOUT = 15000;
	private final int ERRORTIMEOUT = 30000;

	private String delayMsg = "";
	private AnimMessageType delayAnimMessageType;

	@SuppressLint("HandlerLeak")
	private Handler myHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSEMESSAGE:
				Log.i("handle close message", "handle message");
				inAnimation.reverse();
				showing = false;
				break;
			case INIMESSAGE:
				showMessage(delayMsg, delayAnimMessageType);
				Log.i("send delayMsg message", "handle delayMsg message");
				break;
			default:
				break;
			}
		}
	};

	public AnimMessage(Context context) {
		super(context);
		iniView();
	}

	public AnimMessage(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView();
	}

	public AnimMessage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView();
	}

	private void iniView() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);

		li.inflate(R.layout.message, this, true);
		tvMessage = (TextView) findViewById(R.id.tvMeeesage);
		llMessageBg = (LinearLayout) findViewById(R.id.llMessageBg);
		tvMessage.setText("");
		inAnimation = ObjectAnimator.ofFloat(llMessageBg, "alpha", 0, 1);
		colorAnimation = ObjectAnimator.ofObject(new ArgbEvaluator());
		colorAnimation.setDuration(100);
		colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				llMessageBg.setBackgroundColor((Integer) animation.getAnimatedValue());
			}
		});
		llMessageBg.setBackgroundColor(getContext().getResources().getColor(R.color.white_app_bg_secondary));
		showing = false;
	}

	public void reset() {
		showing = false;
		myHandle.removeMessages(CLOSEMESSAGE);
		myHandle.removeMessages(INIMESSAGE);
		tvMessage.setText("");
		llMessageBg.setAlpha(0);
	}

	public void showMessage(String msg) {
		this.showMessage(msg, AnimMessageType.INFO);
	}

	public void showMessage(String msg, int delay) {
		showMessage(msg, AnimMessageType.INFO, delay);
	}

	public void showMessage(String msg, AnimMessageType type, int delay) {
		delayMsg = msg;
		delayAnimMessageType = type;
		myHandle.sendEmptyMessageDelayed(INIMESSAGE, delay);
	}

	public void showMessage(String msg, AnimMessageType type) {
		if (showing) {
			myHandle.removeMessages(CLOSEMESSAGE);
		}
		tvMessage.setText(msg);
		int startColor = ((ColorDrawable) llMessageBg.getBackground()).getColor();
		int endColor = 0;
		switch (type) {
		case INFO:
			endColor = getContext().getResources().getColor(R.color.white_app_bg_primary);
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, INFOTIMEOUT);
			break;
		case WARNING:
			endColor = getContext().getResources().getColor(R.color.white_app_warning);
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, WARNINGTIMEOUT);
			break;
		case ERROR:
			endColor = getContext().getResources().getColor(R.color.white_app_error);
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, ERRORTIMEOUT);
			break;
		case Hint:
			endColor = getContext().getResources().getColor(R.color.white_app_error);
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, HINTTIMEOUT);
			break;
		}
		if (!showing) {
			inAnimation.start();
		}
		if (startColor != endColor) {
			colorAnimation.setObjectValues(startColor, endColor);
			colorAnimation.start();
		}
		showing = true;
	}

	public enum AnimMessageType {
		INFO, WARNING, ERROR,Hint
	}
}
