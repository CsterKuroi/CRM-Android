package com.code.bmj.groupnotifycation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupNotificationMainActivity extends Activity {
    public static final int REQUSETOK2 = 2;
    private ImageView iv_create;
    private ImageView iv_back;
    private   SQLiteDatabase db          = null;
    private ListView lv_receive;
    private ListView lv_my;
    public SimpleAdapter simpleAdapter1,simpleAdapter;
    public static GroupNotificationMainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_group_notification_main);
        lv_receive = (ListView) findViewById(R.id.mynotification_list);
        lv_my = (ListView) findViewById(R.id.receivenotification_list);

        initView();

    }

    public void onPause(){
        mainActivity=null;
        super.onPause();
    }
    @Override
    protected void onRestart() {
        db=(new N_LocalDataBase(getApplicationContext(),null)).getDataBase();
        Cursor cursor = db.rawQuery("select * from group_notification where type='receive' order by cast(create_time as bigint) desc",null);
        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            String old_create_time = cursor.getString(cursor.getColumnIndex("receive_time"));
            String creatorName = cursor.getString(cursor.getColumnIndex("creatorName"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String _id = cursor.getString(cursor.getColumnIndex("_id"));
            String if_read = cursor.getString(cursor.getColumnIndex("read"));
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", creatorName);
            item.put("title", title);
            item.put("content",content);
            item.put("_id",_id);
            if(if_read==null||if_read.equals("false"))
                item.put("image",R.drawable.notificationfalse);
            else item.put("image",R.drawable.notificationtrue);
            item.put("create_time", NotificationDetailActivity.gethowlong(old_create_time));
            listitems.add(item);
        }
        simpleAdapter =  new SimpleAdapter(this,listitems,R.layout.receivenotification,
                new String[] {"image","_id","name","title","content","create_time"}
                ,new int[]{R.id.notification_ifread,R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time});
//            simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.receivenotification,cursor,
//                    new String[] {"_id","creatorName","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time} ,0);

        Cursor cursor1 = db.rawQuery("select * from group_notification where type='send' order by cast(create_time as bigint) desc",null);

        List<Map<String, Object>> listitems1 = new ArrayList<Map<String, Object>>();
        for(cursor1.moveToFirst();!cursor1.isAfterLast();cursor1.moveToNext())
        {
            String old_create_time1 = cursor1.getString(cursor1.getColumnIndex("create_time"));
            String title1 = cursor1.getString(cursor1.getColumnIndex("title"));
            String content1 = cursor1.getString(cursor1.getColumnIndex("content"));
            String _id1 = cursor1.getString(cursor1.getColumnIndex("_id"));
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title1", title1);
            item.put("content1",content1);
            item.put("_id1",_id1);
            item.put("create_time1",NotificationDetailActivity.gethowlong(old_create_time1));
            listitems1.add(item);
        }
        simpleAdapter1 = new SimpleAdapter(this,listitems1,R.layout.mysend_notification,
                new String[] {"_id1","title1","content1","create_time1"}
                ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2});

        lv_my.setAdapter(simpleAdapter);
        lv_receive.setAdapter(simpleAdapter1);

//            simpleCursorAdapter1 = new SimpleCursorAdapter(this,R.layout.mysend_notification,cursor1,
//                    new String[] {"_id","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2} ,0);
//
//            lv_my.setAdapter(simpleCursorAdapter);
//            lv_receive.setAdapter(simpleCursorAdapter1);
        simpleAdapter.notifyDataSetChanged();
        simpleAdapter1.notifyDataSetChanged();

        super.onRestart();
    }


    private void initView() {
        iv_create = (ImageView) findViewById(R.id.imageView_create_new);
        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                Bundle b = new Bundle();

                intent.setClass(GroupNotificationMainActivity.this, CreateNotificationActivity.class);
                startActivityForResult(intent, REQUSETOK2);
            }
        });

        iv_back = (ImageView) findViewById(R.id.imageView_notificationback);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TabHost tabs = (TabHost) findViewById(R.id.overview_tabs);
        tabs.setup();
        tabs.addTab(tabs.newTabSpec("tab1").setIndicator("我收到的").setContent(R.id.tab1));
        TextView tabTitle1 = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabTitle1.setTextSize(16);
        tabs.addTab(tabs.newTabSpec("tab2").setIndicator("我发出的").setContent(R.id.tab2));
        ((TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setTextSize(16);

        db=(new N_LocalDataBase(getApplicationContext(),null)).getDataBase();
        Cursor cursor = db.rawQuery("select * from group_notification where type='receive' order by cast(create_time as bigint) desc",null);
        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            String old_create_time = cursor.getString(cursor.getColumnIndex("receive_time"));
            String creatorName = cursor.getString(cursor.getColumnIndex("creatorName"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String _id = cursor.getString(cursor.getColumnIndex("_id"));
            String if_read = cursor.getString(cursor.getColumnIndex("read"));
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", creatorName);
            item.put("title", title);
            item.put("content",content);
            item.put("_id",_id);
            if(if_read==null||if_read.equals("false"))
                item.put("image",R.drawable.notificationfalse);
            else item.put("image",R.drawable.notificationtrue);
            item.put("create_time", NotificationDetailActivity.gethowlong(old_create_time));
            listitems.add(item);
        }
        simpleAdapter =  new SimpleAdapter(this,listitems,R.layout.receivenotification,
                new String[] {"image","_id","name","title","content","create_time"}
                ,new int[]{R.id.notification_ifread,R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time});
//            simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.receivenotification,cursor,
//                    new String[] {"_id","creatorName","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time} ,0);

        Cursor cursor1 = db.rawQuery("select * from group_notification where type='send' order by cast(create_time as bigint) desc",null);

        List<Map<String, Object>> listitems1 = new ArrayList<Map<String, Object>>();
        for(cursor1.moveToFirst();!cursor1.isAfterLast();cursor1.moveToNext())
        {
            String old_create_time1 = cursor1.getString(cursor1.getColumnIndex("create_time"));
            String title1 = cursor1.getString(cursor1.getColumnIndex("title"));
            String content1 = cursor1.getString(cursor1.getColumnIndex("content"));
            String _id1 = cursor1.getString(cursor1.getColumnIndex("_id"));
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title1", title1);
            item.put("content1",content1);
            item.put("_id1",_id1);
            item.put("create_time1",NotificationDetailActivity.gethowlong(old_create_time1));
            listitems1.add(item);
        }
        simpleAdapter1 = new SimpleAdapter(this,listitems1,R.layout.mysend_notification,
                new String[] {"_id1","title1","content1","create_time1"}
                ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2});

        lv_my.setAdapter(simpleAdapter);
        lv_receive.setAdapter(simpleAdapter1);

//            simpleCursorAdapter1 = new SimpleCursorAdapter(this,R.layout.mysend_notification,cursor1,
//                    new String[] {"_id","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2} ,0);
//
//            lv_my.setAdapter(simpleCursorAdapter);
//            lv_receive.setAdapter(simpleCursorAdapter1);
        simpleAdapter.notifyDataSetChanged();
        simpleAdapter1.notifyDataSetChanged();

        lv_my.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                TextView id = (TextView) parent.getChildAt(position - parent.getFirstVisiblePosition()).findViewById(R.id.id_msg_item_receive);


                SQLiteDatabase db;
                db=(new N_LocalDataBase(getApplicationContext(),null)).getDataBase();
                Cursor c=db.rawQuery("select * from group_notification where _id='"+id.getText()+"'",null);
                if(c.getCount()>0) {
                    c.moveToFirst();
                    String sever_id  = c.getString(c.getColumnIndex("server_id"));
                    String joiner_id = c.getString(c.getColumnIndex("joinerID"));
                    String[] split_id = joiner_id.split(",");

                c.close();
                Intent intent = new Intent();
                intent.putExtra("item_id", id.getText());
                //intent.putExtra("severid",sever_id);
                intent.setClass(GroupNotificationMainActivity.this, NotificationDetailActivity.class);
                startActivity(intent);

              //  Toast.makeText(getApplicationContext(), id.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        lv_receive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                TextView id = (TextView) parent.getChildAt(position - parent.getFirstVisiblePosition()).findViewById(R.id.id_msg_item_send);
                Intent intent = new Intent();
                //db.execSQL("update group_notification set read='true' where _id='"+id.getText()+"'");
                intent.putExtra("item_id",id.getText());
                intent.setClass(GroupNotificationMainActivity.this, NotificationDetailActivity.class);
                startActivity(intent);

               // Toast.makeText(getApplicationContext(), id.getText(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GroupNotificationMainActivity.REQUSETOK2 && resultCode == RESULT_OK) {
            db=(new N_LocalDataBase(getApplicationContext(),null)).getDataBase();
            Cursor cursor = db.rawQuery("select * from group_notification where type='receive' order by cast(create_time as bigint) desc",null);
            List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                String old_create_time = cursor.getString(cursor.getColumnIndex("create_time"));
                String creatorName = cursor.getString(cursor.getColumnIndex("creatorName"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String _id = cursor.getString(cursor.getColumnIndex("_id"));
                String ifread = cursor.getString(cursor.getColumnIndex("read"));

                Map<String, Object> item = new HashMap<String, Object>();

                item.put("name", creatorName);
                item.put("title", title);
                item.put("content",content);
                item.put("_id",_id);
                Log.d("TAG", old_create_time);
                item.put("create_time", NotificationDetailActivity.gethowlong(old_create_time));
                Log.d("TAG", NotificationDetailActivity.gethowlong(old_create_time));
                listitems.add(item);
            }
            simpleAdapter =  new SimpleAdapter(this,listitems,R.layout.receivenotification,
                    new String[] {"_id","creatorName","title","content","create_time"}
                    ,new int[]{R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time});
//            simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.receivenotification,cursor,
//                    new String[] {"_id","creatorName","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_receive,R.id.notifation_creater1,R.id.notification_name1,R.id.notification_content1,R.id.bmjnotification_time} ,0);

            Cursor cursor1 = db.rawQuery("select * from group_notification where type='send' order by cast(create_time as bigint) desc",null);

            List<Map<String, Object>> listitems1 = new ArrayList<Map<String, Object>>();
            for(cursor1.moveToFirst();!cursor1.isAfterLast();cursor1.moveToNext())
            {
                String old_create_time1 = cursor1.getString(cursor1.getColumnIndex("create_time"));
                String title1 = cursor1.getString(cursor1.getColumnIndex("title"));
                String content1 = cursor1.getString(cursor1.getColumnIndex("content"));
                String _id1 = cursor1.getString(cursor1.getColumnIndex("_id"));
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("title1", title1);
                item.put("content1",content1);
                item.put("_id1",_id1);
                item.put("create_time1",NotificationDetailActivity.gethowlong(old_create_time1));
                listitems1.add(item);
            }
            simpleAdapter1 = new SimpleAdapter(this,listitems1,R.layout.mysend_notification,
                    new String[] {"_id1","title1","content1","create_time1"}
                    ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2});

            lv_my.setAdapter(simpleAdapter);
            lv_receive.setAdapter(simpleAdapter1);

//            simpleCursorAdapter1 = new SimpleCursorAdapter(this,R.layout.mysend_notification,cursor1,
//                    new String[] {"_id","title","content","create_time"}
//                    ,new int[]{R.id.id_msg_item_send,R.id.notification_name2,R.id.notification_content,R.id.bmjnotification_time2} ,0);
//
//            lv_my.setAdapter(simpleCursorAdapter);
//            lv_receive.setAdapter(simpleCursorAdapter1);
            simpleAdapter.notifyDataSetChanged();
            simpleAdapter1.notifyDataSetChanged();
        }
    }

}
