package com.pavkoo.franklin;

import java.util.List;

import com.pavkoo.franklin.common.Comment;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.IDialogOKCallBack;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;
import com.pavkoo.franklin.controls.SettingSystemFranklinDialog;
import com.pavkoo.franklin.controls.SettingSystemHelpDialog;
import com.pavkoo.franklin.controls.SettingSystemMeDialog;
import com.pavkoo.franklin.controls.SettingSystemRestartDialog;
import com.pavkoo.franklin.controls.SettingSystemRestartDialog.ViewState;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingSystemFragment extends Fragment {
	private View self;
	private int[] heads = { R.drawable.moralslist, R.drawable.motto, R.drawable.delete, R.drawable.restart,R.drawable.sina, 
			R.drawable.tencent, R.drawable.rate, R.drawable.head,R.drawable.help2 , R.drawable.info };
	private int[] headStrs = { R.string.moralItem, R.string.welcome, R.string.settingRestore, R.string.deleteHistoryComment,R.string.sina,
			R.string.share, R.string.rate, R.string.aboutFranklin, R.string.helpBook,R.string.aboutApp };
	private SettingSystemAdapter adapter;
	private ListView lvSettingSystem;
	private AnimMessage amMessage;
	private SettingSystemRestartDialog restartDialog;
	private SettingSystemRestartDialog restoreDialog;
	private SettingSystemFranklinDialog franklinDialog;
	private SettingSystemMeDialog meDialog;
	private SettingSystemHelpDialog helpDialog;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lvSettingSystem = (ListView) self.findViewById(R.id.lvSettingSystem);
		adapter = new SettingSystemAdapter(heads, headStrs, getActivity());
		lvSettingSystem.setAdapter(adapter);
		amMessage = ((SettingActivity) getActivity()).getAmMessage();
		franklinDialog = new SettingSystemFranklinDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		meDialog = new SettingSystemMeDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		helpDialog = new SettingSystemHelpDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

		restoreDialog = new SettingSystemRestartDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		restoreDialog.setVs(ViewState.SETTINSYSTEMRESTORE);
		restoreDialog.setOnOKCallBack(new IDialogOKCallBack() {

			@Override
			public void UpdateUI(Object object) {
				((SettingActivity) getActivity()).restore();
				((SettingActivity) getActivity()).restoreDefault();
				restoreDialog.dismiss();
				Intent setIntent = new Intent(SettingSystemFragment.this.getActivity(), SettingActivity.class);
				setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
				SettingSystemFragment.this.getActivity().startActivity(setIntent);
				SettingSystemFragment.this.getActivity().finish();
			}
		});

		restartDialog = new SettingSystemRestartDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		restartDialog.setVs(ViewState.SETTINSYSTEMRESTART);
		restartDialog.setOnOKCallBack(new IDialogOKCallBack() {

			@Override
			public void UpdateUI(Object object) {
				List<Comment> comments = ((SettingActivity) getActivity()).getApp().getComments();
				comments.clear();
				((SettingActivity) getActivity()).getApp().saveComments(comments);
				List<Moral> morals = ((SettingActivity) getActivity()).getApp().getMorals();
				for (int i = 0; i < morals.size(); i++) {
					morals.get(i).clearHistoryComment();
				}
				((SettingActivity) getActivity()).getApp().saveMorals(morals);
				restartDialog.dismiss();
				amMessage.showMessage(getActivity().getString(R.string.deleteHistoryCommentSuccess));
			}
		});
		lvSettingSystem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int selected = headStrs[position];
				switch (selected) {
				case R.string.moralItem:
					Intent setIntent = new Intent(SettingSystemFragment.this.getActivity(), SettingActivity.class);
					setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
					getActivity().startActivity(setIntent);
					getActivity().finish();
					break;
				case R.string.welcome:
					Intent setIntent2 = new Intent(SettingSystemFragment.this.getActivity(), SettingActivity.class);
					setIntent2.putExtra("STARTMODE", R.id.rbSettingWelcome);
					getActivity().startActivity(setIntent2);
					getActivity().finish();
					break;
				case R.string.deleteHistoryComment:
					restartDialog.show();
					break;
				case R.string.settingRestore:
					restoreDialog.show();
					break;
				case R.string.language:
					break;
				case R.string.sina:
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.com/pavkoo"));
					try {
						startActivity(browserIntent);
					} catch (ActivityNotFoundException e) {
						amMessage.showMessage(getActivity().getString(R.string.cantFindBrowse), AnimMessageType.ERROR);
					}
					break;
				case R.string.share:
					Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://t.qq.com/sun_de"));
					try {
						startActivity(browserIntent2);
					} catch (ActivityNotFoundException e) {
						amMessage.showMessage(getActivity().getString(R.string.cantFindBrowse), AnimMessageType.ERROR);
					}
					break;
				case R.string.rate:
					Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						amMessage.showMessage(getActivity().getString(R.string.cantFindMarket), AnimMessageType.ERROR);
					}
					break;
				case R.string.aboutFranklin:
					franklinDialog.show();
					break;
				case R.string.aboutApp:
					meDialog.show();
					break;
				case R.string.helpBook:
					helpDialog.show();
					break;
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		self = inflater.inflate(R.layout.settting_system, null);
		return self;
	}

	private class SettingSystemAdapter extends BaseAdapter {
		private Context context;
		private int[] imgRes;
		private int[] strings;

		public SettingSystemAdapter(int[] imageRes, int[] strings, Context context) {
			this.imgRes = imageRes;
			this.strings = strings;
			this.context = context;
		}

		@Override
		public int getCount() {
			return strings.length;
		} 

		@Override
		public Object getItem(int position) {
			return strings[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.settting_system_item, null);
			}
			TextView tvSettingItem = (TextView) convertView.findViewById(R.id.tvSettingSystemitem);
			if (Build.VERSION.SDK_INT>=17){
				tvSettingItem.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(imgRes[position]), null, null,null);					
			}else{
				tvSettingItem.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(imgRes[position]), null, null, null);
			}
			tvSettingItem.setText(context.getResources().getString(strings[position]));
			return convertView;
		}
	}
}
