package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SettingWelcomeDialog extends ParentDialog {
	private AnimMessage amMessage;
	private TextView tvSettingWelcomePopupCancel;
	private TextView tvSettingWelcomePopupYes;
	private EditText txtSettingWelcomePopupContext;
	private EditMode editMode;

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}
	
	private boolean userChanged;

	public boolean isUserChanged() {
		return userChanged;
	}

	private String extraObject;

	public String getExtraObject() {
		return extraObject;
	}

	public void setExtraObject(String extraObject) {
		this.extraObject = extraObject;
	}

	public SettingWelcomeDialog(Context context, int theme) {
		super(context, theme);
		userChanged = false;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_welcome_popup, null);
		setContentView(dialogView);
		amMessage = (AnimMessage) dialogView.findViewById(R.id.amSettingWelcomePupMessage);
		tvSettingWelcomePopupYes = (TextView) dialogView.findViewById(R.id.tvSettingWelcomePopupYes);
		tvSettingWelcomePopupCancel = (TextView) dialogView.findViewById(R.id.tvSettingWelcomePopupCancel);
		txtSettingWelcomePopupContext = (EditText) dialogView.findViewById(R.id.txtSettingWelcomePopupContext);
		tvSettingWelcomePopupYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (valided()) {
					getDate();
					userChanged = true;
					SettingWelcomeDialog.this.dismiss();
				}
			}
		});
		tvSettingWelcomePopupCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingWelcomeDialog.this.dismiss();
			}
		});

		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				amMessage.reset();
				if (editMode == EditMode.Add) {
					extraObject = "";
				}
				initViewData();
			}
		});
	}

	public enum EditMode {
		Modify, Add
	}

	private void initViewData() {
		txtSettingWelcomePopupContext.setText(extraObject);
	}

	private void getDate() {
		extraObject = txtSettingWelcomePopupContext.getText().toString();
	}

	private boolean valided() {
		if (txtSettingWelcomePopupContext.getText().toString().equals("")) {
			amMessage.showMessage(getContext().getString(R.string.errorAddContent), AnimMessageType.ERROR);
			txtSettingWelcomePopupContext.requestFocus();
			return false;
		}
		return true;
	}
}
