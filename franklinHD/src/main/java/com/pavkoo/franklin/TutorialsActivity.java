package com.pavkoo.franklin;

import com.pavkoo.franklin.controls.SettingSystemHelpDialog;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialsActivity extends ParentActivity {
	private LinearLayout llChart;
	private TextView tvChart;
	private TextView tvChartShowBook;
	private TextView tvChartGo;
//	private ImageView ivTutoriHead;
	private SettingSystemHelpDialog helpDialog;
	private TextView tvChartBye;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_tutorials);
		tvChart = (TextView) findViewById(R.id.tvChart);
		llChart = (LinearLayout) findViewById(R.id.llChart);
		tvChartShowBook = (TextView) findViewById(R.id.tvChartShowBook);
		tvChartGo = (TextView) findViewById(R.id.tvChartGo);
//		ivTutoriHead= (ImageView) findViewById(R.id.ivTutoriHead);
		tvChartBye = (TextView) findViewById(R.id.tvChartBye);
		tvChart.setTag(0);
		helpDialog = new SettingSystemHelpDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	protected void initViewEvent() {
		super.initViewEvent();
		tvChart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int tag = (Integer) tvChart.getTag();
				switch(tag){
				case 0:
					tvChart.setText(R.string.tutorials3);
					break;
				case 1:
					tvChart.setText(R.string.tutorials4);
					break;
				case 2:
					tvChart.setVisibility(View.GONE);
					llChart.setVisibility(View.VISIBLE);
					break;
				}
				tag++;
				tvChart.setTag(tag);
			}
		});
		tvChartGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(TutorialsActivity.this, SettingActivity.class);
				setIntent.putExtra("STARTMODE", R.id.rbSettingProjectItem);
				startActivity(setIntent);
				finish();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  
			}
		});
		tvChartShowBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				helpDialog.show();
			}
		});
		tvChartBye.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});
	}
}
