package com.example.spinel.myapplication.Form;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.bpmTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by Spinel on 2015/7/23.
 */
public class bpmForm_Item implements Serializable{
    public String id, value; //这两个是要提交的
    String name; //名字和控件类型
    String type_str;

    String rule;//规则
    int type;//意义类型
    int viewtype; //视图类型
    boolean isSummary;
    String emptyHint;

    public bpmForm_Item previousItem;//每个元素前一个元素，第一个元素的前一个元素为null
    public boolean isHide;

    private Activity activity;
    private bpmForm_Group form_group;

    //额外信息
    public ArrayList<Integer> valueList = new ArrayList<>(); //额外信息
    public String otherInfo;
    public Calendar valueTime;//时间信息
    public bpmRecorder recorder = null;

    //-------------------------------可扩展组件-------------------------------
    //rule: EXPAND SINGLE_单位地址_address MULTI_联系人_contact MULTI_电话_telephone
    //value 格式： 公司id_公司名称 address 地址 contact 联系人id_联系人 telephone 电话 contact 联系人1id_联系人2 telephone 电话2
    public class ExpandInfo{
        List<ExpandInfoItem> clientInfoList;
        HashMap<String, String> nameMap;

        public ExpandInfo(){
            clientInfoList = new ArrayList<>();

            //获得 key-名字 map
            String rules[] = rule.split(" ");
            if(rules[0].equals("EXPAND")) {
                nameMap = new HashMap<>();
                for(int i=1; i<rules.length; i++){
                    String[] info = rules[i].split("_");
                    nameMap.put(info[2], info[1]);
                }
            }
        }

        public void setValue(String str){
            clientInfoList = new ArrayList<>();
            String[] strings = str.split(" ");

            for(int i=0; i<strings.length-1; i++){
                if(nameMap.containsKey(strings[i])){
                    String name = nameMap.get(strings[i]);
                    String[] values = strings[i+1].split("_");
                    String value = values.length==1 ? values[0] : values[1];
                    clientInfoList.add(new ExpandInfoItem(strings[i], name, value));
                }
            }
        }


        public void setExpand(){
            //先找到item在group中的位置
            int position;
            ArrayList<bpmForm_Item> list = form_group.itemList;

            for(position=0; position<list.size(); position++){
                if(list.get(position).id.equals(id))
                    break;
            }

            if(position==list.size())
                return;

            for(ExpandInfoItem item: clientInfoList){
                position++;

                bpmForm_Item newitem = new bpmForm_Item(item.key, item.name, item.value);
                list.add(position, newitem);
            }
        }
    }
    public class ExpandInfoItem{
        String key, name, value;
        public ExpandInfoItem(String key, String name, String value){
            this.key = key;
            this.name = name;
            this.value = value;
        }
    }
    public ExpandInfo expandInfo;

    //-------------------------------type定义-------------------------------
    public static final int RADIO=0, TEXTFILED=1, STAFF=2, ADDRESS=3, DATE=4, SELECT=5, CHECKBOX=6, TEXTAREA=7, CALCULATE=8, SWITCH=9, CLIENT=10, HIDE=11, VOICE=12, EXPAND=13, ITEM=14;

    public static final int VIEW_TYPE_COUNT = 10;
    public static final int RADIO_VIEW=0, EDITTEXT_VIEW=1, TEXT_VIEW=2, SEARCH_VIEW=3, DATE_VIEW=4, SELECT_VIEW=5, CHECKBOX_VIEW=6, TEXTAREA_VIEW=7, SWITCH_VIEW=8, VOICE_VIEW=9;


