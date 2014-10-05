package com.pavkoo.franklin.common;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class UtilsClass {
	public static String dateToString(Date date) {
		String s = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		s = sdf.format(date);
		return s;
	}

	public static Date stringToDate(String date) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static long dayCount(Date fromDay, Date toDay) {
		
		Calendar fromDayCal = Calendar.getInstance();
		fromDayCal.setTime(fromDay);
		fromDayCal.set(Calendar.HOUR_OF_DAY, 0);
		fromDayCal.set(Calendar.MINUTE, 0);
		fromDayCal.set(Calendar.SECOND, 0);
		fromDayCal.set(Calendar.MILLISECOND, 0);
		
		Calendar toDayCal = Calendar.getInstance();		
		toDayCal.setTime(toDay);
		toDayCal.set(Calendar.HOUR_OF_DAY, 0);		
		toDayCal.set(Calendar.MINUTE, 0);
		toDayCal.set(Calendar.SECOND, 0);
		toDayCal.set(Calendar.MILLISECOND, 0);
		long result = toDayCal.getTimeInMillis() - fromDayCal.getTimeInMillis();
		float resultfloat = (float)result / (24 * 60 * 60 * 1000);
		if (resultfloat < 0) {
			result =  (long) Math.floor(resultfloat);
		} else if(result > 0) {
			result = (long) Math.ceil(resultfloat);
		}
		return result;
	}
	
	public static Date subDate(Date date,int daysub){
		Calendar fromDayCal = Calendar.getInstance();
		fromDayCal.setTime(date);
		fromDayCal.add(Calendar.DATE, -daysub);
		return fromDayCal.getTime();
	}
	
	public static void reArrangeDate(List<Moral> morals){
		Date firstUse = new Date();
		Date begin = firstUse;
		Date end = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstUse);
		if (morals == null) {
			return;
		}
		for (int i = 0; i < morals.size(); i++) {
			Moral moral = morals.get(i);
			if (i == 0) {
				begin = calendar.getTime();
			} else {
				calendar.add(Calendar.DATE, 1);
				begin = calendar.getTime();
			}
			calendar.add(Calendar.DATE, moral.getCycle() - 1); // -1 means day
																// between
																// should sub 1
			end = calendar.getTime();
			moral.setStartDate(begin);
			moral.setEndDate(end);
		}
	}
}
