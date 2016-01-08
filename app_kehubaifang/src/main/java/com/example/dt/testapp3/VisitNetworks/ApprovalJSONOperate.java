package com.example.dt.testapp3.VisitNetworks;

import android.util.Log;

import com.example.dt.testapp3.VisitDataPackage.ApprovalData;
import com.example.dt.testapp3.Graphics.GodLiuStructure;
import com.example.dt.testapp3.VISITCONSTANT;
import com.example.dt.testapp3.Graphics.VisitContentFragment;
import com.example.dt.testapp3.VisitDataPackage.VisitData;
import com.example.dt.testapp3.Graphics.VisitMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by dt on 2015/8/20.
 */
public class ApprovalJSONOperate {
    private JSONObject json;
    private WebSocketConnection mConnection;
    private JSONObject rtMsg;

    private String UID;

    private VisitMainActivity act;
    private VisitContentFragment fragment;
    private final String approvalUri = VISITCONSTANT.APPROVALURI;

    private Object argu;
//    private final String approvalUri = "ws://192.168.191.1:1234/ws";        //
//    public ApprovalJSONOperate(){
//        json = new JSONObject();
//        mConnection = new WebSocketConnection();
//    }

    public ApprovalJSONOperate(String _uid) {
        json = new JSONObject();
        mConnection = new WebSocketConnection();
        UID = _uid;
    }

    public ApprovalJSONOperate setAct(VisitMainActivity _act) {
        act = _act;
        return this;
    }

    public ApprovalJSONOperate setFragment(VisitContentFragment _fragment){
        fragment = _fragment;
        return this;
    }

    public ApprovalJSONOperate setArguments(int VisitId){
        argu = VisitId;
        return this;
    }

