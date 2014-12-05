package com.pavkoo.franklin.controls;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.pavkoo.franklin.R;

@SuppressLint("NewApi")
public class ParentDialog extends Dialog {
	@SuppressWarnings("deprecation")
	public ParentDialog(Context context, int theme) {
		super(context, theme);
		getWindow().setWindowAnimations(R.style.DialogAnimationStyle);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.7f;
		lp.gravity = Gravity.CENTER;
		Display d = getWindow().getWindowManager().getDefaultDisplay();
		Point p = new Point();
		if (Build.VERSION.SDK_INT > 12) {
			d.getSize(p);
			lp.width = (int) (p.x * 0.9);
		} else {
			lp.width = (int) (d.getWidth() * 0.9);
		}
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		setCanceledOnTouchOutside(true);
	}
}
