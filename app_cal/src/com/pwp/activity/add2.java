/*
package com.pwp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pwp.application.SysApplication;
import com.pwp.application.application;
import com.pwp.calendar.LunarCalendar;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.myclass.NetworkDetector;
import com.pwp.vo.ScheduleDateTag;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

//import  net.sf.json.JSONObject;
*/
/*
import com.mogujie.tt.R;
import com.mogujie.tt.app.IMApplication;
import com.mogujie.tt.config.UrlConstant;*//*

//import android.app.AlertDialog.Builder;

*/
/**
 * 添加日程主界面
 *
 * @author song
 *//*

public class add2 extends Activity implements OnTouchListener {
    private ImageButton image_back;
    private TextView submit;
    private ImageButton image_type;
    //private ImageButton image_remind;
    private ImageButton image_participant;
    private RadioButton rb;
    int rb_flag = 0;
    private ImageButton etStartTime;
    private ImageButton etEndTime;
    private ImageButton image_note;

    private TextView net;
    private EditText edittext_type;
    //private EditText edittext_remind;
    private TextView textview_participant;

    private TextView textview_starttime;
    private TextView textview_endtime;

    private int priority_flag = 1;
    private TextView text_priority;
    private EditText edittext_note;

    private ScheduleDAO dao;

    // 用于跳转之前存值，回来之后赋值
    private String text_type = "";
    private String remindType = "";
    private int sch_typeID = 0; // 日程类型
    private int remindID = 0; // 提醒类型

    private int sch_typeID2 = 0; // 日程类型
    private int remindID2 = 0; // 提醒类型


    private String text_participant = "";
    private String text_starttime = "";
    private String text_endtime = "";
    private String text_note = "";
    private String text_business = "";

    private String scheduleid = "11";
    String scheduleid2 = "";
    String uid[];//存储uid

    private String[] sch_type = CalendarConstant.sch_type;
    private String[] remind = CalendarConstant.remind;
    //private String[] participant = CalendarConstant.participant;
    private String[] business = CalendarConstant.business;

    private int[] selectedtype = null;
    private int[] selectedparticipant = null;
    private int[] selectedbusiness = null;
    private int[] selectedremind = null;

    private boolean[] flags = null;

    private String from = null;
    int i100;// 判断remind选了几项

    application aid = new application();

    private LunarCalendar lc = null;
    private static int hour = -1;
    private static int minute = -1;
    private static ArrayList<String> scheduleDate = null;
    private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    private String scheduleYear = "";
    private String scheduleMonth = "";
    private String scheduleDay = "";
    private String week = "";
    private String all_ok = "";







	*/
/*IMApplication imapp = (IMApplication) getApplication();*//*

    //String createrID=imapp.getUserid();


    //String createrID="101";
    private String userid = "";
    private String userids = "";


    // 临时日期时间变量，
    private String tempMonth;
    private String tempDay;
    int schTypeID = 0;
    int tsize = 15;
    private static ArrayList<String> pass = null;
    private static ArrayList<String> rewrite = null;

    private final static int type_dialog = 0;
    private final static int edittype_dialog = 1;

    private final static int remind_dialog = 2;
    private final static int editremind_dialog = 3;

    private final static int business_dialog = 4;
    private final static int editbusiness_dialog = 5;

    private final static int participant_dialog = 6;
    private final static int editparticipant_dialog = 7;


    //private EditText startDateTime;
    //private EditText endDateTime;

    private String initStartDateTime = "2013年9月3日 14:44"; // 初始化开始时间
    private String initEndDateTime = "2014年8月23日 17:44"; // 初始化结束时间

    DBOpenHelper dbOpenHelper = new DBOpenHelper(add2.this, "schedules.db");
    application a = new application();
    int width;
    int height;


    String flagg = null;
    //WebSocketConnection mConnection = update_user_schedule.mConnection;
    WebSocketConnection mConnection = new WebSocketConnection();
    private String wsuri = "ws://101.200.189.127:8001/ws";
    public static WebSocketHandler wsHandler;
    String str = null;

    int scheduleID = 0;

    ScheduleVO schedulevo;


    public add2() {
        lc = new LunarCalendar();
        dao = new ScheduleDAO(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bundle bundle = data.getExtras();
                      userids= bundle.getString("re");
                    String re2 = bundle.getString("re2");
                    textview_participant.setText(re2);
                    text_participant=re2;
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SysApplication.getInstance().addActivity(this);

        CenterDatabase centerDatabase = new CenterDatabase(this, null);
        userid = centerDatabase.getUID();
        wsuri = CenterDatabase.URI;
        centerDatabase.close();

//        Toast.makeText(this, "日程管理获取UID:" + userid, Toast.LENGTH_SHORT).show();

        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.add);
        from = null;
        text_priority = (TextView) findViewById(R.id.text_priority);
        net = (TextView) findViewById(R.id.networkflag);
        */
