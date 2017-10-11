package com.pavkoo.franklin;

import com.pavkoo.franklin.common.FranklinApplication;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;

public class ParentActivity extends Activity {
	private FranklinApplication app;

	public FranklinApplication getApp() {
		return app;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (FranklinApplication) this.getApplication();
		initView();
		initViewEvent();
		initViewData();
	}

	protected void initView() {
	}
	
	protected void initViewEvent(){
	}

	protected void initViewData() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	

}
