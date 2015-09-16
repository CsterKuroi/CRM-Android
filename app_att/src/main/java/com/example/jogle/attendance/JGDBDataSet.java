package com.example.jogle.attendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JGDBDataSet extends SQLiteOpenHelper {
	
    private static final String DATABASE_NAME = "record.db";
    private static final int DATABASE_VERSION = 1;
    private static String sql = "create table record (" + "_id integer primary key autoincrement, "
            + "type integer, " + "user_id integer, " + "user_name text, " + "time text, "
            + "position text, " + "content text, " + "time_stamp text)";
	private static String sql2 = "create table remind (" + "_id integer primary key, "
			+ "switch text, " + "remind_id1 text, "+"remind_id2 text, " + "work_time text, " + "off_time text) ";

	public JGDBDataSet(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
		db.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
