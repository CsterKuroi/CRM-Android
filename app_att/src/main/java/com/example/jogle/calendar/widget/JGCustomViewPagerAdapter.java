package com.example.jogle.calendar.widget;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class JGCustomViewPagerAdapter<V extends View> extends PagerAdapter {
	
	private V[] views;

	
	public JGCustomViewPagerAdapter(V[] views) {
		super();
		this.views = views;
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		if (((ViewPager) arg0).getChildCount() == views.length) {
			((ViewPager) arg0).removeView(views[arg1 % views.length]);
		}
		((ViewPager) arg0).addView(views[arg1 % views.length], 0);

		return views[arg1 % views.length];
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startUpdate(View arg0) {
	}

	
	public V[] getAllItems() {
		return views;
	}
}