/*if(NetworkDetector.detect(this)) {}
		else {
			net.setVisibility(View.VISIBLE);
			net.setText("没有网络，请链接网络！");

		}*//*

        Intent intent = getIntent();

		*/
/*	if (intent.getStringArrayListExtra("pass") != null) {
				// 从CalendarActivity中传来的值（包含年与日信息）
				pass = intent.getStringArrayListExtra("pass");
				text_type = pass.get(0);
				text_note = pass.get(1);
				text_participant = pass.get(2);
				//sendornot = pass.get(3);
				text_starttime = pass.get(4);
				text_endtime = pass.get(5);
				text_business = pass.get(6);

			}*//*


        if (intent.getStringArrayListExtra("rewrite") != null) {
            // 从CalendarActivity中传来的值（包含年与日信息）
            from = "calendaredit";
            rewrite = intent.getStringArrayListExtra("rewrite");
            scheduleid = rewrite.get(0);

            ScheduleVO vo1 = dao.getScheduleByID(Integer.parseInt(scheduleid));

            sch_typeID = vo1.getScheduleTypeID();
            sch_typeID2 = sch_typeID;
            text_type = CalendarConstant.sch_type[sch_typeID];
            remindID = vo1.getRemindID();
            remindID2 = remindID;
            remindType = CalendarConstant.remind[remindID];
            text_participant = vo1.getparticipant();
            rb_flag = vo1.getissendmail();
            text_starttime = vo1.getScheduleDate();
            text_endtime = vo1.getScheduleDate2();
            priority_flag = vo1.getpriority();
            text_note = vo1.getScheduleContent();

        } else {
            text_starttime = getScheduleDate();
        }

        TextView space = (TextView) findViewById(R.id.textview_space);
        space.setWidth(4 * width / 7);


        image_back = (ImageButton) findViewById(R.id.ImageButton_back);
        submit = (TextView) findViewById(R.id.submit);
        image_type = (ImageButton) findViewById(R.id.ImageButton_type);
        //image_remind = (ImageButton) findViewById(R.id.ImageButton_remind);
        image_participant = (ImageButton) findViewById(R.id.ImageButton_participant);
        //image_business = (ImageButton) findViewById(R.id.ImageButton_business);
        edittext_type = (EditText) findViewById(R.id.EditText_type);
        //edittext_remind = (EditText) findViewById(R.id.EditText_remind);
        edittext_type.setBackgroundColor(Color.WHITE);
        edittext_type.setTextColor(Color.BLACK);
        edittext_note = (EditText) findViewById(R.id.EditText_note);

        net = (TextView) findViewById(R.id.networkflag);
        if (NetworkDetector.detect(this)) {
            net.setVisibility(View.GONE);
            submit.setClickable(true);
        } else {
            net.setVisibility(View.VISIBLE);
            net.setText("没有网络，请链接网络！");
            submit.setClickable(false);
        }

        textview_participant = (TextView) findViewById(R.id.TextView_participant);
        rb = (RadioButton) findViewById(R.id.RadioButton_sendmessage);

        etStartTime = (ImageButton) this.findViewById(R.id.ImageButton_starttime);
        etEndTime = (ImageButton) this.findViewById(R.id.ImageButton_endtime);

        etStartTime.setOnTouchListener(this);
        etEndTime.setOnTouchListener(this);

        textview_starttime = (TextView) findViewById(R.id.TextView_starttime);
        textview_starttime.setOnTouchListener(add2.this);
        textview_endtime = (TextView) findViewById(R.id.TextView_endtime);
        textview_endtime.setOnTouchListener(this);
        //textview_business = (TextView) findViewById(R.id.TextView_business);
        // 日程类型、备注赋值
        String ff = null;
        if (text_type != "") {
            // 在选择日程类型之前已经输入了日程的信息，则在跳转到选择日程类型之前应当将日程信息保存到schText中，当返回时再次可以取得。
            // 一旦设置完成之后就应该将此静态变量设置为空，
            ff += text_type;
        }
        if (text_type != "") {
            ff += remindType;
        }
        edittext_type.setText(text_type);
        //edittext_remind.setText(remindType);
        //text_type = "";
        //remindType="";
        if (text_note != "") {
            edittext_note.setText(text_note);
            //text_note = "";
        }

        textview_participant.setText(text_participant);

        //rb = (RadioButton) findViewById(R.id.RadioButton_sendmessage);
        rb.setClickable(true);
        rb.setFocusable(true);

        if (rb_flag == 0) rb.setChecked(false);
        else rb.setChecked(true);

        rb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (rb_flag == 0) {
                    rb_flag = 1;
                    rb.setChecked(true);
                } else {
                    rb_flag = 0;
                    rb.setChecked(false);
                }

            }
        });


        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);

        //  ratingBar.
        ratingBar.setRating(priority_flag);
        setprioritytext(priority_flag);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            //当拖动条的滑块位置发生改变时触发该方法
            public void onRatingChanged(RatingBar arg0
                    , float rating, boolean fromUser) {
                //动态改变图片的透明度，其中255是星级评分条的最大值，
                //5个星星就代表最大值255
                // image.setAlpha((int)(rating * 255 / 5));
                priority_flag = (int) rating;
                setprioritytext(priority_flag);

            }
        });

        textview_starttime.setText(text_starttime);
        textview_starttime.setTextSize(tsize);

        textview_endtime.setText(text_endtime);
        textview_endtime.setTextSize(tsize);

        //textview_business.setText(text_business);

        //priority

        Date date = new Date();
        if (hour == -1 && minute == -1) {
            hour = date.getHours();
            minute = date.getMinutes();
        }

        // 右上角保存按钮
        image_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
				*/
