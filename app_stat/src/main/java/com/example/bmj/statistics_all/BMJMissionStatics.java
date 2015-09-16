package com.example.bmj.statistics_all;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BMJMissionStatics extends Activity {
    private String [] mission = null;  //接收方数组
    private String[] missionname = null;
    private String[] missonstatus = null;
    private String[] missoncreater = null;
    private int[] header = null;
    private ImageView ib3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bmjmission_statics);
        Intent intent = getIntent();
        Bundle b=this.getIntent().getExtras();
        missionname = b.getStringArray("missionname");
        missoncreater = b.getStringArray("missioncreater");
        missonstatus = b.getStringArray("missionstatus");
        if(missionname!=null)
        {
            header = new int[missionname.length];
            for (int i = 0; i < missionname.length; i++)
                header[i] = R.drawable.bmjtask;
            List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < missionname.length; i++) {
                Map<String, Object> listitem = new HashMap<String, Object>();
                listitem.put("touxiang", header[i]);
                listitem.put("name", missionname[i]);
                listitem.put("creater", missoncreater[i]);
                listitem.put("status",missonstatus[i]);

                listitems.add(listitem);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listitems, R.layout.bmj_mission_item,
                    new String[]{"touxiang", "name","creater","status"},
                    new int[]{R.id.bmjheadermission, R.id.bmjnamemission,R.id.bmjtimemission,R.id.bmjcreatermission});

            ListView list = (ListView) findViewById(R.id.bmjlistviewmission);
            list.setAdapter(simpleAdapter);

            Log.d("Test1", String.valueOf(missionname.length));
        }
        ib3=(ImageView) findViewById(R.id.bbbba);
        Log.e("ccc","ccc");
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ccccccc","cccccc");
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
