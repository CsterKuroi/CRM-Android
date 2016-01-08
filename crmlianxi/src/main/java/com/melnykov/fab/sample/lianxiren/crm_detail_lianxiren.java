package com.melnykov.fab.sample.lianxiren;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.example.dt.testapp3.Graphics.VisitMainActivity;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.SortListView.crmSortListMainActivity;
import com.melnykov.fab.sample.crm;
import com.melnykov.fab.sample.kehu.crm_addkehu;
import com.melnykov.fab.sample.tools.CRMValidate;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import care.care_add;
import care.care_main;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class crm_detail_lianxiren extends ActionBarActivity {

    String StringE;
    String username;
    TextView scroll_view;
    EditText userName;
    EditText workPhone;
    EditText yidongPhone;
    TextView company;
    EditText email;
    EditText address;
    EditText qq;
    EditText weixin;
    EditText interest;
    EditText growth;
    EditText paixi,guanaidian;
    Button deleteBtn,editBtn,saveBtn;
    Switch sex;
    Switch detail;
    String strsex = "男";
    String status;
    String id;
    String picstr;
    RadioGroup group;
    ArrayList<String> countries;
    crmMyDatabaseHelper dbHelper;
    ImageView imageview_addcare;

    final String wsuri = crmUrlConstant.crmIP;
    WebSocketConnection mConnection = new WebSocketConnection();

    String User_id;

    private PullToZoomScrollViewEx scrollView;

    protected void onDestroy() {
        super.onDestroy();

    }


    protected ArrayList<String>
    converCursorToLists(Cursor cursor,String str)
    {
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        Log.e("renzhe",str);
        while (cursor.moveToNext())
        {
            if(str.equals(cursor.getString(15))) {
                userName.setText(cursor.getString(1));
                if(cursor.getString(2).equals("男"))
                {
                    RadioButton male = (RadioButton)  this.findViewById(R.id.radioMale);
                    male.setChecked(true);
                }
                else
                {
                    RadioButton female = (RadioButton)  this.findViewById(R.id.radioFemale);
                    female.setChecked(true);
                }

                workPhone.setText(cursor.getString(3));
                yidongPhone.setText(cursor.getString(4));
                qq.setText(cursor.getString(5));
                weixin.setText(cursor.getString(6));
                interest.setText(cursor.getString(7));
                growth.setText(cursor.getString(8));
                paixi.setText(cursor.getString(9));
                company.setText(cursor.getString(11));
                email.setText(cursor.getString(12));
                address.setText(cursor.getString(13));
                TextView name = (TextView) findViewById(R.id.tv_user_names);
                name.setText(cursor.getString(1));
                String pic;
                if(cursor.getString(14).equals("")||cursor.getString(14).contains("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
                else
                    pic = cursor.getString(14);
                ImageView img= (ImageView) findViewById(R.id.iv_zoom);

                if(pic.contains("haha.jpg"))
                {
                    int flagResId = getResources().getIdentifier("hi", "drawable", getPackageName());
                    img.setImageResource(flagResId);
                }
                else if(pic.contains( User_id+"-1-"))
                {
                    Bitmap b = BitmapFactory.decodeFile(pic);
                    img.setImageBitmap(b);
                }
                else {
            /*    Bitmap b = BitmapFactory.decodeFile(path, options);
                img.setImageBitmap(b);*/
                    FinalBitmap fitmap = FinalBitmap.create(crm_detail_lianxiren.this);
/*                    fitmap.configBitmapLoadThreadSize(3);*/
                    fitmap.configLoadingImage(R.drawable.load);
                    fitmap.display(img, crmUrlConstant.download_url + pic);
                }
                picstr = pic;
                scroll_view.setText(cursor.getString(1));
            }
        }
        return result;
    }

    public String updatatime()
    {
        Date d = new Date();
        String s =String.valueOf(d.getTime());
        return s;
    }


    private void loadViewForCode() {
        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        View headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.crm_activity_main_activity3, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
        scrollView.setParallax(false);
        scrollView.setZoomEnabled(false);
    }
 protected void onRestart(){
     super.onRestart();
     Cursor cursor2 = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=? and fid=?", new String[]{User_id,StringE});
     guanaidian.setText(cursor2.getCount()+"条记录，点击查看");
     cursor2.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //crm_activity_main_activity3
        setContentView(R.layout.activity_pull_to_zoom_scroll_view);

        loadViewForCode();
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        scroll_view = (TextView) findViewById(R.id.tv_user_name);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));

        Intent intent=getIntent();
        StringE = intent.getStringExtra("extra");
        username = intent.getStringExtra("name");
        status = intent.getStringExtra("source");

        Log.e(StringE, StringE);
        Log.e("source", status);
        scrollView.setHeaderLayoutParams(localObject);
        User_id = IMApplication.getUserid(this);
        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        Cursor cursor3 = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=? and fid=?", new String[]{User_id,StringE});
        guanaidian=(EditText)findViewById(R.id.guanaidian);
        guanaidian.setText(cursor3.getCount()+"条记录，点击查看");
        cursor3.close();


        guanaidian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(crm_detail_lianxiren.this, care_main.class).setAction("editeofonelianxiren").putExtra("lianxirenid",StringE));
            }
        });




        imageview_addcare=(ImageView)findViewById(R.id.guanaidian_addimage);
        imageview_addcare.setFocusable(true);imageview_addcare.setClickable(true);
        imageview_addcare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(crm_detail_lianxiren.this, care_add.class).setAction("lianxirenadd").
                        putExtra("lianxirenid", StringE).putExtra("username", username).putExtra("sex",strsex).putExtra("phone",workPhone.getText().toString()));
            }
        });

        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);

        detail = (Switch) findViewById(R.id.detailswitch);
        userName = (EditText) findViewById(R.id.tv_name);
        workPhone = (EditText) findViewById(R.id.tv_fxid);
        yidongPhone = (EditText) findViewById(R.id.email);
        qq = (EditText) findViewById(R.id.fox);
        weixin = (EditText) findViewById(R.id.tv_fxis);
        interest = (EditText) findViewById(R.id.interest);
        growth = (EditText)findViewById(R.id.chengzhangs);
        paixi = (EditText)findViewById(R.id.paixi);
        company  = (TextView)findViewById(R.id.tv_com);
        email  = (EditText)findViewById(R.id.textemail);
        address  = (EditText)findViewById(R.id.textaddress);

        RelativeLayout back = (RelativeLayout) findViewById( R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crm_detail_lianxiren.this.finish();

            }
        });


        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crmMyDatabaseHelper dbHelper;
                dbHelper = new crmMyDatabaseHelper(getApplication(), "customer.db3", 1);
                ArrayList<String> countries;
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
                countries = converCursorToList(cursor);
                String[] str = new String[]{"测试公司"};
                str = countries.toArray(new String[countries.size()]);
                Intent intent = new Intent(crm_detail_lianxiren.this, crmSortListMainActivity.class);
                intent.putExtra("title", "客户");
                intent.putExtra("data", str);
                startActivityForResult(intent, 0);
            }
        });

        final RelativeLayout detail10 = (RelativeLayout) findViewById(R.id.detail10);
        final RelativeLayout detail1 = (RelativeLayout) findViewById(R.id.detail1);
        final RelativeLayout detail2 = (RelativeLayout) findViewById(R.id.detail2);
     //   final RelativeLayout detail3 = (RelativeLayout) findViewById(R.id.detail3);
     /*   detail1.setVisibility(View.GONE);*/
        detail2.setVisibility(View.GONE);
   //     detail3.setVisibility(View.GONE);

        saveBtn = (Button) findViewById(R.id.button4);
        editBtn = (Button) findViewById(R.id.button);
        deleteBtn = (Button) findViewById(R.id.button5);

        group = (RadioGroup)this.findViewById(R.id.radioGroup);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) crm_detail_lianxiren.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                strsex = rb.getText().toString().trim();

            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //所有控件设置为可编辑
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mConnection.connect(wsuri, new WebSocketHandler() {
                        @Override
                        public void onOpen() {

                            //删除联系人
                            String str = "{\"cmd\":\"deleteContacter\"," +
                                    "\"type\":\"2\"," +
                                    "\"id\":\"" + StringE + "\"}";
                            Log.e("delete", str);
                            mConnection.sendTextMessage(str);
                            Log.e("haha", "Status: Connected to " + wsuri);

                        }

                        @Override
                        public void onTextMessage(String payload) {
                            Log.e("发送json 字符串", "Got echo: " + payload);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(payload);
                                String error = jsonObject.getString("error");
                                String time = jsonObject.getString("time");
                                if (error.contains("1")) {

                                    dbHelper.getReadableDatabase().execSQL("delete from lianxiren where id =?", new String[]{StringE});
                                    // 返回上一级页面

                                    dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                                            new String[]{User_id, time, "", User_id});

                                    startActivity(new Intent(crm_detail_lianxiren.this, crm.class));
                                    crm_detail_lianxiren.this.finish();
                                    mConnection.disconnect();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose(int code, String reason) {
                            Log.e("close", "Connection lost.");
                        }
                    });
                } catch (WebSocketException e) {

                    Log.e("cuowu", e.toString());
                }


            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String data1= userName.getText().toString().trim();
                final String data2= strsex;
                final String data3 = workPhone.getText().toString().trim();
                final String data4= yidongPhone.getText().toString().trim();
                final String data5 = qq.getText().toString().trim();
                final String data6 = weixin.getText().toString().trim();
                final String data7 = interest.getText().toString().trim();
                final String data8 = growth.getText().toString().trim();
                final String data9 = paixi.getText().toString().trim();
                final String data10 = company.getText().toString().trim();
                final String data11 = email.getText().toString().trim();
                final String data12 = address.getText().toString().trim();

                if (data1.equals("") || data1.contains("联系人名称")) {
                    Dialog dialog = new AlertDialog.Builder(crm_detail_lianxiren.this).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入联系人名称").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }
                if (data3.equals("") ||!isNumeric(data3)) {
                    Dialog dialog = new AlertDialog.Builder(crm_detail_lianxiren.this).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入正确的工作电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }

//                if (data3.length()!=11) {
//                    Dialog dialog = new AlertDialog.Builder(crm_detail_lianxiren.this).setIcon(
//                            android.R.drawable.btn_star).setTitle("提示").setMessage(
//                            "请输入11位工作电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).create();
//                    dialog.show();
//                    return;
//                }

                if(data10.equals(""))
                {
                    Dialog dialog = new AlertDialog.Builder(crm_detail_lianxiren.this).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请选择客户").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }
                if (!data11.equals("") && !CRMValidate.isEmail(data11))
                {
                    Dialog dialog = new AlertDialog.Builder(crm_detail_lianxiren.this).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入正确的邮箱地址").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }

                //更新联系人
                final String str = "{\"cmd\":\"updateContacter\"," +
                        "\"type\":\"2\"," +
                        "\"uid\":\""+User_id+"\"," +
                        "\"id\":\""+StringE+"\"," +
                        "\"customer\":\""+data10+"\"," +//company就是customer
                        "\"username\":\""+data1+"\", " +
                        "\"strsex\":\""+data2+"\"," +
                        "\"workphone\":\""+data3+"\"," +
                        "\"yidongdianhua\":\""+data4+"\"," +
                        "\"strqq\":\""+data5+"\"," +
                        "\"strweixin\":\""+data6+"\"," +
                        "\"strinterest\":\""+data7+"\"," +
                        "\"strgrowth\":\""+data8+"\", " +
                        "\"email\":\""+data11+"\", " +
                        "\"address\":\""+data12+"\", " +
                        "\"pic\":\""+picstr+"\", " +
                        "\"relation\":\""+"relation"+"\", " +
                        "\"degree\":\""+"degree"+"\", " +
                        "\"strpaixi\":\""+data9+"\"}";
                Log.e("发送json 字符串", str);

                mConnection.disconnect();
                try {
                    mConnection.connect(wsuri, new WebSocketHandler() {
                        @Override
                        public void onOpen() {
                            mConnection.sendTextMessage(str);
                            Log.e("haha", "Status: Connected to " + wsuri);
                        }
                        @Override
                        public void onTextMessage(String payload) {
                            Log.e("发送json 字符串", "Got echo: " + payload);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(payload);
                                String error = jsonObject.getString("error");
                                String time = jsonObject.getString("time");
                                if (error.contains("1")) {
            
                                    dbHelper.getReadableDatabase().execSQL("update lianxiren set username=?,strsex=? ,workphone=?,yidongphone=?,strqq=?,strweixin=?,strinterest=?,strgrowth=?,strpaixi=?,uid = ?,company = ?,email = ?,address = ?,pic = ?,id = ?,relation = ?,degree = ? where id=?",
                                            new String[]{data1, data2, data3, data4, data5, data6, data7, data8, data9, User_id, data10, data11, data12, picstr, StringE, "", "", StringE});

                                dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                                        new String[]{User_id, time, "", User_id});
                               //     Toast.makeText(crm_detail_lianxiren.this, "保存成功", Toast.LENGTH_SHORT);
                                    mConnection.disconnect();
                                    crm_detail_lianxiren.this.finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onClose(int code, String reason) {
                            Log.e("close", "Connection lost.");
                        }
                    });
                } catch (WebSocketException e) {
                    Log.e("cuowu", e.toString());
                }
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(detail.isChecked()==false)
                {
                    detail10.setVisibility(View.GONE);
                    detail1.setVisibility(View.GONE);
                    detail2.setVisibility(View.GONE);
         //           detail3.setVisibility(View.GONE);
                }
                else
                {
                    {
                        detail10.setVisibility(View.VISIBLE);
                        detail1.setVisibility(View.VISIBLE);
                        detail2.setVisibility(View.VISIBLE);
          //              detail3.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        Button callbtn = (Button) findViewById(R.id.callbutton);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + workPhone.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
            }
        });

        ImageView callimg = (ImageView) findViewById(R.id.tv_phone);
        callimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + workPhone.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);

            }
        });

        ImageView emailimg = (ImageView) findViewById(R.id.tv_email);
        emailimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+workPhone.getText().toString().trim()));
                startActivity(intent);

            }
        });


        ImageView historybtn = (ImageView) findViewById(R.id.history);
        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(crm_detail_lianxiren.this, VisitMainActivity.class);
                intent.putExtra("type",2);
                intent.putExtra("name",Integer.parseInt(StringE));
                startActivity(intent);

            }
        });

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null",null);
        countries = converCursorToLists(cursor,StringE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String change01 = data.getStringExtra("data");
        if (change01==null) return;
        if(change01.contains("(尚未创建，请点击创建)"))
        {
            Intent intent = new Intent(crm_detail_lianxiren.this, crm_addkehu.class);
            startActivityForResult(intent, 0);
        }
        else
            company.setText(change01);
        // 根据上面发送过去的请求吗来区别
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }

    protected ArrayList<String>
    converCursorToList(Cursor cursor) {
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        while (cursor.moveToNext()) {
            // ��������е����ݴ���ArrayList��
            if(User_id.equals(cursor.getString(10))) {
                String map = new String();
                map = cursor.getString(1);
                result.add(map);
            }
        }
        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crm_menu_main_activity3, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id==android.R.id.home)
        {
            crm_detail_lianxiren.this.finish();
            //startActivity(new Intent(detail_lianxiren.this, crm_lianxiren.class));
        }
        if(id==R.id.sures)
        {

        }

        return super.onOptionsItemSelected(item);
    }
}
