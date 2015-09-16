package com.example.spinel.myapplication.Form;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.spinel.myapplication.DateSlider.bpmDateSlider;
import com.example.spinel.myapplication.DateSlider.bpmDateTimeSlider;
import com.example.spinel.myapplication.DateSlider.bpmDefaultDateSlider;
import com.example.spinel.myapplication.DateSlider.bpmMonthYearDateSlider;
import com.example.spinel.myapplication.DateSlider.bpmWeekDateSlider;
import com.example.spinel.myapplication.DateSlider.bpmYearDateSlider;
import com.example.spinel.myapplication.DateSlider.labeler.bpmTimeLabeler;
import com.example.spinel.myapplication.DateSlider.bpmSeasonDateSlider;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.SortListView.bpmSortListMainActivity;
import com.example.spinel.myapplication.bpmStructure;
import com.example.spinel.myapplication.bpmTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class bpmFormActivity extends ActionBarActivity{
    private ArrayList<bpmForm_Group> groupList;
/*    private boolean edit;
    private boolean isNewActivity;
    private boolean isRestart;
    boolean isDraft;*/
    private String title;

    public static int REQUEST_SEARCH=0;

    public static int currentGroupIndex, currentItemIndex;

    LinearLayout linearLayout;
    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    //抄送和执行
    bpmForm_Group extra_group;

    //group JSON。 保存草稿
    JSONArray groups;
    JSONArray blankgroups;
    ArrayList<Object> draft;
    private boolean hasDraft; //是否有草稿

    //handler
    public Handler handler;

    //状态：只读，填写审批，填写审批（不可改只选择下个人），重填开始表单
    public static int STATE_READ=0, STATE_START=1, STATE_RESTART=2, STATE_REVIEW=3, STATE_REVIEW_READONLY=4, STATE_DRAFT=5;
    private int STATE;

    //下载文件
    public ArrayList<String> dFiles=null;


    //Activity返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_SEARCH){
            if(resultCode == RESULT_OK){

                //extra group
                if(currentGroupIndex==-1){
                    bpmForm_Item item = extra_group.itemList.get(currentItemIndex);
                    if(item.rule.equals("MULTI")){
                        ArrayList<Object> list = (ArrayList<Object>) data.getExtras().get("data");
                        item.valueList = (ArrayList<Integer>) list.get(1);
                        item.value = bpmTools.getUserIdListString(item.valueList);

                        String text = item.name;
                        if(item.valueList.size()!=0)
                            text += (" ("+item.valueList.size()+")");

                        if(currentItemIndex==1)
                            ((Button)findViewById(R.id.button_left)).setText(text);
                        else
                            ((Button)findViewById(R.id.button_right)).setText(text);
                    }
                    else{
                        item.value = bpmMainActivity.structure.userList.get(data.getIntExtra("index", 0)).id;
                        if(currentItemIndex==1)
                            ((Button)findViewById(R.id.button_left)).setText(item.name+": "+data.getStringExtra("data"));
                        else
                            ((Button)findViewById(R.id.button_right)).setText(item.name+": "+data.getStringExtra("data"));
                    }

                }

                //正常 group
                else {
                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);

                    //客户
                    if(item.type== bpmForm_Item.CLIENT){
                        //client value 格式： 公司id_公司名称 address 地址 contact 联系人id_联系人 telephone 电话 contact 联系人1id_联系人2 telephone 电话2
//                        item.value = "34_云尚公司 address 北航 contact 55_laigus telephone 123456789 contact 48_spinel telephone 987654321";
                        item.value = data.getStringExtra("data");

                        if(item.expandInfo!=null){
                            //先删掉所有已有扩展项
                            groupList.get(currentGroupIndex).removeExpand();

                            //设置扩展项
                            item.expandInfo.setValue(item.value);

                            //添加扩展项
                            item.expandInfo.setExpand();
                        }
                    }

                    //多选
                    else if (item.rule.equals("MULTI")) {
                        ArrayList<Object> list = (ArrayList<Object>) data.getExtras().get("data");
                        ArrayList<String> stringList = (ArrayList<String>) list.get(0);
                        ArrayList<Integer> intList = (ArrayList<Integer>) list.get(1);
                        //员工
                        if (item.type == bpmForm_Item.STAFF) {
                            item.value = bpmTools.getUserIdListString(intList);
                        } else
                            item.value = bpmTools.converntListToString(stringList);
                        item.valueList = intList;
                    }

                    //单选
                    else {
                        item.valueList.clear();
                        item.valueList.add(data.getIntExtra("index", 0));

                        //员工
                        if (item.type == bpmForm_Item.STAFF)
                            item.value = bpmTools.getUserIdListString(item.valueList);
                        /**
                         * 物品
                         * value：物品名称 inventories 库存数量
                         * rule: SINGLE DEFINE kucun inventories
                         */
                        else if(item.type == bpmForm_Item.ITEM){
                            String[] strs = data.getStringExtra("data").split(" ");
                            item.value = strs[0];

                            String[] rules = item.rule.split(" ");
                            if(rules.length>1 && rules[1].equals("DEFINE")){
                                String target, source;
                                for(int i=2; i<rules.length-1; i+=2){
                                    target = rules[i];
                                    source = rules[i+1];
                                    setDefine(target, source, strs);
                                }
                            }

                        }
                        else
                            item.value = data.getStringExtra("data");
                    }
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();

                }
            }
        }
    }

    private void setDefine(String targetKey, String sourceKey, String[] sourceValues){
        String source="";

        //找到源值
        for(int i=0; i<sourceValues.length-1; i++){
            if(sourceKey.equals(sourceValues[i])){
                source = sourceValues[i+1];
                break;
            }
        }

        //找到目标item
        ArrayList<bpmForm_Item> itemList = groupList.get(currentGroupIndex).itemList;
        for(bpmForm_Item item: itemList)
            if(item.id.equals(targetKey)){
                item.value = source;
                return;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bpm_activity_form);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupList = new ArrayList<bpmForm_Group>();

        initView();
    }

    private void initView(){
        //获得参数
        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        STATE = intent.getIntExtra("state", STATE_READ);
        draft = (ArrayList)intent.getSerializableExtra("draft");

        if(STATE!=STATE_DRAFT && STATE!=STATE_RESTART)
            hasDraft=false;
        else {
            hasDraft = true;
            try {
                blankgroups = new JSONArray((String)draft.get(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setTitle(title);

        if(STATE == STATE_READ){
            findViewById(R.id.botton_relative).setVisibility(View.GONE);
        }

        //JSON 解析

        try {
            Log.e("form", intent.getStringExtra("datas"));
            groups = new JSONArray(intent.getStringExtra("datas"));

            //组循环
            for(int i=0; i<groups.length(); i++){
                groupList.add(new bpmForm_Group((JSONObject)groups.get(i), i, this));

                if(hasDraft&&groupList.get(i).groupType.equals("multi"))
                    groupList.get(i).groupJSON = (JSONObject)blankgroups.get(i);
            }


            //去掉extra
            for(int i=0; i<groupList.size(); i++){
                if(groupList.get(i).groupName.equals("extra")) {
                    extra_group = groupList.remove(i);
                    if(STATE!=STATE_READ)
                        extra_group.clearValue();
                    i--;
                }
            }

            if(extra_group==null)
                findViewById(R.id.botton_relative).setVisibility(View.GONE);

            Collections.sort(groupList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //下载文件
        if(dFiles!=null && !dFiles.isEmpty()){
            Thread download = new Thread(new bpmHTTPDownloader(dFiles, this));
            download.start();
        }

        //生成界面
        linearLayout = (LinearLayout)findViewById(R.id.activity_form_linerLayout);
        for(int i=0; i<groupList.size(); i++){
            addGroupView(i, false);
        }

        //生成extra界面
        if(extra_group!=null&& STATE ==STATE_READ) {
            RelativeLayout interval = (RelativeLayout) getLayoutInflater().inflate(R.layout.bpm_form_title_summary_thin, null);
            ((TextView) interval.findViewById(R.id.form_title_text)).setText("");
            linearLayout.addView(interval);
            linearLayout.addView(extra_group.getView(false, false));
        }


        //草稿，设置组的原始页面，设置加减号
        try {
            if (hasDraft) {
                for (int i = 0; i < blankgroups.length(); i++) {
                    JSONObject group = (JSONObject) blankgroups.get(i);
                    if (group.getString("groupType").equals("single"))
                        continue;
                    int groupId = Integer.parseInt(group.getString("groupId"));
                    for (int j = 0; j < groupList.size(); j++) {
                        if (groupList.get(j).groupId == groupId)
                            groupList.get(j).groupJSON = (JSONObject) blankgroups.get(i);
                    }
                    refreshGroupTitle(groupId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //extra 监听

        ((Button)findViewById(R.id.button_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGroupIndex = -1;
                currentItemIndex = 0;
                bpmForm_Item item = extra_group.itemList.get(currentItemIndex);

                Intent intent = new Intent(bpmFormActivity.this, bpmSortListMainActivity.class);
                intent.putExtra("title", "选择"+item.name);
                intent.putExtra("data", getUserList());

                intent.putExtra("multichoice", item.rule.equals("MULTI"));
                intent.putExtra("indexList", extra_group.itemList.get(currentItemIndex).valueList);

                startActivityForResult(intent, bpmFormActivity.REQUEST_SEARCH);
            }
        });

        ((Button)findViewById(R.id.button_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGroupIndex = -1;
                currentItemIndex = 1;
                bpmForm_Item item = extra_group.itemList.get(currentItemIndex);

                Intent intent = new Intent(bpmFormActivity.this, bpmSortListMainActivity.class);
                intent.putExtra("title", "选择"+item.name);
                intent.putExtra("data", getUserList());

                intent.putExtra("multichoice", item.rule.equals("MULTI"));
                intent.putExtra("indexList", extra_group.itemList.get(currentItemIndex).valueList);

                startActivityForResult(intent, bpmFormActivity.REQUEST_SEARCH);

            }
        });

        //handler
        handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                super.handleMessage(message);

                if(message.obj!=null && message.obj instanceof bpmRecorder)
                    ((bpmRecorder)message.obj).stopPlay();
                //下载完成
                else if(message.arg1==0){
                    for(bpmForm_Group group: groupList) {
                        for (bpmForm_Item item : group.itemList)
                            if (item.type == bpmForm_Item.VOICE && !item.recorder.filename.isEmpty() && !item.recorder.hasVoice)
                                item.recorder.refreshFile();
                        if(group.adapter!=null)
                            group.adapter.notifyDataSetChanged();
                        else if(group.adapter_read!=null)
                            group.adapter_read.notifyDataSetChanged();
                    }

                }
            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bpm_menu_form, menu);
        if(STATE == STATE_READ)
            menu.removeItem(R.id.form_ok);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.form_ok) {

            boolean state = true;
            if (STATE != STATE_REVIEW_READONLY)
                for (int i = 0; i < groupList.size(); i++) {
                    if (!groupList.get(i).isFinished()) {
                        state = false;
                        break;
                    }
                }
            if (state) {
                if (STATE == STATE_DRAFT)
                    bpmMainActivity.dbManager.deleteDraft((Integer) draft.get(0));

                submit();
                finish();
            }
        } else if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            if (STATE == STATE_START || STATE == STATE_RESTART || STATE == STATE_DRAFT)
                startDraftDialog();
            else
                finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            if(STATE==STATE_START || STATE==STATE_RESTART || STATE==STATE_DRAFT)
                startDraftDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startDraftDialog(){
        //草稿 存入数据库？
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否保存到草稿？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (STATE==STATE_DRAFT) {
                            int id = (Integer) draft.get(0);
                            bpmMainActivity.dbManager.saveDraft(id, getDraft());
                        } else if(STATE==STATE_RESTART){
                            bpmMainActivity.dbManager.saveDraft(title, getDraft(), blankgroups.toString());
                        } else
                            bpmMainActivity.dbManager.saveDraft(title, getDraft(), groups.toString());
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    private String getDraft(){
        JSONArray datas = new JSONArray();

        //group
        for(int i=0;i<groupList.size();i++){
            datas.put(groupList.get(i).getJSONObject(true));
        }

        datas.put(extra_group.getJSONObject(true));

        return datas.toString();
    }

    private void submit(){
        JSONArray datas = new JSONArray();

        //group
        for(int i=0;i<groupList.size();i++){
           datas.put(groupList.get(i).getJSONObject(false));
        }

        Intent intent = new Intent();

        //没有执行人
        if(extra_group==null){
            intent.putExtra("data", datas.toString());
            setResult(RESULT_OK, intent);
            return;
        }

        bpmForm_Item opUserItem = extra_group.itemList.get(0);
        //执行人为一个
        if(opUserItem.rule.isEmpty()) {
            datas.put(extra_group.getJSONObject(false));
            intent.putExtra("data", datas.toString());
        }

        //执行人为多个 （指令）
        else{
            JSONArray datas_multi = new JSONArray();
            String[] opUsers = opUserItem.value.split(", ");

            for(int i=0; i<opUsers.length; i++){
                opUserItem.value=opUsers[i];
                try {
                    JSONArray currentData = new JSONArray(datas.toString());
                    currentData.put(extra_group.getJSONObject(false));
                    datas_multi.put(currentData);
                } catch(JSONException e){

                }
            }
            intent.putExtra("data", datas_multi.toString());
        }


        //判断是否有录音
        ArrayList<String> voiceList = new ArrayList<>();

        for(bpmForm_Group group: groupList)
        for(bpmForm_Item form_item: group.itemList)
        if(form_item.type == bpmForm_Item.VOICE && form_item.recorder.hasVoice){
            Log.e("hasVoice", "有录音");
            voiceList.add(form_item.recorder.filename);
        }

        Log.e("form voiceList", "" + voiceList.size());

        intent.putStringArrayListExtra("voiceList", voiceList);

        setResult(RESULT_OK, intent);
    }

    public static String[] getUserList(){
        List<bpmStructure.User> users = bpmMainActivity.structure.userList;
        String[] userList = new String[users.size()];

        for(int i=0; i<users.size(); i++){
            userList[i] = users.get(i).name;
        }
        return userList;
    }

    //添加group
    private void addGroupView(int i, boolean subVisible){
        //生成间隔
        RelativeLayout interval = (RelativeLayout)getLayoutInflater().inflate(R.layout.bpm_form_title, null);
        ((TextView)interval.findViewById(R.id.form_title_text)).setText(groupList.get(i).groupName);
        linearLayout.addView(interval, i*2);

        //处理添加删除按钮
        ImageButton button_sub = (ImageButton)interval.findViewById(R.id.imageButton_sub);
        ImageButton button_add = (ImageButton)interval.findViewById(R.id.imageButton_add);

        button_sub.setVisibility(subVisible?View.VISIBLE:View.INVISIBLE);

        boolean edit = STATE!=STATE_READ && STATE!=STATE_REVIEW_READONLY;
        if(edit) {
            if (groupList.get(i).groupType.equals("single"))
                button_add.setVisibility(View.INVISIBLE);
            else {
                groupList.get(i).button_add = button_add;
                groupList.get(i).button_sub = button_sub;
            }
        }
        else
            button_add.setVisibility(View.INVISIBLE);

        //行程内容
        linearLayout.addView(groupList.get(i).getView(edit, false), i*2+1, layout);
    }

    //multi group 函数
    public void addGroup(int index){

        groupList.add(index+1, new bpmForm_Group(groupList.get(index).groupJSON, index+1, this)); //groupList 添加group
        refreshGroupList(index+2);//更新groupList中的index

        addGroupView(index+1, true);//添加view

        refreshGroupTitle(groupList.get(index).groupId);//更新标题

        ((ScrollView)findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_DOWN);//移动到最下面
    }

    public void removeGroup(int index){
        int groupId = groupList.get(index).groupId;

        groupList.remove(index);
        refreshGroupList(index);

        linearLayout.removeViewAt(index*2);
        linearLayout.removeViewAt(index*2);

        refreshGroupTitle(groupId);
    }

    private void refreshGroupList(int index){
        for(int i=index; i<groupList.size(); i++)
            groupList.get(i).groupIndex=i;
    }

    private void refreshGroupTitle(int groupId){

        //找到包含index的group范围
        int begin, end;

        for(begin=0;groupList.get(begin).groupId!=groupId;begin++){
        }
        for(end=begin+1;end<groupList.size() && groupList.get(end).groupId==groupId;end++);
        end--;


        String name = groupList.get(begin).groupName;
        if(begin==end) {
            ((TextView) linearLayout.getChildAt(begin * 2).findViewById(R.id.form_title_text)).setText(name);
            linearLayout.getChildAt(begin * 2).findViewById(R.id.imageButton_sub).setVisibility(View.INVISIBLE);
        }

        else{
            for(int i=begin; i<=end; i++){
                ((TextView)linearLayout.getChildAt(i*2).findViewById(R.id.form_title_text)).setText(name+ bpmTools.intToZH(i - begin + 1));
                linearLayout.getChildAt(i * 2).findViewById(R.id.imageButton_sub).setVisibility(View.VISIBLE);
            }
        }
    }

    //获得gourpIndex为止的长度
    public void scrollToMeasuredHeight(int groupIndex){
        int height=0;
        for(int i=0; i<groupIndex; i++){
            height += linearLayout.getChildAt(i*2).getMeasuredHeight();
            height += linearLayout.getChildAt(i*2+1).getMeasuredHeight();
        }

        findViewById(R.id.scrollView).scrollTo(0, height);
    }



    //popupwindow
    View view;
    PopupWindow popupWindow;
    public void initPopupWindow(final bpmForm_Item item){
        //分析item rule
        view = getLayoutInflater().inflate(R.layout.bpm_popupwindow_form, null);
        LinearLayout content = (LinearLayout)view.findViewById(R.id.content);


        //生成content
        final String[] rule = item.rule.split(" ");
        final ArrayList<Integer> valueList = item.valueList;
        final ArrayList<View> viewList = new ArrayList<>();

        if(rule[1].equals("MULTI")){

            //生成列表
            for(int i=2; i<rule.length; i++){
                if(rule[i].equals("其他")){
                    View childView = getLayoutInflater().inflate(R.layout.bpm_popupwindow_form_checkelse, null);

                    CheckBox cb = (CheckBox) childView.findViewById(R.id.checkbox);
                    cb.setText(rule[i]);

                    final EditText editText = (EditText)childView.findViewById(R.id.editText);
                    editText.setEnabled(false);

                    content.addView(childView);
                    viewList.add(childView);

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b)
                                editText.setEnabled(true);
                            else
                                editText.setEnabled(false);
                        }
                    });
                }
                else {
                    View childView = getLayoutInflater().inflate(R.layout.bpm_popupwindow_form_checkitem, null);
                    CheckBox cb = (CheckBox) childView.findViewById(R.id.checkbox);
                    cb.setText(rule[i]);
                    content.addView(childView);
                    viewList.add(childView);
                }
            }

            //设置check
            for(int i=0; i<valueList.size(); i++){
                CheckBox cb = (CheckBox)viewList.get(valueList.get(i)).findViewById(R.id.checkbox);
                cb.setChecked(true);
                if(cb.getText().equals("其他")) {
                    EditText editText = (EditText)viewList.get(valueList.get(i)).findViewById(R.id.editText);
                    editText.setEnabled(true);
                    editText.setText(item.otherInfo);
                }

            }
        }
        else{
            //生成列表
            for(int i=2; i<rule.length; i++){
                final boolean iselse = rule[i].equals("其他");

                final View childView = getLayoutInflater().inflate(
                        iselse ? R.layout.bpm_popupwindow_form_radioelse : R.layout.bpm_popupwindow_form_radioitem, null);

                RadioButton rb = (RadioButton)childView.findViewById(R.id.radiobutton);
                rb.setText(rule[i]);
                rb.setTag(i);

                if(iselse){
                    final EditText editText = (EditText)childView.findViewById(R.id.editText);
                    editText.setEnabled(false);
                }

                content.addView(childView);
                viewList.add(childView);
                rb.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        for(int i=0; i<viewList.size(); i++){
                            RadioButton radioButton = (RadioButton)viewList.get(i).findViewById(R.id.radiobutton);

                            if(((Integer)view.getTag()).equals((Integer)radioButton.getTag())) {
                                radioButton.setChecked(true);
                                if(radioButton.getText().equals("其他"))
                                    viewList.get(i).findViewById(R.id.editText).setEnabled(true);
                            }
                            else {
                                radioButton.setChecked(false);
                                if(radioButton.getText().equals("其他"))
                                    viewList.get(i).findViewById(R.id.editText).setEnabled(false);
                            }
                        }
                    }
                });
            }

            if(valueList.size()!=0) {

                RadioButton rb = (RadioButton)viewList.get(valueList.get(0)).findViewById(R.id.radiobutton);
                rb.setChecked(true);
                if(rb.getText().equals("其他")) {
                    EditText editText = (EditText)viewList.get(valueList.get(0)).findViewById(R.id.editText);
                    editText.setEnabled(true);
                    editText.setText(item.otherInfo);
                }
            }
        }


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);

        //背景变暗
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupWindow.showAtLocation(findViewById(R.id.activity_form_linerLayout), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });


        //设置确认取消监听
        ((Button)view.findViewById(R.id.btn_transport_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        ((Button)view.findViewById(R.id.btn_transport_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueList.clear();
                for(int i=0; i<viewList.size(); i++){
                    if(rule[1].equals("MULTI")){
                        CheckBox cb = (CheckBox)viewList.get(i).findViewById(R.id.checkbox);
                        if(cb.isChecked()) {
                            valueList.add(i);
                            if(cb.getText().equals("其他")){
                                item.otherInfo = ((EditText)viewList.get(i).findViewById(R.id.editText)).getText().toString();
                                if(item.otherInfo.isEmpty())
                                    item.otherInfo=" ";
                            }
                        }
                    }
                    else{
                        RadioButton rb = (RadioButton)viewList.get(i).findViewById(R.id.radiobutton);
                        if(rb.isChecked()){
                            valueList.add(i);
                            if(rb.getText().equals("其他")){
                                item.otherInfo = ((EditText)viewList.get(i).findViewById(R.id.editText)).getText().toString();
                                if(item.otherInfo.isEmpty())
                                    item.otherInfo=" ";
                            }
                            break;
                        }
                    }
                }

                groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

    }


    //------------------选择时间监听器------------------
    //选择时间：
    public static final int DATE_DATETIME=0, DATE_DEFAULTDATE=1, DATE_WEEK=2, DATE_MONTH=3, DATE_YEAR=4, DATE_SEASON=5;

    // 日期时间
    private bpmDateSlider.OnDateSetListener mDateTimeSetListener =
            new bpmDateSlider.OnDateSetListener() {
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    // update the dateText view with the corresponding date
                    int minute = selectedDate.get(Calendar.MINUTE) /
                            bpmTimeLabeler.MINUTEINTERVAL* bpmTimeLabeler.MINUTEINTERVAL;
                    selectedDate.set(Calendar.MINUTE, minute);
                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar)selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };
    // 日 2015年 7月 8日
    private bpmDateSlider.OnDateSetListener mDateSetListener =
            new bpmDateSlider.OnDateSetListener() {
                @Override
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);

                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar) selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };
    // 周 2015年 7月 第2周
    private bpmDateSlider.OnDateSetListener mWeekSetListener =
            new bpmDateSlider.OnDateSetListener() {
                @Override
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);
                    selectedDate.setFirstDayOfWeek(Calendar.MONDAY);
                    selectedDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar) selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };
    // 月 2015年 7月
    private bpmDateSlider.OnDateSetListener mMonthSetListener =
            new bpmDateSlider.OnDateSetListener() {
                @Override
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    selectedDate.set(Calendar.DAY_OF_MONTH, 1);
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);

                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar) selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };
    // 年 2015年 7月
    private bpmDateSlider.OnDateSetListener mYearSetListener =
            new bpmDateSlider.OnDateSetListener() {
                @Override
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    selectedDate.set(Calendar.DAY_OF_YEAR, 1);
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);

                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar) selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };
    // 季 2015年 第一季度
    private bpmDateSlider.OnDateSetListener mSeasonSetListener =
            new bpmDateSlider.OnDateSetListener() {
                @Override
                public void onDateSet(bpmDateSlider view, Calendar selectedDate) {
                    selectedDate.set(Calendar.DAY_OF_MONTH, 1);
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                    selectedDate.set(Calendar.MINUTE, 0);
                    selectedDate.set(Calendar.SECOND, 0);


                    int currentMonth = selectedDate.get(Calendar.MONTH) + 1;
                    if (currentMonth >= 1 && currentMonth <= 3)
                        selectedDate.set(Calendar.MONTH, 0);
                    else if (currentMonth >= 4 && currentMonth <= 6)
                        selectedDate.set(Calendar.MONTH, 3);
                    else if (currentMonth >= 7 && currentMonth <= 9)
                        selectedDate.set(Calendar.MONTH, 6);
                    else if (currentMonth >= 10 && currentMonth <= 12)
                        selectedDate.set(Calendar.MONTH, 9);

                    bpmForm_Item item = groupList.get(currentGroupIndex).itemList.get(currentItemIndex);
                    item.valueTime = (Calendar) selectedDate.clone();
                    groupList.get(currentGroupIndex).adapter.notifyDataSetChanged();
                }
            };

    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DATETIME:
                return new bpmDateTimeSlider(this, mDateTimeSetListener, Calendar.getInstance());
            case DATE_DEFAULTDATE:
                return new bpmDefaultDateSlider(this, mDateSetListener, Calendar.getInstance());
            case DATE_WEEK:
                return new bpmWeekDateSlider(this, mWeekSetListener, Calendar.getInstance());
            case DATE_MONTH:
                return new bpmMonthYearDateSlider(this, mMonthSetListener, Calendar.getInstance());
            case DATE_YEAR:
                return new bpmYearDateSlider(this, mYearSetListener, Calendar.getInstance());
            default:
                return new bpmSeasonDateSlider(this, mSeasonSetListener, Calendar.getInstance());

        }
    }


}
