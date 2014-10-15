package com.pavkoo.franklin.controls;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TotalAdapter extends BaseAdapter {
	private LinearLayout llCommentItemBg;
	private TextView txtCommentItemNumber;
	private TextView txtCommentItemText;

	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}
	
	private int mainColor;

	public void setMorals(List<Moral> morals) {
		if (this.morals == null) {
			this.morals = new ArrayList<Moral>();
		} else {
			this.morals.clear();
		}
		if (morals != null) {
			for (int i = 0; i < morals.size(); i++) {
				if (morals.get(i).isDoing() || morals.get(i).isFinished()) {
					this.morals.add(morals.get(i));
				}
			}
		}
		this.notifyDataSetChanged();
	}

	private Context context;

	public TotalAdapter(Context context, List<Moral> morals,int mainColor) {
		this.context = context;
		this.mainColor = mainColor; 
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.cycle_history_comments_item, null);
		}
		llCommentItemBg = (LinearLayout) convertView.findViewById(R.id.llCommentItemBg);
		llCommentItemBg.setBackgroundColor(mainColor);
		txtCommentItemNumber = (TextView) convertView.findViewById(R.id.txtCommentItemNumber);
		txtCommentItemText = (TextView) convertView.findViewById(R.id.txtCommentItemText);
		txtCommentItemNumber.setText(String.valueOf(morals.get(position).getTitle()));

		int total = morals.get(position).getTotalCheckCount();
		int checked = morals.get(position).getTotalCheckedSize();
		float doneRate = 0.0f;
		if (total != 0) {
			doneRate = (float)checked / total;
		}
		DecimalFormat df = new DecimalFormat("00.00%");
		txtCommentItemText.setText(df.format(doneRate)+" ("+String.valueOf(checked)+" / "+String.valueOf(total)+")");
		llCommentItemBg.setScaleX(doneRate);
		return convertView;
	}
}
