package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingSystemMeDialog extends ParentDialog {
	private AnimationDrawable ad;
	private ImageView ivbg;
	private TextView settingmeCheckupdate;
	private TextView tvcopyright;
	private AnimMessage amMessage;
	
	public SettingSystemMeDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.settting_system_popup_me, null);
		ivbg = (ImageView) dialogView.findViewById(R.id.ivSettingSystemMe);
		settingmeCheckupdate = (TextView) dialogView.findViewById(R.id.settingmeCheckupdate);
		tvcopyright = (TextView) dialogView.findViewById(R.id.tvcopyright);
		amMessage = (AnimMessage) dialogView.findViewById(R.id.amSettingsystemMeMessage);
		setContentView(dialogView);		
		ad = (AnimationDrawable) ivbg.getDrawable();
		ad.start();
		
		tvcopyright.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cnblogs.com/pavkoo/p/4102992.html"));
				try {
					SettingSystemMeDialog.this.getContext().startActivity(browserIntent);
				} catch (ActivityNotFoundException e) {
					amMessage.showMessage(SettingSystemMeDialog.this.getContext().getString(R.string.cantFindBrowse), AnimMessageType.ERROR);
				}
			}
		});
		settingmeCheckupdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        switch (updateStatus) {
				        case UpdateStatus.Yes: // has update
				            UmengUpdateAgent.showUpdateDialog(SettingSystemMeDialog.this.getContext(), updateInfo);
				            break;
				        case UpdateStatus.No: // has no update
				        	amMessage.showMessage("已经是最新版本了！");
				            break;
				        case UpdateStatus.NoneWifi: // none wifi
				        	amMessage.showMessage("没有wifi连接， 只在wifi下更新哦",AnimMessageType.ERROR);
				            break;
				        case UpdateStatus.Timeout: // time out
				        	amMessage.showMessage("哎呀，网络超时了",AnimMessageType.ERROR);
				            break;
				        }
				    }
				});
				UmengUpdateAgent.forceUpdate(SettingSystemMeDialog.this.getContext());
				amMessage.showMessage("正在检查更新信息...",AnimMessageType.Waitting);
			}
		});
	}
}
