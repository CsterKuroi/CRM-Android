package com.pwp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UserDAO {
	
	private DBOpenHelper dbOpenHelper = null;
	
	public UserDAO(Context context) {
		dbOpenHelper = new DBOpenHelper(context, "schedules.db");
	}
	public static  class Update{
		private static String time;
		private static String statement;
		public void settime(String time) {
			Update.time=time;
		}
		public String gettime() {
			return time;
		}
		public void setstatement(String statement) {
			Update.statement=statement;
		}
		public String getstatement() {
			return statement;
		}
		
	}
	public static class User{
		private static String userid;
		private static String name;
		private static String department;
		private static String userclass;
		private static String email;
		private static String updatetime;
		public void setuserid(String userid) {
			User.userid=userid;
		}
		public String getuserid() {
			return userid;
		}
		public void setname(String name) {
			User.name=name;
		}
		public String getname() {
			return name;
		}
		public void setdepartment(String department) {
			User.department=department;
		}
		public String getdepartment() {
			return department;
		}
		public void setuserclass(String userclass) {
			User.userclass=userclass;
		}
		public String getuserclass() {
			return userclass;
		}
		public void setemail(String email) {
			User.email=email;
		}
		public String getemail() {
			return email;
		}
		public void setupdatetime(String updatetime) {
			User.updatetime=updatetime;
		}
		public String getupdatetime() {
			return updatetime;
		}
	}
	//ɾ����ϵ��
		public void deleteuser(String userid) {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			db.beginTransaction();
			try{
				db.delete("user", "userid=?", new String[]{userid});
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
		}
		//������ϵ��
		public String saveuser(User user){
			String flag="0";
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("userid", User.userid);
			values.put("name", User.name);
			values.put("department", User.department);
			values.put("class", User.userclass);
			values.put("email", User.email);
			db.beginTransaction();
			try{
				db.insert("user", null, values);
			    Cursor cursor = db.rawQuery("select * from user where userid=? and name=? ",new String[] {values.get("userid").toString(),values.get("name").toString()});
			    if(cursor.moveToFirst()){//��ѯ������˵������ɹ�
			    		flag="1";
			    }
			    cursor.close();
			    db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
		   return flag;
		}
		
		public void updateuser(User user){
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("userid", User.userid);
			values.put("name", User.name);
			values.put("department", User.department);
			values.put("class", User.userclass);
			values.put("email", User.email);
			db.update("user", values, "userid=?", new String[]{values.get("userid").toString()});
		}
		
		
		//����һ�����¼�¼������ɹ�����1���򷵻�0
		public String save(Update update){
			String flag="0";
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("time", Update.time);
			values.put("statement", Update.statement);
			db.beginTransaction();
		
			try{
				db.insert("update_history", null, values);
			    Cursor cursor = db.rawQuery("select * from update_history where id=(select max(id) from update_history)", null);
			    if(cursor.moveToFirst()){
			    	if(values.get("time") ==  cursor.getString(1)) {
			    		flag="1";
			    	}
			    }
			    cursor.close();
			    db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
		   return flag;
		}
	
}
