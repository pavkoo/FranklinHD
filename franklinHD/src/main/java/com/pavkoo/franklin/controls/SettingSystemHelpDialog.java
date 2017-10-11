package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class SettingSystemHelpDialog extends ParentDialog {
	private TextView tvSettingSystemHelplPopupOK;
	private TextView tvSettingSystemHelplPopupNext1;
	private TextView tvSettingSystemHelplPopupNext2;
	
	private TextView tvsettingSystemHelpContent1;
	private TextView tvsettingSystemHelpContent2;
	private TextView tvsettingSystemHelpContent3;
	
	private TextView tvsettingSystemHelpContentTitle1;
	private TextView tvsettingSystemHelpContentTitle2;
	private TextView tvsettingSystemHelpContentTitle3;
	
	private ImageView ivSettingSystemMe;
	private ScrollView svSettingSystemPopupHelp;
	private boolean canClose;
	
	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public SettingSystemHelpDialog(Context context, int theme) {
		super(context, theme);
		canClose = true;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_help, null);
		svSettingSystemPopupHelp = (ScrollView) dialogView.findViewById(R.id.svSettingSystemPopupHelp);
		
		tvSettingSystemHelplPopupOK = (TextView) dialogView.findViewById(R.id.tvSettingSystemHelplPopupOK);
		tvSettingSystemHelplPopupNext1 = (TextView) dialogView.findViewById(R.id.tvSettingSystemHelplPopupNext1);
		tvSettingSystemHelplPopupNext2 = (TextView) dialogView.findViewById(R.id.tvSettingSystemHelplPopupNext2);
		
		tvsettingSystemHelpContentTitle1 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContentTitle1);
		tvsettingSystemHelpContentTitle2 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContentTitle2);
		tvsettingSystemHelpContentTitle3 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContentTitle3);
		
		tvsettingSystemHelpContent1 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContent1);
		tvsettingSystemHelpContent2 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContent2);
		tvsettingSystemHelpContent3 = (TextView) dialogView.findViewById(R.id.tvsettingSystemHelpContent3);
		
		ivSettingSystemMe = (ImageView) dialogView.findViewById(R.id.ivSettingSystemMe);
		
		tvsettingSystemHelpContent1.setText(Html.fromHtml(getContext().getResources().getString(R.string.bookcontent)));
		tvsettingSystemHelpContent2.setText(Html.fromHtml(getContext().getResources().getString(R.string.bookcontent2)));
		tvsettingSystemHelpContent3.setText(Html.fromHtml(getContext().getResources().getString(R.string.bookcontent3)));
		setContentView(dialogView);
		tvSettingSystemHelplPopupNext1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvsettingSystemHelpContentTitle1.setVisibility(View.GONE);
				tvsettingSystemHelpContent1.setVisibility(View.GONE);
				tvSettingSystemHelplPopupNext1.setVisibility(View.GONE);
				
				tvsettingSystemHelpContentTitle2.setVisibility(View.VISIBLE);
				tvsettingSystemHelpContent2.setVisibility(View.VISIBLE);
				tvSettingSystemHelplPopupNext2.setVisibility(View.VISIBLE);
				svSettingSystemPopupHelp.scrollTo(0, 0);
			}
		});
		
		tvSettingSystemHelplPopupNext2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvsettingSystemHelpContentTitle2.setVisibility(View.GONE);
				tvsettingSystemHelpContent2.setVisibility(View.GONE);
				tvSettingSystemHelplPopupNext2.setVisibility(View.GONE);
				
				tvsettingSystemHelpContentTitle3.setVisibility(View.VISIBLE);
				tvsettingSystemHelpContent3.setVisibility(View.VISIBLE);
				tvSettingSystemHelplPopupOK.setVisibility(View.VISIBLE);
				ivSettingSystemMe.setVisibility(View.VISIBLE);
				svSettingSystemPopupHelp.scrollTo(0, 0);
				
			}
		});
		tvSettingSystemHelplPopupOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingSystemHelpDialog.this.dismiss();
			}
		});
		this.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				inishow();
			}
		});
	}
	
	private void inishow(){
		svSettingSystemPopupHelp.scrollTo(0, 0);
		tvsettingSystemHelpContentTitle1.setVisibility(View.VISIBLE);
		tvsettingSystemHelpContent1.setVisibility(View.VISIBLE);
		tvSettingSystemHelplPopupNext1.setVisibility(View.VISIBLE);
		tvsettingSystemHelpContentTitle2.setVisibility(View.GONE);
		tvsettingSystemHelpContent2.setVisibility(View.GONE);
		tvSettingSystemHelplPopupNext2.setVisibility(View.GONE);
		tvsettingSystemHelpContentTitle3.setVisibility(View.GONE);
		tvsettingSystemHelpContent3.setVisibility(View.GONE);
		tvSettingSystemHelplPopupOK.setVisibility(View.GONE);
		ivSettingSystemMe.setVisibility(View.GONE);
	}
}