/*
				 * Intent intent = new Intent(); intent.setClass(add.this,
				 * CalendarActivity.class); startActivity(intent);
				 *//*

            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                all_ok = "";
                if (TextUtils.isEmpty(edittext_type.getText().toString()) || edittext_type.getText().toString().trim() == "")
                    all_ok = "日程类型";
				*/
/*if(TextUtils.isEmpty(edittext_remind.getText().toString())||edittext_remind.getText().toString().trim() == "")
					all_ok+=" 提醒类型";*//*

                if (TextUtils.isEmpty(textview_participant.getText().toString()) || textview_participant.getText().toString().trim() == "")
                    all_ok += " 参与人";
                if (all_ok == "") {
                    if (from == "calendaredit") {
                        new AlertDialog.Builder(add2.this).setTitle("确定修改该日程信息吗？")
                                // MainActivity.this调用本activity的实例
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("确定修改",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // 点击“确认”后的操作
                                                String showDate =
                                                        textview_starttime.getText().toString();*/
/*.split("-")[0]+
												textview_starttime.getText().toString().split("-")[1]+
												textview_starttime.getText().toString().split("-")[2].split("--")[0]+
												hour+minute+week;*//*

                                                //textview_starttime.getText().toString().split(" ")[1].split(":")[0], textview_starttime.getText().toString().split(" ")[1].split(":")[1], week,
                                                //remindID);
                                                int scheduleID = 0;
                                                ScheduleVO schedulevo = new ScheduleVO();
                                                schedulevo.setScheduleID(Integer.parseInt(scheduleid));
                                                schedulevo.setScheduleTypeID(sch_typeID);
                                                schedulevo.setRemindID(remindID);
                                                schedulevo.setparticipant(textview_participant.getText().toString());
                                                //schedulevo.setissendmail(rb_flag==0? 1:0);//单选标记 1选中0未选中
                                                schedulevo.setissendmail(rb.isChecked() ? 1 : 0);
                                                schedulevo.setScheduleDate(textview_starttime.getText().toString());
                                                schedulevo.setScheduleDate2(textview_endtime.getText().toString());
                                                schedulevo.setpriority(priority_flag);
                                                schedulevo.setScheduleContent(edittext_note.getText().toString());
                                                dao.update(schedulevo);
                                                scheduleID = Integer.parseInt(scheduleid);

                                                submit.setClickable(false);
                                                Intent intent = new Intent();
                                                intent.setClass(add2.this, ScheduleInfoView.class);
                                                intent.putExtra("scheduleID", scheduleid);
                                                startActivity(intent);
                                            }
                                        })
                                .setNegativeButton("取消", null).show();
                    } else {
                        new AlertDialog.Builder(add2.this).setTitle("确定创建该日程信息吗？")
                                // MainActivity.this调用本activity的实例
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("确定创建",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // 点击“确认”后的操作

                                                String showDate = handleInfo(
                                                        Integer.parseInt(scheduleYear),
                                                        Integer.parseInt(tempMonth),
                                                        Integer.parseInt(tempDay),
                                                        hour, minute, week,
                                                        remindID);
                                                //createrID=aid.getuserid();


                                                //等待服务器返回scheduleid后再进行村数据库
                                                final int ii = 0;
										*/