    public ApprovalJSONOperate getApprovalInfo(final ArrayList<ApprovalData> data) {
        try {
            json.put("cmd", "getPassedPlan");
            json.put("userId", UID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mConnection.connect(approvalUri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("send json-getPlan ", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        act.setLoadingText("获取审批数据失败！请检查网络连接！");
//                        Toast.makeText(act, "获取审批数据失败！请检查网络连接！", Toast.LENGTH_SHORT).show();

                    Log.e("getPlan json failed", "connection lost code = " + code + " reason = " + reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        rtMsg = new JSONObject(payload);
//                        String tmppayload = "{\"cmd\":\"getPassedPlan\",\"datas\":[{\"processId\":\"20\",\"submitUserName\":\"闫继勇\",\"submitUserId\":\"172\",\"datas\":[{\"groupType\":\"single\",\"summaryType\":\"vertical\",\"groupId\":\"1\",\"datas\":[{\"name\":\"拜访客户\",\"rule\":\"EXPAND SINGLE_单位地址_address MULTI_联系人_contact MULTI_电话_telephone\",\"value\":\"34_云尚公司 address 北航 contact 55_laigus telephone 123456789 contact 48_spinel telephone 987654321\",\"isSummary\":\"false\",\"type\":\"client\",\"id\":\"clientName\"},{\"name\":\"出发时间\",\"rule\":\"\",\"value\":\"1.440704759999E9\",\"isSummary\":\"true\",\"type\":\"date\",\"id\":\"departtime\"},{\"name\":\"同行人\",\"rule\":\"MULTI\",\"value\":\"100\",\"isSummary\":\"true\",\"type\":\"staff\",\"id\":\"userName\"},{\"name\":\"预计目标\",\"rule\":\"\",\"value\":\"haha\",\"isSummary\":\"true\",\"type\":\"textArea\",\"id\":\"plantarget\"}],\"name\":\"外勤计划\"},{\"groupType\":\"single\",\"summaryType\":\"vertical\",\"groupId\":\"2\",\"datas\":[{\"name\":\"执行人\",\"rule\":\"\",\"value\":\"207\",\"isSummary\":\"true\",\"type\":\"staff\",\"id\":\"nextUserId\"},{\"name\":\"抄送人\",\"rule\":\"MULTI\",\"value\":\"\",\"isSummary\":\"true\",\"type\":\"staff\",\"id\":\"copyToUsersId\"}],\"name\":\"extra\"}],\"partners\":[{\"id\":\"100\",\"name\":\"迟家升\"}]}],\"error\":0}";
//                        rtMsg = new JSONObject(tmppayload);
                        Log.e("get json-getPlan", rtMsg.toString());
                        if (!rtMsg.isNull("error")) {
                            success = true;
                            int errorCode = Integer.parseInt(rtMsg.getString("error"));
                            Log.e("getPlan error ", "error code ===================== " + errorCode);
                            if (errorCode != 0) {
                                act.setLoadingText("审批服务器操作失败，请退出重试！");
//                                Toast.makeText(act, "服务器操作失败，请退出重试！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray jsons;
                            if (rtMsg.isNull("datas")) {
                                return;
                            } else {
                                jsons = rtMsg.getJSONArray("datas");
                            }
                            int processIdLei = 0;
                            String submitUid = "";
                            String submitName = "";
                            int[] partnersId = new int[0];
                            String[] partnersName = new String[0];
                            String client = "";
                            double date = 0;
                            String plantarget = "";
                            for (int i = 0; i < jsons.length(); i++) {
                                JSONObject aVisit = jsons.getJSONObject(i);
                                processIdLei = aVisit.isNull("processId") ? -1 : aVisit.getInt("processId");
                                submitUid = aVisit.isNull("submitUserId") ? "" : aVisit.getString("submitUserId");
                                submitName = aVisit.isNull("submitUserName") ? "" : aVisit.getString("submitUserName");
                                //get partners
                                JSONArray pars = aVisit.isNull("partners") ? new JSONArray() : aVisit.getJSONArray("partners");
                                partnersId = new int[pars.length()];
                                partnersName = new String[pars.length()];
                                for (int j = 0; j < pars.length(); j++) {
                                    partnersId[j] = pars.getJSONObject(j).getInt("id");
                                    partnersName[j] = pars.getJSONObject(j).getString("name");
                                }
                                //get client,date,plantarget.
                                JSONArray info = aVisit
                                        .getJSONArray("datas")
                                        .getJSONObject(0)
                                        .getJSONArray("datas");
                                client = info.getJSONObject(0).getString("value");
                                Log.e("date object", info.getJSONObject(1).toString());
                                String dateStr = info.getJSONObject(1).getString("value");
                                if (dateStr.equals(""))
                                    date = 0.0;
                                else
                                    date = Double.parseDouble(info.getJSONObject(1).getString("value"));
                                plantarget = info.getJSONObject(3).getString("value");

                                ApprovalData tmp = new ApprovalData(submitUid, submitName, partnersId,
                                        partnersName, client, date, plantarget, processIdLei);
                                data.add(tmp);
                            }
                            act.SyncApprovalInfo();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ApprovalJSONOperate submit(final VisitData data){
        JSONArray submitterArray = new JSONArray();
        JSONArray censorArray = new JSONArray();
        try {
            StringBuilder client = new StringBuilder();
            client.append(data.getCompanyId()).append("_")
                    .append(data.getCompany())
                    .append(" address ")
                    .append(data.getPlace());
            int[] targetId = data.getTargetId();
            String[] target = data.getTarget();
            for (int i = 0; i < targetId.length; i++){
                client.append(" contact ")
                        .append(targetId[i]).append("_")
                        .append(target[i]);
            }
            submitterArray.put(new JSONObject().put("id", "userName").put("value", String.valueOf(data.getSubmitId())))
                    .put(new JSONObject().put("id", "writedate").put("value", String.valueOf(data.getDateInt())))
                    .put(new JSONObject().put("id", "clientName").put("value", client.toString()))
                    .put(new JSONObject().put("id", "gongzuoqingkuang").put("value", data.getRecord()));

            int[] partnerId = data.getPartnerId();
//            String[] partnerName = data.getPartnerName();
//            for (int ptId : partnerId){
                censorArray.put(new JSONObject().put("id", "nextUserId").put("value", data.getCensorId()));
//              censorArray.put(new JSONObject().put("id", "copyToUsersId").put("value", ""));
//            }

            JSONObject submitter = new JSONObject();
            submitter.put("groupType", "single")
                    .put("datas", submitterArray)
                    .put("groupId", "1");
            JSONObject partner = new JSONObject();
            partner.put("groupType", "single")
                    .put("datas", censorArray)
                    .put("groupId", "2");

            final JSONArray jsonArray = new JSONArray();
            jsonArray.put(submitter).put(partner);


            json.put("datas",jsonArray)
                    .put("activityId", "waiqinreport")
                    .put("cmd", "startActivity")
                    .put("userId", data.getUid())
                    .put("signId", String.valueOf(data.getId()));



            mConnection.connect(approvalUri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("send submit JsonObject:", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        Log.e("submit json connect", "failed. Code = " + code + ", reason = " + reason);

                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        JSONObject rt = new JSONObject(payload);
                        int error = rt.isNull("error") ? -1 : Integer.parseInt(rt.getString("error"));
                        if (error == -1)
                            return;
                        if (error > 0)
                            Log.e("submit json connection", "Server failed.");
                        else if (error == 0) {
                            Log.e("submit json connection", "successfully");
                            int processIdC = rt.isNull("datas") ? null : rt.getInt("datas");
                            data.setProcessIdCui(processIdC);
//                            if (rtArray != null) {
//                                JSONObject tmp = rtArray.getJSONObject(0);
//                                data.setProcessIdCui(Integer.parseInt(tmp.getString("processId")));
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    act.afterSaving();
//                    fragment.refreshList();
//                    act.showVisitOverviewFragment();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ApprovalJSONOperate sendSignedId(ArrayList<VisitData> data){
        try {
            json.put("cmd", "sendSignId");
            json.put("userId", UID);
            final JSONArray jsonArray = new JSONArray();
            for (VisitData v : data){
                JSONObject tmp = new JSONObject();
                tmp.put("processId", String.valueOf(v.processIdLei()));
                tmp.put("signId", String.valueOf(v.getId()));
                jsonArray.put(tmp);
            }
            json.put("datas", jsonArray);
            mConnection.connect(approvalUri, new WebSocketHandler() {
                private boolean success = false;
                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("Send Json-sendSignId", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success){
                        Log.e("sendSignId json", "failed, code = " + code + ", reason = " + reason);
                    }
//                    else
//                        Log.e("Json connection", "successfully.");
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        JSONObject rt = new JSONObject(payload);
                        int error = rt.isNull("error")?-1:Integer.parseInt(rt.getString("error"));
                        if (error == 0)
                            Log.e("sendSignId json", "successfully.");
                        else
                            Log.e("sendSignId json", "server error code = " + error);
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

    public ApprovalJSONOperate getUserList(){
        final GodLiuStructure godLiuStructure = new GodLiuStructure(act);
        if(mConnection==null)
            mConnection = new WebSocketConnection();
        try {
            json.put("cmd", "getUserList").put("userId", UID).put("department", "");
            mConnection.connect(approvalUri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("send getUserList json", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        Log.e("getUserList json", "connect failed, code = " + code + ", reason = " + reason);
//                        Toast.makeText(act,"连接联系人选取服务器失败！请检查网络连接！",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTextMessage(String payload) {
                    success = true;
                    try {
                        JSONObject response = new JSONObject(payload);
                        System.out.println(response.toString());

                        String cmd = response.getString("cmd");
                        int error = response.getInt("error");
                        Log.e("getUserList json error", " code = " + error);
                        if (cmd.equals("getUserList")) {
                            if (error != 0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");

                            godLiuStructure.setUserList(datas);
                            act.analyzeUserList(godLiuStructure);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    act.showHandler.sendMessage(new Message());
                    act.setLoadingText("正在拉取联系人列表......");
                    act.showVisitOverviewFragment();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ApprovalJSONOperate getEvaluation(final int _id){
        try {
            json.put("cmd", "getWaiqinRes");
            json.put("userId", UID);
            json.put("signId", String.valueOf(_id));
            act.setEvaluation(-1, null, null);
            mConnection.connect(approvalUri, new WebSocketHandler(){
                private boolean success = false;
                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("Evaluation Json", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success){
                        Log.e("Evaluation Json", "failed, code = " + code + ", reason = " + reason);
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    int evaluationLevel = -1;
                    String evaluation = "";
                    String evaluationTape = "";

                    try {
                        JSONObject rt = new JSONObject(payload);
                        Log.e("get evaluate json", payload);
                        int error = rt.isNull("error")?-1:rt.getInt("error");
                        if (error == -1){
                            Log.e("Evaluation json", "Server failed. Error code = " + error);
                            return;
                        }
                        if (error == 0){
                            JSONArray tmpArray = rt.getJSONArray("datas");
                            if (tmpArray.length()==0){
                                return;
                            }
                            JSONArray evaluateArray = rt.getJSONArray("datas").getJSONObject(0).getJSONArray("datas");
                            Log.e("evaluateArray = ", evaluateArray.toString());
                            for (int i = 0; i < evaluateArray.length(); i++){
                                JSONObject tmp = evaluateArray.getJSONObject(i);
                                switch (tmp.getString("id")){
                                    case "youxiu":
                                        if (tmp.getString("value").equals("true"))
                                            evaluationLevel = 5;
                                        break;
                                    case "lianghao":
                                        if (tmp.getString("value").equals("true"))
                                            evaluationLevel = 6;
                                        break;
                                    case "hege":
                                        if (tmp.getString("value").equals("true"))
                                            evaluationLevel = 7;
                                        break;
                                    case "jiaocha":
                                        if (tmp.getString("value").equals("true"))
                                            evaluationLevel = 8;
                                        break;
                                    case "comment":
                                        evaluation = tmp.getString("value");
                                        break;
                                    case "voicecomment":
                                        evaluationTape = tmp.getString("value");
                                        break;
                                }
                            }
                            act.setEvaluation(evaluationLevel,new String[]{evaluationTape},evaluation);
                        }
                        // todo .

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    fragment.clickIntoItem(_id);
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
        if ((mConnection != null && mConnection.isConnected())) {
            mConnection.disconnect();
        }
    }
}
