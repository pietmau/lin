package com.lisnx.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lisnx.android.activity.ChooseProfileActivity;
import com.lisnx.android.activity.LisnDetailActivity;
import com.lisnx.android.activity.MenuActivity;
import com.lisnx.android.activity.MessageBoardActivity;
import com.lisnx.android.activity.NotificationsActivity;
import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.android.activity.PeopleNearByActivity;
import com.lisnx.android.activity.ProfileActivity;
import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.LisnDetail;
import com.lisnx.model.Status;
import com.lisnx.service.CustomToast;
import com.lisnx.service.TimerStopper;
import com.lisnx.service.UserInfo;
import com.lisnx.util.AppCrashReports;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
@SuppressWarnings("deprecation")
public class LisnDetailTabView extends TabActivity implements LisnDetailTabBarLayout.OnTabChangedListener,OnClickListener{
	
	private LisnDetailTabBarLayout tabBar;
	private LisnDetailTabButtonLayout lisnerTabButton;
	private LisnDetailTabButtonLayout messageTabButton;
	private TabHost tabHost;
	private ProgressDialog pd;
	private TextView nameText;
	private ImageView userImage;
	private ImageButton joinLisnButton=null;
	public static final int LISN_DETAIL = 3;
	public String id = null;
	private TextView name=null;
	private TextView startDate=null;
	private TextView endDate=null;
	private TextView lisnDescription=null;
	private String callingFrom=null;
	private String messageCount=null;
	private String RSVP=null;
	private View lisnDetailDate=null;
	private View lisnDetailText=null;
	private ImageButton hideButton=null;
	private TextView tabSelectedText=null;
	private TextView lisnCountText=null;
	public static TextView messageCountText=null;

	ImageView backIcon;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ;
	public static TextView peopleNearbyCount=null;
	public static TextView friendRequestCount=null;
	LayoutInflater layoutInflater; 
	public static boolean isLastMessageIdSent = false;
	public String globalLastMessageId = null;
	public boolean openingForFirstTime = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Bundle extras = getIntent().getExtras();
	    if(extras != null){
	    	id = extras.getString("id");
	    	callingFrom=extras.getString("callingFrom");
	    	messageCount=extras.getString("msgCountOfLisn");
        	RSVP=extras.getString("RSVP");
	    }
        
