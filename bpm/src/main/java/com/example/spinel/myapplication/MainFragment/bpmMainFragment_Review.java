package com.example.spinel.myapplication.MainFragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.spinel.myapplication.Form.bpmTraceProcessActivity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Spinel on 2015/7/26.
 */
public class bpmMainFragment_Review extends Fragment {

    //spinner
    private String[] searchdeal = {"需要审核", "审核历史", "超时审核"};
    private String[] searchtypes = {"按类型", "按时间"};
    private String[] searchtypes_history = {"按类型", "按时间", "按状态"};
    private String[] search2_type = {"全部"};
    private String[] search2_time = {"全部", "今天", "一周", "一月"};
    private String[] search2_state = {"全部", "正在审核", "未通过", "已通过", "超时作废", "过期"};

    private ArrayAdapter<String> aa_type, aa_time, aa_types, aa_types_history, aa_state;
    private Spinner spin0, spin1, spin2;

    //list
    public ArrayList<bpmMainFragment_Submit_DataHolder> dataList_all;
    ArrayList<bpmMainFragment_Submit_DataHolder> dataList;
    bpmMainFragment_Submit_ListitemAdapter adapter;

    public ArrayList<bpmMainFragment_Submit_DataHolder> dataList_history_all;
    ArrayList<bpmMainFragment_Submit_DataHolder> dataList_history;
    bpmMainFragment_Submit_ListitemAdapter adapter_history;

    public ArrayList<bpmMainFragment_Submit_DataHolder> dataList_timeout_all;
    ArrayList<bpmMainFragment_Submit_DataHolder> dataList_timeout;
    bpmMainFragment_Submit_ListitemAdapter adapter_timeout;

    ListView listView;

    private String tag;
    private int groupindex;
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

        //筛选step0

