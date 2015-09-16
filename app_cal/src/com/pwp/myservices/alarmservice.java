package com.pwp.myservices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
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
 * Created by 大源 on 2015/7/31.
 */
public class alarmservice extends Service {
    private UserDAO userdao= null;
    DBOpenHelper dbOpenHelper=new DBOpenHelper(alarmservice.this, "schedules.db");
    private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    private Handler mHandler;
    private final WebSocketConnection mConnection = new WebSocketConnection();
    final String wsuri = "ws://"+ com.pwp.constant.UrlConstant.IP+":8001/ws";
    private ScheduleDAO dao =new ScheduleDAO(this);
   // IMApplication imapp = (IMApplication) getApplication();
    //String authorid=imapp.getUserid();
    String createrID="jjj";
    int scheduleid[];
    String timetocome[];
    Long timetocaome2[];
    Long remindtime[];
    String joiners[];
    String note[];
    String remindid[];
    ArrayList<String> stringList = new ArrayList<String>();
    int firststartflag=0;
    @Override
	public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
	public void onCreate(){
        getschedule();
    }
    @Override
	public int onStartCommand(Intent intent,int flags,int startId) {
       if(firststartflag==0){
           firststartflag=1;
       }
       else{
            for (int jj = 0; jj < remindtime.length; jj++) {
                Intent intent2 = new Intent(alarmservice.this, alarmreceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(alarmservice.this, jj, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.cancel(sender);
            }
        stringList = null;
        getschedule();
    }
        return super.onStartCommand(intent,flags,startId);
    }
    public void getschedule(){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // Cursor cursor = db.rawQuery("select * from schedule where createrid=? or aboutid=?", new String[]{createrID, createrID});
        Cursor cursor = db.rawQuery("select * from schedule",null);
        int tt=cursor.getCount();
        scheduleid=new int[tt];
        timetocome=new String[tt];
        timetocaome2=new Long [tt];
        remindtime=new Long[tt];
        joiners=new String[tt];
        note=new String[tt];
        remindid=new String[tt];
        int i = 0;
        int r;
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.moveToNext()) {

            stringList.clear();
            scheduleid[i] = cursor.getInt(0);
            joiners[i]=cursor.getString(5);
            note[i]=cursor.getString(10);
            remindid[i]=String.valueOf(cursor.getInt(4));
            timetocome[i] = new String(cursor.getString(7));
            try {
                d = sdf.parse(timetocome[i]);
                remindtime[i] = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("timetocome", timetocome[i]);
            Log.d("remindtime", String.valueOf(remindtime[i]));

            Intent intent = new Intent(alarmservice.this, alarmreceiver.class);
           /* intent.putExtra("scheduleid", String.valueOf(scheduleid[jj]));
            intent.putExtra("joiner",joiners[jj]);
            intent.putExtra("note",note[jj]);
            intent.putExtra("remindid",remindid[jj]);*/

            stringList.add(String.valueOf(scheduleid[i]));
            stringList.add(remindid[i]);
            stringList.add(joiners[i]);
            stringList.add(note[i]);
          //  intent.putStringArrayListExtra("ListString", stringList);
            Log.d("传参数：",stringList.toString());
            PendingIntent sender = PendingIntent.getBroadcast(alarmservice.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, remindtime[i], sender);

            i++;
           /* r = cursor.getInt(4);//提醒类型
            if (r == 1) {
            } else if (r == 2) remindtime[i] = (remindtime[i] - 1000 * 60 * 10);
                else if (r == 3) remindtime[i] = (remindtime[i] - 1000 * 60 * 30);
                else remindtime[i] = remindtime[i];*/
           // Log.d("scheduleid", String.valueOf(scheduleid[i]));
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
            Log.d("传参数：",stringList.toString());
            PendingIntent sender = PendingIntent.getBroadcast(alarmservice.this, jj, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, remindtime[jj], sender);*/
        Log.d("currenttime", String.valueOf(System.currentTimeMillis()));
        }
    }
    public void close(){
        this.close();
    }
}