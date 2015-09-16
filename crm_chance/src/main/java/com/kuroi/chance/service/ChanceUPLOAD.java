package com.kuroi.chance.service;

import android.util.Log;

import com.kuroi.chance.model.Chance;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ChanceUPLOAD {
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;
    private ChanceUploadCallBack callBack;
    private String userID;

    public ChanceUPLOAD(ChanceUploadCallBack callBack, String userID) {
        this.callBack = callBack;
        this.userID = userID;
    }

    public String changeArrayDateToJson(Chance chance) {
        try {
            JSONObject object = new JSONObject();

            String jihuiid = String.valueOf(chance.getId());
            String number=chance.getNumber();
            String name=chance.getName();
            String mytype = chance.getType();
            String customer=chance.getCustomer();
            String date=chance.getDate();
            String dateStart=chance.getDateStart();
            String dateEnd=chance.getDateEnd();
            String money=chance.getMoney();
            String discount=chance.getDiscount();
            String principal=chance.getPrincipal();
            String ourSigner=chance.getOurSigner();
            String cusSigner=chance.getCusSigner();
            String remark=chance.getRemark();
            String img=chance.getImg();


            object.put("cmd", "addopportunity");
            object.put("type", "8");
            object.put("userID", userID);
            object.put("jihuiid", jihuiid);
            object.put("number", number);
            object.put("name", name);
            object.put("mytype", mytype);
            object.put("customer", customer);
            object.put("date", date);
            object.put("dateStart", dateStart);
            object.put("dateEnd", dateEnd);
            object.put("money", money);
            object.put("discount", discount);
            object.put("principal", principal);
            object.put("ourSigner", ourSigner);
            object.put("cusSigner", cusSigner);
            object.put("remark", remark);
            object.put("img", img);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void up(String json) {
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
                        JSONObject object = null;
                        String s = null;
                        try {
                            object = new JSONObject(payload);
                            s = object.getString("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.uploadCallBack(s);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2)
                            callBack.uploadCallBack("2");
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }
}
