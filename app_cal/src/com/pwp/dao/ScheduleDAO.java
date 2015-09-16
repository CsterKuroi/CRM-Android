package com.pwp.dao;
import java.util.ArrayList;

import com.pwp.vo.ScheduleDateTag;
import com.pwp.vo.ScheduleVO;
import com.pwp.application.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * ���ճ�DAO����
 * @author song
 *
 */
public class ScheduleDAO {
	application aid=new application();

	private DBOpenHelper dbOpenHelper = null;
	//private Context context = null;
	
	public ScheduleDAO(Context context){
        //this.context = context;
		dbOpenHelper = new DBOpenHelper(context, "schedules.db");
	}	
	/**
	 * �����ճ���Ϣ
	 * @param scheduleVO
	 */
	public int save(ScheduleVO scheduleVO){
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		//
		values.put("scheduleTypeID", scheduleVO.getScheduleTypeID());
		values.put("createrID", scheduleVO.getcreaterID());
	
		values.put("remindID", scheduleVO.getRemindID());//没用了 b
		values.put("participant", scheduleVO.getparticipant());
		values.put("issendmail", scheduleVO.getissendmail());
		values.put("scheduleDate", scheduleVO.getScheduleDate());
		values.put("scheduleDate2", scheduleVO.getScheduleDate2());
		values.put("priority", scheduleVO.getpriority());
		values.put("scheduleContent", scheduleVO.getScheduleContent());
		values.put("scheduleid2",scheduleVO.getscheduleid2());
		
	    values.put("aboutID",scheduleVO.getaboutID());
		
		
	/*	"CREATE TABLE IF NOT EXISTS schedule(" +
				"scheduleID integer primary key autoincrement," +
				"scheduleTypeID integer," +//创建者,删除日程需判断uid=createdid才可以，执行删除任务
				"createrID text," +
				"remindID integer," +
				"participant text,"+
				"issendmail integer,"+
				"scheduleDate text,"+
				"scheduleDate2 text," +
				"priority text," +
				"scheduleContent text," +
				"scheduleid2 text,"
				+ "aboutID text" );//参与者，某用户本手机登录后，拉取关于自己的日程)"*/
		
		db.beginTransaction();
		int scheduleID = -1;
		try{
			db.insert("schedule", null, values);
            Log.d("日程插入本地数据库成功","dd");
			//Toast.makeText(add.this, payload, Toast.LENGTH_LONG).show();

		    Cursor cursor = db.rawQuery("select max(scheduleID) from schedule", null);
		    if(cursor.moveToFirst()){
		    	scheduleID = (int) cursor.getLong(0);
		    }
		    cursor.close();
		    db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	    return scheduleID;
	}
	
	/**
	 * ��ѯĳһ���ճ���Ϣ
	 * @param scheduleID
	 * @return
	 */
	public ScheduleVO getScheduleByID(int scheduleID){
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.query
		("schedule", new String[]{"scheduleID","createrID","aboutID","scheduleTypeID","remindID","participant","issendmail"
		,"scheduleDate","scheduleDate2","priority","scheduleContent","scheduleid2"}, "scheduleID=?", new String[]{String.valueOf(scheduleID)}, null, null, null);
		if(cursor.moveToFirst()){
			int schID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
			String createrID = cursor.getString(cursor.getColumnIndex("createrID"));
			int scheduleTypeID = cursor.getInt(cursor.getColumnIndex("scheduleTypeID"));
			int remindID = cursor.getInt(cursor.getColumnIndex("remindID"));
			String participant = cursor.getString(cursor.getColumnIndex("participant"));
			int issendmail = cursor.getInt(cursor.getColumnIndex("issendmail"));
			String scheduleDate = cursor.getString(cursor.getColumnIndex("scheduleDate"));
			String scheduleDate2 = cursor.getString(cursor.getColumnIndex("scheduleDate2"));
			int priority = cursor.getInt(cursor.getColumnIndex("priority"));
			String scheduleContent = cursor.getString(cursor.getColumnIndex("scheduleContent"));
			String scheduleid2=cursor.getString(cursor.getColumnIndex("scheduleid2"));
			String aboutID=cursor.getString(cursor.getColumnIndex("aboutID"));
			
			cursor.close();
			return new ScheduleVO(schID,createrID,scheduleTypeID,remindID,participant,issendmail,scheduleDate,scheduleDate2,priority,scheduleContent,scheduleid2,aboutID);
		}
		cursor.close();
		return null;
		
	}
	
	/**
	 * ��ѯ���е��ճ���Ϣ
	 * @return
	 */
	public ArrayList<ScheduleVO> getAllSchedule(String aboutid){
		ArrayList<ScheduleVO> list = new ArrayList<ScheduleVO>();
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("schedule", new String[]{"scheduleID", "createrID", "scheduleTypeID", "remindID", "participant", "issendmail"
				, "scheduleDate", "scheduleDate2", "priority", "scheduleContent", "scheduleid2,aboutID"}, "aboutid=?", new String[]{aboutid}, null, null, "scheduleID desc");
		while(cursor.moveToNext()){
			int schID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
			//int authorID = cursor.getInt(cursor.getColumnIndex("createrID"));
			int scheduleTypeID = cursor.getInt(cursor.getColumnIndex("scheduleTypeID"));
			int remindID = cursor.getInt(cursor.getColumnIndex("remindID"));
			String participant = cursor.getString(cursor.getColumnIndex("participant"));
			int issendmail = cursor.getInt(cursor.getColumnIndex("issendmail"));
			String scheduleDate = cursor.getString(cursor.getColumnIndex("scheduleDate"));
			String scheduleDate2 = cursor.getString(cursor.getColumnIndex("scheduleDate2"));
			int priority = cursor.getInt(cursor.getColumnIndex("priority"));
			String scheduleContent = cursor.getString(cursor.getColumnIndex("scheduleContent"));
			String scheduleid2=cursor.getString(cursor.getColumnIndex("scheduleid2"));
			String aboutID=cursor.getString(cursor.getColumnIndex("aboutID"));
			ScheduleVO vo = new ScheduleVO(schID,aboutid,scheduleTypeID,remindID,participant,issendmail,scheduleDate,scheduleDate2,priority,scheduleContent,scheduleid2,aboutID);
			list.add(vo);
		}
		cursor.close();
		if(list != null && list.size() > 0){
			return list;
		}
		return null;
		
	}
	
	/**
	 * ɾ���ճ�
	 * @param scheduleID
	 */
	public void delete(int scheduleID){
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			db.delete("schedule", "scheduleID=?", new String[]{String.valueOf(scheduleID)});
			db.delete("scheduletagdate", "scheduleID=?", new String[]{String.valueOf(scheduleID)});
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * �����ճ�
	 * @param vo
	 */
	public void update(ScheduleVO vo){
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("scheduleTypeID", vo.getScheduleTypeID());
		//values.put("authorID", vo.getauthorID());
		values.put("remindID", vo.getRemindID());
		values.put("participant", vo.getparticipant());
		values.put("issendmail", vo.getissendmail());
		values.put("scheduleDate", vo.getScheduleDate());
		values.put("scheduleDate2", vo.getScheduleDate2());
		values.put("priority", vo.getpriority());
		values.put("scheduleContent", vo.getScheduleContent());
		db.update("schedule", values, "scheduleID=?", new String[]{String.valueOf(vo.getScheduleID())});
	}
	
	/**
	 * ���ճ̱�־���ڱ��浽���ݿ���
	 * @param dateTagList
	 */
	public void saveTagDate(ArrayList<ScheduleDateTag> dateTagList){
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ScheduleDateTag dateTag = new ScheduleDateTag();
		for(int i = 0; i < dateTagList.size(); i++){
			dateTag = dateTagList.get(i);
			ContentValues values = new ContentValues();
			values.put("year", dateTag.getYear());
			values.put("month", dateTag.getMonth());
			values.put("day", dateTag.getDay());
			values.put("scheduleID", dateTag.getscheduleID());
			values.put("userid",dateTag.getuserid());
			db.insert("scheduletagdate", null, values);
		}
	}
	
	/**
	 * ֻ��ѯ����ǰ�µ��ճ�����
	 * @param currentYear
	 * @param currentMonth
	 * @return
	 */
	public ArrayList<ScheduleDateTag> getTagDate(String userid,int currentYear, int currentMonth){
		ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
		//dbOpenHelper = new DBOpenHelper(context, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		//Log.e("bbbbbbbbbbbbbbb",userid);
		Cursor cursor = db.query("scheduletagdate", new String[]{"tagID","year","month","day","scheduleID"}, "year=? and month=? and userid=?",
				new String[]{String.valueOf(currentYear),String.valueOf(currentMonth),userid},null, null, null);
		while(cursor.moveToNext()){
			Log.e("number","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			int tagID = cursor.getInt(cursor.getColumnIndex("tagID"));
			int year = cursor.getInt(cursor.getColumnIndex("year"));
			int month = cursor.getInt(cursor.getColumnIndex("month"));
			int day = cursor.getInt(cursor.getColumnIndex("day"));
			int scheduleID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
			//ScheduleDateTag dateTag = new ScheduleDateTag(tagID,aid.getuserid(),year,month,day,scheduleID);
			//�˴������˺ܳ�ʱ����ԣ�ֻ��Ϊ���ú���ʱ������˳��д�����ܹ���ȷ���е��¡�
			ScheduleDateTag dateTag = new ScheduleDateTag(tagID,scheduleID,userid,year,month,day);
			dateTagList.add(dateTag);
			}
		cursor.close();
		if(dateTagList != null && dateTagList.size() > 0){
			return dateTagList;
		}
		return null;
	}


	
	/**
	 * �����ÿһ��gridview��itemʱ,��ѯ�������������е��ճ̱��(scheduleID)
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String[] getScheduleByTagDate(String userid,int year, int month, int day){
		ArrayList<ScheduleVO> scheduleList = new ArrayList<ScheduleVO>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		//����ʱ���ѯ���ճ�ID��scheduleID����һ�����ڿ��ܶ�Ӧ����ճ�ID
		Cursor cursor = db.query("scheduletagdate", new String[]{"scheduleID"}, "year=? and month=? and day=? and userid=?", new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day),userid}, null, null, null);
		String scheduleIDs[] = null;
		scheduleIDs = new String[cursor.getCount()];
		int i = 0;
		while(cursor.moveToNext()){
			String scheduleID = cursor.getString(cursor.getColumnIndex("scheduleID"));
			scheduleIDs[i] = scheduleID;
			i++;
		}
		cursor.close();
		
		return scheduleIDs;
		
		
	}
	
	/**
	 *�ر�DB
	 */
	public void destoryDB(){
		if(dbOpenHelper != null){
			dbOpenHelper.close();
		}
	}

	
	
	
}
