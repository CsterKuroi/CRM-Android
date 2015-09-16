package com.melnykov.fab.sample.kehu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class crm_addkehu extends ActionBarActivity {
    final String wsuri = crmUrlConstant.crmIP;
    WebSocketConnection mConnection = new WebSocketConnection();
    String User_id;
    EditText userName;
    EditText userId;
    EditText userEmail;
    EditText userAddress;
    Button btn;
    Button btn5;
    crmMyDatabaseHelper dbHelper;

    private static final String[] m_leixing = {"国企", "民企", "外资"};
    private static final String[] m_xingzhi = {"线索客户", "潜在客户", "成交客户", "公海客户"};
    private static final String[] m_guimo = {"0~50人", "50~500人", "500~2000", "2000以上"};
    //添加客户状态与客户分级

    String leixing;
    String xingzhi;
    String guimo;

    private Spinner m_Spinner, m_Spinner2, m_Spinner3;
    private ArrayAdapter<String> adapter, adapter2, adapter3;

    ArrayList<String> countries;

    protected ArrayList<String>
    converCursorToList(Cursor cursor) {
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        while (cursor.moveToNext()) {
            // ��������е����ݴ���ArrayList��
            String map = new String();
            if(User_id.equals(cursor.getString(10))) {
                map = cursor.getString(1);
                result.add(map);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.crm_activity_addcustomer);

        User_id = IMApplication.getUserid(this);
        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
        countries = converCursorToList(cursor);

        userName = (EditText) findViewById(R.id.tv_name);
        userId = (EditText) findViewById(R.id.tv_fxid);
        userEmail = (EditText) findViewById(R.id.fox);
        userAddress = (EditText) findViewById(R.id.tv_fxis);

        btn = (Button) findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });



        ImageView backreturn = (ImageView) findViewById(R.id.iv_back);
        backreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("data", userName.getText().toString().trim());
                setResult(RESULT_OK, intent);
                crm_addkehu.this.finish();
            }
        });

        btn5 = (Button) findViewById(R.id.button5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crm_addkehu.this.finish();
            }
        });

        m_Spinner = (Spinner) this.findViewById(R.id.spinner1);
        //����ѡ������ArrayAdapter��������
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_leixing);
        //���������б�ķ��
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //��adapter ��ӵ�m_Spinner��
        m_Spinner.setAdapter(adapter);
        //����¼�Spinner�¼�����
        m_Spinner.setOnItemSelectedListener(m_SpinnerListener);
        //����Ĭ��ֵ
        m_Spinner.setVisibility(View.VISIBLE);

        m_Spinner2 = (Spinner) this.findViewById(R.id.spinner2);
        //����ѡ������ArrayAdapter��������
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_xingzhi);
        //���������б�ķ��
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //��adapter ��ӵ�m_Spinner��
        m_Spinner2.setAdapter(adapter2);
        //����¼�Spinner�¼�����
        m_Spinner2.setOnItemSelectedListener(m_SpinnerListener2);
        //����Ĭ��ֵ
        m_Spinner2.setVisibility(View.VISIBLE);

        m_Spinner3 = (Spinner) this.findViewById(R.id.spinner3);
        //����ѡ������ArrayAdapter��������
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_guimo);
        //���������б�ķ��
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //��adapter ��ӵ�m_Spinner��
        m_Spinner3.setAdapter(adapter3);
        //����¼�Spinner�¼�����
        m_Spinner3.setOnItemSelectedListener(m_SpinnerListener3);
        //����Ĭ��ֵ
        m_Spinner3.setVisibility(View.VISIBLE);
    }

    private Spinner.OnItemSelectedListener m_SpinnerListener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            leixing = m_leixing[arg2];

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    private Spinner.OnItemSelectedListener m_SpinnerListener2 = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            xingzhi = m_xingzhi[arg2];

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    private Spinner.OnItemSelectedListener m_SpinnerListener3 = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            guimo = m_guimo[arg2];

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crm_menu_addcustomer, menu);
        return true;
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        final String username = userName.getText().toString().trim();
        if (username.equals("") || username.contains("例如")) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入正确的联系人名称").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }
        final String usertelephone = userId.getText().toString().trim();
        if (usertelephone.equals("") || !isNumeric(usertelephone)) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入正确的客户电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }
        if(usertelephone.length()!=11){
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入11位电话！").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
            dialog.show();
            return false;
        }
        final String useraddress = userAddress.getText().toString().trim();
        if (useraddress.equals("") || username.contains("例如")) {
            Dialog dialog = new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                    "请输入客户地址").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            }).create();
            dialog.show();
            return false;
        }
        String malReg = "\\w+@\\w+\\.com";
        EditText email = (EditText) findViewById(R.id.email);
        final String useremail = email.getText().toString().trim();
        if((useremail.matches(malReg)==false)||useremail.contains("XXX"))
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

        EditText fox = (EditText) findViewById(R.id.fox);
        final String userfox = fox.getText().toString().trim();
        EditText beizhu = (EditText) findViewById(R.id.editbeizhu);
        final String userbeizhu = beizhu.getText().toString().trim();

        mConnection.disconnect();
        boolean tag = true;
        for(String country:countries)
            if(country.equals(username)) {
                tag = false;
                Toast.makeText(getBaseContext(), "该客户已存在!请重新命名", Toast.LENGTH_LONG)
                        .show();
            }

        if(tag == true) {
            try {
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        //添加客户
                        String str = "{\"cmd\":\"addCustomer\"," +
                                "\"type\":\"2\"," +
                                "\"uid\":\"" + User_id + "\"," +
                                "\"username\":\"" + username + "\"," +
                                "\"userphone\":\"" + usertelephone + "\", " +
                                "\"useremail\":\"" + useremail + "\"," +
                                "\"userfox\":\"" + userfox + "\"," +
                                "\"useraddress\":\"" + useraddress + "\"," +
                                "\"leixing\":\"" + leixing + "\"," +
                                "\"xingzhi\":\"" + xingzhi + "\"," +
                                "\"guimo\":\"" + guimo + "\"," +
                                "\"kehustate\":\"" + "kehustate" + "\"," +
                                "\"kehurank\":\"" + "kehurank" + "\"," +
                                "\"userbeizhu\":\"" + userbeizhu + "\"}";
                        Log.e("添加客户发送json 字符串", str);
                        mConnection.sendTextMessage(str);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.e("收到回复", "Got echo: " + payload);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(payload);
                            String error = jsonObject.getString("error");
                            String time = jsonObject.getString("time");
                            if (error.contains("1")) {
                                String id = jsonObject.getString("id");
                                //尚未加程度和状态
                                insertData(dbHelper.getReadableDatabase(), username, usertelephone, useremail, userfox, useraddress, leixing, xingzhi, guimo, userbeizhu, User_id, id, "", "");
                                dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                                        new String[]{User_id, time, "", User_id});
                                Intent intent = new Intent();
                                intent.putExtra("data", username);
                                setResult(RESULT_OK, intent);
                                crm_addkehu.this.finish();
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), "请尝试连接数据库!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mConnection.disconnect();
                    }
                    @Override
                    public void onClose(int code, String reason) {
                        Log.e("close", "Connection lost.");
                    }
                });
            } catch (WebSocketException e) {
                Log.e("cuowu", e.toString());
                Toast.makeText(getApplication(),"服务器已断开  ",Toast.LENGTH_SHORT);
            }
        }
        return true;
    }

    private void insertData(SQLiteDatabase db, String username, String usertelephone, String useremail, String userfox, String useraddress
            , String leixing, String xingzhi, String guimo, String userbeizhu, String uid, String id, String yuliu1, String yuliu2) {
        // 执行插入语句
        db.execSQL("insert into customer values(null, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                , new String[]{username, usertelephone, useremail, userfox, useraddress, leixing, xingzhi, guimo, userbeizhu,uid,id,yuliu1,yuliu2});
    }

}
