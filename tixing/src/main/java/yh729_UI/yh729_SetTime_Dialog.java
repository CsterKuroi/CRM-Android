package yh729_UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Constant;
import yh729_DB.yh729_DBUtil;
import yh729_DB.yh729_LocalDataBase;

import java.util.Calendar;
import java.util.Date;

import yh729_Fragment.yh729_mFragment;

/**
 * Created by yan on 2015/8/3.
 */
public class yh729_SetTime_Dialog extends Dialog {

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private long datetime;
    private int Repeat;
    private String content;
    private String id;
    public yh729_SetTime_Dialog(Context context, Long datetime, String content, String id) {
        super(context);
        this.datetime=datetime;
        this.content=content;
        this.id=id;
        Repeat= yh729_Constant.Repeat_Once;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yh729_settime_dialog);
        final LinearLayout repeat_line=(LinearLayout)findViewById(R.id.repeat_line);
        final LinearLayout button_line=(LinearLayout)findViewById(R.id.button_line);
        final LinearLayout date_line=(LinearLayout)findViewById(R.id.date_line);
        final LinearLayout time_line=(LinearLayout)findViewById(R.id.time_line);
        final Spinner repeat=(Spinner)findViewById(R.id.repeat);
        final EditText date = (EditText)findViewById(R.id.date);
        final EditText time = (EditText)findViewById(R.id.time);
        final View save = findViewById(R.id.confirm);
        final View cancel = findViewById(R.id.cancel);
        String[] mItems = getContext().getResources().getStringArray(R.array.repeat_type);

        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(getContext(),R.layout.yh729_spinner_item_layout, mItems);
        _Adapter.setDropDownViewResource(R.layout.yh729_spinner_dorpdown_item_layout);
        repeat.setAdapter(_Adapter);
        setTitle("设置提醒");
        initdatetime(time, date);
        repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) time_line.getLayoutParams();
                switch (str) {
                    case "只响一次":
                        Repeat = yh729_Constant.Repeat_Once;
                        date_line.setVisibility(View.VISIBLE);
                        lp.addRule(RelativeLayout.BELOW, R.id.date_line);
                        break;
                    case "周一到周五":
                        Repeat= yh729_Constant.Repeat_WorkDay;
                        date_line.setVisibility(View.INVISIBLE);
                        lp.addRule(RelativeLayout.BELOW, R.id.repeat_line);
                        break;
                    case "每天":
                        lp.addRule(RelativeLayout.BELOW, R.id.repeat_line);
                        Repeat= yh729_Constant.Repeat_EveryDay;
                        date_line.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                //通过自定义控件AlertDialog实现
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = (LinearLayout) inflater.inflate(R.layout.yh729_date_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                //设置日期简略显示 否则详细显示 包括:星期周
                datePicker.setCalendarViewShown(false);
                //初始化当前日期
                calendar.setTimeInMillis(System.currentTimeMillis());
                datePicker.init(mYear,mMonth,mDay, null);
                //设置date布局
                builder.setView(view);
                builder.setTitle("设置日期信息");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //日期格式
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d - %02d -%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        date.setText(sb);
                        //赋值后面闹钟使用
                        mYear = datePicker.getYear();
                        mMonth = datePicker.getMonth();
                        mDay = datePicker.getDayOfMonth();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                //自定义控件
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.yh729_time_dialog, null);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
                //初始化时间
                calendar.setTimeInMillis(System.currentTimeMillis());
                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(mHour);
                timePicker.setCurrentMinute(mMinute);
                //设置time布局
                builder.setView(view);
                builder.setTitle("设置时间信息");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHour = timePicker.getCurrentHour();
                        mMinute = timePicker.getCurrentMinute();
                        //时间小于10的数字 前面补0 如01:12
                        time.setText(new StringBuilder().append(mHour < 10 ? "0" + mHour : mHour).append(":")
                                .append(mMinute < 10 ? "0" + mMinute : mMinute));
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认保存按钮
                final Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth, mDay, mHour, mMinute);
                if((c.getTime().getTime()-Calendar.getInstance().getTime().getTime())>0) {
                    new AlertDialog.Builder(getContext()).setTitle("确认设置提醒吗？")
                            .setMessage("将于" + gethowlong(c) + "后提醒")
                            .setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        SQLiteDatabase db= (new yh729_LocalDataBase(getContext(),null)).getDataBase();
                                        if(id==null) {
                                            yh729_DBUtil.insert(db, "alarm_record", new String[]{"date_time", "repeat", "content","type","way"}, new String[]{c.getTime().getTime() + "", Repeat + "", content,"schedule","1"});
                                        }else{
                                            db.execSQL("update alarm_record set date_time='"+c.getTime().getTime()+"', repeat ='"+Repeat+"' where _id='"+id+"'");
                                            if(yh729_mFragment.now!=null){
                                                yh729_mFragment.now.upDateListview();
                                            }
                                        }
                                       // yh729_AlarmNotificationService.mService.resend();
                                        Log.i("test", "add" + c.getTime());
                                        dismiss();
                                    }catch (Exception e){
                                        Log.i("test",e.toString());
                                    }
                                }
                            })
                            .setNegativeButton("返 回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
                else
                    Toast.makeText(getContext(), "请设置一个正确的时间", Toast.LENGTH_LONG).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void initdatetime(EditText time,EditText date){
        Date a=new Date(datetime);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(a);
        mDay=calendar.get(Calendar.DAY_OF_MONTH);
        mHour=calendar.get(Calendar.HOUR_OF_DAY);
        mMinute=calendar.get(Calendar.MINUTE);
        mYear= calendar.get(Calendar.YEAR);
        mMonth=calendar.get(Calendar.MONTH);
        date.setHint(String.format("%d - %02d -%02d", mYear, mMonth + 1, mDay));
        time.setHint(String.format("%02d :%02d", mHour, mMinute));
    }

    public String gethowlong(Calendar calendar){
        Calendar now=Calendar.getInstance();
        Date d1= calendar.getTime();
        Date d2=now.getTime();
        long diff = d1.getTime() - d2.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
        if(days>0)
            return " "+days+"天"+hours+"小时"+minutes+"分 ";
        else if(hours>0)
            return " "+hours+"小时"+minutes+"分 ";
        else
            return " "+minutes+"分 ";
    }
}
