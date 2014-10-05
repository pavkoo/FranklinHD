package com.pavkoo.franklin.controls;

import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BlemishReportTrendDialog extends ParentDialog {
	private TextView tvReportTrendClose;
	private ListView lvTrend;
	private TrendAdapter trandAdapter;
	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals) {
		this.morals = morals;
		if (trandAdapter == null) {
			trandAdapter = new TrendAdapter(this.getContext(), morals);
			lvTrend.setAdapter(trandAdapter);
		} else {
			trandAdapter.setMorals(morals);
		}
	}

	public BlemishReportTrendDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.cycle_history_report_popup_trend, null);
		setContentView(dialogView);
		tvReportTrendClose = (TextView) dialogView.findViewById(R.id.tvReportTrendClose);
		lvTrend = (ListView) dialogView.findViewById(R.id.lvTrend);
		lvTrend.setDivider(null);
		tvReportTrendClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BlemishReportTrendDialog.this.dismiss();
			}
		});
	}
}
