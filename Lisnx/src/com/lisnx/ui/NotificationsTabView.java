package com.lisnx.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.android.activity.MenuActivity;
import com.lisnx.android.activity.MessageNotificationActivity;
import com.lisnx.android.activity.NotificationsActivity;
import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.android.activity.PeopleNearByActivity;
import com.lisnx.android.activity.ProfileActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.UserInfo;
import com.lisnx.util.Constants;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class NotificationsTabView extends TabActivity implements NotificationsTabBarLayout.OnTabChangedListener,OnClickListener{
	
	private NotificationsTabBarLayout tabBar;
	private NotificationsTabButtonLayout friendReqTabButton;
	private NotificationsTabButtonLayout messageTabButton;
	
	private TabHost tabHost;
	private ProgressDialog pd;
	//private TextView nameText;
	//private ImageView userImage;
	ImageView backIcon;
	public String uid = null;
	public String lisnId = null;
	private String RSVP=null;
	
	public static TextView friendRequestCount;
	public static TextView friendRequestsCountNotification;
	public static TextView msgCount;
	ImageView peopleNearByIcon ;
	LayoutInflater layoutInflater; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		lisnId = extras.getString("lisnId");
        		RSVP=extras.getString("RSVP");
        	}
        }
        if(Constants.CALLING_SCREEN_OTHER_PROFILE.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)||
        		Constants.CALLING_SCREEN_COMMON_LISN.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)||
        		Constants.CALLING_SCREEN_COMMON_FRIEND.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		uid = extras.getString("uid");
        	}
        }
	    
	    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
		setContentView(R.layout.notifications_tab_view);
		layoutInflater = this.getLayoutInflater();
		
		ImageView friendReqImage=(ImageView) findViewById(R.id.friend_requests_image);
		friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_friend_request)));
		ImageView messageImage=(ImageView) findViewById(R.id.message_image);
		messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
		
		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
	    peopleNearByIcon.setOnClickListener(this);
	    backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        /*nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);*/
        
        friendRequestCount=(TextView) findViewById(R.id.notificationBoxTextNotifications);
        friendRequestsCountNotification=(TextView) findViewById(R.id.requestCount);
        msgCount=(TextView) findViewById(R.id.messageCount);
        
        try{
        	new ProcessUITask().execute();
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        performInitialWork();
	}
	
	public void performInitialWork(){
		try{
			if(NowLisnActivity.peopleCount != null && !("0".equalsIgnoreCase(NowLisnActivity.peopleCount))){
				TextView peopleNearByCount=(TextView) findViewById(R.id.notificationBoxTextPeople);
				peopleNearByCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        switch(displayMetrics.densityDpi){ 
		        	case DisplayMetrics.DENSITY_LOW:
		        		peopleNearByCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		break; 
		        }
				peopleNearByCount.setText(NowLisnActivity.peopleCount);
			}
        } catch(Exception e){
        	e.printStackTrace();
        }
		
		/*if((NowLisnActivity.permanentName == null) || 
    			((NowLisnActivity.permanentImage == null) 
    					&& (("true").equalsIgnoreCase(NowLisnActivity.permanentIsImage)))){
        	try{
    			new UserInfo(getApplicationContext(),userImage,nameText, layoutInflater).getProfileData();
        	} catch(Exception e){
                e.printStackTrace();
            }
    	} else{
    		try{
    			nameText.setText(NowLisnActivity.permanentName);
    			if(NowLisnActivity.permanentImage != null){
    				userImage.setImageBitmap(NowLisnActivity.permanentImage);
    			}
    		} catch(Exception e){
            	e.printStackTrace();
            }
    	}*/
	}
	
	public void onClick(View view){
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		}
	}
	
	public void navigateMenuScreen(View view){
		try	{   		
	    	Intent menuIntent = new Intent(view.getContext(), MenuActivity.class);		
	    	startActivityForResult(menuIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);	
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	private void findViews() {
		tabBar = (NotificationsTabBarLayout) findViewById(R.id.maintabbar);
		friendReqTabButton = (NotificationsTabButtonLayout) findViewById(R.id.friend_requests_tabbutton);
		messageTabButton = (NotificationsTabButtonLayout) findViewById(R.id.message_tabbutton);
	}
	
	private void setListeners() {
		tabBar.setOnTabChangedListener(this);
		friendReqTabButton.setOnTouchListener(friendReqTabButton);
		friendReqTabButton.setOnClickListener(tabBar);
		messageTabButton.setOnTouchListener(messageTabButton);
		messageTabButton.setOnClickListener(tabBar);
	}
	
	private void setupTabBar() {
		friendReqTabButton.registerTabBar(tabBar);
		messageTabButton.registerTabBar(tabBar);
		tabBar.selectTab(messageTabButton);
	}

	public void onTabChanged(int tabId) {
		switch (tabId) {
        
        case R.id.message_tabbutton:
        	tabHost.setCurrentTab(0);
            break;
        case R.id.friend_requests_tabbutton:
            tabHost.setCurrentTab(1);
            break;
        }
	}
	
	public void launchActivity(int activityId) {
		tabHost.setCurrentTab(activityId);
	}
	
	@SuppressWarnings("unused")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}else if(msg.what == 2){
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 3){
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 4){
				CustomToast.showCustomToast(getApplicationContext(), Constants.DATABASE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 5){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_IGNORED_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 6){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_ACCEPT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 7){
				CustomToast.showCustomToast(getApplicationContext(), Constants.NO_NOTIFICATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 11){
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 12){
				//CustomToast.showCustomToast(getApplicationContext(), Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
		}
	};
	
	private class ProcessUITask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
        	pd = ProgressDialog.show(NotificationsTabView.this, null, NotificationsTabView.this.getResources().getString(R.string.lodingMessage), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
        	runOnUiThread(new Runnable() {
                public void run() {
                	tabHost = getTabHost();   
            	    TabHost.TabSpec spec;
            	    Intent intent;
            	    
            	    intent = new Intent(NotificationsTabView.this.getApplicationContext(), MessageNotificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
            	    spec = tabHost.newTabSpec("MessageTab").setIndicator("MessageTab").setContent(intent);  
            	    tabHost.addTab(spec); 

            	    intent = new Intent(NotificationsTabView.this.getApplicationContext(), NotificationsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	    if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)){
            	    	Bundle bundleObj = new Bundle();
            	    	bundleObj.putString("lisnId", lisnId);
            	    	bundleObj.putString("RSVP",RSVP);
        		    	intent.putExtras(bundleObj);
                    }
                    if(Constants.CALLING_SCREEN_OTHER_PROFILE.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)||
                    		Constants.CALLING_SCREEN_COMMON_LISN.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)||
                    		Constants.CALLING_SCREEN_COMMON_FRIEND.equalsIgnoreCase(NotificationsActivity.callingNotificationsScreen)){
                    	Bundle bundleObj = new Bundle();
        		    	bundleObj.putString("uid", uid);
        		    	intent.putExtras(bundleObj);
                    }
            	    spec = tabHost.newTabSpec("FriendReqTab").setIndicator("FriendReqTab").setContent(intent); 
            	    tabHost.addTab(spec);  
            	    
            	    
            	    
            	    tabHost.setCurrentTab(0);
            	    
            	    findViews();
            	    setListeners();
            	    setupTabBar();
            	    
                }});
        	return null;
        }

        @Override
        protected void onPostExecute(Void result){
        	if(pd != null)
        		pd.dismiss();
        }
    }
}