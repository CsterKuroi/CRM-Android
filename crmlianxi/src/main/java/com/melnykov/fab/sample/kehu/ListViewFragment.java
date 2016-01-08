package com.melnykov.fab.sample.kehu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.dt.testapp3.Graphics.VisitMainActivity;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.tools.CRMValidate;
import com.melnykov.fab.sample.tools.IMApplication;
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


/**
 * Created by renxin on 2015/8/28.
 */
public  class ListViewFragment extends Fragment {
    private android.widget.Spinner Spinner,Spinner2,Spinner3,Spinner4,Spinner5;
    private ArrayAdapter<String> adapters,adapters2,adapters3,adapters4,adapters5;
    EditText userName;
    EditText userId;
    EditText userEmail;
    EditText userAddress;
    EditText fox;
    EditText beizhu;
    String stre;
    crmMyDatabaseHelper dbHelper;
    ArrayList<String> countries;
    Button saveBt;
    String User_id;
    String id;
    String kehuxinxi;
    private String[] m_leixing={"国企","民企","外资"};
    private String[] m_xingzhi={"线索客户","潜在客户","成交客户","公海客户"};
    private String[] m_guimo={"0~50人","50~500人","500~2000","2000以上"};
    private String[] m_state={"初步沟通","见面拜访","确定意向","正式报价","商务洽谈","签约成交","售后服务","停滞客户","流失客户"};
    private String[] m_rank={"小型客户","中型客户","大型客户","Vip客户"};
    final String wsuri = crmUrlConstant.crmIP;
    WebSocketConnection mConnection = new WebSocketConnection();


