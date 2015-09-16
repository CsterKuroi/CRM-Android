package com.pwp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.ricky.database.CenterDatabase;

public class DBOpenHelper extends SQLiteOpenHelper {

	private final static int VERSION = 1;


	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBOpenHelper(Context context, String name, CursorFactory factory){
		this(context,name,null,VERSION);
	}

	public DBOpenHelper(Context context,String name){
		this(context,name,null);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//日程
		db.execSQL("CREATE TABLE IF NOT EXISTS schedule(" +
				"scheduleID integer primary key autoincrement," +
				"createrID text," +//创建者,删除日程需判断uid=createdid才可以，执行删除任务
				"aboutID text," +//参与者，某用户本手机登录后，拉取关于自己的日程
				"scheduleTypeID integer," +
				"remindID integer," +
				//"nextremindtime text,"+
				"participant text,"+
				"issendmail integer,"+
				"scheduleDate text,"+
				"scheduleDate2 text," +
				"priority text," +
				"scheduleContent text," +
				"state text,"+
		        "scheduleid2 text)");

		db.execSQL("CREATE TABLE IF NOT EXISTS scheduletagdate(" +
				"tagID integer primary key autoincrement," +
				"userid text," +
				"year integer," +
				"month integer," +
				"day integer," +
				"scheduleID integer)");

		/*db.execSQL("create table if not exists  user(" +
				"id integer primary key autoincrement," +
				"userID text," +
				"name text," +
				"department text," +
				"class text," +
				"email text," +
				"updatetime text)");*/
		//联系人更新记录，不用记录用户的id
	/*	db.execSQL("create table if not exists  contacts_update(" +
				"id integer primary key autoincrement," +
				"time text," +
				"statement text," +
				"userid text)");*/
		//更新日程，本机记录不同用户的更新时间，往本机上加载关于自己的日程
		db.execSQL("create table if not exists  schedule_update(" +
				"id integer primary key autoincrement," +
				"userid text,"+//用户的更新记录
				"time text," +
				"statement text)");

		/*db.execSQL("create table if not exists t_schedule(id integer primary key autoincrement,whosetup integer,scheduleTypeID integer,remindID integer,priority text,joiner text,issendmail integer,scheduleDate text,endtime text,scheduleContent text)");
	     db.execSQL("create table if not exists t_department(id integer primary key autoincrement,name text)");
	    db.execSQL("create table if not exists t_class(id integer primary key autoincrement,name text)");
	    //db.execSQL("alter table t_user add constraint fk_1 foreign key(departmentid) references t_department(id)");
	   //db.execSQL("alter table t_user add constraint fk_2 foreign key(class) references t_class(id)");
	    db.execSQL("insert into t_user(id,name,departmentid,class) values (1,'tom',1,1)");
	    db.execSQL("insert into t_user(id,name,departmentid,class) values (2,'jim',2,1)");
	    db.execSQL("insert into t_department(id,name) values (1,'���۲�')");
	    db.execSQL("insert into t_department(id,name) values (2,'���ڲ�')");*/

		/*Cursor cursor = db.rawQuery("select * from user",null);
		if(cursor.moveToFirst()){
		}
		else {
			db.execSQL("insert into contacts_update(time) values ('1000000000')");
		}
		cursor.close();
*/

		Cursor cursor2 = db.rawQuery("select * from schedule_update",null);
		if(cursor2.moveToFirst()){
		}
		else {
			db.execSQL("insert into schedule_update(time) values ('0')");
		}
		cursor2.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS schedule");
		db.execSQL("DROP TABLE IF EXISTS scheduletagdate");
		onCreate(db);
	}
}
