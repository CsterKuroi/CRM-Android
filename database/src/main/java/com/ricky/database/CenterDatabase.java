package com.ricky.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 使用说明：
 * 1、新建数据库helper类
 *      在Activity中：CenterDatabase dbOpenHelper = new CenterDatabase(this, null);
 *      在Fragment中：CenterDatabase dbOpenHelper = new CenterDatabase(getActivity(), null);
 * 说明：构造函数第二个参数为SQLiteDatabase.CursorFactory类，如果用不到可以传null。
 *
 * 2、生成一些测试的数据
 *      dbOpenHelper.createLocalTestData();     // 在数据库中插入两个部门，三个人员；插入自己的ID和UID
 *
 * 3、获取用户自己的ID的方法
 *      获取ID：dbOpenHelper.getID();           // 获取Teamtalk的ID，int类型，一般用不到
 *      获取UID：dbOpenHelper.getUID();         // 获取人员的ID（即工号、登录用户名），String类型
 *
 * 4、如果需要自定义
 *     （1）不要修改本文件已有的函数、变量、常量
 *     （2）通过新建的数据库helper类，获取数据库变量SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
 *     （3）使用数据库变量进行数据库操作。数据库变量的使用方式自行查阅资料，可以参考本文件中的几个函数。
 */

public class CenterDatabase extends SQLiteOpenHelper {

    public static final String NAME = "center.db";
    public static final String DEPT = "department";
    public static final String USER = "user";
    public static final String TIME = "time";
    public static final String MYID = "myid";
    public static final String KEHU = "kehu";
    public static final String LIANXIREN = "lianxiren";
    public static final String TIME2 = "time2";

    public static final String IP = "101.200.189.127";
    public static final String testIP = "192.168.50.11";
    public static final String URI = "ws://" + IP + ":8001/ws";

    private Context context;

    public CenterDatabase(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, NAME, factory, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + DEPT + "' (" +
                "id integer PRIMARY KEY," +
                "name text," +
                "pri text," +
                "manager text," +
                "status text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + USER + "' (" +
                "id integer PRIMARY KEY," +
                "uid text," +
                "name text," +
                "utouxiang text," +
                "udept text," +
                "status text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + TIME + "' (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "time text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + MYID + "' (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "ttid integer," +
                "uid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + KEHU + "' (" +
                "id integer PRIMARY KEY," +
                "uid text," +
                "uname text," +
                "userphone text," +
                "useremail text," +
                "userfox text," +
                "useraddress text," +
                "leixing text," +
                "xingzhi text," +
                "guimo text," +
                "userbeizhu text," +
                "kehustate text," +
                "kehurank text," +
                "status text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + LIANXIREN + "' (" +
                "id integer PRIMARY KEY," +
                "uid text," +
                "customer text," +
                "username text," +
                "strsex text," +
                "workphone text," +
                "yidongphone text," +
                "strqq text," +
                "strweixin text," +
                "strinterest text," +
                "strgrowth text," +
                "strpaixi text," +
                "address text," +
                "degree text," +
                "status text," +
                "pic text," +
                "email text," +
                "relation text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + TIME2 + "' (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "time text)");
    }

//    public int getID() {
//        int id = -1;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("select ttid from " + MYID, null);
//        if (cursor.moveToLast()) {
//            id = cursor.getInt(0);
//        }
//        cursor.close();
//        db.close();
//        return id;
//    }

    public String getUID() {
        String uid = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select uid from " + MYID, null);
        if (cursor.moveToLast()) {
            uid = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return uid;
    }

    public String getNameByUID(String uid) {
        String name = uid+"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from " + USER + " where uid = ?", new String[]{uid});
        if (cursor.moveToLast()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }

    public void putID(int id, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ttid", id);
        values.put("uid", uid);
        db.insert(MYID, null, values);
        db.execSQL("DELETE FROM " + KEHU);
        db.execSQL("DELETE FROM " + LIANXIREN);
        db.execSQL("DELETE FROM " + TIME2);
        db.close();
    }

    public void createLocalTestData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", 1);
        values.put("name", "星网宇达");
        values.put("pri", "1,0");
        values.put("manager", "0");
        values.put("status", "1");
        db.insert(DEPT, null, values);
        values.clear();
        values.put("id", 2);
        values.put("name", "总经理办公室");
        values.put("pri", "1-1,1");
        values.put("manager", "0");
        values.put("status", "1");
        db.insert(DEPT, null, values);
        values.clear();

        values.put("id", 1);
        values.put("uid", "100");
        values.put("name", "迟家升");
        values.put("utouxiang", "");
        values.put("udept", "2-2");
        values.put("status", "1");
        db.insert(USER, null, values);
        values.clear();
        values.put("id", 2);
        values.put("uid", "101");
        values.put("name", "李国盛");
        values.put("utouxiang", "");
        values.put("udept", "2-7");
        values.put("status", "1");
        db.insert(USER, null, values);
        values.clear();
        values.put("id", 4);
        values.put("uid", "103");
        values.put("name", "张志良");
        values.put("utouxiang", "");
        values.put("udept", "2-7");
        values.put("status", "1");
        db.insert(USER, null, values);
        values.clear();
        db.close();

        putID(1, "100");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
