package com.pavkoo.franklin.controls;

import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BlemishReportTotalDialog extends ParentDialog {
	private TextView tvReportToatalClose;
	private TextView tvReportToatalShare;
	private TextView tvReportToatalTitle;
	private ListView lvTotal;
	private TotalAdapter totalAdapter;
	private List<Moral> morals;
	private SparseIntArray doneRate;
	View dialogView;

	public List<Moral> getMorals() {
		return morals;
	}

	public void iniTotalData(List<Moral> morals, int mainColor,SparseIntArray doneRate) {
		this.morals = morals;
		this.doneRate = doneRate;
		if (totalAdapter == null) {
			totalAdapter = new TotalAdapter(this.getContext(), morals,
					mainColor,doneRate);
			lvTotal.setAdapter(totalAdapter);
		} else {
			totalAdapter.setMorals(morals);
			totalAdapter.setDoneRate(this.doneRate);
		}
	}

	public void updateUIByMoral(int index) {
		int mainColor = Color.parseColor(CommonConst.colors[index
				% CommonConst.colors.length]);
		GradientDrawable gd = (GradientDrawable) tvReportToatalTitle
				.getBackground();
		gd.setColor(mainColor);
	}

	public BlemishReportTotalDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		dialogView = li.inflate(R.layout.cycle_history_report_popup_total, null);
		setContentView(dialogView);
		tvReportToatalClose = (TextView) dialogView.findViewById(R.id.tvReportToatalClose);
		tvReportToatalTitle= (TextView) dialogView.findViewById(R.id.tvReportToatalTitle);
		tvReportToatalShare = (TextView) dialogView.findViewById(R.id.tvReportToatalShare);
		lvTotal = (ListView) dialogView.findViewById(R.id.lvTotal);
		tvReportToatalClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BlemishReportTotalDialog.this.dismiss();
			}
		});
		tvReportToatalShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				UtilsClass.shareMsg(BlemishReportTotalDialog.this.getContext(),
						BlemishReportTotalDialog.this.getContext().getString(R.string.Mainshare),
						BlemishReportTotalDialog.this.getContext().getString(R.string.sharetotal),
						dialogView);
			}
		});
	}
}
