package com.example.spinel.myapplication.Form;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.example.spinel.myapplication.bpmListViewForScrollView;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.SortListView.bpmSortListMainActivity;
import com.example.spinel.myapplication.bpmTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Spinel on 2015/7/23.
 */
public class bpmForm_Group implements Comparable<bpmForm_Group>{
    public int groupIndex; // 在form activity里位置
    public int groupId;
    public String groupType; //1:single  2:multi
    public String summaryType;
//    public String detailType; //具体Type（radio group）
    public String groupName;

    public ArrayList<bpmForm_Item> itemList;
    private ArrayList<bpmForm_Item> hideList;
    private ArrayList<bpmForm_Item> realHideList;

    public bpmListViewForScrollView group;

    public bpmForm_Adapter_Write adapter;
    public bpmForm_Adapter_Read adapter_read;

    private boolean radiofinish = true;
    private Activity activity;

    //multi使用
    public JSONObject groupJSON;
    public ImageButton button_add, button_sub;

    public int compareTo(bpmForm_Group form_group){
        return groupId<form_group.groupId ? -1 : 1;
    }
    //---------------------------------------------------

    private String get(JSONObject json, String key){
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public bpmForm_Group(JSONObject group, int index, Activity activity){
        this.activity = activity;
        groupIndex = index;
        itemList = new ArrayList<bpmForm_Item>();
        hideList = new ArrayList<>();
        realHideList = new ArrayList<>();

        try {
            groupId = Integer.parseInt(group.getString("groupId"));
            groupType = group.getString("groupType");
            groupName = get(group, "name");
            summaryType = group.getString("summaryType");
            if(groupType.equals("multi"))
                groupJSON = group;



            JSONArray datas = group.getJSONArray("datas");

            //组内控件循环
            for(int i=0; i<datas.length(); i++){
                itemList.add(new bpmForm_Item((JSONObject)datas.get(i), activity, this));
            }

            //hide控件
            for(int i=0; i<itemList.size(); i++){
                if(itemList.get(i).type== bpmForm_Item.HIDE){
                    realHideList.add(itemList.remove(i));
                    i--;
                }
            }

            //分析控件存在并设置其结束状态
            for(int i=0; i<itemList.size(); i++){
                switch (itemList.get(i).viewtype){
                    case bpmForm_Item.RADIO_VIEW:
                        radiofinish=false;
                        break;
                }
            }

            //设置前一个元素 分析隐藏元素
            for(int i=1; i<itemList.size(); i++){
                itemList.get(i).previousItem = itemList.get(i-1);
            }

            for(int i=0; i<itemList.size(); i++){
                bpmForm_Item item = itemList.get(i);
                if(!item.rule.isEmpty()){
                    String[] strings = item.rule.split(" ");
                    if(strings[0].equals("HIDE") && !item.value.equals("true")){
                        setHide(strings);
                    }
                }
            }

            for(int i=0; i<itemList.size(); i++){
                if(itemList.get(i).isHide){
                    hideList.add(itemList.get(i));
                    itemList.remove(i);
                    i--;
                }
            }

            //设置expand值
            for(int i=0; i<itemList.size(); i++) {
                bpmForm_Item item = itemList.get(i);
                if (item.type == bpmForm_Item.CLIENT) {

                    if (item.expandInfo != null) {

                        //设置扩展项
                        item.expandInfo.setValue(item.value);

                        //添加扩展项
                        item.expandInfo.setExpand();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void clearValue(){
        for(bpmForm_Item item: itemList)
            item.value = "";
        for(bpmForm_Item item: hideList)
            item.value = "";
        for(bpmForm_Item item: realHideList)
            item.value = "";
    }

    public void removeExpand(){
        //删掉所有扩展项
        for(int i=0; i<itemList.size(); i++){
            if(itemList.get(i).type== bpmForm_Item.EXPAND){
                itemList.remove(i);
                i--;
            }
        }
    }

    private void setHide(String[] strings){
        for(int i=1; i<strings.length; i++){
            String id = strings[i];
            for(int j=0; j<itemList.size(); j++)
                if(itemList.get(j).id.equals(id)) {
                    itemList.get(j).isHide = true;
                    break;
                }
        }
    }

    public View getView(boolean edit, boolean isSummary){
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //只读页面
        if(!edit){
            group = new bpmListViewForScrollView(activity);
            if(isSummary){
                for(int i=0; i<itemList.size(); i++){
                    if(!itemList.get(i).isSummary) {
                        itemList.remove(i);
                        i--;
                    }
                }
            }
            adapter_read = new bpmForm_Adapter_Read(activity, itemList, isSummary);
            group.setAdapter(adapter_read);
        }


        else{

            //设置添加删除按钮监听
            if(button_sub!=null) {
                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((bpmFormActivity)activity).addGroup(groupIndex);
                    }
                });

                button_sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((bpmFormActivity)activity).removeGroup(groupIndex);
                    }
                });
            }


            group = new bpmListViewForScrollView(activity);
            adapter = new bpmForm_Adapter_Write(((bpmFormActivity)activity), itemList, hideList, groupIndex);
            ((bpmListViewForScrollView)group).setAdapter(adapter);

            group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bpmForm_Item item = itemList.get(position);

                    //设置当前组index和itemindex
                    bpmFormActivity.currentGroupIndex = groupIndex;
                    bpmFormActivity.currentItemIndex = position;

                    switch (item.viewtype){
                        //点击radio
                        case bpmForm_Item.RADIO_VIEW:{
                            radiofinish = true;
                            item.value="true";
                            for(int i=0; i<itemList.size(); i++){
                                bpmForm_Item currentItem = itemList.get(i);
                                if(currentItem.viewtype== bpmForm_Item.RADIO_VIEW && i!=position)
                                    currentItem.value="false";

                            }
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        //checkbox
                        case bpmForm_Item.CHECKBOX_VIEW:{
                            item.value = item.value.equals("true")?"false":"true";
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        //搜索
                        case bpmForm_Item.SEARCH_VIEW:{
                            Intent intent=null;

                            switch(item.type){
                                case bpmForm_Item.ADDRESS:{
                                    intent = new Intent(activity, bpmSortListMainActivity.class);
                                    intent.putExtra("title", "选择"+item.name);
                                    intent.putExtra("type", bpmSortListMainActivity.TYPE_PLACE);
                                    intent.putExtra("data", bpmMainActivity.dbManager.getPlaces());
                                    break;
                                }
                                case bpmForm_Item.STAFF:{
                                    intent = new Intent(activity, bpmSortListMainActivity.class);
                                    intent.putExtra("title", "选择"+item.name);
                                    intent.putExtra("data", bpmFormActivity.getUserList());
                                    break;
                                }
                                case bpmForm_Item.CLIENT:{
                                    intent = new Intent(activity, bpmClientActivity.class);
                                    break;
                                }
                                case bpmForm_Item.ITEM:{
                                    intent = new Intent(activity, bpmSortListMainActivity.class);
                                    intent.putExtra("title", "选择"+item.name);
                                    intent.putExtra("type", bpmSortListMainActivity.TYPE_ITEM);
                                    intent.putExtra("data", new String[]{"手机, 100", "电脑, 200", "塑料小人, 300", "音响, 400"});
                                    break;
                                }
                            }

                            if(item.rule.split(" ")[0].equals("MULTI")) {
                                intent.putExtra("multichoice", true);
                                intent.putExtra("indexList", item.valueList);
                            }



                            activity.startActivityForResult(intent, bpmFormActivity.REQUEST_SEARCH);

                            break;
                        }
                        //时间选择
                        case bpmForm_Item.DATE_VIEW:{
                            if(item.rule.isEmpty())
                                activity.showDialog(bpmFormActivity.DATE_DATETIME);
                            else if(item.rule.equals("DAY"))
                                activity.showDialog(bpmFormActivity.DATE_DEFAULTDATE);
                            else if(item.rule.equals("WEEK"))
                                activity.showDialog(bpmFormActivity.DATE_WEEK);
                            else if(item.rule.equals("MONTH"))
                                activity.showDialog(bpmFormActivity.DATE_MONTH);
                            else if(item.rule.equals("YEAR"))
                                activity.showDialog(bpmFormActivity.DATE_YEAR);
                            else if(item.rule.equals("SEASON"))
                                activity.showDialog(bpmFormActivity.DATE_SEASON);

                            break;
                        }
                        //popupwindow select
                        case bpmForm_Item.SELECT_VIEW:{
                            ((bpmFormActivity)activity).initPopupWindow(item);
                            break;
                        }

                    }
                }
            });
        }




