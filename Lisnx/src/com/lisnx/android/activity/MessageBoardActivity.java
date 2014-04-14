package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.event.ILisner;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.LisnDetail;
import com.lisnx.model.LisnDetailViewHolder;
import com.lisnx.model.MessageDetail;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class MessageBoardActivity extends BaseActivity implements ILisner,OnClickListener,Runnable, OnScrollListener{

	@SuppressWarnings("unused")
	private EventNotifier eventNotifier;
	private ArrayList<Object> messageDetailList = new ArrayList<Object>(); 
	public static final String LISN_DETAIL_KEY = "lisnDetail";
	public static final String DATE_KEY = "startDate";
	public static final String LISNERS_NAME_KEY = "lisnersName";
	public static final String NAME_COUNT_KEY = "name";
	public static final String BITMAP_KEY = "bitmap";
	public static final String SENDER_KEY = "sender";
	public static final String CONTENT_KEY = "content";
	public static final String IS_IMAGE_KEY = "isImage";
	
	public static ListView listView = null;
	public boolean addHeader = true;
	public String id = null;
	public String messageName=null;
	public String messangerName=null;
	public String messageId=null;
	public String lisnerId=null;
	public String userId=null;
	public String dateCreated=null;
	public String isImage=null;
	public String connectionStatus=null;	
	public Bitmap bmImg=null;
	private int imageWidth=0;
	private int imageHeight=0;
	public static Resources res;
	public static Bitmap defaultBitmap;
	LisnDetail lisnDetail;
	public String isNew=null;
	public static Timer timer = new Timer();
	private int msgCount=0;
	private int newMsgCount=0;
	public ListAdapter adapter = null;
	public int lastMessageId = 0;
	public int newMessageListCount = 0;
	public int oldMessageListCount = 0;
	public static String lastMessageIdForMessageTab = null;
	public BitmapDownloaderTask task;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	id = extras.getString("id");
        }
                
        setContentView(R.layout.message_board);
        eventNotifier = new EventNotifier (this);
        layoutInflater = this.getLayoutInflater();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        ImageView postMessageButton = (ImageView) findViewById(R.id.postMessageButton);
        postMessageButton.setOnClickListener(this);
        
        listView = (ListView)findViewById(R.id.messageBoardList);
        adapter = new ListAdapter(messageDetailList,getApplicationContext());		
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setOnScrollListener(this);
        if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "MessageBoardActivity called...");
        
        try{
        	toCallAsynchronous();
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		try{
			int lastVisiblePosition = listView.getLastVisiblePosition();
			int lastVisibleItemId = 0;
			
			if(lastVisiblePosition >= 0){
			   lastVisibleItemId = getMessageId((HashMap<String , String>)adapter.getItem(lastVisiblePosition));
			}
		
			if(lastVisibleItemId > lastMessageId){
				lastMessageId = lastVisibleItemId;
				lastMessageIdForMessageTab = Integer.toString(lastMessageId);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getMessageId(HashMap<String,String> map){
		Set<Map.Entry<String, String>> set = map.entrySet();
		String messageKey = "";
		String messageValue = "";
		for(Map.Entry<String, String> entry : set){
			messageKey = entry.getKey();
			 
			if("messageId".equalsIgnoreCase(messageKey)){
				messageValue = entry.getValue();
				messageId = messageValue;
			}
		}
		return Integer.parseInt(messageId); 
	}
	
	public int getUserId(HashMap<String,String> map){
		Set<Map.Entry<String, String>> set = map.entrySet();
		String messageKey = "";
		String messageValue = "";
		for(Map.Entry<String, String> entry : set){
			messageKey = entry.getKey();
			 
			if(messageKey.equalsIgnoreCase("userId")){
				messageValue = entry.getValue();
				userId=messageValue;
			}
		}
		return Integer.parseInt(userId) ; 
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	public void toCallAsynchronous() {
		timer = new Timer();
	    TimerTask doAsynchronousTask;
	    final Handler handler = new Handler();

	            doAsynchronousTask = new TimerTask() {
	                @Override
	                public void run() {
	                    handler.post(new Runnable() {
	                        public void run() {
	                            try {
	                            	MessageDetail messageDetail=getMessageDetails(id);
	                            	if(messageDetail != null){
	                            		displayMessageDetail(messageDetail);
	                            	}
	                            } catch (Exception e){}
	                        }
	                    });
	                }
	            };
	            timer.schedule(doAsynchronousTask, 0, Constants.REFRESH_ITERATION_INTERVAL);	
	}
	
    public void displayMessageDetail(final MessageDetail messageDetail){
    	messageDetailList.clear();
        try{
        	for(int i = 0 ; i < messageDetail.getOldMessageName().size() ; i++){
        		messageDetailList.add(messageDetail.getOldMessageName().get(i));
        	}
        	
            for(int i = 0 ; i < messageDetail.getNewMessageName().size() ; i++){
            	messageDetailList.add(messageDetail.getNewMessageName().get(i));
            }
            
            if(msgCount != messageDetail.getOldMessageName().size() || (newMsgCount != messageDetail.getNewMessageName().size()))
        	{
        		adapter.notifyDataSetChanged();
        		msgCount = messageDetail.getOldMessageName().size();
        		newMsgCount = messageDetail.getNewMessageName().size();
        	}
        	
            if(!(messageDetail.getNewMessageName().isEmpty())){
        		newMessageListCount = messageDetail.getNewMessageName().size();
        	} else{
        		newMessageListCount = 0;
        	}
        	
            if(!(messageDetail.getOldMessageName().isEmpty())){
            	oldMessageListCount = messageDetail.getOldMessageName().size();
            } else{
            	oldMessageListCount = 0;
            }
        	
        	runOnUiThread(new Runnable() {
				public void run() {
                	int listSize = 0 ;
                	int lastUserId = 0;
                	if(messageDetail.getNewMessageName() != null){
                		listSize = messageDetail.getNewMessageName().size();
                	}
                	if(listSize != 0){
                		for(int i = 0 ; i < messageDetail.getNewMessageName().size() ; i++){
                			lastUserId = getUserId((HashMap<String , String>)messageDetail.getNewMessageName().get(i));
                			if(lastUserId == Integer.parseInt(BaseActivity.permanentUserId)){
                				newMessageListCount = newMessageListCount - 1;
                				oldMessageListCount = oldMessageListCount + 1;
                			}
                		}
                	}
                	setMessageCount(newMessageListCount, oldMessageListCount);
                }
            });
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public MessageDetail getMessageDetails(String id){
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
		params.put(Constants.LISN_ID_PARAM, id);
		params.put(Constants.LAST_MESSAGE_ID_PARAM, Integer.toString(lastMessageId));
		
		MessageDetail messageDetail = null;
		try {
			messageDetail = Utils.getMessageDetails(params, this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		try{
			if(messageDetail != null){
				if(messageDetail.getLastMessageId() == null || "".equalsIgnoreCase(messageDetail.getLastMessageId())){
					lastMessageId = 0;
				} else{
					lastMessageId = Integer.parseInt(messageDetail.getLastMessageId());
				}
				lastMessageIdForMessageTab = Integer.toString(lastMessageId);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return messageDetail;
	}
	
	public void setMessageCount(int newMessageListCount, int oldMessageListCount){
		try{
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			switch(displayMetrics.densityDpi){
	        	case DisplayMetrics.DENSITY_LOW:
	        		LisnDetailTabView.messageCountText.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        		break; 
			}
		
			if(newMessageListCount > 0){
				LisnDetailTabView.messageCountText.setVisibility(View.VISIBLE);
				LisnDetailTabView.messageCountText.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.ic_notification_icon));
				LisnDetailTabView.messageCountText.setText(Integer.toString(newMessageListCount));
			} else if(oldMessageListCount > 0){
				LisnDetailTabView.messageCountText.setVisibility(View.VISIBLE);
				LisnDetailTabView.messageCountText.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.ic_notification_icon_gray));
				LisnDetailTabView.messageCountText.setText(Integer.toString(oldMessageListCount));
			} else {
				LisnDetailTabView.messageCountText.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendLastViewedMessageId(){
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
		params.put(Constants.LAST_MESSAGE_ID_PARAM, Integer.toString(lastMessageId));
		
		Status status = null;
		try {
			status = Utils.sendLastViewedMessageId(params, getApplicationContext());
		} catch (NullPointerException e) {} 
		  catch (JSONException e) {}
		
		try{
			if(status != null && Constants.SUCCESS.equalsIgnoreCase(status.getStatus())){
				LisnDetailTabView.isLastMessageIdSent = true;
				return;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized Bitmap getOtherUserImage(String otherUserId){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, otherUserId);
		
		try {
			bmImg = Utils.getOtherPersonImage(params, MessageBoardActivity.this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(1);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
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
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return null;
	}

	public void finishingTasks(){
		if(timer != null){
			timer.cancel();
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
			else if(msg.what == 6){
				if(pd != null)
				pd.dismiss();
			}
		}
	};

	@Override
	public void run() {}

	@Override
	public void onClick(View view) {
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
		   case R.id.postMessageButton:
			   sendButtonClickListener();
			   break;
		   case R.id.peopleNearByNotification:
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   navigateFriendRequestScreen(view);
			   break;
		}
	}
	
	public void sendButtonClickListener(){
		EditText messageText = (EditText) findViewById(R.id.messageField);
		final String fullMessage = messageText.getText().toString().trim();
		
		if(fullMessage == null || fullMessage.length() == 0){
			CustomToast.showCustomToast(this, Constants.MESSAGE_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			messageText.requestFocus();
			return;
		}
		
		messageText.setText("");
		try{
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		} catch(Exception e){
			
		}
		
		Thread thread=new Thread(new Runnable(){
	        public void run(){
	        	postMessage(fullMessage);
	        }
        });
        thread.start();
	}
  
	public void postMessage(final String fullMessage){
		String timeZone=Utils.getTimeZone();
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(1);
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LISN_ID_PARAM, id);
		params.put(Constants.MESSAGE_CONTENT_PARAM, fullMessage);
		params.put(Constants.TIME_STAMP,timeZone);
		
		MessageDetail messageDetail = null;
		try {
			messageDetail = Utils.getMessagePostDetails(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		try{
			if(messageDetail!=null){
				lastMessageId = Integer.parseInt(messageDetail.getLastViewedMessageId());
				lastMessageIdForMessageTab = Integer.toString(lastMessageId);
				runOnUiThread(new Runnable(){
            		public void run() {
            			try{
            				MessageDetail messageDetail=getMessageDetails(id);
            				displayMessageDetail(messageDetail);
            				handler.sendEmptyMessage(6);
            			} catch(Exception e){
            			}
            		}
				});
			}
			}catch(Exception e){
				handler.sendEmptyMessage(12); 
		}
	}
	
	@Override
	public void updateLocation() {
		DatabaseHelper db = new DatabaseHelper(this);
    	DatabaseUtility database = new DatabaseUtility(db);	
    	
    	CurrentLocation location = database.getLocationData();
    	if(location != null){
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
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("lisnId", id);
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
	    	peopleNearByIntent.putExtras(bundleObj);
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
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
		private ArrayList<Object> lisnDetail;
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<Object> lisnDetail, Context context){
			this.lisnDetail = lisnDetail;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return lisnDetail.size();
		}

		@Override
		public Object getItem(int position) {
			return lisnDetail.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			 LisnDetailViewHolder holder;
			 if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				 holder = new LisnDetailViewHolder();
				 
				 HashMap<String,String> map = (HashMap<String , String>)lisnDetail.get(position);
				 Set<Map.Entry<String, String>> set = map.entrySet();
				 String lisnersKey = "";
				 String lisnersValue="";
				 for(Map.Entry<String, String> entry : set){
					 lisnersKey = entry.getKey();

					 if("userId".equalsIgnoreCase(lisnersKey)){
						 lisnersValue = entry.getValue();
						 lisnerId=lisnersValue;						 
					 }
				 }
				 
				 if(lisnerId != null && lisnerId.equalsIgnoreCase(BaseActivity.permanentUserId))
					 convertView = mInflater.inflate(R.layout.message_board_message_name_reverse, null);
				 else
					 convertView = mInflater.inflate(R.layout.message_board_message_name, null);
				 
		             holder.lisnersName = (TextView) convertView.findViewById(R.id.messageName);
		             holder.startDate = (TextView) convertView.findViewById(R.id.txtDate);
		             holder.lisnerImage = (ImageView) convertView.findViewById(R.id.lisnerImage);
		             
		             holder.lisnerImage.setOnClickListener( new View.OnClickListener(){
			     	        public void onClick(View view){
			     	        	 try{
			     	        		finishingTasks();
			     	        	 } catch(Exception e){
			     	        		 e.printStackTrace();
			     	        	 }
			     	        	 OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
			     	        	 HashMap<String,String> map = (HashMap<String , String>)lisnDetail.get(position);
								 Set<Map.Entry<String, String>> set = map.entrySet();
								 String lisnersKey = "";
								 String lisnersValue="";
								 for(Map.Entry<String, String> entry : set){
									 lisnersKey = entry.getKey();

									 if("userId".equalsIgnoreCase(lisnersKey)){
										 lisnersValue = entry.getValue();
										 lisnerId=lisnersValue;
									 }
								 }								 
								 
								 if(lisnerId != null && lisnerId.equalsIgnoreCase(BaseActivity.permanentUserId)){
									 ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_LISN_DETAIL;
									 Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									 Bundle bundleObj = new Bundle();
								     bundleObj.putString("lisnId", id);
								     profileIntent.putExtras(bundleObj);
								     view.getContext().startActivity(profileIntent);
					    		     overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
								 }else {
								 
									 try{   
					            		Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    		    	Bundle bundleObj = new Bundle();	
					    		    	bundleObj.putString("lisnerId", lisnerId);
					    		    	bundleObj.putString("lisnId", id);
					    		    	otherProfileIntent.putExtras(bundleObj);
					    		    	view.getContext().startActivity(otherProfileIntent);
					    		    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		//Specify an explicit transition animation to perform next
					    	    	} catch(Exception ex) {
					    	    	}
								 }
			     	     }});
		             convertView.setTag(holder);
			 }else {
				 holder = (LisnDetailViewHolder) convertView.getTag(); 
			 }
			 
				HashMap<String,String> map = (HashMap<String , String>)lisnDetail.get(position);
				 Set<Map.Entry<String, String>> set = map.entrySet();
				 String messageKey = "";
				 String messageValue = "";
				 for(Map.Entry<String, String> entry : set){
					 messageKey = entry.getKey();
					 
					 if("messageId".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 messageId=messageValue;
					 }
					 if("content".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 messageName=messageValue;
					 }
					 if("fullName".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 messangerName=messageValue;
					 }
					 if("userId".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 lisnerId=messageValue;
					 }
					 if("createdDate".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 dateCreated=messageValue;
					 }
					 if("isImage".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 isImage=messageValue;
					 }
					 if("isNew".equalsIgnoreCase(messageKey)){
						 messageValue = entry.getValue();
						 isNew=messageValue;						 
					 }
				 }
				
				 String date = dateCreated;
				 SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   	 try {
			   		Date myDate = format.parse(date);
			   		date=new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(myDate);
			   	 } catch (ParseException e) {
			   		e.printStackTrace();
			   	 }
			   	 
			   	try{
			   		 if(messangerName != null && messangerName.length() >= 10){
			   			String shortname = messangerName.substring(0, 7);
			   			messangerName = shortname + ".." ;
			   		 }
			   	} catch (Exception e){
			   		e.printStackTrace();
			   	}
			   	 
			   	date = date + "; " + messangerName;
				holder.startDate.setText(date);
				holder.lisnersName.setText(messageName);
				/*if("true".equalsIgnoreCase(isNew) && (lisnerId != null) && (!((lisnerId).equalsIgnoreCase(LoginActivity.permanentUserId)))){
					holder.lisnersName.setTypeface(Typeface.DEFAULT, 1);
				}*/
				 
				if("true".equalsIgnoreCase(isImage)){
					downloadImage(lisnerId, holder.lisnerImage, position);
				}	 
			return convertView;
		}
	}
}