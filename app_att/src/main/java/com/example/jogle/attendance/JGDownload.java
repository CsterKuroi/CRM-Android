package com.example.jogle.attendance;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Jiagao on 2015/8/9.
 */

public class JGDownload {
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private int typejson;
    private WebSocketConnection mConnection;
    private JGDownloadCallBack callBack;

    public JGDownload(JGDownloadCallBack callBack) {
        this.callBack = callBack;
    }
    public String getCommand(int type, String uid, String startTime, String endTime) {
        try {
            JSONObject object = new JSONObject();
            if (type == 0)
                object.put("cmd", "mykaoqin");
            else if (type == 1)
                object.put("cmd", "underkaoqin"); // TODO
            object.put("type", "5");
            object.put("uid", uid);
            object.put("start", startTime);
            object.put("end", endTime);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void down(int type, int uid, String startTime, String endTime) {
        String uidString = (uid < 10) ? "00" + uid :
                (uid < 100) ? "0" + uid : "" + uid;
        String json = getCommand(type, uidString, startTime, endTime);
        typejson = type;
        mConnection = new WebSocketConnection();
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json数据" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json数据" + tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test", "Got echo: " + payload);
                        callBack.downloadCallBack(1, payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        callBack.downloadCallBack(typejson, null);
//                        callBack.downloadCallBack(1, "{\"cmd\": \"underkaoqin\", \"result\": " +
//                                "[{\"content\": \"\\u5443\\u5443\\u5443\", " +
//                                "\"position\": \"\\u5317\\u4eac\\u5e02\\u6d77\\u6dc0\\u533a\\u5b66\\u9662\\u8def43\\u53f7\", " +
//                                "\"type\": \"0\", " +
//                                "\"uid\": \"006\", " +
//                                "\"time\": \"2015\\u5e7408\\u670807\\u65e5 14:57:52\"}, " +
//                                "{\"content\": \"\", \"" +
//                                "position\": \"\\u5317\\u4eac\\u5e02\\u6d77\\u6dc0\\u533a\\u5b66\\u9662\\u8def43\\u53f7\", " +
//                                "\"type\": \"1\", " +
//                                "\"uid\": \"014\", " +
//                                "\"time\": \"2015\\u5e7408\\u670806\\u65e5 15:07:26\"}], " +
//                                "\"error\": \"1\"}");
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }
}
