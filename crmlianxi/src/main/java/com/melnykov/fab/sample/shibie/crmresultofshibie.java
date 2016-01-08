package com.melnykov.fab.sample.shibie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.kehu.crm_addkehu;
import com.melnykov.fab.sample.tools.CRMValidate;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

//提醒处理的类

public class crmresultofshibie extends Activity {

    //    final String wsuri = "ws://"+ UrlConstant.IP+":8000/ws";
//      此字段要IP
    //要与提醒消息对接
    final String wsuri = crmUrlConstant.crmIP;
    WebSocketConnection mConnection = new WebSocketConnection();
    String User_id;
    String picaddress;
    EditText  editName = null;
    EditText  company = null;
    EditText telephone = null;
    ArrayList<String> countries;
    EditText qq;
    EditText email;
    EditText address;
    EditText weixin;
    EditText interest;
    EditText growth;
    EditText paixi;
    EditText yidongphone;
    String strsex="男";
    Switch detail;
    String id;
    RelativeLayout backimg;
    TextView saveimg;
    String picture;
    JSONArray result;
    String contactorid;
    crmMyDatabaseHelper dbHelper;

    public String updatatime()
    {
        Date d = new Date();
        String s =String.valueOf(d.getTime());
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_show_result);
        User_id = IMApplication.getUserid(this);
        Bitmap img = null;
        Intent intent = this.getIntent();

        //获取数据
        String strid = (String) intent.getSerializableExtra("res");
        Log.e("图片id", strid);

//        String strid = "{\"lianxiren\": [{\"workphone\": \"6666999\", \"name\": \"\\u5b59\\u6811\\u6770\", \"url\": \"/mingpian//101/1439193380.jpg\", \"company\": \"\\u661f\\u5149\", \"sex\": \"\\u7537\", \"phone\": \"22222222222\", \"id\": 137}], \"cmd\": \"mingpian\", \"time\": \"1439216518\", \"kehu\": [{\"username\": 44, \"id\": 44}], \"error\": \"1\"}";

//        picture = (String) intent.getSerializableExtra("uploadFile");
//        Log.e("pictur位置", picture);

//        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//        img = BitmapFactory.decodeFile(picture);

//        imageView.setImageBitmap(img);


        backimg = (RelativeLayout) findViewById(R.id.iv_back);
        saveimg = (TextView) findViewById(R.id.shibie_add);

        detail = (Switch) findViewById(R.id.detailswitch);
        qq = (EditText) findViewById(R.id.fox);
        weixin = (EditText) findViewById(R.id.tv_fxis);
        interest = (EditText) findViewById(R.id.interest);
        growth = (EditText) findViewById(R.id.chengzhangs);
        paixi = (EditText) findViewById(R.id.paixi);
        email = (EditText) findViewById(R.id.textemail);
        address = (EditText) findViewById(R.id.textaddress);
        yidongphone = (EditText) findViewById(R.id.yidongphone);

        TextView title = (TextView)findViewById(R.id.shibie_head);
        title.setText("人工后台识别结果");

        RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) crmresultofshibie.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                strsex = rb.getText().toString().trim();

            }
        });
        editName = (EditText) findViewById(R.id.tv_name);
        company = (EditText) findViewById(R.id.tv_com);
        telephone = (EditText) findViewById(R.id.tv_fxid);
        address = (EditText) findViewById(R.id.email);

        final RelativeLayout detail1 = (RelativeLayout) findViewById(R.id.detail1);
        final RelativeLayout detail2 = (RelativeLayout) findViewById(R.id.detail2);
        final RelativeLayout detail3 = (RelativeLayout) findViewById(R.id.detail3);

        detail1.setVisibility(View.GONE);
        detail2.setVisibility(View.GONE);
        detail3.setVisibility(View.GONE);

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (detail.isChecked() == false) {
                    detail1.setVisibility(View.GONE);
                    detail2.setVisibility(View.GONE);
                    detail3.setVisibility(View.GONE);
                } else {
                    {
                        detail1.setVisibility(View.VISIBLE);
                        detail2.setVisibility(View.VISIBLE);
                        detail3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        try {
            JSONObject jsonObject = new JSONObject(strid);
            result = jsonObject.getJSONArray("lianxiren");
            String cmd = jsonObject.getString("cmd");
            if (cmd.equals("mingpian")) {
                JSONObject jsonObject2 = result.getJSONObject(0);
                String strusername = jsonObject2.getString("name");
                String strtelephone = jsonObject2.getString("workphone");
                picaddress = jsonObject2.getString("url");//图片地址
                String strorg = jsonObject2.getString("company");
                String strsex = jsonObject2.getString("sex");
                contactorid = jsonObject2.getString("id");

                editName.setText(strusername);
                company.setText(strorg);
                telephone.setText(strtelephone);
                yidongphone.setText(jsonObject2.getString("phone"));
                String path = Environment.getExternalStorageDirectory().getPath();  //Don't use "/sdcard/" here
                String  fileName = User_id+ "-0-0.jpg";
                picture = path + "/mingpian/"+User_id+"/" +fileName;
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                FinalBitmap fitmap = FinalBitmap.create(crmresultofshibie.this);
/*                fitmap.configBitmapLoadThreadSize(3);*/
                fitmap.configLoadingImage(R.drawable.load);
                fitmap.display(imageView, crmUrlConstant.download_url + picaddress);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dbHelper = new crmMyDatabaseHelper(getBaseContext(), "customer.db3", 1);
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
                countries = converCursorToList(cursor);

                //获取各个字段数据，保存数据库
                final String usercompany = company.getText().toString().trim();
                boolean tag = true;
                for (String country : countries)
                    if (country.equals(usercompany)) {
                        tag = false;
                    }
                if(tag == true)
                {
                    Dialog	dialog = new AlertDialog.Builder(crmresultofshibie.this).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "该客户尚未存在，是否进行创建？").setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent(crmresultofshibie.this, crm_addkehu.class);
                                    startActivityForResult(intent, 0);
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            return;
                        }
                    }).create();
                    dialog.show();
                    return;
                }

                mConnection.disconnect();
                final String username = editName.getText().toString().trim();
                final String userphone = telephone.getText().toString().trim();
                final String useraddress = address.getText().toString().trim();
                final String strqq = qq.getText().toString().trim();
                final String strweixin = weixin.getText().toString().trim();
                final String strinterest = interest.getText().toString().trim();
                final String strgrowth = growth.getText().toString().trim();
                final String strpaixi = paixi.getText().toString().trim();
                final String stremail = email.getText().toString().trim();
                final String stryidongphone = yidongphone.getText().toString().trim();
                Log.e(username, username);
                if (username.equals("") || username.contains("联系人名称")) {
                    editName.setError("请输入联系人名称");
                    Dialog dialog = new AlertDialog.Builder(crmresultofshibie.this).setTitle("请输入联系人名称").create();
                    dialog.show();
                    return;
                }

                Log.e(userphone, userphone);
                if (userphone.equals("") || !isNumeric(userphone)) {
                    telephone.setError("请输入正确的工作电话");
                    Dialog dialog = new AlertDialog.Builder(crmresultofshibie.this).setTitle("请输入正确的工作电话").create();
                    dialog.show();
                    return;
                }

                if (usercompany.equals("")) {
                    company.setError("请输入客户");
                    Dialog dialog = new AlertDialog.Builder(crmresultofshibie.this).setTitle("请输入客户").create();
                    dialog.show();
                    return;

                }
                if (!stremail.equals("") && !CRMValidate.isEmail(stremail)) {
                    email.setError("请输入正确的邮箱地址");
                    Dialog dialog = new AlertDialog.Builder(crmresultofshibie.this).setTitle("请输入正确的邮箱地址").create();
                    dialog.show();
                    return;
                }

                detail = (Switch) findViewById(R.id.detailswitch);

                final String str = "{\"cmd\":\"updateContacter\"," +
                        "\"type\":\"2\"," +
                        "\"uid\":\"" + User_id + "\"," +
                        "\"id\":\"" + contactorid + "\"," +
                        "\"customer\":\"" + usercompany + "\"," +//company就是customer
                        "\"username\":\"" + username + "\", " +
                        "\"strsex\":\"" + strsex + "\"," +
                        "\"workphone\":\"" + userphone + "\"," +
                        "\"yidongdianhua\":\"" + stryidongphone + "\"," +
                        "\"strqq\":\"" + strqq + "\"," +
                        "\"strweixin\":\"" + strweixin + "\"," +
                        "\"strinterest\":\"" + strinterest + "\"," +
                        "\"email\":\"" + stremail + "\"," +
                        "\"address\":\"" + useraddress + "\"," +
                        "\"pic\":\"" + picaddress + "\"," +
                        "\"relation\":\"" + " " + "\"," +
                        "\"degree\":\"" + " " + "\"," +
                        "\"strgrowth\":\"" + strgrowth + "\", " +
                        "\"strpaixi\":\"" + strpaixi + "\"}";

                try {
                    mConnection.connect(wsuri, new WebSocketHandler() {
                        @Override
                        public void onOpen() {
                            Log.e("发送的数据", str);
                            mConnection.sendTextMessage(str);
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            Log.e("haha", "Got echo: " + payload);

                            try {
                                JSONObject jsonObject = new JSONObject(payload);
                                String result = jsonObject.getString("error");
                                String cmd = jsonObject.getString("cmd");
                                String time = jsonObject.getString("time");
                                if (result.equals("1")) {
                                    crmMyDatabaseHelper dbHelper;
                                    dbHelper = new crmMyDatabaseHelper(getBaseContext(), "customer.db3", 1);
                                    //网络图片地址
                                    dbHelper.getReadableDatabase().execSQL("update lianxiren set username=?,strsex=? ,workphone=?,yidongphone=?,strqq=?,strweixin=?,strinterest=?,strgrowth=?,strpaixi=?,uid = ?,company = ?,email = ?,address = ?,pic = ?,id = ?,relation = ?,degree = ? where id=?",
                                            new String[]{username, strsex, userphone, stryidongphone, strqq, strweixin, strinterest, strgrowth, strpaixi, User_id, usercompany, stremail, useraddress, picaddress, contactorid, "", ""});
//                                    insertData(dbHelper.getReadableDatabase(), username, strsex, userphone, "", strqq, strweixin, strinterest, strgrowth, strpaixi, User_id, usercompany, stremail, useraddress, picture, contactorid, "","");

                                    dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                                            new String[]{User_id, time, "", User_id});
                                    mConnection.disconnect();
                                    crmresultofshibie.this.finish();
                                }
                            } catch (Exception e) {
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


        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crmresultofshibie.this.finish();
            }
        });

    }

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0) {
            String change01 = data.getStringExtra("data");
            if (change01 == null) {
                return;
            }
            company.setText(change01);

        }
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

    private void insertData(SQLiteDatabase db, String username, String strsex, String workphone, String yidongphone, String strqq, String strweixin
            , String strinterest, String growth, String paixi,String uid,String strcompany,String stremail,String straddress,String pic,String id,String yuliu1,String yuliu2) {
        // 执行插入语句
        db.execSQL("insert into lianxiren values(null, ? , ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)"
                , new String[]{username, strsex, workphone, yidongphone, strqq, strweixin, strinterest, growth, paixi,uid,strcompany,stremail,straddress,pic,id,"",""});
    }

    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

}
