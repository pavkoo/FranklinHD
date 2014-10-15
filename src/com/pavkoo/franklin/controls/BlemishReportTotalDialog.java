package com.pavkoo.franklin.controls;

import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BlemishReportTotalDialog extends ParentDialog {
	private TextView tvReportToatalClose;
	private TextView tvReportToatalTitle;
	private ListView lvTotal;
	private TotalAdapter totalAdapter;
	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals,int mainColor) {
		this.morals = morals;
		if (totalAdapter == null) {
			totalAdapter = new TotalAdapter(this.getContext(), morals,mainColor);
			lvTotal.setAdapter(totalAdapter);
		} else {
			totalAdapter.setMorals(morals);
		}
	}
	
	public void updateUIByMoral(int index){
		int mainColor =Color.parseColor(CommonConst.colors[index % CommonConst.colors.length]);
		GradientDrawable gd = (GradientDrawable) tvReportToatalTitle.getBackground();
		gd.setColor(mainColor);
	}


	public BlemishReportTotalDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.cycle_history_report_popup_total, null);
		setContentView(dialogView);
		tvReportToatalClose = (TextView) dialogView.findViewById(R.id.tvReportToatalClose);
		tvReportToatalTitle= (TextView) dialogView.findViewById(R.id.tvReportToatalTitle);
		lvTotal = (ListView) dialogView.findViewById(R.id.lvTotal);
		tvReportToatalClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BlemishReportTotalDialog.this.dismiss();
			}
		});
	}

}
