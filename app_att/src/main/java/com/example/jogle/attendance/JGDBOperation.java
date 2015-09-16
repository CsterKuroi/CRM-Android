package com.example.jogle.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class JGDBOperation {
	
	private JGDBDataSet database = null;

	public JGDBOperation(Context context){
		database = new JGDBDataSet(context);
	}

	public boolean saveremind(String s,String i1,String i2,String wt,String ot){
		SQLiteDatabase db = database.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("_id", "1");
		value.put("switch",s);
		value.put("remind_id1", i1);
		value.put("remind_id2", i2);
		value.put("work_time", wt);
		value.put("off_time", ot);
		db.insertOrThrow("remind", null, value);
		db.close();
		return true;
	}

	public void deleteremind(){
		SQLiteDatabase db = database.getWritableDatabase();
		String sql = "delete from remind";
		db.execSQL(sql);
		db.close();
	}

	public List<String> getremind(){
		List<String> list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from remind";
		Cursor cursor = db.rawQuery(sql, null);
		list = new ArrayList<String>();
		while(cursor.moveToNext()){
			String s=cursor.getString(1);
			String i1=cursor.getString(2);
			String i2=cursor.getString(3);
			String wt=cursor.getString(4);
			String ot=cursor.getString(5);
			list.add(s);
			list.add(i1);
			list.add(i2);
			list.add(wt);
			list.add(ot);
		}
		cursor.close();
		db.close();
		Log.e("list", list.toString());
		return list;
	}

	public boolean save(JGDataSet dataSet){
		SQLiteDatabase db = database.getWritableDatabase();
		if(dataSet != null){
			ContentValues value = new ContentValues();
			value.put("type", dataSet.getType());
			value.put("user_id", dataSet.getUserID());
            value.put("user_name", dataSet.getUserName());
            value.put("time", dataSet.getTime());
			value.put("position", dataSet.getPosition());
			value.put("content", dataSet.getContent());
			value.put("time_stamp", dataSet.getTimeStamp());

			db.insertOrThrow("record", null, value);
			db.close();
			return true;
		}
		else{
			return false;
		}
	}

	public List<JGDataSet> getByUID(String queryName){
		if(queryName == null || queryName.equals("")){
			return getAll();
		}
		List<JGDataSet> list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from record where user_id like ?";
		String[] params = new String[]{queryName};
		Cursor cursor = db.rawQuery(sql, params);

		list = new ArrayList<JGDataSet>();
		while(cursor.moveToNext()){
			JGDataSet dataSet = new JGDataSet();
			dataSet.setType(cursor.getInt(1));
			dataSet.setUserID(cursor.getInt(2));
			dataSet.setUserName(cursor.getString(3));
			dataSet.setTime(cursor.getString(4));
			dataSet.setPosition(cursor.getString(5));
			dataSet.setContent(cursor.getString(6));
			dataSet.setTimeStamp(cursor.getString(7));
			list.add(dataSet);
		}
		cursor.close();
		db.close();
		return list;
	}

	public List<JGDataSet> getByDate(String date) {
		List<JGDataSet> list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from record where time like ?";
		String[] params = new String[]{date+"%"};
		Cursor cursor = db.rawQuery(sql, params);

		list = new ArrayList<JGDataSet>();
		while(cursor.moveToNext()){
			if (cursor.getString(4).split(" ")[0].equals(date))
				continue;
			JGDataSet dataSet = new JGDataSet();
			dataSet.setType(cursor.getInt(1));
			dataSet.setUserID(cursor.getInt(2));
			dataSet.setUserName(cursor.getString(3));
			dataSet.setTime(cursor.getString(4));
			dataSet.setPosition(cursor.getString(5));
			dataSet.setContent(cursor.getString(6));
			dataSet.setTimeStamp(cursor.getString(7));
			list.add(dataSet);
		}
		return list;
	}

	public List<JGDataSet> getAll(){
		List<JGDataSet> list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from record";
		Cursor cursor = db.rawQuery(sql, null);
		
		list = new ArrayList<JGDataSet>();
		while(cursor.moveToNext()){
			JGDataSet dataSet = new JGDataSet();
			dataSet.setType(cursor.getInt(1));
            dataSet.setUserID(cursor.getInt(2));
            dataSet.setUserName(cursor.getString(3));
            dataSet.setTime(cursor.getString(4));
			dataSet.setPosition(cursor.getString(5));
			dataSet.setContent(cursor.getString(6));
			dataSet.setTimeStamp(cursor.getString(7));
			list.add(dataSet);
		}
		cursor.close();
		db.close();
		return list;
	}
}