package com.pavkoo.franklin.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	public boolean needIniApp() {
		String sql = "select * from appconfig";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0) {
			return true;
		}
		return false;
	}

	public ApplicationConfig loadConfig() {
		ApplicationConfig config = new ApplicationConfig();
		String sql = "select * from appconfig";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			config.setDefaultSaved(cursor.getInt(cursor
					.getColumnIndex("isDefaultSaved")) == 1 ? true : false);
			config.setFrist(cursor.getInt(cursor.getColumnIndex("isFirst")) == 1 ? true
					: false);
			config.setIsselfConfiged(cursor.getInt(cursor
					.getColumnIndex("isSelfConfiged")) == 1 ? true : false);
			config.setProjectStarted(cursor.getInt(cursor
					.getColumnIndex("isProjectStarted")) == 1 ? true : false);
			config.setFirstUse(UtilsClass.stringToDate(cursor.getString(cursor
					.getColumnIndex("firstUse"))));
			config.setLastUse(UtilsClass.stringToDate(cursor.getString(cursor
					.getColumnIndex("lastUse"))));
			config.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			config.setHistoryCount(cursor.getInt(cursor
					.getColumnIndex("historyCount")));
		}
		return config;
	}

	public void updateConfig(ApplicationConfig config) {
		ContentValues cs = new ContentValues();
		cs.put("isDefaultSaved", config.isDefaultSaved() ? 1 : 0);
		cs.put("isFirst", config.isFrist() ? 1 : 0);
		cs.put("isSelfConfiged", config.isIsselfConfiged() ? 1 : 0);
		cs.put("isProjectStarted", config.isProjectStarted() ? 1 : 0);
		cs.put("firstUse", UtilsClass.dateToString(config.getFirstUse()));
		cs.put("lastUse", UtilsClass.dateToString(config.getLastUse()));
		cs.put("historyCount", config.getHistoryCount());
		db.update("appconfig", cs, "_id=?",
				new String[] { String.valueOf(config.getId()) });
	}

	public void importAppCon(ApplicationConfig config) {
		if (config == null) {
			return;
		}
		String sql = "INSERT INTO appconfig VALUES(null,?,?,?,?,?,?,?)";
		db.beginTransaction();
		try {
			db.execSQL(
					sql,
					new Object[] { config.isFrist() ? 1 : 0,
							config.isIsselfConfiged() ? 1 : 0,
							config.isDefaultSaved() ? 1 : 0,
							config.isProjectStarted() ? 1 : 0,
							config.getFirstUse(), config.getLastUse(),
							config.getHistoryCount() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void importMottos(List<String> mottos) {
		if (mottos == null) {
			return;
		}
		String sql = "INSERT INTO mottos VALUES(null,?)";
		db.beginTransaction();
		try {
			for (int i = 0; i < mottos.size(); i++) {
				db.execSQL(sql, new Object[] { mottos.get(i) });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void importComms(List<Comment> comms) {
		if (comms == null) {
			return;
		}
		String sql = "INSERT INTO comment VALUES(null,?,?,?,?,?)";
		db.beginTransaction();
		try {
			Comment comm = null;
			for (int i = 0; i < comms.size(); i++) {
				comm = comms.get(i);
				db.execSQL(
						sql,
						new Object[] { comm.getContent(), comm.getCount(),
								comm.isRemoved() ? 1 : 0, new Date(),
								comm.getVersion() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	
	public void updateMorals(List<Moral> morals){
		db.delete("moral", null, null);
		importMorals(morals);
	}

	public void updateMottos(List<String> mottos){
		db.delete("mottos", null, null);
		importMottos(mottos);
	}
	
	
	public void importMorals(List<Moral> morals) {
		if (morals == null) {
			return;
		}
		String sql = "INSERT INTO moral VALUES(null,?,?,?,?,?,?,?)";
		db.beginTransaction();
		try {
			Moral moral = null;
			for (int i = 0; i < morals.size(); i++) {
				moral = morals.get(i);
				db.execSQL(
						sql,
						new Object[] { moral.getTitle(), moral.getTitleDes(),
								moral.getTitleMotto(), moral.getCycle(),
								moral.getStartDate(), moral.getEndDate(),
								moral.getVersion() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void importSign(List<Moral> morals) {
		if (morals == null) {
			return;
		}

		String sql = "SELECT _id,title FROM mottos";
		Cursor cr = db.rawQuery(sql, null);
		ArrayList<Moral> dbMorals = new ArrayList<Moral>();
		while (cr.moveToNext()) {
			Moral m = new Moral();
			m.setId(cr.getInt(cr.getColumnIndex("_id")));
			m.setTitle(cr.getString(cr.getColumnIndex("title")));
			dbMorals.add(m);
		}
		cr.close();

		List<SignRecords> srList = new ArrayList<SignRecords>();
		for (int i = 0; i < morals.size(); i++) {
			Moral m = morals.get(i);
			List<CheckState> cslist = m.getStateList();
			Date startDate = m.getStartDate();
			int moarlIndex = -1;
			for (int k = 0; k < dbMorals.size(); k++) {
				if (dbMorals.get(k).getTitle() == m.getTitle()) {
					moarlIndex = dbMorals.get(k).getId();
					break;
				}
			}
			for (int j = 0; j < cslist.size(); j++) {
				SignRecords sr = new SignRecords();
				sr.setMoarlIndex(moarlIndex);
				sr.setInputDate(UtilsClass.subDate(startDate, -j));
				// 以前的感想记录都不再保存到关联里面了
				sr.setCommentIndex(-1);
				sr.setCs(cslist.get(j));
				srList.add(sr);
			}
		}

		sql = "INSERT INTO signrecord VALUES(null,?,?,?,?)";
		db.beginTransaction();
		try {
			SignRecords sr = null;
			for (int i = 0; i < srList.size(); i++) {
				sr = srList.get(i);
				db.execSQL(
						sql,
						new Object[] { sr.getMoarlIndex(),
								sr.getCommentIndex(), sr.getCs().ordinal(),
								sr.getInputDate() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public List<Comment> loadComment() {
		String sql = "SELECT * FROM comment";
		Cursor cr = db.rawQuery(sql, null);
		ArrayList<Comment> comms = new ArrayList<Comment>();
		while (cr.moveToNext()) {
			Comment c = new Comment();
			c.setId(cr.getInt(cr.getColumnIndex("_id")));
			c.setContent(cr.getString(cr.getColumnIndex("content")));
			c.setCount(cr.getInt(cr.getColumnIndex("refCount")));
			c.setRemoved(cr.getInt(cr.getColumnIndex("removed")) == 1 ? true
					: false);
			c.setTimestamp(UtilsClass.stringToDate(
					cr.getString(cr.getColumnIndex("timestamp"))).getTime());
			c.setVersion(cr.getInt(cr.getColumnIndex("version")));
			comms.add(c);
		}
		cr.close();
		return comms;
	}

	public List<Moral> loadMorals(){
		String sql = "SELECT * FROM moral";
		Cursor cr = db.rawQuery(sql, null);
		ArrayList<Moral> morals = new ArrayList<Moral>();
		while (cr.moveToNext()) {
			Moral m = new Moral();
			m.setTitle(cr.getString(cr.getColumnIndex("title")));
			m.setTitleDes(cr.getString(cr.getColumnIndex("titleDes")));
			m.setTitleMotto(cr.getString(cr.getColumnIndex("titleMotto")));
			m.setCycle(cr.getInt(cr.getColumnIndex("cycle")));
			m.setStartDate(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("startDate"))));
			m.setEndDate(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("endDate"))));
			m.setVersion(cr.getInt(cr.getColumnIndex("version")));
			morals.add(m);
		}
		cr.close();
		return morals;
	}
	
	
	
	public List<String> loadMottos() {
		String sql = "SELECT * FROM mottos";
		Cursor cr = db.rawQuery(sql, null);
		ArrayList<String> mottos = new ArrayList<String>();
		while (cr.moveToNext()) {
			mottos.add(cr.getString(cr.getColumnIndex("content")));
		}
		cr.close();
		return mottos;
	}

	
	
	
}

