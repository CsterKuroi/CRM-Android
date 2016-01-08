package com.example.dt.testapp3.VisitNetworks;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.dt.testapp3.VISITCONSTANT;
import com.example.dt.testapp3.VisitDataPackage.VisitDB;
import com.example.dt.testapp3.VisitDataPackage.VisitData;
import com.example.dt.testapp3.Graphics.VisitMainActivity;
import com.example.dt.testapp3.Graphics.VisitOverviewFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by dt on 2015/8/9.
 */
public class VisitJSONOperate {
    JSONObject json;

    private VisitData tik;
    private VisitMainActivity act;
    private VisitOverviewFragment fragment;
    private HashMap<String, String> map;
    private String UID;

    private boolean silence;

    private boolean isnull = false;
    private int rtCode;
    private JSONObject rtMsg;
    private WebSocketConnection mConnection;
    private String tempjson;
        private final String wsuri = VISITCONSTANT.DATABASEURI;
//    private final String wsuri = "ws://192.168.50.11:8000/ws";
//    private final String wsuri = "ws://192.168.50.101:8001/ws";

    private CountDownLatch latch;

    public VisitJSONOperate() {
        json = new JSONObject();
        mConnection = new WebSocketConnection();
        latch = null;
        silence = false;
    }

    public VisitJSONOperate(HashMap<String, String> _map) {
        this();
        map = _map;
    }

    public VisitJSONOperate setAct(VisitMainActivity _act){
        act = _act;
        return this;
    }

    public VisitJSONOperate setFragment(VisitOverviewFragment _fragment){
        fragment = _fragment;
        return this;
    }

    public VisitJSONOperate setUID(String _uid) {
        UID = _uid;
        return this;
    }

    public VisitJSONOperate setLatch(CountDownLatch _latch){
        latch = _latch;
        return this;
    }

    public VisitJSONOperate setSilence(boolean silence){
        this.silence = silence;
        return this;
    }

