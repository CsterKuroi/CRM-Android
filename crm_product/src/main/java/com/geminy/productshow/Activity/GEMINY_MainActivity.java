package com.geminy.productshow.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Fragment.GEMINY_DefaultFragment;
import com.geminy.productshow.Fragment.GEMINY_NavigationDrawerFragment;
import com.geminy.productshow.Fragment.GEMINY_StarProductFragment;
import com.geminy.productshow.GEMINY_JSON;
import com.geminy.productshow.R;
import com.ricky.database.CenterDatabase;


public class GEMINY_MainActivity extends ActionBarActivity
        implements GEMINY_NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String uid="";

    public static final String UID="UID";
    public Handler mHandler;
    public static GEMINY_MainActivity mainActivity;

    public ProgressDialog dialog;

    @Override
    public void onPause(){
        mainActivity=null;
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CenterDatabase cd = new CenterDatabase(this, null);
        uid = cd.getUID();
        cd.close();
        mainActivity=this;
        if(isOpenNetwork()){
            new Thread(){
                @Override
                public void run() {
                    updateLocDataBase();
                }
            }.start();
            dialog= new ProgressDialog(GEMINY_MainActivity.mainActivity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
            dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
            dialog.setCanceledOnTouchOutside(true);// 设置在点击Dialog外是否取消Dialog进度条
            dialog.setMessage("产品列表加载中,请稍后...");
            // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
            dialog.setTitle("提示");
            // dismiss监听
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    Log.e("dismiss", "dialog dismissed");
                }
            });
            // 监听Key事件被传递给dialog
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.cancel();
                    }
                    return false;
                }
            });
            // 监听cancel事件
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    Log.e("dialogCancel","dialog cancel");
                }
            });
            dialog.show();
            Log.e("show", "dialog showed");
            mHandler=new Handler(){
                public void handleMessage(Message msg) {
                    onNavigationDrawerItemSelected(0);
                    dialog.dismiss();
                }
            };
        } else{
            Toast.makeText(GEMINY_MainActivity.this,"无法连接至服务器！",Toast.LENGTH_SHORT).show();
        }


        setContentView(R.layout.geminy_activity_main);
        GEMINY_NavigationDrawerFragment mGEMINYNavigationDrawerFragment = (GEMINY_NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mTitle = getTitle();
        mGEMINYNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position){
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GEMINY_StarProductFragment().newInstance(position + 1, 1,uid))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GEMINY_DefaultFragment().newInstance(position + 1, getString(R.string.ps_title_section1), uid))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GEMINY_DefaultFragment().newInstance(position + 1,getString(R.string.ps_title_section2),uid))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GEMINY_DefaultFragment().newInstance(position + 1,getString(R.string.ps_title_section3),uid))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GEMINY_DefaultFragment().newInstance(position + 1,getString(R.string.ps_title_section4),uid))
                        .commit();
                break;

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.ps_title_section0);
                break;
            case 2:
                mTitle = getString(R.string.ps_title_section1);
                break;
            case 3:
                mTitle = getString(R.string.ps_title_section2);
                break;
            case 4:
                mTitle = getString(R.string.ps_title_section3);
                break;
            case 5:
                mTitle = getString(R.string.ps_title_section4);
                break;
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(mTitle);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.geminy_main, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id==R.id.search){

            Intent intent=new Intent(GEMINY_MainActivity.this,GEMINY_SearchActivity.class);
            Bundle args=new Bundle();
            args.putString(UID, uid);
            intent.putExtras(args);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */

    private void updateLocDataBase(){
        GEMINY_ProductDAO GEMINYProductDAO =new GEMINY_ProductDAO(GEMINY_MainActivity.this);
        GEMINY_JSON geminy_json=new GEMINY_JSON(GEMINYProductDAO);
        geminy_json.linkJSON();
    }
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connManager.getActiveNetworkInfo()!=null)&&(connManager.getActiveNetworkInfo().isAvailable());
    }
}
