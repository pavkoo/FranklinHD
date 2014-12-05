package com.pavkoo.franklin.controls;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.SplashActivity;
import com.pavkoo.franklin.common.DBManager;
import com.pavkoo.franklin.common.Moral;
import com.pavkoo.franklin.common.UtilsClass;

public class DateChangeReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		DBManager db = new DBManager(context);
		List<Moral> morals = db.loadMorals();
		int id = db.getCurrentMoralId();
		db.close();
		if (morals == null) {
			return;
		}
		if (morals.size() == 0) {
			return;
		}
		id = UtilsClass.getIndexMorals(morals, id);
		if (id < 0 || id > morals.size()) {
			return;
		}

		Moral todayMoral = morals.get(id);

		if (todayMoral == null) {
			return;
		}

		NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int FranklinNotifyId = 9527;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), 0);
		mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker(context.getString(R.string.doyoudotaday))
				.setContentTitle(context.getString(R.string.todaySubject) + todayMoral.getTitle())
				.setContentText(context.getString(R.string.todo) + todayMoral.getTitleDes()).setContentIntent(pIntent);
		Notification notify = mBuilder.build();
		notify.icon = R.drawable.ic_launcher;
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.tickerText = context.getString(R.string.app_name);
		notify.when = System.currentTimeMillis();
		notifyMgr.cancelAll();
		notifyMgr.notify(FranklinNotifyId, notify);
	}
}
