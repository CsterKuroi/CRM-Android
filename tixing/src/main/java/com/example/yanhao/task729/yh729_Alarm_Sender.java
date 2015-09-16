package com.example.yanhao.task729;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import yh729_ServiceAndThread.yh729_AlarmNotificationService;

/**
 * Created by yan on 2015/8/7.
 */
public class yh729_Alarm_Sender {
    private PendingIntent pi=null;
    private AlarmManager am;
    private SQLiteDatabase db;
    private yh729_AlarmNotificationService service;
    public yh729_Alarm_Sender(yh729_AlarmNotificationService service, SQLiteDatabase db){
        this.db=db;
        this.service=service;
        am = (AlarmManager) service.getSystemService(Context.ALARM_SERVICE);
    }

    public void cancel(){
        if(pi!=null) {
            am.cancel(pi);
        }
    }

    public void sendnext(){
        Cursor c = db.rawQuery("select * from alarm_record order by cast(date_time as bigint)asc", null);
        if(c.getCount()!=0) {
            c.moveToFirst();
            String _id=c.getColumnName(c.getColumnIndex("date_time"));
            db.execSQL("delete  from yh729_alarm_recordAdapter where _id='"+_id+"'");
        }
        c.close();
        send();
    }

    public void send(){
        Cursor c = db.rawQuery("select * from alarm_record order by cast(date_time as DATETIME)asc", null);
        if(c.getCount()!=0) {
            c.moveToFirst();
            Intent intent=new Intent("com.example.yanhao.task729.alarm");
            pi = PendingIntent.getBroadcast(service, 0, intent, 0);
            Calendar calendar = Calendar.getInstance();
            String datetime = c.getString(c.getColumnIndex("date_time"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
            try {
                Date date = sdf.parse(datetime);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);
            Log.e("test","send alarm "+calendar.getTime().toString());
        }
        c.close();
    }
    /*
    private void setAlarm() {
        ArrayList<Calendar> calendars = new ArrayList<>();
        Cursor c = db.query("yh729_alarm_recordAdapter", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                c.move(i);
                Calendar calendar = Calendar.getInstance();
                String datetime = c.getString(c.getColumnIndex("date_time"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
                try {
                    Date date = sdf.parse(datetime);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendars.add(calendar);
            }
        }
        c.close();
        for (int i = 0; i < calendars.size(); i++) {
            Calendar calendar = calendars.get(i);
            Calendar a = Calendar.getInstance();
            if (calendar.compareTo(a) <= 0) {
                //发送广播
                service.sendBroadcast(new Intent("com.example.yanhao.task729.alarm"));
                //从表中删除对应记录
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
                String datetime = sdf.format(calendar.getTime());
                String sql = "DELETE FROM yh729_alarm_recordAdapter WHERE date_time=" + "'" + datetime + "'";
                db.execSQL(sql);
                Log.i("test", db.toString());
            }
        }
    }*/

}