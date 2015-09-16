package com.example.spinel.myapplication;

import android.util.Log;

import com.example.spinel.myapplication.SortListView.bpmCharacterParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Spinel on 2015/7/25.
 */
public class bpmStructure implements Serializable{

//-----------------------------------------Department-------------------------------------------
    public class Department{
        public int id;
        public String name;
        public ArrayList<User> userList;


        public Department(int id, String name){
            this.id = id;
            this.name = name;
            userList = new ArrayList<User>();
        }
    }

//-----------------------------------------User-------------------------------------------
    public class User{
        public String sex, id, name;
        public String deptId, deptName, levelName, levelId;
        public String sortLetters;  //拼音的首字母


        public User(String sex, String id, String name){
            this.sex = sex;
            this.id = id;
            this.name = name;
        }
    }

//-----------------------------------------Item-------------------------------------------
    public class Item{
        String id, name;
        int inventory;
    }

//-----------------------------------------Client-------------------------------------------
    public class Client{
        public String id, name, address;
        public Client(JSONObject data){
            try {
                id = data.getString("uid");
                name = data.getString("name");
                address = data.getString("address");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//-----------------------------------------Contact-------------------------------------------
    public class Contact{
        public String id, name, company, phone;
        public Contact(JSONObject data){
            try {
                id = data.getString("id");
                name = data.getString("name");
                company = data.getString("company");
                phone = data.getString("phone");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

//------------------------------------------------------------------------------------
public class UserPinyinComparator implements Comparator<User> {


    public int compare(bpmStructure.User o1, bpmStructure.User o2){

        if (o1.sortLetters.equals("@")
                || o2.sortLetters.equals("#")) {
            return -1;
        } else if (o1.sortLetters.equals("#")
                || o2.sortLetters.equals("@")) {
            return 1;
        } else {
            return o1.sortLetters.compareTo(o2.sortLetters);
        }
    }

}

    //--------------------------------------Structure----------------------------------------------
    public ArrayList<Department> departmentList;
    public List<User> userList;
    public List<Item> itemList=null;
    public List<Client> clientList=null;
    public List<Contact> contactList=null;

    private bpmMainActivity activity;


    public bpmStructure(bpmMainActivity activity){
        userList = new ArrayList<User>();
        departmentList = new ArrayList<Department>();
        this.activity = activity;

        mConnection = new WebSocketConnection();
        connect();
    }

    public void setDepartmentList(JSONArray datas){
        //获取部门
        for(int i=0; i<datas.length(); i++){
            try {
                JSONObject item = datas.getJSONObject(i);
                departmentList.add(new Department(item.getInt("id"), item.getString("name")) );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        activity.getUserList("");
    }

    public void setUserList(JSONArray datas){

            for( int i=0; i<datas.length(); i++){
                try {
                    JSONObject item = datas.getJSONObject(i);
                    User user = new User(item.getString("sex"), item.getString("id"), item.getString("name"));
                    userList.add(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        setUser();

    }

    public void setUser(){
        for(int i=0; i<userList.size(); i++){
            if(userList.get(i).id.equals(bpmMainActivity.userId)){
                bpmMainActivity.user = userList.get(i);
            }

        }

        sortUserList();

        activity.getUserDepartment(bpmMainActivity.userId);
    }

    public void setLoginUserDepartment(JSONObject datas){

        try {
            bpmMainActivity.user.deptId = datas.getString("deptId");
            bpmMainActivity.user.deptName = datas.getString("deptName");
            bpmMainActivity.user.levelName = datas.getString("levelName");
            bpmMainActivity.user.levelId = datas.getString("levelId");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        activity.getFragmentData();
    }

    public String getUserName(String userId){

        for(int i=0; i<userList.size(); i++){
            if(userList.get(i).id.equals(userId)){
                return userList.get(i).name;
            }

        }
        return "";
    }

    public void sortUserList(){
        bpmCharacterParser characterParser = bpmCharacterParser.getInstance();

        for(int i=0; i<userList.size(); i++){
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(userList.get(i).name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                userList.get(i).sortLetters = sortString.toUpperCase();
            }else{
                userList.get(i).sortLetters = "#";
            }
        }
        UserPinyinComparator pinyinComparator = new UserPinyinComparator();

        Collections.sort(userList, pinyinComparator);
    }

    public boolean isEmpty(){
        return userList.isEmpty();
    }


//--------------------------------------WebSocket----------------------------------------------
//    final static private String wsuri = "ws://192.168.50.11:8000/ws";
    final static private String wsuri = "ws://101.200.189.127:8000/ws";

    private WebSocketConnection mConnection;
    private boolean isConnected=false;

    private void connect(){
        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    isConnected = true;
                    getClient();
                    //getItem();
                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.e("TextMessage", payload);
                    try {
                        JSONObject response = new JSONObject(payload);
                        String error = response.getString("error");
                        String cmd = response.getString("cmd");
                        if (!error.equals("1"))
                            return;

                        //解析json
                        if(cmd.equals("kehulianxiren")) {
                            clientList = new ArrayList<>();
                            contactList = new ArrayList<>();

                            JSONArray contact = response.getJSONArray("mylianxiren");
                            JSONArray client = response.getJSONArray("mykehu");

                            for(int i=0; i<client.length(); i++)
                                clientList.add(new Client(client.getJSONObject(i)));
                            for(int i=0; i<contact.length(); i++){
                                contactList.add(new Contact(contact.getJSONObject(i)));
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    private void getClient(){

        if(mConnection==null || !mConnection.isConnected() || !isConnected) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd", "kehulianxiren").put("type", "7").put("uid", bpmMainActivity.userId);
            mConnection.sendTextMessage(request.toString());
            Log.e("Send Json Package", request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getItem(){

        if(mConnection==null || !mConnection.isConnected() || !isConnected) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd", "getMyProcess").put("type", "12");
            mConnection.sendTextMessage(request.toString());
            Log.e("Send Json Package", request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
