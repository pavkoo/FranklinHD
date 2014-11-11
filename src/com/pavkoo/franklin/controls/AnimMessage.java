package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AnimMessage extends LinearLayout {
	private LinearLayout llMessageBg;
	private TextView tvMessage;
	private Object inAnimation;
	private boolean showing;
	private final int CLOSEMESSAGE = 0;
	private final int INIMESSAGE = 1;
	private final int HINTTIMEOUT = 4000;
	private final int INFOTIMEOUT = 15000;
	private final int WARNINGTIMEOUT = 15000;
	private final int ERRORTIMEOUT = 30000;
	private final int WAITTINGTIMEOUT = 180*1000;

	private String delayMsg = "";
	private AnimMessageType delayAnimMessageType;

	@SuppressLint("HandlerLeak")
	private Handler myHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSEMESSAGE:
				if (Build.VERSION.SDK_INT >= 11) {
					((ValueAnimator) inAnimation).reverse();
				} else {
					llMessageBg.setAlpha(0);
				}
				showing = false;
				break;
			case INIMESSAGE:
				showMessage(delayMsg, delayAnimMessageType);
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

	@SuppressLint("NewApi")
	public AnimMessage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView();
	}

	private void iniView() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);

		li.inflate(R.layout.message, this, true);
		tvMessage = (TextView) findViewById(R.id.tvMeeesage);
		llMessageBg = (LinearLayout) findViewById(R.id.llMessageBg);
		tvMessage.setText("");
		if (Build.VERSION.SDK_INT >= 11) {
			inAnimation = ObjectAnimator.ofFloat(llMessageBg, "alpha", 0, 1);
		} else {
			inAnimation = null;
		}
		llMessageBg.setBackgroundColor(getContext().getResources().getColor(
				R.color.white_app_bg_secondary));
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
		int endColor = 0;
		switch (type) {
		case INFO:
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, INFOTIMEOUT);
			endColor = getContext().getResources().getColor(R.color.white_app_bg_primary);
			break;
		case WARNING:
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, WARNINGTIMEOUT);
			endColor = getContext().getResources().getColor(R.color.white_app_warning);
			break;
		case ERROR:
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, ERRORTIMEOUT);
			endColor = getContext().getResources().getColor(R.color.white_app_error);
			break;
		case Hint:
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, HINTTIMEOUT);
			endColor = getContext().getResources().getColor(R.color.white_app_error);
			break;
		case Waitting:
			myHandle.sendEmptyMessageDelayed(CLOSEMESSAGE, WAITTINGTIMEOUT);
			endColor = getContext().getResources().getColor(R.color.white_app_error);
			break;
		}
		llMessageBg.setBackgroundColor(endColor);
		if (!showing) {
			if (Build.VERSION.SDK_INT >= 11) {
				((ValueAnimator) inAnimation).start();
			} else {
				llMessageBg.setAlpha(1);
			}
		}
		showing = true;
	}

	public enum AnimMessageType {
		INFO, WARNING, ERROR, Hint,Waitting
	}
}
