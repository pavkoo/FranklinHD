package com.pavkoo.franklin;

import java.util.ArrayList;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.ComfirmDialog;
import com.pavkoo.franklin.controls.SettingProjectItemDialog;
import com.pavkoo.franklin.controls.SettingProjectItemDialog.EditMode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingProjectItemFragment extends Fragment {
	private View self;
	private SettingActivity parent;
	private DragSortListView dslvMorals;
	private ImageView ivSettingProjectItemAdd;
	private ImageView ivSettingProjectItemAddBG;
	private MoralAdapter adapter;
	private List<Moral> morals;
	private Animation addExpanding;
	private SettingProjectItemDialog itemDialog;
	private AnimMessage amMessage;
	private ComfirmDialog comfirmDialog;
	private int deleteWhich;
	private DragSortListView.DropListener onDrop = new DropListener() {

		@Override
		public void drop(int from, int to) {
			Moral item = (Moral) adapter.getItem(from);
			Moral itemto = (Moral) adapter.getItem(to);
			if (itemto.isFinished() || itemto.getCurrentDayInCycle() != 0) {
				amMessage.showMessage(getActivity().getString(R.string.cantArrange));
				adapter.notifyDataSetChanged();
				return;
			}
			morals.remove(item);
			amMessage.showMessage(String.format(getActivity().getString(R.string.moveMoral), item.getTitle(), from + 1, to + 1));
			morals.add(to, item);
			updateDate();
			adapter.notifyDataSetChanged();
		}
	};

	private DragSortListView.RemoveListener onRemove = new RemoveListener() {
		@Override
		public void remove(int which) {
			Moral item = (Moral) adapter.getItem(which);
			if (item.isFinished() || item.getCurrentDayInCycle() != 0) {
				amMessage.showMessage(getActivity().getString(R.string.cantDelete));
				adapter.notifyDataSetChanged();
				return;
			}
			deleteWhich = which;
			comfirmDialog.setDialogTitle(getActivity().getString(R.string.strComfirmDelete)+item.getTitle());
			comfirmDialog.show();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dslvMorals = (DragSortListView) self.findViewById(R.id.dslvMorals);
		dslvMorals.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				itemDialog.setEditMode(EditMode.Modify);
				String title = String.format(getActivity().getString(R.string.modifyMoralitem), morals.get(position).getTitle());
				itemDialog.SetDialogTitle(title);
				itemDialog.setExtraObject(morals.get(position));
				itemDialog.show();
			}
		});
		ivSettingProjectItemAdd = (ImageView) self.findViewById(R.id.ivSettingProjectItemAdd);
		ivSettingProjectItemAddBG = (ImageView) self.findViewById(R.id.ivSettingProjectItemAddBG);
		amMessage = ((SettingActivity) getActivity()).getAmMessage();
		comfirmDialog = new ComfirmDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		addExpanding = AnimationUtils.loadAnimation(getActivity(), R.anim.setting_add_expand);
		ivSettingProjectItemAddBG.startAnimation(addExpanding);
		morals = ((SettingActivity) getActivity()).getApp().getMorals();
		parent = (SettingActivity) getActivity();
		if (parent.getApp().getAppCon().isFrist()) {
			parent.buildAllCycleDate();
		}
		adapter = new MoralAdapter(getActivity(), morals);
		dslvMorals.setAdapter(adapter);
		ivSettingProjectItemAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				itemDialog.setEditMode(EditMode.Add);
				String title = getActivity().getString(R.string.addNew) + getActivity().getString(R.string.moralItem);
				itemDialog.SetDialogTitle(title);
				Moral m = new Moral();
				if (morals.size() > 0) {
					m.setCycle(morals.get(0).getCycle());
				} else {
					m.setCycle(7);
				}
				itemDialog.setExtraObject(m);
				itemDialog.show();
			}
		});
		itemDialog = new SettingProjectItemDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		itemDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (itemDialog.isUserChanged()) {
					if (itemDialog.getEditMode() == EditMode.Add) {
						morals.add(itemDialog.getExtraObject());
						updateDate();
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
		dslvMorals.setDragEnabled(true);
		dslvMorals.setDropListener(onDrop);
		dslvMorals.setRemoveListener(onRemove);
		comfirmDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (comfirmDialog.isDialogResult()){
					amMessage.showMessage(String.format(getActivity().getString(R.string.noMoreNeed), deleteWhich + 1,
							((Moral) adapter.getItem(deleteWhich)).getTitle()));
					morals.remove(deleteWhich);
				}
				updateDate();
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void updateDate() {
		if (parent.getApp().getAppCon().isFrist()) {
			parent.buildAllCycleDate();
		} else {
			parent.buildAllCycleDateNew();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return self = inflater.inflate(R.layout.setting_projectitem, null);
	}

	public void ReStore() {
		morals = ((SettingActivity) getActivity()).getApp().getMorals();
		adapter.setMorals(morals);
	}

	private class MoralAdapter extends BaseAdapter {
		private Context context;
		private List<Moral> morals;

		public void setMorals(List<Moral> morals) {
			this.morals = morals;
			this.notifyDataSetChanged();
		}

		public MoralAdapter(Context context, List<Moral> morals) {
			this.context = context;
			if (morals == null) {
				this.morals = new ArrayList<Moral>();
			} else {
				this.morals = morals;
			}
		}

		@Override
		public int getCount() {
			return morals.size();
		}

		@Override
		public Object getItem(int position) {
			return morals.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.setting_projectitem_item, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.settingProjectItemTitle);
			TextView des = (TextView) convertView.findViewById(R.id.setttingProjectItemTitleDes);
			TextView motto = (TextView) convertView.findViewById(R.id.setttingProjectItemTitleMotto);
			TextView timePeriod = (TextView) convertView.findViewById(R.id.setttingProjectItemTime);
			TextView drag = (TextView) convertView.findViewById(R.id.drag_handle);
			LinearLayout llremoveid = (LinearLayout) convertView.findViewById(R.id.remove_handle);
			drag.setText(String.valueOf(position + 1));
			title.setText(morals.get(position).getTitle());
			des.setText(morals.get(position).getTitleDes());
			motto.setText(morals.get(position).getTitleMotto());
			if (morals.get(position).isFinished() || morals.get(position).getCurrentDayInCycle() != 0) {
				convertView.setBackgroundColor(Color.GRAY);
				drag.setVisibility(View.GONE);
				llremoveid.setEnabled(false);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
				convertView.findViewById(R.id.drag_handle).setVisibility(View.VISIBLE);
				llremoveid.setEnabled(true);
			}
			timePeriod.setText(UtilsClass.dateToString(morals.get(position).getStartDate()) + "---"
					+ UtilsClass.dateToString(morals.get(position).getEndDate()));
			return convertView;
		}
	}
}
