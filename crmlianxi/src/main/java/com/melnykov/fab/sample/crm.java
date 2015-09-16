package com.melnykov.fab.sample;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.widget.LinearLayout;

import com.melnykov.fab.sample.kehu.crm_kehu;
import com.melnykov.fab.sample.lianxiren.crm_lianxiren;
import com.melnykov.fab.sample.tools.IMApplication;

public class crm extends ActionBarActivity implements View.OnTouchListener,View.OnFocusChangeListener {

    public static File file;
    LinearLayout kehu;
    LinearLayout lianxiren;
    LinearLayout crm;
    LinearLayout kehuchachong;
    LinearLayout soumingpian;
    LinearLayout jihui;
    LinearLayout hetong;
    LinearLayout shichanghuodong;
    LinearLayout xiansuo;
    LinearLayout shezhi;

    //copy file
    public  void copyToSD(Context context) {
        InputStream is = null;
        FileOutputStream fos = null;

        String authorid = IMApplication.getUserid(this);

        try {
            String path = android.os.Environment.getExternalStorageDirectory().getPath();
            path = path + "/mingpian/";
            String dbPathAndName = path + "haha.jpg";
            file = new File(path);
            if (file.exists() == false)
            {
                file.mkdir();
            }
            File dbFile = new File(dbPathAndName);
            if (!dbFile.exists()) {
                is = context.getResources().openRawResource(R.drawable.hi);
                fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8 * 1024];// 8K
                while (is.read(buffer) > 0)// >
                {
                    fos.write(buffer);
                }
            }

        } catch (Exception e) {

        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_index);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.top_bar_back);

        crm = (LinearLayout) findViewById(R.id.layout0);
        kehu = (LinearLayout) findViewById(R.id.layout1);
        kehuchachong= (LinearLayout) findViewById(R.id.layout2);
        lianxiren = (LinearLayout) findViewById(R.id.layout3);
        soumingpian= (LinearLayout) findViewById(R.id.layout4);
        jihui= (LinearLayout) findViewById(R.id.layoutb1);
        hetong= (LinearLayout) findViewById(R.id.layoutb2);
        shichanghuodong= (LinearLayout) findViewById(R.id.layoutb3);
        xiansuo= (LinearLayout) findViewById(R.id.layoutb4);
        shezhi= (LinearLayout) findViewById(R.id.layoutb5);

        crm.setOnTouchListener(this);
        kehu.setOnTouchListener(this);
        kehuchachong.setOnTouchListener(this);
        lianxiren.setOnTouchListener(this);
        soumingpian.setOnTouchListener(this);
        jihui.setOnTouchListener(this);
        hetong.setOnTouchListener(this);
        shichanghuodong.setOnTouchListener(this);
        xiansuo.setOnTouchListener(this);
        shezhi.setOnTouchListener(this);

        copyToSD(this);
    }
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.layout0) {
                    crm.setBackgroundColor(Color.LTGRAY);
                } else if (v.getId() == R.id.layout1) {
                    kehu.setBackgroundColor(Color.LTGRAY);
                } else if (v.getId() == R.id.layout2) {
                    kehuchachong.setBackgroundColor(Color.LTGRAY);
                } else if (v.getId() == R.id.layout3) {
                    lianxiren.setBackgroundColor(Color.LTGRAY);
                }
                else if (v.getId() == R.id.layout4) {
                    soumingpian.setBackgroundColor(Color.LTGRAY);
                } else if (v.getId() == R.id.layoutb1) {
                    jihui.setBackgroundColor(Color.LTGRAY);
                }
                else if (v.getId() == R.id.layoutb2) {
                    hetong.setBackgroundColor(Color.LTGRAY);
                }
                else if (v.getId() == R.id.layoutb3) {
                    shichanghuodong.setBackgroundColor(Color.LTGRAY);
                }
                else if (v.getId() == R.id.layoutb4) {
                    xiansuo.setBackgroundColor(Color.LTGRAY);
                }
                else if (v.getId() == R.id.layoutb5) {
                    shezhi.setBackgroundColor(Color.LTGRAY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.layout1) {
                    startActivity(new Intent(crm.this, crm_kehu.class));
                    //动画跳转
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else if (v.getId() == R.id.layout3) {
                    startActivity(new Intent(crm.this, crm_lianxiren.class));
                    //动画跳转
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    break;
                }
            case MotionEvent.ACTION_CANCEL:
                if (v.getId() == R.id.layout0) {
                    crm.setBackgroundColor(Color.WHITE);
                } else if (v.getId() == R.id.layout1) {
                    kehu.setBackgroundColor(Color.WHITE);
                } else if (v.getId() == R.id.layout2) {
                    kehuchachong.setBackgroundColor(Color.WHITE);
                } else if (v.getId() == R.id.layout3) {
                    lianxiren.setBackgroundColor(Color.WHITE);
                }
                else if (v.getId() == R.id.layout4) {
                    soumingpian.setBackgroundColor(Color.WHITE);
                } else if (v.getId() == R.id.layoutb1) {
                    jihui.setBackgroundColor(Color.WHITE);
                }
                else if (v.getId() == R.id.layoutb2) {
                    hetong.setBackgroundColor(Color.WHITE);
                }
                else if (v.getId() == R.id.layoutb3) {
                    shichanghuodong.setBackgroundColor(Color.WHITE);
                }
                else if (v.getId() == R.id.layoutb4) {
                    xiansuo.setBackgroundColor(Color.WHITE);
                }
                else if (v.getId() == R.id.layoutb5) {
                    shezhi.setBackgroundColor(Color.WHITE);
                }
                break;
            default:return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crm_menu_crm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v.getId()==R.id.layout1){
            if(!hasFocus) {
                kehu.setBackgroundColor(Color.WHITE);
            }
        }
        else if(v.getId()==R.id.layout3){
            if(!hasFocus) lianxiren.setBackgroundColor(Color.WHITE);
        }
    }
}