    private void insertData(SQLiteDatabase db, String username, String usertelephone, String useremail, String userfox, String useraddress
            , String leixing, String xingzhi, String guimo, String userbeizhu, String uid, String id, String yuliu1, String yuliu2) {
        // 执行插入语句
        db.execSQL("insert into customer values(null, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                , new String[]{username, usertelephone, useremail, userfox, useraddress, leixing, xingzhi, guimo, userbeizhu,uid,id,yuliu1,yuliu2});
    }


    public ListViewFragment(String str,String  kehuxinxi)
    {
        this.stre=str;
        this.kehuxinxi = kehuxinxi;
    }

    public ListViewFragment( )
    {

    }


    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void up(String str)
    {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null",null);
        countries = converCursorToLists(cursor,str);
    };

    protected ArrayList<String>
    converCursorToLists(Cursor cursor,String str)
    {
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        while (cursor.moveToNext())
        {
            if(User_id.equals(cursor.getString(10))){
                result.add(cursor.getString(1));
                if(str.equals(cursor.getString(1))) {
                    userName.setText(cursor.getString(1));
                    userId.setText(cursor.getString(2));
                    userEmail.setText(cursor.getString(3));
                    fox.setText(cursor.getString(4));
                    userAddress.setText(cursor.getString(5));
                    int i =0;
                    for (i =0;i<m_leixing.length;i++)
                    {
                        if( m_leixing[i].equals(cursor.getString(6)))
                            break;
                    }
                    if(i>=m_leixing.length) i=0;
                    int j =0;
                    for ( j =0;j<m_xingzhi.length;j++)
                    {
                        if (m_xingzhi[j].equals(cursor.getString(7)))
                            break;
                    }
                    if(j>=m_xingzhi.length) j=0;
                    int k=0;
                    for ( k=0;k<m_guimo.length;k++)
                    {
                        if(m_guimo[k].equals(cursor.getString(8)))
                            break;
                    }
                    if(k>=m_guimo.length) k=0;
                    Spinner.setSelection(i);
                    Spinner4.setSelection(j);
                    Spinner3.setSelection(k);
                    beizhu.setText(cursor.getString(9));
                    id = cursor.getString(11);
                    break;
                }
            }

        }
        return result;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        User_id = IMApplication.getUserid(getActivity());
        final View root = inflater.inflate(R.layout.crm_activity_detailkehu, container, false);
        dbHelper = new crmMyDatabaseHelper(getActivity(), "customer.db3", 1);

        userName = (EditText) root.findViewById(R.id.tv_name);
        userId = (EditText) root.findViewById(R.id.tv_fxid);
        userEmail = (EditText) root.findViewById(R.id.email);
        fox = (EditText) root.findViewById(R.id.fox);
        userAddress = (EditText) root.findViewById(R.id.tv_fxis);
        beizhu = (EditText) root.findViewById(R.id.editText);
        //客户拜访历史
        Button btnHis = (Button) root.findViewById(R.id.history);
        btnHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VisitMainActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("name",Integer.parseInt(id));
                startActivity(intent);

            }
        });

        Spinner=(android.widget.Spinner)root.findViewById(R.id.spinner1);
        adapters=new ArrayAdapter<String>(getActivity(),R.layout.crm_spinner_style,m_leixing);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner.setAdapter(adapters);
        Spinner.setOnItemSelectedListener(m_SpinnerListener);
        Spinner.setVisibility(View.VISIBLE);
        Spinner2=(android.widget.Spinner)root.findViewById(R.id.spinner2);
        adapters2=new ArrayAdapter<String>(getActivity(),R.layout.crm_spinner_style,m_state);
        adapters2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner2.setAdapter(adapters2);
        Spinner2.setOnItemSelectedListener(m_SpinnerListener2);
        Spinner2.setVisibility(View.VISIBLE);
        Spinner3=(android.widget.Spinner)root.findViewById(R.id.spinner3);
        adapters3=new ArrayAdapter<String>(getActivity(),R.layout.crm_spinner_style,m_guimo);
        adapters3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner3.setAdapter(adapters3);
        Spinner3.setOnItemSelectedListener(m_SpinnerListener3);
        Spinner3.setVisibility(View.VISIBLE);
        Spinner4=(android.widget.Spinner)root.findViewById(R.id.spinner4);
        adapters4=new ArrayAdapter<String>(getActivity(),R.layout.crm_spinner_style,m_xingzhi);
        adapters4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner4.setAdapter(adapters4);
        Spinner4.setVisibility(View.VISIBLE);

        Spinner5=(android.widget.Spinner)root.findViewById(R.id.spinner5);
        adapters5=new ArrayAdapter<String>(getActivity(),R.layout.crm_spinner_style,m_rank);
        adapters5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner5.setAdapter(adapters5);
        Spinner5.setVisibility(View.VISIBLE);

        saveBt= (Button)root.findViewById(R.id.button6);
        Button callbtn = (Button) root.findViewById(R.id.callbutton);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + userId.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
            }
        });

        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //保存数据库

                final String data1 = userName.getText().toString().trim();
                final String data2 = userId.getText().toString().trim();
                final String data3 = userEmail.getText().toString().trim();
                final  String data4 = fox.getText().toString().trim();
                final String data5 = userAddress.getText().toString().trim();
                final  String data6 = beizhu.getText().toString().trim();
                final  String data7 = m_leixing[Spinner.getSelectedItemPosition()];
                final String data8 = m_xingzhi[Spinner2.getSelectedItemPosition()];
                final String data9 = m_guimo[Spinner3.getSelectedItemPosition()];

                if (data1.equals("")) {
                    Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入正确的联系人名称").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }

                if (data2.equals("") || !isNumeric(data2)) {
                    Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入正确的客户电话").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }
//                if(data2.length()!=11){
//                    Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
//                            android.R.drawable.btn_star).setTitle("提示").setMessage(
//                            "请输入11位电话！").setNeutralButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    }).create();
//                    dialog.show();
//                    return;
//                }

//                String malReg = "\\w+@\\w+\\.com";
//                if((data3.matches(malReg)==false)||data3.contains("XXX"))
                if (!data3.equals("") && !CRMValidate.isEmail(data3))
                {
                    Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                            android.R.drawable.btn_star).setTitle("提示").setMessage(
                            "请输入正确的邮箱地址").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dialog.show();
                    return;
                }

                if(!data1.equals(stre)) {
                    for (String company : countries) {
                        if (data1.equals(company)&&data1!=stre)
                        {
                            Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                                    android.R.drawable.btn_star).setTitle("提示").setMessage(
                                    "该客户已存在，请重新命名！").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                            dialog.show();
                            return;
                        }
                    }
                }

                final String str = "{\"cmd\":\"updateCustomer\"," +
                        "\"type\":\"2\"," +
                        "\"id\":\"" + id + "\"," +
                        "\"uid\":\"" + User_id + "\"," +
                        "\"username\":\"" + data1 + "\"," +
                        "\"userphone\":\"" + data2 + "\", " +
                        "\"useremail\":\"" + data3 + "\"," +
                        "\"userfox\":\"" + data4 + "\"," +
                        "\"useraddress\":\"" + data5 + "\"," +
                        "\"leixing\":\"" + data7 + "\"," +
                        "\"xingzhi\":\"" + data8 + "\"," +
                        "\"guimo\":\"" + data9 + "\"," +
                        "\"kehustate\":\"" + "kehustate" + "\"," +
                        "\"kehurank\":\"" + "kehurank" + "\"," +
                        "\"userbeizhu\":\"" + data6 + "\"}";


                mConnection.disconnect();

                try {
                    mConnection.connect(wsuri, new WebSocketHandler() {
                        @Override
                        public void onOpen() {
                            mConnection.sendTextMessage(str);
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(payload);
                                String error = jsonObject.getString("error");
                                String time = jsonObject.getString("time");
                                if (error.contains("1")) {
                                    dbHelper.getReadableDatabase().execSQL("update customer set username=?,userphone=?,useremail=?,userfox=?,useraddress=?,leixing=?,xingzhi=?,guimo=?,userbeizhu=?,uid=?,id=?,kehustate=?,kehurank=? where username=? and uid = ?",
                                            new String[]{data1, data2, data3, data4, data5, data7, data8, data9, data6, User_id, id, "", "", stre,User_id});

                                    dbHelper.getReadableDatabase().execSQL("update lianxiren set company = ? where company=? and uid = ?",
                                            new String[]{data1,stre,User_id});

                                    dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
                                            new String[]{User_id, time, "", User_id});
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mConnection.disconnect();

                            Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                                    android.R.drawable.btn_star).setTitle("保存成功！").setMessage(
                                    "请选择跳转页面：").setPositiveButton("客户主页面",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            Intent intent = new Intent(getActivity(), crm_kehu.class);
                                            startActivityForResult(intent, 0);
                                            getActivity().finish();
                                        }
                                    }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                            dialog.show();
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

        if(stre!=null)
            up(stre);

        if(kehuxinxi !=null)
            updateunder(kehuxinxi);
        return root;
    }

    public  void updateunder(String payload){
        try {
            JSONObject jsonObject2 = new JSONObject(payload);
            String useraddress = jsonObject2.getString("useraddress");
            String useremail = jsonObject2.getString("useremail");
            String uid = jsonObject2.getString("uid");
            String guimo = jsonObject2.getString("guimo");
            String userfox = jsonObject2.getString("userfox");
            String username = jsonObject2.getString("uname");
            String userbeizhu = jsonObject2.getString("userbeizhu");
            String xingzhi = jsonObject2.getString("xingzhi");
            String leixing = jsonObject2.getString("leixing");
            String sid = jsonObject2.getString("id");
            String userphone = jsonObject2.getString("userphone");
            String kehustate = jsonObject2.getString("kehustate");
            String kehurank = jsonObject2.getString("kehurank");

            userName.setText(username);
            userId.setText(userphone);
            userEmail.setText(useremail);
            fox.setText(userfox);
            userAddress.setText(useraddress);
            int i =0;
            for (i =0;i<m_leixing.length;i++)
            {
                if( m_leixing[i].equals(leixing))
                    break;
            }
            if(i>=m_leixing.length) i=0;
            int j =0;
            for ( j =0;j<m_xingzhi.length;j++)
            {
                if (m_xingzhi[j].equals(xingzhi))
                    break;
            }
            if(j>=m_xingzhi.length) j=0;
            int k=0;
            for ( k=0;k<m_guimo.length;k++)
            {
                if(m_guimo[k].equals(guimo))
                    break;
            }
            if(k>=m_guimo.length) k=0;
            Spinner.setSelection(i);
            Spinner2.setSelection(j);
            Spinner3.setSelection(k);
            beizhu.setText(userbeizhu);
            id = sid;
            saveBt.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public String updatatime()
    {
        Date d = new Date();
        String s =String.valueOf(d.getTime());
        return s;
    }

    private android.widget.Spinner.OnItemSelectedListener m_SpinnerListener=new android.widget.Spinner.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    private android.widget.Spinner.OnItemSelectedListener m_SpinnerListener2=new android.widget.Spinner.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    private android.widget.Spinner.OnItemSelectedListener m_SpinnerListener3=new android.widget.Spinner.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub


        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

}