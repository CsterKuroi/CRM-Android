package com.example.dt.testapp3;

import com.example.dt.testapp3.GodLiuSortListView.GodLiuCharacterParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Spinel on 2015/7/25.
 */
public class GodLiuStructure implements Serializable{


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
//------------------------------------------------------------------------------------
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

//------------------------------------------------------------------------------------
public class UserPinyinComparator implements Comparator<User> {


    public int compare(GodLiuStructure.User o1, GodLiuStructure.User o2){

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
    //------------------------------------------------------------------------------------
    public List<User> userList;
    public ArrayList<Department> departmentList;
    private VisitMainActivity activity;
    private int currentIndex;


    public GodLiuStructure(VisitMainActivity activity){
        userList = new ArrayList<User>();
        departmentList = new ArrayList<Department>();
        this.activity = activity;
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

        sortUserList();

    }


    public void sortUserList(){
        GodLiuCharacterParser godLiuCharacterParser = GodLiuCharacterParser.getInstance();

        for(int i=0; i<userList.size(); i++){
            //汉字转换成拼音
            String pinyin = godLiuCharacterParser.getSelling(userList.get(i).name);
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


}
