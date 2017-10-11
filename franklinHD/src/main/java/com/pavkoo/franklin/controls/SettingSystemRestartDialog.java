package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SettingSystemRestartDialog extends ParentDialog {
	private TextView tvSettingSystemPopupYes;
	private TextView tvSettingSystemPopupCancel;
	private TextView tvSettingSystemInfo;
	private TextView tvSettingSystemTitle;
	private IDialogOKCallBack onOKCallBack;

	private ViewState vs;

	public ViewState getVs() {
		return vs;
	}

	public void setVs(ViewState vs) {
		this.vs = vs;
	}

	public IDialogOKCallBack getOnOKCallBack() {
		return onOKCallBack;
	}

	public void setOnOKCallBack(IDialogOKCallBack onOKCallBack) {
		this.onOKCallBack = onOKCallBack;
	}

	public SettingSystemRestartDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_restart, null);
		setContentView(dialogView);
		tvSettingSystemPopupYes = (TextView) dialogView.findViewById(R.id.tvSettingSystemPopupYes);
		tvSettingSystemPopupCancel = (TextView) dialogView.findViewById(R.id.tvSettingSystemPopupCancel);
		tvSettingSystemInfo = (TextView) dialogView.findViewById(R.id.tvSettingSystemInfo);
		tvSettingSystemTitle = (TextView) dialogView.findViewById(R.id.tvSettingSystemTitle);

		tvSettingSystemPopupCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingSystemRestartDialog.this.dismiss();
			}
		});
		tvSettingSystemPopupYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onOKCallBack != null) {
					onOKCallBack.UpdateUI(null);
				}
			}
		});
		setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				if (vs == ViewState.SETTINSYSTEMRESTART) {
					tvSettingSystemInfo.setText(getContext().getResources().getString(R.string.deleteHistoryCommentInfo));
					tvSettingSystemTitle.setText(getContext().getResources().getString(R.string.deleteHistoryComment));
				} else {
					tvSettingSystemInfo.setText(getContext().getResources().getString(R.string.restoretinfo));
					tvSettingSystemTitle.setText(getContext().getResources().getString(R.string.settingRestore));
				}
			}
		});
	}

	public enum ViewState {
		SETTINSYSTEMRESTART, SETTINSYSTEMRESTORE
	};
}
