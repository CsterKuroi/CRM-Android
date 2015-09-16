package com.melnykov.fab.sample.lianxiren;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import care.care_add;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.SortListView.crmSortListMainActivity;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;
import com.melnykov.fab.sample.kehu.crm_addkehu;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import care.care_main;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import care.aa;

public class crm_addlianxiren extends Activity {
    EditText userName;
    EditText workPhone;
    EditText yidongPhone;
    EditText qq;
    TextView company;
    EditText email;
    EditText address;
    EditText weixin;
    EditText interest;
    EditText growth;
    EditText paixi;
    Switch detail;
    String strsex="男";
    crmMyDatabaseHelper dbHelper;
    String username ;
    String workphone ;
    String yidongphone ;
    String strqq ;
    String strweixin;
    String strinterest ;
    String strgrowth ;
    String strpaixi ;
    String strcompany ;
    String stremail ;
    String straddress ;
    String User_id;
    final String wsuri = crmUrlConstant.crmIP;
    WebSocketConnection mConnection = new WebSocketConnection();

    Button btn_add_care,btn_add_care2;
    LinearLayout detail0;
    String care[]=new String [100];
    int count=0;

    public crm_addlianxiren() {
        detail0 = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_activity_addlianxi);
        User_id = IMApplication.getUserid(this);
        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        Intent intent = getIntent();
        String kehu = intent.getStringExtra("kehu");

        detail0=(LinearLayout)this.findViewById(R.id.detail0);
        detail = (Switch)this. findViewById(R.id.detailswitch);
        userName = (EditText)this. findViewById(R.id.tv_name);
        workPhone = (EditText)this. findViewById(R.id.tv_fxid);
        yidongPhone = (EditText)this. findViewById(R.id.yidong);
        qq = (EditText)this. findViewById(R.id.fox);
        weixin = (EditText) findViewById(R.id.tv_fxis);
        interest = (EditText) findViewById(R.id.interest);
        growth = (EditText) findViewById(R.id.chengzhangs);
        paixi = (EditText) findViewById(R.id.paixi);
        company = (TextView) findViewById(R.id.tv_com);
        email = (EditText) findViewById(R.id.textemail);
        address = (EditText) findViewById(R.id.textaddress);

