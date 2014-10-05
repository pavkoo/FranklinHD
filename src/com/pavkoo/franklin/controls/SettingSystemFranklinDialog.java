package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class SettingSystemFranklinDialog extends ParentDialog {
	public SettingSystemFranklinDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_franklin, null);
		setContentView(dialogView);
	}
}
