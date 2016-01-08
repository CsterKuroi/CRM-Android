package yh729_Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.code.bmj.groupnotifycation.GroupNotificationMainActivity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Constant;
import com.pwp.activity.CalendarActivity;
import com.ricky.database.CenterDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import yh729_DB.yh729_DBUtil;
import yh729_DB.yh729_LocalDataBase;
import yh729_UI.yh729_SetTime_Dialog;
import yh729_activity.yh729_MainActivity;
import yh729_adapter.yh729_alarm_recordAdapter;
import yh729_adapter.yh729_mBaseAdapter;
import yh729_adapter.yh729_scheduleAdapter;
import yh729_bean.my_remindBean;

/**
 * Created by Yanhao on 15-7-29.
 */
public class yh729_mFragment extends Fragment {

    public static yh729_mFragment now = null;
    public static String mtag = "";
    public Handler handler;
    private String[] fromColumns = new String[]{};
    private int[] toLayoutIDs = new int[]{};
    private boolean first;
    private String tag;
    private SQLiteDatabase db;
    private BaseAdapter adapter;
    private Cursor cursor;
    private String sql = "";
    private ListView mListView;
    private ArrayList<my_remindBean> mBean = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.yh729_mfragment, container, false);
        mtag = tag = getTag();
        db = (new yh729_LocalDataBase(getActivity(), null)).getDataBase();
        mListView = (ListView) Layout.findViewById(R.id.listview_message);
        InitListView();
        first = true;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    Bundle bundle = msg.getData();
                    String type = bundle.getString("type");
                    if (type != null) {
                        switch (type) {
                            case "censor":
                                if(getTag().equals(yh729_Constant.MY_REMIND)) {
                                    refreashBean("censor");
                                    upDateListview();
                                }
                                break;
                            case "schedule":
                                if(getTag().equals(yh729_Constant.MY_REMIND)) {
                                    refreashBean("schedule");
                                    upDateListview();
                                }
                                else if(getTag().equals(yh729_Constant.SCHEDULE_REMIND1))
                                    upDateListview();
                                break;
                            case "group_notify":
                                if(getTag().equals(yh729_Constant.MY_REMIND)) {
                                    refreashBean("group_notify");
                                    upDateListview();
                                }
                                break;
                        }
                    }
                }
            }
        };
        return Layout;
    }

    /*
    *根据tag绑定listview的adapter
    * */
    public void InitListView() {
        switch (tag) {
            case yh729_Constant.MY_REMIND:
//                mBean.add(new my_remindBean(R.drawable.ic_photo_workremind, yh729_Constant.WORK_REMIND, "", 0, yh729_Constant.WORK_REMIND, 0));
//                mBean.add(new my_remindBean(R.drawable.ic_photo_instructionremind, yh729_Constant.INSTRUCTION_REMIND, "", 0, yh729_Constant.INSTRUCTION_REMIND, 0));
//                mBean.add(new my_remindBean(R.drawable.ic_photo_diaryremind, yh729_Constant.DAIRY_REMIND, "", 0, yh729_Constant.DAIRY_REMIND, 0));
                mBean.add(new my_remindBean(R.drawable.tixing_shenpi, yh729_Constant.CHECK_REMIND, "", 0, yh729_Constant.CHECK_REMIND, 0));
//                mBean.add(new my_remindBean(R.drawable.ic_photo_focus, yh729_Constant.FOCUS_REMIND, "", 0, yh729_Constant.FOCUS_REMIND, 0));
                mBean.add(new my_remindBean(R.drawable.tixing_richeng, yh729_Constant.SCHEDULE_REMIND, "", 0, yh729_Constant.SCHEDULE_REMIND, 0));
                mBean.add(new my_remindBean(R.drawable.tixing_quntongzhi, yh729_Constant.GROUP_REMIND, "", 0, yh729_Constant.GROUP_REMIND, 0));
                break;
            case yh729_Constant.WORK_REMIND:
                mBean.add(new my_remindBean(R.drawable.needreply, yh729_Constant.NEEDREPLY, "", 0, yh729_Constant.NEEDREPLY, 0));
                mBean.add(new my_remindBean(R.drawable.reply, yh729_Constant.REPLYME, "", 0, yh729_Constant.REPLYME, 0));
                mBean.add(new my_remindBean(R.drawable.atme, yh729_Constant.ATME, "", 0, yh729_Constant.ATME, 0));
                mBean.add(new my_remindBean(R.drawable.atmyteam, yh729_Constant.ATMYSECTOR, "", 0, yh729_Constant.ATMYSECTOR, 0));
                mBean.add(new my_remindBean(R.drawable.send, yh729_Constant.SEND, "", 0, yh729_Constant.SEND, 0));
                mBean.add(new my_remindBean(R.drawable.praise, yh729_Constant.PRAISE, "", 0, yh729_Constant.PRAISE, 0));
                break;
            case yh729_Constant.DAIRY_REMIND1:
                break;
            case yh729_Constant.SCHEDULE_REMIND1:
                sql = "select * from yh_schedule order by CAST (time AS bigint) desc, CAST (priority AS int) desc";
                break;
            case yh729_Constant.SCHEDULE_REMIND2:
                sql = "select * from alarm_record where type = 'schedule' order by CAST (date_time AS bigint) desc";
                break;
            default:
                break;
        }
        try {
            if(!sql.equals("")) {
                cursor = db.rawQuery(sql, null);
                getActivity().startManagingCursor(cursor);
            }
            else if(getTag().equals(yh729_Constant.MY_REMIND))
                refreashBean("all");
            switch (getTag()) {
                case yh729_Constant.MY_REMIND:
                case yh729_Constant.WORK_REMIND:
                    adapter = new yh729_mBaseAdapter(mBean, getActivity());
                    break;
                case yh729_Constant.DAIRY_REMIND1:
                    break;
                case yh729_Constant.SCHEDULE_REMIND1:
                    adapter = new yh729_scheduleAdapter(getActivity(), R.layout.yh729_message_item_layout, cursor, fromColumns, toLayoutIDs, 0);
                    break;
                case yh729_Constant.SCHEDULE_REMIND2:
                    adapter = new yh729_alarm_recordAdapter(getActivity(), R.layout.yh729_message_item_layout, cursor, fromColumns, toLayoutIDs, 0);
                    break;
                default:
                    break;
            }
            mListView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("test", e.toString());
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                try {
                    click(position, parent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    刷新mBean中的数据
  */
    public void refreashBean(String type) {
        String content = "";
        int num = 0;
        long time=0;
        Cursor cur;
        switch (type) {


            case "all":
            case "schedule":
                //日程提醒
                cur = db.rawQuery("select * from yh_schedule where read = 'false' order by CAST (time AS bigint) desc, CAST (priority AS int) desc", null);
                num = cur.getCount();
                if (num > 0) {
                    cur.moveToFirst();
                    time = cur.getLong(cur.getColumnIndex("time"));
                    content = cur.getString(cur.getColumnIndex("scheduleContent"));
                    if(cur.getInt(cur.getColumnIndex("priority"))==5){
                        mBean.get(1).setImportant(true);
                    }
                    else
                        mBean.get(1).setImportant(false);
                    mBean.get(1).setMessageContent(content);
                    mBean.get(1).setMessageTime(time);
                }
                cur.close();
                if(num==0){
                    cur = db.rawQuery("select * from yh_schedule order by CAST (time AS bigint) desc, CAST (priority AS int) desc", null);
                    if(cur.getCount()>0) {
                        cur.moveToFirst();
                        time = cur.getLong(cur.getColumnIndex("time"));
                        content = cur.getString(cur.getColumnIndex("scheduleContent"));
                        mBean.get(1).setMessageContent(content);
                        mBean.get(1).setMessageTime(time);
                    }
                    cur.close();
                }
                mBean.get(1).setNum(num);
                if (!type.equals("all"))
                    break;


            case "censor":
                cur = db.rawQuery("select * from censor where isRead='false' order by CAST (time AS bigint) desc", null);
                num = cur.getCount();
                if (num > 0) {
                    cur.moveToFirst();
                    if(cur.getCount()>0) {
                        time = cur.getLong(cur.getColumnIndex("time"));
                        content = cur.getString(cur.getColumnIndex("content"));
                        mBean.get(0).setMessageContent(content);
                        mBean.get(0).setMessageTime(time);
                    }
                }
                mBean.get(0).setNum(num);
                cur.close();
                if(num==0){
                    cur = db.rawQuery("select * from censor order by CAST (time AS bigint) desc", null);
                    if(cur.getCount()>0) {
                        cur.moveToFirst();
                        time = cur.getLong(cur.getColumnIndex("time"));
                        content = cur.getString(cur.getColumnIndex("content"));
                        mBean.get(0).setMessageContent(content);
                        mBean.get(0).setMessageTime(time);
                    }
                    cur.close();
                }
                if (!type.equals("all"))
                    break;


            case "group_notify":
                cur = db.rawQuery("select * from group_notification where read = 'false' and type='receive' order by CAST (receive_time AS bigint) desc", null);
                num = cur.getCount();
                if (num > 0) {
                    cur.moveToFirst();
                    time = cur.getLong(cur.getColumnIndex("receive_time"));
                    content = cur.getString(cur.getColumnIndex("creatorName"))+"发送了一条群通知："+cur.getString(cur.getColumnIndex("title"))+" "+cur.getString(cur.getColumnIndex("content"));
                    mBean.get(2).setMessageContent(content);
                    mBean.get(2).setMessageTime(time);
                }
                cur.close();
                if(num==0){
                    cur = db.rawQuery("select * from group_notification where type='receive' order by CAST (receive_time AS bigint) desc", null);
                    if(cur.getCount()>0) {
                        cur.moveToFirst();
                        time = cur.getLong(cur.getColumnIndex("receive_time"));
                        content = cur.getString(cur.getColumnIndex("creatorName")) + "发送了一条群通知：" + cur.getString(cur.getColumnIndex("title")) + " " + cur.getString(cur.getColumnIndex("content"));
                        mBean.get(2).setMessageContent(content);
                        mBean.get(2).setMessageTime(time);
                    }
                    cur.close();
                }
                mBean.get(2).setNum(num);
                if (!type.equals("all"))
                    break;
        }

    }

    /*
    * 根据Fragment的tag来处理listview的click事件
    * position parent与ListItemClickListenter中的参数相同
    * */
    private void click(int position, AdapterView<?> parent) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Cursor c;
        TextView id = (TextView) parent.getChildAt(position - parent.getFirstVisiblePosition()).findViewById(R.id.id_msg_item);
        switch (tag) {
            case yh729_Constant.WORK_REMIND:
                bundle.putString("type", mBean.get(position).getMessageType());
                intent.putExtras(bundle);
                intent.setClass(getActivity(), yh729_MainActivity.class);
                startActivity(intent);
                break;
            case yh729_Constant.MY_REMIND:
                switch (mBean.get(position).getMessageType()) {
                    case yh729_Constant.SCHEDULE_REMIND:
                    case yh729_Constant.FOCUS_REMIND:
                    case yh729_Constant.INSTRUCTION_REMIND:
                    case yh729_Constant.WORK_REMIND:
                    case yh729_Constant.DAIRY_REMIND:
                        bundle.putString("type", mBean.get(position).getMessageType());
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), yh729_MainActivity.class);
                        startActivity(intent);
                        break;
                    case yh729_Constant.CHECK_REMIND:
                        intent.setClass(getActivity(), bpmMainActivity.class);
                        CenterDatabase cdb = new CenterDatabase(getActivity(), null);
                        intent.putExtra("userId", cdb.getUID());
                        cdb.close();
                        startActivity(intent);
                        db.execSQL("update censor set isRead='true'");
                        refreashBean("censor");
                        break;
                    case yh729_Constant.GROUP_REMIND:
                        intent.setClass(getActivity(), GroupNotificationMainActivity.class);
                        startActivity(intent);
                        break;
                }
                break;
            case yh729_Constant.SCHEDULE_REMIND1:
                final TextView s_id1 = id;
                c = db.rawQuery("SELECT * FROM yh_schedule WHERE _id='" + id.getText() + "'", null);
                c.moveToFirst();
                if (c.getString(c.getColumnIndex("read")).equals("false")) {
                    yh729_DBUtil.update(db, "yh_schedule", new String[]{"read"}, new String[]{"true"}, new String[]{"_id"}, new String[]{id.getText().toString()});
                    parent.getChildAt(position - parent.getFirstVisiblePosition()).setBackgroundColor(0x000000);
//                    refreashBean("schedule");
                }
                final String scheduleID=c.getString(c.getColumnIndex("scheduleID"));
                c.close();
                new AlertDialog.Builder(getActivity())
                        .setItems(new String[]{"查看详情", "定时提醒"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    startActivity(new Intent(getActivity(), CalendarActivity.class).putExtra("scheduleID", scheduleID));
                                } else if (which == 1) {
                                    Cursor c;
                                    c = db.rawQuery("SELECT * FROM yh_schedule WHERE _id='" + s_id1.getText() + "'", null);
                                    c.moveToFirst();
                                    Date targetTime = yh729_DBUtil.String2Date(c.getString(c.getColumnIndex("scheduleDate")));
                                    Calendar _hahaha_ = Calendar.getInstance();
                                    Calendar _hehehe_ = Calendar.getInstance();
                                    _hehehe_.setTime(targetTime);
                                    c.close();
                                    if (_hahaha_.compareTo(_hehehe_) < 0) {
                                        String[] settime_item = {"提前一天提醒", "提前一小时提醒", "提前半小时提醒", "提前十五分钟提醒", "提前五分钟提醒", "自定义"};
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                                                setTitle("设置提醒时间")
                                                .setItems(settime_item, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Cursor c1 = db.rawQuery("SELECT * FROM yh_schedule WHERE _id='" + s_id1.getText() + "'", null);
                                                        c1.moveToFirst();
                                                        Long t = yh729_DBUtil.String2Date(c1.getString(c1.getColumnIndex("scheduleDate"))).getTime();
                                                        switch (which) {
                                                            case 0:
                                                                t -= 3600 * 1000 * 24;
                                                                break;
                                                            case 1:
                                                                t -= 3600 * 1000;
                                                                break;
                                                            case 2:
                                                                t -= 1800 * 1000;
                                                                break;
                                                            case 3:
                                                                t -= 900 * 1000;
                                                                break;
                                                            case 4:
                                                                t -= 300 * 1000;
                                                                break;
                                                            case 5:
                                                                new yh729_SetTime_Dialog(getActivity(), t, c1.getString(c1.getColumnIndex("scheduleContent")), null).show();
                                                                break;
                                                        }
                                                        if (which != 5) {
                                                            final Long finalT = t;
                                                            final String ccntext1 = c1.getString(c1.getColumnIndex("scheduleContent"));
                                                            if ((t - Calendar.getInstance().getTime().getTime()) > 0) {
                                                                new AlertDialog.Builder(getActivity()).setTitle("确认设置提醒吗？")
                                                                        .setMessage("将于" + gethowlong(t) + "后提醒")
                                                                        .setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                yh729_DBUtil.insert(db, "alarm_record", new String[]{"date_time", "repeat", "content", "type", "way"}, new String[]{finalT + "", yh729_Constant.Repeat_Once + "", ccntext1, "schedule", "1"});
                                                                                Log.i("test", "add" + new Date(finalT).toString());

                                                                            }
                                                                        })
                                                                        .setNegativeButton("返 回", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                            }
                                                                        }).show();
                                                            } else
                                                                Toast.makeText(getActivity(), "请选择一个正确的时间", Toast.LENGTH_SHORT).show();
                                                        }
                                                        c1.close();
                                                    }
                                                }).create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(getActivity(), "该日程已过期", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }).show();
                break;
            case yh729_Constant.SCHEDULE_REMIND2:
                final TextView s_id2 = id;
                c = db.rawQuery("SELECT * FROM alarm_record WHERE _id='" + id.getText() + "'", null);
                c.moveToFirst();
                final Long time_s2 = c.getLong(c.getColumnIndex("date_time"));
                new AlertDialog.Builder(getActivity()).setItems(new String[]{"删除", "更改"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                db.execSQL("delete from alarm_record where _id='" + s_id2.getText() + "'");
                                upDateListview();
                                break;
                            case 1:
                                Cursor ctemp = db.rawQuery("select * from alarm_record where _id='" + s_id2.getText() + "'", null);
                                ctemp.moveToFirst();
                                new yh729_SetTime_Dialog(getActivity(), time_s2, ctemp.getString(ctemp.getColumnIndex("content")), s_id2.getText().toString()).show();
                                ctemp.close();
                                break;
                        }
                    }
                }).show();
                c.close();
                break;
            case yh729_Constant.DAIRY_REMIND1:
                break;
            case yh729_Constant.CHECK_REMIND:
                break;
            default:
                break;
        }
    }

    /*
    * 根据tag重新给listview绑定adapter
    * */
    public void upDateListview() {
        try {
            if(!sql.equals("")) {
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
            }
            switch (getTag()) {
                case yh729_Constant.MY_REMIND:
                case yh729_Constant.WORK_REMIND:
                    adapter.notifyDataSetChanged();
                    break;
                case yh729_Constant.SCHEDULE_REMIND1:
                    mListView.setAdapter(new yh729_scheduleAdapter(getActivity(), R.layout.yh729_message_item_layout, cursor, fromColumns, toLayoutIDs, 0));
                    break;
                case yh729_Constant.SCHEDULE_REMIND2:
                    mListView.setAdapter(new yh729_alarm_recordAdapter(getActivity(), R.layout.yh729_message_item_layout, cursor, fromColumns, toLayoutIDs, 0));
            }
        } catch (Exception e) {
            Log.e("test", e.toString());
        }
    }

    public void onResume() {
        super.onResume();
        now = this;
        try {
            mtag = getTag();
            if (!first) {
                Log.i("test", "mfragment： first=false");
                if(tag.equals(yh729_Constant.MY_REMIND))
                    refreashBean("all");
                upDateListview();
            }
            first = false;
        } catch (Exception e) {
            Log.e("test", e.toString());
        }
    }

    public void onPause() {
        super.onPause();
        now = null;
        mtag = "";
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onStop() {
        super.onStop();
    }

    public String gethowlong(Long t) {
        Calendar now = Calendar.getInstance();
        Date d1 = new Date(t);
        Date d2 = now.getTime();
        long diff = d1.getTime() - d2.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        if (days > 0)
            return " " + days + "天" + hours + "小时" + minutes + "分 ";
        else if (hours > 0)
            return " " + hours + "小时" + minutes + "分 ";
        else
            return " " + minutes + "分 ";
    }
}