/* new Thread(new Runnable() {


													@Override
													public void run() {
														*//*



											*/
/*			  do {
									                            Thread.sleep(10000);
									                            Message message = new Message();
									                            message.what = 1;
									                            mHandler.sendMessage(message);
									                            //Toast.makeText(CalendarActivity.this,"handler启动",Toast.LENGTH_LONG).show();
									                        } while (!Thread.interrupted());

														  *//*


                                                str = "{\"cmd\":\"1-1\",\"type\":\"1\",\"content\":{\"type\":\"" + String.valueOf(sch_typeID) + "\"," +
                                                        "\"joiner\":\"" + userids + "\",\"start\":\"" + textview_starttime.getText().toString() + "\",\"end\":\"" +
                                                        textview_endtime.getText().toString() + "\",\"pri\":\"" + String.valueOf(priority_flag) + "\"," +
                                                        "\"ps\":\"" + edittext_note.getText().toString() + "\",\"creater\":\"" + userid + "\"}}";
                                                Log.d("写日程准备语句：eeeeeeeeeeeeeeeeeeeeeeeeeee ", str);

                                                try {
                                                    mConnection.connect(wsuri, new WebSocketHandler() {
                                                        @Override
                                                        public void onOpen() {
                                                            Log.d("写日程提交语句： ", str);

                                                            mConnection.sendTextMessage(str);
                                                        }

                                                        @Override
                                                        public void onTextMessage(String payload) {
                                                            Log.d("插入服务器返回结果：", payload);
                                                            //  Looper.prepare();

//                                                            Toast.makeText(add.this, payload, Toast.LENGTH_LONG).show();
                                                            //
                                                            // Looper.loop();
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(payload);
                                                                String cmd = jsonObject.getString("cmd");
                                                                Toast.makeText(add2.this, "eeeeeeeeeeeeeee"+cmd, Toast.LENGTH_LONG).show();
                                                                if (cmd.equals("1-1")) {

                                                                    scheduleid2 = jsonObject.getString("tempid");

                                                                    schedulevo = new ScheduleVO();
                                                                    //schedulevo.setScheduleID(Integer.parseInt(scheduleid));
                                                                    //schedulevo.setScheduleID(Integer.parseInt(scheduleid));
                                                                    schedulevo.setcreaterID(userid);
                                                                    schedulevo.setScheduleTypeID(sch_typeID);
                                                                    //schedulevo.setRemindID(remindID);
                                                                    schedulevo.setRemindID(0);
                                                                    schedulevo.setparticipant(textview_participant.getText().toString());
                                                                    schedulevo.setissendmail(rb_flag);//单选标记 1选中0未选中
                                                                    schedulevo.setScheduleDate(textview_starttime.getText().toString());
                                                                    schedulevo.setScheduleDate2(textview_endtime.getText().toString());
                                                                    schedulevo.setpriority(priority_flag);
                                                                    schedulevo.setScheduleContent(edittext_note.getText().toString());
                                                                    schedulevo.setscheduleid2(scheduleid2);
                                                                    schedulevo.setaboutID(userid);//自己创建的日程标记
                                                                    scheduleID = dao.save(schedulevo);

                                                                    String scheduleIDs = String.valueOf(scheduleID);
                                                                    submit.setClickable(false);

                                                                    Intent intent = new Intent();
                                                                    intent.setClass(add2.this, ScheduleInfoView.class);
                                                                    // intent.putExtra("scheduleID",
                                                                    // String.valueOf(scheduleID));
                                                                    intent.putExtra("scheduleID", scheduleIDs);
                                                                    startActivity(intent);


                                                                    setScheduleDateTag(0, scheduleYear, tempMonth, tempDay, scheduleID);
//                                                                    Toast.makeText(add.this, "服务器日程ID" + schedulevo.getscheduleid2(), Toast.LENGTH_LONG).show();
                                                                    //Toast.makeText(add.this, "解析"+scheduleid2, Toast.LENGTH_LONG).show();


                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                                Log.d("写日程提交语句", e.toString());
                                                            }
                                                        }

                                                        @Override
                                                        public void onClose(int code, String reason) {
                                                            //Log.d(TAG, "Connection lost.");
                                                        }
                                                    });
                                                } catch (WebSocketException e) {
                                                    Log.d("写日程提交语句", e.toString());
                                                }
												*/