    //-------------------------------基本函数-------------------------------
    public bpmForm_Item(JSONObject item, Activity activity, bpmForm_Group form_group){
        this.form_group = form_group;
        this.activity = activity;
        try {
            id = item.getString("id");
            value = item.getString("value");
            name = item.getString("name");
            rule = item.getString("rule");
            isSummary = item.getString("isSummary").equals("true");
            isHide=false;
            emptyHint = item.isNull("emptyHint")?"":item.getString("emptyHint");

            String str = item.getString("type");
            type_str = str;

            //扩展
            String[] rules = rule.split(" ");
            if(rules.length>0 && rules[0].equals("EXPAND"))
                expandInfo = new ExpandInfo();

            //类型判断
            if(str.equals("radio")) {
                type = RADIO;
                viewtype = RADIO_VIEW;
            }
            else if(str.equals("textFiled")) {
                type = TEXTFILED;

                if(rule.isEmpty() || rule.split(" ")[0].equals("NUM"))
                    viewtype = EDITTEXT_VIEW;
                else if(isCalculator(rule.split(" ")[rule.split(" ").length-1])){
                    viewtype = TEXT_VIEW;
                    type = CALCULATE;
                }
                else if(rule.equals("CURRENTDATE")){
                    viewtype = TEXT_VIEW;
                    type = DATE;
                }
                else
                    viewtype = TEXT_VIEW;
            }
            else if(str.equals("staff")) {
                type = STAFF;
                if(rule.equals("creater"))
                    viewtype = TEXT_VIEW;
                else
                    viewtype = SEARCH_VIEW;

            }
            else if(str.equals("address")) {
                type = ADDRESS;
                viewtype = SEARCH_VIEW;
            }
            else if(str.equals("date")) {
                type = DATE;
                viewtype = DATE_VIEW;
            }
            else if(str.equals("select")) {
                type = SELECT;
                viewtype = SELECT_VIEW;
            }
            else if(str.equals("checkbox")) {
                type = CHECKBOX;
                viewtype = CHECKBOX_VIEW;
            }
            else if(str.equals("textArea")) {
                type = TEXTAREA;
                viewtype = TEXTAREA_VIEW;
            }
            else if(str.equals("switch")){
                type = SWITCH;
                viewtype = SWITCH_VIEW;
            }
            else if(str.equals("client")){
                type = CLIENT;
                viewtype = SEARCH_VIEW;
            }
            else if(str.equals("hide")){
                type = HIDE;
            }
            else if(str.equals("voice")){
                type = VOICE;
                viewtype = VOICE_VIEW;

                recorder = new bpmRecorder((bpmFormActivity)activity, form_group, id, value);
            }
            else if(str.equals("item")){
                type = ITEM;
                viewtype = SEARCH_VIEW;
            }

            getInitValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public bpmForm_Item(String id, String name, String value){
        this.id = id;
        this.name = name;
        this.value = value;

        this.type = EXPAND;
        this.viewtype = TEXT_VIEW;
        this.isSummary = false;
        this.emptyHint = "ignore";
    }

    private void getInitValue(){
        switch (type){
            case DATE:
                valueTime = bpmTools.Millisecond2Calendar(value);
                break;
            case SELECT:
                setSelectIndex();
                break;
        }
    }

    public void setSelectString(){
        String[] strings = rule.split(" ");
        ArrayList<String> result = new ArrayList<>();

        for(int i=0; i<valueList.size(); i++){
            String str = strings[valueList.get(i)+2];
            result.add(str.equals("其他") ? otherInfo : str);
        }

        value = bpmTools.converntListToString(result);
    }

    private void setSelectIndex(){
        if(value.isEmpty())
            return;
        String[] result = value.split(", ");
        String[] rules = rule.split(" ");

        for(String str: result){
            boolean found = false;

            for(int j=2; j<rules.length; j++){
                if(str.equals(rules[j])){
                    found=true;
                    valueList.add(j-2);
                    if(rules[1].equals("SINGLE"))
                        return;
                    break;
                }
            }

            if(!found){
                otherInfo = str;
                for(int j=2; j<rules.length; j++)
                    if(rules[j].equals("其他")){
                        valueList.add(j-2);
                        break;
                    }
            }
        }

        Collections.sort(valueList);
    }

    public JSONObject getJSONObject(boolean isDraft){
        JSONObject item = new JSONObject();

        try {
            item.put("id", id);

            switch(type) {
                case HIDE:
                    item.put("value", calculate(form_group.itemList));
                    break;
                case VOICE:
                    if (recorder.hasVoice)
                        item.put("value", recorder.filename);
                    else
                        item.put("value", "-");
                    break;
                default:
                    item.put("value", value);
                    break;
            }

            if(isDraft){
                item.put("name", name).put("rule", rule).put("isSummary", isSummary?"true":"false").put("type", type_str);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    //-------------------------------是否完成-------------------------------
    public boolean isFinished(){

        switch (viewtype){


            //edittext
            case bpmForm_Item.EDITTEXT_VIEW:
            case bpmForm_Item.TEXTAREA_VIEW:{
                String[] rules = rule.split(" ");
                if(rules.length>1 && rules[0].equals("NUM")){
                    String[] newrules = new String[rules.length-1];
                    for(int i=1; i<rules.length; i++)
                        newrules[i-1] = rules[i];

                    String result = calculate(form_group.itemList, newrules);
                    if(!result.equals(value)){
                        value = result;
                        form_group.adapter.notifyDataSetChanged();
                        return false;
                    }
                }
                break;
            }
        }


        if(emptyHint.equals("reject") && value.isEmpty()){
            Toast.makeText(activity, "请填写"+name, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //-------------------------------计算-------------------------------
    //是否为运算符
    public boolean isCalculator(String str){
        if(str.equals("COUNT") || str.equals("SUB") || str.equals("ADD") || str.equals("GT") || str.equals("ISEXISTS")
                || str.equals("LE"))
            return true;
        else
            return false;
    }

    public String calculate(List<bpmForm_Item> mList){
        String[] rules = rule.split(" ");

        if(rules.length>2 && rules[0].equals("LASTDAY"))
            return calculateLastday(mList, rules);
        return calculate(mList, rules);
    }

    public String calculate(List<bpmForm_Item> mList, String[] rules){
        Stack<String> stack = new Stack<>();

        for(int i=0; i<rules.length; i++){

            if(isCalculator(rules[i])){
                if(rules[i].equals("COUNT")){
                    String res = calc_count(mList, stack.pop());
                    stack.push(res);
                }
                else if(rules[i].equals("ADD")){
                    String res = calc_add(mList, stack.pop(), stack.pop());
                    stack.push(res);
                }
                else if(rules[i].equals("SUB")){
                    String b=stack.pop();
                    String res = calc_sub(mList, stack.pop(), b);
                    stack.push(res);
                }
                else if(rules[i].equals("GT")){
                    String b = stack.pop();
                    String a = stack.pop();
                    if(a=="")
                        return "";
                    boolean flag = isGT(mList, a, b);

                    if(flag)
                        stack.push(getParameterNum(a));
                    else{
                        stack.push(getParameterNum(b));
                        Toast.makeText(activity, name+"应该大于"+getParameterName(a), Toast.LENGTH_SHORT).show();
                    }
                }
                else if(rules[i].equals("LE")){
                    String b = stack.pop();
                    String a = stack.pop();
                    if(a.isEmpty())
                        return "";
                    boolean flag = isLE(mList, a, b);

                    if(flag)
                        stack.push(getParameterNum(a));
                    else{
                        stack.push(getParameterNum(b));
                        Toast.makeText(activity, name+"应该小于等于"+getParameterName(b), Toast.LENGTH_SHORT).show();
                    }
                }
                else if(rules[i].equals("ISEXISTS")){
                    String str = stack.pop();
                    for(int j=0; j<mList.size(); j++)
                        if(str.equals(mList.get(j).id)){
                            if(mList.get(j).value.isEmpty())
                                stack.push("false");
                            else
                                stack.push("true");
                        }
                }
            }
            else{
                stack.push(rules[i]);
            }
        }

        if(stack.size()==1)
            return stack.pop();
        else
            return "";
    }

    public String calculateLastday(List<bpmForm_Item> mList, String[] rules){
        String type = rules[1];
        String iden = rules[2];
        Calendar c=null;
        for(bpmForm_Item item: mList)
            if(item.id.equals(iden))
                c = item.valueTime;

        if(c==null)
            return "";

        return bpmTools.Calendar2Millisecond(bpmTools.getLastDay(c, type));
    }

    private String calc_count(List<bpmForm_Item> mList, String id){
        if(id.isEmpty())
            return "";

        for(int i=0; i<mList.size(); i++){
            if(mList.get(i).id.equals(id)){
                return bpmTools.Int2String(mList.get(i).valueList.size());
            }
        }
        return "0";
    }

    private String getParameterName(String param){

        if(param.isEmpty())
            return param;

        //数
        try {
            Double.parseDouble(param);

        //标识符
        }catch (NumberFormatException e){
            for(bpmForm_Item item: form_group.itemList){
                if(item.id.equals(param))
                    return item.name;
            }
        }

        return param;
    }

    private String getParameterNum(String param){


        if(param.isEmpty())
            return "";

        //数
        try {
            Double.parseDouble(param);

        //标识符
        }catch (NumberFormatException e){
            if(param.equals("this"))
                return value;
            for(bpmForm_Item item: form_group.itemList){
                if(item.id.equals(param))
                    return item.value;
            }
        }

        return param;
    }
    //------------未完 只有整数 缺少浮点数和标识符------------
    private String calc_add(List<bpmForm_Item> mList, String a, String b){
        if(a.isEmpty() || b.isEmpty())
            return "";

        int ia = Integer.parseInt(a);
        int ib = Integer.parseInt(b);

        return bpmTools.Int2String(ia + ib);
    }

    //------------未完, 只有标识符，没有数------------

    private String calc_sub(List<bpmForm_Item> mList, String a, String b){

        if(a.isEmpty() || b.isEmpty())
            return "";



        bpmForm_Item itema=null, itemb=null;
        for(int i=0; i<mList.size(); i++){
            bpmForm_Item item = mList.get(i);
            if(item.id.equals(a))
                itema = item;
            else if(item.id.equals(b))
                itemb = item;
        }

        if(itema==null || itemb==null) {
            return "";
        }


        if(itema.type==DATE && itemb.type==DATE){
            if(itema.valueTime==null || itemb.valueTime==null)
                return "";
            return ((Double) bpmTools.calculateDay(itemb.valueTime, itema.valueTime)).toString();
        }
        return "";
    }

    //未完 只有数
    private boolean isGT(List<bpmForm_Item> mList, String a, String b){

        if(a.isEmpty() || b.isEmpty())
            return false;

        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);

            if(da>db)
                return true;
            else
                return false;
        }catch (NumberFormatException e){
            return false;
        }
    }

    //未完，只有标识符
    private boolean isLE(List<bpmForm_Item> mList, String a, String b){

        if(a.isEmpty() || b.isEmpty())
            return false;

        String sa="", sb="";

        //获得标识符对应值串
        if(a.equals("this"))
            sa = value;
        if(b.equals("this"))
            sb = value;

        for(bpmForm_Item item: mList){
            if(item.id.equals(a))
                sa = item.value;
            if(item.id.equals(b))
                sb = item.value;
            if(!sa.isEmpty() && !sb.isEmpty())
                break;
        }

        //获得值并比较
        try {
            double da = Double.parseDouble(sa);
            double db = Double.parseDouble(sb);

            return da<=db;
        }catch (NumberFormatException e){
            return false;
        }

    }
}