        spin0 = (Spinner)getView().findViewById(R.id.spinner_deal);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searchdeal);
        spin0.setAdapter(aa);
        aa.setDropDownViewResource(R.layout.my_spinner_dialog_item);


        //筛选step1
        spin1 = (Spinner)getView().findViewById(R.id.spinner_worktype);
        aa_types = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searchtypes);
        aa_types_history = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searchtypes_history);
        aa_types.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        aa_types_history.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        spin1.setAdapter(aa_types);

        //筛选step2
        spin2 = (Spinner)getView().findViewById(R.id.spinner_detail);
        aa_type = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, search2_type);
        aa_time = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, search2_time);
        aa_state = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, search2_state);
        aa_type.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        aa_time.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        aa_state.setDropDownViewResource(R.layout.my_spinner_dialog_item);
        spin2.setAdapter(aa_type);

        //设置spinner宽度
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        lp.width = dm.widthPixels/3;
        getView().findViewById(R.id.rl1).setLayoutParams(lp);
        getView().findViewById(R.id.rl2).setLayoutParams(lp);

        //监听0
        spin0.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(dataList_all==null || dataList_history_all==null || dataList_timeout_all==null)
                    return;
                if(position==0) {
                    listView.setAdapter(adapter);
                    spin1.setAdapter(aa_types);
                }
                else if(position==1){
                    listView.setAdapter(adapter_history);
                    spin1.setAdapter(aa_types_history);
                } else if(position == 2){
                    listView.setAdapter(adapter_timeout);
                    spin1.setAdapter(aa_types);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                selection(spin0.getSelectedItemPosition(), spin1.getSelectedItemPosition(), position);
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
                if(spin0.getSelectedItemPosition()==0) {
                    bpmMainFragment_Submit_DataHolder item = (bpmMainFragment_Submit_DataHolder) adapter.getItem(arg2);
                    String processId = item.processId;
                    bpmMainActivity.currentFormTitle = item.stepName;
                    bpmMainActivity.currentActivityId = item.activityId;
                    bpmMainActivity.currentStepId = item.stepId;
                    bpmMainActivity.currentClickProcessId = item.processId;
                    bpmMainActivity.currentTaskId = "";


                    if(((bpmMainFragment_Submit_DataHolder)adapter.getItem(arg2)).status.equals("callback")) {
                        bpmMainActivity.TRACE_PROCESS_STATE = bpmTraceProcessActivity.STATE_REVIEW_READONLY;
                        activity.traceProcess(processId);
                    }
                    else {
                        bpmMainActivity.TRACE_PROCESS_STATE = bpmTraceProcessActivity.STATE_REVIEW;
                        activity.getDealForm(processId);
                    }

                }
                else if(spin0.getSelectedItemPosition()==1){
                    bpmMainFragment_Submit_DataHolder item = (bpmMainFragment_Submit_DataHolder)adapter_history.getItem(arg2);
                    String processId = item.processId;
                    activity.traceProcess(processId);

                    bpmMainActivity.currentFormTitle = item.stepName;
                    bpmMainActivity.currentActivityId = item.activityId;
                    bpmMainActivity.currentStepId = item.stepId;
                    bpmMainActivity.currentClickProcessId = item.processId;
                    bpmMainActivity.currentTaskId = "";
                }
                else if(spin0.getSelectedItemPosition()==2){
                    bpmMainFragment_Submit_DataHolder item = (bpmMainFragment_Submit_DataHolder)adapter_timeout.getItem(arg2);
                    String processId = item.processId;
                    activity.traceProcess(processId);

                    bpmMainActivity.currentFormTitle = item.stepName;
                    bpmMainActivity.currentActivityId = item.activityId;
                    bpmMainActivity.currentStepId = item.stepId;
                    bpmMainActivity.currentClickProcessId = item.processId;
                    bpmMainActivity.currentTaskId = "";
                }
            }
        });

        //初始化并设置adapter
        if(dataList==null){
            dataList = new ArrayList<>();
            dataList_all = new ArrayList<>();
        }

        if(adapter==null){
            adapter = new bpmMainFragment_Submit_ListitemAdapter(getActivity(), dataList);
        }
        listView.setAdapter(adapter);

        if(dataList_history==null){
            dataList_history = new ArrayList<>();
            dataList_history_all = new ArrayList<>();
        }

        if(adapter_history==null){
            adapter_history = new bpmMainFragment_Submit_ListitemAdapter(getActivity(), dataList_history);
        }

        if(dataList_timeout==null){
            dataList_timeout = new ArrayList<>();
            dataList_timeout_all = new ArrayList<>();
        }

        if(adapter_timeout==null){
            adapter_timeout = new bpmMainFragment_Submit_ListitemAdapter(getActivity(), dataList_timeout);
        }

        activity.getFragmentData();

        if(bpmMainActivity.reviewindex!=0) {
            spin0.setSelection(bpmMainActivity.reviewindex);
            bpmMainActivity.reviewindex=0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bpm_fragment_review, container, false);
    }

    //-----------------------更新data-----------------------

    public void setUndealData(ArrayList<bpmMainFragment_Submit_DataHolder> tempList){
        dataList_all.clear();
        //倒序
        for(int i=tempList.size()-1; i>=0; i--)
            dataList_all.add(tempList.get(i));

        Collections.sort(dataList_all);

        selection(spin0.getSelectedItemPosition(), spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition());
        adapter.notifyDataSetChanged();

    }

    public void setHistoryData(ArrayList<bpmMainFragment_Submit_DataHolder> tempList){
        dataList_history_all.clear();
        //倒序
        for(int i=tempList.size()-1; i>=0; i--)
            dataList_history_all.add(tempList.get(i));

        Collections.sort(dataList_history_all);

        selection(spin0.getSelectedItemPosition(), spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition());
        adapter_history.notifyDataSetChanged();

    }

    public void setTimeoutData(ArrayList<bpmMainFragment_Submit_DataHolder> tempList){
        dataList_timeout_all.clear();
        //倒序
        for(int i=tempList.size()-1; i>=0; i--)
            dataList_timeout_all.add(tempList.get(i));

        Collections.sort(dataList_timeout_all);

        selection(spin0.getSelectedItemPosition(), spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition());
        adapter_timeout.notifyDataSetChanged();

    }

    //-----------------------筛选-----------------------
    private void selection(int deal, int type, int detail) {

        ArrayList<bpmMainFragment_Submit_DataHolder> listall;
        ArrayList<bpmMainFragment_Submit_DataHolder> list;
        bpmMainFragment_Submit_ListitemAdapter adapter;

        switch(deal){
            case 0: listall = dataList_all; list = dataList; adapter = this.adapter; break;
            case 1: listall = dataList_history_all; list = dataList_history; adapter = adapter_history; break;
            default: listall = dataList_timeout_all; list = dataList_timeout; adapter = adapter_timeout; break;
        }

            //---------------历史流程-------------
        if(listall == null)
            return;
        list.clear();
        for(int i=0; i<listall.size(); i++) {
            boolean ok = false;
            switch (type){
                //按类型
                case 0: {
                    switch (detail) {
                        case 0:
                            ok = true;
                            break;
                        default:
                            ok = search2_type[detail].equals(listall.get(i).activityName);
                            break;
                    }
                    break;
                }
                //按时间
                case 1: {
                    switch (detail) {
                        case 0:
                            ok = true;
                            break;
                        case 1:
                            ok = bpmTools.isADay(listall.get(i).startTime);
                            break;
                        case 2:
                            ok = bpmTools.isAWeek(listall.get(i).startTime);
                            break;
                        case 3:
                            ok = bpmTools.isAMonth(listall.get(i).startTime);
                            break;
                    }
                    break;
                }
                //按状态
                case 2:{
                    switch (detail){
                        case 0: ok = true; break;
                        default: ok = search2_state[detail].equals(listall.get(i).statusName); break;
                    }
                    break;
                }
            }
            if(ok)
                list.add(listall.get(i));
        }

        adapter.notifyDataSetChanged();


    }
}
