package yh729_ServiceAndThread;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ricky.database.CenterDatabase;

import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import yh729_DB.yh729_LocalDataBase;

/**
 * Created by yan on 2015/8/7.
 */
public class yh729_Notify_Thread extends Thread {
    private boolean flag;
    private yh729_AlarmNotificationService service;
    private WebSocketConnection mConnection;
    private WebSocketConnection censorConnection;
    private String type;

    public yh729_Notify_Thread(yh729_AlarmNotificationService service, WebSocketConnection mConnection, WebSocketConnection censorConnection) {
        this.service = service;
        this.censorConnection=censorConnection;
        this.mConnection = mConnection;
        flag = true;
    }

    public void run() {
        int a = 0;
        Cursor cursor;
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String timetype, cmd="", time="", str="", uid, type = "";
        SQLiteDatabase db=(new yh729_LocalDataBase(service,null)).getDataBase();

//        uid="101";
        boolean test = true;
        while (flag) {
            CenterDatabase cdb=new CenterDatabase(service,null);
            uid=cdb.getUID();
            cdb.close();
            switch (a) {
                case 0:
                    cmd = "mingpian";
                    type = "b_card";
                    cursor = db.rawQuery("select * from remind_time where type = '" + type + "'", null);
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    cursor.close();
                    str =
                            "{\"time\":\"" + time + "\"," +
                                    "\"type\":\"4\"," +
                                    "\"uid\":\""+uid+"\"," +
                                    "\"cmd\":\"" + cmd + "\"}";
                    Log.i("test", str);
                    if (service.comfirmmConnection()) {
                        mConnection.sendTextMessage(str);
                    }
                    break;
                case 1:
                    type = "schedule";
                    cmd = "richeng";
                    cursor = db.rawQuery("select * from remind_time where type = '" + type + "'", null);
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    cursor.close();
                    str =
                            "{\"time\":\"" + time + "\"," +
                                    "\"type\":\"4\"," +
                                    "\"uid\":\""+uid+"\"," +
                                    "\"cmd\":\"" + cmd + "\"}";
                    Log.i("test", str);
                    if (service.comfirmmConnection()) {
                        mConnection.sendTextMessage(str);
                    }
                    break;
                case 2:
                    type = "censor";
                    cmd = "getCensorNotification";
                    cursor = db.rawQuery("select * from remind_time where type = '" + type + "'", null);
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    cursor.close();
                    str =
                            "{\"timestamp\":\"" + time + "\"," +
                                    "\"userId\":\""+uid+"\"," +
                                    "\"cmd\":\"" + cmd + "\"}";
                    Log.i("test", str);
                    if (service.comfirmcensorConnection()) {
                       censorConnection.sendTextMessage(str);
                    }
                    break;
                case 3:
                    type="contract";
                    cursor = db.rawQuery("select * from remind_time where type = 'contract'", null);
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    cursor.close();
                    str =
                            "{\"time\":\"" + time + "\"," +
                                    "\"type\":\"4\"," +
                                    "\"uid\":\""+uid+"\"," +
                                    "\"cmd\":\"hetong\"}";
                    if (service.comfirmmConnection()) {
                        mConnection.sendTextMessage(str);
                    }
                    Log.i("test", str);
                    break;
                case 4:
                    type="group_notify";
                    cursor = db.rawQuery("select * from remind_time where type = 'group_notify'", null);
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    cursor.close();
                    str =
                            "{\"time\":\"" + time + "\"," +
                                    "\"type\":\"4\"," +
                                    "\"uid\":\""+uid+"\"," +
                                    "\"cmd\":\"addnotice\"}";
                    if (service.comfirmmConnection()) {
                        mConnection.sendTextMessage(str);
                    }
                    Log.i("test", str);
                    break;
                default:
                    break;
            }
            try {
                //             uid=cdb.getUID();
                sleep(3000);

               if (test) {
                   JSONObject aaaa=new JSONObject("{\"timestamp\": \"1440666716.75\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"toExecute\", \"datas\": []}, {\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": []}, {\"msgId\": \"careProcess\", \"datas\": [{\"status\": \"backToStart\", \"processId\": \"1\", \"activityId\": \"travel\", \"stepId\": \"start\", \"startTime\": \"1440666456.88\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"res\": \"userTask\", \"opUserId\": \"124\", \"timeout\": 60, \"opUserName\": \"\\u5f20\\u4f1f\\u5cb8\"}]}, {\"msgId\": \"atMeProcess\", \"datas\": []}, {\"msgId\": \"partnerProcess\", \"datas\": []}, {\"msgId\": \"timeoutDeal\", \"datas\": [{\"status\": \"timeout\", \"processId\": \"1\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1440666533.32\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"res\": \"backToStart\", \"opUserId\": \"124\", \"timeout\": 60, \"opUserName\": \"\\u5f20\\u4f1f\\u5cb8\"}]}], \"error\": 0}");
//                   JSONObject kkkk=new JSONObject("{\"timestamp\": \"1440576770.76\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"toExecute\", \"datas\": []}, {\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": [{\"status\": \"timeout\", \"processId\": \"14\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440576756.8\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": \"backToStart\", \"opUserId\": \"186\", \"timeout\": 60, \"opUserName\": \"\\u5df4\\u83ca\\u82b3\"}]}, {\"msgId\": \"careProcess\", \"datas\": []}, {\"msgId\": \"atMeProcess\", \"datas\": []}, {\"msgId\": \"partnerProcess\", \"datas\": []}, {\"msgId\": \"timeoutDeal\", \"datas\": []}], \"error\": 0}");
//                   JSONObject ffff=new JSONObject("{\"timestamp\": \"1440576050.36\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"toExecute\", \"datas\": []}, {\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": []}, {\"msgId\": \"careProcess\", \"datas\": []}, {\"msgId\": \"atMeProcess\", \"datas\": []}, {\"msgId\": \"partnerProcess\", \"datas\": []}, {\"msgId\": \"timeoutDeal\", \"datas\": [{\"status\": \"timeout\", \"processId\": \"12\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440576036.8\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": \"backToStart\", \"opUserId\": \"123\", \"timeout\": 60, \"opUserName\": \"\\u5468\\u4f73\\u9759\"}]}], \"error\": 0}");
//                   JSONObject eeee=new JSONObject("{\"error\":0,\"datas\":[{\"datas\":[{\"startTime\":\"1440489033.34\",\"activityId\":\"waiqinplan\",\"res\":null,\"processId\":\"141\",\"status\":\"unprocessed\",\"opUserName\":\"段素平\",\"stepId\":\"userTask\",\"activityName\":\"外勤计划\",\"timeout\":60,\"opUserId\":\"207\"},{\"startTime\":\"1440489033.34\",\"activityId\":\"waiqinplan\",\"res\":null,\"processId\":\"141\",\"status\":\"unprocessed\",\"opUserName\":\"段素平\",\"stepId\":\"userTask\",\"activityName\":\"外勤计划\",\"timeout\":60,\"opUserId\":\"207\"}],\"msgId\":\"toExecute\"},{\"datas\":[],\"msgId\":\"executed\"},{\"datas\":[],\"msgId\":\"initiated\"},{\"datas\":[{\"startTime\":\"1440489033.34\",\"activityId\":\"waiqinplan\",\"res\":\"userTask\",\"processId\":\"141\",\"status\":\"finished\",\"opUserName\":\"段素平\",\"stepId\":\"userTask\",\"activityName\":\"外勤计划\",\"timeout\":60,\"opUserId\":\"207\"},{\"startTime\":\"1440489033.34\",\"activityId\":\"waiqinplan\",\"res\":\"userTask\",\"processId\":\"141\",\"status\":\"finished\",\"opUserName\":\"段素平\",\"stepId\":\"userTask\",\"activityName\":\"外勤计划\",\"timeout\":60,\"opUserId\":\"207\"}],\"msgId\":\"careProcess\"},{\"datas\":[],\"msgId\":\"atMeProcess\"},{\"datas\":[],\"msgId\":\"partnerProcess\"},{\"datas\":[],\"msgId\":\"timeoutDeal\"}],\"timestamp\":\"1440489037.55\",\"cmd\":\"getCensorNotification\"}");
//                    JSONObject ssss = new JSONObject("{\"timestamp\": \"1439022792.5\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"toExecute\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": [{\"status\": \"unprocessed\", \"processId\": \"34\", \"activityId\": \"travel\", \"startTime\": \"1439022787.71\", \"activityName\": \"\u5dee\u65c5\u6d41\u7a0b\", \"opUserId\": \"186\", \"timeout\": 86400, \"opUserName\": \"\u5df4\u83ca\u82b3\"}]}], \"error\": 0}");
//                    JSONObject aaaa = new JSONObject("{\"delete\": \"\", \"add\": [{\"ps\": \"\\u597d\\u7684\", \"end\": \"2015-09-10 12:12:00\", \"start\": \"2015-09-11 12:12:00\", \"pri\": \"5\", \"creater\": \"101\", \"joiner\": \"100\", \"type\": \"1\", \"id\": 13}], \"cmd\": \"richeng\", \"time\": \"1439180015\", \"error\": \"1\"}");
//                    JSONObject dddd = new JSONObject("{\"timestamp\": \"1439222532.17\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"toExecute\", \"datas\": []}, {\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": [{\"status\": \"finished\", \"processId\": \"1\", \"activityId\": \"distributeTask\", \"stepId\": \"readTask\", \"startTime\": \"1439219827.56\", \"activityName\": \"\\u53d1\\u4efb\\u52a1\", \"opUserId\": \"101\", \"timeout\": 86400, \"opUserName\": \"\\u674e\\u56fd\\u76db\"}, {\"status\": \"finished\", \"processId\": \"1\", \"activityId\": \"distributeTask\", \"stepId\": \"readTask\", \"startTime\": \"1439219827.56\", \"activityName\": \"\\u53d1\\u4efb\\u52a1\", \"opUserId\": \"101\", \"timeout\": 86400, \"opUserName\": \"\\u674e\\u56fd\\u76db\"}, {\"status\": \"finished\", \"processId\": \"1\", \"activityId\": \"distributeTask\", \"stepId\": \"readTask\", \"startTime\": \"1439219827.56\", \"activityName\": \"\\u53d1\\u4efb\\u52a1\", \"opUserId\": \"101\", \"timeout\": 86400, \"opUserName\": \"\\u674e\\u56fd\\u76db\"}, {\"status\": \"finished\", \"processId\": \"1\", \"activityId\": \"distributeTask\", \"stepId\": \"readTask\", \"startTime\": \"1439219827.56\", \"activityName\": \"\\u53d1\\u4efb\\u52a1\", \"opUserId\": \"101\", \"timeout\": 86400, \"opUserName\": \"\\u674e\\u56fd\\u76db\"}]}, {\"msgId\": \"careProcess\", \"datas\": [{\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"start\", \"startTime\": \"1439220490.22\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"122\", \"timeout\": 86400, \"opUserName\": \"\\u6768\\u71d5\\u6587\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"start\", \"startTime\": \"1439220490.22\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"122\", \"timeout\": 86400, \"opUserName\": \"\\u6768\\u71d5\\u6587\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"start\", \"startTime\": \"1439220490.22\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"122\", \"timeout\": 86400, \"opUserName\": \"\\u6768\\u71d5\\u6587\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"start\", \"startTime\": \"1439220490.22\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"122\", \"timeout\": 86400, \"opUserName\": \"\\u6768\\u71d5\\u6587\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220736.32\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"132\", \"timeout\": 86400, \"opUserName\": \"\\u66f9\\u987a\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220736.32\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"132\", \"timeout\": 86400, \"opUserName\": \"\\u66f9\\u987a\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220736.32\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"132\", \"timeout\": 86400, \"opUserName\": \"\\u66f9\\u987a\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220736.32\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"132\", \"timeout\": 86400, \"opUserName\": \"\\u66f9\\u987a\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220562.33\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"207\", \"timeout\": 86400, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220562.33\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"207\", \"timeout\": 86400, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220562.33\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"207\", \"timeout\": 86400, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}, {\"status\": \"finished\", \"processId\": \"2\", \"activityId\": \"travel\", \"stepId\": \"userTask\", \"startTime\": \"1439220562.33\", \"activityName\": \"\\u51fa\\u5dee\\u8ba1\\u5212\", \"opUserId\": \"207\", \"timeout\": 86400, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}]}, {\"msgId\": \"atMeProcess\", \"datas\": []}, {\"msgId\": \"partnerProcess\", \"datas\": []}], \"error\": 0}");
//                    JSONObject bbbb = new JSONObject("{\"lianxiren\": [{\"workphone\": \"6666999\", \"name\": \"\\u5b59\\u6811\\u6770\", \"url\": \"/mingpian//101/1439193380.jpg\", \"company\": \"\\u661f\\u5149\", \"sex\": \"\\u7537\", \"phone\": \"22222222222\", \"id\": 137}], \"cmd\": \"mingpian\", \"time\": \"1439216518\", \"kehu\": [{\"username\": 44, \"id\": 44}], \"error\": \"1\"}");
//                    JSONObject dddd=new JSONObject("{\"timestamp\": \"1440489037.55\", \"cmd\": \"getCensorNotification\", \"datas\": [{\"msgId\": \"toExecute\", \"datas\": [{\"status\": \"unprocessed\", \"processId\": \"141\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440489033.34\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": null, \"opUserId\": \"207\", \"timeout\": 60, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}, {\"status\": \"unprocessed\", \"processId\": \"141\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440489033.34\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": null, \"opUserId\": \"207\", \"timeout\": 60, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}]}, {\"msgId\": \"executed\", \"datas\": []}, {\"msgId\": \"initiated\", \"datas\": []}, {\"msgId\": \"careProcess\", \"datas\": [{\"status\": \"finished\", \"processId\": \"141\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440489033.34\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": \"userTask\", \"opUserId\": \"207\", \"timeout\": 60, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}, {\"status\": \"finished\", \"processId\": \"141\", \"activityId\": \"waiqinplan\", \"stepId\": \"userTask\", \"startTime\": \"1440489033.34\", \"activityName\": \"\\u5916\\u52e4\\u8ba1\\u5212\", \"res\": \"userTask\", \"opUserId\": \"207\", \"timeout\": 60, \"opUserName\": \"\\u6bb5\\u7d20\\u5e73\"}]}, {\"msgId\": \"atMeProcess\", \"datas\": []}, {\"msgId\": \"partnerProcess\", \"datas\": []}, {\"msgId\": \"timeoutDeal\", \"datas\": []}], \"error\": 0}");
//                    service.parseJSON2(kkkk.toString());
                    test = false;
//                    Log.e("test",service.setRemind("2015-08-21 20:10","lalallalal","inside_work",1, yh729_Constant.Repeat_Friday));
            }
            a = (a + 1) % 5;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("test", "NotifyThread:" + e.toString());
            }
        }
    }


    public void Stop() {
        flag = false;
    }
}