/*	}
												}).start();*//*


                                                // 将scheduleID保存到数据中(因为在CalendarActivity中点击gridView中的一个Item可能会对应多个标记日程(scheduleID))

                                                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
												*/
/*String username[]=textview_participant.getText().toString().split("、");
												userid=null;
												for (int i=0;i<username.length;i++){
													Cursor cursor = db.rawQuery("select * from user where name="+username[i],null);
													userid +=cursor.getString(1)+",";
													userid=userid.substring(0,username.length-1);
												}*//*

                                                //userid="";
										*/
/*		for (int i=0;i<uid.length;i++){
													if(uid[i]!="0")
														userid=userid+uid[i]+",";
												}*//*

                                                //Toast.makeText(add.this,"uid："+userid,Toast.LENGTH_LONG).show();
												*/
/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
												//String time = df.format(new Date());
												ts = Timestamp.valueOf(showDate);
												ts2 = Timestamp.valueOf(textview_endtime.getText().toString());*//*



                                          */
/* new Thread(new Runnable() {
											   @Override
											   public void run() {
												   try {
													   do {
														   try{
															   if(mConnection.isConnected()){}
															   else {   mConnection.connect(wsuri, mhandler);}
															   mConnection.sendTextMessage(str);
														   }catch (WebSocketException e){
														   }
														   if(flagg=="1"){Thread.interrupted();}
														   else{
															   mConnection.sendTextMessage(str);
														   }
														   Thread.sleep(10000);
													   } while (!Thread.interrupted());
												   }
												   catch (InterruptedException e){
												   }
											   }
										   }).start();*//*

                                            }
                                        }).setNegativeButton("取消", null).show();
                    }
                } else {
                    new AlertDialog.Builder(add2.this)
                            .setTitle("提醒")
                            .setMessage(all_ok + " 未设置")
                            .setPositiveButton("确定", null)
                            .show();
                }

            }
        });

        // 获得日程类型
        image_type.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(type_dialog);
            }
        });
        edittext_type.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(edittype_dialog);
            }
        });
		*/
/*image_remind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(remind_dialog);
			}
		});*//*

		*/
/*edittext_remind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(editremind_dialog);
			}
		});*//*

        image_participant.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(participant_dialog);
            }
        });
        textview_participant.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(editparticipant_dialog);
            }
        });

		*/
/*image_business.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(business_dialog);
			}
		});*//*

		*/
/*textview_business.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(editbusiness_dialog);
			}
		});*//*

    }

    //设置优先级文字
    public void setprioritytext(int f) {
        if (f < 2) text_priority.setText("一般");
        else if (f < 4) text_priority.setText("较重要");
        else if (f < 6) text_priority.setText("重要");
        else if (f < 8) text_priority.setText("很重要");
    }

    */
/**
     * 创建复选框对话框
     *//*


    */
