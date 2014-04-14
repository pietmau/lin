package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Friend;
import com.lisnx.model.FriendsViewHolder;
import com.lisnx.model.OtherProfile;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint({ "ParserError", "SimpleDateFormat", "HandlerLeak" })
public class CommonFriendsActivity extends BaseActivity implements OnClickListener{
	
	public String uid = null;
	public static ListView listView = null;
	private ArrayList <HashMap<String, Object>> availableFriends;
	public static final String FRIENDS_NAME_KEY = "friendName";
	public static final String IDKEY = "id";
	public static String IS_IMAGE_KEY = "isImage";
	public static final String POSITION_KEY = "position";
	public static final String BITMAP_KEY = "bitmap";	
	public ListAdapter adapter = null;
	public String longitude;
	public String lattitude;
	
	public Bitmap bmImg=null;
	private int imageWidth=0;
	private int imageHeight=0;
	public static Resources res;
	public static Bitmap defaultBitmap;
	public boolean fromItemClickListener=false;
	
	private ImageButton connectButton;
	private ImageButton acceptButton;
	private ImageButton ignoreButton;
	private ImageButton hideButton;
	@SuppressWarnings("unused")
	private ImageView commonFriendsImage;
	private ImageView commonLisnsImage;
	private TextView commonFriendsText;
	private TextView commonLisnsText;
	private ImageView profileFacebookImage;
	private ImageView profileLinkedInImage;
	private TextView declineMessage=null;
	private LinearLayout buttonsGroup;
	public String lisn_Id=null;
	ImageView backIcon;
	
