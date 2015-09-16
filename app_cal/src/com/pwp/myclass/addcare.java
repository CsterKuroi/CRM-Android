/*
package com.pwp.myclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pwp.activity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

*/
/**
 * Created by dell on 2015/8/25.
 *//*



public class addcare extends Activity implements View.OnTouchListener{

    private Button btn01;
    private EditText et01;
    private EditText et02;
    public static final String KEY_USER_ID="KEY_USER_ID";
    public static final String KEY_USER_PASSWORD="KEY_USER_PASSWORD";

    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private List<String> list2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;

    private Spinner spinner_type,spinner_remind;
    private TextView care_time;
    //private I
    TextView care_note,queding;
    ImageView back;
    private String type,time,remind,note,isokflag="";
    */
/** Called when the activity is first created. *//*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.care_dialog);
        spinner_type=(Spinner)findViewById(R.id.spinner_type);
        spinner_remind=(Spinner)findViewById(R.id.spinner_remind);
        care_time=(TextView)findViewById(R.id.care_time);
        care_note=(TextView)findViewById(R.id.care_note);
        back=(ImageView)findViewById(R.id.ImageButton_back);
        queding=(TextView)findViewById(R.id.submit);

        back.setOnTouchListener(this);  queding.setOnTouchListener(this);

                //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
                list.add("生日关爱");
                list.add("节日关爱");
                list.add("庆祝日关爱");
                list.add("其他关爱");
                //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
                //第三步：为适配器设置下拉列表下拉时的菜单样式。
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //第四步：将适配器添加到下拉列表上
        spinner_type.setAdapter(adapter);
                //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        type=adapter.getItem(arg2);
                */
/* 将mySpinner 显示*//*

                        arg0.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        arg0.setVisibility(View.VISIBLE);
                    }
                });
        */
/*下拉菜单弹出的内容选项触屏事件处理*//*

        spinner_type.setOnTouchListener(new Spinner.OnTouchListener(){
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });
        */
/*下拉菜单弹出的内容选项焦点改变事件处理*//*

        spinner_type.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        list2.add("提前一小时");
        list2.add("提前一天");
        list2.add("提前两天");
        list2.add("提前五天");
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list2);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinner_remind.setAdapter(adapter2);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        spinner_remind.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                remind=adapter2.getItem(arg2);
                */
/* 将mySpinner 显示*//*

                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
        */
/*下拉菜单弹出的内容选项触屏事件处理*//*

        spinner_type.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        */
/*下拉菜单弹出的内容选项焦点改变事件处理*//*

        spinner_type.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_time_dialog, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
            builder.setView(view);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), null);

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);
            if (v.getId() == R.id.ImageButton_starttime||v.getId()==R.id.TextView_starttime) {
                // final int inType = etStartTime.getInputType();
                // etStartTime.setInputType(InputType.TYPE_NULL);
               // etStartTime.onTouchEvent(event);
                // etStartTime.setInputType(inType);
                // etStartTime.setSelection(etStartTime.getText().length());

                // builder.setTitle("选取起始时间");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StringBuffer sb = new StringBuffer();
								*/
/*sb.append(String.format("%d-%02d-%02d",
										datePicker.getYear(),
										datePicker.getMonth() + 1,
										datePicker.getDayOfMonth()));
								sb.append(" ");
								sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*//*

                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                sb.append(" ");
                                sb.append(String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                                care_time.setText(sb);
                                time=sb.toString();
								*/
/*sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*//*

                              */
/*  textview_starttime.setText(sb);
                                textview_starttime.setTextSize(tsize);
                                scheduleYear = String.format("%d",
                                        datePicker.getYear());
                                tempMonth = String.format("%d",
                                        datePicker.getMonth() + 1);
                                tempDay = String.format("%d",
                                        datePicker.getDayOfMonth());*//*

                                // etEndTime.requestFocus();
                                dialog.cancel();
                            }
                        });
            }
            Dialog dialog = builder.create();
            dialog.show();
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(v.getId()==R.id.ImageButton_back){
                    new AlertDialog.Builder(addcare.this)
                            .setTitle("提醒")
                            .setMessage("却请退出填写曲？")
                            .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addcare.this.finish();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            }

            else if(v.getId()==R.id.submit){
            if(type==null||time==null||remind==null) {
                new AlertDialog.Builder(addcare.this)
                        .setTitle("提醒")
                        .setMessage("请完善信息！")
                        .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
                else{
                //返回数据
                Intent mIntent = new Intent();
                mIntent.putExtra("type", type);
                mIntent.putExtra("time", time);
                mIntent.putExtra("remind", remind);
                mIntent.putExtra("note", note);
                // 设置结果，并进行传送
                this.setResult(0, mIntent);
                this.finish();
            }
            }
        }
        return false;
    }
        */
/*btn01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent=new Intent();
                intent.putExtra(KEY_USER_ID, et01.getText().toString());
                intent.putExtra(KEY_USER_PASSWORD, et02.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });*//*


}*/
