package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.MessageNotifications;
import com.lisnx.model.MessageNotificationsViewHolder;
import com.lisnx.model.Notification;
import com.lisnx.model.NotificationsViewHolder;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class NotificationsActivity extends BaseActivity implements OnClickListener,Runnable{
	
	public static ListView listView = null;
	public static final String NOTIFICATION_NAME_KEY = "notification";
	public static final String IDKEY = "id";
	public static final String MESSAGE_TYPE_KEY = "messageType";
	public ListAdapter adapter = null;
	public static String targetUserId = null;
	public static ArrayList<String> notificationId = new ArrayList<String>();
	public static final int PENDING_NOTIFICATION = 3;
	public int newPosition=0;
	public static String IS_IMAGE_KEY = "isImage";
	public static final String POSITION_KEY = "position";
	public static final String BITMAP_KEY = "bitmap";
	private TextView noFriendRequestText;
	
	public Bitmap bmImg=null;
	private int imageWidth=0;
	private int imageHeight=0;
	public static String callingNotificationsScreen=null;
	public String uid = null;
	public String lisnId = null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	private String RSVP=null;
	public int countOfFriendRqsts = 0;
	public int countOfMessageNotifications = 0 ;
	public int totalNotifications = 0;
	public BitmapDownloaderTask task;
	List<MessageNotifications> messageNotificationsList;
	List<Notification> notificationsList;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_list);
        layoutInflater = this.getLayoutInflater();
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        
        if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		lisnId = extras.getString("lisnId");
        		RSVP=extras.getString("RSVP");
        	}
        }
        if(Constants.CALLING_SCREEN_OTHER_PROFILE.equalsIgnoreCase(callingNotificationsScreen)||
        		Constants.CALLING_SCREEN_COMMON_LISN.equalsIgnoreCase(callingNotificationsScreen)||
        		Constants.CALLING_SCREEN_COMMON_FRIEND.equalsIgnoreCase(callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		uid = extras.getString("uid");
        	}
        }
        //VISH
        if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "NotificationsActivity Called...");
        //showOKAlertMsg("", "NotificationsActivity");
        
        listView = (ListView)findViewById(R.id.list);
        TextView listIntroductionText=(TextView) findViewById(R.id.listIntroText);
        listIntroductionText.setText(R.string.friendRequests);
        noFriendRequestText=(TextView) findViewById(R.id.noFriendRequestsnotificationBox);
        //VISH BaseActivity.friendRequestCount = (TextView)findViewById(R.id.notificationBoxTextNotifications);
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetNotificationsData getNotificationsData = new GetNotificationsData();
        			getNotificationsData.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(2);
        	e.printStackTrace();
        }
        setListenersOnTopIcons(); 
	}
	
	private class GetNotificationsData extends AsyncTask<Void, Void, List<Notification>>{
		public GetNotificationsData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(NotificationsActivity.this, null, NotificationsActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected List<Notification> doInBackground(Void... params) {
			getMessageNotificationsList();
			return getNotificationsList();
		}
		
		@Override
        protected void onPostExecute(List<Notification> notificationsData)
		{
			List<Notification> dataExist = notificationsData;
            if((dataExist!=null) && (!(dataExist.isEmpty()))){
            	displayNotifications(dataExist);
    		} else{
    			setAllOtherNotificationCounts(0);
				listView.setVisibility(View.GONE);
				noFriendRequestText.setVisibility(View.VISIBLE);
            }
			handler.sendEmptyMessage(2);
		}
	}
	
	public void run(){}
	
	public void setAllOtherNotificationCounts(int frndRqstCount){
		try{
			List<MessageNotifications> messageNotificationList = getMessageNotificationsList();
			if(messageNotificationList != null){
				if(frndRqstCount == 0){
					//NotificationsTabView.friendRequestsCountNotification.setVisibility(View.GONE);
            	}
				countOfMessageNotifications = Integer.valueOf(messageNotificationList.get(0).getPrivateMessageCount());
				NotificationsTabView.msgCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
	        	getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        	switch(displayMetrics.densityDpi){ 
	        		case DisplayMetrics.DENSITY_LOW:
	        			NotificationsTabView.msgCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        			//VISH BaseActivity.friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        			break; 
	        	}
	        	//NotificationsTabView.msgCount.setText(Integer.toString(countOfMessageNotifications));
	        	if(countOfMessageNotifications == 0){
	        		NotificationsTabView.msgCount.setVisibility(View.GONE);
	        	}
			} else if(frndRqstCount > 0){
				//VISH BaseActivity.friendRequestCount.setVisibility(View.VISIBLE);
				//VISH BaseActivity.friendRequestCount.setText(Integer.toString(frndRqstCount));
				NotificationsTabView.msgCount.setVisibility(View.GONE);
			} else {
				//VISH BaseActivity.friendRequestCount.setVisibility(View.GONE);
				NotificationsTabView.friendRequestsCountNotification.setVisibility(View.GONE);
				NotificationsTabView.msgCount.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void onClick(View view){
		switch(view.getId()){
		   case R.id.backIcon:
			   navigateMenuScreen(view);
			   break;
		  /* case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;*/
		   case R.id.peopleNearByNotification:
			   navigatePeopleNearByScreen(view);
			   break;
		}
	}
	
	public List<Notification> getNotificationsList(){
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"NotificationsActivity ---  getNotificationsList");
		Map<String , String> params = getParamsMapWithTokenAndTimeZone(handler);
		try {
			notificationsList = Utils.getNotificationsList(params,this);
			if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"getNotificationsList size : "+notificationsList.size());
			if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"Params : "+params.toString());
		} catch (NullPointerException e) { 
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
			return null;
		}
		return notificationsList;
	}
	
	private List<MessageNotifications> getMessageNotificationsList(){
		Map<String , String> params = getParamsMapWithTokenAndTimeZone(handler);
		
		try {
			messageNotificationsList = Utils.getMessageNotificationsList(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
			return null;
		}
		return messageNotificationsList;
	}
	
	public void displayNotifications(List<Notification> notificationLists){
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"NotificationsActivity ---  displayNotifications");
		List notificationsList = new ArrayList();
		notificationsList.addAll(messageNotificationsList);
		notificationsList.addAll(this.notificationsList);
		
		countOfFriendRqsts = notificationsList.size();
		try{
	    
			if(notificationsList.size()!=0){
				//VISH BaseActivity.friendRequestCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				switch(displayMetrics.densityDpi){ 
			    	case DisplayMetrics.DENSITY_LOW:
			    		//VISH BaseActivity.friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
			    		break; 
				}
				//BaseActivity.friendRequestCount.setText(Integer.toString(notificationsList.size()));
			}		
			
			//setAllOtherNotificationCounts(notificationsList.size());
          
			adapter = new ListAdapter(notificationsList,NotificationsActivity.this);     
			listView.setAdapter(adapter);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "On New Adapter : "+notificationsList.toString());
			OnItemClickListener listener = new OnItemClickListener (){
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
					OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
					if(adapter.getAdapter().getItem(position) instanceof Notification){
						Notification notification = (Notification) adapter.getAdapter().getItem(position);
						targetUserId=(String)notification.getNotificationID();
						String messageType = (String)notification.getMsgType();
						if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "On New Adapter Called: ");
	    				try	{   
	    					if(Constants.MESSAGE_TYPE_ACCEPTED_REQUEST.equalsIgnoreCase(messageType)){
	    						notifyServerForAcceptedRequestSeen(notification.getNotificationID());
	    					}
	    					Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    					Bundle bundleObj = new Bundle();
	    					bundleObj.putString("lisnerId", notification.getNotificationID());
	    					otherProfileIntent.putExtras(bundleObj);
	    					view.getContext().startActivity(otherProfileIntent);
	    					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
	    					
	    				} catch(Exception ex) {}
					}else {
						if(adapter.getAdapter().getItem(position) instanceof MessageNotifications){
							MessageNotifications messageNotification = (MessageNotifications)adapter.getAdapter().getItem(position) ;
							onMessageClick(messageNotification, view, position, id);
						}
					}
				}
			};
			listView.setOnItemClickListener (listener);
		} catch(Exception e){
			handler.sendEmptyMessage(7);
			return;
		}
	}
	public void onMessageClick(MessageNotifications messageNotifications, View view, int position,long id) {
		String msgType=(String)messageNotifications.getMsgType();
	
		if("lisnMessage".equalsIgnoreCase(msgType))
		{
			targetUserId=(String)messageNotifications.getMessageNotificationID();
			String msgCountOfLisn=(String)messageNotifications.getMessageCount();
			try	{
				Intent otherProfileIntent = new Intent(view.getContext(), LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundleObj = new Bundle();
				bundleObj.putString("id", (String)messageNotifications.getMessageNotificationID());
				bundleObj.putString("callingFrom",Constants.CALLING_SCREEN_MESSAGE_NOTIFICATION);
				bundleObj.putString("msgCountOfLisn",msgCountOfLisn);
	    	
				otherProfileIntent.putExtras(bundleObj);    		    	
				view.getContext().startActivity(otherProfileIntent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    			} catch(Exception ex) {}
		} else {
			@SuppressWarnings("unused")
			String sender=(String)messageNotifications.getSender();
			OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
			try{
        			Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			Bundle bundleObj = new Bundle();	
        			bundleObj.putString("lisnerId", (String)messageNotifications.getSender());
        			otherProfileIntent.putExtras(bundleObj);
        			view.getContext().startActivity(otherProfileIntent);
        			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	    	} catch(Exception ex) {}
		}
	}
	
	public void resetNotificationCounts(){
		totalNotifications = countOfFriendRqsts + countOfMessageNotifications;
		try{
			if((countOfFriendRqsts - 1) > 0){
				NotificationsTabView.friendRequestsCountNotification.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
	    		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	    		switch(displayMetrics.densityDpi){ 
	        		case DisplayMetrics.DENSITY_LOW:
	        			NotificationsTabView.friendRequestsCountNotification.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        			break; 
	    		}
	    		NotificationsTabView.friendRequestsCountNotification.setText(Integer.toString(countOfFriendRqsts - 1));
	    		countOfFriendRqsts = countOfFriendRqsts - 1;
			} else {
				NotificationsTabView.friendRequestsCountNotification.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
            	noFriendRequestText.setVisibility(View.VISIBLE);
			}
			
			if((totalNotifications - 1) > 0){
				//VISH BaseActivity.friendRequestCount.setVisibility(View.VISIBLE);
            	DisplayMetrics displayMetrics = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        switch(displayMetrics.densityDpi){ 
		        	case DisplayMetrics.DENSITY_LOW:
		        		//VISH BaseActivity.friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		break; 
		        }
		      //VISH BaseActivity.friendRequestCount.setText(Integer.toString((totalNotifications - 1)));
		        totalNotifications = totalNotifications - 1 ;
			} else{
				//VISH BaseActivity.friendRequestCount.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void notifyServerForAcceptedRequestSeen(String friendId){
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
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
		params.put(Constants.FRIEND_ID_PARAM, friendId);
		
		Status status = null;
		try {
			status = Utils.notifyForAcceptedRequestSeen(params, getApplicationContext());
		} catch (NullPointerException e) {} 
		  catch (JSONException e) {}
		
		try{
			if(status != null && Constants.SUCCESS.equalsIgnoreCase(status.getStatus())){
				return;
			} else{}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void acceptRequest(View view, String targetUserId, int position){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		navigateChooseProfileScreen(view,targetUserId, position);
	}
	
	public void acceptFriendRequest(String targetUserId,int position){
		//String shareProfileType2=ChooseProfileActivity.shareProfileType1;
		String shareProfileType2=Constants.PROFILE_SHARE_ALL;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
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
		params.put(Constants.TARGET_USER_ID_PARAM, targetUserId);
		params.put(Constants.PROFILE_TYPE_PARAM,shareProfileType2);
		
		Status status = null;
		
		try {
			status = Utils.acceptFriendRequest(params, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			runOnUiThread(new Runnable() {
	            public void run() {
	            	resetNotificationCounts();
	            }
			});
			runOnUiThread(new Runnable() {
	            public void run() {
	            	adapter.notifyDataSetChanged();
	            }
			});
			handler.sendEmptyMessage(6);
			return;
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	public void ignoreFriendRequest(View view, String targetUserId, int position){
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.ignoringRequest), true);
		
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
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
		params.put(Constants.TARGET_USER_ID_PARAM, targetUserId);
		
		Status status = null;
		try {
			status = Utils.ignoreFriendRequest(params, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			runOnUiThread(new Runnable() {
	            public void run() {
	            	resetNotificationCounts();
	            }
			});
			runOnUiThread(new Runnable() {
	            public void run() {
	            	adapter.notifyDataSetChanged();
	            }
			});
			handler.sendEmptyMessage(5);
			return;
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	public synchronized Bitmap getOtherUserImage(String otherUserId){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, otherUserId);
		
		try {
			bmImg = Utils.getOtherPersonImage(params, NotificationsActivity.this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(1);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(1);
			return null;
		}
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch(displayMetrics.densityDpi){ 
        	case DisplayMetrics.DENSITY_LOW: 
        		imageWidth=Constants.LOW_WIDTH_SMALL;
        		imageHeight=Constants.LOW_HEIGHT_SMALL;
        		break; 
        	case DisplayMetrics.DENSITY_MEDIUM: 
        		imageWidth=Constants.MEDIUM_WIDTH_SMALL;
        		imageHeight=Constants.MEDIUM_HEIGHT_SMALL;
        		break; 
        	case DisplayMetrics.DENSITY_HIGH: 
        		imageWidth=Constants.HIGH_WIDTH_SMALL;
        		imageHeight=Constants.HIGH_HEIGHT_SMALL;
        		break; 
        	case DisplayMetrics.DENSITY_XHIGH: 
        		imageWidth=Constants.HIGH_WIDTH_SMALL;
        		imageHeight=Constants.HIGH_HEIGHT_SMALL;
        		break; 
        	default:
        		imageWidth=Constants.HIGH_WIDTH_SMALL;
        		imageHeight=Constants.HIGH_HEIGHT_SMALL;
        		break;
        } 
		
        if(bmImg==null){
        	return null;
        }else {
        	bmImg = Bitmap.createScaledBitmap(bmImg,imageWidth, imageHeight, true);
        	return bmImg;
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.device_menu, menu);
	    setMenuBackground();
	    return true;
	}
	
	private void setMenuBackground() {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {
            public View onCreateView(final String name,final Context context,final AttributeSet attributeSet) {
            	
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {  
                    try {
                        final LayoutInflater f = getLayoutInflater();
                        final View view = f.createView(name, null, attributeSet);
                        //view.setBackgroundResource(R.drawable.menu_selector);
                        
                        new Handler().post(new Runnable() {
                            public void run() {
                               view.setBackgroundResource(R.drawable.menu_selector);               
                            }
                        });
                        return view;
                    } catch (final Exception e) {                                                             
                    }
                }
                return null;
            }
        });
    }
	
	@Override
	public View onCreateView(String name, Context context,AttributeSet attrs) {
		return null;
	}

	private void refreshNotificationsList(){
	  try{
	 runOnUiThread(new Runnable() {
        public void run() {
    		List<Notification> dataExist = getNotificationsList();
            if(dataExist!=null){
                listView.setVisibility(View.VISIBLE);
                noFriendRequestText.setVisibility(View.GONE);
        		displayNotifications(dataExist);
        	} else{
            	setAllOtherNotificationCounts(0);
            	//VISH BaseActivity.friendRequestCount.setVisibility(View.GONE);
            	listView.setVisibility(View.GONE);
            	noFriendRequestText.setVisibility(View.VISIBLE);
            }    
        }
	 });
	  } catch(Exception e){
		  e.printStackTrace();
	  }
  }
	
	public void navigateSettingsScreen(View view){
		try	{   		
	    	Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);	
	    	startActivityForResult(settingsIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateChooseProfileScreen(View view, String targetUserId ,int position){
		try	{  
	    	Intent chooseProfileIntent = new Intent(view.getContext(), ChooseProfileActivity.class);
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", targetUserId);
	    	bundleObj.putString("position", Integer.toString(position));
	    	chooseProfileIntent.putExtras(bundleObj);
	    	startActivityForResult(chooseProfileIntent, PENDING_NOTIFICATION);	
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
	
	public void navigateNowLisnScreen(View view){
		try	{   		
	    	Intent loginIntent = new Intent(view.getContext(), TabView.class);		
	    	startActivityForResult(loginIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateChangePasswordScreen(View view){
		try	{   		
	    	Intent changePasswordIntent = new Intent(view.getContext(), ChangePasswordActivity.class);	
	    	startActivityForResult(changePasswordIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateFriendRequestScreen(View view){
		NotificationsActivity.callingNotificationsScreen=Constants.CALLING_SCREEN_OTHER_PROFILE;
		try	{   		
	    	Intent friendRequestIntent = new Intent(view.getContext(), NotificationsActivity.class);	
	    	startActivityForResult(friendRequestIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateFriendsScreen(View view){
		try	{   		
	    	Intent friendsIntent = new Intent(view.getContext(), FriendsActivity.class);	
	    	startActivityForResult(friendsIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateOtherProfileScreen(View view){
		OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		try	{   		
	    	Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class);
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("lisnerId", uid);
	    	otherProfileIntent.putExtras(bundleObj);
	    	startActivityForResult(otherProfileIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateLisnDetailScreen(View view){
		try	{   
    		Intent lisnDetailIntent = new Intent(view.getContext(), LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("id", lisnId);
	    	lisnDetailIntent.putExtras(bundleObj);
	    	lisnDetailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.lodingMessage), true);
	    	view.getContext().startActivity(lisnDetailIntent);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	    	pd.dismiss();
    	} catch(Exception ex) {
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
	
	public void downloadImage(String otherId, ImageView imageView, int position) {
        //resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(otherId);

        if (bitmap == null) {
            download(otherId, imageView, position);
        } else {
            cancelPotentialDownload(otherId, imageView, position);
            imageView.setImageBitmap(bitmap);
        }
    }
	
	public void  download(String otherId, ImageView imageView, int position) {
		if (otherId == null) {
	         imageView.setImageDrawable(null);
	         return;
	     }
		 
		 if (cancelPotentialDownload(otherId, imageView, position)) {
			 task = new BitmapDownloaderTask(imageView, position);
			 DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, res, defaultBitmap);
        	 imageView.setImageDrawable(downloadedDrawable);
        	 task.execute(otherId);
		 }
	}
	
	private static class DownloadedDrawable extends BitmapDrawable {
	    private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

	    private DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Resources resources, Bitmap bitmap) {
	        super(res, defaultBitmap); 
	        bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
	    }

	    public BitmapDownloaderTask getBitmapDownloaderTask() {
	        return bitmapDownloaderTaskReference.get();
	    }
	}
	 
	private static boolean cancelPotentialDownload(String otherId, ImageView imageView, int position) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapId = bitmapDownloaderTask.otherId;
            if ((bitmapId == null) || (!bitmapId.equals(otherId))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same id is already being downloaded.
                return false;
            }
        }
        return true;
	}
	 
	private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
	}
	 
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		 private String otherId;
		 private final WeakReference<ImageView> imageViewReference;
		 @SuppressWarnings("unused")
		private int position;

		 public BitmapDownloaderTask(ImageView imageView, int imagePosition) {
		     imageViewReference = new WeakReference<ImageView>(imageView);
		     position = imagePosition;
		 }

		 @Override
		 protected Bitmap doInBackground(String... params) {
		    otherId = params[0];
		    return getOtherUserImage(otherId);
		 }

		 @Override
		 protected void onPostExecute(Bitmap bitmap) {
		     if (isCancelled()) {
		         bitmap = null;
		     }
		     
		     if(bitmap != null){
		         addBitmapToCache(otherId, new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
		     }

		     if (imageViewReference != null) {
	             ImageView imageView = imageViewReference.get();
	             BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
	             if ((this == bitmapDownloaderTask)) {
	            	 if(imageView != null && bitmap != null){
	            		 imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
	            	 }
	             }
	         }
		 }
	}
	
	private Bitmap getBitmapFromCache(String otherId) {
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(otherId);
            if (bitmap != null) {
                sHardBitmapCache.remove(otherId);
                sHardBitmapCache.put(otherId, bitmap);
                return bitmap;
            }
        }
        
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(otherId);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                return bitmap;
            } else {
                sSoftBitmapCache.remove(otherId);
            }
        }

        return null;
	}
	
    @SuppressWarnings("serial")
	private final HashMap<String, Bitmap> sHardBitmapCache =
        new LinkedHashMap<String, Bitmap>(Constants.HARD_CACHE_CAPACITY / 2, 0.75f, true) {
    	
        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > Constants.HARD_CACHE_CAPACITY) {
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(Constants.HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };
    
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }
    
    @SuppressWarnings("unused")
	private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, Constants.DELAY_BEFORE_PURGE);
    }

    private void addBitmapToCache(String otherId, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(otherId, bitmap);
            }
        }
    }
	 
	private class ListAdapter extends BaseAdapter{
		private List<?> allNotifications; 
		private LayoutInflater mInflater;
		
		public ListAdapter(List<?> allNotifications, Context context){
			this.allNotifications = allNotifications;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return allNotifications.size();
		}

		@Override
		public Object getItem(int position) {
			return allNotifications.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(allNotifications.get(position) instanceof Notification){
				final Notification notification = (Notification)allNotifications.get(position);
			 NotificationsViewHolder holder;
			 String msgType=(String)notification.getMsgType();
			 
			 if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				 holder = new NotificationsViewHolder();
				 
				 if(Constants.MESSAGE_TYPE_ACCEPTED_REQUEST.equalsIgnoreCase(msgType)){
					 convertView = mInflater.inflate(R.layout.notification_view_accepted, null);
					 holder.notificationName = (TextView) convertView.findViewById(R.id.notificationsNameAccepted);
		             holder.notificationImage = (ImageView) convertView.findViewById(R.id.notificationsImageAccepted);
		             holder.connectedImage = (ImageView) convertView.findViewById(R.id.connectedImageAccepted);
				 } else{
					 convertView = mInflater.inflate(R.layout.notifications_view, null);
	             	 holder.notificationName = (TextView) convertView.findViewById(R.id.notificationsName);
	             	 holder.notificationImage = (ImageView) convertView.findViewById(R.id.notificationsImage);
	             	 holder.acceptRequestButton = (ImageView) convertView.findViewById(R.id.acceptRequestButton);
	             	 holder.ignoreRequestButton = (ImageView) convertView.findViewById(R.id.ignoreRequestButton);
				 }
	             convertView.setTag(holder);
			 } else {
				 holder = (NotificationsViewHolder) convertView.getTag(); 
			 }
			 
			 if(Constants.MESSAGE_TYPE_ACCEPTED_REQUEST.equalsIgnoreCase(msgType)){
            	 holder.notificationName.setText(((String)notification.getNotificationName()));
             } else{
            	 holder.notificationName.setText((String)notification.getNotificationName());
            	 holder.acceptRequestButton.setFocusable(false);
            	 holder.acceptRequestButton.setFocusableInTouchMode(false);
            	 holder.ignoreRequestButton.setFocusable(false);
            	 holder.ignoreRequestButton.setFocusableInTouchMode(false);
			
            	 holder.acceptRequestButton.setOnClickListener( new View.OnClickListener(){
            		 public void onClick(View view){
            			targetUserId=(String)notification.getNotificationID();
						//acceptRequest(view, targetUserId, position);
						acceptFriendRequest(targetUserId, position);
						
						//VISH recall same activity to perform refresh
		        		//startActivity(getIntent());
		        		Intent i = new Intent(getApplicationContext(), NotificationsActivity.class);
		        		startActivity(i);
		        		finish();
            	 }});
			
				holder.ignoreRequestButton.setOnClickListener( new View.OnClickListener(){
					public void onClick(View view){
						targetUserId=(String)notification.getNotificationID();
		        		ignoreFriendRequest(view,targetUserId,position);
		        
		        		//VISH recall same activity to perform refresh
		        		//startActivity(getIntent());
		        		Intent i = new Intent(getApplicationContext(), NotificationsActivity.class);
		        		startActivity(i);
		        		finish();
				}});
            }
			
			String otherId=(String)notification.getNotificationID();
			String isImage=(String)notification.getIsImage();
			
			if("true".equalsIgnoreCase(isImage)){
				downloadImage(otherId, holder.notificationImage, position);
			}return convertView; 
			}else return getMessageView(position, convertView, parent);
			
			
		}
		public View getMessageView(final int position, View convertView, ViewGroup parent) {
			
			 MessageNotificationsViewHolder holder;
			 if(allNotifications.get(position) instanceof MessageNotifications){
			 	MessageNotifications messageNotification = (MessageNotifications)allNotifications.get(position);
			 String msgType=(String) messageNotification.getMsgType();
			
			 if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				 holder = new MessageNotificationsViewHolder();
				
				 if(Constants.MESSAGE_TYPE_LISNS.equalsIgnoreCase(msgType)){
					 convertView = mInflater.inflate(R.layout.message_notification_view, null);
		             holder.messageNotification = (TextView) convertView.findViewById(R.id.txtMessage);
				 } else {
					 convertView = mInflater.inflate(R.layout.message_notification_direct_message_list_view, null);
		             holder.txtPrivateMessage = (TextView) convertView.findViewById(R.id.txtPrivateMessage);
		             holder.senderImage = (ImageView) convertView.findViewById(R.id.senderImage);
		             holder.senderName = (TextView) convertView.findViewById(R.id.senderName);
		             holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
		             holder.newMessageCount =(TextView) convertView.findViewById(R.id.newMessageCount);
		             
		             holder.txtPrivateMessage = (TextView) convertView.findViewById(R.id.txtPrivateMessage);
		             
				 }
	             convertView.setTag(holder); 
			 } else {
				 holder = (MessageNotificationsViewHolder) convertView.getTag(); 
			 }
			 
			 if(Constants.MESSAGE_TYPE_LISNS.equalsIgnoreCase(msgType))
			 {
				 String message_count=messageNotification.getMessageCount();
				 String lisn_name= messageNotification.getLisnName();
				 holder.messageNotification.setText(message_count + " messages in " + lisn_name + " LISN");
			 } else {
				 String message_count=messageNotification.getPrivateMessageCount();
				 String sender_name=messageNotification.getSenderName();
				 String messageDate = messageNotification.getMessageDate();
				 String message = messageNotification.getMessage();
				 
				 holder.txtPrivateMessage.setText(message );
				 holder.senderName.setText(sender_name);
				 try {
					holder.txtDate.setText(Utils.parseAndFormat(messageDate));
				} catch (ParseException e) {
				}
				 holder.newMessageCount.setText(message_count);
				 if("0".equals(message_count)){
					 holder.newMessageCount.setVisibility(View.INVISIBLE);
				 }
				 
				 String otherId=messageNotification.getSender();
				 String isImage=messageNotification.getIsImage();
					
				 if("true".equalsIgnoreCase(isImage)){
					 downloadImage(otherId, holder.senderImage, position);
				 }
			 	}
			 }
			return convertView;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == PENDING_NOTIFICATION) {
	        if (resultCode == RESULT_OK) {
	        	if(data != null){
	        		targetUserId = data.getStringExtra("lisnerId");
	        		int position=Integer.parseInt(data.getStringExtra("position"));
	        		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.acceptingRequest), true);
	        		acceptFriendRequest(targetUserId, position);
	        	}
	        } 
	    }
	}
}