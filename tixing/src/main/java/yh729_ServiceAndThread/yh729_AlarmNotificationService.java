package yh729_ServiceAndThread;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.code.bmj.groupnotifycation.GroupNotificationMainActivity;
import com.example.jogle.attendance.JGMain2Activity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Alarm_Sender;
import com.example.yanhao.task729.yh729_Constant;
import com.kuroi.contract.activity.ConMainActivity;
import com.melnykov.fab.sample.shibie.crmresultofshibie;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import care.care_main;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import yh729_DB.yh729_DBUtil;
import yh729_DB.yh729_LocalDataBase;
import yh729_Fragment.yh729_mFragment;
import yh729_activity.yh729_MainActivity;

/**
 * Created by 昊 on 2015/7/29.
 */
public class yh729_AlarmNotificationService extends Service {

    private final String spuri = "ws://101.200.189.127:1234/ws";
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    public static yh729_AlarmNotificationService mService;
    //    private final String spuri ="ws://192.168.191.1:1234/ws";//审批
//    private final String wsuri = "ws://192.168.50.11:8000/ws";//yun
    //    private final String wsuri = "ws://192.168.50.101:8001/ws";//F217_2.4.
    private WebSocketConnection mConnection;
    private WebSocketConnection censorConnection;
    private int notificationid = 0;
    private android.support.v7.app.NotificationCompat.Builder mBuilder;
    private yh729_Notify_Thread notification;
    private yh729_Alarm_Sender yh729Alarm_sender;
    private yh729_Alarm_Thread alarm;
    private SQLiteDatabase db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification = new yh729_Notify_Thread(this, mConnection, censorConnection);
        alarm = new yh729_Alarm_Thread(this);
        alarm.start();
        notification.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            db = (new yh729_LocalDataBase(getApplicationContext(), null)).getDataBase();
            mConnection = new WebSocketConnection();
            censorConnection = new WebSocketConnection();

