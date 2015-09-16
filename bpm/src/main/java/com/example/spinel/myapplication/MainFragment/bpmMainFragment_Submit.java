package com.example.spinel.myapplication.MainFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.spinel.myapplication.BPMGroup;
import com.example.spinel.myapplication.BPMItem;
import com.example.spinel.myapplication.Form.bpmTraceProcessActivity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.ArrayList;
import java.util.Collections;

public class bpmMainFragment_Submit extends Fragment {
    private String[] searchtypes = {"按类型", "按时间",  "按状态"};
    private String[] search2_type = {"全部"};
    private String[] search2_time = {"全部", "今天", "一周", "一月"};
    private String[] search2_state = {"全部", "正在审核", "未通过", "已通过", "已撤销", "超时作废"};

    private ArrayAdapter<String> aa_type, aa_time, aa_state;
    private Spinner spin1, spin2;

    public ArrayList<bpmMainFragment_Submit_DataHolder> dataList_all;
    ArrayList<bpmMainFragment_Submit_DataHolder> dataList;
    bpmMainFragment_Submit_ListitemAdapter adapter;


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
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //筛选step2
        spin2 = (Spinner)getView().findViewById(R.id.spinner_detail);
        aa_type = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_type);
        aa_time = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_time);
        aa_state = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, search2_state);
        aa_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(aa_type);

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
                bpmMainFragment_Submit_DataHolder item = (bpmMainFragment_Submit_DataHolder)adapter.getItem(arg2);
                String processId = item.processId;
                bpmMainActivity.currentActivityId = item.activityId;

                if(spinnerindex==0 && (item.status.equals("cancelled") || item.status.equals("callback") || item.status.equals("abort") || item.status.equals("backToStart"))) {
                    bpmMainActivity.TRACE_PROCESS_STATE = bpmTraceProcessActivity.STATE_RESTART;

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
                    bpmMainActivity.currentDealForm = startform;
                }


                activity.traceProcess(processId);
                //已读
         //       activity.writeIsRead(item.activityId, item.currentStep, item.submitUser, item.processId);
            }
        });

        //我提交的，可以撤回，撤回超时的可以充填
        if(spinnerindex==0){
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                    new AlertDialog.Builder(activity).setTitle("操作").setMessage("是否撤回"+ bpmMainActivity.BPMGroupList.get(groupindex).name+"?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    activity.cancelProcess(((bpmMainFragment_Submit_DataHolder)adapter.getItem(position)).processId);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                    return true;
                }
            });
        }

        //初始化并设置adapter
        if(dataList==null){
            dataList = new ArrayList<bpmMainFragment_Submit_DataHolder>();
            dataList_all = new ArrayList<bpmMainFragment_Submit_DataHolder>();
        }

        if(adapter==null){
            adapter = new bpmMainFragment_Submit_ListitemAdapter(getActivity(), dataList);
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

    public void setData(ArrayList<bpmMainFragment_Submit_DataHolder> tempList){

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
                        default: ok = search2_state[detail].equals(dataList_all.get(i).statusName); break;
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