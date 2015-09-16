package com.pwp.myservices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.dao.UserDAO;
import com.pwp.vo.ScheduleDateTag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;

/**
 * Created by ��Դ on 2015/8/3.
 */
public  class alarmget {
    static  private UserDAO userdao= null;
    static DBOpenHelper dbOpenHelper;
    static  private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    static  private Handler mHandler;
    static   private final WebSocketConnection mConnection = new WebSocketConnection();
    final String wsuri = "ws://"+ com.pwp.constant.UrlConstant.IP+":8001/ws";
    static private ScheduleDAO dao;
    //IMApplication imapp;
    //String authorid=imapp.getUserid();
    static  String createrID="jjj";
    static int scheduleid[];
    static String starttime[];
    static Long timetocaome2[];
    static  Long starttime2[];
    static String joiners[];
    static String note[];
    static String remindid[];
    static String nextremindtime[];
    static ArrayList<String> stringList = new ArrayList<String>();
    static   int firststartflag=0;
    public  alarmget(Context context){
        dbOpenHelper=new DBOpenHelper(context, "schedules.db");
        dao =new ScheduleDAO(context);
        //imapp = (IMApplication) ((Activity)context).getApplication();  //authorid=imapp.getUserid();
        getschedule(context);

    }
    public static void getschedule(Context context){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // Cursor cursor = db.rawQuery("select * from schedule where createrid=? or aboutid=?", new String[]{createrID, createrID});
        Cursor cursor = db.rawQuery("select * from schedule where state!=-1",null);
        int tt=cursor.getCount();
        scheduleid=new int[tt];
        starttime=new String[tt];
        timetocaome2=new Long [tt];
        starttime2=new Long[tt];
        joiners=new String[tt];
        note=new String[tt];
        remindid=new String[tt];
        nextremindtime=new String[tt];
        int i = 0;
        int r;
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.moveToNext()) {
            stringList.clear();

            scheduleid[i] = cursor.getInt(0);
            remindid[i]=String.valueOf(cursor.getInt(4));
            nextremindtime[i]=String.valueOf(cursor.getInt(5));
            joiners[i]=cursor.getString(6);
            starttime[i] = new String(cursor.getString(8));
            note[i]=cursor.getString(11);

            try {
                d = sdf.parse(starttime[i]);
                starttime2[i] = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("timetocome", starttime[i]);
            Log.d("remindtime", String.valueOf(starttime2[i]));

            Intent intent = new Intent(context, alarmreceiver.class);
           /* intent.putExtra("scheduleid", String.valueOf(scheduleid[jj]));
            intent.putExtra("joiner",joiners[jj]);
            intent.putExtra("note",note[jj]);
            intent.putExtra("remindid",remindid[jj]);*/

            stringList.add(String.valueOf(scheduleid[i]));
            stringList.add(remindid[i]);
            stringList.add(joiners[i]);
            stringList.add(note[i]);
            //  intent.putStringArrayListExtra("ListString", stringList);
            Log.d("��������",stringList.toString());
            PendingIntent sender = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, starttime2[i], sender);

            i++;
        }
       /*  Long temp;
        int temp2;
       for (int ii = remindtime.length - 1; ii > 0; --ii) {
            for (int j = 0; j < ii; ++j) {
                if (remindtime[j + 1] < remindtime[j]) {
                    temp = remindtime[j];
                    remindtime[j] = remindtime[j + 1];
                    remindtime[j + 1] = temp;
                    temp2 = scheduleid[j];
                    scheduleid[j] = scheduleid[j + 1];
                    scheduleid[j + 1] = temp2;
                }
            }
        }*/
        for (int jj = 0; jj <scheduleid.length; jj++) {
          /*  Log.d("scheduleid", String.valueOf(scheduleid[jj]));
            Log.d("timetocome", timetocome[jj]);*/
         /*   Intent intent = new Intent(alarmservice.this, alarmreceiver.class);
           *//* intent.putExtra("scheduleid", String.valueOf(scheduleid[jj]));
            intent.putExtra("joiner",joiners[jj]);
            intent.putExtra("note",note[jj]);
            intent.putExtra("remindid",remindid[jj]);*//*
            stringList.add(String.valueOf(scheduleid[jj]));
            stringList.add(remindid[jj]);
            stringList.add(joiners[jj]);
            stringList.add(note[jj]);
            intent.putStringArrayListExtra("ListString", stringList);
            Log.d("��������",stringList.toString());
            PendingIntent sender = PendingIntent.getBroadcast(alarmservice.this, jj, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, remindtime[jj], sender);*/
            Log.d("currenttime", String.valueOf(System.currentTimeMillis()));
        }
    }





}