        btn_add_care=(Button)findViewById(R.id.btn_add_care);
        btn_add_care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = 4;
                startActivityForResult(new Intent().setClass(crm_addlianxiren.this,care_add.class), requestCode);
            }
        });

        btn_add_care2=(Button)findViewById(R.id.btn_add_care2);
        btn_add_care2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* int requestCode = 0;
                if(count!=0){
                    int j=0;
                    for (int i=0;i<count/4;i++){
                        Toast.makeText(crm_addlianxiren.this, care[0], Toast.LENGTH_SHORT).show();
                        dbHelper.getWritableDatabase().execSQL("insert into care_table(uid,type,time,remind,note,fname,fsex,fphone) values(?,?,?,?,?,?,?,?)", new String[]{User_id, care[j++], care[j++], care[j++], care[j++],username,strsex,workphone});
                    }*/
              /*  ArrayList<String> mydata = new ArrayList<String>();
                for (int i=0;i<count;i++){
                    mydata.add(care[i]);

                }
                startActivityForResult(new Intent().setClass(crm_addlianxiren.this, care_main.class)
                        .putStringArrayListExtra("edite", mydata).setAction("edite"), 5);*/
                startActivity(new Intent().setClass(crm_addlianxiren.this, care_main.class));

                new aa(crm_addlianxiren.this);

            }
            // startActivityForResult(new Intent().setClass(crm_addlianxiren.this,care_main.class), requestCode);

        });

        if(kehu!=null)
            company.setText(kehu);

        final RelativeLayout detail1 = (RelativeLayout) findViewById(R.id.detail1);
        final RelativeLayout detail2 = (RelativeLayout) findViewById(R.id.detail2);
        final RelativeLayout detail3 = (RelativeLayout) findViewById(R.id.detail3);
        detail1.setVisibility(View.INVISIBLE);
        detail2.setVisibility(View.INVISIBLE);
        detail3.setVisibility(View.INVISIBLE);

        ImageView backreturn = (ImageView) findViewById(R.id.iv_back);
        backreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crm_addlianxiren.this.finish();
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
                Intent intent = new Intent(crm_addlianxiren.this, crmSortListMainActivity.class);
                intent.putExtra("title", "客户");
                intent.putExtra("data", str);
                startActivityForResult(intent, 0);
            }
        });


        RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) crm_addlianxiren.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                strsex = rb.getText().toString().trim();

            }
        });

        Button savebtn = (Button) findViewById(R.id.buttonsure);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        detail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (detail.isChecked() == false) {
                    detail0.setVisibility(View.GONE);
                    detail1.setVisibility(View.GONE);
                    detail2.setVisibility(View.GONE);
                    detail3.setVisibility(View.GONE);

                } else {
                    {
                        detail0.setVisibility(View.VISIBLE);
                        detail1.setVisibility(View.VISIBLE);
                        detail2.setVisibility(View.VISIBLE);
                        detail3.setVisibility(View.VISIBLE);

                    }
                }
            }
        });
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       /* String type = data.getStringExtra("type");
//        String time = data.getStringExtra("time");
//        String remind = data.getStringExtra("remind");
//        String note = data.getStringsvExtra("note");*/
//        // 根据上面发送过去的请求吗来区别
//        switch (requestCode) {
//            case 0:
//                if(resultCode==1){
//                    btn_add_care.setText("继续添加");
//                    care[count++]= data.getStringExtra("type");
//                    care[count++]= data.getStringExtra("time");
//                    care[count++]= data.getStringExtra("remind");
//                    care[count++] = data.getStringExtra("note");
//                }
//                break;
//            case 2:
//                //  mText02.setText(change02);
//                break;
//            default:
//                break;
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crm_menu_addcustomer, menu);
        return true;
    }

    public String updatatime()
    {
        Date d = new Date();
        String s =String.valueOf(d.getTime());
        return s;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                String change01 = data.getStringExtra("data");
                if (change01==null) return;
                if(change01.contains("(尚未创建，请点击创建)"))
                {
                    Intent intent = new Intent(crm_addlianxiren.this, crm_addkehu.class);
                    startActivityForResult(intent, 0);
                }
                else
                    company.setText(change01);
                break;
            case 4:
                if(resultCode==1){
                    int ff=count/4+1;
                    btn_add_care.setText("继续添加("+ff+")");
                    care[count++]= data.getStringExtra("type");
                    care[count++]= data.getStringExtra("time");
                    care[count++]= data.getStringExtra("time2");
                    care[count++]= data.getStringExtra("remind");
                    care[count++] = data.getStringExtra("note");
                }
                break;
            case 5:
                count=0;
                ArrayList<String> mydata2 = new ArrayList<String>();
                // mydata2= getIntent().getStringArrayExtra("edite").;

                break;
            default:
                break;
        }
    }

    protected ArrayList<String>
    converCursorToList(Cursor cursor) {
        ArrayList<String> result =
                new ArrayList<String>();
        while (cursor.moveToNext()) {
            String map = new String();
            if(User_id.equals(cursor.getString(10))) {
                map = cursor.getString(1);
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }

    private boolean validate() {
        username = userName.getText().toString().trim();
        if (username.equals("") || username.contains("联系人名称")) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入联系人名称").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }
        workphone = workPhone.getText().toString().trim();
        if (workphone.equals("") ||!isNumeric(workphone)) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入正确的工作电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }

        if (workphone.length()!=11) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入11位工作电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create();
            dialog.show();
            return false;
        }
        strcompany = company.getText().toString().trim();
        if(strcompany.equals(""))
        {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请选择客户").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }

        yidongphone = yidongPhone.getText().toString();
        strqq = qq.getText().toString()!=null?qq.getText().toString().trim():"";
        strweixin = weixin.getText().toString().trim()!=null?weixin.getText().toString().trim():"";
        strinterest = interest.getText().toString().trim()!=null?interest.getText().toString().trim():"";
        strgrowth = growth.getText().toString().trim()!=null?growth.getText().toString().trim():"";
        strpaixi = paixi.getText().toString().trim()!=null?paixi.getText().toString().trim():"";
        stremail = email.getText().toString().trim();
        straddress =address.getText().toString().trim()!=null?address.getText().toString().trim():"";
        String malReg = "\\w+@\\w+\\.com";
        if(stremail==""||stremail==null&&((stremail.matches(malReg)==false)||stremail.contains("XXX")))
        {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入正确的邮箱地址").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }







        mConnection.disconnect();
        try {
            mConnection.connect(wsuri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.e("haha", "Status: Connected to " + wsuri);
                    //添加联系人
                    //pic图片路径问题
                    String str = "{\"cmd\":\"addContacter\"," +
                            "\"type\":\"2\"," +
                            "\"uid\":\""+User_id+"\"," +
                            "\"customer\":\""+strcompany+"\"," +//company就是customer
                            "\"username\":\""+username+"\", " +
                            "\"strsex\":\""+strsex+"\"," +
                            "\"workphone\":\""+workphone+"\"," +
                            "\"yidongdianhua\":\""+yidongphone+"\"," +
                            "\"strqq\":\""+strqq+"\"," +
                            "\"strweixin\":\""+strweixin+"\"," +
                            "\"strinterest\":\""+strinterest+"\"," +
                            "\"email\":\""+stremail+"\"," +
                            "\"address\":\""+straddress+"\"," +
                            "\"pic\":\""+" "+"\"," +
                            "\"relation\":\""+" "+"\"," +
                            "\"degree\":\""+" "+"\"," +
                            "\"strgrowth\":\""+strgrowth+"\", " +
                            "\"strpaixi\":\""+strpaixi+"\"}";
                    Log.e("发送json 字符串", str);
                    mConnection.sendTextMessage(str);
                }
                @Override
                public void onTextMessage(String payload) {
                    Log.e("发送json 字符串", "Got echo: " + payload);
                    parseJSON2(payload, User_id);
                    mConnection.disconnect();
                }
                @Override
                public void onClose(int code, String reason) {
                    Log.e("close", "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Log.e("cuowu", e.toString());
        }
        return true;
    }


    private void insertData(SQLiteDatabase db, String username, String strsex, String workphone, String yidongphone, String strqq, String strweixin
            , String strinterest, String growth, String paixi,String uid,String strcompany,String stremail,String straddress,String pic,String id,String yuliu1,String yuliu2) {
        // 执行插入语句
        db.execSQL("insert into lianxiren values(null, ? , ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)"
                , new String[]{username, strsex, workphone, yidongphone, strqq, strweixin, strinterest, growth, paixi,uid,strcompany,stremail,straddress,pic,id,"",""});
    }


    private void parseJSON2(String jsonData,String Uid) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String result=jsonObject.getString("error");
            String cmd=jsonObject.getString("cmd");
            String time = jsonObject.getString("time");
            String fid =jsonObject.getString("id");
            if(result.equals("1")&&cmd.equals("addContacter")) {

                insertData(dbHelper.getReadableDatabase(), username, strsex, workphone, yidongphone, strqq, strweixin, strinterest, strgrowth, strpaixi, User_id, strcompany, stremail, straddress, "", fid, "", "");
                dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                        new String[]{User_id, time, "", User_id});

                //保存关爱信息

                if(count!=0) {
                    int j = 0;
                    for (int i = 0; i < count / 4; i++) {
                        Toast.makeText(crm_addlianxiren.this, username, Toast.LENGTH_SHORT).show();
                        dbHelper.getWritableDatabase().execSQL("insert into care_table(uid,type,time,time2,note,fname,fsex,fphone,fid)" +
                                " values(?,?,?,?,?,?,?,?,?)", new String[]{User_id, care[j++], care[j++], care[j++], care[j++], username, strsex, workphone,fid});
                    }
                }

                crm_addlianxiren.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}