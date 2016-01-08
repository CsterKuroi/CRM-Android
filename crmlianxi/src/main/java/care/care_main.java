package care;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.lianxiren.crm_lianxiren;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.ricky.database.CenterDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dell on 2015/8/26.
 */
public class care_main extends Activity {
    crmMyDatabaseHelper dbHelper;
    String userid;
    ListView listView;
    String care_ids[]=new String[100];
    int useridsflag=0;
    ArrayList<String> mydata = new ArrayList<String>();
    int jj=0;
    ArrayList<String> mydata2 = new ArrayList<String>();
    String mydata3[]=new String[100];
    RelativeLayout iv_back;
    TextView tv_submit,nothing;
    int w;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.care_remind);

        dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
        CenterDatabase centerDatabase = new CenterDatabase(this, null);
        userid = centerDatabase.getUID();
        centerDatabase.close();
        tv_submit=(TextView)findViewById(R.id.care_submit);

        if(getIntent().getAction()=="editeofonelianxiren")   tv_submit.setVisibility(View.GONE);

        iv_back=(RelativeLayout)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent().setClass(care_main.this,crm_lianxiren.class));
                care_main.this.finish();
            }
        });

        tv_submit.setVisibility(View.VISIBLE);
        tv_submit.setText("编辑");
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(tv_submit.getText().toString().trim()=="删除"){
                if(getIntent().getAction()=="editeofonelianxiren"){
                    startActivity(new Intent().setClass(care_main.this, care_main.class).setAction("deleteofonelianxiren").putExtra("lianxirenid",getIntent().getStringExtra("lianxirenid")));
                }else
                startActivity(new Intent().setClass(care_main.this, care_main.class));
                care_main.this.finish();
            }
                else if(tv_submit.getText().toString().trim()=="编辑"){
                if(getIntent().getAction()=="deleteofonelianxiren"){
                    startActivity(new Intent().setClass(care_main.this, care_main.class).setAction("editeofonelianxiren").putExtra("lianxirenid",getIntent().getStringExtra("lianxirenid")));

                }else
                    startActivity(new Intent().setClass(care_main.this, care_main.class).setAction("delete"));
                care_main.this.finish();
                }
                else if(tv_submit.getText().toString().trim()=="确定"){
                Intent intent=new Intent();
                intent.putStringArrayListExtra("edite",mydata2);
                // 设置结果，并进行传送
                care_main.this.setResult(5, intent);
            }

            }
        });
        final List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        if(getIntent().getAction()=="edite"){
            int j=0;
            tv_submit.setVisibility(View.VISIBLE);
            mydata=getIntent().getStringArrayListExtra("edite");
            for (int i=0;i<mydata.size();i++){
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("type",mydata.get(j++));
                listItem.put("time",mydata.get(j++));
                listItem.put("time2", mydata.get(j++));
                //listItem.put("priorty",mydata.get(j++));
                listItems.add(listItem);

                SimpleAdapter simpleAdapter = new SimpleAdapter(care_main.this, listItems, R.layout.care_remind_item, new String[]{"name","type","time", "time2"}, new int[]{R.id.care_name, R.id.care_type, R.id.care_time, R.id.care_time2});
                listView = (ListView) findViewById(R.id.care_list);
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        //获得选中项的HashMap对象
                       // Toast.makeText(care_main.this, "点击了" + arg2, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(care_main.this, care_add.class);
                        intent.putExtra("id", care_ids[arg2]);
                        intent.setAction("rewrite");
                        startActivity(intent);
                    }
                });
            }

        } else if(getIntent().getAction()=="delete") {
            tv_submit.setVisibility(View.VISIBLE);
            tv_submit.setText("删除");
            //批量删除
            useridsflag=0;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=?", new String[]{userid});
            while (cursor.moveToNext()) {
                care_ids[useridsflag++]=new String (String.valueOf(cursor.getInt(0)));
                // uid text,type text,time text,time2 text,note text,fid text,fname text,fsex text,fphone text,state text
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("fname",cursor.getString(7));
                listItem.put("type",cursor.getString(2));
                listItem.put("time", cursor.getString(3));
                listItem.put("time2", cursor.getString(4));
                //listItem.put("remind", cursor.getString(5));
                //listItem.put("priorty", cursor.getString(8));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(care_main.this, listItems, R.layout.care_remind_item, new String[]{"fname","type","time", "time2"}, new int[]{R.id.care_name, R.id.care_type, R.id.care_time, R.id.care_time2});
            listView = (ListView) findViewById(R.id.care_list);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    w=arg2;
                    new AlertDialog.Builder(care_main.this)
                            .setTitle("提醒")
                            .setMessage("确定删除！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  Toast.makeText(care_main.this,"_id"+care_ids[w],Toast.LENGTH_SHORT).show();
                                    dbHelper.getWritableDatabase().execSQL("DELETE FROM care_table WHERE _id =?",new String[]{care_ids[w]});
                                    for (int i=0;i<100;i++){
                                        if(aa.remindid[i][0]==care_ids[w]){
                                            aa.cancelRemind(aa.remindid[i][1], care_main.this);
                                        }
                                    }

                                    startActivity(new Intent().setClass(care_main.this, care_main.class));
                                    care_main.this.finish();

                                  /*  for (int i = 0; i < mydata.size(); i++) {
                                        if (i != i * 4 && i != i * 4+1&& i != i * 4+2&& i != i * 4+3)
                                           mydata3[jj++]=mydata.get(i);
                                    }*/
                                }
                            }).setNegativeButton("取消", null).show();


                  /*  //获得选中项的HashMap对象
                    Toast.makeText(care_main.this, "点击了" + arg2, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(care_main.this, care_add.class);
                    intent.putExtra("id", userids[arg2]);
                    intent.setAction("rewrite");
                    startActivity(intent);*/
                }
            });
        }
        else if(getIntent().getAction()=="editeofonelianxiren") {
            tv_submit.setVisibility(View.VISIBLE);
            tv_submit.setText("删除");
            //批量删除
            useridsflag=0;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=? and fid=?", new String[]{userid,getIntent().getStringExtra("lianxirenid")});
            while (cursor.moveToNext()) {
                care_ids[useridsflag++]=new String (String.valueOf(cursor.getInt(0)));
                // uid text,type text,time text,time2 text,note text,fid text,fname text,fsex text,fphone text,state text
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("fname",cursor.getString(7));
                listItem.put("type",cursor.getString(2));
                listItem.put("time", cursor.getString(3));
                listItem.put("time2", cursor.getString(4));
                //listItem.put("remind", cursor.getString(5));
                //listItem.put("priorty", cursor.getString(8));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(care_main.this, listItems, R.layout.care_remind_item, new String[]{"fname","type","time", "time2"}, new int[]{R.id.care_name, R.id.care_type, R.id.care_time, R.id.care_time2});
            listView = (ListView) findViewById(R.id.care_list);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    w=arg2;
                    new AlertDialog.Builder(care_main.this)
                            .setTitle("提醒")
                            .setMessage("确定删除！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  Toast.makeText(care_main.this,"_id"+care_ids[w],Toast.LENGTH_SHORT).show();
                                    dbHelper.getWritableDatabase().execSQL("DELETE FROM care_table WHERE _id =?",new String[]{care_ids[w]});
                                    for (int i=0;i<100;i++){
                                        if(aa.remindid[i][0]==care_ids[w]){
                                            aa.cancelRemind(aa.remindid[i][1], care_main.this);
                                        }
                                    }
              if(getIntent().getAction()=="editeofonelianxiren"){
                    startActivity(new Intent().setClass(care_main.this, care_main.class).setAction("editeofonelianxiren").putExtra("lianxirenid", getIntent().getStringExtra("lianxirenid")));

              }
                                    startActivity(new Intent().setClass(care_main.this, care_main.class).setAction("editeofonelianxiren").putExtra("lianxirenid", getIntent().getStringExtra("lianxirenid")));

                                  /*  for (int i = 0; i < mydata.size(); i++) {
                                        if (i != i * 4 && i != i * 4+1&& i != i * 4+2&& i != i * 4+3)
                                           mydata3[jj++]=mydata.get(i);
                                    }*/
                                    care_main.this.finish();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            });
        }
        else if(getIntent().getAction()=="deleteofonelianxiren") {
            tv_submit.setVisibility(View.VISIBLE);
            tv_submit.setText("编辑");
            //批量删除
            useridsflag=0;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=? and fid=?", new String[]{userid,getIntent().getStringExtra("lianxirenid")});
            while (cursor.moveToNext()) {
                care_ids[useridsflag++]=new String (String.valueOf(cursor.getInt(0)));
                // uid text,type text,time text,time2 text,note text,fid text,fname text,fsex text,fphone text,state text
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("fname",cursor.getString(7));
                listItem.put("type",cursor.getString(2));
                listItem.put("time", cursor.getString(3));
                listItem.put("time2", cursor.getString(4));
                //listItem.put("remind", cursor.getString(5));
                //listItem.put("priorty", cursor.getString(8));
                listItems.add(listItem);
            }
                SimpleAdapter simpleAdapter = new SimpleAdapter(care_main.this, listItems, R.layout.care_remind_item, new String[]{"fname","type","time", "time2"}, new int[]{R.id.care_name, R.id.care_type, R.id.care_time, R.id.care_time2});
                listView = (ListView) findViewById(R.id.care_list);
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        //获得选中项的HashMap对象
                        // Toast.makeText(care_main.this, "点击了" + arg2, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(care_main.this, care_add.class);
                        intent.putExtra("id", care_ids[arg2]);
                        intent.setAction("rewrite");
                        startActivity(intent);
                    }
                });
        }
        else{
            //修改
            useridsflag=0;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=?", new String[]{userid});
            while (cursor.moveToNext()) {
                care_ids[useridsflag++]=new String (String.valueOf(cursor.getInt(0)));
                // uid text,type text,time text,time2 text,note text,fid text,fname text,fsex text,fphone text,state text
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("fname",cursor.getString(7));
                listItem.put("type",cursor.getString(2));
                listItem.put("time", cursor.getString(3));
                listItem.put("time2", cursor.getString(4));
                //listItem.put("remind", cursor.getString(5));
                //listItem.put("priorty", cursor.getString(8));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(care_main.this, listItems, R.layout.care_remind_item, new String[]{"fname","type","time", "time2"}, new int[]{R.id.care_name, R.id.care_type, R.id.care_time, R.id.care_time2});
            listView = (ListView) findViewById(R.id.care_list);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    w=arg2;
                    startActivity(new Intent().setClass(care_main.this,care_add.class).setAction("edite").putExtra("_id",care_ids[w]));
                }
            });



        }



        if(listItems.size()<1){
            listView.setVisibility(View.GONE);
            nothing=(TextView)findViewById(R.id.nothing);
            nothing.setVisibility(View.VISIBLE);
        }
        }
}
