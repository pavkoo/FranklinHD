package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrendAdapter extends BaseAdapter {
		
	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals) {
		if (this.morals == null) {
			this.morals = new ArrayList<Moral>();
		} else {
			this.morals.clear();
		}
		if (morals != null) {
			for (int i = 0; i < morals.size(); i++) {
				int cycleSize = morals.get(i).getStateList().size() / morals.get(i).getCycle();
				if (cycleSize >= 1) {
					this.morals.add(morals.get(i));
				}else{
					break;
				}
			}
		}
		this.notifyDataSetChanged();
	}

	private Context context;

	public TrendAdapter(Context context, List<Moral> morals) {
		this.context = context;
		this.setMorals(morals);
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
//		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.cycle_history_report_popup_trend_item, null);
//		}
		TextView cycleHistoryReportTrendTitle = (TextView) convertView.findViewById(R.id.cycleHistoryReportTrendTitle);
		int color = Color.parseColor(CommonConst.colors[position % CommonConst.colors.length]);
		String title = morals.get(position).getTitle();
		if (UtilsClass.isEng()){
			title = title.substring(0, 3)+"."; 
		}
		cycleHistoryReportTrendTitle.setText(title);
		GradientDrawable gd = (GradientDrawable) cycleHistoryReportTrendTitle.getBackground();
		gd.setColor(color);
		TrendReport trend = (TrendReport) convertView.findViewById(R.id.cycleHistoryReportTrendReport);
		trend.setMoral(morals.get(position),position);
		return convertView;
	}
}
