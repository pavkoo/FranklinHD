package com.pavkoo.franklin.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseIntArray;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
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
			config.setDefaultSaved(cursor.getInt(cursor.getColumnIndex("isDefaultSaved")) == 1 ? true : false);
			config.setFrist(cursor.getInt(cursor.getColumnIndex("isFirst")) == 1 ? true : false);
			config.setIsselfConfiged(cursor.getInt(cursor.getColumnIndex("isSelfConfiged")) == 1 ? true : false);
			config.setProjectStarted(cursor.getInt(cursor.getColumnIndex("isProjectStarted")) == 1 ? true : false);
			config.setFirstUse(UtilsClass.stringToDate(cursor.getString(cursor.getColumnIndex("firstUse"))));
			config.setLastUse(UtilsClass.stringToDate(cursor.getString(cursor.getColumnIndex("lastUse"))));
			config.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			config.setHistoryCount(cursor.getInt(cursor.getColumnIndex("historyCount")));
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
		db.update("appconfig", cs, "_id=?", new String[]{String.valueOf(config.getId())});
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
					new Object[]{config.isFrist() ? 1 : 0, config.isIsselfConfiged() ? 1 : 0, config.isDefaultSaved() ? 1 : 0,
							config.isProjectStarted() ? 1 : 0, UtilsClass.dateToString(config.getFirstUse()),
							UtilsClass.dateToString(config.getLastUse()), config.getHistoryCount()});
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
				db.execSQL(sql, new Object[]{mottos.get(i)});
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
				db.execSQL(sql,
						new Object[]{comm.getContent(), comm.getCount(), comm.isRemoved() ? 1 : 0, UtilsClass.dateToString(new Date()),
								comm.getVersion()});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void updateMorals(List<Moral> morals) {
		updateMorals(morals, false);
	}

	public void updateMorals(List<Moral> morals, boolean deleteAll) {
		// 先删除
		db.beginTransaction();
		try {
			if (deleteAll) {
				db.delete("moral", null, null);
			} else {
				String delestr = "";
				for (int i = 0; i < morals.size(); i++) {
					if (morals.get(i).isFinished() || morals.get(i).isDoing()) {
						delestr += morals.get(i).getId() + ",";
					}

				}
				if (delestr != "") {
					delestr = delestr.substring(0, delestr.length() - 1);
					String sql = "delete from moral where _id not in (" + delestr + ")";
					db.execSQL(sql);
				}
			}

			String sql = "INSERT INTO moral VALUES(null,?,?,?,?,?,?,?)";
			for (int i = 0; i < morals.size(); i++) {
				if (morals.get(i).isFinished() || morals.get(i).isDoing()) {
					continue;
				}
				Moral moral = null;
				moral = morals.get(i);
				db.execSQL(
						sql,
						new Object[]{moral.getTitle(), moral.getTitleDes(), moral.getTitleMotto(), moral.getCycle(),
								UtilsClass.dateToString(moral.getStartDate()), UtilsClass.dateToString(moral.getEndDate()),
								moral.getVersion()});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	public void updateMottos(List<String> mottos) {
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
						new Object[]{moral.getTitle(), moral.getTitleDes(), moral.getTitleMotto(), moral.getCycle(),
								UtilsClass.dateToString(moral.getStartDate()), UtilsClass.dateToString(moral.getEndDate()),
								moral.getVersion()});
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
				db.execSQL(sql, new Object[]{sr.getMoarlIndex(), sr.getCommentIndex(), sr.getCs().ordinal(), sr.getInputDate()});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public List<Comment> loadComment() {
		String sql = "SELECT * FROM comment where removed=?";
		Cursor cr = db.rawQuery(sql, new String[]{String.valueOf(0)});
		ArrayList<Comment> comms = new ArrayList<Comment>();
		while (cr.moveToNext()) {
			Comment c = new Comment();
			c.setId(cr.getInt(cr.getColumnIndex("_id")));
			c.setContent(cr.getString(cr.getColumnIndex("content")));
			c.setCount(cr.getInt(cr.getColumnIndex("refCount")));
			c.setRemoved(cr.getInt(cr.getColumnIndex("removed")) == 1 ? true : false);
			c.setTimestamp(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("timestamp"))).getTime());
			c.setVersion(cr.getInt(cr.getColumnIndex("version")));
			comms.add(c);
		}
		cr.close();
		return comms;
	}

	public List<Moral> loadMorals() {
		String sql = "SELECT * FROM moral";
		Cursor cr = db.rawQuery(sql, null);
		ArrayList<Moral> morals = new ArrayList<Moral>();
		while (cr.moveToNext()) {
			Moral m = new Moral();
			m.setId(cr.getInt(cr.getColumnIndex("_id")));
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

	public List<SignRecords> loadSignedRecord() {
		ArrayList<SignRecords> slist = new ArrayList<SignRecords>();
		String sql = "SELECT * FROM signrecord WHERE checkstate=1 order by inputdate";
		Cursor cr = db.rawQuery(sql, null);
		while (cr.moveToNext()) {
			SignRecords sr = new SignRecords();
			sr.setId(cr.getInt(cr.getColumnIndex("_id")));
			sr.setCommentIndex(cr.getInt(cr.getColumnIndex("refCommentIndex")));
			sr.setMoarlIndex(cr.getInt(cr.getColumnIndex("refMoralindex")));
			sr.setCs(CheckState.values()[cr.getInt(cr.getColumnIndex("checkstate"))]);
			sr.setInputDate(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("inputdate"))));
			slist.add(sr);
		}
		return slist;
	}

	public List<SignRecords> loadAllSginRecord() {
		ArrayList<SignRecords> slist = new ArrayList<SignRecords>();
		String sql = "SELECT * FROM signrecord";
		Cursor cr = db.rawQuery(sql, null);
		while (cr.moveToNext()) {
			SignRecords sr = new SignRecords();
			sr.setId(cr.getInt(cr.getColumnIndex("_id")));
			sr.setCommentIndex(cr.getInt(cr.getColumnIndex("refCommentIndex")));
			sr.setMoarlIndex(cr.getInt(cr.getColumnIndex("refMoralindex")));
			sr.setCs(CheckState.values()[cr.getInt(cr.getColumnIndex("checkstate"))]);
			sr.setInputDate(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("inputdate"))));
			slist.add(sr);
		}
		return slist;
	}

	// 业务区
	public int getCurrentMoralId() {
		String sql = "SELECT * FROM moral where (julianday(date('now'))>=julianday(startDate)) and (julianday(date('now'))<=julianday(endDate))";
		Cursor cr = db.rawQuery(sql, null);
		int id = -1;
		while (cr.moveToNext()) {
			id = cr.getInt(cr.getColumnIndex("_id"));
		}
		cr.close();
		return id;
	}

	public boolean isBeforeTraining() {
		String sql = "SELECT * FROM moral where date('now')>=startDate";
		Cursor cr = db.rawQuery(sql, null);
		if (cr.getCount() == 0) {
			return true;
		}
		return false;
	}

	public boolean isAfterTraning() {
		String sql = "SELECT * FROM moral WHERE date('now')<=endDate";
		Cursor cr = db.rawQuery(sql, null);
		if (cr.getCount() == 0) {
			return true;
		}
		return false;
	}

	public int lastReflectDate() {
		String sql = "SELECT MAX(inputdate) AS inputdate  FROM signrecord";
		Cursor cr = db.rawQuery(sql, null);
		int dayCount = -1;
		if (cr.getCount() == 0) {
			return dayCount;
		} else {

			if (cr.moveToNext()) {
				Date lastDate = UtilsClass.stringToDate(cr.getString(0));
				if (lastDate == null) {
					return dayCount;
				}
				dayCount = (int) (UtilsClass.dayCount(lastDate, new Date()) + 1);
			}
			cr.close();
		}
		return dayCount;
	}

	public HashMap<String, List<SignRecords>> getNewWeekSing(Date weekstartDate, Date weekEndDate) {
		HashMap<String, List<SignRecords>> newWeekData = new HashMap<String, List<SignRecords>>();

		String sql = "SELECT _id,refMoralindex,refCommentIndex,checkstate,inputdate FROM signrecord  WHERE (julianday(inputdate)>=julianday(date(?)) AND julianday(inputdate)<=julianday(date(?)))  order by inputdate";
		Cursor cr = db.rawQuery(sql, new String[]{UtilsClass.dateToString(weekstartDate), UtilsClass.dateToString(weekEndDate)});
		while (cr.moveToNext()) {
			int moralid = cr.getInt(cr.getColumnIndex("refMoralindex"));
			List<SignRecords> slist = null;
			if (newWeekData.containsKey(String.valueOf(moralid))) {
				slist = newWeekData.get(String.valueOf(moralid));
			} else {
				slist = new ArrayList<SignRecords>();
			}
			SignRecords sr = new SignRecords();
			sr.setId(cr.getInt(cr.getColumnIndex("_id")));
			sr.setCommentIndex(cr.getInt(cr.getColumnIndex("refCommentIndex")));
			sr.setMoarlIndex(cr.getInt(cr.getColumnIndex("refMoralindex")));
			sr.setCs(CheckState.values()[cr.getInt(cr.getColumnIndex("checkstate"))]);
			sr.setInputDate(UtilsClass.stringToDate(cr.getString(cr.getColumnIndex("inputdate"))));
			slist.add(sr);
			newWeekData.put(String.valueOf(moralid), slist);
		}
		cr.close();
		return newWeekData;
	}

	public SignRecords insertNew(SignRecords sr) {
		String sql = "INSERT INTO signrecord VALUES(null,?,?,?,?)";
		db.beginTransaction();
		try {
			db.execSQL(
					sql,
					new Object[]{sr.getMoarlIndex(), sr.getCommentIndex(), sr.getCs().ordinal(), UtilsClass.dateToString(sr.getInputDate())});
			sql = "select last_insert_rowid() from signrecord";
			Cursor cr = db.rawQuery(sql, null);
			if (cr.moveToNext()) {
				sr.setId(cr.getInt(0));
			}
			cr.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return sr;
	}

	public SignRecords updateSr(SignRecords sr) {
		ContentValues cs = new ContentValues();
		cs.put("refMoralindex", sr.getMoarlIndex());
		cs.put("refCommentIndex", sr.getCommentIndex());
		cs.put("checkstate", sr.getCs().ordinal());
		cs.put("inputdate", UtilsClass.dateToString(sr.getInputDate()));
		db.update("signrecord", cs, "_id=?", new String[]{String.valueOf(sr.getId())});
		return sr;
	}

	public void updateComment(Comment c) {
		ContentValues cs = new ContentValues();
		cs.put("content", c.getContent());
		cs.put("refCount", c.getCount());
		cs.put("removed", c.isRemoved() ? 1 : 0);
		cs.put("timestamp", UtilsClass.dateToString(new Date()));
		cs.put("version", c.getVersion());
		db.update("comment", cs, "_id=?", new String[]{String.valueOf(c.getId())});
	}

	public void removeComment(int id) {
		db.delete("comment", "_id=?", new String[]{String.valueOf(id)});
	}

	public Comment insertNewComment(Comment comm) {
		String sql = "INSERT INTO comment VALUES(null,?,?,?,?,?)";
		db.beginTransaction();
		try {
			db.execSQL(sql, new Object[]{comm.getContent(), comm.getCount(), comm.isRemoved() ? 1 : 0, UtilsClass.dateToString(new Date()),
					comm.getVersion()});
			sql = "select last_insert_rowid() from comment";
			Cursor cr = db.rawQuery(sql, null);
			if (cr.moveToNext()) {
				comm.setId(cr.getInt(0));
			}
			cr.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return comm;
	}

	public int getTotoalSignCount() {
		int count = 0;
		String sql = "SELECT COUNT(*) FROM signrecord";
		Cursor cr = db.rawQuery(sql, null);
		if (cr.moveToNext()) {
			count = cr.getInt(0);
		}
		cr.close();
		return count;
	}

	public SparseIntArray getDoneSignCount() {
		String sql = "SELECT COUNT(refMoralindex) totalcount,refMoralindex FROM (SELECT * FROM signrecord WHERE checkstate=1 ) GROUP BY refMoralindex";
		Cursor cr = db.rawQuery(sql, null);
		SparseIntArray sparseArray = new SparseIntArray();
		while (cr.moveToNext()) {
			sparseArray.put(cr.getInt(1), cr.getInt(0));
		}
		return sparseArray;
	}

	public void clearComment() {
		db.delete("signrecord", "refMoralindex=?", new String[]{String.valueOf(-1)});
		db.delete("comment", null, null);
	}

	public void clearSignrecord() {
		db.delete("signrecord", null, null);
	}

	public void restoretodefault() {
		db.delete("comment", null, null);
		db.delete("signrecord", null, null);
		db.delete("mottos", null, null);
		db.delete("moral", null, null);
	}
}
