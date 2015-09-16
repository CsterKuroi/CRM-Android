package com.example.dt.testapp3;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class VisitContentFragment extends Fragment {


    private String UID;
    private boolean isTaping = false;
    private boolean isPlaying = false;
    private VisitDB visitdb;
    private SQLiteDatabase dbr;
    private ArrayList<String> tapeList;
    private ArrayAdapter<String> tapeAdapter;
    private View fragmentLayout;
    private VisitMainActivity act;

    private int id;
    private int companyId;
    private String company;
    private int[] targetId;
    private String[] target;
    private String submitId;
    private String submitName;
    private String date;
    private double dateInt;
    private String place;
    private String todo;
    private int type;
    private String record;
    private int result;
    private String remark;
    private String tape;
    private int condition;
    private int[] partnerId;
    private String[] partnerName;
    private int pidL;
    private int pidC;
    private boolean isTemp;

    private boolean start;

    private ArrayList<String> newTapeList;

    private TextView Condition;

    public VisitContentFragment() {
        // Required empty public constructor
    }

    //    new VisitData(int _id, String _submitId, String _submitName, int _dateInt,
//                  int _condition, int[] _partnerId, String[] _partner, int _pidL, int _pidC, boolean _isTemp);
    public int getVisitId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getCompany() {
        return company;
    }

    public int[] getTargetId() {
        return targetId;
    }

    public String[] getTarget() {
        return target;
    }

    public String getSubmitId() {
        return submitId;
    }

    public String getSubmitName() {
        return submitName;
    }

    public double getDateInt() {
        return dateInt;
    }

    public int getCondition() {
        return condition;
    }

    public int[] getPartnerId() {
        return partnerId;
    }

    public String[] getPartnerName() {
        return partnerName;
    }

    public int getPidL() {
        return pidL;
    }

    public int getPidC() {
        return pidC;
    }

    public boolean getIsTemp() {
        return isTemp;
    }

    public ArrayList<String> getTapeList() {
        return tapeList;
    }

    public static VisitContentFragment getInstance(Bundle bundle) {
        VisitContentFragment secondFragment = new VisitContentFragment();
        secondFragment.setArguments(bundle);
        return secondFragment;
    }

    public void addTape(String rec) {
        tapeList.add(rec);
        tapeAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentLayout = inflater.inflate(R.layout.fragment_visit_content, container, false);
        id = getArguments().getInt("id");
        UID = getArguments().getString("uid");
        start = getArguments().getBoolean("start", false);
        submitName = getArguments().getString("uname","");
        newTapeList = new ArrayList<>();
        act = (VisitMainActivity) VisitContentFragment.this.getActivity();
        act.setNewTapeList(newTapeList);
        //TODO : get info from SQLite by id

        //数据库查询
        visitdb = new VisitDB(this.getActivity(), "USER" + UID, null, 1);
        dbr = visitdb.getReadableDatabase();
        Cursor detail = dbr.query("visitTable", null, "id = " + id, null, null, null, null);
        detail.moveToFirst();

        //数据提取
        if (detail == null)
            Log.e("Cursor", "Cursor detail is null");
        Log.e("detail.getCount()", String.valueOf(detail.getCount()));
        Log.e("getColumnIndex", String.valueOf(detail.getColumnIndex("submitId")));
        if (id != -1) {
            companyId = detail.getInt(detail.getColumnIndex("companyId"));
            company = detail.getString(detail.getColumnIndex("company"));
            String targetIdStr = detail.getString(detail.getColumnIndex("targetId"));
            targetIdStr = targetIdStr.substring(1, targetIdStr.length() - 1);
            String[] targetIdStrArray = targetIdStr.split(", ");
            if (!targetIdStrArray[0].equals("")) {
                targetId = new int[targetIdStrArray.length];
                for (int i = 0; i < targetIdStrArray.length; i++) {
                    targetId[i] = Integer.parseInt(targetIdStrArray[i]);
                }
            } else {
                targetId = new int[0];
            }
            String targetStr = detail.getString(detail.getColumnIndex("target"));
            targetStr = targetStr.substring(1, targetStr.length() - 1);
            target = targetStr.split(", ");

            submitId = detail.getString(detail.getColumnIndex("submitId"));
            submitName = detail.getString(detail.getColumnIndex("submitName"));
            dateInt = detail.getDouble(detail.getColumnIndex("dateInt"));
            condition = detail.getInt(detail.getColumnIndex("condition"));
            String partnerIdStr = detail.getString(detail.getColumnIndex("partnerId"));
            partnerIdStr = partnerIdStr.substring(1, partnerIdStr.length() - 1);
            String[] partnerIdStrArray = partnerIdStr.split(", ");
            if (!partnerIdStrArray[0].equals("")) {
                partnerId = new int[partnerIdStrArray.length];
                for (int i = 0; i < partnerIdStrArray.length; i++) {
                    partnerId[i] = Integer.parseInt(partnerIdStrArray[i]);
                }
            } else {
                partnerId = new int[0];
            }
            String partnerNameStr = detail.getString(detail.getColumnIndex("partnerName"));
            partnerNameStr = partnerNameStr.substring(1, partnerNameStr.length() - 1);
            partnerName = partnerNameStr.split(", ");
            if (partnerName[0].equals("")) {
                partnerName = new String[0];
            }
            pidL = detail.getInt(detail.getColumnIndex("pidL"));
            pidC = detail.getInt(detail.getColumnIndex("pidC"));
            int isTempInt = detail.getInt(detail.getColumnIndex("isTemp"));
            isTemp = (isTempInt == 1);
        }



        //生成界面！！！
        //拜访日期
        final TextView Date = (TextView) fragmentLayout.findViewById(R.id.Date);
        final TextView DateRight = (TextView) fragmentLayout.findViewById(R.id.Date_right);
        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : get time info.(DatePicker)
                VisitDatePickerFragment datePicker = new VisitDatePickerFragment();
                datePicker.show(VisitContentFragment.this.getFragmentManager(), "datePicker");
            }
        };
        DateRight.setOnClickListener(dateListener);
        if (id == -1) {
            StringBuilder tmp = new StringBuilder();
            tmp.append(String.format("%d-%02d-%02d", Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH) + 1,
                    Calendar.getInstance().get(Calendar.DATE)));
            Date.setText(tmp.toString());
            Date.setOnClickListener(dateListener);
            DateRight.setVisibility(View.VISIBLE);
        } else {
            date = detail.getString(detail.getColumnIndex("date"));
            Date.setText(date);
            if (isTemp && condition == 0) {
                //还没有开始拜访的临时拜访
                //permission to change date
                DateRight.setVisibility(View.VISIBLE);
                Date.setOnClickListener(dateListener);
            } else
                DateRight.setVisibility(View.INVISIBLE);
        }


        //拜访地点
        final TextView Place = (TextView) fragmentLayout.findViewById(R.id.Place);
        if (id != -1) {
            place = detail.getString(detail.getColumnIndex("place"));
            Place.setText(place);
        }

        //拜访客户
        final TextView Company = (TextView) fragmentLayout.findViewById(R.id.Company);
        final TextView CompanyRight = (TextView) fragmentLayout.findViewById(R.id.Company_right);
        View.OnClickListener companyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO ： get Company info, such as BUAA(ListView)
                act.callContact();
            }
        };
        CompanyRight.setOnClickListener(companyListener);
        if (id == -1) {
            Company.setText("无");
            Company.setOnClickListener(companyListener);
            CompanyRight.setVisibility(View.VISIBLE);
        } else {
            Log.e("company from cursor", detail.getString(detail.getColumnIndex("company")));
            Company.setText(detail.getString(detail.getColumnIndex("company")));
            if (isTemp && condition == 0) {
                Company.setOnClickListener(companyListener);
                CompanyRight.setVisibility(View.VISIBLE);
            } else
                CompanyRight.setVisibility(View.INVISIBLE);
        }


        //拜访对象
        final TextView Target = (TextView) fragmentLayout.findViewById(R.id.Target);
        final TextView TargetRight = (TextView) fragmentLayout.findViewById(R.id.Target_right);
        View.OnClickListener targetListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.callContact();
            }
        };
        TargetRight.setOnClickListener(targetListener);
        if (id == -1) {
            Target.setText("无");
            Target.setOnClickListener(targetListener);
            TargetRight.setVisibility(View.VISIBLE);
        } else {
            Target.setText(detail.getString(detail.getColumnIndex("target")));
            if (isTemp && condition == 0) {
                Target.setOnClickListener(targetListener);
                TargetRight.setVisibility(View.VISIBLE);
            } else{
                TargetRight.setOnClickListener(null);
                TargetRight.setText("");
            }
//                TargetRight.setVisibility(View.INVISIBLE);
        }


        //拜访状态
