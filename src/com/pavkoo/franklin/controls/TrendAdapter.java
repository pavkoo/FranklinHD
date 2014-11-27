package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.SignRecords;
import com.pavkoo.franklin.common.UtilsClass;

public class TrendAdapter extends BaseAdapter {

	private List<Moral> morals;
	private List<SignRecords> signlist;
	private SparseArray<List<Integer>> weeklyCount;
	private int cycle;
	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals) {
		this.morals = morals;
	}

	private Context context;

	public TrendAdapter(Context context, List<Moral> morals, List<SignRecords> signlist) {
		this.context = context;
		this.setMorals(morals);
		this.setSignlist(signlist);
	}

	@Override
	public int getCount() {
		return weeklyCount.size();
	}

	@Override
	public Object getItem(int position) {
		return weeklyCount.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (convertView == null) {
		convertView = LayoutInflater.from(context).inflate(R.layout.cycle_history_report_popup_trend_item, null);
		// }
		TextView cycleHistoryReportTrendTitle = (TextView) convertView.findViewById(R.id.cycleHistoryReportTrendTitle);
		int color = Color.parseColor(CommonConst.colors[position % CommonConst.colors.length]);
		String title = morals.get(position).getTitle();
		if (UtilsClass.isEng()) {
			title = UtilsClass.shortString(title);
		}
		cycleHistoryReportTrendTitle.setText(title);
		GradientDrawable gd = (GradientDrawable) cycleHistoryReportTrendTitle.getBackground();
		gd.setColor(color);
		TrendReport trend = (TrendReport) convertView.findViewById(R.id.cycleHistoryReportTrendReport);
		List<Double> points = new ArrayList<Double>();
		List<Integer> list = weeklyCount.get(position);
		for (int i = 0; i < list.size(); i++) {
			points.add((double) (list.get(i)) / cycle);
		};
		trend.setMoralandRate(morals.get(position), position, points);
		return convertView;
	}
	public List<SignRecords> getSignlist() {
		return signlist;
	}

	public void setSignlist(List<SignRecords> signlist) {
		if (weeklyCount == null) {
			weeklyCount = new SparseArray<List<Integer>>();
		} else {
			weeklyCount.clear();
		}
		this.signlist = signlist;
		cycle = this.getMorals().get(0).getCycle();
		for (int i = 0; i < signlist.size(); i++) {
			SignRecords sr = signlist.get(i);
			int id = sr.getMoarlIndex();
			int index = UtilsClass.getIndexMorals(morals, id);
			Moral m = morals.get(index);
			Date start = m.getStartDate();
			int daycount = (int) UtilsClass.dayCount(start, sr.getInputDate());
			int weekindex = daycount / cycle;
			List<Integer> weekList = weeklyCount.get(m.getId());
			if (weekList == null) {
				weekList = new ArrayList<Integer>();
				weeklyCount.put(m.getId(), weekList);
			}
			if (weekList.get(weekindex) == -1) {
				for (int j = 0; j < weekindex; j++) {
					weekList.add(0);
				}
			}
			int weekPoint = weekList.get(weekindex);
			weekPoint += 1;
			weekList.set(weekindex, weekPoint);
		}
		this.notifyDataSetChanged();
	}
}
