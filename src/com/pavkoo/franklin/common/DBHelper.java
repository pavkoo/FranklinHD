package com.pavkoo.franklin.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String DATABASE_NAME = "pavkooFranklin.db";
	private static final int DATABASE_VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		String tableAppConfig = "CREATE TABLE IF NOT EXISTS appconfig "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "isFirst SMALLINT," + "isSelfConfiged SMALLINT,"
				+ "isDefaultSaved SMALLINT,isProjectStarted SMALLINT,"
				+ "firstUse DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "lastUse DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "historyCount SMALLINT)";
		String tableMoral = " CREATE TABLE IF NOT EXISTS moral "
				+ "(_id INTEGER PRIAMRY KEY AUTOINTECREMENT,"
				+ "title VARCHAR," + "titleDes VARCHAR,"
				+ "titleMotto VARCHAR," + "cycle SMALLINT,"
				+ "startDate DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "endDate DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "version SMALLINT)";
		String tableComment = "CREATE TABLE IF NOT EXISTS comment "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "content VARCHAR," + "refCount SMALLINT,"
				+ "removed SMALLINT,"
				+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "version SMALLINT)";
		String tableSignRecord = "CREATE TABLE IF NOT EXISTS signrecord "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "refMoralindex INTEGER," + "refCommentIndex INTEGER,"
				+ "checkstate SMALLINT,"
				+ "inputdate DATETIME DEFAULT CURRENT_TIMESTAMP)";
		String tableMottos = "CREATE TABLE IF NOT EXISTS mottos "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "content VARCHAR)";
		db.execSQL(tableAppConfig);
		db.execSQL(tableMoral);
		db.execSQL(tableComment);
		db.execSQL(tableSignRecord);
		db.execSQL(tableMottos);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
