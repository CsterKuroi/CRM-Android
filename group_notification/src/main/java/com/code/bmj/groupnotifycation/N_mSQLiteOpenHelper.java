package com.code.bmj.groupnotifycation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

//import com.mogujie.tt.DB.CenterDatabase;

/**
 * Created by 昊 on 2015/7/30.
 */
public class N_mSQLiteOpenHelper extends SQLiteOpenHelper {

    public static boolean first=true;
    private WebSocketConnection mmConnection = new WebSocketConnection();
    private Boolean isConnected = false;
    private String type11uri = NotificationConfig.BMJ_IP;
    private Context ct;


    public void initgroupnotification(final SQLiteDatabase db) {
        try {
            mmConnection.connect(type11uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                  isConnected = true;
                    JSONObject request2 = new JSONObject();
                    try {

                        CenterDatabase cd = new CenterDatabase(ct, null);
                        String creater_id = cd.getUID();
                        cd.close();
                        request2.put("type", "11");
                        request2.put("uid", creater_id);
                        request2.put("cmd", "updatenotice");
                        mmConnection.sendTextMessage(request2.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    String strUTF8 = null;
                    try {

                        strUTF8 = URLDecoder.decode(payload, "UTF-8");
                        JSONObject root = new JSONObject(strUTF8);
                        JSONArray usercomman = root.getJSONArray("noticelist");

                        CenterDatabase cd = new CenterDatabase(ct, null);
                        String all_joiner_name = "";

                        for (int i = 0; i < usercomman.length(); i++) {
                            JSONObject lan = usercomman.getJSONObject(i);
                            String content = lan.getString("content");
                            String creater = lan.getString("creater");
                            String joiner = lan.getString("joiner");
                            String creater_name = cd.getNameByUID(creater);
                            String type = lan.getString("type");
                            String title = lan.getString("title");
                            String server_id = lan.getString("id");
                            String creat_time = lan.getString("createtime");
                            String[] allid = lan.getString("joiner").split(",");
                            String status = lan.getString("status");
                           // String start_status="";

                            for(int ii =0 ;ii<allid.length;ii++)
                            {
                                all_joiner_name = all_joiner_name + cd.getNameByUID(allid[ii])+",";
                              //  start_status = start_status + "0,";
                            }
                           // start_status.substring(0,start_status.length()-1);

                            all_joiner_name = all_joiner_name.substring(0,all_joiner_name.length()-1);

                            db.execSQL("insert into group_notification " +
                                    "(creatorID,server_id,needconfirm,creatorName,joinerID,joinerName,title,content,create_time,status,type) values" +
                                    "('"+creater+"','"+server_id+"','"+type+"','"+creater_name+"','"+joiner+"','"+all_joiner_name
                                    +"','"+title+"','"+content+"','"+creat_time+"','"+status+"','send')");
                        }
                        cd.close();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                }
            });
        } catch (WebSocketException e) {
        }
    }

    public N_mSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name , factory, version);
        this.ct = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql;
            //定时
            sql = "CREATE TABLE IF NOT EXISTS alarm_record(" +
                    "_id integer primary key autoincrement," +
                    "photo text,"+
                    "repeat text,"+
                    "time text,"+
                    "type text,"+
                    "content text,"+
                    "way text,"+
                    "date_time text," +
                    "scheduleid text)";
            db.execSQL(sql);

            //时间戳
            db.execSQL("CREATE TABLE IF NOT EXISTS remind_time(type TEXT PRIMARY KEY,time text)");
            db.execSQL("insert into remind_time values ('b_card','100')");
            db.execSQL("insert into remind_time values ('schedule','100')");
            db.execSQL("insert into remind_time values ('censor','100')");
            db.execSQL("insert into remind_time values ('contract','100')");


