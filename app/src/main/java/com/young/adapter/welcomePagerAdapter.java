package com.young.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class welcomePagerAdapter extends PagerAdapter {

	private Context ctx;
	private List<View> views;
	
	public welcomePagerAdapter(Context ctx, List<View> views){
		
		this.ctx=ctx;
		this.views=views;
		
		
	}
	
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		
		((ViewPager) container).removeView(views.get(position));
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		
		((ViewPager) container).addView(views.get(position));
		
		return views.get(position);
		
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		
		return (arg0==arg1);
	}


}
