package com.example.bmj.statistics_all;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;


public class BMJLXRStatics extends Activity {
    private String[] lianxiren = null;
    private String[] lianxirentime = null;
    private String[] lianxirencreater = null;

    private int[] header = null;
    private ImageView ib3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bmjlxrstatics);
        Intent intent = getIntent();
        Bundle b = this.getIntent().getExtras();

        lianxiren = b.getStringArray("lxr");
        lianxirentime=b.getStringArray("lxrtime");
        lianxirencreater=b.getStringArray("lxrcreater");

        ib3=(ImageView) findViewById(R.id.imageView11);
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (lianxiren != null) {
            //初始化的头像
            header = new int[lianxiren.length];
            for (int i = 0; i < lianxiren.length; i++)
                header[i] = R.drawable.bmjportrait;
            if(lianxirencreater==null) {
                lianxirencreater = new String[lianxiren.length];
                for(int i=0;i<lianxiren.length;i++)
                {
                    lianxirencreater[i]="我";
                }
            }

            List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < lianxiren.length; i++) {
                Map<String, Object> listitem = new HashMap<String, Object>();
                listitem.put("touxiang", header[i]);
                listitem.put("xingming", lianxiren[i]);
                listitem.put("xingmingtime", lianxirentime[i]);
                listitem.put("xingmingcreater",lianxirencreater[i]);


                listitems.add(listitem);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listitems, R.layout.bmj_simple_item,
                    new String[]{"touxiang", "xingming","xingmingtime","xingmingcreater"},
                    new int[]{R.id.bmjheader, R.id.bmjname,R.id.bmjtime,R.id.bmjcreater});

            ListView list = (ListView) findViewById(R.id.bmjlistview);
            list.setAdapter(simpleAdapter);

            Log.d("Test1", String.valueOf(lianxiren.length));

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
