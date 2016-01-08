package com.melnykov.fab.sample.lianxiren;
//00EFE50CBCF70857D13208E4D3DA0702
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.melnykov.fab.sample.kehu.crm_detail_kehu;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.SortListView.crmSortListMainActivity;
import com.melnykov.fab.sample.tools.Imageviewcluss;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;
import com.melnykov.fab.sample.shibie.crmcapture;
import com.melnykov.fab.sample.shibie.crmmpw_recognize;
import com.melnykov.fab.sample.tools.sortName;
import com.ricky.database.CenterDatabase;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import care.care_main;

public class crm_lianxiren extends Activity {

    private static final String[] m_diqu={"我的联系人","关爱联系人","关爱点"};
    private Spinner m_Spinner3;
    private ArrayAdapter<String> adapter3;
    LinearLayout list;
    ArrayList<String>  countries;
    crmMyDatabaseHelper dbHelper;
    String spin3;
    Dialog dialog;
    String User_id;
    Lianxirenreceive receiver;
    private ProgressDialog pd;
    boolean flag = false;

    /** Called when the activity is first created. */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (!isFinishing()) {
                try {
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
                    countries = converCursorToList(cursor);
                    update(0);
                    if(pd==null) return;
                    if(pd.isShowing()==true)
                        pd.dismiss();
                } catch (Exception e) {
                }
            }
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_activity_crmlianxiren);
        spin3 = new String();


        User_id = IMApplication.getUserid(this);

        receiver = new Lianxirenreceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("database");
        this.registerReceiver(receiver, filter);


        pd = ProgressDialog.show(crm_lianxiren.this, "稍等", "数据读取中...", true, false);
        pd.show();

        new Thread(){
            @Override
            public void run() {
                //需要花时间的函数
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //向handler发消息
                handler.sendEmptyMessage(0);
            }}.start();

        list = (LinearLayout) findViewById(R.id.linelayout);
        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        final EditText search = (EditText) findViewById(R.id.tv_search);
  /*      search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            *//*    crmMyDatabaseHelper dbHelper;
                dbHelper = new crmMyDatabaseHelper(getApplication(), "customer.db3", 1);
                ArrayList<String> countries;
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
                countries = converCursorToListname(cursor);
                String[] str = new String[]{"测试公司"};
                str = countries.toArray(new String[countries.size()]);
                Intent intent = new Intent(crm_lianxiren.this, crmSortListMainActivity.class);
                intent.putExtra("title", "联系人");
                intent.putExtra("data", str);
                startActivityForResult(intent, 1);*//*
            }
        });*/

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str =  search.getText().toString();
                countries.clear();
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
                countries = converCursorTosearch(cursor, str);
                update(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog = new AlertDialog.Builder(this).setMessage(
                "请选择添加联系人方式：").setPositiveButton("手动添加",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(crm_lianxiren.this, crm_addlianxiren.class);
                        startActivityForResult(intent, 0);
                    }
                }).setNegativeButton("名片扫描", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getBaseContext(), "名片扫描。", Toast.LENGTH_LONG)
                        .show();

                Intent intent = new Intent(crm_lianxiren.this, crmmpw_recognize.class);
                startActivityForResult(intent, 0);

            }
        }).setNeutralButton("后台识别", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(crm_lianxiren.this, crmcapture.class);
                startActivityForResult(intent, 0);
            }
        }).create();

        ImageView addcus = (ImageView) this.findViewById(R.id.iv_add);
        addcus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        RelativeLayout back = (RelativeLayout) this.findViewById(R.id.iv_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(crm_lianxiren.this)
                        .setTitle("提醒")
                        .setMessage("                   确认退出吗")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        crm_lianxiren.this.finish();
                                    }
                                }).show();
            }
        });
        m_Spinner3=(Spinner)this.findViewById(R.id.spinner);
        adapter3=new ArrayAdapter<String>(this,R.layout.whitespinner_style,m_diqu);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_Spinner3.setAdapter(adapter3);
        m_Spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!flag)
                {
                    flag = true;
                    return;
                }

               String str =  m_Spinner3.getSelectedItem().toString();
                if(str.equals("我的联系人")){

                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null",null);
                    countries = converCursorToList(cursor);
                    update(0);

                    }
             else    if(str.equals("关爱联系人")) {
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select distinct fid,fname ,fphone,kehu from care_table where uid=?", new String[]{User_id});
                    countries = careCursorToList(cursor);
                    update(6);
                }
                else    if(str.equals("关爱点")) {

                    Intent intent = new Intent(crm_lianxiren.this, care_main.class);
                    startActivityForResult(intent, 5);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null",null);
        countries = converCursorToList(cursor);
        ImageView img = (ImageView)findViewById(R.id.crmarrow);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Spinner3.performClick();
            }
        });
    }


    public class Lianxirenreceive extends BroadcastReceiver//作为内部类的广播接收者
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String aa=intent.getAction();
           if(aa=="database"){
               if(intent.getStringExtra("select")==null)
                  return;
               else if(intent.getStringExtra("select").equals("ok")) {
                   Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
                   countries = converCursorToList(cursor);
                   update(0);
                   pd.dismiss();
               }
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            Log.e("requestcode"+requestCode,"resultCode"+resultCode);
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
            countries = converCursorToList(cursor);
            update(0);
        }
        else if(requestCode==1){
            String change01 = data.getStringExtra("data");
            if (change01 == null) {
                return;
            }
            countries.clear();
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
            countries = converCursorTosearch(cursor, change01);
            update(0);
        }

        // 根据上面发送过去的请求吗来区别
    }



    protected ArrayList<String>
    converCursorTosearch(Cursor cursor, String str)
    {
        Log.e(spin3,spin3);
        ArrayList<String> result =
                new ArrayList<String>();
        while (cursor.moveToNext())
        {
            String map = new String(cursor.getString(1));
            if(map.contains(str)&&User_id.equals(cursor.getString(10)))
            {
                Log.e(map+"都有哪些人呢",map);
                String strpi =  User_id+"-1-";
                String pic;
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
                else if(cursor.getString(14).equals(strpi))
                    pic = cursor.getString(14);
                else
                    pic = cursor.getString(14);
                result.add(map+";"+cursor.getString(15)+";"+pic+";"+cursor.getString(3)+";"+cursor.getString(11)+";");
            }

        }
        return result;
    }

    protected ArrayList<String>
    converCursorToListname(Cursor cursor)
    {


        ArrayList<String> result =
                new ArrayList<String>();
        while (cursor.moveToNext())
        {
            if(User_id.equals(cursor.getString(10))){
                result.add(cursor.getString(1));
            }
        }
        return result;
    }

    protected ArrayList<String>
    converCursorToList(Cursor cursor)
    {

        ArrayList<String> result =
                new ArrayList<String>();
        while (cursor.moveToNext())
        {
            if(User_id.equals(cursor.getString(10))){
                String pic;
                String strpi =  User_id+"-1-";
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
                else if(cursor.getString(14).equals(strpi))
                    pic = cursor.getString(14);
                else
                    pic = cursor.getString(14);

                result.add(cursor.getString(1)+";"+cursor.getString(15)+";"+pic+";"+cursor.getString(3)+";"+cursor.getString(11)+";");
            }
        }

        return result;
    }


    protected ArrayList<String>
    careCursorToList(Cursor cursor)
    {
        ArrayList<String> result = new ArrayList<String>();
        while (cursor.moveToNext())
        {
                String pic = "mnt/sdcard/mingpian/haha.jpg";
                    //xingming   id   tupian   workpjhone
                result.add(cursor.getString(1)+";"+cursor.getString(0)+";"+pic+";"+cursor.getString(2)+";"+cursor.getString(3)+";");
        }

        return result;
    }



    public Bitmap returnBitmap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void update(final int result)
    {
        list.removeAllViews();
        if(countries.size()==0)
        {
            TextView tip = (TextView) findViewById(R.id.nothing);
            tip.setVisibility(View.VISIBLE);
        }
        else{
            TextView tip = (TextView) findViewById(R.id.nothing);
            tip.setVisibility(View.GONE);
        }

        for (String country : countries) {

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.crm_lianxiren_item, null);
            TextView text1 = (TextView) layout.findViewById(R.id.text1);
            TextView text2 = (TextView) layout.findViewById(R.id.text2);
//            ImageView img = (ImageView) layout.findViewById(R.id.image);
            Button lianxirenphone = (Button) layout.findViewById(R.id.lianxirenphone);
            final String[] values = country.split(";");
            text1.setText( values[0].trim());

            if(values.length>4) {
                if (values[4].trim() != null)
                    text2.setText(values[4].trim());
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("extra", values[1]);
                    intent.putExtra("name", values[0]);
                    intent.putExtra("source", "lianxiren");
                    intent.setClass(crm_lianxiren.this, crm_detail_lianxiren.class);
                    startActivityForResult(intent, result);
                }
            });

            lianxirenphone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + values[3]);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            String path = new String(values[2]);
            if(path.contains("haha.jpg"))
            {
                int flagResId = getResources().getIdentifier("hi", "drawable", getPackageName());
//                img.setImageResource(flagResId);
            }
            else if(path.contains( User_id+"-1-"))
            {
                Bitmap b = BitmapFactory.decodeFile(path, options);
//                img.setImageBitmap(b);
            }
            else {
//                Bitmap b = BitmapFactory.decodeFile(path, options);
//                img.setImageBitmap(b);
//                FinalBitmap fitmap = FinalBitmap.create(crm_lianxiren.this);
////                fitmap.configBitmapLoadThreadSize(3);
//                fitmap.configLoadingImage(R.drawable.load);
//                fitmap.display(img,crmUrlConstant.download_url+path);
            }
