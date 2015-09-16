package com.kuroi.contract.service;

import android.util.Log;

import com.kuroi.contract.model.Contract;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ConUPLOAD {
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;
    private ConUploadCallBack callBack;
    private String userID;

    public ConUPLOAD(ConUploadCallBack callBack, String userId) {
        this.callBack = callBack;
        this.userID = userId;
    }

    public String changeArrayDateToJson(Contract contract) {
        try {
            JSONObject object = new JSONObject();

            String hetongid = String.valueOf(contract.getId());
            String number=contract.getNumber();
            String name=contract.getName();
            String mytype = contract.getType();
            String customer=contract.getCustomer();
            String date=contract.getDate();
            String dateStart=contract.getDateStart();
            String dateEnd=contract.getDateEnd();
            String money=contract.getMoney();
            String discount=contract.getDiscount();
            String principal=contract.getPrincipal();
            String ourSigner=contract.getOurSigner();
            String cusSigner=contract.getCusSigner();
            String remark=contract.getRemark();
            String img=contract.getImg();


            object.put("cmd", "hetong");
            object.put("type", "8");
            object.put("userID", userID);
            object.put("hetongid", hetongid);
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
