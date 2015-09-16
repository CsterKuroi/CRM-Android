package com.example.bmj.statistics_all;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BMJHTStatics extends Activity {
    private String [] hetong = null;  //接收方数组
    private String [] htmoney = null;
    private String [] htdate=null;
    private String [] httarget=null;
    private String [] htcreater=null;
    private int[] header;
    private ImageView ib3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bmjhtstatics);
        Intent intent = getIntent();
        Bundle b=this.getIntent().getExtras();
        hetong = b.getStringArray("hetong");
        htmoney=b.getStringArray("htmoney");
        htdate=b.getStringArray("htdate");
        httarget=b.getStringArray("httarget");
        htcreater=b.getStringArray("htcreater");
        ib3=(ImageView) findViewById(R.id.imageView11);
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(hetong!=null)
        {
            header = new int[hetong.length];

            for (int i = 0; i < hetong.length; i++)
                header[i] = R.drawable.bmjbfpc;
            if(htcreater==null){ htcreater = new String[hetong.length];
            for(int i=0;i<htcreater.length;i++)
                htcreater[i]="我";
            }
            List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < htcreater.length; i++) {
                Map<String, Object> listitem = new HashMap<String, Object>();
                listitem.put("touxiang", header[i]);
                listitem.put("hetong", hetong[i]);//合同
                listitem.put("date",htdate[i]);//日期
                listitem.put("money",htmoney[i]);//金额
                listitem.put("target",httarget[i]);//客户
                listitem.put("creater",htcreater[i]);//创建人
//                Log.d("Test",bfcreater[i]+"哈哈哈哈哈哈哈哈");
                listitems.add(listitem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listitems, R.layout.bmjhetong,
                    new String[]{"touxiang", "hetong","date","money","target","creater"},
                    new int[]{R.id.bmjhtheader, R.id.bmjhtname,R.id.bmjhtdate,R.id.bmjhtmoney,R.id.bmjhttarget,R.id.bmjhtcreater});

            ListView list2 = (ListView) findViewById(R.id.bmjlistviewht);
            list2.setAdapter(simpleAdapter);

            Log.d("Test1", String.valueOf(hetong.length));

        }

    }
}
