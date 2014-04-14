package com.lisnx.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import com.lisnx.android.activity.R;

public class TabButtonLayout extends RelativeLayout implements OnTouchListener{
	
	private TabBarLayout tabBar;
	private int background1 = R.drawable.bg_tab_button;
	private int background2 = R.drawable.bg_gray_tab_button;
	@SuppressWarnings("unused")
	private int textColorDefault;
	@SuppressWarnings("unused")
	private int textColorHighlighted;
	Resources res = getResources();
	public TabButtonLayout(Context context) {
		super(context);
	}
	public TabButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TabButtonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void registerTabBar(TabBarLayout rb) {
		tabBar = rb;
	}
	
	public void deSelect() {
		setBackgroundResource(background2);
	}

	public void select() {
		setBackgroundResource(background1);
	}
	
	private void onTouchChangeBackground(MotionEvent event) {
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if(!tabBar.isSelected(this)){
			onTouchChangeBackground(event);
		}
		return false;
	}
}