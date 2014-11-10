package com.pavkoo.franklin;

import java.util.List;

import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.controls.AnimMessage;
import com.pavkoo.franklin.controls.AnimMessage.AnimMessageType;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingCycleFragment extends Fragment {
	private View self;
	private  SeekBar sbCycle;
	private TextView tvSettingCycleText;
	private TextView tvSettingCycleBg;
	private AnimMessage amMessage;
	private List<Moral> morals;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		sbCycle = (SeekBar) self.findViewById(R.id.sbCycle);
		tvSettingCycleText = (TextView) self.findViewById(R.id.tvSettingCycleText);
		tvSettingCycleBg = (TextView) self.findViewById(R.id.tvSettingCycleBg);
		amMessage = ((SettingActivity) getActivity()).getAmMessage();
		morals = ((SettingActivity) getActivity()).getApp().getMorals();
		sbCycle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			
			/*
			 * 最小设置天数为7天
			 * 最大天数为14天
			 */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float saclesize = tvSettingCycleBg.getScaleX();
				saclesize = 0.5f +(float)progress/seekBar.getMax() / 0.5f;
				tvSettingCycleBg.setScaleX(saclesize);
				tvSettingCycleBg.setScaleY(saclesize);
				int value = progress + 7;
				tvSettingCycleText.setText(String.valueOf(value)+ getActivity().getString(R.string.day));
				if (value == 7){
					amMessage.showMessage(getActivity().getString(R.string.minCycleInfo));
				}else if (value==14){
					amMessage.showMessage(getActivity().getString(R.string.maxCycleWarning),AnimMessageType.WARNING);
				}else{
					amMessage.reset();
				}
				tvSettingCycleBg.invalidate();
				//TODO: 这里为什么会是空值呢
				if (morals==null) return;
				for(int i=0;i<morals.size();i++){
					morals.get(i).setCycle(value);
				}
			}
		});
		sbCycle.setProgress(0);
		Animation scale = AnimationUtils.loadAnimation(getActivity(), R.anim.setting_cycle_scale);
		tvSettingCycleBg.setAnimation(scale);
		scale.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		self = inflater.inflate(R.layout.setting_cycle, null);
		return self;
	}

}