        if(messageCount == null){
        	messageCount="0";
        }
	    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
		setContentView(R.layout.lisn_detail_tab_view);
		layoutInflater = this.getLayoutInflater();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ImageView friendReqImage=(ImageView) findViewById(R.id.lisner_image);
		friendReqImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_lisners2)));
		ImageView messageImage=(ImageView) findViewById(R.id.message_image);
		messageImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_message_bubble)));
		
		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
	    peopleNearByIcon.setOnClickListener(this);
	    backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        peopleNearbyCount = (TextView) findViewById(R.id.notificationBoxTextPeople);
        friendRequestCount = (TextView) findViewById(R.id.notificationBoxTextNotifications);
        /*nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);*/
        
        try{
    	    if(!RSVP.equalsIgnoreCase(Constants.RSVP_IN)){
    	    	TextView text = (TextView) findViewById(R.id.txtView);
    	    	text.setVisibility(View.VISIBLE);
    	    	text.setText("Click to join");
    	    	joinLisnButton = (ImageButton) findViewById(R.id.joinLisnButton);
    	    	joinLisnButton.setVisibility(View.VISIBLE);
    	    	joinLisnButton.setBackgroundResource(R.drawable.ic_lisn_in);
    	    	joinLisnButton.setOnClickListener(this);
            }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
        
        name = (TextView) findViewById(R.id.name);
        startDate = (TextView) findViewById(R.id.one);
        endDate = (TextView) findViewById(R.id.two);
        lisnDescription = (TextView) findViewById(R.id.lisnDetail);
        lisnDescription.setMovementMethod(new ScrollingMovementMethod());
        lisnCountText=(TextView)findViewById(R.id.lisnerCount);
        messageCountText=(TextView)findViewById(R.id.messageCount);
        
        try{
        	runOnUiThread(new Runnable() {
	        	public void run() {
	        		GetLisnDetails getLisnDetails = new GetLisnDetails();
	        		getLisnDetails.execute(id);
	        	}
	        });
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        tabSelectedText=(TextView)findViewById(R.id.listIntroText);
        tabSelectedText.setText(R.string.lisners);
        lisnDetailDate=findViewById(R.id.LDD);
        lisnDetailText=findViewById(R.id.LDT);
        hideButton=(ImageButton) findViewById(R.id.hideButton);
        hideButton.setOnClickListener(this);
         
        try{
        	new ProcessUITask().execute();
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        performInitialWork();
	}
	
	public void performInitialWork(){
        if((NowLisnActivity.permanentName == null) || 
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
    	}
	}
	
	private class GetLisnDetails extends AsyncTask<String, Void, LisnDetail>{
		public GetLisnDetails(){
			super();
		}
		
		@Override
        protected void onPreExecute(){}

		@Override
		protected LisnDetail doInBackground(String... params) {
			return getLisnDetails(params[0]);
		}
		
		@Override
        protected void onPostExecute(LisnDetail lisnDetailData){
			displayLisnsDetail(lisnDetailData);
			DisplayMetrics displayMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        switch(displayMetrics.densityDpi){ 
	        	case DisplayMetrics.DENSITY_LOW:
	        		lisnCountText.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        		messageCountText.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        		break;
	        }
        	lisnCountText.setText(Integer.toString(lisnDetailData.getLisnersName().size()));
		}
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        try{
        	AppCrashReports.activityPaused();
        	TimerStopper.stopTimers();
        	if(isLastMessageIdSent == false){ 
        		if(tabHost.getCurrentTab() == 0){
        			globalLastMessageId = LisnDetailActivity.lastMessageIdForLisnTab; 
        		} else if(tabHost.getCurrentTab() == 1){
        			globalLastMessageId = MessageBoardActivity.lastMessageIdForMessageTab;
        		}
                sendLastViewedMessageId(globalLastMessageId);
        	} 
        	
        	LisnDetailActivity.lastMessageIdForLisnTab = null;
        	MessageBoardActivity.lastMessageIdForMessageTab = null;
        	isLastMessageIdSent = false;
        	ImageView homeIcon = (ImageView) findViewById(R.id.backIcon);
            if(homeIcon != null){
            	homeIcon.setAlpha(250);
            }
        } catch(Exception e){
        	e.printStackTrace();
        }
    }

	public void displayLisnsDetail(LisnDetail lisnDetail){
		try {
			name.setText(lisnDetail.getName());
			SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date myDate1 = format.parse(lisnDetail.getStartDate());
			Date myDate2 = format.parse(lisnDetail.getEndDate());
			String finalStartDate=new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(myDate1);
			String finalEndDate=new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(myDate2);
			startDate.setText(finalStartDate);
			endDate.setText(finalEndDate);
			if(!"null".equalsIgnoreCase(lisnDetail.getDescription()) ){
				lisnDescription.setText(lisnDetail.getDescription());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LisnDetail getLisnDetails(String id){
		String timeZone=Utils.getTimeZone();
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			//handler.sendEmptyMessage(1);
		}
		
		if(accessToken == null || accessToken.length() == 0){
			//handler.sendEmptyMessage(1);
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.TIME_STAMP,timeZone);
		params.put("id", id);
		
		LisnDetail lisnDetail = null;
		try {
			lisnDetail = Utils.getLisnDetails(params, this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		return lisnDetail;
	}
	
	public void sendLastViewedMessageId(String lastMessageId){	
		if(lastMessageId == null){
			lastMessageId = "0";
		}
		
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			//handler.sendEmptyMessage(1);
		}
		
		if(accessToken == null || accessToken.length() == 0){
			//handler.sendEmptyMessage(1);
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LISN_ID_PARAM, id);
		params.put(Constants.LAST_MESSAGE_ID_PARAM, lastMessageId);
		
		Status status = null;
		try {
			status = Utils.sendLastViewedMessageId(params, getApplicationContext());
		} catch (NullPointerException e) {} 
		  catch (JSONException e) {}
		
		try{
			if((status != null) && (Constants.SUCCESS.equalsIgnoreCase(status.getStatus()))){
				return;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
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
				CustomToast.showCustomToast(getApplicationContext(), Constants.LOCATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 8){
				CustomToast.showCustomToast(getApplicationContext(), Constants.LISN_JOINED_SUCCESSFUL, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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
	
	public void onClick(View view){
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   performFinishingTasks();
			   navigateMenuScreen(view);
			   break;	
		  /* case R.id.userName:
			   performFinishingTasks();
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   performFinishingTasks();
			   navigateProfileScreen(view);
			   break;*/
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   performFinishingTasks();
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   performFinishingTasks();
			   navigateFriendRequestScreen(view);
			   break;
		   case R.id.joinLisnButton:
			   joinLisn();
			   break;
		   case R.id.hideButton:
			   hideButtonClicked(view);
			   break;
		}
	}
	
	public void sendLastMessageId(String lastMessageId){
		try{
			sendLastViewedMessageId(lastMessageId);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void hideButtonClicked(View view) {
		if(lisnDetailDate.getVisibility()==View.VISIBLE||lisnDetailText.getVisibility()==View.VISIBLE)
		{
			lisnDetailDate.setVisibility(View.GONE);	
			lisnDetailText.setVisibility(View.GONE);
			if(findViewById(R.id.for_lisn_header) != null){
				findViewById(R.id.for_lisn_header).setVisibility(View.GONE);
			}
			/*if(findViewById(R.id.image_name_strip) != null){
				findViewById(R.id.image_name_strip).setVisibility(View.GONE);
			}*/
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_down_arrow)));
		} else{
			lisnDetailDate.setVisibility(View.VISIBLE);	
			lisnDetailText.setVisibility(View.VISIBLE);
			if(findViewById(R.id.for_lisn_header) != null){
				findViewById(R.id.for_lisn_header).setVisibility(View.VISIBLE);
			}
			/*if(findViewById(R.id.image_name_strip) != null){
				findViewById(R.id.image_name_strip).setVisibility(View.VISIBLE);
			}*/
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_up_arrow)));
		}
	}

	public void navigateLisnDetailScreen(View view){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
		navigateChooseProfileScreen(view); 
	}
	
	public void navigateChooseProfileScreen(View view){
		try	{   		
	    	Intent chooseProfileIntent = new Intent(view.getContext(), ChooseProfileActivity.class);
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("id", id);
	    	chooseProfileIntent.putExtras(bundleObj);
	    	startActivityForResult(chooseProfileIntent, LISN_DETAIL);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {}
	}
	
	public void joinLisn(){
		String shareProfileType2=Constants.PROFILE_SHARE_ALL;
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(1);
			return;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LISN_ID_PARAM, id);
		params.put(Constants.PROFILE_TYPE_PARAM,shareProfileType2);
		
		Status status = null;
		try {
			status = Utils.joinLisn(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			try	{   
        		Intent lisnDetailIntent = new Intent(this, LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	Bundle bundleObj = new Bundle();	
		    	bundleObj.putString("id", id);
		    	bundleObj.putString("RSVP","In");
		    	lisnDetailIntent.putExtras(bundleObj);
		    	startActivityForResult(lisnDetailIntent,0);
		    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
		    	handler.sendEmptyMessage(8);
	    	} catch(Exception ex) {}
		} else{
			handler.sendEmptyMessage(2);
			return;
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
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);	
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("lisnId", id);
	    	bundleObj.putString("RSVP",RSVP);
	    	profileIntent.putExtras(bundleObj);
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("lisnId", id);
	    	bundleObj.putString("RSVP",RSVP);
	    	peopleNearByIntent.putExtras(bundleObj);
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateFriendRequestScreen(View view){
		NotificationsActivity.callingNotificationsScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
		try	{   		
	    	Intent friendRequestIntent = new Intent(view.getContext(), NotificationsActivity.class);
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("lisnId", id);
	    	bundleObj.putString("RSVP",RSVP);
	    	friendRequestIntent.putExtras(bundleObj);
	    	startActivityForResult(friendRequestIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	private void findViews() {
		tabBar = (LisnDetailTabBarLayout) findViewById(R.id.maintabbar);
		lisnerTabButton = (LisnDetailTabButtonLayout) findViewById(R.id.lisner_tabbutton);
		messageTabButton = (LisnDetailTabButtonLayout) findViewById(R.id.message_tabbutton);
	}
	
	private void setListeners() {
		tabBar.setOnTabChangedListener(this);
		lisnerTabButton.setOnTouchListener(lisnerTabButton);
		lisnerTabButton.setOnClickListener(tabBar);
		messageTabButton.setOnTouchListener(messageTabButton);
		messageTabButton.setOnClickListener(tabBar);
	}
	
	private void setupTabBar() {
		lisnerTabButton.registerTabBar(tabBar);
		messageTabButton.registerTabBar(tabBar);

		if(Constants.CALLING_SCREEN_MESSAGE_NOTIFICATION.equalsIgnoreCase(callingFrom)){
			tabBar.selectTab(messageTabButton);
	    } else{
	    	tabBar.selectTab(lisnerTabButton);
	    }	
	}

	public void onTabChanged(int tabId) {
		switch (tabId) {
        case R.id.lisner_tabbutton:
        	performFinishingTasksForTabChanged();
            tabHost.setCurrentTab(0);
            tabSelectedText.setText(R.string.lisners);
            if(MessageBoardActivity.timer != null){
				MessageBoardActivity.timer.cancel();
			}
            break;
        case R.id.message_tabbutton:
        	performFinishingTasksForTabChanged();
        	tabHost.setCurrentTab(1);
        	tabSelectedText.setText(R.string.messages);
        	if(LisnDetailActivity.lisnDetailTimer != null){
				LisnDetailActivity.lisnDetailTimer.cancel();
			}
            break;
        }
	}	
	
	public void launchActivity(int activityId) {
		tabHost.setCurrentTab(activityId);
	}
	
	private class ProcessUITask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
        	pd = ProgressDialog.show(LisnDetailTabView.this, null, LisnDetailTabView.this.getResources().getString(R.string.lodingMessage), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
        	runOnUiThread(new Runnable() {
                public void run() {
                	tabHost = getTabHost();   
            	    TabHost.TabSpec spec;
            	    Intent intent;
            	    Bundle bundleObj = new Bundle();
                	bundleObj.putString("id", id);

            	    intent = new Intent(LisnDetailTabView.this.getApplicationContext(), LisnDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	    intent.putExtras(bundleObj);
            	    spec = tabHost.newTabSpec("LisnerTab").setIndicator("LisnerTab").setContent(intent); 
            	    tabHost.addTab(spec);  
            	    
            	    intent = new Intent(LisnDetailTabView.this.getApplicationContext(), MessageBoardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	    intent.putExtras(bundleObj);
            	    spec = tabHost.newTabSpec("MessageBoardTab").setIndicator("MessageBoardTab").setContent(intent);  
            	    tabHost.addTab(spec); 
            	    tabHost.setCurrentTab(0);
            	    
            	    findViews();
            	    setListeners();
            	    setupTabBar();
            	    
            	    try{
	            	    if(!RSVP.equalsIgnoreCase(Constants.RSVP_IN)){
	                    	tabHost.setVisibility(View.GONE);	
	                    	hideButton.setVisibility(View.GONE);
	                    }
            	    }catch(Exception e){}
                }});
        	return null;
        }

        @Override
        protected void onPostExecute(Void result){
        	if(pd != null)
        		pd.dismiss();
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == LISN_DETAIL) {
	    	if(data != null){
	    		id = data.getStringExtra("id");
	    		joinLisn();
	    	}
	    }
	}
	
	public void performFinishingTasks(){
		try{
			if(tabHost.getCurrentTab() == 0){
				if(LisnDetailActivity.lisnDetailTimer != null){
					LisnDetailActivity.lisnDetailTimer.cancel();
				}
			} else if(tabHost.getCurrentTab() == 1){
				if(MessageBoardActivity.timer != null){
					MessageBoardActivity.timer.cancel();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void performFinishingTasksForTabChanged(){
		try{
			if(!openingForFirstTime){
			if(tabHost.getCurrentTab() == 1){
    			if(MessageBoardActivity.lastMessageIdForMessageTab != null){
    				globalLastMessageId = MessageBoardActivity.lastMessageIdForMessageTab ; 
    				sendLastMessageId(MessageBoardActivity.lastMessageIdForMessageTab);	
    			}
				MessageBoardActivity.lastMessageIdForMessageTab = null;
    		}
		
    		if(tabHost.getCurrentTab() == 0){
    			if(LisnDetailActivity.lastMessageIdForLisnTab != null){
    				globalLastMessageId = LisnDetailActivity.lastMessageIdForLisnTab;
    				sendLastMessageId(LisnDetailActivity.lastMessageIdForLisnTab);
    			}
    			LisnDetailActivity.lastMessageIdForLisnTab = null;
    		}
			} else{
				openingForFirstTime = false;
			}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
		
		openingForFirstTime = false;
	}
}