            /*yh729Alarm_sender = new yh729_Alarm_Sender(this, db);
            resend();*/
            iniConstant();
            mService = this;
        } catch (Exception e) {
            Log.e("test", e.toString());
        }
    }

    /*
    * 判断mConnection(负责发送日程和名片的请求)的连接状态，如果没连接则重新连接，返回isConnected的值
    * */
    public synchronized boolean comfirmmConnection() {
        CenterDatabase cdb=new CenterDatabase(getApplicationContext(),null);
        final String uid=cdb.getUID();
        cdb.close();
//        final String uid = "101";
        if (mConnection.isConnected())
            return true;
        else {
            try {
                mConnection.connect(wsuri, new WebSocketHandler() {
                    public void onOpen() {
                        String t = "";
                        Cursor c = db.query("remind_time", null, null, null, null, null, null);//第一个条目是名片
                        if (c.moveToFirst()) {
                            t = c.getString(c.getColumnIndex("time"));
                        }
                        c.close();
                        String str = "{\"time\":\"" + t + "\"," +
                                "\"uid\":\"" + uid + "\"," +
                                "\"type\":\"4\"," +
                                "\"cmd\":\"mingpian\"}";
                        Log.e("test", "发送Json字段" + str);
                        mConnection.sendTextMessage(str);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        parseJSON2(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
//                        Log.e("test", "Connection lost."+code+" "+reason);
                    }
                });
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
            return mConnection.isConnected();
        }
    }


    /*
    * 判断censorConnection(负责发送审批请求)的连接状态，如果没连接则重新连接，返回isConnected的值
    * */
    public synchronized boolean comfirmcensorConnection() {
        CenterDatabase cdb=new CenterDatabase(getApplicationContext(),null);
        final String uid=cdb.getUID();
        cdb.close();
//        final String uid = "101";
        if (censorConnection.isConnected())
            return true;
        else {
            try {
                censorConnection.connect(spuri, new WebSocketHandler() {
                    public void onOpen() {
                        String t = "";
                        Cursor c = db.rawQuery("select * from remind_time where type ='censor'", null);
                        if (c.moveToFirst()) {
                            t = c.getString(c.getColumnIndex("time"));
                        }
                        c.close();
                        String str = "{\"timestamp\":\"" + t + "\"," +
                                "\"userId\":\"" + uid + "\"," +
                                "\"cmd\":\"getCensorNotification\"}";
                        Log.e("test", "发送Json字段" + str);
                        censorConnection.sendTextMessage(str);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        parseJSON2(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.e("test", "Connection lost." + code + " " + reason);
                    }
                });
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
            return censorConnection.isConnected();
        }
    }

    /*
    * 解析接收到的jason数据，并写入数据库，发出notification
    * */
    public void parseJSON2(String jsonData) {
        Log.e("test", "get jason:" + jsonData);
        try {
            String time = "";
            String cmd = "";
            String error = "";
            String sql = "";
            String temp = "";
            Bundle bundle = new Bundle();
            boolean changed = false;

            JSONObject jsonObject = new JSONObject(jsonData);
            if (!jsonObject.isNull("time")) {
                time = jsonObject.getString("time");
            }
            if (!jsonObject.isNull("timestamp")) {
                time = jsonObject.getString("timestamp");
            }
            if (!jsonObject.isNull("cmd")) {
                cmd = jsonObject.getString("cmd");
            }

            String lastmessage = "";

            switch (cmd) {
                //日程
                case "richeng":
                    db.execSQL("UPDATE remind_time SET time = '" + time + "' WHERE type = 'schedule' ");
                    String[] delete = new String[]{};
                    JSONArray add = new JSONArray();
                    if (!jsonObject.isNull("delete")) {
                        temp = jsonObject.getString("delete");
                        if (!temp.equals(""))
                            delete = temp.split("-");
                    }
                    if (!jsonObject.isNull("add")) {
                        temp = jsonObject.getString("add");
                        if (!temp.equals(""))
                            add = jsonObject.getJSONArray("add");
                    }

                    //处理delete的操作
                    try {
                        for (String id : delete) {
                            if (!id.equals("")) {
                                Cursor c = db.rawQuery("select * from yh_schedule where scheduleid2='" + id + "'", null);
                                c.moveToFirst();
                                if (c.getCount() > 0) {
                                    showNotification(yh729_Constant.SCHEDULE_REMIND, "删除了日程:" + c.getString(c.getColumnIndex("scheduleContent")), bundle, "schedule");
                                    db.execSQL("DELETE FROM yh_schedule WHERE scheduleid2 = '" + id + "'");
                                    changed = true;
                                }
                                c.close();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("test", "delete schedule" + e.toString());
                    }

                    //处理add的操作
                    try {
                        for (int i = 0; i < add.length(); i++) {
                            JSONObject object = add.getJSONObject(i);
                            String scheduleid2 = object.getString("id");
                            String type = object.getString("type");
                            String creator = object.getString("creater");
                            String joiner = object.getString("joiner");
                            String start = object.getString("start");
                            String end = object.getString("end");
                            String ps = object.getString("ps");
                            String pri = object.getString("pri");

                            //插入数据库
                            ContentValues cv = new ContentValues();
                            cv.put("scheduleContent", ps);
                            cv.put("scheduleid2", scheduleid2);
                            cv.put("createrID", creator);
                            cv.put("aboutID", joiner);
                            cv.put("scheduleTypeID", type);
                            cv.put("scheduleDate", start);
                            cv.put("scheduleDate2", end);
                            cv.put("priority", pri);
                            cv.put("read", "false");
                            cv.put("time", time);
                            db.insert("yh_schedule", null, cv);//执行插入操作
                            showNotification(yh729_Constant.SCHEDULE_REMIND, "添加了新日程:" + start + ps, bundle, "schedule");
                            Cursor cur2 = db.rawQuery("select * from yh_schedule", null);
                            cur2.moveToLast();
                            int id = cur2.getInt(0);
                            db.execSQL("update yh_schedule set scheduleID='" + id + "' where _id='" + id + "'");
                            cur2.close();
                            changed = true;
                        }
                    } catch (Exception e) {
                        Log.e("test", "add schedule " + e.toString());
                    }
                    if (changed)
                        UpdateMyremind("schedule");
                    break;


                //名片
                case "mingpian":
                    db.execSQL("UPDATE remind_time SET time = '" + time + "' WHERE type = 'b_card' ");
                    bundle.putString("res", jsonData);
                    JSONArray contact = new JSONArray();
                    if (!jsonObject.isNull("lianxiren")) {
                        temp = jsonObject.getString("lianxiren");
                        if (!temp.equals("")) {
                            contact = jsonObject.getJSONArray("lianxiren");
                        }
                    }
                    for (int index = 0; index < contact.length(); index++) {
                        JSONObject person = contact.getJSONObject(index);
                        String name = person.getString("name");
                        showNotification("名片处理", name + "的名片信息已更新！", bundle, "b_card");
                    }
                    if (!jsonObject.isNull("kehu")) {
                        temp = jsonObject.getString("kehu");
                        if (!temp.equals("")) {
                            contact = jsonObject.getJSONArray("kehu");
                        }
                    }
                    for (int index = 0; index < contact.length(); index++) {
                        JSONObject person = contact.getJSONObject(index);
                        String name = person.getString("name");
                        showNotification("名片处理", name + "的客户信息已更新！", bundle, "b_card");
                    }
                    break;


                //审批
                case "getCensorNotification":
                    db.execSQL("UPDATE remind_time SET time = '" + time + "' WHERE type = 'censor' ");
                    JSONArray datas = new JSONArray();
                    if (!jsonObject.isNull("error")) {
                        error = jsonObject.getString("error");
                    }
                    //处理datas
                    if (!jsonObject.isNull("datas")) {
                        temp = jsonObject.getString("datas");
                        if (!temp.equals("")) {
                            datas = jsonObject.getJSONArray("datas");
                        }
                    }

                    //json数组datas内部数据
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject object = datas.getJSONObject(i);
                        JSONArray datas_in = new JSONArray();
                        String msgId = "";
                        //处理msgId字段
                        if (!object.isNull("msgId")) {
                            msgId = object.getString("msgId");

                            //处理datas字段
                            if (!object.isNull("datas")) {
                                temp = object.getString("datas");
                                if (!temp.equals("")) {
                                    datas_in = object.getJSONArray("datas");
                                }
                            }
                            //处理jason数组datas_in内部数据
                            for (int j = 0; j < datas_in.length(); j++) {
                                JSONObject in = datas_in.getJSONObject(j);
                                String processId = "";
                                String activityId = "";
                                String activityName = "";
                                String opUserName = "";
                                String startTime = "";
                                String timeOut = "";
                                String status =
                                        "";
                                String res = "";
                                String stepId = "";
                                String opUserId = "";

                                //获取相应字段的数据，不存在返回""
                                status = getInfo(in, "status");
                                processId = getInfo(in, "processId");
                                activityId = getInfo(in, "activityId");
                                opUserId = getInfo(in, "opUserId");
                                //isFinished=getInfo(in,"isFinished");
                                stepId = getInfo(in, "stepId");
                                activityName = getInfo(in, "activityName");
                                opUserName = getInfo(in, "opUserName");
                                startTime = getInfo(in, "startTime");
                                timeOut = getInfo(in, "timeOut");
                                res = getInfo(in, "res");

                                bundle.putString("type", msgId);
                                bundle.putString("activityId", activityId);
                                Cursor c = db.rawQuery("select * from censor where processId ='" + processId + "' and activityId ='" + activityId + "' and stepId='" + stepId + "' and opUserId='" + opUserId + "' and type='" + msgId + "'", null);
                                String content = "";
                                content = getContentOfCensor(msgId, res, status, opUserName, activityName, activityId, processId, stepId);
                                /*if(isFinished.equals("true")) {
                                    //删除这条记录
                                    db.execSQL("delete from censor where processId='" + processId + "' and activityId='" + activityId + "'");
                                }
                                else {*/
                                if (c.getCount() == 0) {
                                    yh729_DBUtil.insert(db, "censor", new String[]{"processId", "activityId", "stepId", "opUserName", "startTime", "timeOut", "type", "time", "activityName", "isRead", "opUserId", "res", "content"},
                                            new String[]{processId, activityId, stepId, opUserName, startTime, timeOut, msgId, time, activityName, "false", opUserId, res, content});
                                    showNotification("审批提醒", content, bundle, "censor");
                                    changed = true;
                                }
                                    /*else {
                                        db.execSQL("UPDATE censor SET opUserName = '" + opUserName + "', type = '" + msgId + "' ,stepId ='" + stepId + "' , opUserId='"+opUserId+"' WHERE  activityId= '"+activityId+"' and processId ='"+processId+"' and isRead='false' and time='"+time+"'"+" and opUserId='"+opUserId+"' and stepId ='"+ stepId+"'" );
                                        showNotification("审批提醒", content , bundle, "censor");
                                    }*/
                                // }
                                c.close();
                            }
                        }
                    }
                    if (changed)
                        UpdateMyremind("censor");
                    break;

                //合同
                case "hetong":
                    db.execSQL("UPDATE remind_time SET time = '" + time + "' WHERE type = 'contract' ");
                    JSONArray list = new JSONArray();
                    if (!jsonObject.isNull("hetonglist")) {
                        temp = jsonObject.getString("hetonglist");
                        if (!temp.equals(""))
                            list = jsonObject.getJSONArray("hetonglist");
                    }
                    for (int i = 0; i < list.length(); i++) {
                        String name = getInfo((list.getJSONObject(i)), "name");
                        showNotification("合同提醒", name + "处理完成", bundle, "contract");
                    }
                    break;


                //群通知
                case "addnotice":
                    CenterDatabase cdb=new CenterDatabase(getApplicationContext(),null);
                    db.execSQL("UPDATE remind_time SET time = '" + time + "' WHERE type = 'group_notify' ");
                    JSONArray noticelist = new JSONArray();
                    if (!jsonObject.isNull("noticelist")) {
                        temp = jsonObject.getString("noticelist");
                        if (!temp.equals("")) {
                            noticelist = jsonObject.getJSONArray("noticelist");
                        }
                    }
                    for (int i = 0; i < noticelist.length(); i++) {
                        JSONObject notice = noticelist.getJSONObject(i);
                        String server_id = "";
                        String creatorID = "";
                        String joinerID = "";
                        String title = "";
                        String content = "";
                        String create_time = "";
                        String status = "";
                        String creatorName = "";

                        server_id = getInfo(notice, "id");
                        creatorID = getInfo(notice, "creater");
                        creatorName=cdb.getNameByUID(creatorID);
                        joinerID = getInfo(notice, "joiner");
                        content = getInfo(notice, "content");
                        title = getInfo(notice, "title");
                        create_time = getInfo(notice, "create_time");

                        for(int ii=0;ii<joinerID.split(",").length;ii++)
                            status = status + "0,";
                        status = status.substring(0,status.length()-1);

                       // status = getInfo(notice, "status");
                        String[] allid=joinerID.split(",");
                        String all_joiner_name = "";
                        for(int ii =0 ;ii<allid.length;ii++)
                        {
                            all_joiner_name = all_joiner_name + cdb.getNameByUID(allid[ii])+",";
                        }
                        all_joiner_name = all_joiner_name.substring(0,all_joiner_name.length()-1);
                        Log.e("test",status);
                        yh729_DBUtil.insert(db, "group_notification", new String[]{"server_id", "creatorID", "joinerID", "content", "title", "create_time", "status", "receive_time", "read", "type","creatorName","joinerName"},
                                new String[]{server_id, creatorID, joinerID, content, title, create_time, status, time, "false", "receive",creatorName,all_joiner_name});
                        changed = true;
                        showNotification(creatorName + "发来了一条群通知", title + ":" + content, bundle, "group_notify");
                    }
                    if (changed)
                        UpdateMyremind("group_notify");
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("test", e.toString());
        }
    }

    private String getInfo(JSONObject object, String col) throws JSONException {
        if (!object.isNull(col))
            return object.getString(col);
        return "";
    }

    private void UpdateMyremind(String type) {
        if (yh729_mFragment.now != null) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            msg.setData(bundle);
            yh729_mFragment.now.handler.sendMessage(msg);
        }
    }

    @Override
    public void onDestroy() {
        notification.Stop();
        alarm.Stop();
        yh729_MainActivity.first = true;
        if (mConnection != null && mConnection.isConnected())
            mConnection.disconnect();
        super.onDestroy();
    }

    public void stopService() {
        Log.i("test", "stop service");
        notification.Stop();
        alarm.Stop();
        yh729_MainActivity.first = true;
        stopSelf();
    }

    public void resend() {
        yh729Alarm_sender.cancel();
        yh729Alarm_sender.send();
    }

    public void sendnext() {
        yh729Alarm_sender.sendnext();
    }

    private void initNotificationBuilder(String title, String context, PendingIntent intent) {
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(context) //设置通知栏显示内容
                .setContentIntent(intent) //设置通知栏点击意图
                .setTicker(title + "：" + context);//通知首次出现在通知栏，带上升动画效果的
    }

    public void showNotification(String title, String content, Bundle bundle, String type) {
        if (!yh729_Constant.notification)
            return;
        NotificationManager mNotificationManager;
        Intent intent = new Intent();
        Intent[] a = {intent};
        switch (type) {
            case "schedule":
                bundle.putString("type", yh729_Constant.SCHEDULE_REMIND);
                intent.putExtras(bundle);
                intent.setClass(this, yh729_MainActivity.class);
                break;
            case "censor":
                CenterDatabase cdb = new CenterDatabase(getApplicationContext(), null);
                intent.putExtra("userId", cdb.getUID());
                cdb.close();
                intent.putExtra("type", bundle.getString("type"));
                intent.putExtra("activityId", bundle.getString("activityId"));
                intent.setClass(this, bpmMainActivity.class);
                break;
            case "b_card":
                intent.putExtras(bundle);
                intent.setClass(this, crmresultofshibie.class);
                break;
            case "approval":
                break;
            case "inside_work":
                intent.setClass(this,JGMain2Activity.class);
                break;
            case "outside_work":
                break;
            case "contract":
                intent.setClass(this,ConMainActivity.class);
                break;
            case "group_notify":
                intent.setClass(this,GroupNotificationMainActivity.class);
                break;
            case "care_remind":
                intent.setClass(this,care_main.class);
                break;
        }
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationBuilder(title, content, PendingIntent.getActivities(getApplicationContext(), 0, a, 0));
        Notification notification = mBuilder.build();
        switch (type) {
            case "schedule":
                if (yh729_Constant.schedule_notification) {
                    if (yh729_Constant.schedule_sound && yh729_Constant.sound)
                        notification.defaults |= Notification.DEFAULT_SOUND;
                    if (yh729_Constant.schedule_vibrate && yh729_Constant.vibrate)
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notification.defaults |= Notification.DEFAULT_LIGHTS;
                    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                    mNotificationManager.notify(notificationid++, notification);
                }
                break;
            case "censor":
                if (yh729_Constant.censor_notification) {
                    if (yh729_Constant.censor_sound && yh729_Constant.sound)
                        notification.defaults |= Notification.DEFAULT_SOUND;
                    if (yh729_Constant.censor_vibrate && yh729_Constant.vibrate)
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notification.defaults |= Notification.DEFAULT_LIGHTS;
                    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                    mNotificationManager.notify(notificationid++, notification);
                }
                break;
            default:
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notification.defaults |= Notification.DEFAULT_LIGHTS;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                mNotificationManager.notify(notificationid++, notification);
                break;
        }
        Log.e("test", "showNotification：" + type + " " + content);
    }

    private void iniConstant() {
        Cursor c = db.rawQuery("select * from remind_setting where id='0'", null);
        c.moveToFirst();
        yh729_Constant.notification = Boolean.parseBoolean(c.getString(c.getColumnIndex("notification")));
        yh729_Constant.sound = Boolean.parseBoolean(c.getString(c.getColumnIndex("sound")));
        yh729_Constant.vibrate = Boolean.parseBoolean(c.getString(c.getColumnIndex("vibration")));
        yh729_Constant.censor_notification = Boolean.parseBoolean(c.getString(c.getColumnIndex("censor_notification")));
        yh729_Constant.schedule_notification = Boolean.parseBoolean(c.getString(c.getColumnIndex("schedule_notification")));
        yh729_Constant.diary_notification = Boolean.parseBoolean(c.getString(c.getColumnIndex("diary_notification")));
        yh729_Constant.censor_sound = Boolean.parseBoolean(c.getString(c.getColumnIndex("censor_sound")));
        yh729_Constant.schedule_sound = Boolean.parseBoolean(c.getString(c.getColumnIndex("schedule_sound")));
        yh729_Constant.diary_sound = Boolean.parseBoolean(c.getString(c.getColumnIndex("diary_sound")));
        yh729_Constant.censor_vibrate = Boolean.parseBoolean(c.getString(c.getColumnIndex("censor_vibration")));
        yh729_Constant.schedule_vibrate = Boolean.parseBoolean(c.getString(c.getColumnIndex("schedule_vibration")));
        yh729_Constant.diary_vibrate = Boolean.parseBoolean(c.getString(c.getColumnIndex("diary_vibration")));
        c.close();
    }

    public String getContentOfCensor(String msgId, String res, String status, String opUserName, String activityName, String activityId, String processId, String stepId) {
        Cursor cursor=db.rawQuery("select * from censor where type='"+msgId+"' and activityId='"+activityId+"' and processId='"+processId+"'",null);
        int n=cursor.getCount();
        cursor.close();
        String content = "";
        String type = "";
        switch (activityId) {
            case "travel":
            case "waiqinplan":
            case "jiewu":
            case "jiekuan":
            case "qingjia":
                type = "review";
                break;
            case "travelReport":
            case "waiqinreport":
            case "weekreport":
            case "dayreport":
            case "monthreport":
                type = "log";
                break;
            case "distributeTask":
            case "seasonTask":
            case "monthTask":
            case "yearTask":
                type = "order";
                break;
        }
        switch (msgId) {
            case "toExecute":
                if (!type.equals("order"))
                    content = "您有一条" + opUserName + "发来的" + activityName + "待审批,请在一天内处理完！";
                else {
                    if (stepId.equals("readTask"))
                        content = "您有一项" + opUserName + "发来的" + activityName + "待查看";
//                    else if(stepId.equals("doTask"))
//                        content = "您有一项" + opUserName + "发来的" + activityName + "待执行";
                }
                break;
            case "executed":
                switch (status) {
                    case "finished":
                        switch (res) {
                            case "end":
                                content = opUserName + "批准了您经手的" + activityName + "，审批通过";
                                break;
                            case "abort":
                                content = opUserName + "否决了您经手的" + activityName + "，审批不通过";
                                break;
                            case "":
                            default:
                                content = opUserName + "批准了您经手的" + activityName + "，审批正在进行中";
                                break;
                        }
                        break;
                    case "timeout":
                        switch (res) {
                            case "timeout":
                                content = "您经手的" + activityName + "已过期";
                                break;
                            case "backToStart":
                                content = "由于" + opUserName + "未及时处理您经手的" + activityName + "，审批作废";
                                break;
                            case "":
                                content = "由于" + opUserName + "未及时处理您经手的" + activityName + "，审批回退至上一步";
                                break;
                        }
                        break;
                }
                break;
            case "initiated":
                switch (status) {
                    case "finished":
                        switch (res) {
                            case "end":
                                if (!type.equals("order"))
                                    content = opUserName + "批准了您发出的" + activityName + "，审批通过";
                                else {
                                    if (stepId.equals("readTask"))
                                        content = "您发出的" + activityName + "已被" + opUserName + "查看";
                                    else if (stepId.equals("doTask"))
                                        content = "您发出的" + activityName + "已被" + opUserName + "执行";
                                }
                                break;
                            case "abort":
                                content = opUserName + "否决了您发出的" + activityName + "，审批不通过";
                                break;
                            case "":
                            default:
                                if (!type.equals("order"))
                                    content = opUserName + "批准了您发出的" + activityName + "，审批正在进行中";
                                else {
                                    if (stepId.equals("readTask"))
                                        content = "您发出的" + activityName + "已被" + opUserName + "查看";
                                    else if (stepId.equals("doTask"))
                                        content = "您发出的" + activityName + "已被" + opUserName + "执行";
                                }
                                break;
                        }
                        break;
                    case "timeout":
                        switch (res) {
                            case "timeout":
                                content = "您发出的" + activityName + "已过期";
                                break;
                            case "backToStart":
                                if (!type.equals("order"))
                                    content = "由于" + opUserName + "未及时处理您发出的" + activityName + "，审批作废";
                                else
                                    content = "由于" + opUserName + "未及时处理您发出的" + activityName + "，任务作废";
                                break;
                            case "":
                                content = "由于" + opUserName + "未及时处理您发出的" + activityName + "，审批回退至上一步";
                                break;
                        }
                        break;
                }
                break;
            case "careProcess":
                switch (status) {
                    case "finished":
                        switch (res) {
                            case "end":
                                if (!type.equals("order"))
                                    content = opUserName + "批准了您下属发出的" + activityName + "，审批通过";
                                else {
                                    if (stepId.equals("readTask"))
                                        content = "您下属发出的" + activityName + "已被" + opUserName + "查看";
                                    else if (stepId.equals("doTask"))
                                        content = "您下属发出的" + activityName + "已被" + opUserName + "执行";
                                }
                                break;
                            case "abort":
                                content = opUserName + "否决了您下属发出的" + activityName + "，审批不通过";
                                break;
                            case "":
                            default:
                                if (!type.equals("order"))
                                    content = "您的下属" + opUserName + "发出了一份" + activityName + "，审批正在进行中";
                                else {
                                    switch (stepId) {
                                        case "start":
                                            content = "您的下属" + opUserName + "发出了一份" + activityName;
                                            break;
                                        case "readTask":
                                            content = opUserName + "查看了您下属发出的" + activityName;
                                            break;
                                        case "doTask":
                                            content = opUserName + "执行了您下属发出的" + activityName;
                                            break;
                                    }
                                }
                        }
                        break;
                    case "timeout":
                        switch (res) {
                            case "cancelled":
                                content = "您的下属撤回了其发出的" + activityName + "";
                                break;
                            case "timeout":
                                content = "您下属发出的" + activityName + "已过期";
                                break;
                            case "backToStart":
                                if (!type.equals("order"))
                                    content = "由于" + opUserName + "未及时处理您下属发出的" + activityName + "，审批作废";
                                else
                                    content = "由于" + opUserName + "未及时处理您下属发出的" + activityName + "，任务作废";
                                break;
                            case "":
                                content = "由于" + opUserName + "未及时处理您下属发出的" + activityName + "，审批回退至上一步";
                                break;
                        }
                        break;
                }
                break;
            case "atMeProcess":
                switch (status) {
                    case "finished":
                        switch (res) {
                            case "end":
                                if (n == 0) {
                                    if (!type.equals("order"))
                                        content = opUserName + "抄送给了您一份" + activityName + "，审批通过";
                                    else {
                                        switch (stepId) {
                                            case "start":
                                                content = opUserName + "抄送给您了一份" + activityName;
                                                break;
                                            case "readTask":
                                                content = opUserName + "抄送给您了一份" + activityName + "，已查看";
                                                break;
                                            case "doTask":
                                                content = opUserName + "抄送给您了一份" + activityName + "，已完成";
                                                break;
                                        }
                                    }
                                } else {
                                    if (!type.equals("order"))
                                        content = opUserName + "批准了抄送给您的" + activityName + "，审批通过";
                                    else {
                                        if (stepId.equals("readTask"))
                                            content = opUserName + "查看了抄送给您的" + activityName;
                                        else if (stepId.equals("doTask"))
                                            content = opUserName + "执行了抄送给您的" + activityName;
                                    }
                                }
                                break;
                            case "abort":
                                if (n == 0)
                                    content = opUserName + "抄送给了您一份" + activityName + "，审批不通过";
                                else
                                    content = opUserName + "否决了抄送给您的" + activityName + "，审批不通过";
                                break;
                            case "":
                            default:
                                if (n == 0) {
                                    if (!type.equals("order"))
                                        content = opUserName + "抄送给了您一份" + activityName + "，审批通过";
                                    else {
                                        switch (stepId) {
                                            case "start":
                                                content = opUserName + "抄送给您了一份" + activityName;
                                                break;
                                            case "readTask":
                                                content = opUserName + "抄送给您了一份" + activityName + "，已查看";
                                                break;
                                            case "doTask":
                                                content = opUserName + "抄送给您了一份" + activityName + "，已完成";
                                                break;
                                        }
                                    }
                                } else {
                                    if (!type.equals("order"))
                                        content = opUserName + "批准了抄送给您的" + activityName + "，审批正在进行中";
                                    else {
                                        if (stepId.equals("readTask"))
                                            content = opUserName + "查看了抄送给您的" + activityName;
                                        else if (stepId.equals("doTask"))
                                            content = opUserName + "执行了抄送给您的" + activityName;
                                    }
                                }
                                break;
                        }
                        break;
                    case "timeout":
                        switch (res) {
                            case "cancelled":
                                if (n == 0)
                                    content = opUserName + "抄送给了您一份" + activityName + "，审批被撤回";
                                else
                                    content = "抄送给您的" + activityName + "被发起人撤回";
                                break;
                            case "timeout":
                                content = "抄送给您的" + activityName + "已过期";
                                break;
                            case "backToStart":
                                if (!type.equals("order"))
                                    content = "由于" + opUserName + "未及时处理抄送给您的" + activityName + "，审批作废";
                                else
                                    content = opUserName + "未及时处理抄送给您的" + activityName + "，任务作废";
                                break;
                            case "":
                                if (!type.equals("order"))
                                    content = "由于" + opUserName + "未及时处理抄送给您的" + activityName + "，审批回退至上一步";
                                else
                                    content = opUserName + "未及时处理抄送给您的" + activityName;
                                break;
                        }
                        break;
                }
                break;
            case "partnerProcess":
                switch (status) {
                    case "finished":
                        switch (res) {
                            case "end":
                                content = opUserName + "批准了您作为同行人的" + activityName + "，审批通过";
                                break;
                            case "abort":
                                content = opUserName + "否决了您作为同行人的" + activityName + "，审批不通过";
                                break;
                            case "":
                            default:
                                content = opUserName + "批准了您作为同行人的" + activityName + "，审批正在进行中";
                                break;
                        }
                        break;
                    case "timeout":
                        switch (res) {
                            case "cancelled":
                                content = "您作为同行人的" + activityName + "被发起人撤回";
                                break;
                            case "timeout":
                                content = "您作为同行人的" + activityName + "已过期";
                                break;
                            case "backToStart":
                                content = "由于" + opUserName + "未及时处理您作为同行人的" + activityName + "，审批作废";
                                break;
                            case "":
                                content = "由于" + opUserName + "未及时处理您作为同行人的" + activityName + "，审批回退至上一步";
                                break;
                        }
                        break;
                }
                break;
            case "timeoutDeal":
                switch (status) {
                    case "timeout":
                        switch (res) {
                            case "cancelled":
                                content = "发送给您的" + activityName + "被发起人撤回";
                                break;
                            case "timeout":
                                if (!type.equals("order"))
                                    content = "由于您未及时处理，发送给您的" + activityName + "已过期";
                                else {
                                    if (stepId.equals("readTask"))
                                        content = opUserName + "发出的" + activityName + "未被及时查看，" + activityName + "过期";
                                    else if (stepId.equals("doTask"))
                                        content = opUserName + "发出的" + activityName + "未被及时执行，" + activityName + "过期";
                                }
                                break;
                            case "backToStart":
                                if (!type.equals("order"))
                                    content = "由于" + opUserName + "未及时处理发送给您的" + activityName + "，审批作废";
                                else {
                                    if (stepId.equals("readTask"))
                                        content = opUserName + "发出的" + activityName + "未被及时查看，" + activityName + "作废";
                                    else if (stepId.equals("doTask"))
                                        content = opUserName + "发出的" + activityName + "未被及时执行，" + activityName + "作废";
                                }
                                break;
                            case "":
                                content = "由于" + opUserName + "未及时处理发送给您的" + activityName + "，审批回退至上一步";
                                break;
                        }
                        break;
                }
                break;
        }
        return content;
    }
}
