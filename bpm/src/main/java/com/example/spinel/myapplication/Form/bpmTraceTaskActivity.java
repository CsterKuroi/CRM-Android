package com.example.spinel.myapplication.Form;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class bpmTraceTaskActivity extends ActionBarActivity {

    private ArrayList<bpmProcessItem> processList;

    ArrayList<bpmProcessItem> submitGroup = new ArrayList<>();
    ArrayList<bpmProcessItem> stateGroup = new ArrayList<>();
    ArrayList<bpmProcessItem> resultGroup = new ArrayList<>();

    private String formtitle;
    private String dealForm;

    private int REQUEST_RESTART=0;
    public static int  RESULT_RESTART=667;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST_RESTART){
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("data", data.getStringExtra("data"));
                setResult(RESULT_RESTART, intent);
                finish();
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bpm_activity_trace_task);


        initView();


        //初始化actionbar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.bpm_title, null);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ((TextView)findViewById(R.id.title)).setText("发起的任务");
        ((Button)findViewById(R.id.button_ok)).setText("重新发任务");


        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(bpmTraceTaskActivity.this, bpmFormActivity.class);
                intent.putExtra("datas", dealForm);
                System.out.println("dealForm: " + dealForm);
                intent.putExtra("title", formtitle);
                ArrayList<Object> draft = new ArrayList<Object>();
                draft.add(-1);
                draft.add(getIntent().getStringExtra("dealForm"));
                System.out.println("blankdealForm: " + getIntent().getStringExtra("dealForm"));
                intent.putExtra("draft", draft);

                intent.putExtra("state", bpmFormActivity.STATE_RESTART);
                startActivityForResult(intent, REQUEST_RESTART);
            }
        });


        findViewById(R.id.imageButton_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initView(){

        //JSON 解析
        processList = new ArrayList<>();

        try {
            JSONArray datas = new JSONArray(getIntent().getStringExtra("datas"));

            //进程循环
            for(int i=0; i<datas.length(); i++){
                JSONObject process = datas.getJSONObject(i);

                long time_long = Math.round(process.getDouble("time")*1000);
                Calendar time = bpmTools.Millisecond2Calendar(time_long);

                String stepId = process.getString("stepId");
                String opUserId = process.getString("opUserId");
                String opUserName = bpmMainActivity.structure.getUserName(opUserId);


                processList.add(new bpmProcessItem(
                        process.getString("stepName"), stepId, opUserId, opUserName, time, process.getJSONArray("datas").toString()
                        , process.getString("summary")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //processList排序
        Collections.sort(processList);

        //获得重新开始任务表单
        dealForm = processList.get(0).datas;
        formtitle = processList.get(0).stepName;

        //list分类
        for(bpmProcessItem item: processList){
            if(item.summary.equals("提交"))
                submitGroup.add(item);
            else if(item.summary.equals("同意执行任务") || item.summary.equals("不同意执行任务") || item.summary.equals("无人应答"))
                stateGroup.add(item);
            else
                resultGroup.add(item);
        }

        //listview内容
        //提交
        ListView listView = (ListView)findViewById(R.id.listView_tracetask_submit);
        if(!submitGroup.isEmpty()){
            listView.setAdapter(new bpmTraceProcess_ListAdapter(submitGroup, this, getLayoutInflater()));
        }
        else
            listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String summary = submitGroup.get(position).summary;
                if(!summary.equals("超期未完成") && !summary.equals("无人应答")) {

                    Intent intent = new Intent(bpmTraceTaskActivity.this, bpmFormActivity.class);
                    intent.putExtra("datas", submitGroup.get(position).datas);
                    intent.putExtra("edit", false);
                    intent.putExtra("title", submitGroup.get(position).stepName);
                    startActivity(intent);
                }

            }
        });

        //接受状态
        listView = (ListView)findViewById(R.id.listView_tracetask_state);
        if(!stateGroup.isEmpty()){
            listView.setAdapter(new bpmTraceProcess_ListAdapter(stateGroup, this, getLayoutInflater()));
        }
        else
            listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String summary = stateGroup.get(position).summary;
                if(!summary.equals("超期未完成") && !summary.equals("无人应答")) {

                    Intent intent = new Intent(bpmTraceTaskActivity.this, bpmFormActivity.class);
                    intent.putExtra("datas", stateGroup.get(position).datas);
                    intent.putExtra("edit", false);
                    intent.putExtra("title", stateGroup.get(position).stepName);
                    startActivity(intent);
                }

            }
        });

        //完成状态
        listView = (ListView)findViewById(R.id.listView_tracetask_result);
        if(!resultGroup.isEmpty()){
            listView.setAdapter(new bpmTraceProcess_ListAdapter(resultGroup, this, getLayoutInflater()));
        }
        else
            listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String summary = resultGroup.get(position).summary;
                if(!summary.equals("超期未完成") && !summary.equals("无人应答")) {

                    Intent intent = new Intent(bpmTraceTaskActivity.this, bpmFormActivity.class);
                    intent.putExtra("datas", resultGroup.get(position).datas);
                    intent.putExtra("edit", false);
                    intent.putExtra("title", resultGroup.get(position).stepName);
                    startActivity(intent);
                }

            }
        });

        boolean a=!submitGroup.isEmpty(), b=!stateGroup.isEmpty(), c=!resultGroup.isEmpty();
        View view1=findViewById(R.id.view1), view2=findViewById(R.id.view2);
        if(a&&b&&c){
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
        } else if(a&&b){
            view1.setVisibility(View.VISIBLE);
        } else if(a&&c || b&&c){
            view2.setVisibility(View.VISIBLE);
        }

        //获取第一个表单的view
        try {
            if(processList.isEmpty())
                return;


            //生成groupList
            String datas = processList.get(0).datas;
            ArrayList<bpmForm_Group> groupList = new ArrayList<>();
            JSONArray groups = new JSONArray(datas);

            for(int i=0; i<groups.length(); i++){
                groupList.add(new bpmForm_Group((JSONObject)groups.get(i), i, this));
            }

            //去掉extra和不显示的
            bpmForm_Group extra_group=null;

            for(int i=0; i<groupList.size(); i++ ){

                if(groupList.get(i).summaryType.equals("false")) {
                    groupList.remove(i);
                    i--;
                }
                else if(groupList.get(i).groupName.equals("extra") ){
                    extra_group = groupList.remove(i);
                    i--;

                }

            }


            //groupList排列
            Collections.sort(groupList);


            //添加view
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayout2);
            int i;
            for(i=0; i<groupList.size(); i++){
                String name = groupList.get(i).groupName;
                RelativeLayout interval = (RelativeLayout)getLayoutInflater().inflate(name.isEmpty() ? R.layout.bpm_form_title_summary_thin : R.layout.bpm_form_title_summary, null);
                ((TextView)interval.findViewById(R.id.form_title_text)).setText(name);

                linearLayout.addView(interval, i*2);
                linearLayout.addView(groupList.get(i).getView(false, true), i*2+1);
            }


            //生成extra界面
            if(extra_group==null)
                return;

            RelativeLayout interval = (RelativeLayout)getLayoutInflater().inflate(R.layout.bpm_form_title_summary, null);
            ((TextView)interval.findViewById(R.id.form_title_text)).setText("发送目标");
            linearLayout.addView(interval, i*2);
            linearLayout.addView(extra_group.getView(false, true), i*2+1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
