package com.pwp.myservices;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.pwp.activity.CalendarActivity;
import com.pwp.constant.UrlConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.dao.UserDAO;
import com.pwp.vo.ScheduleDateTag;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by 大源 on 2015/7/31.
 */
 public class update_user_schedule{
    public LayoutInflater mFactory;
    private UserDAO userdao= null;
    DBOpenHelper dbOpenHelper=null;
    private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    private static Handler mHandler;
    public static WebSocketConnection mConnection = new WebSocketConnection();
	//final String wsuri = "ws://"+ com.pwp.constant.UrlConstant.IP+":8001/ws";
    final  String wsuri= UrlConstant.ACCESS_MSG_ADDRESS;
    private String  TAG="myservice";
    String time="0";
    String time2="0";
    String update_flag="0";
    String update_flag2="0";
    private ScheduleDAO dao;

    //IMApplication imapp =null;
    //String createrID=imapp.getUserid();

    private String userid;
    String URL;

    Context context=null;
    Thread tt=null;
    public static WebSocketHandler wsHandler;
    SQLiteDatabase db;

    public static boolean thread_flag=true;

    public update_user_schedule(Context context,String userid,String url) {
        this.context=context;
        this.userid=userid;
        this.URL=url;
        //Toast.makeText(context, "URL" +userid+ url, Toast.LENGTH_SHORT).show();
        dbOpenHelper=new DBOpenHelper(context, "schedules.db");
        db = dbOpenHelper.getWritableDatabase();
        userdao=new UserDAO(context);
        dao=new ScheduleDAO(context);
       // imapp=(IMApplication) ((Activity)context).getApplication();
        mystart();
    }
    public void mystart(){
        thread_flag=true;
    	//final String wsuri =UrlConstant.ACCESS_MSG_ADDRESS;
       // Toast.makeText(context, "URL2" + URL, Toast.LENGTH_SHORT).show();
            //userdao=new UserDAO(update_user_schedule.this);
             db = dbOpenHelper.getWritableDatabase();
             wsHandler = new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.e(TAG, "Status: Connected to " + URL);
                }
                @Override
                public void onTextMessage(String payload) {
                   Log.e(TAG, "Got echo: " + payload);
                 //Toast.makeText(context, "收到" + payload, Toast.LENGTH_SHORT).show();
                    parseJSON(payload);
                }
                @Override
                public void onClose(int code, String reason) {
                   // Log.d(TAG, "Connection lost.");
                }
            };
            try {
                mConnection.connect(URL, wsHandler);
            } catch (WebSocketException e) {
             //  Toast.makeText(context, e.toString(),Toast.LENGTH_SHORT).show();
                Log.e("ssssssssssssssss", e.toString());
            }
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                      /*  Cursor cursor = db.rawQuery("select * from contacts_update where id=(select max(id) from contacts_update)",null);
                        if(cursor.moveToFirst()){
                            time = cursor.getString(1);
                            //Toast.makeText(context, "提交时间?" + time, Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();*/
                        Cursor cursor = db.rawQuery("select * from schedule_update where userid="+userid,null);
                        Log.e("读取个数：",""+cursor.getCount());
                      //  Toast.makeText(context,"读取个数："+cursor.getCount(), Toast.LENGTH_SHORT).show();
                        if(cursor.getCount()==0){
                            db.execSQL("insert into schedule_update(time,userid) values ('0',"+userid+")");
                          //  Toast.makeText(context,"charu:"+ "ddddddddd", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();

                        Cursor cursor2 = db.rawQuery("select time from schedule_update where id=(select max(id) from (select * from schedule_update where userid="+userid+"))",null);
                        cursor2.moveToFirst();
                        time2=cursor2.getString(0);
                        //Toast.makeText(context,"cursor2读取个数："+cursor2.getCount()+"time2"+time2, Toast.LENGTH_SHORT).show();
                        cursor2.close();
                        try {
                            if (!mConnection.isConnected()) {
                                    mConnection.connect(URL, wsHandler);
                            }
                            if(mConnection.isConnected()){


                            String str1 = "{\"type\":\"1\",\"cmd\":\"1-2\", \"time\":" + time2 + ",\"uid\":\"" + userid + "\"}";
                            Log.e("日程信息请求",str1);
                            mConnection.sendTextMessage(str1);
                           // Toast.makeText(context,"发语句str1:"+ str1, Toast.LENGTH_SHORT).show();

                          /*  String str2 = "{\"type\":\"1\", \"cmd\":\"1\", \"time\":\""+time+"\"}";
                          //  Log.d("联系人请求?",str2);
                           // Toast.makeText(context,"发语句str2:"+ str2, Toast.LENGTH_SHORT).show();
                            mConnection.sendTextMessage(str2);*/
                        }
                        }catch (WebSocketException e) {
                         //   Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                          //  Log.d(TAG, e.toString());
                        }
                    }
                }
            };
           tt= new Thread(new Runnable() {
               @Override
               public void run() {
                       try {
                           do {
                               if(!thread_flag) Thread.interrupted();
                               Thread.sleep(10000);
                               Message message = new Message();
                               message.what = 1;
                               mHandler.sendMessage(message);
                               //Toast.makeText(CalendarActivity.this,"handler启动",Toast.LENGTH_LONG).show();
                           } while (!Thread.interrupted());
                       } catch (InterruptedException e) {
                           Looper.prepare();
                           //     Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
                           Looper.loop();
                       }
                   }

           });
            tt.start();
    }
    public void stopThread() {
       // this .thread_flag = run;
        tt.interrupt();
        mConnection.disconnect();
    }

    private void parseJSON(String jsonData) {
        int i = 0 ;
        int scheduleid = 0 ;
       // Log.d("更新联系人或日程，返回的语句�?",jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String cmd=jsonObject.getString("cmd");
            if (cmd.equals("1")) {
                String change = jsonObject.getString("change");
                if (change.equals("0")) {
                    return;
                } else if (change.equals("1")){
              //     Toast.makeText(context,"更新联系时间?", Toast.LENGTH_SHORT).show();
                    //String updatetime=new DecimalFormat("#").format(jsonObject.getDouble("time"));
                    //String updatetime=String.valueOf(jsonObject.getDouble("time"));
                    String updatetime = jsonObject.getString("time");
                  // Toast.makeText(context, "收到时间?" + updatetime, Toast.LENGTH_SHORT).show();
                    JSONArray result = jsonObject.getJSONArray("result");
                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                    for (i = 0; i < result.length(); i++) {
                        JSONObject jsonObject2 = result.getJSONObject(i);
                        UserDAO.User user = new UserDAO.User();
                        user.setuserid(jsonObject2.getString("uid"));
                        user.setdepartment(jsonObject2.getString("udeptname"));
                        user.setname(jsonObject2.getString("uname"));
                        user.setuserclass(jsonObject2.getString("udutyname"));
                        user.setemail(jsonObject2.getString("uemail"));
                        Cursor cursor = db.rawQuery("select * from user where userID=?", new String[]{user.getuserid()});
                        //Toast.makeText(CalendarActivity.this, "1", Toast.LENGTH_SHORT).show();
                        if (cursor.moveToFirst()) {
                            //Toast.makeText(CalendarActivity.this, "2", Toast.LENGTH_SHORT).show();
                            userdao.updateuser(user);//
                        } else {
                            //Toast.makeText(CalendarActivity.this, "3", Toast.LENGTH_SHORT).show();
                            userdao.saveuser(user);//
                        }
                        cursor.close();
                    }
                    db.execSQL("insert into contacts_update (time,userid) values (?,?) ", new String[]{updatetime,userid});
                    db.close();
                }
            } else if (cmd.equals("1-2")) {
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                String result=jsonObject.getString("result");
                if(result.equals("1")){
                    String updatetime=jsonObject.getString("time");
                   // Toast.makeText(context, "收到时间?" + updatetime, Toast.LENGTH_SHORT).show();
                    db.execSQL("insert into schedule_update (time,userid) values (?,?)", new String[]{updatetime, userid});
                    //String updatetime=new DecimalFormat("#").format(jsonObject.getDouble("time"));
                    JSONArray content=jsonObject.getJSONArray("content");
                    for (i = 0; i < content.length(); i++) {
                        JSONObject jsonObject2 = content.getJSONObject(i);

                        String status=jsonObject2.getString("status");
                        if(status.equals("0")){
                           String sid=jsonObject2.getString("sid");
                            ScheduleDAO   dao = new ScheduleDAO(context);
                            //db.execSQL("DELETE FROM schedule WHERE scheduleid2 =?", new String[]{sid});
                            Cursor cursor = db.rawQuery("select scheduleID from schedule where scheduleid2=?", new String[]{sid});
                            if(cursor.moveToFirst()){
                                dao.delete(Integer.parseInt(cursor.getString(0)));
                                //((Activity)context).finish();
                              //  context.startActivity(new Intent().setClass(context, CalendarActivity.class));
                            }
                        } else{
                        int type=Integer.parseInt(jsonObject2.getString("type"));
                       // int remind=Integer.parseInt(jsonObject2.getString("remind"));
                        int remind=0;
                        String joiner=jsonObject2.getString("joiner");
                          //  Toast.makeText(context,joiner,Toast.LENGTH_SHORT).show();
                        String start=jsonObject2.getString("start");
                        String end=jsonObject2.getString("end");
                        String pri=jsonObject2.getString("pri");
                        String ps=jsonObject2.getString("ps");
                        String creater=jsonObject2.getString("creater");
                        String sid=jsonObject2.getString("sid");

                        ScheduleVO schedulevo = new ScheduleVO();
                        //schedulevo.setScheduleID(Integer.parseInt(scheduleid));
                        //schedulevo.setScheduleID(Integer.parseInt(scheduleid));
                        schedulevo.setcreaterID(creater);
                        schedulevo.setScheduleTypeID(type);
                        schedulevo.setRemindID(remind);
                        schedulevo.setparticipant(joiner);
                        //schedulevo.setissendmail(rb_flag);//单�?�标�? 1选中0未�?�中
                        schedulevo.setScheduleDate(start);
                        schedulevo.setScheduleDate2(end);
                        schedulevo.setpriority(Integer.parseInt(pri));
                        schedulevo.setScheduleContent(ps);
                        schedulevo.setscheduleid2(sid);
                        
                        schedulevo.setaboutID(userid);//接受日程标志
                        dao=new ScheduleDAO(context);
                        scheduleid= dao.save(schedulevo);

                        setScheduleDateTag(0,start.split("-")[0], start.split("-")[1],start.split("-")[2].split(" ")[0], scheduleid,userid);
                       // Toast.makeText(context, start.split("-")[0]+start.split("-")[1]+start.split("-")[2]+start.split("-")[2].split(" ")[0], Toast.LENGTH_SHORT).show();
                        Log.e( "kkkkkkkkkkkkkkkkk",start+","+start.split("-")[0]+","+start.split("-")[1]+","+start.split("-")[2].split(" ")[0]);
                            ((Activity)context).finish();

                    }
                    }

                    //Toast.makeText(context,"有新日程更新！",Toast.LENGTH_SHORT).show();
                    //context.startActivity(new Intent().setClass(context, CalendarActivity.class));
                    time2 = updatetime;

                }
               // db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setScheduleDateTag(int remindID, String year, String month, String day, int scheduleID,String userid) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        String d = year + "-" + month + "-" + day;
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(d));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 封装要标记的日期
        if (remindID >= 0 && remindID <= 4) {
            // "提醒�?�?","�?10分钟","�?30分钟","隔一小时"（只�?标记当前这一天）
            ScheduleDateTag dateTag = new ScheduleDateTag();
            dateTag.setYear(Integer.parseInt(year));
            dateTag.setMonth(Integer.parseInt(month));
            dateTag.setDay(Integer.parseInt(day));
            dateTag.setscheduleID(scheduleID);
            dateTag.setuserid(userid);
            dateTagList.add(dateTag);
        }
		/*else if (remindID == 4) {
			// 每天重复(从设置的日程的开始的之后每一天都要标�?)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4 * 7; i++) {
				if (i == 0) {
					cal.add(Calendar.DATE, 0);
				} else {
					cal.add(Calendar.DATE, 1);
				}
				handleDate(cal, scheduleID);
			}
		}*/
        else if (remindID == 5) {
            // 每周重复(从设置日程的这天(星期�?)，接下来的每周的这一天多要标�?)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4; i++) {
                if (i == 0) {
                    cal.add(Calendar.WEEK_OF_MONTH, 0);
                } else {
                    cal.add(Calendar.WEEK_OF_MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 6) {
            // 每月重复(从设置日程的这天(几月几号)，接下来的每月的这一天多要标�?)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12; i++) {
                if (i == 0) {
                    cal.add(Calendar.MONTH, 0);
                } else {
                    cal.add(Calendar.MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 7) {
            // 每年重复(从设置日程的这天(哪一年几月几�?)，接下来的每年的这一天多要标�?)
            for (int i = 0; i <= 2049 - Integer.parseInt(year); i++) {
                if (i == 0) {
                    cal.add(Calendar.YEAR, 0);
                } else {
                    cal.add(Calendar.YEAR, 1);
                }
                handleDate(cal, scheduleID);
            }
        }
        // 将标记日期存入数据库�?
        dao.saveTagDate(dateTagList);
    }

    public void handleDate(int remindID, String year, String month, String day, int scheduleID) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        String d = year + "-" + month + "-" + day;
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(d));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 封装要标记的日期
        if (remindID >= 0 && remindID <= 4) {
            // "提醒�?�?","�?10分钟","�?30分钟","隔一小时"（只�?标记当前这一天）
            ScheduleDateTag dateTag = new ScheduleDateTag();
            dateTag.setYear(Integer.parseInt(year));
            dateTag.setMonth(Integer.parseInt(month));
            dateTag.setDay(Integer.parseInt(day));
            dateTag.setscheduleID(scheduleID);
            //dateTag.setcreaterID(aid.getuserid());
            dateTagList.add(dateTag);
        }
		/*else if (remindID == 4) {
			// 每天重复(从设置的日程的开始的之后每一天都要标�?)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4 * 7; i++) {
				if (i == 0) {
					cal.add(Calendar.DATE, 0);
				} else {
					cal.add(Calendar.DATE, 1);
				}
				handleDate(cal, scheduleID);
			}
		}*/
        else if (remindID == 5) {
            // 每周重复(从设置日程的这天(星期�?)，接下来的每周的这一天多要标�?)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4; i++) {
                if (i == 0) {
                    cal.add(Calendar.WEEK_OF_MONTH, 0);
                } else {
                    cal.add(Calendar.WEEK_OF_MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 6) {
            // 每月重复(从设置日程的这天(几月几号)，接下来的每月的这一天多要标�?)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12; i++) {
                if (i == 0) {
                    cal.add(Calendar.MONTH, 0);
                } else {
                    cal.add(Calendar.MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 7) {
            // 每年重复(从设置日程的这天(哪一年几月几�?)，接下来的每年的这一天多要标�?)
            for (int i = 0; i <= 2049 - Integer.parseInt(year); i++) {
                if (i == 0) {
                    cal.add(Calendar.YEAR, 0);
                } else {
                    cal.add(Calendar.YEAR, 1);
                }
                handleDate(cal, scheduleID);
            }
        }
        // 将标记日期存入数据库�?
        dao.saveTagDate(dateTagList);
    }
    public void handleDate(Calendar cal, int scheduleID) {
        ScheduleDateTag dateTag = new ScheduleDateTag();
        dateTag.setYear(cal.get(Calendar.YEAR));
        dateTag.setMonth(cal.get(Calendar.MONTH) + 1);
        dateTag.setDay(cal.get(Calendar.DATE));
        dateTag.setuserid(userid);
        dateTag.setscheduleID(scheduleID);
        dateTagList.add(dateTag);
    }


}


