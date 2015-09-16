package com.geminy.productshow.Activity;


import android.content.Intent;
import android.support.v7.app.ActionBar;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geminy.productshow.Fragment.GEMINY_CharacterUIFragment;
import com.geminy.productshow.Fragment.GEMINY_DefaultFragment;
import com.geminy.productshow.Fragment.GEMINY_ProductDetailFragment;
import com.geminy.productshow.Fragment.GEMINY_RelatedUIFragment;
import com.geminy.productshow.R;


/**
 * Created by Hatsune Miku on 2015/8/5.
 */
public class GEMINY_ProductDetailInfosActivity extends ActionBarActivity implements TabListener{
    private ViewPager mViewPager;
    public static final int MAX_TAB_NUM=3;
    private TabFragmentPagerAdapter mAdapter;
    public static final String PRODUCT_NAME="product_name";
    public static final String UID="UID";
    private String pro_name;
    private String uid;
    private static final String ManagerUID="100";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geminy_activity_product_detail);
        mViewPager=(ViewPager)this.findViewById(R.id.pager);
        pro_name=getIntent().getExtras().getString(GEMINY_DefaultFragment.PRO_NAME);
        uid=getIntent().getExtras().getString(GEMINY_DefaultFragment.UID);
        initView();
    }
    private void restoreActionBar(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(uid.equals(ManagerUID)) {
            getMenuInflater().inflate(R.menu.geminy_menu_pro_detail_more,menu);
        }
        else
            getMenuInflater().inflate(R.menu.geminy_menu_pro_detail,menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        if(item.getItemId()==R.id.stock_control){
            Intent intent=new Intent(GEMINY_ProductDetailInfosActivity.this,GEMINY_StockControlActivity.class);
            Bundle args=new Bundle();
            args.putString(PRODUCT_NAME,pro_name);
            intent.putExtras(args);
            startActivity(intent);



        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        final ActionBar mActionBar=getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mAdapter=new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for(int i=0;i<MAX_TAB_NUM;i++){
            ActionBar.Tab tab = mActionBar.newTab();
            tab.setText(mAdapter.getPageTitle(i)).setTabListener(GEMINY_ProductDetailInfosActivity.this);
            mActionBar.addTab(tab);
        }
    }
    public  class TabFragmentPagerAdapter extends FragmentPagerAdapter{
        public TabFragmentPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment ft=null;
            switch (position){
                case 0:
                    ft=new GEMINY_ProductDetailFragment();
                    break;
                case 1:
                    ft=new GEMINY_CharacterUIFragment();
                    break;
                case 2:
                    ft=new GEMINY_RelatedUIFragment();
                    break;
            }


            Bundle bundle=new Bundle();
            bundle.putString(PRODUCT_NAME, pro_name);
            bundle.putString(UID,uid);
            ft.setArguments(bundle);
            return ft;
        }

        @Override
        public int getCount() {
            return MAX_TAB_NUM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabTitle=new String();
            switch (position){
                case 0:
                    tabTitle=getString(R.string.ps_tab1);
                    break;
                case 1:
                    tabTitle=getString(R.string.ps_tab2);
                    break;
                case 2:
                    tabTitle=getString(R.string.ps_tab3);
                    break;
                default:
                    tabTitle=getString(R.string.ps_tab3);
            }
            return tabTitle;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}
