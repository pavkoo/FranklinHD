package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ComfirmDialog extends ParentDialog {
	private boolean dialogResult;

	public boolean isDialogResult() {
		return dialogResult;
	}

	private TextView tvSettingProjectItemDeleteTitle;
	private TextView tvSettingProjectItemDeleteCancel;
	private TextView tvSettingProjectItemDeleteYes;


	public void setDialogTitle(String dialogTitle) {
		tvSettingProjectItemDeleteTitle.setText(dialogTitle);
	}

	public ComfirmDialog(Context context, int theme) {
		super(context, theme);
		dialogResult = false;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_projectitem_popup_delete, null);
		setContentView(dialogView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		tvSettingProjectItemDeleteCancel = (TextView) findViewById(R.id.tvSettingProjectItemDeleteCancel);
		tvSettingProjectItemDeleteTitle = (TextView) findViewById(R.id.tvSettingProjectItemDeleteTitle);
		tvSettingProjectItemDeleteYes = (TextView) findViewById(R.id.tvSettingProjectItemDeleteYes);
		
		
		tvSettingProjectItemDeleteCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogResult = false;
				ComfirmDialog.this.dismiss();
			}
		});
		
		tvSettingProjectItemDeleteYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogResult = true;
				ComfirmDialog.this.dismiss();
			}
		});
		
		this.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				dialogResult = false;
			}
		});
	}

}
