package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class SettingSystemMeDialog extends ParentDialog {
	private AnimationDrawable ad;
	private ImageView ivbg;
	public SettingSystemMeDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_me, null);
		ivbg = (ImageView) dialogView.findViewById(R.id.ivSettingSystemMe);
		setContentView(dialogView);
		ad = (AnimationDrawable) ivbg.getDrawable();
		ad.start();
	}
}
