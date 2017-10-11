package com.pavkoo.franklin.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.SettingActivity;
import com.pavkoo.franklin.common.FranklinApplication;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;

public class SettingSystemBackUpDialog extends ParentDialog {
	private AnimMessage amMessage;
	private TextView tvBackUP;
	private TextView tvRestore;
	private TextView tvCancel;
	private FranklinApplication app;
	private IDialogOKCallBack onOKCallBack;

	public IDialogOKCallBack getOnOKCallBack() {
		return onOKCallBack;
	}

	public void setOnOKCallBack(IDialogOKCallBack onOKCallBack) {
		this.onOKCallBack = onOKCallBack;
	}

	public SettingSystemBackUpDialog(Context context, int theme) {
		super(context, theme);
		app = (FranklinApplication) ((SettingActivity) context).getApp();
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_databackup, null);
		setContentView(dialogView);
		amMessage = (AnimMessage) dialogView.findViewById(R.id.amSettingsystemMebackupMessage);
		tvBackUP = (TextView) dialogView.findViewById(R.id.tvSettingSystemPopupBackUP);
		tvRestore = (TextView) dialogView.findViewById(R.id.tvSettingSystemPopupRestore);
		tvCancel = (TextView) dialogView.findViewById(R.id.tvSettingSystemPopupRestoreCancel);
		tvBackUP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				amMessage.showMessage(getContext().getString(R.string.exporting), AnimMessageType.Waitting);
				if (app.getMgr().ExportTOCSV()) {
					amMessage.showMessage(getContext().getString(R.string.exportSuccess), AnimMessageType.INFO);
				} else {
					amMessage.showMessage(getContext().getString(R.string.errorbackup), AnimMessageType.WARNING);
				}
			}
		});

		tvRestore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				amMessage.showMessage(getContext().getString(R.string.startRestore), AnimMessageType.Waitting);
				if (app.getMgr().ImportCSVTOSql()) {
					amMessage.showMessage(getContext().getString(R.string.restoreSuccess), AnimMessageType.INFO);
					if (onOKCallBack != null) {
						onOKCallBack.UpdateUI(null);
					}
				} else {
					amMessage.showMessage(getContext().getString(R.string.backupDataError), AnimMessageType.WARNING);
				}
			}
		});
		tvCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingSystemBackUpDialog.this.dismiss();
			}
		});
	}
}
