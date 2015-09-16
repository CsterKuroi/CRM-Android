package com.melnykov.fab.sample.kehu;
//客户状态与客户分级的筛选，查找时间的筛选
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.SortListView.crmSortListMainActivity;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;
import com.melnykov.fab.sample.popupwindow.kehu_gengxinshijianPopWindow;
import com.melnykov.fab.sample.popupwindow.kehu_shaixuanPopWindow;
import com.melnykov.fab.sample.tools.sortName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class crm_kehu extends Activity implements View.OnTouchListener {

    boolean flag = false;
    crmMyDatabaseHelper dbHelper;
    LinearLayout list;
    LinearLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;
    ImageView back;
    private List<String> listcustomers = new ArrayList<String>();
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    ArrayList<String> countries;
    String kehuxinxi = null;
    String User_id;
    String select="";
    String sort;
    MyReceiver  receiver;
    ImageView addcus;
    TextView tip;
    List<sortName> studentlist = new ArrayList<sortName>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        User_id = IMApplication.getUserid(this);
        setContentView(R.layout.crm_activity_addfriends);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("popshaixuan");
        filter.addAction("shijian");
        this.registerReceiver(receiver, filter);

        list = (LinearLayout) findViewById(R.id.linelayout);

        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
        countries = converCursorToList(cursor);

        tip = (TextView) findViewById(R.id.nothing);
        back = (ImageView) this.findViewById(R.id.iv_back);
        addcus = (ImageView) this.findViewById(R.id.iv_add);

        layout1= (LinearLayout) findViewById(R.id.kehu_sousuo);
        layout2= (LinearLayout) findViewById(R.id.kehu_shaixuantiaojian);
        layout3 = (LinearLayout) findViewById(R.id.kehu_gengxinshijian);

        list.setOnTouchListener(this);
        back.setOnTouchListener(this);
        addcus.setOnTouchListener(this);
        layout1.setOnTouchListener(this);
        layout2.setOnTouchListener(this);
        layout3.setOnTouchListener(this);

        listcustomers.add("我的客户");
        listcustomers.add("下属客户");


        mySpinner = (Spinner)findViewById(R.id.Spinner01);
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,R.layout.spinner_style, listcustomers);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!flag)
                {
                    flag = true;
                    return;
                }
                if (mySpinner.getSelectedItem().toString() == "下属客户") {
                    list.removeAllViews();
                    final String wsuri = crmUrlConstant.crmIP;
                    final WebSocketConnection mConnection = new WebSocketConnection();
                    mConnection.disconnect();
                    try {
                        mConnection.connect(wsuri, new WebSocketHandler() {
                            @Override
                            public void onOpen() {
                                //修改1，换成此申请，需要重新定义
                                String str = null;
                                    str = "{\"cmd\":\"underCustomers\"," +
                                            "\"type\":\"2\"," +
                                            "\"uid\":\"" + User_id + "\"}";
                                    mConnection.sendTextMessage(str);

                            }

                            @Override
                            public void onTextMessage(String payload) {
                                Log.e("数据开始同步。。。。。", "Got echo: " + payload);
                                try {
                                    JSONObject jsonObject = new JSONObject(payload);
                                    String result = jsonObject.getString("error");
                                    String cmd = jsonObject.getString("cmd");
                                    if (result.equals("1")) {
                                        list.removeAllViews();
                                        JSONArray content = jsonObject.getJSONArray("res");
                                        for (int i = 0; i < content.length(); i++) {
                                            JSONObject jsonObject2 = content.getJSONObject(i);
                                            final String username = jsonObject2.getString("uname");
                                            final  String  kehuinfo = jsonObject2.toString();
                                            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.crm_list_item, null);
                                            int flagResId = getResources().getIdentifier("a_9", "drawable", getPackageName());
                                            textView.setText(username);
                                            textView.setCompoundDrawablesWithIntrinsicBounds(flagResId, 0, 0, 0);
                                            textView.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("extra", username);
                                                    intent.putExtra("kehuxinxi", kehuinfo);
                                                    Log.e(kehuinfo,kehuinfo);
                                                    intent.setClass(crm_kehu.this, crm_detail_kehu.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            list.addView(textView);
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                            Toast.makeText(getBaseContext(),payload,Toast.LENGTH_SHORT).show();
                                mConnection.disconnect();
                            }

                            @Override
                            public void onClose(int code, String reason) {
                                Log.e("close", "Connection lost.");
                            }
                        });
                    } catch (WebSocketException e) {
                        Log.e("cuowu", e.toString());
                        Toast.makeText(getApplication(), "服务器已断开  ", Toast.LENGTH_SHORT);
                    }
                }else
                {
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
                    countries = converCursorToList(cursor);
                    update();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        update();
    }

    /**
     * Called when the activity is first created.
     */
    public class MyReceiver extends BroadcastReceiver//作为内部类的广播接收者
    {
        @Override
        public void onReceive(Context context, Intent intent) {
             int i;
            String aa=intent.getAction();
            if(aa=="popshaixuan"){
                select=intent.getStringExtra("select");
                crmMyDatabaseHelper dbHelper;
                dbHelper = new crmMyDatabaseHelper(getApplication(), "customer.db3", 1);
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
                countries=shaixuan(cursor,select);
                update();
            }
            else if(aa=="shijian"){
                sort= intent.getStringExtra("select");
                //countries排序
                //update
                int j =0;
                if(sort.equals("1")){
                Collections.sort(studentlist, new SortByCreatetime());
                for(sortName name:studentlist) {
                    countries.set(j++, name.getData());
                    if(j<countries.size())
                        break;
                }}
                else   if(sort.equals("2")) {

                    Collections.sort(studentlist, new SortByName());
                    for (sortName name : studentlist) {
                        countries.set(j++, name.getData());
                        if(j<countries.size())
                            break;
                    }
                }
                else   if(sort.equals("3")) {
                    Collections.sort(studentlist, new SortByUpdatetime());
                    for (sortName name : studentlist) {
                        countries.set(j++, name.getData());
                        if(j<countries.size())
                            break;
                    }
                }
                update();

            }

        }
    }


    class SortByCreatetime implements Comparator {
        public int compare(Object o1, Object o2) {
            sortName s1 = (sortName) o1;
            sortName s2 = (sortName) o2;
            if (s1.getCreatetime().compareTo(s2.getCreatetime())<0)
                return -1;
            else if (s1.getCreatetime().compareTo(s2.getCreatetime())>0) {
                return 1;
            }
            return 0;
        }
    }

    class SortByName implements Comparator {
        public int compare(Object o1, Object o2) {
            sortName s1 = (sortName) o1;
            sortName s2 = (sortName) o2;
            if(s1.getName().compareTo(s2.getName()) < 0)
                return -1;
            else if (s1.getName().compareTo(s2.getName()) > 0) {
                return 1;
            }
            return 0;
        }
    }


    class SortByUpdatetime implements Comparator {
        public int compare(Object o1, Object o2) {
            sortName s1 = (sortName) o1;
            sortName s2 = (sortName) o2;
            if(s1.getUpdatetime().compareTo(s2.getUpdatetime()) < 0)
                return -1;
            else if (s1.getUpdatetime().compareTo(s2.getUpdatetime()) > 0) {
                return 1;
            }
            return 0;
        }
    }


    public boolean onTouch(View v, MotionEvent e){
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            if (v.getId() == R.id.kehu_sousuo) {
                layout1.setBackgroundColor(Color.LTGRAY);
            } else if (v.getId() == R.id.kehu_shaixuantiaojian) {
                layout2.setBackgroundColor(Color.LTGRAY);
            } else if (v.getId() == R.id.kehu_gengxinshijian) {
                layout3.setBackgroundColor(Color.LTGRAY);
            } /*else if(v.getId() == R.id.iv_back){
                back.setBackgroundColor(Color.LTGRAY);
            } else if(v.getId() ==R.id.iv_add ) {
                addcus.setBackgroundColor(Color.LTGRAY);
            }*/
        }
        if(e.getAction()==MotionEvent.ACTION_UP){
            crmMyDatabaseHelper dbHelper;
            dbHelper = new crmMyDatabaseHelper(getApplication(), "customer.db3", 1);
            ArrayList<String> countries;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);

            if (v.getId() == R.id.kehu_sousuo) {
                countries = converCursorToList(cursor);
                String[] str = new String[]{"测试公司"};
                str = countries.toArray(new String[countries.size()]);
                Intent intent = new Intent(crm_kehu.this, crmSortListMainActivity.class);
                intent.putExtra("title", "客户");
                intent.putExtra("data", str);
                startActivityForResult(intent, 1);
                layout1.setBackgroundColor(Color.WHITE);
            } else if (v.getId() == R.id.kehu_shaixuantiaojian) {
                kehu_shaixuanPopWindow mm = new kehu_shaixuanPopWindow(crm_kehu.this);
                mm.showPopupWindow(v);
                layout2.setBackgroundColor(Color.WHITE);
            } else if (v.getId() == R.id.kehu_gengxinshijian) {
                kehu_gengxinshijianPopWindow mm2 = new kehu_gengxinshijianPopWindow(crm_kehu.this);
                mm2.showPopupWindow(v);
                layout3.setBackgroundColor(Color.WHITE);
            }else if(v.getId() == R.id.iv_back){
                crm_kehu.this.finish();
            }else if(v.getId() == R.id.iv_add){
                Intent intent = new Intent(crm_kehu.this, crm_addkehu.class);
                startActivityForResult(intent, 0);
            }
        }
        if(e.getAction()==MotionEvent.ACTION_CANCEL){
            if (v.getId() == R.id.kehu_sousuo) {
                layout1.setBackgroundColor(Color.WHITE);
            } else if (v.getId() == R.id.kehu_shaixuantiaojian) {
                layout2.setBackgroundColor(Color.WHITE);
            } else if (v.getId() == R.id.kehu_gengxinshijian) {
                layout3.setBackgroundColor(Color.WHITE);
            }
        }
        return true;
    }

    protected ArrayList<String>
    shaixuan(Cursor cursor,String select) {
        final String[] aa = select.split(";");
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        while (cursor.moveToNext()) {
            // ��������е����ݴ���ArrayList��
            String map = new String();
            if(User_id.equals(cursor.getString(10))){
                //修改我的客户
                for (int i =0 ;i<aa.length;i++)
                {
                    if(cursor.getString(6).equals(aa[i])||cursor.getString(7).equals(aa[i]))
                    {
                        map = cursor.getString(1);
                        result.add(map);
                        break;
                    }

                }

            }

        }
        return result;
    }


    protected ArrayList<String>
    converCursorToList(Cursor cursor) {
        ArrayList<String> result =
                new ArrayList<String>();
        // ����Cursor�����
        while (cursor.moveToNext()) {
            // ��������е����ݴ���ArrayList��
            String map = new String();
            if(User_id.equals(cursor.getString(10))){
                //修改我的客户
                map = cursor.getString(1);
                result.add(map);
                sortName  sort = new sortName(cursor.getString(1),cursor.getString(1),cursor.getString(11),cursor.getString(10));
                studentlist.add(sort);
            }

        }
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==0){
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
            countries = converCursorToList(cursor);
            update();}
        else if(requestCode==1){
            String change01 = data.getStringExtra("data");
            if (change01 == null) {
                return;
            }
            if(change01.contains("(尚未创建，请点击创建)"))
            {
                Intent intent = new Intent(getBaseContext(), crm_addkehu.class);
                startActivityForResult(intent, 0);
            }
            else
            {
                countries.clear();
                countries.add(change01);
                update();
            }

        }

        // 根据上面发送过去的请求吗来区别
    }


    //此处可能有BUg
    private void update() {
        list.removeAllViews();
        if(countries.size()==0)
        {

            tip.setVisibility(View.VISIBLE);
        }
        else
            tip.setVisibility(View.GONE);
        for (String country : countries) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.crm_list_item, null);
            final String[] values = country.split(",");
            final String countryName = values[0].trim();
            int flagResId = getResources().getIdentifier("a_9", "drawable", getPackageName());
            textView.setText(countryName);
            textView.setCompoundDrawablesWithIntrinsicBounds(flagResId, 0, 0, 0);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("extra", countryName);
                    intent.setClass(crm_kehu.this, crm_detail_kehu.class);
                    startActivity(intent);
                }
            });
            list.addView(textView);
        }
    }
    public void back(View view) {
        finish();
    }

}
