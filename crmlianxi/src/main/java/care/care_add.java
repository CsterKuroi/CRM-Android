package care;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.ricky.database.CenterDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2015/8/25.
 */


public class care_add extends Activity implements View.OnTouchListener{

    private Button btn01;
    private EditText et01;
    private EditText et02;
    public static final String KEY_USER_ID="KEY_USER_ID";
    public static final String KEY_USER_PASSWORD="KEY_USER_PASSWORD";

    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private List<String> list2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;

    private Spinner spinner_type;
    //private I
    TextView queding,care_time,care_time2;
    EditText care_note;
    ImageView back,care_choose_time,care_choose_time2;
    private String type="生日关爱",time=" ",time2,note="",isokflag="";
    String userid;
    String care_id;

    public crmMyDatabaseHelper dbHelper;
    String fff;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.care_dialog);

        CenterDatabase centerDatabase = new CenterDatabase(this, null);
        userid = centerDatabase.getUID();
        centerDatabase.close();

        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        spinner_type=(Spinner)findViewById(R.id.spinner_type);
        //spinner_remind=(Spinner)findViewById(R.id.spinner_remind);
        care_time=(TextView)findViewById(R.id.care_time);
        care_time2=(TextView)findViewById(R.id.care_time2);
        care_note=(EditText)findViewById(R.id.care_note);
        back=(ImageView)findViewById(R.id.ImageButton_back);
        queding=(TextView)findViewById(R.id.care_submit);

        care_choose_time=(ImageView)findViewById(R.id.care_choose_time);
        care_choose_time2=(ImageView)findViewById(R.id.care_choose_time2);

        queding.setFocusable(true);queding.setClickable(true);

        care_choose_time.setFocusable(true);care_choose_time.setClickable(true);
        care_choose_time.setOnTouchListener(this);
        care_choose_time2.setFocusable(true);care_choose_time2.setClickable(true);
        care_choose_time2.setOnTouchListener(this);
        back.setOnTouchListener(this);  queding.setOnTouchListener(this);

        if(getIntent().getAction()=="edite"){
            queding.setText("修改");
            care_id= getIntent().getExtras().getString("_id");
            Toast.makeText(care_add.this, "修改id：" + care_id, Toast.LENGTH_LONG).show();
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where _id="+care_id,null);
            while (cursor.moveToNext()){
                Toast.makeText(care_add.this, "修改查询出来为：" + cursor.getString(1), Toast.LENGTH_LONG).show();
                note=cursor.getString(5);
                care_note.setText(note);
                time=cursor.getString(3);
                care_time.setText(time);
                time2=cursor.getString(4);
                care_time2.setText(time2);
            }
           // Toast.makeText(care_add.this, "修改查询出来为：" + cursor.getString(1), Toast.LENGTH_LONG).show();
        }
        else{
           /* Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.HOUR);
            int minute=calendar.get(Calendar.MINUTE);
            int second=calendar.get(Calendar.SECOND);
            Time time=new Time();*/
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time22=format.format(date);
            care_time.setText(time22);
            time=time22;
        }


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
                /* 将mySpinner 显示*/
                        arg0.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        arg0.setVisibility(View.VISIBLE);
                    }
                });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        spinner_type.setOnTouchListener(new Spinner.OnTouchListener(){
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
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
        //spinner_remind.setAdapter(adapter2);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
       /* spinner_remind.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                remind=adapter2.getItem(arg2);
                *//* 将mySpinner 显示*//*
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });*/
        /*下拉菜单弹出的内容选项触屏事件处理*/
        spinner_type.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        spinner_type.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

    }
    int ii=0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.care_choose_time||v.getId() == R.id.care_choose_time2) {

                if(v.getId() == R.id.care_choose_time) ii=1;else ii=2;
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
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StringBuffer sb = new StringBuffer();
								/*sb.append(String.format("%d-%02d-%02d",
										datePicker.getYear(),
										datePicker.getMonth() + 1,
										datePicker.getDayOfMonth()));
								sb.append(" ");
								sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*/
                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                sb.append(" ");
                                sb.append(String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                                if(ii==1) {
                                    care_time.setText(sb);
                                    time = sb.toString();
                                }
                                else {
                                    care_time2.setText(sb);
                                    time2 = sb.toString();
                                }


								/*sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*/
                              /*  textview_starttime.setText(sb);
                                textview_starttime.setTextSize(tsize);
                                scheduleYear = String.format("%d",
                                        datePicker.getYear());
                                tempMonth = String.format("%d",
                                        datePicker.getMonth() + 1);
                                tempDay = String.format("%d",
                                        datePicker.getDayOfMonth());*/
                                // etEndTime.requestFocus();
                                dialog.cancel();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }

        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(v.getId()== R.id.ImageButton_back){
                    new AlertDialog.Builder(care_add.this)
                            .setTitle("提醒")
                            .setMessage("确定退出填写吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    care_add.this.finish();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            }

            else if(v.getId()== R.id.care_submit){
            if(type==null||time==null||time2==null) {
                new AlertDialog.Builder(care_add.this)
                        .setTitle("提醒")
                        .setMessage("请完善信息！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
                else{

                if (getIntent().getAction()!=null){
                      fff="确定修改？";
                }
                else{
                      fff="确定提交？";
                }
               /* new AlertDialog.Builder(care_add.this)
                        .setTitle("提醒")
                        .setMessage(fff)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {*/
                note=care_note.getText().toString().trim();
                                if (fff.equals("确定提交？")) {
                                    Intent mIntent = new Intent();
                                    mIntent.putExtra("type", type);
                                    mIntent.putExtra("time", time);
                                    mIntent.putExtra("time2",time2);
                                    mIntent.putExtra("note",note );
                                    // 设置结果，并进行传送
                                    care_add.this.setResult(1, mIntent);
                                } else {
                                   /* dbHelper.getReadableDatabase().rawQuery("update care_table set type="+type+",time="+time+",remind="+remind+",note="+note+" where" +
                                            "uid="+userid,null);*/
                                    dbHelper.getWritableDatabase().execSQL("update care_table set type = ?,time = ?,time2 = ?,note = ? where _id=?",
                                            new String[]{type, time, time2,note,care_id});
                                    Toast.makeText(care_add.this,"_id"+care_id,Toast.LENGTH_SHORT).show();
                                    for (int i=0;i<100;i++){
                                        if(aa.remindid[i][0]==care_id){
                                            aa.cancelRemind(aa.remindid[i][1], care_add.this);
                                            aa.setRemind(time2,type+" "+time+note,"care_remind",0,2, care_add.this);
                                        }
                                    }


                                    Log.e("更新关爱信息","成功");
                                    startActivity(new Intent().setClass(care_add.this, care_main.class));
                                }

                                care_add.this.finish();


                            /*    dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();*/

            }

            }
        }
        return false;
    }
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
        });*/

}