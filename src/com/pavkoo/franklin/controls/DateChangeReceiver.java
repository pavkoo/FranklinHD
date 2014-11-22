package com.pavkoo.franklin.controls;

import java.util.Date;
import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.SplashActivity;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.SharePreferenceService;
import com.pavkoo.franklin.common.UtilsClass;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class DateChangeReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		SharePreferenceService mPreference = new SharePreferenceService(context);
		List<Moral> morals = mPreference.loadMorals();
		if (morals == null) {
			return;
		}
		if (morals.size() == 0) {
			return;
		}
		Moral todayMoral = null;
		Date now = new Date(System.currentTimeMillis());
		for (int i = 0; i < morals.size(); i++) {
			Moral m = morals.get(i);
			int enddaycount = (int) UtilsClass.dayCount(m.getEndDate(), now);
			if (enddaycount <= 0) {
				todayMoral = m;
				break;
			}
		}
		if (todayMoral == null) {
			return;
		}

		NotificationManager notifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int FranklinNotifyId = 9527;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, SplashActivity.class), 0);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(context.getString(R.string.doyoudotaday))
				.setContentTitle(
						context.getString(R.string.todaySubject)
								+ todayMoral.getTitle())
				.setContentText(
						context.getString(R.string.todo)
								+ todayMoral.getTitleDes())
				.setContentIntent(pIntent);
		Notification notify = mBuilder.build();
		notify.icon = R.drawable.ic_launcher;
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.tickerText = context.getString(R.string.app_name);
		notify.when = System.currentTimeMillis();
		notifyMgr.cancelAll();
		notifyMgr.notify(FranklinNotifyId, notify);
	}
}
