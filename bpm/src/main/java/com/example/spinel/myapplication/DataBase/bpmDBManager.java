package com.example.spinel.myapplication.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Draft_DataHolder;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Submit_DataHolder;
import com.example.spinel.myapplication.BPMItem;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Task_DataHolder;
import com.example.spinel.myapplication.bpmTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by Spinel on 2015/7/25.
 */
public class bpmDBManager {
    private bpmDbOpenHelper helper;
    private SQLiteDatabase db;
    private bpmMainActivity activity;

    public bpmDBManager(bpmMainActivity activity){
        helper = new bpmDbOpenHelper(activity);
        db = helper.getWritableDatabase();
        this.activity = activity;

        //设置时间戳
        Cursor c = db.rawQuery("select count(*) from timestamp where userId=?", new String[]{bpmMainActivity.userId});
        c.moveToFirst();
        int num = c.getInt(0);
        if(num==0){
            db.execSQL("INSERT INTO timestamp VALUES(?,?,?)", new Object[]{bpmMainActivity.userId, "myprocess", 0});
            db.execSQL("INSERT INTO timestamp VALUES(?,?,?)", new Object[]{bpmMainActivity.userId, "mydeal", 0});
            db.execSQL("INSERT INTO timestamp VALUES(?,?,?)", new Object[]{bpmMainActivity.userId, "careprocess", 0});
            db.execSQL("INSERT INTO timestamp VALUES(?,?,?)", new Object[]{bpmMainActivity.userId, "atmeprocess", 0});
            db.execSQL("INSERT INTO timestamp VALUES(?,?,?)", new Object[]{bpmMainActivity.userId, "partnerprocess", 0});
        }

        activity.getFragmentData();
    }

    public void closeDB(){
        db.close();
    }

    public String[] getPlaces(){

        Cursor c = db.rawQuery("select count(*) from swf_area", null);
        c.moveToFirst();
        int num = c.getInt(0);

        String[] places = new String[num];

        c = db.query("swf_area", new String[]{"name"}, null, null, null, null, null);


        for(int i=0; i<num && c.moveToNext(); i++){
            places[i]=c.getString(c.getColumnIndex("name"));
        }


        c.close();

        return places;
    }


    public String getTimeStamp(String type){
        Cursor c = db.query("timestamp", new String[]{"time"}, "userId=? and type=?", new String[]{bpmMainActivity.userId, type}, null, null, null);
        while(c.moveToNext()){
            return c.getString(0);
        }
        return "0";
    }

    public void saveTimeStamp(String type, String time){
        ContentValues values = new ContentValues();
        values.put("time", time);
        db.update("timestamp", values, "userId=? and type=?", new String[]{bpmMainActivity.userId, type});
    }

