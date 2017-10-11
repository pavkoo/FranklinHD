package com.pavkoo.franklin.common;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.SplashActivity;

public class FranklinAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);
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
		rv.setTextViewText(R.id.tvWidgetTitle, todayMoral.getTitle());
		rv.setTextViewText(R.id.tvWidgetTitleDescrible, todayMoral.getTitleDes());
		rv.setTextViewText(R.id.tvWidgetTitleMotto, todayMoral.getTitleMotto());
		Intent intent = new Intent(context, SplashActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		rv.setOnClickPendingIntent(R.id.franklinWidget, pIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
