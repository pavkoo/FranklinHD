package com.pavkoo.franklin.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("SimpleDateFormat")
public class UtilsClass {
	private static final String DATEFORMAT = "yyyy-MM-dd";
	private static final int ShortSizeLen = 3;

	public static boolean isEng() {
		Locale l = Locale.getDefault();
		String lan = l.getLanguage();
		if (lan.contains("en")) {
			return true;
		}
		return false;
	}

	public static String shortString(String org){
		if (org == null){
			return "";
		}
		if (org==""){
			return "";
		}
		if (org.length()<ShortSizeLen){
			return org;
		}
		return org.substring(0,ShortSizeLen)+".";
	}
	
	public static String dateToString(Date date) {
		String s = "";
		SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		s = sdf.format(date);
		return s;
	}

	public static Date stringToDate(String date) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static long dayCount(Date fromDay, Date toDay) {
		if (fromDay==null || toDay==null) return 0;

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

	public static void shareMsg(Context context, String activityTitle,
			String msgText, View shareView) {
		if (shareView == null) {
			shareMsg(context, activityTitle, activityTitle, msgText, "");
		} else {
			String path = GetandSaveCurrentImage(shareView);
			shareMsg(context, activityTitle, activityTitle, msgText, path);
		}
	}

	// 存储文件夹
	private static final String MyFolder = "/Frankin";
	private static final String MyTemp = "/TEMP.png";

	private static String GetandSaveCurrentImage(View drawingView) {
		String filepath = "";
		if (drawingView == null)
			return filepath;
		drawingView.setDrawingCacheEnabled(true);
		Bitmap Bmp = Bitmap.createBitmap(drawingView.getDrawingCache());
		drawingView.setDrawingCacheEnabled(false);
		String SavePath = getSDCardPath() + MyFolder;
		try {
			File path = new File(SavePath);
			filepath = SavePath + MyTemp;
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
		if (sdcardDir == null) {
			return "";
		}
		return sdcardDir.toString();
	}

	private static final String BingUrl = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";

	private static String getBingImagesInfo() {
		try {
			URL url = new URL(BingUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			String data = br.readLine().toString();
			br.close();
			in.close();
			return data;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * Bing image 格式 參考 BingUrl
	 */
	private static String getImageURL(String bufferStream) {
		if (bufferStream == null) {
			return "";
		}
		if (bufferStream.equals("")) {
			return "";
		}

		try {
			JSONObject json = new JSONObject(bufferStream);
			JSONArray images = json.getJSONArray("images");
			int length = images.length();
			if (length != 1) {
				return "";
			}
			JSONObject imageObject = images.getJSONObject(0);
			String imageUrl = imageObject.getString("url");
			return imageUrl;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static Bitmap downLoadImg(String url) {
		Bitmap bmp = null;
		try {
			InputStream in = new URL(url).openStream();
			bmp = BitmapFactory.decodeStream(in);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}

	// 缓存规则：默认每天下载一次，每天如果下了一次，就将文件保存为 今天的日期.jpg 下次打开先看有没有今天的图片，如果有就直接从缓存中取
	public static Bitmap downloadBingImage() {
		Bitmap bmp = null;
		Date today = new Date();
		SimpleDateFormat sf = new SimpleDateFormat(DATEFORMAT);
		String pathStr = getSDCardPath() + MyFolder;
		String fileName = "/" + sf.format(today) + ".jpg";
		String filePath = pathStr + fileName;
		File path = new File(pathStr);
		File file = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		if (file.exists()) {
			bmp = BitmapFactory.decodeFile(filePath);
			return bmp;
		}
		String bingInfo = getBingImagesInfo();
		String bingUrl = getImageURL(bingInfo);
		bmp = downLoadImg(bingUrl);
		// 缓存起来
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			if (null != fos && bmp != null) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}

}