    //--------------bpmlist--------------
    //返回是否有改变
    public boolean saveBPMList(JSONArray datas){

        ArrayList<BPMItem> oldList = getBPMList();
        ArrayList<BPMItem> newList = new ArrayList<>();

        //构建新list
        try{
        for (int i=0; i<datas.length(); i++){
            JSONObject item = null;
            item = datas.getJSONObject(i);

            String activityId = item.getString("activityId");
            String activityName = item.getString("activityName");
            String type = item.getString("type");
            String info = item.getString("info");
            JSONArray formdatas = item.getJSONArray("datas");

            newList.add(new BPMItem(info, activityId, type, activityName, formdatas));
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //比较是否有不同
        if(oldList.size() == newList.size()) {
            if (isBPMListEqual(oldList, newList)) {
                return false;
            }
        }

        //不同刷新数据库
        db.execSQL("delete from bpmlist where userId="+ bpmMainActivity.userId);
        for(BPMItem item: newList){

            db.execSQL("INSERT INTO bpmlist(userId, activityId, activityName, type, datas, info) VALUES(?,?,?,?,?,?)",
                    new Object[]{bpmMainActivity.userId, item.activityId, item.activityName, item.type, item.datas.toString(), item.info});
        }
        return true;
    }

    public boolean isBPMListEqual(ArrayList<BPMItem> aa, ArrayList<BPMItem> bb){
        ArrayList<BPMItem> a=(ArrayList<BPMItem>)aa.clone();
        ArrayList<BPMItem> b=(ArrayList<BPMItem>)bb.clone();

        Collections.sort(a);
        Collections.sort(b);
        if(a.size() != b.size()) {
            return false;
        }

        for(int i=0; i<a.size(); i++)
            if(!a.get(i).equals(b.get(i))) {
                return false;
            }
        return true;
    }

    public ArrayList<BPMItem> getBPMList(){
        ArrayList<BPMItem> list = new ArrayList<>();

        Cursor c = db.query("bpmlist", new String[]{"activityId", "activityName", "type", "datas", "info"}, "userId="+ bpmMainActivity.userId, null, null, null, null);

        while(c.moveToNext()){
            String activityId = c.getString(0);
            String activityName = c.getString(1);
            String type = c.getString(2);
            String info = c.getString(4);
            try {
                JSONArray datas = new JSONArray(c.getString(3));

                list.add(new BPMItem(info, activityId, type, activityName, datas));

            }catch(JSONException e){

            }
        }

        c.close();

        return list;
    }


    //--------------草稿--------------
    public void saveDraft(String title, String datas, String blankdatas){
        //时间是按秒存储
    //    String activityId = MainActivity.BPMList.get(MainActivity.currentBPMIndex).activityId;

        long time_long = Calendar.getInstance().getTimeInMillis();
        double time_double = ((double)time_long)/1000;
        String time = ((Double)time_double).toString();
        String activityId = bpmMainActivity.BPMGroupList.get(bpmMainActivity.currentBPMIndex_group).list.get(bpmMainActivity.currentBPMIndex_item).activityId;
        db.execSQL("INSERT INTO draft(activityId, time, userId, title, datas, blankdatas) VALUES(?,?,?,?,?,?)",
                new Object[]{activityId, time, bpmMainActivity.userId, title, datas, blankdatas});
        Toast.makeText(activity, "已存入草稿箱："+title, Toast.LENGTH_SHORT).show();


        System.out.println("save draft: "+datas);
    }

    public void saveDraft(int id, String datas){
        ContentValues values = new ContentValues();
        values.put("datas", datas);
        db.update("draft", values, "id=?", new String[]{""+id});
    }

    public ArrayList<bpmMainFragment_Draft_DataHolder> getDraft(){
        ArrayList<bpmMainFragment_Draft_DataHolder> datas = new ArrayList<>();

        Cursor c = db.query("draft", new String[]{"id", "activityId", "time", "title", "datas", "blankdatas"}, "userId="+ bpmMainActivity.userId, null, null, null, null);

        while(c.moveToNext()){
            int id = c.getInt(0);
            String activityId = c.getString(1);
            Calendar time = bpmTools.Millisecond2Calendar(c.getString(2));
            String title = c.getString(3);
            String data = c.getString(4);
            String blankdatas = c.getString(5);


            datas.add(new bpmMainFragment_Draft_DataHolder(id, activityId, title, time, data, blankdatas));
        }

        return datas;
    }

    public void deleteDraft(int id){
        System.out.println("id: "+id);
        db.delete("draft", "id=? and userId=?", new String[]{bpmTools.Int2String(id), bpmMainActivity.userId});
    }

    //--------------get--------------
    public void saveProcess(JSONArray datas, String type){

        if(!type.equals("myprocess"))
            db.delete("process", "userId=? and type=?", new String[]{bpmMainActivity.userId, type});

        try {

        for (int i = 0; i < datas.length(); i++) {
            JSONObject item = null;
            item = datas.getJSONObject(i);


            String activityId = item.getString("activityId");
            String activityName = item.getString("activityName");
            String processId = item.getString("processId");
            String status = item.getString("status");
            String stepId = item.getString("stepId");
            String stepName = item.getString("stepName");
            String submitUserId = item.getString("submitUserId");
            String submitUserName = item.getString("submitUserName");
            String startTime = item.getString("startTime");
            String endTime = item.getString("endTime");

            //查询process和activity是否重复
            if(type.equals("myprocess"))
                db.delete("process", "processId=? and userId=?", new String[]{processId, bpmMainActivity.userId});

            db.execSQL("INSERT INTO process(userId, type, activityId, activityName, " +
                            "processId, status, stepId, stepName, submitUserId, submitUserName, startTime, endTime" +
                            ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{bpmMainActivity.userId, type, activityId, activityName, processId, status, stepId, stepName,
                            submitUserId, submitUserName, startTime, endTime});

        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<bpmMainFragment_Submit_DataHolder> getProcess(String type){
        ArrayList<bpmMainFragment_Submit_DataHolder> datas = new ArrayList<>();

        Cursor c = db.query("process", new String[]{"activityId, activityName, processId, status, stepId, stepName, submitUserId, submitUserName, startTime, endTime"}, "userId=? and type=?", new String[]{bpmMainActivity.userId, type}, null, null, null);

        while(c.moveToNext()){
            String activityId = c.getString(c.getColumnIndex("activityId"));
            String activityName = c.getString(c.getColumnIndex("activityName"));
            String processId = c.getString(c.getColumnIndex("processId"));
            String status = c.getString(c.getColumnIndex("status"));
            String stepId = c.getString(c.getColumnIndex("stepId"));
            String stepName = c.getString(c.getColumnIndex("stepName"));
            String submitUserId = c.getString(c.getColumnIndex("submitUserId"));
            String submitUserName = c.getString(c.getColumnIndex("submitUserName"));
            Calendar startTime = bpmTools.Millisecond2Calendar(c.getString(c.getColumnIndex("startTime")));
            Calendar endTime = bpmTools.Millisecond2Calendar(c.getString(c.getColumnIndex("endTime")));


            datas.add(new bpmMainFragment_Submit_DataHolder(activityId, activityName, processId, status, stepId, stepName, submitUserId, submitUserName, startTime, endTime));

        }

        c.close();

        return datas;
    }

    public void saveMyTask(JSONArray datas){

        db.execSQL("delete from task where userId='"+ bpmMainActivity.userId+"'");

        try {

            for (int i = 0; i < datas.length(); i++) {
                JSONObject item = null;
                item = datas.getJSONObject(i);


                String activityId = item.getString("activityId");
                String activityName = item.getString("activityName");
                String taskId = item.getString("taskId");
                String status = item.getString("status");
                String submitUser = bpmMainActivity.user.name;

                String startTime = item.getString("startTime");
                String endTime = item.getString("endTime");

                JSONArray processIds = item.getJSONArray("datas");

                db.execSQL("INSERT INTO task(userId, startTime, endTime, activityName, status, submitUser, processIds, taskId, activityId) VALUES(?,?,?,?,?,?,?,?,?)",
                        new Object[]{bpmMainActivity.userId, startTime, endTime, activityName, status, submitUser, processIds.toString(), taskId, activityId});


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<bpmMainFragment_Task_DataHolder> getMyTask(){


        ArrayList<bpmMainFragment_Task_DataHolder> datas = new ArrayList<>();

        Cursor c = db.query("task", new String[]{"startTime, endTime, activityName, status, submitUser, processIds, taskId, activityId"}, "userId="+ bpmMainActivity.userId, null, null, null, null);

        while(c.moveToNext()) {

            String activityId = c.getString(c.getColumnIndex("activityId"));
            String activityName = c.getString(c.getColumnIndex("activityName"));
            String taskId = c.getString(c.getColumnIndex("taskId"));
            String status = c.getString(c.getColumnIndex("status"));
            String submitUser = c.getString(c.getColumnIndex("submitUser"));

            Calendar startTime = bpmTools.Millisecond2Calendar(c.getString(c.getColumnIndex("startTime")));
            Calendar endTime = bpmTools.Millisecond2Calendar(c.getString(c.getColumnIndex("endTime")));

            try {
                String processIdstr = c.getString(c.getColumnIndex("processIds"));
                JSONArray processIdArray = new JSONArray(processIdstr);
                String[] processIds = new String[processIdArray.length()];
                for(int i=0; i<processIdArray.length(); i++){
                    processIds[i] = processIdArray.getString(i);
                }

                datas.add(new bpmMainFragment_Task_DataHolder(activityId, activityName, taskId, status, submitUser, processIds, startTime, endTime));

            }catch (JSONException e){

            }
        }

        c.close();

        return datas;
    }


}