package com.pavkoo.franklin.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferenceService {
	private Context context;
	private final String MORAL_FILE = "moral";
	private final String HISTORY_MORAL_FILE = "historymoral";
	private final String HISTORY_COMMENT_FILE = "historycomment";
	private final String COMMENT_FILE = "comment";
	private final String WELCOME_FILE = "welcome";

	public SharePreferenceService(Context context) {
		this.context = context;
	}

	@SuppressLint("SimpleDateFormat")
	public ApplicationConfig loadAppconfig() {
		SharedPreferences sp = context.getSharedPreferences("APPCONFIG", Context.MODE_PRIVATE);
		ApplicationConfig appCon = new ApplicationConfig();
		appCon.setFrist(sp.getBoolean("IsFirst", true));
		appCon.setIsselfConfiged(sp.getBoolean("ISSelfConfiged", false));
		appCon.setProjectStarted(sp.getBoolean("ProjectStarted", false));
		appCon.setDefaultSaved(sp.getBoolean("DefaultSaved", false));
		appCon.setFirstUse(UtilsClass.stringToDate(sp.getString("FistUseDate", UtilsClass.dateToString(new Date()))));
		appCon.setFirstUse(UtilsClass.stringToDate(sp.getString("LastUseDate", UtilsClass.dateToString(new Date()))));
		appCon.setHistoryCount(sp.getInt("HistoryCount", 0));
		return appCon;
	}

	@SuppressLint("SimpleDateFormat")
	public ApplicationConfig saveAppConfig(ApplicationConfig appCon) {
		SharedPreferences sp = context.getSharedPreferences("APPCONFIG", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("IsFirst", appCon.isFrist());
		edit.putBoolean("ISSelfConfiged", appCon.isIsselfConfiged());
		edit.putBoolean("ProjectStarted", appCon.isProjectStarted());
		edit.putBoolean("DefaultSaved", appCon.isDefaultSaved());
		edit.putString("FistUseDate", UtilsClass.dateToString(appCon.getFirstUse()));
		edit.putString("LastUseDate", UtilsClass.dateToString(new Date()));
		edit.putInt("HistoryCount", appCon.getHistoryCount());
		edit.commit();
		return appCon;
	}

	public List<Moral> saveMorals(List<Moral> morals) {
		saveFile(morals, MORAL_FILE);
		return morals;
	}

	public List<Comment> saveComments(List<Comment> comments) {
		saveFile(comments, COMMENT_FILE);
		return comments;
	}
	
	public List<Comment> saveHistoryComments(List<Comment> comments,int historyCount) {
		String historyFileName = HISTORY_COMMENT_FILE+String.valueOf(historyCount);
		saveFile(comments, historyFileName);
		return comments;
	}
	
	public List<Moral> saveHistoryMorals(List<Moral> morals,int historyCount){
		String historyFileName = HISTORY_MORAL_FILE+String.valueOf(historyCount);
		saveFile(morals, historyFileName);
		return morals;
	}

	public List<String> saveWelcomes(List<String> welcomes) {
		saveFile(welcomes, WELCOME_FILE);
		return welcomes;
	}

	@SuppressWarnings("unchecked")
	public List<Moral> loadMorals() {
		return (List<Moral>) loadFile(MORAL_FILE);
	}

	@SuppressWarnings("unchecked")
	public List<Comment> loadComments() {
		return (List<Comment>) loadFile(COMMENT_FILE);
	}

	@SuppressWarnings("unchecked")
	public List<String> loadWelcomes() {
		return (List<String>) loadFile(WELCOME_FILE);
	}

	private void saveFile(Object object, String fileName) {
		File objectFile = new File(context.getFilesDir(), fileName);
		if (objectFile.exists())
			objectFile.delete();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(objectFile, false));
			oos.writeObject(object);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			oos = null;
		}
	}

	@SuppressWarnings("unchecked")
	private Object loadFile(String fileName) {
		List<Moral> object = null;
		File objectFile = new File(context.getFilesDir(), fileName);
		if (!objectFile.exists()) {
			return object;
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(objectFile));
			try {
				object = (ArrayList<Moral>) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ois = null;
		}
		return object;
	}

}
