package yh729_ServiceAndThread;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.yanhao.task729.yh729_Constant;
import yh729_DB.yh729_LocalDataBase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yan on 2015/8/7.
 */
public class yh729_Alarm_Thread extends Thread {
    private yh729_AlarmNotificationService service;
    private SQLiteDatabase db;
    private boolean flag;

    public yh729_Alarm_Thread(yh729_AlarmNotificationService service) {
        this.service = service;
        db = (new yh729_LocalDataBase(service,null)).getDataBase();
        flag = true;
    }

    public void run() {
        while (flag) {
            try {
                Log.i("test", "AlarmThread is running");
                setAlarm();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAlarm() {
        Cursor c = db.rawQuery("select * from alarm_record order by CAST (date_time AS bigint)", null);
        Calendar a = Calendar.getInstance();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Calendar calendar = Calendar.getInstance();
            Long datetime = c.getLong(c.getColumnIndex("date_time"));
            calendar.setTime(new Date(datetime));
            if (calendar.compareTo(a) <= 0) {
                calendar.set(Calendar.YEAR,a.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH,a.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, a.get(Calendar.DAY_OF_MONTH));
                switch (c.getInt(c.getColumnIndex("repeat"))) {
                    case yh729_Constant.Repeat_Once:
                        remind(c);
                        String sql = "DELETE FROM alarm_record WHERE _id='" + c.getString(c.getColumnIndex("_id")) + "'";
                        db.execSQL(sql);
                        break;
                    case yh729_Constant.Repeat_EveryDay:
                        remind(c);
                        db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");
                        break;
                    case yh729_Constant.Repeat_WorkDay:
                        if (a.get(Calendar.DAY_OF_WEEK) == 1)
                            db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");
                        else if (a.get(Calendar.DAY_OF_WEEK) == 7)
                            db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24 * 2) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");
                        else {
                            db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");
                            remind(c);
                        }
                        break;
                    case yh729_Constant.Repeat_Friday:
                        if(a.get(Calendar.DAY_OF_WEEK)==6) {
                            db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24 * 7) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");
                            remind(c);
                        }
                        else
                            db.execSQL("update alarm_record set date_time='" + (calendar.getTime().getTime() + 3600 * 1000 * 24*(5-(6-a.get(Calendar.DAY_OF_WEEK)))) + "' where _id='" + c.getString(c.getColumnIndex("_id")) + "'");

                }
            } else {
                break;
            }
        }
        c.close();
    }

    public void Stop() {
        flag = false;
    }

    private void remind(Cursor c) {
        switch (c.getString(c.getColumnIndex("type"))) {
            case "schedule":
                if(c.getInt(c.getColumnIndex("way"))==0)
                {
                    Log.e("test","Alarm_Thread remind :are you kidding me!?");
                }
                else if(c.getInt(c.getColumnIndex("way"))==1) {
                    Intent intent=new Intent("com.example.yanhao.task729.alarm");
                    intent.putExtra("type","schedule");
                    intent.putExtra("content","");
                    service.sendBroadcast(intent);
                }
                break;
            case "outside_work":
                if (c.getInt(c.getColumnIndex("way")) == 0)
                    service.showNotification("外勤提醒", c.getString(c.getColumnIndex("content")), new Bundle(), "outside_work");
                else if(c.getInt(c.getColumnIndex("way"))==1) {
                    Intent intent=new Intent("com.example.yanhao.task729.alarm");
                    intent.putExtra("type","outside_work");
                    intent.putExtra("content",c.getString(c.getColumnIndex("content")));
                    service.sendBroadcast(intent);
                }
                break;
            case "inside_work":
                if (c.getInt(c.getColumnIndex("way")) == 0)
                    service.showNotification("内勤提醒", c.getString(c.getColumnIndex("content")), new Bundle(), "inside_work");
                else if(c.getInt(c.getColumnIndex("way"))==1) {
                    Intent intent=new Intent("com.example.yanhao.task729.alarm");
                    intent.putExtra("type","inside_work");
                    intent.putExtra("content",c.getString(c.getColumnIndex("content")));
                    service.sendBroadcast(intent);
                }
                break;
            case "approval":
                if (c.getInt(c.getColumnIndex("way")) == 0)
                    service.showNotification("审批提醒", c.getString(c.getColumnIndex("content")), new Bundle(), "approval");
                else if(c.getInt(c.getColumnIndex("way"))==1) {
                    Intent intent=new Intent("com.example.yanhao.task729.alarm");
                    intent.putExtra("type","approval");
                    intent.putExtra("content",c.getString(c.getColumnIndex("content")));
                    service.sendBroadcast(intent);
                }
                break;
            case "care_remind":
                if (c.getInt(c.getColumnIndex("way")) == 0)
                    service.showNotification("关爱提醒", c.getString(c.getColumnIndex("content")), new Bundle(), "care_remind");
                else if(c.getInt(c.getColumnIndex("way"))==1) {
                    Intent intent=new Intent("com.example.yanhao.task729.alarm");
                    intent.putExtra("type","care_remind");
                    intent.putExtra("content",c.getString(c.getColumnIndex("content")));
                    service.sendBroadcast(intent);
                }
                break;
        }
    }
}
