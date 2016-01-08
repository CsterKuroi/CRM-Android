package com.example.spinel.myapplication.MainFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.spinel.myapplication.BPMGroup;
import com.example.spinel.myapplication.BPMItem;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.ArrayList;
import java.util.Collections;

public class bpmMainFragment_Task extends Fragment {
    private String[] searchtypes = {"按类型", "按时间",  "按状态"};
    private String[] search2_type = {"全部"};
    private String[] search2_time = {"全部", "今天", "一周", "一月"};
    private String[] search2_state = {"全部", "正在审核", "未通过", "已通过", "过期"};

    private ArrayAdapter<String> aa_type, aa_time, aa_state;
    private Spinner spin1, spin2;

    public ArrayList<bpmMainFragment_Task_DataHolder> dataList_all;
    ArrayList<bpmMainFragment_Task_DataHolder> dataList;
    bpmMainFragment_Task_ListitemAdapter adapter;


    ListView listView;

    private String tag;
    private int groupindex;
    private int spinnerindex;
    private bpmMainActivity activity;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        tag = getTag();


        this.activity = (bpmMainActivity)activity;

    }

    @Override
    public void onStart(){
        super.onStart();

        groupindex =  tag.getBytes()[1]-'0';
        search2_type = bpmMainActivity.BPMGroupList.get(groupindex).searchTypes;
        spinnerindex =  tag.getBytes()[3]-'0';

        //筛选step1
        spin1 = (Spinner)getView().findViewById(R.id.spinner_worktype);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searchtypes);
        spin1.setAdapter(aa);
        aa.setDropDownViewResource(R.layout.my_spinner_dialog_item);

        //筛选step2
        spin2 = (Spinner)getView().findViewById(R.id.spinner_detail);
        aa_type = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_type);
        aa_time = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_time);
        aa_state = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_state);
        aa_type.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        aa_time.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        aa_state.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        spin2.setAdapter(aa_type);

        //设置spinner宽度
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        lp.width = dm.widthPixels/2;
        getView().findViewById(R.id.rl1).setLayoutParams(lp);
        getView().findViewById(R.id.rl2).setLayoutParams(lp);

        //监听1
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        spin2.setAdapter(aa_type);
                        break;
                    case 1:
                        spin2.setAdapter(aa_time);
                        break;

                    case 2:
                        spin2.setAdapter(aa_state);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //监听2
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection(spin1.getSelectedItemPosition(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //listview
        listView = (ListView)getView().findViewById(R.id.listView_workshow);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> arg0, View arg1, int arg2,
                                    long arg3) {

                bpmMainFragment_Task_DataHolder item = (bpmMainFragment_Task_DataHolder)adapter.getItem(arg2);
                String taskId = item.taskId;
                activity.traceTask(taskId);

                String startform="";
                for(BPMGroup group: bpmMainActivity.BPMGroupList){
                    for(BPMItem bpmitem: group.list)
                        if(bpmitem.activityId.equals(item.activityId)){
                            startform = bpmitem.datas.toString();
                            break;
                        }
                    if(!startform.isEmpty())
                        break;
                }
                bpmMainActivity.currentActivityId = item.activityId;
                bpmMainActivity.currentTaskId = item.taskId;
                bpmMainActivity.currentDealForm = startform;
                bpmMainActivity.currentClickProcessId = "";
                bpmMainActivity.currentClickTaskId = item.taskId;
            }
        });


        //初始化并设置adapter
        if(dataList==null){
            dataList = new ArrayList<>();
            dataList_all = new ArrayList<>();
        }

        if(adapter==null){
            adapter = new bpmMainFragment_Task_ListitemAdapter(getActivity(), dataList);
        }


        listView.setAdapter(adapter);
        activity.getFragmentData();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bpm_fragment_submit, container, false);
    }


    //-----------------------更新data-----------------------

    public void setData(ArrayList<bpmMainFragment_Task_DataHolder> tempList){

        dataList_all.clear();
        //倒序
        for(int i=tempList.size()-1; i>=0; i--)
            dataList_all.add(tempList.get(i));

        Collections.sort(dataList_all);

        selection(spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition());
        adapter.notifyDataSetChanged();
    }


    //-----------------------筛选-----------------------
    private void selection(int type, int detail) {
        if (dataList_all == null)
            return;

        dataList.clear();

        for(int i=0; i<dataList_all.size(); i++){
            boolean ok =false;
            switch (type){
                //按类型
                case 0:{
                    if(detail==0)
                        ok = true;
                    else
                        ok = dataList_all.get(i).activityName.equals(search2_type[detail]);
                    break;
                }
                //按时间
                case 1:{
                    switch (detail){
                        case 0: ok = true; break;
                        case 1: ok = bpmTools.isADay(dataList_all.get(i).startTime); break;
                        case 2: ok = bpmTools.isAWeek(dataList_all.get(i).startTime); break;
                        case 3: ok = bpmTools.isAMonth(dataList_all.get(i).startTime); break;
                    }
                    break;
                }
                //按状态
                case 2:{
                    switch (detail){
                        case 0: ok = true; break;
                        case 1: ok = dataList_all.get(i).status.equals("正在审核"); break;
                        case 2: ok = dataList_all.get(i).status.equals("未通过"); break;
                        case 3: ok = dataList_all.get(i).status.equals("已通过"); break;
                    }
                    break;
                }
            }
            if(ok)
                dataList.add(dataList_all.get(i));
        }

        adapter.notifyDataSetChanged();

    }


}