	private String connectionStatus;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ;
	private TextView noCommonFriendsYetText;
	public static final int OTHER_PROFILE = 5;
	public Bitmap otherProfileImage=null;
	TextView connectOnText ;
	public BitmapDownloaderTask task;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	uid = extras.getString("uid");
        	lisn_Id = extras.getString("lisnId");
        }
        setContentView(R.layout.common_friend_list);
        layoutInflater = this.getLayoutInflater();
        
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        
        peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        noCommonFriendsYetText=(TextView) findViewById(R.id.noCommonFriendsYetBox);
        connectOnText = (TextView) findViewById(R.id.connectOnMessage);
        
        TextView listIntroductionText=(TextView) findViewById(R.id.listIntroText);
        listIntroductionText.setText(R.string.commonFriends);
        listView = (ListView)findViewById(R.id.list);
        /*nameText = (TextView) findViewById(R.id.userName);
        userImage=(ImageView)findViewById(R.id.userImage);*/
        
        connectButton = (ImageButton) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(this);
        acceptButton = (ImageButton) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(this);
        ignoreButton = (ImageButton) findViewById(R.id.ignoreButton);
        ignoreButton.setOnClickListener(this);
        hideButton = (ImageButton) findViewById(R.id.hideButton);
        hideButton.setOnClickListener(this);
        
        /*commonFriendsImage=(ImageView)findViewById(R.id.friendsImage);
        commonLisnsImage=(ImageView)findViewById(R.id.lisnImage);
        commonLisnsImage.setOnClickListener(this);
        commonFriendsText = (TextView) findViewById(R.id.commonFriendsCountText);
        commonLisnsText = (TextView) findViewById(R.id.commonLisnCountText);*/
        profileFacebookImage=(ImageView) findViewById(R.id.profileFacebookImage);
        profileFacebookImage.setOnClickListener(this);
        profileLinkedInImage=(ImageView) findViewById(R.id.profileLinkedInImage);
        profileLinkedInImage.setOnClickListener(this);
        declineMessage=(TextView) findViewById(R.id.declineMessage);
        buttonsGroup = (LinearLayout) findViewById(R.id.OPLL9);
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetCommonFriendsData getCommonFriendsData = new GetCommonFriendsData();
        			getCommonFriendsData.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(2);
        	e.printStackTrace();
        }
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetOtherProfileData getOtherProfileData = new GetOtherProfileData();
        			getOtherProfileData.execute();
        		}
        	});
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        //VISH updateNotificationAndPeopleNearbyCounts(); 
	}
	
	private class GetCommonFriendsData extends AsyncTask<Void, Void, List<Friend>>{
		public GetCommonFriendsData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(CommonFriendsActivity.this, null, CommonFriendsActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected List<Friend> doInBackground(Void... params) {
			return getFriendsList();
		}
		
		@Override
        protected void onPostExecute(List<Friend> commonFriendsData){
			List<Friend> dataExist = commonFriendsData;
            if((dataExist!=null) && (!(dataExist.isEmpty()))){
            	displayFriends(dataExist);
    		} else{
    			listView.setVisibility(View.GONE);
    			noCommonFriendsYetText.setVisibility(View.VISIBLE);
            }
			handler.sendEmptyMessage(2);
		}
	}
	
	private class GetOtherProfileData extends AsyncTask<Void, Void, OtherProfile>{
		public GetOtherProfileData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){}
		
		@Override
		protected OtherProfile doInBackground(Void... params) {
			return getOtherProfileData();
		}
		
		@Override
        protected void onPostExecute(OtherProfile otherProfile){
			setOtherProfileData(otherProfile);
		}
	}
	
	public void setOtherProfileData(OtherProfile otherProfile){
		if(otherProfile != null){
		try{
			buttonsGroup.setVisibility(View.VISIBLE);
			connectionStatus = otherProfile.getConnectionStatus();
			if(Constants.CONNECTION_STATUS_CONNECTED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
				connectButton.setClickable(false);
				connectButton.setVisibility(View.GONE);
				acceptButton.setClickable(false);
				acceptButton.setVisibility(View.GONE);
				ignoreButton.setClickable(false);
				ignoreButton.setVisibility(View.GONE);
				declineMessage.setVisibility(View.GONE);
				profileLinkedInImage.setVisibility(View.VISIBLE);
				connectOnText.setVisibility(View.VISIBLE);
				profileFacebookImage.setVisibility(View.VISIBLE);
				buttonsGroup.setVisibility(View.GONE);
				hideButton.setVisibility(View.GONE);
			}
			if(Constants.CONNECTION_STATUS_ACCEPT.equalsIgnoreCase(otherProfile.getConnectionStatus())){
				connectButton.setClickable(false);
				connectButton.setVisibility(View.GONE);
			}
			if(Constants.CONNECTION_STATUS_PENDING.equalsIgnoreCase(otherProfile.getConnectionStatus())){
				acceptButton.setClickable(false);
				acceptButton.setVisibility(View.GONE);
				ignoreButton.setClickable(false);
				ignoreButton.setVisibility(View.GONE);
			
				declineMessage.setVisibility(View.VISIBLE);
				declineMessage.setText("Your friend request is pending.");
				connectButton.setVisibility(View.VISIBLE);
				hideButton.setVisibility(View.VISIBLE);
			}
			if(Constants.CONNECTION_STATUS_NOT_CONNECTED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
				acceptButton.setClickable(false);
				acceptButton.setVisibility(View.GONE);
				ignoreButton.setClickable(false);
				ignoreButton.setVisibility(View.GONE);
			}
			if(Constants.CONNECTION_STATUS_IGNORED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
				acceptButton.setClickable(false);
				acceptButton.setVisibility(View.GONE);
				ignoreButton.setClickable(false);
				ignoreButton.setVisibility(View.GONE);	
				declineMessage.setVisibility(View.VISIBLE);
				declineMessage.setText("Your friend request is pending.");
			}
		
			try{
				if("true".equalsIgnoreCase(otherProfile.getIsDecline())){
					String date=otherProfile.getViewConnection();
					date=date.substring(0,10) + " " + date.substring(12,date.length()-1);		
					SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						Date myDate = format.parse(date);
						date=new SimpleDateFormat("MMM dd, yyyy").format(myDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				
					declineMessage.setVisibility(View.VISIBLE);
					declineMessage.setText("You declined " + otherProfile.getFullname() + "'s request on " + date);
					ignoreButton.setVisibility(View.GONE);
					connectButton.setVisibility(View.GONE);
					acceptButton.setVisibility(View.VISIBLE);
					acceptButton.setOnClickListener(this);
				}
			}catch(Exception e){}
		
			nameText.setText(otherProfile.getFullname());		
		
			if("true".equalsIgnoreCase(otherProfile.getIsImage())){
				downloadPersonImage(uid,userImage);
			}
		
			if("noValue".equalsIgnoreCase(otherProfile.getCommonFriendsCount())){
			}else{
				if(otherProfile.getCommonFriendsCount() != null && !("0".equalsIgnoreCase(otherProfile.getCommonFriendsCount()))){
					commonFriendsText.setVisibility(View.VISIBLE);
					DisplayMetrics displayMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
					switch(displayMetrics.densityDpi){ 
		        		case DisplayMetrics.DENSITY_LOW:
		        			commonFriendsText.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        			break; 
					}
					commonFriendsText.setText(otherProfile.getCommonFriendsCount());
				}
			}
		
			if("noValue".equalsIgnoreCase(otherProfile.getCommonLisnsCount())){
			}else{
				if(otherProfile.getCommonLisnsCount() != null && !("0".equalsIgnoreCase(otherProfile.getCommonLisnsCount()))){
					commonLisnsText.setVisibility(View.VISIBLE);
					DisplayMetrics displayMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
					switch(displayMetrics.densityDpi){ 
		        		case DisplayMetrics.DENSITY_LOW:
		        			commonLisnsText.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        			break; 
					}
					commonLisnsText.setText(otherProfile.getCommonLisnsCount());
				}
			}
		
			if("noValue".equalsIgnoreCase(otherProfile.getIsNearBy())){
			}else{
				if("true".equalsIgnoreCase(otherProfile.getIsNearBy())){
					ImageView isNearByIcon = (ImageView) findViewById(R.id.nearbyIndicator);
					isNearByIcon.setVisibility(View.VISIBLE);
					isNearByIcon.setBackgroundDrawable((getApplication().getResources().getDrawable(R.drawable.ic_online_icon2)));
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}	
		}
		
		try{
			if((acceptButton.getVisibility() == View.GONE) && (connectButton.getVisibility() == View.GONE)){
				hideButton.setVisibility(View.GONE);
			}	
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public OtherProfile getOtherProfileData(){
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			return null;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			return null;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		
		if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)){
			params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			params.put(Constants.USER_ID_PARAM, uid);
			params.put(Constants.LISN_ID_PARAM_2, lisn_Id);
		}
		else if(Constants.CALLING_SCREEN_PEOPLE_NEARBY.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_FRIENDS.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_COMMON_FRIEND.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_CHOOSE_PROFILE.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_NOTIFICATIONS.equalsIgnoreCase(OtherProfileActivity.callingOtherProfileScreen)){
			params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			params.put(Constants.OTHER_USER_ID_PARAM, uid);
		}
		
		OtherProfile otherProfile = null;
		try {
			otherProfile = Utils.getOtherProfileData(params, this);
		} catch (NullPointerException e) {
		} catch (JSONException e) {}
		
		return otherProfile;
	}
    
    public void downloadPersonImage(String otherId, ImageView imageView) {
    	UserBitmapDownloaderTask task = new UserBitmapDownloaderTask(imageView);
        task.execute(otherId);
    }
	
	class UserBitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	 	@SuppressWarnings("unused")
		private String otherId;
	    private final WeakReference<ImageView> imageViewReference;

	    public UserBitmapDownloaderTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    @Override
	    protected Bitmap doInBackground(String... params) {
	         return getImage(params[0]);
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if(bitmap!=null){
	        	if (imageViewReference != null) {
	        		ImageView imageView = imageViewReference.get();
	        		if(imageView != null ){
	        			imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
	        		} else {
	        			imageView=(ImageView)findViewById(R.id.userImage);
	        			imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
	        		}
	            }
	        }
	    }
	}
	
	public Bitmap getImage(String uid){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, uid);
		
	    Bitmap bmImg = null;
		try {
			bmImg = Utils.getOtherPersonImage(params, CommonFriendsActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch(displayMetrics.densityDpi){ 
        	case DisplayMetrics.DENSITY_LOW: 
        		imageWidth=Constants.LOW_WIDTH;
        		imageHeight=Constants.LOW_HEIGHT;
        		break; 
        	case DisplayMetrics.DENSITY_MEDIUM: 
        		imageWidth=Constants.MEDIUM_WIDTH;
        		imageHeight=Constants.MEDIUM_HEIGHT;
        		break; 
        	case DisplayMetrics.DENSITY_HIGH: 
        		imageWidth=Constants.HIGH_WIDTH;
        		imageHeight=Constants.HIGH_HEIGHT;
        		break; 
        	case DisplayMetrics.DENSITY_XHIGH: 
        		imageWidth=Constants.HIGH_WIDTH;
        		imageHeight=Constants.HIGH_HEIGHT;
        		break; 
        	default:
        		imageWidth=Constants.HIGH_WIDTH;
        		imageHeight=Constants.HIGH_HEIGHT;
        		break; 
        } 
		
        if(bmImg==null){
        		handler.sendEmptyMessage(4);
        		return null;
        }else {
        	bmImg = Bitmap.createScaledBitmap(bmImg,imageWidth, imageHeight, true);
        	otherProfileImage=bmImg;
        	return bmImg;
        }
	}

	public void onClick(View view){
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   navigateFriendRequestScreen(view);
			   break;
		   case R.id.hideButton:
			   hideButtons(view);
			   break;
		   case R.id.lisnImage:
			   navigateCommonLisnsScreen(view);
			   break;
		   case R.id.connectButton:
			   friendRequest(view);
			   break;
		   case R.id.acceptButton:
			   acceptRequest(view);
			   break;
		   case R.id.ignoreButton:
			   ignoreRequest(view);
			   break;
		   case R.id.instantBackButton:
			   navigateMenuScreen(view);
			   break;
		  /* case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;*/
		   case R.id.profileFacebookImage:
			   sendExternalConnectionRequest(view,"facebook");
			   break;
		   case R.id.profileLinkedInImage:
			   sendExternalConnectionRequest(view,"linkedin");
			   break;
		}
	}
	
	private void sendExternalConnectionRequest(View view,String connectTo) {
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
		params.put(Constants.TARGET_USER_ID_PARAM,uid);
		
		if("facebook".equalsIgnoreCase(connectTo))
			params.put(Constants.SOCIAL_NETWORK_PARAM,"facebook");
		else
			params.put(Constants.SOCIAL_NETWORK_PARAM,"linkedin");
		
		Status status = null;
		try {
			status = Utils.sendExternalConnectionRequest(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	public void ignoreRequest(final View view){
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.ignoringRequest), true);
		try{
    	    ignoreFriendRequest(view);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void ignoreFriendRequest(View view){
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.ignoringRequest), true);
		String timeZone=Utils.getTimeZone();
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
		params.put(Constants.TARGET_USER_ID_PARAM, uid);
		params.put(Constants.TIME_STAMP,timeZone);
		
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
        			setOtherProfileData(getOtherProfileData());
        		}
        	});
			handler.sendEmptyMessage(8);
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
	
	public void acceptRequest(View view){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_CONNECT;
		OtherProfileActivity.connectionActivity=Constants.ACCEPT_FRIEND_REQUEST;
		navigateChooseProfileScreen(view);
	}
	
	public void friendRequest(View view){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_CONNECT;
		OtherProfileActivity.connectionActivity=Constants.SEND_FRIEND_REQUEST;
		navigateChooseProfileScreen(view);
	}
	
	public void navigateChooseProfileScreen(View view){
		try	{  
	    	Intent chooseProfileIntent = new Intent(view.getContext(), ChooseProfileActivity.class);
	    	Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", uid);
	    	if("Ignored".equalsIgnoreCase(connectionStatus)){
	    		bundleObj.putString("ignored", "ignored");
			}
	    	chooseProfileIntent.putExtras(bundleObj);
	    	startActivityForResult(chooseProfileIntent, OTHER_PROFILE);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
    public void sendFriendRequest(){
		String shareProfileType2=ChooseProfileActivity.shareProfileType1;
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
		params.put(Constants.TARGET_USER_ID_PARAM, uid);
		params.put(Constants.PROFILE_TYPE_PARAM,shareProfileType2);
		
		Status status = null;
		try {
			status = Utils.sendFriendRequest(params, getApplicationContext());
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
        			declineMessage.setVisibility(View.GONE);
        		}
			});
			handler.sendEmptyMessage(9);
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

	public void hideConnect(){
		connectButton.setVisibility(View.GONE);
		hideButton.setVisibility(View.GONE);
		handler.sendEmptyMessage(9);
	}

	public void acceptFriendRequest(){
		String shareProfileType2=ChooseProfileActivity.shareProfileType1;
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
		params.put(Constants.TARGET_USER_ID_PARAM, uid);
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
			handler.sendEmptyMessage(7);
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
	
	public void navigateCommonLisnsScreen(View view){
		try{
			Intent commonLisns = new Intent(view.getContext(), CommonLisnActivity.class);
			Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", uid);
	    	bundleObj.putString("lisnId", lisn_Id);
	    	commonLisns.putExtras(bundleObj);
	    	startActivityForResult(commonLisns, 0);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		} catch(Exception ex){}
	}
	
	public void hideButtons(View view){
		if(buttonsGroup.getVisibility()==View.VISIBLE)
		{
			buttonsGroup.setVisibility(View.GONE);			
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_down_arrow)));
		} else {
			buttonsGroup.setVisibility(View.VISIBLE);
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_up_arrow)));
		}
		
		buttonsGroup.refreshDrawableState();
		listView.refreshDrawableState();	
	}
	
	public List<Friend> getFriendsList(){
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
		params.put("otherUserId",uid);
		
		List<Friend> friendsList = null;
		try {
				friendsList = Utils.getCommonFriendsList(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
			return null;
		}
		
		return friendsList;
	}
	
	public void displayFriends(List<Friend> friendList){
		List<Friend> friendsList = friendList;
		availableFriends = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> friendData = null;
		Friend friend = null;
        
		try{
			for(int i = 0 ; i < friendsList.size() ; i++){
				friend = friendsList.get(i);
        		friendData = new HashMap<String, Object>();
        		friendData.put(FRIENDS_NAME_KEY, friend.getFriendName());
        		friendData.put(IS_IMAGE_KEY, friend.getIsImage());
        		friendData.put(IDKEY, friend.getFriendID());
        		availableFriends.add(friendData);
			}
        
        	adapter = new ListAdapter(availableFriends,CommonFriendsActivity.this);
        	listView.setAdapter(adapter);
        	listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        	OnItemClickListener listener = new OnItemClickListener (){
        		@Override
        		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
        			OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_COMMON_FRIEND;
        			fromItemClickListener=true;
        			@SuppressWarnings("unchecked")
        			HashMap<String, Object> friend = (HashMap<String, Object>) adapter.getAdapter().getItem(position);
        			try	{   
        				Intent otherProfileIntent = new Intent(view.getContext(), OtherProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        				Bundle bundleObj = new Bundle();	
    		    		bundleObj.putString("lisnerId", (String)friend.get(IDKEY));
    		    		bundleObj.putString("commonFriendsUid",uid);
    		    		otherProfileIntent.putExtras(bundleObj);
    		    		view.getContext().startActivity(otherProfileIntent);
    		    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
        			} catch(Exception ex) {}
        		}
        	};
        	listView.setOnItemClickListener (listener);
		}catch(Exception e){
			handler.sendEmptyMessage(6);
			return;
		}
	}	
	
	public synchronized Bitmap getOtherUserImage(String otherUserId){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, otherUserId);
		
		try {
			bmImg = Utils.getOtherPersonImage(params, CommonFriendsActivity.this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(1);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
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
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return null;
	}

	public void refreshCommonFriendsList(){
		try{
			runOnUiThread(new Runnable() {
				public void run() {
        			List<Friend> dataExist = getFriendsList();
                    if((dataExist != null) && (dataExist.size() != 0)){
                    	listView.setVisibility(View.VISIBLE);
                    	noCommonFriendsYetText.setVisibility(View.GONE);
            			displayFriends(dataExist);
            		} else{
            			listView.setVisibility(View.GONE);
            			noCommonFriendsYetText.setVisibility(View.VISIBLE);
                    }
				}
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_COMMON_FRIEND;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_COMMON_FRIEND;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("uid", uid);
	    	peopleNearByIntent.putExtras(bundleObj);
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
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
				CustomToast.showCustomToast(getApplicationContext(), Constants.LOCATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 6){
				CustomToast.showCustomToast(getApplicationContext(), Constants.NO_FRIENDS_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 7){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_ACCEPT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 8){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_IGNORED_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 9){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_SENT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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
		private ArrayList<HashMap<String, Object>> allFriends; 
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<HashMap<String, Object>> allFriends, Context context){
			this.allFriends = allFriends;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return allFriends.size();
		}

		@Override
		public Object getItem(int position) {
			return allFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			FriendsViewHolder holder;
			
			if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
	             convertView = mInflater.inflate(R.layout.friend_list_view, null);
	             
	             holder = new FriendsViewHolder();
	             holder.friendName = (TextView) convertView.findViewById(R.id.friendName);
	             holder.friendImage = (ImageView) convertView.findViewById(R.id.friendImage);
	             convertView.setTag(holder);
			 }else {
				 holder = (FriendsViewHolder) convertView.getTag(); 
			 }
			 
			holder.friendName.setText((String) allFriends.get(position).get(CommonFriendsActivity.FRIENDS_NAME_KEY));
			String otherId=(String) allFriends.get(position).get(CommonFriendsActivity.IDKEY);
			String isImage=(String) allFriends.get(position).get(CommonFriendsActivity.IS_IMAGE_KEY);
			
			if("true".equalsIgnoreCase(isImage)){
				downloadImage(otherId, holder.friendImage, position);
			}
			return convertView;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == OTHER_PROFILE) {
	        if (resultCode == RESULT_OK) {
	        	uid = data.getStringExtra("uid");
	        	
	        	if(Constants.SEND_FRIEND_REQUEST.equalsIgnoreCase(OtherProfileActivity.connectionActivity)){
	        		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.sendingRequest), true);
	        		try{
	        			sendFriendRequest();
	        		} catch(Exception e){
	        			e.printStackTrace();
	        		}
	        	}
	        	if(Constants.ACCEPT_FRIEND_REQUEST.equalsIgnoreCase(OtherProfileActivity.connectionActivity)){
	        		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.acceptingRequest), true);
	        		try{
	        			acceptFriendRequest();
	        		} catch(Exception e){
	        			e.printStackTrace();
	        		}
	        	}
	        	
	        	runOnUiThread(new Runnable() {
	        		public void run() {
	        			setOtherProfileData(getOtherProfileData());
	        		}
	        	});
	        } 
	    }
	}
}