            //日程
            db.execSQL("CREATE TABLE IF NOT EXISTS yh_schedule(" +
                    "_id integer primary key autoincrement," +
                    "scheduleID integer,"+
                    "needconfirm text,"+
                    "photo text,"+
                    "createrID text," +//创建者,删除日程需判断uid=createdid才可以，执行删除任务
                    "aboutID text," +//参与者，某用户本手机登录后，拉取关于自己的日程
                    "scheduleTypeID integer," +
                    "remindID integer," +
                    "nextremindtime text,"+
                    "participant text,"+
                    "issendmail integer,"+
                    "scheduleDate text,"+
                    "scheduleDate2 text," +
                    "priority text," +
                    "scheduleContent text," +
                    "scheduleid2 text,"+
                    "time text," +
                    "read text," +
                    "state text)");

            //审批
            db.execSQL("CREATE TABLE IF NOT EXISTS censor(" +
                    "_id integer primary key autoincrement,"+
                    "processId text," +
                    "activityId text," +
                    "stepId text,"+
                    "opUserName text,"+
                    "opUserId text," +
                    "startTime text," +
                    "timeOut text," +
                    "type text," +
                    "time text,"+
                    "activityName text,"+
                    //"isFinished text,"+
                    "isRead text" +
                    ")");


            //提醒设置
            db.execSQL("create table if not exists remind_setting("+
                    "id integer primary key,"+
                    "notification text,"+
                    "sound text,"+
                    "vibration text,"+
                    "censor_notification text,"+
                    "censor_sound text,"+
                    "censor_vibration text,"+
                    "schedule_notification text,"+
                    "schedule_sound text,"+
                    "schedule_vibration text,"+
                    "diary_notification text,"+
                    "diary_sound text,"+
                    "diary_vibration text)");
            db.execSQL("insert into remind_setting (id,notification,sound,vibration,censor_notification,censor_sound,censor_vibration,schedule_notification,schedule_sound,schedule_vibration,diary_notification,diary_sound,diary_vibration) values"+
                                            "('0','true','true','true','true','true','true','true','true','true','true','true','true')");

            //群通知
            db.execSQL("create table if not exists group_notification("+
                    "_id integer primary key autoincrement,"+
                    "server_id text,"+//存储服务器的id
                    "needconfirm text,"+//通知的类型是否需要去人
                    "creatorID text,"+//创建者id
                    "creatorName text,"+//创建名字
                    "joinerID text,"+//参与者id
                    "joinerName text,"+//参与者姓名
                    "title text,"+//通知标题
                    "content text,"+//通知内容
                    "create_time text,"+//创建时间
                    "receive_time text,"+//接收到时间
                    "confirm_time text,"+//确认时间
                    "read text,"+//是否已读
                    "status text,"+//0:未读；1:已读；2:已确认  对应每个人的状态0,0,1,1,1,0,1,0,2,1
                    "type text"+//"send":我发出的，"receive":我接收的
                    ")");
            initgroupnotification(db);

//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('101','tom','102','jerry,Mary,Bob','cat and mouse','hahahaha6','6','0','0','0,1,2','send')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('102','tomm','102','jerry,Mary,Bob','cat and mouse','hahahaha2','2','0','0','0,1,2','send')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('103','tommm','102','jerry,Mary,Bob','cat and mouse','hahahaha3','3','0','0','0,1,2','send')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('104','tommmm','102','jerry,Mary,Bob','cat and mouse','hahahaha4','4','0','0','0,1,2','send')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('105','tommmmm','102','jerry,Mary,Bob','cat and mouse','hahahaha5','5','0','0','0,1,2','send')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('106','tommmmm','102','jerry,Mary,Bob','cat and mouse','hahahaha5','5','0','0','0,1,2','receive')");
//            db.execSQL("insert into group_notification (creatorID,creatorName,joinerID,joinerName,title,content,create_time,receive_time,confirm_time,status,type) values"+
//                    "('107','tommmmm','102','jerry,Mary,Bob','cat and mouse','hahahaha5','5','0','0','0,1,2','receive')");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
