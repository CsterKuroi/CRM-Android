package com.melnykov.fab.sample.kehu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.sample.R;

import com.melnykov.fab.sample.lianxiren.crm_addlianxiren;

import com.melnykov.fab.sample.shibie.crmmpw_recognize;
import com.melnykov.fab.sample.shibie.crmcapture;
import com.melnykov.fab.sample.tools.TextAppearanceUtil;

public class crm_detail_kehu extends ActionBarActivity {
    //获取客户名称
    String StringE;
    String kehuxinxi;
    private ListViewFragment list;
    private ScrollViewFragment scrolls;
    private FragmentManager fragmentManager;
    TextView jindu1,jindu2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_detail_kehu);
        Intent intent=getIntent();
        StringE = intent.getStringExtra("extra");
        kehuxinxi = intent.getStringExtra("kehuxinxi");
        Log.e(kehuxinxi,"看看");
        initActionBar();
    }
    @SuppressWarnings("deprecation")
    private void initActionBar() {

        fragmentManager = getFragmentManager();

        list = new ListViewFragment(StringE,kehuxinxi);
        scrolls = new ScrollViewFragment(StringE);
        jindu1 = (TextView) findViewById(R.id.jindu1);
        jindu2 = (TextView) findViewById(R.id.jindu2);

        TabHost tabhost =(TabHost) findViewById(R.id.tabhost);
        //调用 TabHost.setup()
        tabhost.setup();
        //创建Tab标签
        tabhost.addTab(tabhost.newTabSpec("one").setIndicator("客户").setContent(R.id.widget_layout_red));
        tabhost.addTab(tabhost.newTabSpec("two").setIndicator("联系人").setContent(R.id.widget_layout_yellow));

        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.tabcontent,scrolls);
        transaction.add(R.id.tabcontent,list);
        transaction.hide(scrolls);
        transaction.commit();

        TextAppearanceUtil.setTabWidgetTitle(this,tabhost.getTabWidget(), 15,R.color.gray_normal);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.e(tabId, tabId);

                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
                if(tabId=="one") {
                    transaction.show(list);
                    transaction.hide(scrolls);
                }
                else {
                    transaction.show(scrolls);
                    transaction.hide(list);
                }
                transaction.commit();
            }

        });

        final LinearLayout layout1 = (LinearLayout) findViewById(R.id.kehubutton);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.show(list);
                transaction.hide(scrolls);
                jindu1.setBackgroundColor(getResources().getColor(R.color.skyblue));
                jindu2.setBackgroundColor(getResources().getColor(R.color.grayvrm));
                transaction.commit();

            }
        });

        LinearLayout layout2 = (LinearLayout) findViewById(R.id.lianxirenbutton);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.show(scrolls);
                transaction.hide(list);
                jindu2.setBackgroundColor(getResources().getColor(R.color.skyblue));
                jindu1.setBackgroundColor(getResources().getColor(R.color.grayvrm));
                transaction.commit();
            }
        });

        ImageView add = (ImageView) findViewById(R.id.addlianxi);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new AlertDialog.Builder(crm_detail_kehu.this).setMessage(
                        "请选择添加"+StringE+"的联系人:").setPositiveButton("手动添加",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(crm_detail_kehu.this, crm_addlianxiren.class);
                                intent.putExtra("kehu",StringE);
                                startActivityForResult(intent, 0);
                                crm_detail_kehu.this.finish();

                            }
                        }).setNegativeButton("名片全能王识别", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // TODO Auto-generated method stub
                        Toast.makeText(getBaseContext(), "名片扫描。", Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent(crm_detail_kehu.this, crmmpw_recognize.class);
                        startActivityForResult(intent, 0);

                    }
                }).setNeutralButton("后台识别", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(crm_detail_kehu.this, crmcapture.class);
                        startActivityForResult(intent, 0);
                        // TODO Auto-generated method stub

                    }
                }).create();
                dialog.show();

            }
        });

        RelativeLayout kehu_back = (RelativeLayout) findViewById(R.id.iv_back);
        kehu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crm_detail_kehu.this.finish();

            }
        });

        final TextView  kehuname = (TextView) findViewById(R.id.kehu_text);
        kehuname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kehuname.setText(StringE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crm_main, menu);
        menu.getItem(0).setTitle(StringE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null)return;
        scrolls.update();
    }

}