        return group;
    }

    public boolean isFinished(){

        //判断是否填完
        for(int i=0; i<itemList.size(); i++){
            bpmForm_Item item = itemList.get(i);
            if(!item.isFinished())
                return false;
        }


        //radio
        if(!radiofinish)
            return false;

        return true;
    }


    public JSONObject getJSONObject(boolean isDraft){
        removeExpand();

        //还原
        for(int i=0; i<hideList.size(); i++){
            //还原非隐藏item
            bpmForm_Item item = hideList.get(i);
            item.isHide=false;

            bpmForm_Item previousItem = item.previousItem;
            while(previousItem!=null && previousItem.isHide)
                previousItem = previousItem.previousItem;

            if(previousItem==null)
                itemList.add(0, item);
            else{
                for(int j=0; j<itemList.size(); j++)
                    if(itemList.get(j)==previousItem) {

                        itemList.add(j + 1, item);
                        break;
                    }
            }
        }

        JSONObject group = new JSONObject();

        try {
            group.put("groupId", bpmTools.Int2String(groupId));
            group.put("groupType", groupType);
            if(isDraft)
                group.put("summaryType", summaryType).put("name", groupName);
            JSONArray datas = new JSONArray();
            for(int i=0; i<itemList.size();i++)
                datas.put(itemList.get(i).getJSONObject(isDraft));

            for(int i=0; i<realHideList.size();i++)
                datas.put(realHideList.get(i).getJSONObject(isDraft));
            group.put("datas", datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return group;
    }


}
