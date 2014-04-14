package com.lisnx.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import com.lisnx.android.activity.R;

public class LisnDetailTabButtonLayout extends RelativeLayout implements OnTouchListener{
	@SuppressWarnings("unused")
	private LisnDetailTabBarLayout tabBar;
	private int background1 = R.drawable.bg_tab_button_lisn_detail;
	private int background2 = R.drawable.bg_gray_tab_button_lisn_detail;
	@SuppressWarnings("unused")
	private int textColorDefault;
	@SuppressWarnings("unused")
	private int textColorHighlighted;
	Resources res = getResources();
	public LisnDetailTabButtonLayout(Context context) {
		super(context);
	}
	public LisnDetailTabButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public LisnDetailTabButtonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void registerTabBar(LisnDetailTabBarLayout rb) {
		tabBar = rb;
	}
	
	public void deSelect() {
		setBackgroundResource(background2);
	}

	public void select() {
		setBackgroundResource(background1);
	}
	
	@SuppressWarnings("unused")
	private void onTouchChangeBackground(MotionEvent event) {
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}