    public VisitJSONOperate addJson(VisitData data) {
        try {
            json.put("cmd", "baifangkehu");
            json.put("type", "6");

            json.put("id", String.valueOf(data.getId()));
            json.put("uid", data.getUid());
            json.put("submitId", data.getSubmitId());
            json.put("submitName", data.getSubmitName());
            json.put("date", data.getDate());
            json.put("dateInt", String.valueOf(data.getDateInt()));
            json.put("place", data.getPlace());
            json.put("companyId", String.valueOf(data.getCompanyId()));
            json.put("company", data.getCompany());
            json.put("targetId", Arrays.toString(data.getTargetId()));
            json.put("target", Arrays.toString(data.getTarget()));
            json.put("todo", data.getTodo());
            json.put("mytype", String.valueOf(data.getMytype()));
            json.put("record", data.getRecord());
            json.put("result", String.valueOf(data.getResult()));
            json.put("remark", data.getRemark());
            json.put("tape", data.getTape());
            json.put("condition", String.valueOf(data.getCondition()));
            json.put("partnerId", Arrays.toString(data.getPartnerId()));
            json.put("partnerName", Arrays.toString(data.getPartnerName()));
            json.put("pidL", String.valueOf(data.processIdLei()));
            json.put("pidC", String.valueOf(data.processIdCui()));
            json.put("isTemp", (data.isTemp() ? "1" : "0"));
            json.put("shenherenid", data.getCensorId());
            json.put("shenherenname", data.getCensorName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String addStr = "insert into visitTable values ( " + data.getAddSQLString() + ")";
        this.sendMessage(addStr);
        return this;
    }

    public VisitJSONOperate updateJson(VisitData data) {
        try {
            json.put("cmd", "xiugaibaifang");
            json.put("type", "6");

            json.put("id", String.valueOf(data.getId()));
            json.put("uid", data.getUid());
            json.put("submitId", data.getSubmitId());
            json.put("submitName", data.getSubmitName());
            json.put("date", data.getDate());
            json.put("dateInt", String.valueOf(data.getDateInt()));
            json.put("place", data.getPlace());
            json.put("companyId", String.valueOf(data.getCompanyId()));
            json.put("company", data.getCompany());
            json.put("targetId", Arrays.toString(data.getTargetId()));
            json.put("target", Arrays.toString(data.getTarget()));
            json.put("todo", data.getTodo());
            json.put("mytype", String.valueOf(data.getMytype()));
            json.put("record", data.getRecord());
            json.put("result", String.valueOf(data.getResult()));
            json.put("remark", data.getRemark());
            json.put("tape", data.getTape());
            json.put("condition", String.valueOf(data.getCondition()));
            json.put("partnerId", Arrays.toString(data.getPartnerId()));
            json.put("partnerName", Arrays.toString(data.getPartnerName()));
            json.put("pidL", String.valueOf(data.processIdLei()));
            json.put("pidC", String.valueOf(data.processIdCui()));
            json.put("isTemp", (data.isTemp() ? "1" : "0"));
            json.put("shenherenid", data.getCensorId());
            json.put("shenherenname", data.getCensorName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String updateStr = "update visitTable set " +
                data.getUpdateSQLString() +
                " where id = " + data.getId();
        this.sendMessage(updateStr);
        return this;
    }

    public void sendMessage(final String SQLStr) {
        mConnection = new WebSocketConnection();
        if (!silence)
            act.setLoadingText("正在向服务器发送数据......");
        try {
            tempjson = json.toString();
            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(tempjson);
                    Log.e("test2", "发送Json字段 " + tempjson);
                }

                @Override
                public void onClose(int code, String reason) {
                    if (latch != null)
                        latch.countDown();
                    if (!success && !silence)
                        Toast.makeText(act, "保存失败！请检查网络连接！", Toast.LENGTH_SHORT).show();
                    Log.e("test", "connection lost" + code + reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        rtMsg = new JSONObject(payload);
                        Log.e("test", payload);
                        if (!rtMsg.isNull("error")) {
                            success = true;
                            rtCode = Integer.parseInt(rtMsg.getString("error"));
                            Log.e("error ", "error code ===================== " + rtCode);
                            if (rtCode == 1) {
                                VisitDB visitdb = new VisitDB(act, "USER" + UID, null, 1);
                                SQLiteDatabase db = visitdb.getWritableDatabase();
                                db.execSQL(SQLStr);
                                db.close();
                                Log.e("SQLite exec", "insert/update successfully.");
                                //Toast.makeText(fragment.getActivity(), map.toString() + "被删除了", Toast.LENGTH_SHORT).show();


                                //todo : call some method;
                                if (!silence){
                                    act.afterSaving();
//                                    act.showVisitOverviewFragment();
                                    Toast.makeText(act, "保存成功！", Toast.LENGTH_SHORT).show();
//                                    fragment.refreshList();
                                }

                            } else {
                                if (!silence)
                                    Toast.makeText(act, "服务器端保存失败，请重试！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mConnection.disconnect();
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }


    public VisitJSONOperate getAll(final ArrayList<VisitData> data2) {
        try {
            json.put("type", "6");
            json.put("cmd", "mykehubaifang");
            json.put("uid", UID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        act.setLoadingText("正在拉取服务器拜访记录......");
        try {
            tempjson = json.toString();
            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(tempjson);
                    Log.e("test2", "发送Json字段 " + tempjson);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.e("test", "connection lost" + code + reason);
                    if (!success){
                        Toast.makeText(act, "同步本地数据库失败！请检查网络连接！", Toast.LENGTH_SHORT).show();
                        act.showVisitOverviewFragment();
                    }
//                        act.setLoadingText("同步本地数据库失败！请检查网络连接！");
//
                }

                @Override
                public void onTextMessage(String payload) {
                    int maxid = 0;

                    try {
                        success = true;
                        Log.e("收到json字段", payload);
                        rtMsg = new JSONObject(payload);
                        rtCode = Integer.parseInt(rtMsg.getString("error"));
                        Log.e("error ", "error code ===================== " + rtCode);
                        if (rtCode != 1) {
                            act.setLoadingText("从服务器端同步至本地数据库失败！");
                            act.showVisitOverviewFragment();
                            Toast.makeText(act, "从服务器端同步至本地数据库失败！", Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Toast.makeText(act, "同步本地数据库成功！", Toast.LENGTH_SHORT).show();
//                        act.setLoadingText("同步本地数据库成功！");
                        JSONArray jsonArray = new JSONArray();
//                            Log.e("test","herehereherehereherehereherehereherehere");


                        if (rtMsg == null) {
                            Log.e("test", "rtMsg ================= null");
                            isnull = true;
                            return;
                        }
                        if (!rtMsg.isNull("result")) {
                            jsonArray = rtMsg.getJSONArray("result");
                            Log.e("test", "rtMsg.result ================= null " + jsonArray.length());
                        }

                        act.setLoadingText("正在刷新本地数据......");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            //get data from json package
                            Log.e("jsonObject", j.toString());
                            int _id = j.isNull("id") ? -1 : Integer.parseInt(j.getString("id"));
                            String _uid = j.isNull("uid") ? "" : j.getString("uid");
                            String _submitId = j.isNull("submitid") ? "" : j.getString("submitid");
                            String _submitName = j.isNull("submit") ? "" : j.getString("submit");
                            String _date = j.isNull("date") ? "" : j.getString("date");
                            double _dateInt = j.isNull("dateint") ? -1 : Double.parseDouble(j.getString("dateint"));
                            String _place = j.isNull("place") ? "" : j.getString("place");
                            int _companyId = j.isNull("companyid") ? -1 : Integer.parseInt(j.getString("companyid"));
                            String _company = j.isNull("company") ? "" : j.getString("company");
//                                    int[] _targetId = new int[0];
                            String targetIdStr = j.isNull("targetid") ? "" : j.getString("targetid");
                            Log.e("targetId", targetIdStr);
//                                    String[] _target = new String[0];
                            String targetStr = j.isNull("target") ? "" : j.getString("target");
                            String _todo = j.isNull("todo") ? "" : j.getString("todo");
                            int _mytype = j.isNull("type") ? 0 : Integer.parseInt(j.getString("type"));
                            String _record = j.isNull("record") ? "" : j.getString("record");
                            int _result = j.isNull("result") ? 0 : Integer.parseInt(j.getString("result"));
                            String _remark = j.isNull("remark") ? "" : j.getString("remark");
                            String _tape = j.isNull("tape") ? "" : j.getString("tape");
                            int _condition = j.isNull("condition") ? 0 : Integer.parseInt(j.getString("condition")); //等待拜访
//                                    int[] _partnerId = new int[0];
                            String partnerIdStr = j.isNull("partnerid") ? "" : j.getString("partnerid");
//                                    String[] _partnerName = new String[0];
                            String partnerStr = j.isNull("partnername") ? "" : j.getString("partnername");
                            int _processIdLei = j.isNull("pidL") ? -1 : Integer.parseInt(j.getString("pidL"));
                            int _processIdCui = j.isNull("pidC") ? -1 : Integer.parseInt(j.getString("pidC"));
                            int isTempInt = j.isNull("istemp") ? 0 : Integer.parseInt(j.getString("istemp"));
                            boolean _isTemp = (isTempInt == 1) ? true : false;
                            String _censorId = j.isNull("shenherenid")?"":j.getString("shenherenid");
                            String _censorName = j.isNull("shenherenname")?"":j.getString("shenherenname");

                            //handle the target arrays
                            targetIdStr = targetIdStr.substring(1, targetIdStr.length() - 1);
                            targetStr = targetStr.substring(1, targetStr.length() - 1);
                            String[] targetIdTmpArray = targetIdStr.split(", ");
                            int[] _targetId;
                            String[] _target = targetStr.split(", ");
                            if (!targetIdTmpArray[0].equals("") && !_target[0].equals("")) {
                                _targetId = new int[targetIdTmpArray.length];
                                for (int kms = 0; kms < targetIdTmpArray.length; kms++) {
                                    _targetId[kms] = Integer.parseInt(targetIdTmpArray[kms]);
                                }
                            } else {
                                _targetId = new int[0];
                                _target = new String[0];
                            }
                            //handle the partner arrays
                            partnerIdStr = partnerIdStr.substring(1, partnerIdStr.length() - 1);
                            partnerStr = partnerStr.substring(1, partnerStr.length() - 1);
                            String[] partnerIdTmpArray = partnerIdStr.split(", ");
                            int[] _partnerId;
                            String[] _partner = partnerStr.split(", ");
                            if (!partnerIdTmpArray[0].equals("") && !_partner[0].equals("")) {
                                _partnerId = new int[partnerIdTmpArray.length];
                                for (int kms = 0; kms < partnerIdTmpArray.length; kms++) {
                                    _partnerId[kms] = Integer.parseInt(partnerIdTmpArray[kms]);
                                }
                            } else {
                                _partnerId = new int[0];
                                _partner = new String[0];
                            }

                            maxid = maxid<_id ? _id : maxid;

                            VisitData tmp = new VisitData(_id, _uid, _submitId, _submitName, _date,
                                    _dateInt, _place, _companyId, _company, _targetId, _target, _todo,
                                    _mytype, _record, _result, _remark, _tape, _condition,
                                    _partnerId, _partner, _processIdLei, _processIdCui, _isTemp,
                                    _censorId, _censorName);
                            data2.add(tmp);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    act.setMaxId(maxid);
//                    act.afterInitHandler.sendMessage(new Message());
                    act.afterInit();
                    mConnection.disconnect();
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        Log.e("isnull ============ ", String.valueOf(isnull));
        return this;
    }

    public VisitJSONOperate getAddress(String com) {
        try {
            json.put("type", "6");
            json.put("cmd", "baifangdizhi");
            json.put("company", com);
            json.put("uid", UID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("test2", "发送Json字段 " + json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        Toast.makeText(act, "获取公司地址失败！请检查网络连接！", Toast.LENGTH_SHORT).show();
                    Log.e("test", "connection lost" + code + reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        rtMsg = new JSONObject(payload);
                        Log.e("test", payload);
                        if (!rtMsg.isNull("error")) {
                            success = true;
                            rtCode = Integer.parseInt(rtMsg.getString("error"));
                            Log.e("error ", "error code ===================== " + rtCode);
                            if (rtCode == 1) {
                                String placeData = rtMsg.getString("address");
                                act.setPlace(placeData);
                            } else {
                                Toast.makeText(act, "获取公司地址失败，请重试！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mConnection.disconnect();
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public VisitJSONOperate delete() {

        try {
            json.put("type", "6");
            json.put("cmd", "shanchubaifang");
            json.put("id", String.valueOf(map.get("visitId")));
            json.put("uid", UID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            tempjson = json.toString();
            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(tempjson);
                    Log.e("test2", "发送Json字段 " + tempjson);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.e("test", "connection lost" + code + reason);
                    if (!success)
                        Toast.makeText(act, map.toString() + "删除失败！请检查网络连接！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTextMessage(String payload) {
                    try {

                        rtMsg = new JSONObject(payload);
                        Log.e("test", payload);
                        if (!rtMsg.isNull("error")) {
                            success = true;
                            rtCode = Integer.parseInt(rtMsg.getString("error"));
                            Log.e("error ", "error code ===================== " + rtCode);
                            switch (rtCode) {
                                case 1:
                                    VisitDB visitdb = new VisitDB(act, "USER" + UID, null, 1);
                                    SQLiteDatabase db = visitdb.getWritableDatabase();
                                    db.execSQL("delete from visitTable where id = " + map.get("visitId"));
                                    db.close();
                                    Log.e("SQLite exec", "delete successfully.");
//                                    Toast.makeText(act, map.toString() + "已经被删除。", Toast.LENGTH_SHORT).show();
                                    //fragment.refreshDatabase();
                                    fragment.refreshList();
                                    break;
                                default:
                                    Toast.makeText(act, map.toString() + "服务器端删除失败！请重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mConnection.disconnect();
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public VisitJSONOperate updateCondition(final int _id, String _uid, final int _cond){
        try {
            json.put("cmd", "changecondition");
            json.put("type", "6");
            json.put("id", String.valueOf(_id));
            json.put("uid", _uid);
            json.put("condition", String.valueOf(_cond));

            mConnection.connect(wsuri, new WebSocketHandler(){
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("Update Condition Json", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success){
                        Log.e("Update Condition Json", "failed. Code = " + code + ", reason = " + reason);
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        success = true;
                        Log.e("Update Condition Json", payload);
                        JSONObject rt = new JSONObject(payload);
                        int error = rt.isNull("error")?-1:Integer.parseInt(rt.getString("error"));
                        switch (error){
                            case 1:
                                Log.e("Update Condition Json", "send successfully. error code = " +error);
                                act.updateSQLCondition(_id,_cond);
                                break;
                            default:
                                Log.e("Update Condition Json", "Server failed. error code = " + error);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void disconnect() {
        if (mConnection != null && mConnection.isConnected()) {
            mConnection.disconnect();
        }
    }
}
