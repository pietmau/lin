package com.lisnx.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lisnx.android.activity.R;
import com.lisnx.android.activity.MessageBoardActivity;

public class LisnDetailTabBarLayout extends LinearLayout implements OnClickListener {
	private LisnDetailTabButtonLayout currentSelection;
	private OnTabChangedListener tabChangeListener;
	private int background1 = R.drawable.bg_tab_button_lisn_detail;
	
	public LisnDetailTabBarLayout(Context context) {
		super(context);
	}
	public LisnDetailTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setOnTabChangedListener(OnTabChangedListener listener) {
		tabChangeListener = listener;
	}
	
	public boolean isSelected(LisnDetailTabButtonLayout t) {
		if(t == currentSelection){
			t.setBackgroundResource(background1);
		}
		
		return t == currentSelection;
	}
	
	public synchronized void selectTab(LisnDetailTabButtonLayout t) {
		if(t != currentSelection) {
			if(currentSelection != null)
				currentSelection.deSelect();
			currentSelection = t;
			currentSelection.select();
			
			if(t.getId()==R.id.lisner_tabbutton){ 
				cancelTimer();
				ImageView friendReqImage=(ImageView) findViewById(R.id.lisner_image);
				friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_lisners2)));
				ImageView messageImage=(ImageView) findViewById(R.id.message_image);
				messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
				
			} else if(t.getId()==R.id.message_tabbutton){
				ImageView friendReqImage=(ImageView) findViewById(R.id.lisner_image);
				friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_lisners2)));
				ImageView messageImage=(ImageView) findViewById(R.id.message_image);
				messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
				
			}
			
			tabChangeListener.onTabChanged(currentSelection.getId());
		}
	}
	
	public void onClick(View v) {
		selectTab((LisnDetailTabButtonLayout) v);
	}
	
	public interface OnTabChangedListener {
		public void onTabChanged(int tabId);
	}
	
	public void cancelTimer(){
		try{
			if(MessageBoardActivity.timer != null){
				MessageBoardActivity.timer.cancel();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}