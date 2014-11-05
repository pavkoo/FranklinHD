package com.pavkoo.franklin.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
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
		float resultfloat = (float) result / (24 * 60 * 60 * 1000);
		if (resultfloat < 0) {
			result = (long) Math.floor(resultfloat);
		} else if (result > 0) {
			result = (long) Math.ceil(resultfloat);
		}
		return result;
	}

	public static Date subDate(Date date, int daysub) {
		Calendar fromDayCal = Calendar.getInstance();
		fromDayCal.setTime(date);
		fromDayCal.add(Calendar.DATE, -daysub);
		return fromDayCal.getTime();
	}

	public static void reArrangeDate(List<Moral> morals) {
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

	/**
	 * 分享功能
	 * 
	 * @param context
	 *            上下文
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public static void shareMsg(Context context, String activityTitle,
			String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}
	
	
	public static void shareMsg(Context context, String activityTitle,String msgText, View shareView) {
		if (shareView==null){
			shareMsg(context,activityTitle,activityTitle,msgText,"");
		}else{
			String path = GetandSaveCurrentImage(shareView);
			shareMsg(context,activityTitle,activityTitle,msgText,path);
		}
	}

	private static String GetandSaveCurrentImage(View drawingView) {
		String filepath = "";
		if (drawingView==null) return filepath;
		drawingView.setDrawingCacheEnabled(true);
		Bitmap Bmp = Bitmap.createBitmap(drawingView.getDrawingCache());
		drawingView.setDrawingCacheEnabled(false);
		String SavePath = getSDCardPath() + "/Frankin";
		try {
			File path = new File(SavePath);
			filepath = SavePath + "/TEMP.png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return filepath;
	}

	private static String getSDCardPath() {
		File sdcardDir = null;
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

}
