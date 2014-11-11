package com.pavkoo.franklin.controls;

import com.pavkoo.franklin.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplashFragment extends Fragment {
	private String value;

	public static Fragment init(String value) {
		SplashFragment sf = new SplashFragment();
		Bundle args = new Bundle();
		args.putString("value", value);
		sf.setArguments(args);
		return sf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			value = args.getString("value");
		} else {
			value = this.getActivity().getString(R.string.welcome1);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater
				.inflate(R.layout.activity_splash_item, null);
		TextView tvSplash = (TextView) layout.findViewById(R.id.tvSplash);
		tvSplash.setText(value);
		return layout;
	}

}
