package com.pavkoo.franklin.common;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.pavkoo.franklin.R;

public class FranklinAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals("com.pavkoo.franklin.click")) {
			Toast.makeText(context, "点击了widget日历", 1).show();
		}
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);

		Intent intent = new Intent("com.pavkoo.franklin.click");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		rv.setOnClickPendingIntent(R.id.franklinWidget, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