//        final TextView Condition = (TextView) fragmentLayout.findViewById(R.id.Condition);
        Condition = (TextView) fragmentLayout.findViewById(R.id.Condition);
        TextView ConditionRight = (TextView) fragmentLayout.findViewById(R.id.Condition_right);
        View.OnClickListener conditionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.scanCensorTape();
            }
        };
        ConditionRight.setVisibility(View.INVISIBLE);
        if (id != -1) {
//            act.setSubmitButtonVisible(true);
            if (condition >= 5) {
                ConditionRight.setVisibility(View.VISIBLE);
                Condition.setOnClickListener(conditionListener);
                ConditionRight.setOnClickListener(conditionListener);
            }
            if (condition>=4){
                act.setTapeAndPlayVisible(false);
            }
            if (start){
                condition = 1;
            }
            switch (condition) {
                //等待拜访0/正在拜访1/拜访结束2/已提交记录3/已提交报告4/评价：优5良6中7差8,etc
                case 0:
                    Condition.setText("等待拜访");
                    break;
                case 1:
                    Condition.setText("正在拜访");
                    act.setFinishVisible(true);
                    break;
                case 2:
                    Condition.setText("拜访结束");
                    act.setSubmitButtonVisible(true);
                    break;
                case 3:
                    Condition.setText("已提交记录");
                    break;
                case 4:
                    Condition.setText("已提交报告");
                    break;
                case 5:
                    Condition.setText("评价：优秀");
                    break;
                case 6:
                    Condition.setText("评价：良好");
                    break;
                case 7:
                    Condition.setText("评价：合格");
                    break;
                case 8:
                    Condition.setText("评价：较差");
                    break;
            }
        } else {
            Condition.setText("正在添加临时拜访");
        }


        //发起人
        final TextView Submit = (TextView) fragmentLayout.findViewById(R.id.Submit);
        Submit.setText(submitName);

        //同行人
        final TextView Partner = (TextView) fragmentLayout.findViewById(R.id.Partner);
        if (id != -1) {
            Partner.setText(Arrays.toString(partnerName));
//            String partnerId = detail.getString(detail.getColumnIndex("partnerId"));
//            String partnerName = detail.getString(detail.getColumnIndex("partnerName"));
//            if(partnerId != null && partnerName != null){
//                partnerId = partnerId.substring(1, partnerId.length() - 1);
//                partnerName = partnerName.substring(1, partnerName.length() - 1);
//                String[] ids_Str = partnerId.split(", ");
//
//                int[] ids;
//                if(!ids_Str[0].equals("")){
//                    ids = new int[ids_Str.length];
//                    ids[0] = Integer.parseInt(ids_Str[0]);
//                }
//                else {
//                    ids = new int[0];
//                }
//                for(int i = 1; i < ids_Str.length; i++){
//                    ids[i] = Integer.parseInt(ids_Str[i]);
//                }
//
//                String[] names = partnerName.split(", ");
//                if(names[0].equals("")){
//                    names = new String[0];
//                }
//
//                Partner.setText(Arrays.toString(names));
//            }
        }
        else {
            Partner.setText("无");
        }


        //拜访目的
        final EditText Todo = (EditText) fragmentLayout.findViewById(R.id.Todo);
        final TextView TodoRight = (TextView) fragmentLayout.findViewById(R.id.Todo_right);
        if (id != -1) {
            todo = detail.getString(detail.getColumnIndex("todo"));
            Todo.setText(todo);
            if (condition >= 3) {
                Todo.setEnabled(false);
            }
        }


        //拜访类型
        final Spinner Type = (Spinner) fragmentLayout.findViewById(R.id.Type);
        String[] types = new String[]{"了解客户", "方案拟定", "意向确定", "订单合作洽谈", "催款拜访", "其他拜访"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_visit_item_layout, types);
        typeAdapter.setDropDownViewResource(R.layout.spinner_visit_dorpdown_item_layout);
        Type.setAdapter(typeAdapter);
        if (id != -1) {
            type = detail.getInt(detail.getColumnIndex("type"));
            Type.setSelection(type,true);
            if (condition > 0) {
                Type.setEnabled(false);
            }
        }


        //拜访记录
        final EditText Record = (EditText) fragmentLayout.findViewById(R.id.Record);
        final TextView RecordRight = (TextView) fragmentLayout.findViewById(R.id.Record_right);
        if (id != -1) {
            record = detail.getString(detail.getColumnIndex("record"));
            Record.setText(record);
            if (condition >= 3) {
                Record.setEnabled(false);
            }
        }


        //拜访结果
        final Spinner Result = (Spinner) fragmentLayout.findViewById(R.id.Result);
        String[] results = new String[]{"等待拜访", "目标未达成", "基本达成", "全部达成", "超额达成"};
        ArrayAdapter<String> resultAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_visit_item_layout, results);
        resultAdapter.setDropDownViewResource(R.layout.spinner_visit_dorpdown_item_layout);
        Result.setAdapter(resultAdapter);
        if (id != -1) {
            result = detail.getInt(detail.getColumnIndex("result"));
            Result.setSelection(result,true);
            if (condition >= 3) {
                Result.setEnabled(false);
            }

        }

        //备注信息
        final EditText Remark = (EditText) fragmentLayout.findViewById(R.id.Remark);
        final TextView RemarkRight = (TextView) fragmentLayout.findViewById(R.id.Remark_right);
        if (id != -1) {
            remark = detail.getString(detail.getColumnIndex("remark"));
            Remark.setText(remark);
        }
