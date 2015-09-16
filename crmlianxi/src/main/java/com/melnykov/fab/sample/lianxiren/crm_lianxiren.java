package com.melnykov.fab.sample.lianxiren;
//00EFE50CBCF70857D13208E4D3DA0702
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.SortListView.crmSortListMainActivity;
import com.melnykov.fab.sample.tools.Imageviewcluss;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;
import com.melnykov.fab.sample.shibie.crmcapture;
import com.melnykov.fab.sample.shibie.crmmpw_recognize;
import com.ricky.database.CenterDatabase;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import care.care_main;

public class crm_lianxiren extends Activity {

    private static final String[] m_diqu={"所有联系人","关爱联系人","关爱点"};
    private Spinner m_Spinner3;
    private ArrayAdapter<String> adapter3;
    LinearLayout list;
    ArrayList<String>  countries;
    crmMyDatabaseHelper dbHelper;
    String spin3;
    Dialog dialog;
    String User_id;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_activity_crmlianxiren);
        spin3 = new String();


        User_id = IMApplication.getUserid(this);

        list = (LinearLayout) findViewById(R.id.linelayout);
        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        TextView search = (TextView) findViewById(R.id.tv_search);
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                crmMyDatabaseHelper dbHelper;
                dbHelper = new crmMyDatabaseHelper(getApplication(), "customer.db3", 1);
                ArrayList<String> countries;
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
                countries = converCursorToListname(cursor);
                String[] str = new String[]{"测试公司"};
                str = countries.toArray(new String[countries.size()]);
                Intent intent = new Intent(crm_lianxiren.this, crmSortListMainActivity.class);
                intent.putExtra("title", "联系人");
                intent.putExtra("data", str);
                startActivityForResult(intent, 1);
            }
        });

        dialog = new AlertDialog.Builder(this).setIcon(
                android.R.drawable.btn_star).setTitle("添加联系人").setMessage(
                "请选择添加方式：").setPositiveButton("手动添加",
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

        ImageView back = (ImageView) this.findViewById(R.id.iv_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                crm_lianxiren.this.finish();
            }
        });

        m_Spinner3=(Spinner)this.findViewById(R.id.spinner);
        adapter3=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m_diqu);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_Spinner3.setAdapter(adapter3);
        m_Spinner3.setOnItemSelectedListener(m_SpinnerListener3);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null",null);
        countries = converCursorToList(cursor);
        update();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            Log.e("requestcode"+requestCode,"resultCode"+resultCode);
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
            countries = converCursorToList(cursor);
            update();
        }
        else if(requestCode==1){
            String change01 = data.getStringExtra("data");
            if (change01 == null) {
                return;
            }
            countries.clear();
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
            countries = converCursorTosearch(cursor, change01);
            update();
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
                String pic;
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
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
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
                else
                    pic = cursor.getString(14);

                result.add(cursor.getString(1)+";"+cursor.getString(15)+";"+pic+";"+cursor.getString(3)+";"+cursor.getString(11)+";");
            }
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

    private void update()
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

        int i =2 ;
        for (String country : countries) {
            i++;

            final String[] aa = country.split(";");
            LinearLayout itemview = new LinearLayout(this);
            itemview.setOrientation(LinearLayout.HORIZONTAL);
            itemview.setGravity(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.HORIZONTAL | Gravity.CENTER_HORIZONTAL);
            ImageView img = new ImageView(this);
            final String countryName = country;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            String path = new String(aa[2]);

            if(path.contains("haha.jpg"))
            {
                int flagResId = getResources().getIdentifier("hi", "drawable", getPackageName());
                img.setImageResource(flagResId);
            }
            else {
//                Bitmap b = BitmapFactory.decodeFile(path, options);
//                img.setImageBitmap(b);

                FinalBitmap fitmap = FinalBitmap.create(crm_lianxiren.this);
//                fitmap.configBitmapLoadThreadSize(3);
                fitmap.configLoadingImage(R.drawable.load);
                fitmap.display(img,crmUrlConstant.download_url+path);
            }

            img.setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));

            TextView textpad = new TextView(this);
            textpad.setText("       ");
            if(i%2==0)
            {
                itemview.setBackgroundColor(getResources().getColor(R.color.lightgray));
                textpad.setTextColor(getResources().getColor(R.color.lightgray));
            }
            else
            {  itemview.setBackgroundColor(getResources().getColor(R.color.white));
                textpad.setTextColor(getResources().getColor(R.color.white));
            }

            TextView text = new TextView(this);
            text.setText("\n       名称：       " + aa[0]+"\n\n       电话：       "+aa[3]);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setTextSize(14);
            itemview.addView(textpad);
            itemview.addView(img);
            itemview.addView(text);
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

        }
    }


    public void back(View view){
        finish();
    }

    private Spinner.OnItemSelectedListener m_SpinnerListener3=new Spinner.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            switch (arg2){
                case 0:
                    break;
                case 1:
                       /*list.removeAllViews();
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
*/
                    break;
                case 2:
                    startActivity(new Intent().setClass(crm_lianxiren.this,care_main.class));
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
}