/**
     * 通过选择提醒次数来处理最后的显示结果
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param week
     * @param remindID
     *//*

    public String handleInfo(int year, int month, int day, int hour,
                             int minute, String week, int remindID) {
        remindType = remind[remindID]; // 提醒类型
        String show = "";
        if (0 <= remindID && remindID <= 4) {
            // 提醒一次,隔10分钟,隔30分钟,隔一小时
            show = year + "-" + month + "-" + day + "\t" + hour + ":" + minute
                    + "\t" + week;
            // + "\t\t" ;+ remindType;
        } else if (remindID == 5) {
            // 每周
            show = "每周" + week + "\t" + hour + ":" + minute;
        } else if (remindID == 6) {
            // 每月
            show = "每月" + day + "号" + "\t" + hour + ":" + minute;
        } else if (remindID == 7) {
            // 每年
            show = "每年" + month + "-" + day + "\t" + hour + ":" + minute;
        }
        return show;
    }

    int typef = 0;
    int remindf = 0;


    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case type_dialog:
            case edittype_dialog:

                new AlertDialog.Builder(this)
                        .setTitle("请选择日程类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(sch_type, 0,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        sch_typeID = which;
                                        text_type = sch_type[which];
                                        edittext_type.setText(sch_type[which]);
                                    }
                                }).setPositiveButton("确定", null)
                        .setNegativeButton("取消", null).show();


                break;
            case remind_dialog:
            case editremind_dialog:
                new AlertDialog.Builder(this)
                        .setTitle("请选择提醒类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(remind, 0,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        remindID = which;
                                        remindType = remind[which];
                                        //edittext_remind.setText(remind[which]);
                                    }
                                }).setPositiveButton("确定", null)
                        .setNegativeButton("取消", null).show();

                break;
            case participant_dialog:
            case editparticipant_dialog:

                Intent intentcus = new Intent(add2.this,
                        calGSActivity.class);
                startActivityForResult(intentcus, 100);
                */
/*if (a.getusers() == null) {
                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select * from user ", null);
                    int ii = 0;
                    if (cursor.moveToFirst()) {
                        String names[] = new String[cursor.getCount()];
                        uid = new String[cursor.getCount()];
                        do {
                            String ui = cursor.getString(1);
                            String name = cursor.getString(2);
                            names[ii] = new String(name);
                            uid[ii] = new String(ui);
                            ii++;
                        } while (cursor.moveToNext());
                        a.setusers(names);

                    }
                    //数据库为空的话，添加日程只有自己
                    else {
//				IMApplication imapp = (IMApplication) getApplication();
//				imapp.getUserid();
//				a.setusers(new String[]{imapp.getUserid()});
                        uid = new String[]{"01"};
                        a.setusers(new String[]{"我自己"});
                    }
                    cursor.close();
                }

//				if(a.getusers()==null) {
//					Toast.makeText(add.this, "人员为空，请刷新!", Toast.LENGTH_SHORT).show();;
//				}
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                        this);
                // 设置对话框的图标
                builder.setIcon(android.R.drawable.ic_dialog_info);
                // 设置对话框的标题
                builder.setTitle("选择参与人");

                flags = new boolean[a.getusers().length];
                selectedparticipant = new int[a.getusers().length];
                for (int i = 0; i < a.getusers().length; i++) {
                    flags[i] = false;
                    selectedparticipant[i] = -1;
                }

                builder.setMultiChoiceItems(a.getusers(), flags,
                        new DialogInterface.OnMultiChoiceClickListener() {

                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                flags[which] = isChecked;

                                String result = "";
                                int mm = 0;

                                userids = "";
                                for (int i = 0; i < flags.length; i++) {
                                    if (flags[i]) {
                                        result = result + a.getusers()[i] + "、";
                                        userids = userids + uid[i] + ",";
                                        selectedparticipant[mm++] = i;
                                    } else {

                                    }
                                }
                                if (userids.length() != 0) {
                                    userids = userids.substring(0, result.length() - 1);
                                }
                                if (result.length() != 0) {
                                    text_participant = result.substring(0,
                                            result.length() - 1);
                                } else text_participant = "";

                                textview_participant.setText(text_participant);
                            }
                        });

                // 添加一个确定按钮
                builder.setPositiveButton(" 确 定 ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                // 创建一个复选框对话框
                dialog = builder.create();


                break;*//*

		*/
