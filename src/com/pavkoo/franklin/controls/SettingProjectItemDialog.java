package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class SettingProjectItemDialog extends ParentDialog {
	private TextView tvSettingProjectItemPopupCancel;
	private TextView tvSettingProjectItemPopupYes;
	private AutoCompleteTextView txtSettingProjectItemPopupTitle;
	private AutoCompleteTextView txtSettingProjectItemPopupTitleDes;
	private AutoCompleteTextView txtSettingProjectItemPopupTitleMotto;
	private AnimMessage amMessage;
	private TextView tvSettingPopupTitle;
	public void SetDialogTitle(String title){
		tvSettingPopupTitle.setText(title);
	}
	
	private EditMode editMode;

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}

	private Moral extraObject;

	public Moral getExtraObject() {
		return extraObject;
	}

	private boolean userChanged;

	public boolean isUserChanged() {
		return userChanged;
	}

	public void setExtraObject(Moral extraObject) {
		this.extraObject = extraObject;
	}

	public SettingProjectItemDialog(Context context, int theme) {
		super(context, theme);
		editMode = EditMode.Modify;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.setting_projectitem_popup, null);
		setContentView(dialogView);

		tvSettingProjectItemPopupYes = (TextView) dialogView.findViewById(R.id.tvSettingProjectItemPopupYes);
		tvSettingProjectItemPopupCancel = (TextView) dialogView.findViewById(R.id.tvSettingProjectItemPopupCancel);
		txtSettingProjectItemPopupTitle = (AutoCompleteTextView) dialogView.findViewById(R.id.txtSettingProjectItemPopupTitle);
		txtSettingProjectItemPopupTitleDes = (AutoCompleteTextView) dialogView.findViewById(R.id.txtSettingProjectItemPopupTitleDes);
		txtSettingProjectItemPopupTitleMotto = (AutoCompleteTextView) dialogView.findViewById(R.id.txtSettingProjectItemPopupTitleMotto);
		tvSettingPopupTitle = (TextView) dialogView.findViewById(R.id.tvSettingPopupTitle);
		amMessage = (AnimMessage) dialogView.findViewById(R.id.amSettingProjectItemPupMessage);
		tvSettingProjectItemPopupYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (valided()) {
					getDate();
					userChanged = true;
					SettingProjectItemDialog.this.dismiss();
				}
			}
		});
		tvSettingProjectItemPopupCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingProjectItemDialog.this.dismiss();
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
				initViewData();
			}
		});
	}

	public enum EditMode {
		Modify, Add
	}

	private void initViewData() {
		userChanged = false;
		txtSettingProjectItemPopupTitle.setText(extraObject.getTitle());
		txtSettingProjectItemPopupTitleDes.setText(extraObject.getTitleDes());
		txtSettingProjectItemPopupTitleMotto.setText(extraObject.getTitleMotto());
	}

	private void getDate() {
		extraObject.setTitle(txtSettingProjectItemPopupTitle.getText().toString());
		extraObject.setTitleDes(txtSettingProjectItemPopupTitleDes.getText().toString());
		extraObject.setTitleMotto(txtSettingProjectItemPopupTitleMotto.getText().toString());
	}

	private boolean valided() {
		if (txtSettingProjectItemPopupTitle.getText().toString().equals("")) {
			amMessage.showMessage(getContext().getString(R.string.errorAddTitle), AnimMessageType.ERROR);
			txtSettingProjectItemPopupTitle.requestFocus();
			return false;
		}

		if (txtSettingProjectItemPopupTitle.getText().toString().length() > 8) {
			amMessage.showMessage(getContext().getString(R.string.errorTooMuchCh));
			txtSettingProjectItemPopupTitle.requestFocus();
			return false;
		}

		if (txtSettingProjectItemPopupTitleDes.getText().toString().equals("")) {
			amMessage.showMessage(getContext().getString(R.string.errorAddDes), AnimMessageType.ERROR);
			txtSettingProjectItemPopupTitleDes.requestFocus();
			return false;
		}

		if (txtSettingProjectItemPopupTitleDes.getLineCount() > 4) {
			amMessage.showMessage(getContext().getString(R.string.errorTooMuchLines));
			txtSettingProjectItemPopupTitleDes.requestFocus();
			return false;
		}

		if (txtSettingProjectItemPopupTitleMotto.getText().toString().equals("")) {
			amMessage.showMessage(getContext().getString(R.string.errorMotto), AnimMessageType.ERROR);
			txtSettingProjectItemPopupTitleMotto.requestFocus();
			return false;
		}

		return true;
	}

}
