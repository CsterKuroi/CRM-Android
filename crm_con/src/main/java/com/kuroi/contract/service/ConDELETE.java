package com.kuroi.contract.service;

import android.util.Log;

import com.kuroi.contract.model.Contract;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ConDELETE {
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;
    private ConDeleteCallBack callBack;
    private String userID;

    public ConDELETE(ConDeleteCallBack callBack, String userID) {
        this.callBack = callBack;
        this.userID = userID;
    }


    public String deleteJson(Contract contract) {
        try {
            JSONObject object = new JSONObject();

            String hetongid = String.valueOf(contract.getId());

            object.put("cmd", "shanchuhetong");
            object.put("type", "8");
            object.put("userID", userID);
            object.put("hetongid", hetongid);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void delete(String json) {
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
                        Log.e("test", "发送Json字段"+tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test", "Got echo: " + payload);
                        JSONObject object = null;
                        String s = null;
                        try {
                            object = new JSONObject(payload);
                            s = object.getString("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.deleteCallBack(s);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2)
                            callBack.deleteCallBack("2");
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }
}
