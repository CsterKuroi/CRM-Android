package com.code.bmj.groupnotifycation;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ricky.database.CenterDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by SpongeBob_PC on 2015/8/25.
 */
public class CreateNotificationActivity extends Activity {
    String choose_name = "";
    String choose_uid = "";
    String send_title = "";
    String send_content = "";
    private ImageView iv_back;
    private EditText et_titile;
    private EditText et_content;
    private ImageView sendok;
    private Button bt;
    private TextView tv_yixuan;
    private Switch sw_needconfim;
    public static final int REQUSETOK = 1;
    private Boolean ifneedconfirm = false;
    private WebSocketConnection mmConnection;
    private Boolean isConnected = false;
    private String type11uri = NotificationConfig.BMJ_IP;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_notification);


        sw_needconfim = (Switch) findViewById(R.id.switch_choose);
        sw_needconfim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    ifneedconfirm = true;
                    Toast.makeText(getApplicationContext(), "需要确认此通知！", Toast.LENGTH_SHORT).show();

                } else {
                    ifneedconfirm = false;
                    Toast.makeText(getApplicationContext(), "不需要确认此通知！", Toast.LENGTH_SHORT).show();

                }
            }
        });

        iv_back = (ImageView) findViewById(R.id.imageView_create_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_content = (EditText) findViewById(R.id.edittext_content);
        et_titile = (EditText) findViewById(R.id.edittext_lable);
        bt = (Button) findViewById(R.id.choosefanwei);
        tv_yixuan = (TextView) findViewById(R.id.Textview_yixuan);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                intent.setClass(CreateNotificationActivity.this, GetFanweiActivity.class);
                startActivityForResult(intent, REQUSETOK);
            }
        });
        sendok = (ImageView) findViewById(R.id.imageView_create_sendok);
        sendok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_title = String.valueOf(et_titile.getText());
                send_content = String.valueOf(et_content.getText());

//                if (isConnected == false)
//                    connect();
                if(mmConnection == null)
                    mmConnection = new WebSocketConnection();
                connect();

               // sendreq();
              //  Toast.makeText(getApplicationContext(), "fawan", Toast.LENGTH_SHORT).show();
                //这个地方需要补全

                if (send_content.equals("") || send_title.equals("") || choose_uid.equals("")) {

                }
            }
        });

    }

    public void connect() {
        try {
            mmConnection.connect(type11uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                 //   Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                    sendreq();
                }

                @Override
                public void onTextMessage(String payload) {
                    String strUTF8 = null;
                    try {

                        strUTF8 = URLDecoder.decode(payload, "UTF-8");
                        JSONObject root = new JSONObject(strUTF8);
                        String gettime = root.getString("time");
                        String server_id = root.getString("id");
                        if (root.getString("error").equals("1")) {
                            //TODO
                            String[] split_id = choose_uid.split(",");
                            String start_status="0";
                            for(int i =0 ;i<split_id.length;i++)
                            {
                                start_status = start_status + ",0";
                            }

                            CenterDatabase cd = new CenterDatabase(CreateNotificationActivity.this, null);
                            String creater_id = cd.getUID();
                            String creater_name = cd.getNameByUID(creater_id);
                            cd.close();
                            SQLiteDatabase db = (new N_LocalDataBase(getApplicationContext(), null)).getDataBase();
                            db.execSQL("insert into group_notification " +
                                    "(creatorID,server_id,needconfirm,creatorName,joinerID,joinerName,title,content,create_time,read,status,type) values" +
                                    "('" + creater_id + "','" + server_id + "','" + "0" + "','" + creater_name + "','" + choose_uid + "','" + choose_name
                                    + "','" + send_title + "','" + send_content + "','" + gettime + "','" + "false" + "','" + start_status + "','send')");
                            //uid,uidname,chooseid,choosename,title,content,create_time,"send"
                            Toast.makeText(getApplicationContext(), "收到服务器数据，插入数据库！", Toast.LENGTH_SHORT).show();

                        }
                        else Toast.makeText(getApplicationContext(), "出了设么问题", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        //解决掉返回信息的值
                        //severid

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(), "无法连接服务器", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WebSocketException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendreq() {

        JSONObject request2 = new JSONObject();
        try {
            request2.put("type", "11");
            request2.put("cmd", "addnotice");

            CenterDatabase cd = new CenterDatabase(CreateNotificationActivity.this, null);
            request2.put("creater", cd.getUID());
            request2.put("joiner", choose_uid);
            request2.put("title", send_title);
            request2.put("content", send_content);
            if (ifneedconfirm == false)
                request2.put("mytype", "0");
            else request2.put("mytype", "1");
            cd.close();


            mmConnection.sendTextMessage(request2.toString());
            Toast.makeText(getApplicationContext(), "已发送", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
           // Toast.makeText(getApplicationContext(), "Beng！", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateNotificationActivity.REQUSETOK && resultCode == RESULT_OK) {
            choose_name = data.getStringExtra("choose_name");
            choose_uid = data.getStringExtra("choose_id");

            String tempname = data.getStringExtra("choose_name");
            if (tempname.length() < 10)
                tv_yixuan.setText(data.getStringExtra("choose_name"));
            else {
                tempname = tempname.substring(0, tempname.indexOf(",")) + "等人";
                tv_yixuan.setText(tempname);

            }
        }
    }
}