/*case business_dialog:
		case editbusiness_dialog:
			android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(
					this);
			// 设置对话框的图标
			builder2.setIcon(android.R.drawable.ic_dialog_info);
			// 设置对话框的标题
			builder2.setTitle("选择业务");
			flags = new boolean[business.length];
			selectedbusiness = new int[business.length];
			for (int i = 0; i < business.length; i++) {
				flags[i] = false;
				selectedbusiness[i] = -1;
			}

			builder2.setMultiChoiceItems(business, flags,
					new DialogInterface.OnMultiChoiceClickListener() {

						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							flags[which] = isChecked;
							String result = "";
							int mm = 0;
							for (int i = 0; i < flags.length; i++) {
								if (flags[i]) {
									result = result + business[i] + "、";
									selectedbusiness[mm++] = i;
								}
							}
							text_business = result.substring(0,
									result.length() - 1);
							textview_business.setText(text_business);
						}
					});

			// 添加一个确定按钮
			builder2.setPositiveButton(" 确 定 ",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			// 创建一个复选框对话框
			dialog = builder2.create();

			break;*//*

        }
        return dialog;
    }


    public void setScheduleDateTag(int remindID, String year, String month, String day, int scheduleID) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        String d = year + "-" + month + "-" + day;
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(d));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 封装要标记的日期
        if (remindID >= 0 && remindID <= 4) {
            // "提醒一次","隔10分钟","隔30分钟","隔一小时"（只需标记当前这一天）
            ScheduleDateTag dateTag = new ScheduleDateTag();
            dateTag.setYear(Integer.parseInt(year));
            dateTag.setMonth(Integer.parseInt(month));
            dateTag.setDay(Integer.parseInt(day));
            dateTag.setscheduleID(scheduleID);
            //dateTag.setcreaterID(aid.getuserid());
            dateTagList.add(dateTag);
        }
		*/
/*else if (remindID == 4) {
			// 每天重复(从设置的日程的开始的之后每一天都要标记)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4 * 7; i++) {
				if (i == 0) {
					cal.add(Calendar.DATE, 0);
				} else {
					cal.add(Calendar.DATE, 1);
				}
				handleDate(cal, scheduleID);
			}
		}*//*

        else if (remindID == 5) {
            // 每周重复(从设置日程的这天(星期几)，接下来的每周的这一天多要标记)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4; i++) {
                if (i == 0) {
                    cal.add(Calendar.WEEK_OF_MONTH, 0);
                } else {
                    cal.add(Calendar.WEEK_OF_MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 6) {
            // 每月重复(从设置日程的这天(几月几号)，接下来的每月的这一天多要标记)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12; i++) {
                if (i == 0) {
                    cal.add(Calendar.MONTH, 0);
                } else {
                    cal.add(Calendar.MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 7) {
            // 每年重复(从设置日程的这天(哪一年几月几号)，接下来的每年的这一天多要标记)
            for (int i = 0; i <= 2049 - Integer.parseInt(year); i++) {
                if (i == 0) {
                    cal.add(Calendar.YEAR, 0);
                } else {
                    cal.add(Calendar.YEAR, 1);
                }
                handleDate(cal, scheduleID);
            }
        }
        // 将标记日期存入数据库中
        dao.saveTagDate(dateTagList);
    }

    */
/**
     * 日程标记日期的处理
     *
     * @param cal
     *//*

    public void handleDate(Calendar cal, int scheduleID) {
        ScheduleDateTag dateTag = new ScheduleDateTag();
        dateTag.setYear(cal.get(Calendar.YEAR));
        dateTag.setMonth(cal.get(Calendar.MONTH) + 1);
        dateTag.setDay(cal.get(Calendar.DATE));
        dateTag.setuserid(aid.getuserid());
        dateTag.setscheduleID(scheduleID);
        dateTagList.add(dateTag);
    }

    */
/**
     * 点击item之后，显示的日期信息
     *
     * @return
     *//*

    public String getScheduleDate() {
        Intent intent = getIntent();
        // intent.getp
        //if (intent.getStringArrayListExtra("scheduleDate") != null) {// 判断是不是从日历界面跳转过来的
        // 从CalendarActivity中传来的值（包含年与日信息）
        scheduleDate = intent.getStringArrayListExtra("scheduleDate");
        //}

	*/
