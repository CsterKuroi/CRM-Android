package com.example.spinel.myapplication;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spinel.myapplication.DataBase.bpmDBManager;
import com.example.spinel.myapplication.Form.bpmFormActivity;
import com.example.spinel.myapplication.Form.bpmHTTPUploader;
import com.example.spinel.myapplication.Form.bpmTraceProcessActivity;
import com.example.spinel.myapplication.Form.bpmTraceTaskActivity;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Draft;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Draft_DataHolder;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Review;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Submit;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Submit_DataHolder;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Task;
import com.example.spinel.myapplication.MainFragment.bpmMainFragment_Task_DataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class bpmMainActivity extends ActionBarActivity implements View.OnClickListener, Serializable {
    public static String userId;
    public static bpmStructure.User user;

    //traceprocess
/*    public static boolean needDealForm;
    public static boolean reStartForm =false;*/
    public static int TRACE_PROCESS_STATE = bpmTraceProcessActivity.STATE_READ;
    public static String currentDealForm;
    public static String currentFormTitle;


    public static WebSocketConnection mConnection;
    final String wsuri = "ws://101.200.189.127:1234/ws";
    //final String wsuri = "ws://192.168.191.1:1234/ws";
    //final String wsuri = "ws://192.168.50.114:1234/ws";

    public static enum REQUEST_CODE{STARTACTIVITY, REVIEW, TRACEPROCESS, TRACETASK}

    public static String currentProcessId="";
    public static String currentTaskId="";
    public static String currentStepId="";
    private boolean isConnected = false;

    //组织
    public static bpmStructure structure;

    //动态·bpmlist
    public static ArrayList<BPMGroup> BPMGroupList;
    public static int currentBPMIndex_group, currentBPMIndex_item;
    public static String currentActivityId="";

    //动态·popupwindow
    private PopupWindow popupWindow_main;
    private PopupWindow[] popupWindow_list;


    //spinner list index
    public static int spinnerIndex = 0;
    private Spinner mSpinner;

    //数据库
    public static bpmDBManager dbManager;

    //跳转到的页面类型wewe
    public static int reviewindex=0;

    //-----------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bpm_activity_main);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        initView();

        skipPage();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mConnection!=null && mConnection.isConnected())
            mConnection.disconnect();
    }

    @Override
    public void onClick(View v){
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST_CODE.STARTACTIVITY.ordinal()) {
            if (resultCode == RESULT_OK) {
                try {
                    JSONArray datas = new JSONArray(data.getStringExtra("data"));

                    if(!currentActivityId.isEmpty()){
                        if(getBPMItemGroupType(currentActivityId).equals("order"))
                            startTask(datas);
                        else
                            startActivity(datas);
                    }
                    else {
                        if (BPMGroupList.get(currentBPMIndex_group).type.equals("order"))
                            startTask(datas);
                        else
                            startActivity(datas);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendFile(data.getStringArrayListExtra("voiceList"));
            }
            else if(resultCode == RESULT_CANCELED)
                currentActivityId="";
        }

        else if(requestCode== REQUEST_CODE.TRACEPROCESS.ordinal()){
            if(resultCode== bpmTraceProcessActivity.RESULT_REVIEW) {
                try {
                    JSONArray datas = new JSONArray(data.getStringExtra("data"));
                    submit(datas);
                    currentActivityId="";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendFile(data.getStringArrayListExtra("voiceList"));
            }
            else if(resultCode== bpmTraceProcessActivity.RESULT_RESTART) {
                try {
                    JSONArray datas = new JSONArray(data.getStringExtra("data"));

                    if(!currentActivityId.isEmpty()){
                        if(getBPMItemGroupType(currentActivityId).equals("order"))
                            startTask(datas);
                        else
                            startActivity(datas);
                    }
                    else {
                        if (BPMGroupList.get(currentBPMIndex_group).type.equals("order"))
                            startTask(datas);
                        else
                            startActivity(datas);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendFile(data.getStringArrayListExtra("voiceList"));
            }

            else if(resultCode == RESULT_CANCELED)
                currentActivityId="";

        }

        else if(requestCode== REQUEST_CODE.TRACETASK.ordinal()) {
            if(resultCode== bpmTraceProcessActivity.RESULT_RESTART) {
                try {

                    JSONArray datas = new JSONArray(data.getStringExtra("data"));
                    addNewToTask(datas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendFile(data.getStringArrayListExtra("voiceList"));
            }
            else if(resultCode == RESULT_CANCELED)
                currentActivityId="";
        }

    }

    private void initView() {

        mConnection = new WebSocketConnection();
        dbManager = new bpmDBManager(this);


        //初始化组织架构
        structure = new bpmStructure(this);

        //初始化bpm
        initBPM();

        //据跳转到本页面的类型，跳到对应fragment
        skipPage();

        connect();


    }


    //----------动态·初始化 begin----------
    private void initBPM(){

        //----------动态·初始化 bpm group list----------
        initBPMGroupList();

        if(BPMGroupList.isEmpty())
            return;

        //----------动态·初始化 popupWindow_child----------
        initPopupWindow_Child();

        //----------动态·初始化 popupWindow_main----------
        initPopupWindow_Main();

        //----------动态·初始化 actionbar----------
        initBPMActionBar();
    }

    private void initBPMGroupList(){
        ArrayList<BPMItem> BPMList = dbManager.getBPMList();
        BPMGroupList = new ArrayList<>();

        for(BPMItem item: BPMList){
            int index = getBPMGroupIndex(item.type);

            if(index<0){
                BPMGroup group = new BPMGroup(item.type, item.info);
                group.addItem(item);
                BPMGroupList.add(group);
            }
            else
                BPMGroupList.get(index).addItem(item);
        }

        for(BPMGroup group: BPMGroupList)
            group.setSearchTypes();

    }

    private int getBPMGroupIndex(String type){
        for(int i=0; i<BPMGroupList.size(); i++){
            if(BPMGroupList.get(i).type.equals(type))
                return i;
        }
        return -1;
    }

    private String getBPMItemGroupType(String activityId){
        for(BPMGroup group: BPMGroupList)
            for(BPMItem item: group.list)
                if(item.activityId.equals(activityId))
                    return group.type;
        return "";
    }

    private void initPopupWindow_Main(){

        if(BPMGroupList==null)
            return;

        //window 属性
        View customView = getLayoutInflater().inflate(R.layout.bpm_popupwindow_main, null, false);
        popupWindow_main = new PopupWindow(customView, 300, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow_main.setAnimationStyle(R.style.AnimationFade);
        popupWindow_main.setBackgroundDrawable(new ColorDrawable(0x000000));
        popupWindow_main.setFocusable(true);
        popupWindow_main.setOutsideTouchable(true);



        //bpmgroup list
        String[] strings = new String[BPMGroupList.size()];
        for(int i=0; i<BPMGroupList.size(); i++)
            strings[i] = BPMGroupList.get(i).name;

        ListView lv = (ListView)customView.findViewById(R.id.listView_BPMGroupList);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentBPMIndex_group = i;

                //只有一个流程 直接开始表单
                if(BPMGroupList.get(i).list.size()==1){

                    Intent intent = new Intent(bpmMainActivity.this, bpmFormActivity.class);
                    BPMItem item = BPMGroupList.get(i).list.get(0);
                    intent.putExtra("datas", item.datas.toString());
                    intent.putExtra("title", item.activityName);
                    intent.putExtra("state", bpmFormActivity.STATE_START);
                    startActivityForResult(intent, REQUEST_CODE.STARTACTIVITY.ordinal());
                }
                //多个流程，启动popupwindow
                else{
                    showPopupWindow_Child(i);
                }

                popupWindow_main.dismiss();
            }
        });
    }

    private void initPopupWindow_Child(){
        popupWindow_list = new PopupWindow[BPMGroupList.size()];

        for(int i=0; i<BPMGroupList.size(); i++){
            //window属性
            View view = getLayoutInflater().inflate(R.layout.bpm_popupwindow_approve, null);
            popupWindow_list[i] = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            popupWindow_list[i].setBackgroundDrawable(new ColorDrawable(0x000000));
            popupWindow_list[i].setOutsideTouchable(true);
            popupWindow_list[i].setFocusable(true);
            popupWindow_list[i].setAnimationStyle(R.style.AnimationFade);

            //bpm列表
            ArrayList<BPMItem> list = BPMGroupList.get(i).list;
            String[] strings = new String[list.size()];
            for(int j=0; j<list.size(); j++ )
                strings[j] = list.get(j).activityName;

            //设置adapter、监听
            ListView lv = (ListView)view.findViewById(R.id.listView_BPMList);
            lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    currentBPMIndex_item = i;

                    Intent intent = new Intent(bpmMainActivity.this, bpmFormActivity.class);
                    BPMItem item = BPMGroupList.get(currentBPMIndex_group).list.get(i);
                    intent.putExtra("datas", item.datas.toString());
                    intent.putExtra("title", item.activityName);
                    intent.putExtra("state", bpmFormActivity.STATE_START);
                    startActivityForResult(intent, REQUEST_CODE.STARTACTIVITY.ordinal());

                    popupWindow_list[currentBPMIndex_group].dismiss();
                }
            });
        }
    }

    private void showPopupWindow_Child(int index){
        //背景变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        popupWindow_list[index].showAtLocation(findViewById(R.id.RelativeLayout_main), Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0, 0);

        popupWindow_list[index].update();
        popupWindow_list[index].setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void initBPMActionBar(){
        //初始化actionbar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setCustomView(LayoutInflater.from(this).inflate(R.layout.bpm_title_main, null));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        findViewById(R.id.imageButton_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow_main==null){
                    Toast.makeText(getApplicationContext(), "未读取到流程列表", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(popupWindow_main.isShowing()) {
                    popupWindow_main.dismiss();
                } else {
                    popupWindow_main.showAsDropDown(findViewById(R.id.imageButton_work), 0, 0);
                }
            }
        });

        findViewById(R.id.imageButton_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mConnection.isConnected() || !isConnected)
                    connect();
            }
        });



        //添加Tab选项、fragment
        final int groupsize = BPMGroupList.size();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Fragment f[][] = new Fragment[groupsize][];
        String f_tag[][] = new String[groupsize][];
        final bpmMyTabListener tabListeners[] = new bpmMyTabListener[groupsize];


        actionBar.removeAllTabs();
        for(int i=0; i<groupsize; i++){
            f[i] = new Fragment[6];
            f_tag[i] = new String[6];

            for(int j=0; j<6; j++){
                f_tag[i][j] = "f"+i+"_"+j;
                f[i][j] = new bpmMainFragment_Submit();

                //我提交的
                if(j==0){
                    if(BPMGroupList.get(i).type.equals("order"))
                        f[i][j] = new bpmMainFragment_Task();
                    else
                        f[i][j] = new bpmMainFragment_Submit();
                }
                else if(j==1)
                    f[i][j] = new bpmMainFragment_Review();
                else if(j==5)
                    f[i][j] = new bpmMainFragment_Draft();
                else
                    f[i][j] = new bpmMainFragment_Submit();
            }

            tabListeners[i] = new bpmMyTabListener(f[i], f_tag[i]);
            actionBar.addTab(actionBar.newTab().setText(BPMGroupList.get(i).name).setTabListener(tabListeners[i]));
        }


        //下拉列表
        mSpinner = (Spinner)findViewById(R.id.spinner_main);
        String[] strings = getResources().getStringArray(R.array.action_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, strings);
        mSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                TextView tv = (TextView)view;
                tv.setGravity(Gravity.CENTER_HORIZONTAL);

                spinnerIndex = position;

                for(int i=0; i<groupsize; i++)
                    tabListeners[i].changeIndex(position, getSupportFragmentManager().beginTransaction());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    //----------动态·初始化 end----------

    private void skipPage(){
        String type, activityId;
        type = getIntent().getStringExtra("type");
        activityId = getIntent().getStringExtra("activityId");

        int spinindex=0, tabindex=0;

        if(type!=null && !type.isEmpty()) {
            if (type.equals("executed")) {
                spinindex = 1;
                reviewindex = 1;
            }
            else if (type.equals("initiated"))
                spinindex = 0;
            else if (type.equals("toExecute"))
                spinindex = 1;
            else if (type.equals("partner"))
                spinindex = 4;
            else if (type.equals("care"))
                spinindex = 2;
            else if (type.equals("atme"))
                spinindex = 3;
            else if(type.equals("timeoutDeal")){
                spinindex = 1;
                reviewindex =2;
            }
        }


        if(activityId!=null && !activityId.isEmpty()) {

            for(int i=0; i<BPMGroupList.size(); i++){
                boolean flag=false;
                for(BPMItem item: BPMGroupList.get(i).list)
                    if(item.activityId.equals(activityId)){
                        tabindex = i;
                        flag=true;
                        break;
                    }
                if(flag)
                    break;
            }
        }

        if(tabindex!=0)
            getSupportActionBar().selectTab(getSupportActionBar().getTabAt(tabindex));
        if(spinindex!=0)
            mSpinner.setSelection(spinindex);


    }

    private void connect(){

        //websocket
        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {

                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();


                    isConnected=true;


                    //获得人员和部门
                    getUserList("");

                    //获得bpmList
                    getBPMList();

                    getFragmentData();
                }

                @Override
                public void onTextMessage(String payload) {

                    try {
                        JSONObject response= new JSONObject(payload);
                        Log.e("onTextMessage", response.toString());

                        String cmd = response.getString("cmd");
                        int error = response.getInt("error");


                        //获取流程列表
                        if(cmd.equals("getBPMList")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            if(dbManager.saveBPMList(datas)) {
                                Log.e("getBPMList", "is new");
                                initBPM();
                            }else
                                Log.e("getBPMList", "is old");

                        }

                        //查询我下属的流程
                        else if(cmd.equals("getCareProcess")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "careprocess");

                            setCareProcessData();
                        }
                        //查询我参与的流程
                        else if(cmd.equals("getPartnerProcess")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "partnerprocess");

                            setPartnerProcessData();
                        }
                        //查询@我
                        else if(cmd.equals("getAtMeProcess")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "atmeprocess");

                            setAtMeData();
                        }
                        //查询我发起的流程
                        else if(cmd.equals("getMyProcess")){
                            if(error!=0)
                                return;

                            //更新数据
                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "myprocess");

                            //存时间戳
                            String timestamp = response.getString("timestamp");
                            dbManager.saveTimeStamp("myprocess", timestamp);

                            setMyProcessData();
                        }
                        //
                        else if(cmd.equals("getMyTask")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveMyTask(datas);

                            setMyTaskData();
                        }
                        //查询等待我审核的流程
                        else if(cmd.equals("getMyDeal")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "mydeal");

                            setDealData();
                        }
                        //获取我处理的流程历史
                        else if(cmd.equals("getDealHistory")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "dealhistory");

                            setDealHistoryData();

                        }
                        //获取我处理的流程历史
                        else if(cmd.equals("getTimeoutDeal")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            dbManager.saveProcess(datas, "timeoutdeal");

                            setTimeoutDealData();
                        }
                        //获取要处理表单
                        else if(cmd.equals("getDealForm")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            currentDealForm = datas.toString();

                            traceProcess(currentProcessId);

                        }

                        //跟踪流程
                        else if(cmd.equals("traceProcess")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            if(datas.length()==0) {
                                Toast.makeText(bpmMainActivity.this, "服务器不存在此流程数据", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(bpmMainActivity.this, bpmTraceProcessActivity.class);
                            intent.putExtra("datas", datas.toString());
                            intent.putExtra("state", TRACE_PROCESS_STATE);
                            TRACE_PROCESS_STATE = bpmTraceProcessActivity.STATE_READ;

                            intent.putExtra("dealForm", currentDealForm);
                            intent.putExtra("dealFormTitle", currentFormTitle);

                            startActivityForResult(intent, REQUEST_CODE.TRACEPROCESS.ordinal());
                        }

                        //跟踪任务
                        else if(cmd.equals("traceTask")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");
                            if(datas.length()==0) {
                                Toast.makeText(bpmMainActivity.this, "服务器不存在此流程数据", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(bpmMainActivity.this, bpmTraceTaskActivity.class);
                            intent.putExtra("datas", datas.toString());
                            intent.putExtra("dealForm", currentDealForm);

                            startActivityForResult(intent, REQUEST_CODE.TRACETASK.ordinal());
                        }

                        //获取人员列表
                        else if(cmd.equals("getUserList")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");

                                structure.setUserList(datas);
                        }

                        //获取人员列表
                        else if(cmd.equals("getDepartmentList")){
                            if(error!=0)
                                return;

                            JSONArray datas = response.getJSONArray("datas");

                            structure.setDepartmentList(datas);
                        }

                        //获取用户部门和职位信息
                        else if(cmd.equals("getUserDepartment")){
                            if(error!=0)
                                return;

                            JSONObject datas = response.getJSONObject("datas");

                            structure.setLoginUserDepartment(datas);
                        }

                        else if(cmd.equals("cancelProcess")){
                            if(error==1){
                                Toast.makeText(getApplicationContext(), "已经审核无法撤回", Toast.LENGTH_SHORT).show();
                            }
                            else if(error==0) {
                                getMyProcess();
                                Toast.makeText(getApplicationContext(), "撤回成功", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if(cmd.equals("cancelTask")){
                            if(error==1){
                                Toast.makeText(getApplicationContext(), "已经接受无法撤回", Toast.LENGTH_SHORT).show();
                            }
                            else if(error==0) {
                                getMyTask();
                                Toast.makeText(getApplicationContext(), "撤回成功", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected=false;
                    Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                    getFragmentData();

                }
            });
        } catch (WebSocketException e) {

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void getFragmentData(){
        ActionBar.Tab tab = getSupportActionBar().getSelectedTab();
        if(tab==null){
            return;
        }

        int tabindex = tab.getPosition();

        switch(spinnerIndex){
            case 0:
                if(BPMGroupList.get(tabindex).type.equals("order"))
                    getMyTask();
                else
                    getMyProcess();
                break;
            case 1: getMyDeal(); getDealHistory(); getTimeoutDeal(); break;
            case 2: getCareProcess(); break;
            case 3: getAtMeProcess(); break;
            case 4: getPartnerProcess(); break;
            case 5:
                setDraftData();
        }

    }

    //-------------------------------fragment设置数据---------------------------------------
    private final int TYPE_SUBMIT=0, TYPE_DRAFT=1, TYPE_TASK=2;

    public int getBPMItemIndex(BPMGroup group, String activityId){
        ArrayList<BPMItem> list = group.list;

        for(int i=0; i<list.size(); i++)
            if(list.get(i).activityId.equals(activityId))
                return i;
        return -1;
    }

    public ArrayList splitData(ArrayList list, int type, int index){
        BPMGroup group = BPMGroupList.get(index);

        switch (type){
            case TYPE_SUBMIT:
                ArrayList<bpmMainFragment_Submit_DataHolder> list_submit = (ArrayList<bpmMainFragment_Submit_DataHolder>)list;
                for(int i=0; i<list_submit.size(); i++) {
                    if(getBPMItemIndex(group, list_submit.get(i).activityId)<0) {
                        list_submit.remove(i);
                        i--;
                    }
                }
                return list_submit;


            case TYPE_DRAFT:
                ArrayList<bpmMainFragment_Draft_DataHolder> list_draft = (ArrayList<bpmMainFragment_Draft_DataHolder>)list;
                for(int i=0; i<list_draft.size(); i++) {
                    if(getBPMItemIndex(group, list_draft.get(i).activityId)<0) {
                        list_draft.remove(i);
                        i--;
                    }
                }
                return list_draft;

            case TYPE_TASK:
                ArrayList<bpmMainFragment_Task_DataHolder> list_task = (ArrayList<bpmMainFragment_Task_DataHolder>)list;
                for(int i=0; i<list_task.size(); i++) {
                    if(getBPMItemIndex(group, list_task.get(i).activityId)<0) {
                        list_task.remove(i);
                        i--;
                    }
                }
                return list_task;
        }


        return list;
    }

    public void setDealData(){
        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("mydeal");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Review fragment = (bpmMainFragment_Review)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+1);
            if(fragment!=null){
                fragment.setUndealData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }
    }

    public void setDealHistoryData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("dealhistory");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Review fragment = (bpmMainFragment_Review)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+1);
            if(fragment!=null){
                fragment.setHistoryData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }
    }

    public void setTimeoutDealData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("timeoutdeal");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Review fragment = (bpmMainFragment_Review)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+1);
            if(fragment!=null){
                fragment.setTimeoutData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }

    }

    public void setMyProcessData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("myprocess");

        for(int i=0; i<BPMGroupList.size(); i++){
            if(BPMGroupList.get(i).type.equals("order"))
                continue;
            bpmMainFragment_Submit fragment = (bpmMainFragment_Submit)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+0);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }
    }

    public void setMyTaskData(){

        ArrayList<bpmMainFragment_Task_DataHolder> datas = dbManager.getMyTask();

        for(int i=0; i<BPMGroupList.size(); i++){
            if(!BPMGroupList.get(i).type.equals("order"))
                continue;
            bpmMainFragment_Task fragment = (bpmMainFragment_Task)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+0);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_TASK, i));
                break;
            }
        }
    }


    public void setCareProcessData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("careprocess");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Submit fragment = (bpmMainFragment_Submit)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+2);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }
    }

    public void setPartnerProcessData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("partnerprocess");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Submit fragment = (bpmMainFragment_Submit)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+4);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }

    }

    public void setAtMeData(){

        ArrayList<bpmMainFragment_Submit_DataHolder> datas = dbManager.getProcess("atmeprocess");

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Submit fragment = (bpmMainFragment_Submit)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+3);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_SUBMIT, i));
                break;
            }
        }
    }

    public void setDraftData(){

        ArrayList<bpmMainFragment_Draft_DataHolder> datas = dbManager.getDraft();

        for(int i=0; i<BPMGroupList.size(); i++){
            bpmMainFragment_Draft fragment = (bpmMainFragment_Draft)getSupportFragmentManager().findFragmentByTag("f"+i+"_"+5);
            if(fragment!=null){
                fragment.setData(splitData(datas, TYPE_DRAFT, i));
                break;
            }
        }
    }


    //-------------------------------JSON---------------------------------------
    public void getMyProcess(){
        setMyProcessData();

        //如果没有连接就返回
        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd","getMyProcess").put("userId", userId).put("type","all").put("timestamp", dbManager.getTimeStamp("myprocess"));
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getMyDeal(){
        setDealData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }

        JSONObject request = new JSONObject();

        try {
            request.put("cmd","getMyDeal").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDealHistory(){
        setDealHistoryData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd","getDealHistory").put("userId", userId).put("type", "all");
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getTimeoutDeal(){

        setDealHistoryData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd","getTimeoutDeal").put("userId", userId).put("type", "all");
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDealForm(String processId){
        currentProcessId = processId;


        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();

        try {
            request.put("cmd","getDealForm").put("userId", userId).put("processId", processId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void submit(JSONArray datas){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;
        JSONObject request = new JSONObject();

        try {

            request.put("userId", userId).put("cmd", "submit").put("processId", currentProcessId)
                    .put("datas", datas);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startActivity(JSONArray datas){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;
        JSONObject request = new JSONObject();

        try {
            if(!currentActivityId.isEmpty()){
                request.put("userId", userId).put("cmd", "startActivity").put("activityId", currentActivityId)
                        .put("datas", datas).put("signId", "");
                currentActivityId="";
            }
            else
                request.put("userId", userId).put("cmd", "startActivity").put("activityId", BPMGroupList.get(currentBPMIndex_group).list.get(currentBPMIndex_item).activityId)
                    .put("datas", datas).put("signId", "");

            Log.e("startActivity", request.toString());
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getMyTask(){
        setMyTaskData();

        //如果没有连接就返回
        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            request.put("cmd", "getMyTask").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startTask(JSONArray datas){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;
        JSONObject request = new JSONObject();

        try {

            if(!currentActivityId.isEmpty()) {
                request.put("userId", userId).put("cmd", "startTask").put("datas", datas).put("activityId", currentActivityId);
                currentActivityId="";
            } else {
                request.put("userId", userId).put("cmd", "startTask").put("datas", datas).put("activityId", BPMGroupList.get(currentBPMIndex_group).list.get(currentBPMIndex_item).activityId);
            }

            Log.e("startTask", request.toString());
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addNewToTask(JSONArray datas){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;
        JSONObject request = new JSONObject();

        try {

            request.put("userId", userId).put("cmd", "addNewToTask").put("taskId", currentTaskId).put("activityId", currentActivityId).put("datas", datas);
            currentActivityId="";

            Log.e("addNewToTask", request.toString());
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void traceTask(String taskId){

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        currentProcessId = taskId;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "traceTask").put("taskId", taskId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void traceProcess(String processId){


        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        currentProcessId = processId;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "traceProcess").put("userId", userId).put("processId", processId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getUserList(String department){

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;


        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getUserList").put("userId", userId).put("department", department);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDepartmentList(){

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;


        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getDepartmentList").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserDepartment(String user){

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getUserDepartment").put("userId", userId).put("user", user);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCareProcess(){
        setCareProcessData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getCareProcess").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAtMeProcess(){
        setAtMeData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getAtMeProcess").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPartnerProcess(){
        setPartnerProcessData();

        if(mConnection==null || !mConnection.isConnected() || !isConnected || user==null) {
            return;
        }
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getPartnerProcess").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBPMList(){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "getBPMList").put("userId", userId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cancelProcess(String processId){
        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "cancelProcess").put("userId", userId).put("processId", processId);
            mConnection.sendTextMessage(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    //-------------------------------HTTP上传文件---------------------------------------
    public void sendFile(ArrayList<String> list){

        if(list==null || list.isEmpty()){
            Log.e("sendFile", "list empty");
            return;
        }

        Log.e("sendFile", "ok");

        for(String filename: list) {
            Log.e("sendFile", filename);
            Thread t = new Thread(new bpmHTTPUploader(this, filename));
            t.start();
        }
    }



}