//        Remark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO : get what you want to get.
//            }
//        });

        //录音下拉框
        final Spinner TapeSpinner = (Spinner) fragmentLayout.findViewById(R.id.Tape_spinner);
        tapeList = new ArrayList<String>();
        if (id != -1) {
            tape = detail.getString(detail.getColumnIndex("tape"));
            List<String> tmpList = Arrays.asList(tape.split(", "));
            if (tmpList != null && tmpList.size() > 0 && !tmpList.get(0).equals(""))
                tapeList.addAll(tmpList);
            Log.e("tapeList.size() = ", String.valueOf(tapeList.size()));
            Log.e("tapeList.get(0) = ", "'" + (tapeList.size() > 0 ? tapeList.get(0) : "NOTHING") + "'");
            if (tapeList.size() > 0 && tapeList.get(0) != null && !tapeList.get(0).equals("")) {
                act.setTapeId(Integer.parseInt(tapeList.get(tapeList.size() - 1).substring(19, 23)));
            } else {
                act.setTapeId(0);
            }

        }       //format : tape+uid(%04d)+id(%08d)+number(%04d)+.amr;
        else {
            act.setTapeId(0);
        }
        tapeAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_visit_item_layout, tapeList);
        tapeAdapter.setDropDownViewResource(R.layout.spinner_visit_dorpdown_item_layout);
        TapeSpinner.setAdapter(tapeAdapter);

        //检查本地录音文件齐全性
        String dir = String.format("%s/SaleCircle/", Environment.getExternalStorageDirectory().getPath());
        for(String fileName : tapeList){
            File file = new File(dir+fileName);
            if(!file.exists()){
                //todo download tape files.
                Thread download = new Thread(new VisitHTTPDownloader(1,UID,fileName));
                download.start();
            }
        }

        return fragmentLayout;
    }

    @Override
    public void onStart() {
        if (condition >= 4) {
            ApprovalJSONOperate js = new ApprovalJSONOperate(UID)
                    .setAct((VisitMainActivity) getActivity())
                    .setFragment(VisitContentFragment.this)
                    .getEvaluation(id);
        }
        if (start){
            VisitData update = new VisitData(id,UID,submitId,submitName,
                    date,dateInt,place,companyId,company,targetId,target,
                    todo,type,record,result,remark,tape, condition,
                    partnerId, partnerName, pidL, pidC, isTemp, null, null);
            String updateStr = "update visitTable set " +
                    update.getUpdateSQLString() +
                    " where id = " + id;
            new VisitJSONOperate(update)
                    .setAct(act)
                    .setUID(UID)
                    .setSilence(true)
                    .updateJson(updateStr);
        }
        super.onStart();
    }

    public void setCondition(int _codi){
        switch (_codi) {
            //等待拜访0/正在拜访1/拜访结束2/已提交记录3/已提交报告4/评价：优5良6中7差8,etc
            case 5:
                Condition.setText("评价：优秀");
                break;
            case 6:
                Condition.setText("评价：良好");
                break;
            case 7:
                Condition.setText("评价：合格");
                break;
            case 8:
                Condition.setText("评价：较差");
                break;
        }
        TextView ConditionRight = (TextView) fragmentLayout.findViewById(R.id.Condition_right);
        View.OnClickListener conditionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.scanCensorTape();
            }
        };

        Condition.setOnClickListener(conditionListener);
        ConditionRight.setVisibility(View.VISIBLE);
        ConditionRight.setOnClickListener(conditionListener);

        act.setTapeAndPlayVisible(false);

    }
}