/*	int[] schType_remind = intent.getIntArrayExtra("schType_remind"); // 从ScheduleTypeView中传来的值(包含日程类型和提醒次数信息)
		if (schType_remind != null) {// 判断是不是从日历类型页面跳转过来的
			sch_typeID = schType_remind[0];
			remindID = schType_remind[1];
			// edittext_type.setText(sch_type[sch_typeID]+"\t\t\t\t"+remind[remindID]);
			edittext_type
					.setText(sch_type[sch_typeID] + " " + remind[remindID]);
			edittext_type.setTextSize(tsize);
		}*//*

        // 得到年月日和星期
        scheduleYear = scheduleDate.get(0);
        scheduleMonth = scheduleDate.get(1);
        tempMonth = scheduleMonth;
        if (Integer.parseInt(scheduleMonth) < 10) {
            scheduleMonth = "0" + scheduleMonth;
        }
        scheduleDay = scheduleDate.get(2);
        tempDay = scheduleDay;
        if (Integer.parseInt(scheduleDay) < 10) {
            scheduleDay = "0" + scheduleDay;
        }
        // week = scheduleDate.get(3);
        String hour_c = String.valueOf(hour);
        String minute_c = String.valueOf(minute);
        if (hour < 10) {
            hour_c = "0" + hour_c;
        }
        if (minute < 10) {
            minute_c = "0" + minute_c;
        }
        // 得到对应的阴历日期
        String scheduleLunarDay = getLunarDay(Integer.parseInt(scheduleYear),
                Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
        String scheduleLunarMonth = lc.getLunarMonth(); // 得到阴历的月份
        StringBuffer scheduleDateStr = new StringBuffer();
        scheduleDateStr.append(scheduleYear).append("-").append(scheduleMonth)
                .append("-").append(scheduleDay).append(" ").append(hour_c)
                .append(":").append(minute_c);*/
/*.append("\n")
				.append(scheduleLunarMonth).append(scheduleLunarDay);
		// .append(" ").append(week);
*//*
        // dateText.setText(scheduleDateStr);
        return scheduleDateStr.toString();
    }

    */
/**
     * 根据日期的年月日返回阴历日期
     *
     * @param year
     * @param month
     * @param day
     * @return
     *//*

    public String getLunarDay(int year, int month, int day) {
        String lunarDay = lc.getLunarDate(year, month, day, true);
        // {由于在取得阳历对应的阴历日期时，如果阳历日期对应的阴历日期为"初一"，就被设置成了月份(如:四月，五月。。。等)},所以在此就要判断得到的阴历日期是否为月份，如果是月份就设置为"初一"
        if (lunarDay.substring(1, 2).equals("月")) {
            lunarDay = "初一";
        }
        return lunarDay;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_time_dialog, null);
            final DatePicker datePicker = (DatePicker) view
                    .findViewById(R.id.date_picker);
            final TimePicker timePicker = (TimePicker) view
                    .findViewById(R.id.time_picker);
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
                etStartTime.onTouchEvent(event);
                // etStartTime.setInputType(inType);
                // etStartTime.setSelection(etStartTime.getText().length());

                // builder.setTitle("选取起始时间");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

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
                                sb.append(String.format("%02d:%02d",
                                        timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
								*/
/*sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*//*

                                sb.append(":00");
























                                textview_starttime.setText(sb);
                                textview_starttime.setTextSize(tsize);
                                scheduleYear = String.format("%d",
                                        datePicker.getYear());
                                tempMonth = String.format("%d",
                                        datePicker.getMonth() + 1);
                                tempDay = String.format("%d",
                                        datePicker.getDayOfMonth());
                                // etEndTime.requestFocus();
                                dialog.cancel();
                            }
                        });

            } else if (v.getId() == R.id.ImageButton_endtime||v.getId()==R.id.TextView_endtime) {
                // int inType = etEndTime.getInputType();
                // etEndTime.setInputType(InputType.TYPE_NULL);
                etEndTime.onTouchEvent(event);
                // etEndTime.setInputType(inType);
                // etEndTime.setSelection(etEndTime.getText().length());

                // builder.setTitle("选取结束时间");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                StringBuffer sb = new StringBuffer();
                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                sb.append(" ");
                                sb.append(String.format("%02d:%02d",
                                        timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
								*/
/*sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());*//*

                                sb.append(":00");
                                textview_endtime.setText(sb);
                                textview_endtime.setTextSize(tsize);
                                dialog.cancel();
                            }
                        });
            }

            Dialog dialog = builder.create();
            dialog.show();
        }

        return true;
    }

}
*/
