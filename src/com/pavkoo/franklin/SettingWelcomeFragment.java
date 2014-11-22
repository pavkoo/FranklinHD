package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.ComfirmDialog;
import com.pavkoo.franklin.controls.SettingWelcomeDialog;
import com.pavkoo.franklin.controls.SettingWelcomeDialog.EditMode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingWelcomeFragment extends Fragment {
	private View self;
	private DragSortListView dslvWelcome;
	private ImageView ivSettingWelcomeAdd;
	private ImageView ivSettingWelcomeAddBG;
	private MoralAdapter adapter;
	private List<String> welcomes;
	private Animation addExpanding;
	private SettingWelcomeDialog dialog;

	private AnimMessage amMessage;
	private ComfirmDialog comfirmDialog;
	private int deleteWhich;
	private int modifyWhich;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return self = inflater.inflate(R.layout.settting_welcome, null);
	}

	private DragSortListView.RemoveListener onRemove = new RemoveListener() {
		@Override
		public void remove(int which) {
			deleteWhich = which;
			comfirmDialog.setDialogTitle(getActivity().getString(
					R.string.strComfirmDelete)
					+ getActivity().getString(R.string.welcome));
			comfirmDialog.show();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		modifyWhich = -1;
		dslvWelcome = (DragSortListView) self.findViewById(R.id.dslvWelcome);
		ivSettingWelcomeAdd = (ImageView) self
				.findViewById(R.id.ivSettingWelcomeAdd);
		ivSettingWelcomeAddBG = (ImageView) self
				.findViewById(R.id.ivSettingWelcomeAddBG);
		welcomes = ((SettingActivity) getActivity()).getApp().getWelcomes();
		amMessage = ((SettingActivity) getActivity()).getAmMessage();
		comfirmDialog = new ComfirmDialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		adapter = new MoralAdapter(getActivity(), welcomes);
		dslvWelcome.setAdapter(adapter);
		ivSettingWelcomeAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.setEditMode(EditMode.Add);
				dialog.setExtraObject("");
				dialog.show();
			}
		});
		dslvWelcome.setRemoveListener(onRemove);
		dslvWelcome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialog.setEditMode(EditMode.Modify);
				dialog.setExtraObject(welcomes.get(position));
				modifyWhich = position;
				dialog.show();
			}
		});
		addExpanding = AnimationUtils.loadAnimation(getActivity(),
				R.anim.setting_add_expand);
		ivSettingWelcomeAddBG.startAnimation(addExpanding);
		dialog = new SettingWelcomeDialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialogi) {
				if (dialog.isUserChanged()) {
					if (dialog.getEditMode() == EditMode.Add) {
						welcomes.add(dialog.getExtraObject());
					}else{
						welcomes.set(modifyWhich, dialog.getExtraObject());
					}
					adapter.notifyDataSetChanged();
				}
			}
		});

		comfirmDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (comfirmDialog.isDialogResult()) {
					amMessage.showMessage(String.format(getActivity()
							.getString(R.string.noMoreNeed), deleteWhich + 1,
							""));
					welcomes.remove(deleteWhich);
					adapter.notifyDataSetChanged();
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private class MoralAdapter extends BaseAdapter {
		private Context context;
		private List<String> welcomes;

		public MoralAdapter(Context context, List<String> morals) {
			this.context = context;
			if (morals == null) {
				this.welcomes = new ArrayList<String>();
			} else {
				this.welcomes = morals;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return welcomes.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return welcomes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.settting_welcome_item, null);
			}
			TextView text = (TextView) convertView
					.findViewById(R.id.settingWelcomeItemContent);
			if (position != -1) {
				text.setText(String.valueOf(position + 1) + "."
						+ welcomes.get(position));
			}
			return convertView;
		}
	}
}