//            img.setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));
            list.addView(layout);

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(crm_lianxiren.this)
                    .setTitle("提醒")
                    .setMessage("                   确认退出吗")
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {

                            dialog.cancel();

                        }
                    })
                    .setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    public void back(View view){
        finish();
    }

/*    private Spinner.OnItemSelectedListener m_SpinnerListener3=new AdapterView.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            switch (arg2){
                case 0:
                {
              *//*     Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null",null);
                    countries = converCursorToList(cursor);*//*
                    update();
                }
                    break;
                case 1:
                       *//*list.removeAllViews();
                       LinearLayout itemview = new LinearLayout(crm_lianxiren.this);
                       itemview.setOrientation(LinearLayout.HORIZONTAL);
                       Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=?", new String[]{User_id});
                       while (cursor.moveToNext()) {
                           // uid text,type text,time text,remind text,note text,fid text,fname text,fsex text,fphone text,state text
                           TextView text = new TextView(crm_lianxiren.this);
                           text.setText("\n       名称：       " + cursor.getString()+"\n\n       电话：       "+aa[3]);
                           text.setTextColor(getResources().getColor(R.color.black));
                           text.setTextSize(14);
                           itemview.addView(text);
                       }
                       itemview.setOnClickListener(new OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent();
                               intent.putExtra("extra", aa[1]);
                               intent.putExtra("name", aa[0]);
                               intent.putExtra("source", "lianxiren");
                               intent.setClass(crm_lianxiren.this, crm_detail_lianxiren.class);
                               startActivityForResult(intent,0);
                           }
                       });
                       list.addView(itemview);
*//*
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select fname,fid,fphone from care_table where uid=?",new String[]{User_id});
                    countries = careCursorToList(cursor);
                    update();

                    break;
                case 2:
                    startActivity(new Intent().setClass(crm_lianxiren.this,care_main.class));
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };*/
}
