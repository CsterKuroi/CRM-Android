package com.example.dt.testapp3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dt on 2015/8/8.
 */
public class VisitDB extends SQLiteOpenHelper {

    private final static String DBNAME = "VisitDB";
    private final static int VERSION = 1;

    public VisitDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                   int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {

        //String createDatabase = "create database visitDatabase";
        //db.execSQL(createDatabase);

//        private int id;
//        private String uid;
//        private String submitId;
//        private String submitName;
//        private String date;
//        private int dateInt;
//        private String place;
//        private int companyId;
//        private String company;
//        private int[] targetId;
//        private String[] target;
//        private String todo;
//        private int mytype;
//        private String record;
//        private int result;
//        private String remark;
//        private String tape;
//        private int condition; //等待拜访0/正在拜访1/拜访结束2/已提交记录3/已提交报告4/评价：优5良6中7差8,etc
//        private int[] partnerId;
//        private String[] partnerName;
//        private int processIdLei;
//        private int processIdCui;
//        private boolean isTemp;
        String createTable = "create table if not exists " + "visitTable " + "( " +
                "id integer primary key, " +
                "uid varchar(20), " +
                "submitId varchar(20), " +
                "submitName varchar(40), " +
                "date varchar(10), " +
                "dateInt double, " +       //todo : change to long/string.
                "place varchar(255), " +
                "companyId int, " +
                "company varchar(255), " +
                "targetId varchar(255), " +
                "target varchar(255), " +
                "todo varchar(255), " +
                "type int, " +
                "record text, " +
                "result int, " +
                "remark text, " +
                "tape text, " +
                "condition int, " +
                "partnerId varchar(255), " +
                "partnerName varchar(255), " +
                "pidL int, " +
                "pidC int, " +
                "isTemp int, " +
                "censorId text, " +
                "censorName text " +
                ") ";
        db.execSQL(createTable);
        String createTimeStep = "create table if not exists timeStep ( name varchar(50), timeStep int )";
        db.execSQL(createTimeStep);

//        type : 6
//        cmd : baifangkehu
//
//        back
//        cmd : baifangkehu
//        error 1-normal 2-error
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    public int getLocalVisitTimeStep() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor timeStep = db.query("timeStep", new String[]{"timeStep"},
                "name = ?", new String[]{"visitTimeStep"}, null, null, null);
        if (timeStep.getCount() == 0) {
            db.execSQL("insert into timeStep values ( 'visitTimeStep', 1 )");
            return 1;
        } else {
            return timeStep.getInt(timeStep.getColumnIndex("timeStep"));
        }
    }
}
