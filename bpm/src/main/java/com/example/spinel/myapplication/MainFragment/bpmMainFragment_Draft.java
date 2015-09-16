package com.example.spinel.myapplication.MainFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.spinel.myapplication.Form.bpmFormActivity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.ArrayList;

public class bpmMainFragment_Draft extends Fragment {
    private String[] searchtypes = {"按类型", "按时间"};
    private String[] search2_type = {"全部"};
    private String[] search2_time = {"全部", "今天", "一周", "一月"};

    private ArrayAdapter<String> aa_type, aa_time;
    private Spinner spin1, spin2;

    ArrayList<bpmMainFragment_Draft_DataHolder> dataList_all;
    ArrayList<bpmMainFragment_Draft_DataHolder> dataList;
    bpmMainFragment_Draft_ListAdapter adapter;

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


        //筛选step1
        spin1 = (Spinner)getView().findViewById(R.id.spinner_worktype);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searchtypes);
        spin1.setAdapter(aa);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //筛选step2
        spin2 = (Spinner)getView().findViewById(R.id.spinner_detail);
        aa_type = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, search2_type);
        aa_time = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, search2_time);
        aa_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        if(dataList == null) {
            dataList = new ArrayList<>();
            dataList_all = new ArrayList<>();
        }
        if(adapter==null)
            adapter = new bpmMainFragment_Draft_ListAdapter(dataList, activity, activity.getLayoutInflater());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(activity, bpmFormActivity.class);
                intent.putExtra("datas", dataList.get(position).datas);
                intent.putExtra("title", dataList.get(position).title);
                bpmMainActivity.currentActivityId = dataList.get(position).activityId;

                //id
                ArrayList<Object> draft = new ArrayList<Object>();
                draft.add(dataList.get(position).id);
                draft.add(dataList.get(position).blankdatas);
                intent.putExtra("draft", draft);

                intent.putExtra("state", bpmFormActivity.STATE_DRAFT);
                getActivity().startActivityForResult(intent, bpmMainActivity.REQUEST_CODE.STARTACTIVITY.ordinal());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                new AlertDialog.Builder(activity).setTitle("操作").setMessage("是否删除草稿？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bpmMainActivity.dbManager.deleteDraft(dataList.get(position).id);
                                setData(bpmMainActivity.dbManager.getDraft());
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

        activity.getFragmentData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bpm_fragment_draft, container, false);
    }


    //-----------------------更新data-----------------------


    public void setData(ArrayList<bpmMainFragment_Draft_DataHolder> tempList){

        dataList_all.clear();
        //倒序
        for(int i=tempList.size()-1; i>=0; i--)
            dataList_all.add(tempList.get(i));

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
                    else {
/*                        System.out.println(dataList_all.get(i).title);
                        System.out.println("detail:"+detail);
                        System.out.println("length:"+search2_type.length);*/
                        ok = dataList_all.get(i).title.equals(search2_type[detail]);
                    }
                    break;
                }
                //按时间
                case 1:{
                    switch (detail){
                        case 0: ok = true; break;
                        case 1: ok = bpmTools.isADay(dataList_all.get(i).time); break;
                        case 2: ok = bpmTools.isAWeek(dataList_all.get(i).time); break;
                        case 3: ok = bpmTools.isAMonth(dataList_all.get(i).time); break;
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