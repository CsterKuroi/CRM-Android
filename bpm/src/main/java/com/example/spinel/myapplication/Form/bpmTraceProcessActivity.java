package com.example.spinel.myapplication.Form;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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


public class bpmTraceProcessActivity extends ActionBarActivity {

    private ArrayList<bpmProcessItem> processList;

    private int REQUEST_REVIEW=0, REQUEST_RESTART=1;

    private String formtitle;
    private String dealForm;
    public static int RESULT_REVIEW=666, RESULT_RESTART=667;

    //状态：只读，填写审批，填写审批（不可改只选择下个人），重填开始表单
    public static int STATE_READ=0, STATE_REVIEW=1, STATE_REVIEW_READONLY=2, STATE_RESTART=3;
    private int STATE;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode== REQUEST_REVIEW){
            if(resultCode== RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("data", data.getStringExtra("data"));

                ArrayList<String> list = data.getStringArrayListExtra("voiceList");
                if(list!=null && !list.isEmpty())
                    intent.putStringArrayListExtra("voiceList", list);

                setResult(RESULT_REVIEW, intent);
                finish();

            }

        } else if(requestCode == REQUEST_RESTART){
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("data", data.getStringExtra("data"));

                ArrayList<String> list = data.getStringArrayListExtra("voiceList");
                if(list!=null && !list.isEmpty())
                    intent.putStringArrayListExtra("voiceList", list);

                setResult(RESULT_RESTART, intent);
                finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bpm_activity_trace_process);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        formtitle = getIntent().getStringExtra("dealFormTitle");
        dealForm = getIntent().getStringExtra("dealForm");
        STATE = getIntent().getIntExtra("state", STATE_READ);


        initView();
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

        //重新处理：获取处理表单
        if(STATE == STATE_REVIEW_READONLY){
            bpmProcessItem dealProcess=null;
            for(bpmProcessItem item: processList)
                if(item.opUserId.equals(bpmMainActivity.userId) && !item.summary.equals("审核超时")){
                    dealProcess = item;
                    break;
                }

            if(dealProcess!=null) {
                dealForm = dealProcess.datas;
                formtitle = dealProcess.stepName;
            }

        } else if(STATE == STATE_RESTART){
            dealForm = processList.get(0).datas;
            formtitle = processList.get(0).stepName;
        }

        //listview
        ListView listView = (ListView)findViewById(R.id.listView_trace_process);

        listView.setAdapter(new bpmTraceProcess_ListAdapter(processList, this, getLayoutInflater()));

        //监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!processList.get(position).summary.equals("审核超时")) {

                    Intent intent = new Intent(bpmTraceProcessActivity.this, bpmFormActivity.class);
                    intent.putExtra("datas", processList.get(position).datas);
                    intent.putExtra("edit", false);
                    intent.putExtra("title", processList.get(position).stepName);
                    startActivity(intent);
                }

            }
        });

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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 7, 6, 0);
            int i;
            for(i=0; i<groupList.size(); i++){
                String name = groupList.get(i).groupName;
                RelativeLayout interval = (RelativeLayout)getLayoutInflater().inflate(name.isEmpty() ? R.layout.bpm_form_title_summary_thin : R.layout.bpm_form_title_summary, null);
                ((TextView)interval.findViewById(R.id.form_title_text)).setText(name);
                interval.setLayoutParams(lp);

                linearLayout.addView(interval, i*2);
                linearLayout.addView(groupList.get(i).getView(false, true), i*2+1);
            }


            //生成extra界面
            if(extra_group==null)
                return;

            RelativeLayout interval = (RelativeLayout)getLayoutInflater().inflate(R.layout.bpm_form_title_summary_thin, null);
            ((TextView)interval.findViewById(R.id.form_title_text)).setText("");
            interval.setLayoutParams(lp);
            linearLayout.addView(interval, i*2);
            linearLayout.addView(extra_group.getView(false, true), i*2+1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bpm_menu_review_process, menu);

        if(STATE == STATE_RESTART)
            menu.findItem(R.id.traceprocess_review).setTitle("重新提交");
        else if(STATE == STATE_REVIEW || STATE == STATE_REVIEW_READONLY)
            menu.findItem(R.id.traceprocess_review).setTitle(formtitle);
        else
            menu.findItem(R.id.traceprocess_review).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            bpmTraceProcessActivity.this.finish();
        } else if (id == R.id.traceprocess_review) {
            if (STATE == STATE_RESTART) {

                Intent intent = new Intent(bpmTraceProcessActivity.this, bpmFormActivity.class);
                intent.putExtra("datas", dealForm);
                intent.putExtra("title", formtitle);
                ArrayList<Object> draft = new ArrayList<Object>();
                draft.add(-1);
                draft.add(getIntent().getStringExtra("dealForm"));
                intent.putExtra("draft", draft);

                intent.putExtra("state", bpmFormActivity.STATE_RESTART);
                startActivityForResult(intent, REQUEST_RESTART);
            } else if (STATE == STATE_REVIEW) {

                Intent intent = new Intent(bpmTraceProcessActivity.this, bpmFormActivity.class);
                intent.putExtra("datas", dealForm);
                intent.putExtra("title", formtitle);

                intent.putExtra("state", bpmFormActivity.STATE_REVIEW);
                startActivityForResult(intent, REQUEST_REVIEW);
            } else if (STATE == STATE_REVIEW_READONLY) {

                Intent intent = new Intent(bpmTraceProcessActivity.this, bpmFormActivity.class);
                intent.putExtra("datas", dealForm);
                intent.putExtra("title", formtitle);

                intent.putExtra("state", bpmFormActivity.STATE_REVIEW_READONLY);
                startActivityForResult(intent, REQUEST_REVIEW);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
