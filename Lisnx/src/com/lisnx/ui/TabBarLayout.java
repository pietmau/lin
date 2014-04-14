package com.lisnx.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lisnx.android.activity.R;

public class TabBarLayout extends LinearLayout implements OnClickListener {
	private TabButtonLayout currentSelection;
	private OnTabChangedListener tabChangeListener;
	private int background1 = R.drawable.bg_tab_button;
	
	public TabBarLayout(Context context) {
		super(context);
	}
	public TabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setOnTabChangedListener(OnTabChangedListener listener) {
		tabChangeListener = listener;
	}
	
	public boolean isSelected(TabButtonLayout t) {
		if(t == currentSelection){
			t.setBackgroundResource(background1);
		}
		return t == currentSelection;
	}
	
	public synchronized void selectTab(TabButtonLayout t) {
		if(t != currentSelection) {
			if(currentSelection != null)
				currentSelection.deSelect();
			currentSelection = t;
			currentSelection.select();
			
			if(t.getId()==R.id.now_lisn_tabbutton){ 
				ImageView nowLisnImage=(ImageView) findViewById(R.id.now_lisn_image);
				nowLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_now_lisn)));
				ImageView createLisnImage=(ImageView) findViewById(R.id.create_lisn_image);
				createLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_create_lisn_gray)));
				ImageView pastLisnImage=(ImageView) findViewById(R.id.past_lisn_image);
				pastLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_past_lisn_gray)));
			} else if(t.getId()==R.id.create_lisn_tabbutton){
				ImageView createLisnImage=(ImageView) findViewById(R.id.create_lisn_image);
				createLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_create_lisn)));
				ImageView nowLisnImage=(ImageView) findViewById(R.id.now_lisn_image);
				nowLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_now_lisn_gray)));
				ImageView pastLisnImage=(ImageView) findViewById(R.id.past_lisn_image);
				pastLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_past_lisn_gray)));
			} else if(t.getId()==R.id.past_lisn_tabbutton){
				ImageView pastLisnImage=(ImageView) findViewById(R.id.past_lisn_image);
				pastLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_past_lisn)));
				ImageView nowLisnImage=(ImageView) findViewById(R.id.now_lisn_image);
				nowLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_now_lisn_gray)));
				ImageView createLisnImage=(ImageView) findViewById(R.id.create_lisn_image);
				createLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_create_lisn_gray)));
			} 
			
			tabChangeListener.onTabChanged(currentSelection.getId());
		}
	}
	
	public void onClick(View v) {
		selectTab((TabButtonLayout) v);
	}
	
	public interface OnTabChangedListener {
		public void onTabChanged(int tabId);
	}
}