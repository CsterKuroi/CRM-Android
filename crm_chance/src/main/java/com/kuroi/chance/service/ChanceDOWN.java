package com.kuroi.chance.service;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ChanceDOWN {
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;
    private ChanceDownCallBack callBack;
    private String userID;

    public ChanceDOWN(ChanceDownCallBack callBack, String userID) {
        this.callBack = callBack;
        this.userID = userID;
    }

    public String downJson() {
        try {
            JSONObject object = new JSONObject();

            object.put("cmd", "myopportunity");
            object.put("type", "8");
            object.put("userID", userID);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void down(String json) {
        mConnection = new WebSocketConnection();
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json字段" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段" + tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test", "Got echo: " + payload);
                        callBack.downCallBack(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2)
                            callBack.downCallBack("{\"cmd\": \"myjihui\", \"jihui\": [], \"error\": \"2\"}");
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }
}
