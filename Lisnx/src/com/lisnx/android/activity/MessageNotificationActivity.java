package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
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

import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.MessageNotifications;
import com.lisnx.model.MessageNotificationsViewHolder;
import com.lisnx.model.Notification;
import com.lisnx.model.NotificationCount;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class MessageNotificationActivity extends BaseActivity implements OnClickListener,Runnable{
	
	public static ListView listView = null;
	private ArrayList <HashMap<String, Object>> availableMessageNotifications;
	public static final String MESSAGE_COUNT = "count";
	public static final String IDKEY = "id";
	public static String LISN_NAME = "lisnName";
	public static final String MESSAGE_TYPE_KEY = "messageType";
	public static final String PRIVATE_MESSAGE_COUNT_KEY = "privateMessageCount";
	public static final String PRIVATE_MESSAGE_SENDER_KEY = "sender";
	public static final String PRIVATE_MESSAGE_SENDER_NAME_KEY = "senderName";
	public static final String PRIVATE_MESSAGE_LATEST = "latestMessageContent";
	public static final String PRIVATE_MESSAGE_LATEST_DATE = "latestMessageDate";
	public static final String IS_IMAGE_KEY = "isImage";

	public ListAdapter adapter = null;
	public static String targetUserId = null;
	public static ArrayList<String> notificationId = new ArrayList<String>();
	public static final int PENDING_NOTIFICATION = 3;
	public int newPosition=0;
	public static final String POSITION_KEY = "position";
	public static final String BITMAP_KEY = "bitmap";
	
	public Bitmap bmImg=null;
	private int imageWidth=0;
	private int imageHeight=0;
	public static String callingNotificationsScreen=null;
	public String uid = null;
	public String lisnId = null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	private TextView noMessagesText;
	public BitmapDownloaderTask task;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_notification_list);
        layoutInflater = this.getLayoutInflater();
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        
        if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		lisnId = extras.getString("lisnId");
        	}
        }
        if(Constants.CALLING_SCREEN_OTHER_PROFILE.equalsIgnoreCase(callingNotificationsScreen)){
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		uid = extras.getString("uid");
        	}
        }
        
        listView = (ListView)findViewById(R.id.list);
        TextView listIntroductionText=(TextView) findViewById(R.id.listIntroText);
        listIntroductionText.setText(R.string.unreadMessages);
        noMessagesText=(TextView) findViewById(R.id.noMessageNotificationBox);
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetMessageNotificationsData getMessageNotificationsData = new GetMessageNotificationsData();
        			getMessageNotificationsData.execute();
            	}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(2);
        	e.printStackTrace();
        }
	}
	
	private class GetMessageNotificationsData extends AsyncTask<Void, Void, List<MessageNotifications>>{
		public GetMessageNotificationsData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(MessageNotificationActivity.this, null, MessageNotificationActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected List<MessageNotifications> doInBackground(Void... params) {
			return getMessageNotificationsList();
		}
		
		@Override
        protected void onPostExecute(List<MessageNotifications> notificationsData){
			List<MessageNotifications> dataExist = notificationsData;
            if((dataExist!=null) && (!(dataExist.isEmpty()))){
            	displayMessageNotifications(dataExist);
    		} else{
    			if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "No Message Visible...");
    			setAllOtherNotificationCounts(0);
				listView.setVisibility(View.GONE);
        		noMessagesText.setVisibility(View.VISIBLE);
            }
			handler.sendEmptyMessage(2);
		}
	}
	
	public void run(){}
	
	public void setAllOtherNotificationCounts(int msgnotificationCount){
		try{
			NotificationCount notificationCount = Utils.getNotificationCounts(getParamsMapWithTokenAndTimeZone(handler), getApplicationContext()); 
            if(notificationCount != null){
            	if(msgnotificationCount == 0){
            		NotificationsTabView.msgCount.setVisibility(View.GONE);
            	}
            	
            	NotificationsTabView.friendRequestsCountNotification.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        switch(displayMetrics.densityDpi){ 
		        	case DisplayMetrics.DENSITY_LOW:
		        		NotificationsTabView.friendRequestsCountNotification.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		//VISH BaseActivity.friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		break; 
		        }
		        NotificationsTabView.friendRequestsCountNotification.setText(notificationCount.getFriendRequestCount());
		        
		        if(notificationCount.getFriendRequestCount()>0){
		        	//VISH BaseActivity.friendRequestCount.setVisibility(View.VISIBLE);
		        	//VISH BaseActivity.friendRequestCount.setText(notificationCount.getFriendRequestCount());
				} else {
					//VISH BaseActivity.friendRequestCount.setVisibility(View.GONE);
				}
            } else if(msgnotificationCount > 0){
            	//VISH BaseActivity.friendRequestCount.setVisibility(View.VISIBLE);
            	//VISH BaseActivity.friendRequestCount.setText(Integer.toString(msgnotificationCount));
	        	NotificationsTabView.friendRequestsCountNotification.setVisibility(View.GONE);
            } else{
            	//VISH BaseActivity.friendRequestCount.setVisibility(View.GONE);
            	NotificationsTabView.msgCount.setVisibility(View.GONE);
            	NotificationsTabView.friendRequestsCountNotification.setVisibility(View.GONE);
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
		   case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;
		   case R.id.peopleNearByNotification:
			   navigatePeopleNearByScreen(view);
			   break;
		}
	}
	
	public List<MessageNotifications> getMessageNotificationsList(){
		Map<String, String> params = getParamsMapWithTokenAndTimeZone(handler);
		
		List<MessageNotifications> messageNotificationsList = null;
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

	public List<Notification> getNotificationsList(){
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return null;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(1);
			return null;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		
		List<Notification> notificationsList = null;
		try {
			notificationsList = Utils.getNotificationsList(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
			return null;
		}
		return notificationsList;
	}
	
	public void displayMessageNotifications(List<MessageNotifications> messageNotificationLists){
		int msgCountNotification=0;
		List<MessageNotifications> messageNotificationsList = messageNotificationLists;
		availableMessageNotifications = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> messageNotificationsData = null;
		MessageNotifications messageNotifications = null;
        
		try{
        for(int i = 0 ; i < messageNotificationsList.size() ; i++){
        	messageNotifications = messageNotificationsList.get(i);
        	messageNotificationsData = new HashMap<String, Object>();
        	messageNotificationsData.put(MESSAGE_COUNT, messageNotifications.getMessageCount());
        	messageNotificationsData.put(LISN_NAME, messageNotifications.getLisnName());
        	messageNotificationsData.put(IDKEY, messageNotifications.getMessageNotificationID());
        	messageNotificationsData.put(MESSAGE_TYPE_KEY, messageNotifications.getMsgType());
        	messageNotificationsData.put(PRIVATE_MESSAGE_COUNT_KEY, messageNotifications.getPrivateMessageCount());
        	messageNotificationsData.put(PRIVATE_MESSAGE_SENDER_KEY, messageNotifications.getSender());
        	messageNotificationsData.put(PRIVATE_MESSAGE_SENDER_NAME_KEY, messageNotifications.getSenderName());
        	messageNotificationsData.put(PRIVATE_MESSAGE_LATEST, messageNotifications.getMessage());
        	messageNotificationsData.put(PRIVATE_MESSAGE_LATEST_DATE, messageNotifications.getMessageDate());;
        	messageNotificationsData.put(IS_IMAGE_KEY, messageNotifications.getIsImage());
        	availableMessageNotifications.add(messageNotificationsData);
        	
        	if(messageNotifications.getMsgType().equalsIgnoreCase("lisnMessage")){
        		msgCountNotification=msgCountNotification + Integer.valueOf(messageNotifications.getMessageCount());
        	}
        
			if(messageNotificationsList.size() != 0){
				NotificationsTabView.msgCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
			    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			    switch(displayMetrics.densityDpi){
			        case DisplayMetrics.DENSITY_LOW:
			        	NotificationsTabView.msgCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
			        	break; 
			    }
			    NotificationsTabView.msgCount.setText(Integer.toString(messageNotificationsList.size()));
			}	
			Integer privateNotificationCount =0;
			try{
				privateNotificationCount = Integer.valueOf(messageNotifications.getPrivateMessageCount());
			}catch(Exception e){
			}
			setAllOtherNotificationCounts(privateNotificationCount);
		 
			adapter = new ListAdapter(availableMessageNotifications,MessageNotificationActivity.this);
			listView.setAdapter(adapter);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
			OnItemClickListener listener = new OnItemClickListener (){
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> messageNotifications = (HashMap<String, Object>) adapter.getAdapter().getItem(position);
					String msgType=(String)messageNotifications.get(MESSAGE_TYPE_KEY);
				
					if("lisnMessage".equalsIgnoreCase(msgType))
					{
						targetUserId=(String)messageNotifications.get(IDKEY);
						String msgCountOfLisn=(String)messageNotifications.get(MESSAGE_COUNT);
						try	{
							Intent otherProfileIntent = new Intent(view.getContext(), LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Bundle bundleObj = new Bundle();
							bundleObj.putString("id", (String)messageNotifications.get(IDKEY));
							bundleObj.putString("callingFrom",Constants.CALLING_SCREEN_MESSAGE_NOTIFICATION);
							bundleObj.putString("msgCountOfLisn",msgCountOfLisn);
	    		    	
							otherProfileIntent.putExtras(bundleObj);    		    	
							view.getContext().startActivity(otherProfileIntent);
							overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
	    	    			} catch(Exception ex) {}
					} else {
						@SuppressWarnings("unused")
						String sender=(String)messageNotifications.get("pmSender");
						OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
						try{
		            			Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            			Bundle bundleObj = new Bundle();	
		            			bundleObj.putString("lisnerId", (String)messageNotifications.get(PRIVATE_MESSAGE_SENDER_KEY));
		            			otherProfileIntent.putExtras(bundleObj);
		            			view.getContext().startActivity(otherProfileIntent);
		            			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		    	    	} catch(Exception ex) {}
					}
				}
        	};
        	listView.setOnItemClickListener (listener);
			}
		}catch(Exception e){
			handler.sendEmptyMessage(7);
			return;
		}
	}
	
	public void acceptRequest(View view, String targetUserId, int position){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		navigateChooseProfileScreen(view,targetUserId, position);
	}
	
	public synchronized Bitmap getOtherUserImage(String otherUserId){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, otherUserId);
		
		try {
			bmImg = Utils.getOtherPersonImage(params, MessageNotificationActivity.this);
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
                    } catch (final Exception e) {}
                }
                return null;
            }
        });
    }
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return null;
	}

	public void refreshMessageNotificationsList(){
		try{
		runOnUiThread(new Runnable() {
            public void run() {
        		List<MessageNotifications> dataExist = getMessageNotificationsList();
                if((dataExist != null) && (dataExist.size() != 0)){
                    listView.setVisibility(View.VISIBLE);
                    noMessagesText.setVisibility(View.GONE);
            		displayMessageNotifications(dataExist);
            	} else{
                	setAllOtherNotificationCounts(0);
                	listView.setVisibility(View.GONE);
                	noMessagesText.setVisibility(View.VISIBLE);
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
		MessageNotificationActivity.callingNotificationsScreen=Constants.CALLING_SCREEN_OTHER_PROFILE;
		try	{   		
	    	Intent friendRequestIntent = new Intent(view.getContext(), MessageNotificationActivity.class);	
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
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_NOTIFICATIONS;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);	
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigateNotificationsScreen(View view){
		try	{   		
	    	Intent notificationIntent = new Intent(view.getContext(), NotificationsActivity.class);	
	    	startActivityForResult(notificationIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex){}
	}
	
	
	
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
		private ArrayList<HashMap<String, Object>> allNotifications; 
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<HashMap<String, Object>> allNotifications, Context context){
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
			 MessageNotificationsViewHolder holder;
			 String msgType=(String) allNotifications.get(position).get(MessageNotificationActivity.MESSAGE_TYPE_KEY);
			
			 if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				 holder = new MessageNotificationsViewHolder();
				//VISH
				if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "Get View Called...");
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
				 String message_count=(String) allNotifications.get(position).get(MessageNotificationActivity.MESSAGE_COUNT);
				 String lisn_name=(String) allNotifications.get(position).get(MessageNotificationActivity.LISN_NAME);
				 holder.messageNotification.setText(message_count + " messages in " + lisn_name + " LISN");
			 } else {
				 String message_count=(String) allNotifications.get(position).get(MessageNotificationActivity.PRIVATE_MESSAGE_COUNT_KEY);
				 String sender_name=(String) allNotifications.get(position).get(MessageNotificationActivity.PRIVATE_MESSAGE_SENDER_NAME_KEY);
				 String messageDate = (String) allNotifications.get(position).get(MessageNotificationActivity.PRIVATE_MESSAGE_LATEST_DATE);
				 String message = (String) allNotifications.get(position).get(MessageNotificationActivity.PRIVATE_MESSAGE_LATEST);
				 
				 holder.txtPrivateMessage.setText(message );
				 holder.senderName.setText(sender_name);
				 holder.txtDate.setText(messageDate);
				 holder.newMessageCount.setText(message_count);
				 if("0".equals(message_count)){
					 holder.newMessageCount.setVisibility(View.INVISIBLE);
				 }
				 
				 String otherId=(String) allNotifications.get(position).get(MessageNotificationActivity.PRIVATE_MESSAGE_SENDER_KEY);
				 String isImage=(String) allNotifications.get(position).get(MessageNotificationActivity.IS_IMAGE_KEY);
					
				 if("true".equalsIgnoreCase(isImage)){
					 downloadImage(otherId, holder.senderImage, position);
				 }
			 }
			return convertView;
		}
	}//End of list adapter
}