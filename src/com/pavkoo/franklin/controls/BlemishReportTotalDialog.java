package com.pavkoo.franklin.controls;

import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Moral;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BlemishReportTotalDialog extends ParentDialog {
	private TextView tvReportToatalClose;
	private ListView lvTotal;
	private TotalAdapter totalAdapter;
	private List<Moral> morals;

	public List<Moral> getMorals() {
		return morals;
	}

	public void setMorals(List<Moral> morals) {
		this.morals = morals;
		if (totalAdapter == null) {
			totalAdapter = new TotalAdapter(this.getContext(), morals);
			lvTotal.setAdapter(totalAdapter);
		} else {
			totalAdapter.setMorals(morals);
		}
	}

	public BlemishReportTotalDialog(Context context, int theme) {
		super(context, theme);
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.cycle_history_report_popup_total, null);
		setContentView(dialogView);
		tvReportToatalClose = (TextView) dialogView.findViewById(R.id.tvReportToatalClose);
		lvTotal = (ListView) dialogView.findViewById(R.id.lvTotal);
		tvReportToatalClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BlemishReportTotalDialog.this.dismiss();
			}
		});
	}

}
