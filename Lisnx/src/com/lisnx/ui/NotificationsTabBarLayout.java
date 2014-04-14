package com.lisnx.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lisnx.android.activity.R;

public class NotificationsTabBarLayout extends LinearLayout implements OnClickListener {
	private NotificationsTabButtonLayout currentSelection;
	private OnTabChangedListener tabChangeListener;
	private int background1 = R.drawable.bg_tab_button_notification;
	
	public NotificationsTabBarLayout(Context context) {
		super(context);
	}
	public NotificationsTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setOnTabChangedListener(OnTabChangedListener listener) {
		tabChangeListener = listener;
	}
	
	public boolean isSelected(NotificationsTabButtonLayout t) {
		if(t == currentSelection){
			t.setBackgroundResource(background1);
		}
		
		return t == currentSelection;
	}
	
	public synchronized void selectTab(NotificationsTabButtonLayout t) {
		if(t != currentSelection) {
			if(currentSelection != null)
				currentSelection.deSelect();
			currentSelection = t;
			currentSelection.select();
			
			if(t.getId()==R.id.friend_requests_tabbutton){ 
				ImageView friendReqImage=(ImageView) findViewById(R.id.friend_requests_image);
				friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_friend_request)));
				ImageView messageImage=(ImageView) findViewById(R.id.message_image);
				messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
			} else if(t.getId()==R.id.message_tabbutton){
				ImageView friendReqImage=(ImageView) findViewById(R.id.friend_requests_image);
				friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_friend_request)));
				ImageView messageImage=(ImageView) findViewById(R.id.message_image);
				messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
			}
			
			tabChangeListener.onTabChanged(currentSelection.getId());
		}
	}
	
	public void onClick(View v) {
		selectTab((NotificationsTabButtonLayout) v);
	}
	
	public interface OnTabChangedListener {
		public void onTabChanged(int tabId);
	}
}