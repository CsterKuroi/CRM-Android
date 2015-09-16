package com.geminy.productshow;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hatsune Miku on 2015/8/7.
 */
public class GEMINY_ImgScroll extends ViewPager{
    private Activity mActivity;
    private List<View> mListViews;
    private int mScrollTime = 0;
    private Timer timer;
    private int oldIndex=0;
    private int curIndex=0;

    public GEMINY_ImgScroll(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public void start(Activity activity,List<View> imgList,int scrollTime,LinearLayout ovalLayout,int ovalLayoutId,int ovalLayoutItemId,int focusedId,int normalId){
        mActivity=activity;
        mListViews=imgList;
        mScrollTime=scrollTime;

        setOvalLayout(ovalLayout, ovalLayoutId, ovalLayoutItemId, focusedId, normalId);
        this.setAdapter(new MyPagerAdapter());
        if (mListViews.size()>1){
            this.setCurrentItem((Integer.MAX_VALUE/2)-(Integer.MAX_VALUE/2)%mListViews.size());
        }
    }
    private void setOvalLayout(final LinearLayout ovalLayout,int ovalLayoutId,final int ovalLayoutItemId,final int focusedId,final int normalId){
        if(ovalLayout!=null){
            LayoutInflater inflater=LayoutInflater.from(mActivity);
            if(mListViews.size()==0){
                return;
            }
            for(int i=0; i<mListViews.size();i++){
                ovalLayout.addView(inflater.inflate(ovalLayoutId,null));
            }
            ovalLayout.getChildAt(0).findViewById(ovalLayoutItemId).setBackgroundResource(focusedId);
            this.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    curIndex = position % mListViews.size();
                    ovalLayout.getChildAt(oldIndex).findViewById(ovalLayoutItemId).setBackgroundResource(normalId);
                    ovalLayout.getChildAt(curIndex).findViewById(ovalLayoutItemId).setBackgroundResource(focusedId);
                    oldIndex = curIndex;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
    public int getCurIndex(){
        return curIndex;
    }
    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    public void startTimer(){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GEMINY_ImgScroll.this.setCurrentItem(GEMINY_ImgScroll.this.getCurrentItem()+1);
                    }
                });
            }
        },mScrollTime,mScrollTime);
    }

    private class MyPagerAdapter extends PagerAdapter{
        @Override
        public void finishUpdate(View container) {

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mListViews.size()==1){
                return 1;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            if(mListViews.size()==0){
                return null;
            }
            if(((ViewPager)container).getChildCount()==mListViews.size()){
                ((ViewPager)container).removeView(mListViews.get(position%mListViews.size()));
            }
            ((ViewPager)container).addView(mListViews.get(position%mListViews.size()),0);
            return mListViews.get(position%mListViews.size());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if(view==object)
                return true;
            return false;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {

        }

        @Override
        public void destroyItem(View container, int position, Object object) {

        }
    }
}
