package com.example.bmj.statistics_all;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class BMJStatisticsActivity extends Activity {
    private int int_mykehu = 0;
    private int int_mylixnaxiren = 0;
    private int int_mybaifang = 0;
    private int int_myhetong = 0;
    private int int_mymission = 0;
    private int int_undermission= 0;

    private int int_underkehu = 0;
    private int int_underlixnaxiren = 0;
    private int int_underbaifang = 0;
    private int int_underhetong = 0;

    private EditText sellStartTime;
    private EditText sellEndTime;
    private Spinner spinner2;
    private String[] mData = {"我的数据统计", "下属数据统计"};
    private int timeFlag;
    private int mYear;
    private int mMonth;
    private int mDay;
    public  static int FlagSpinner = 0;
    private  WebSocketConnection mConnection=null;
    private  WebSocketConnection mConnection2=null;
    private  WebSocketConnection mConnection3=null;
    private boolean isConnected = false;
    private boolean isConnected2 = false;
    private boolean isConnected3 = false;
    private TextView tx_kehu;
    private TextView tx_baifang;
    private TextView tx_lixiren;
    private TextView tx_mission;
    private TextView tx_hetong;

    private String[] st_mykehu;
    private String[] st_mykehu_creattime;

    private String[] st_mybaifang;
    private String[] st_mybaifang_creattime;

    private String[] st_mylianxiren;
    private String[] st_mylianxiren_craetime;


    private String[] st_underkehu;
    private String[] st_underkehu_creattime;
    private String[] st_underkehu_creater;

    private String[] st_underbaifang;


    private String[] st_underlxr;
    private String[] st_underlxr_creater;
    private String[] st_underlxr_creattime;

    private String[] st_mybfplace;
    private String[] st_mybfcompany;
    private String[] st_mybfdate;
    private String[] st_mybftarget;

    private String[] st_underbfplace;
    private String[] st_underbfdate;
    private String[] st_underbftarget;
    private String[] st_underbfcreater;

    private String[] st_mymission_name;
    private String[] st_mymission_creater;
    private String[] st_mymission_status;

    private String[] st_undermission_name;
    private String[] st_undermission_creater;
    private String[] st_undermission_status;

    private String[] st_myhetong;
    private String[] st_myhtmoney;
    private String[] st_myhtdate;
    private String[] st_myhttarget;

    private String[] st_underhetong;
    private String[] st_underhtmoney;
    private String[] st_underhtdate;
    private String[] st_underhttarget;
    private String[] st_underhtcreater;

    private View ly_kehu;
    private View ly_lxr;
    private View ly_bf;
    private View ly_mission;
    private View ly_hetong;
    private String type3uri = "ws://101.200.189.127:8001/ws";
    private String type3uri2 = "ws://101.200.189.127:1234/ws";


    private String uid;
    private ImageView ib3;
    private TextView queding;
    private String tempjson;
    private String tempjson2;
    private String tempjson3;


    // ip uid
    private void initTextview() {
        tx_kehu = (TextView) findViewById(R.id.txshow2);
        tx_lixiren = (TextView) findViewById(R.id.txshow3);
        tx_baifang = (TextView) findViewById(R.id.txshow4);
        tx_mission = (TextView) findViewById(R.id.txshow1);
        tx_hetong = (TextView) findViewById(R.id.textView);
        ly_kehu = findViewById(R.id.statics_show2);
        ly_bf = findViewById(R.id.statics_show4);
        ly_lxr = findViewById(R.id.statics_show3);
        ly_mission = findViewById(R.id.statics_show);
        ly_hetong = findViewById(R.id.linearLayout);
        ib3=(ImageView) findViewById(R.id.imageButton3);
        queding = (TextView) findViewById(R.id.queding);

        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendreq();
                sendreq2();
                sendreq3();
            }
        });

        ly_kehu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                Bundle b = new Bundle();
                if (FlagSpinner == 0){
                    b.putStringArray("kehu", st_mykehu);
                    b.putStringArray("kehutime",st_mykehu_creattime);
                }
                else {
                    b.putStringArray("kehu", st_underkehu);
                    b.putStringArray("kehutime",st_underkehu_creattime);
                    b.putStringArray("kehucreater",st_underkehu_creater);
                }
                intent.putExtras(b);

                intent.setClass(BMJStatisticsActivity.this, BMJKHStatistics.class);
                startActivity(intent);
            }
        });

        ly_lxr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                Bundle b = new Bundle();
                if (FlagSpinner == 0) {
                    b.putStringArray("lxr", st_mylianxiren);
                    b.putStringArray("lxrtime",st_mylianxiren_craetime);
                }
                else{
                    b.putStringArray("lxr", st_underlxr);
                    b.putStringArray("lxrtime",st_underlxr_creattime);
                    b.putStringArray("lxrcreater",st_underlxr_creater);
                }
                intent.putExtras(b);

                intent.setClass(BMJStatisticsActivity.this, BMJLXRStatics.class);
                startActivity(intent);

            }
        });

        ly_bf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                if (FlagSpinner == 0) {
                    b.putStringArray("baifang", st_mybaifang);
                    b.putStringArray("bfdate", st_mybfdate);
                    b.putStringArray("bfplace", st_mybfplace);
                    b.putStringArray("bftarget", st_mybftarget);
                } else {
                    b.putStringArray("baifang", st_underbaifang);
                    b.putStringArray("bfdate", st_underbfdate);
                    b.putStringArray("bfplace", st_underbfplace);
                    b.putStringArray("bftarget", st_underbftarget);
                    b.putStringArray("bfcreater",st_underbfcreater);

                }
                intent.putExtras(b);
                intent.setClass(BMJStatisticsActivity.this, BMJBFStatics.class);
                startActivity(intent);
            }
        });
        ly_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                if (FlagSpinner == 0) {
                    b.putStringArray("missionname", st_mymission_name);
                    b.putStringArray("missioncreater",st_mymission_creater);
                    b.putStringArray("missionstatus",st_mymission_status);
                }
                else{
                    b.putStringArray("missionname", st_undermission_name);
                    b.putStringArray("missioncreater",st_undermission_creater);
                    b.putStringArray("missionstatus",st_undermission_status);
                }
                intent.putExtras(b);
                intent.setClass(BMJStatisticsActivity.this, BMJMissionStatics.class);
                startActivity(intent);
            }
        });
        ly_hetong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                if (FlagSpinner == 0) {
                    b.putStringArray("hetong", st_myhetong);
                    b.putStringArray("htmoney", st_myhtmoney);
                    b.putStringArray("htdate", st_myhtdate);
                    b.putStringArray("httarget", st_myhttarget);
                }
                else{
                    b.putStringArray("hetong", st_underhetong);
                    b.putStringArray("htmoney", st_underhtmoney);
                    b.putStringArray("htdate", st_underhtdate);
                    b.putStringArray("httarget", st_underhttarget);
                    b.putStringArray("htcreater", st_underhtcreater);
                }
                intent.putExtras(b);
                intent.setClass(BMJStatisticsActivity.this, BMJHTStatics.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 当月第一天
     * @return
     */
    private static String getFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        return day_first;

    }

    /**
     * 当月最后一天
     * @return
     */
    private static String getLastDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String day_last = df.format(gcLast.getTime());
        return day_last;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tab3);
        final ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        getActionBar().setCustomView(LayoutInflater.from(this).inflate(R.layout.bmjtitle_main, null));
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
        CenterDatabase cd = new CenterDatabase(this, null);
        uid = cd.getUID();
        cd.close();
        initTextview();


        Calendar a = Calendar.getInstance();

        mYear = a.get(Calendar.YEAR);
        mMonth = a.get(Calendar.MONTH);
        mDay = a.get(Calendar.DAY_OF_MONTH);
        sellStartTime = (EditText) findViewById(R.id.setstartdatetxt);
        sellEndTime = (EditText) findViewById(R.id.setfinishdatetxt);
        sellStartTime.setText(getFirstDay());
        sellEndTime.setText(getLastDay());

        sellStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timeFlag = 0;
                showDialog(0);
            }
        });
        sellEndTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timeFlag = 1;
                showDialog(1);
            }
        });
        sellStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    timeFlag = 0;
                    hideIM(v);
                    showDialog(0);
                }
            }
        });

        sellEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    timeFlag = 1;
                    hideIM(v);
                    showDialog(1);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.myspinner_sta, mData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2 = (Spinner) findViewById(R.id.bmjspinner_main);
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    FlagSpinner = 0;
                } else if (position == 1)
                    FlagSpinner = 1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner2.setVisibility(View.VISIBLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void sendreq3(){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startime = df.parse(sellStartTime.getText().toString());
            Date finshitime = df.parse(sellEndTime.getText().toString());
            Log.d("Test", String.valueOf(startime.getTime() / 1000));
            Log.d("Test", "日了狗");
            JSONObject requestToP2 = new JSONObject();
            requestToP2.put("cmd", "getCareTaskStatistic");

            requestToP2.put("startTime", String.valueOf(startime.getTime()/1000));
            requestToP2.put("endTime", String.valueOf(finshitime.getTime() / 1000));
            requestToP2.put("userId", uid);
            if (mConnection3==null)
                mConnection3 = new WebSocketConnection();
            if (mConnection3.isConnected()) {
                mConnection3.sendTextMessage(requestToP2.toString());
                Log.d("test", "发送Json字段" + requestToP2.toString());
            } else {
                tempjson3 = requestToP2.toString();
                mConnection3.connect(type3uri2, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段" + tempjson3);
                        mConnection3.sendTextMessage(tempjson3);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        deencode3(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        isConnected = false;
                        Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (WebSocketException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendreq() {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startime = df.parse(sellStartTime.getText().toString());
            Date finshitime = df.parse(sellEndTime.getText().toString());
            Log.d("sss", String.valueOf(startime.getTime() / 1000));
            Log.d("eeee", String.valueOf(finshitime.getTime() / 1000));
            JSONObject request2 = new JSONObject();

            request2.put("start", String.valueOf(startime.getTime() / 1000));
            request2.put("end", String.valueOf(finshitime.getTime() / 1000));
            request2.put("type", "3");
            request2.put("uid", uid);
            request2.put("cmd", "tongji");
            if (mConnection==null)
                mConnection = new WebSocketConnection();
            if (mConnection.isConnected()) {
                mConnection.sendTextMessage(request2.toString());
                Log.d("test", "发送Json字段" + request2.toString());
            } else {
                tempjson = request2.toString();
                mConnection.connect(type3uri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段" + tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        doencode(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        isConnected = false;
                        Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (WebSocketException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendreq2(){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startime = df.parse(sellStartTime.getText().toString());
            Date finshitime = df.parse(sellEndTime.getText().toString());
            JSONObject requestToP = new JSONObject();
            requestToP.put("cmd", "getMyTaskStatistic");
            requestToP.put("startTime", String.valueOf(startime.getTime()/1000));
            requestToP.put("endTime", String.valueOf(finshitime.getTime() / 1000));
            requestToP.put("userId", uid);
            if (mConnection2==null)
                mConnection2 = new WebSocketConnection();
            if (mConnection2.isConnected()) {
                mConnection2.sendTextMessage(requestToP.toString());
                Log.d("test", "发送Json字段" + requestToP.toString());
            } else {
                tempjson2 = requestToP.toString();
                mConnection2.connect(type3uri2, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段" + tempjson2);
                        mConnection2.sendTextMessage(tempjson2);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        deencode2(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        isConnected = false;
                        Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            }catch (WebSocketException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


    }


    public void doencode(String test) {

        String strUTF8 = null;
        try {
            JSONObject lan = null;
            strUTF8 = test;
            JSONObject root = new JSONObject(strUTF8);
            Log.e("re11111",root.toString());
            JSONArray underkehu = root.getJSONArray("underkehu");
            int_underkehu = underkehu.length();
            st_underkehu = new String[int_underkehu];
            st_underkehu_creater = new String[int_underkehu];
            st_underkehu_creattime = new String[int_underkehu];
            for (int i = 0; i < int_underkehu; i++) {
                lan = underkehu.getJSONObject(i);
                st_underkehu[i] = lan.getString("name");
                st_underkehu_creater[i]=lan.getString("creater");
                st_underkehu_creattime[i]=getStringDateShort(lan.getString("createtime"));
            }

            JSONArray underbaifang = root.getJSONArray("underbaifang");
            int_underbaifang = underbaifang.length();
            st_underbaifang = new String[int_underbaifang];
            st_underbfdate = new String[int_underbaifang];
            st_underbfplace = new String[int_underbaifang];
            st_underbftarget = new String[int_underbaifang];
            st_underbfcreater=new String[int_underbaifang];;
            for (int i = 0; i < int_underbaifang; i++) {
                lan = underbaifang.getJSONObject(i);
                //拜访人的名字不知道啊
                st_underbaifang[i] = lan.getString("company");
                st_underbfdate[i] = lan.getString("date");
                st_underbftarget[i] = lan.getString("target");
                st_underbfplace[i] = lan.getString("place");
                st_underbfcreater[i] = lan.getString("creater");
            }


            JSONArray underlianxiren = root.getJSONArray("underlianxiren");
            int_underlixnaxiren = underlianxiren.length();
            st_underlxr = new String[int_underlixnaxiren];
            st_underlxr_creattime = new String[int_underlixnaxiren];
            st_underlxr_creater = new String[int_underlixnaxiren];
            for (int i = 0; i < int_underlixnaxiren; i++) {
                lan = underlianxiren.getJSONObject(i);
                //拜访人的名字不知道啊
                st_underlxr[i] = lan.getString("username");
                st_underlxr_creattime[i]=getStringDateShort(lan.getString("createtime"));
                st_underlxr_creater[i]=lan.getString("creater");
            }

            JSONArray mykehu = root.getJSONArray("mykehu");
            int_mykehu = mykehu.length();
            st_mykehu = new String[int_mykehu];
            st_mykehu_creattime = new String[int_mykehu];
            for (int i = 0; i < int_mykehu; i++) {
                lan = mykehu.getJSONObject(i);
                st_mykehu[i] = lan.getString("name");
                st_mykehu_creattime[i]=getStringDateShort(lan.getString("createtime"));
            }

            JSONArray mybaifang = root.getJSONArray("mybaifang");
            int_mybaifang = mybaifang.length();
            st_mybaifang = new String[int_mybaifang];
            st_mybfdate = new String[int_mybaifang];
            st_mybfplace = new String[int_mybaifang];
            st_mybftarget = new String[int_mybaifang];
            for (int i = 0; i < int_mybaifang; i++) {
                lan = mybaifang.getJSONObject(i);
                st_mybaifang[i] = lan.getString("company");
                st_mybfplace[i] = lan.getString("place");
                st_mybftarget[i] = lan.getString("target");
                st_mybfdate[i] = lan.getString("date");
            }

            JSONArray mylianxiren = root.getJSONArray("mylianxiren");
            int_mylixnaxiren = mylianxiren.length();
            st_mylianxiren = new String[int_mylixnaxiren];
            st_mylianxiren_craetime = new String[int_mylixnaxiren];
            for (int i = 0; i < int_mylixnaxiren; i++) {
                lan = mylianxiren.getJSONObject(i);
                st_mylianxiren[i] = lan.getString("name");
                st_mylianxiren_craetime[i]=getStringDateShort(lan.getString("createtime"));
            }

            JSONArray myhetong = root.getJSONArray("myhetong");
            int_myhetong = myhetong.length();
            st_myhetong = new String[int_myhetong];
            st_myhtmoney = new String[int_myhetong];
            st_myhtdate = new String[int_myhetong];
            st_myhttarget = new String[int_myhetong];
            for (int i = 0; i < int_myhetong; i++) {
                lan = myhetong.getJSONObject(i);
                st_myhetong[i] = lan.getString("name");
                st_myhtmoney[i] = lan.getString("money");
                st_myhtdate[i] = lan.getString("date");
                st_myhttarget[i] = lan.getString("customer");
            }

            JSONArray underhetong = root.getJSONArray("underhetong");
            int_underhetong = underhetong.length();
            st_underhetong = new String[int_underhetong];
            st_underhtmoney = new String[int_underhetong];
            st_underhtdate = new String[int_underhetong];
            st_underhttarget = new String[int_underhetong];
            st_underhtcreater = new String[int_underhetong];
            CenterDatabase cd = new CenterDatabase(this, null);
            for (int i = 0; i < int_underhetong; i++) {
                lan = underhetong.getJSONObject(i);
                st_underhetong[i] = lan.getString("name");
                st_underhtmoney[i] = lan.getString("money");
                st_underhtdate[i] = lan.getString("date");
                st_underhttarget[i] = lan.getString("customer");
                st_underhtcreater[i] = cd.getNameByUID(lan.getString("uid"));
            }
            cd.close();

            if (FlagSpinner == 0) {
                tx_kehu.setText(String.valueOf(int_mykehu) + "人");
                tx_baifang.setText(String.valueOf(int_mybaifang) + "人");
                tx_lixiren.setText(String.valueOf(int_mylixnaxiren) + "人");
                tx_hetong.setText(String.valueOf(int_myhetong) + "个");
            } else {
                tx_kehu.setText(String.valueOf(int_underkehu) + "人");
                tx_baifang.setText(String.valueOf(int_underbaifang) + "人");
                tx_lixiren.setText(String.valueOf(int_underlixnaxiren) + "人");
                tx_hetong.setText(String.valueOf(int_underhetong) + "个");
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
    public void deencode2(String test)
    {
        int j = 0;
        String strUTF8 = null;
        try {
            JSONObject lan = null;
            strUTF8 = test;
            JSONObject root = new JSONObject(strUTF8);
            Log.e("re222",root.toString());
            JSONArray mymission = root.getJSONArray("datas");
            int_mymission = mymission.length();
            st_mymission_name = new String[int_mymission];
            st_mymission_status=new String[int_mymission];
            st_mymission_creater = new String[int_mymission];
            for(int i=0;i<int_mymission;i++)
            {
                lan = mymission.getJSONObject(i);
                st_mymission_name[i] = lan.getString("name");
                st_mymission_status[i]=lan.getString("status");
                if(lan.getString("status").equals("已完成")) j++;
                st_mymission_creater[i] = lan.getString("opUserId");

            }
//            st_underkehu = new String[int_underkehu];
//            st_underkehu_creater = new String[int_underkehu];
//            st_underkehu_creattime = new String[int_underkehu];
//            for (int i = 0; i < int_underkehu; i++) {
//                lan = underkehu.getJSONObject(i);
//                st_underkehu[i] = lan.getString("name");
//                st_underkehu_creater[i]=lan.getString("creater");
//                st_underkehu_creattime[i]=getStringDateShort(lan.getString("createtime"));

            if (FlagSpinner == 0) {
                tx_mission.setText(String.valueOf(j)+"个");
            } else {

            }
        }  catch (JSONException e) {
            e.printStackTrace();

        }

    }
    public void deencode3(String test){
        int j = 0;
        String strUTF8 = null;
        try {
            JSONObject lan = null;
            strUTF8 = test;
            JSONObject root = new JSONObject(strUTF8);
            Log.e("re3333",root.toString());

            JSONArray undermission = root.getJSONArray("datas");
            int_undermission = undermission.length();
            st_undermission_name = new String[int_undermission];
            st_undermission_status=new String[int_undermission];
            st_undermission_creater = new String[int_undermission];

            for(int i=0;i<int_undermission;i++)
            {
                lan = undermission.getJSONObject(i);
                st_undermission_name[i] = lan.getString("name");
                st_undermission_status[i]=lan.getString("status");
                if(lan.getString("status").equals("已完成")) j++;
                st_undermission_creater[i] = lan.getString("opUserId");

            }
//            st_underkehu = new String[int_underkehu];
//            st_underkehu_creater = new String[int_underkehu];
//            st_underkehu_creattime = new String[int_underkehu];
//            for (int i = 0; i < int_underkehu; i++) {
//                lan = underkehu.getJSONObject(i);
//                st_underkehu[i] = lan.getString("name");
//                st_underkehu_creater[i]=lan.getString("creater");
//                st_underkehu_creattime[i]=getStringDateShort(lan.getString("createtime"));
//            }

            if (FlagSpinner == 0) {
            } else {
                tx_mission.setText(String.valueOf(j)+"个");

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    public static String getStringDateShort(String createtime) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            sendreq();
            sendreq2();
            sendreq3();
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return true;
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    String mm;
                    String dd;
                    if (monthOfYear <= 8) {
                        mMonth = monthOfYear + 1;
                        mm = "0" + mMonth;
                    } else {
                        mMonth = monthOfYear + 1;
                        mm = String.valueOf(mMonth);
                    }
                    if (dayOfMonth <= 9) {
                        mDay = dayOfMonth;
                        dd = "0" + mDay;
                    } else {
                        mDay = dayOfMonth;
                        dd = String.valueOf(mDay);
                    }
                    mDay = dayOfMonth;
                    if (timeFlag == 0) {
                        sellStartTime.setText(String.valueOf(mYear) + "-" + mm + "-" + dd);
                    } else {
                        sellEndTime.setText(String.valueOf(mYear) + "-" + mm + "-" + dd);
                    }
                }
            };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
            case 1:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    // 隐藏手机键盘
    private void hideIM(View edt) {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();
            if (windowToken != null) {
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {

        }
    }
}
