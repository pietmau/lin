package com.lisnx.android.activity;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.MessageDetail;
import com.lisnx.model.OtherProfile;
import com.lisnx.model.OtherProfileViewHolder;
import com.lisnx.model.PrivateMessage;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.service.TimerStopper;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.util.AppCrashReports;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class OtherProfileActivity extends BaseActivity implements OnClickListener{
	
	public String uid = null;
	public String lisnId = null;
	public View tempView=null;
	public static String targetUserId = null;
	private ImageButton connectButton;
	private ImageButton acceptButton;
	private Button sendButton;
	private ArrayList <HashMap<String, Object>> availableMessage=new ArrayList<HashMap<String,Object>>();
	public ListAdapter adapter = null;
	public static ListView listView = null;
	private ImageButton ignoreButton;
	public static String callingOtherProfileScreen=null;
	public static String connectionActivity=null;
	
	private TextView commonFriendsText;
	private TextView commonLisnsText;
	private ImageView commonFriendsImage;
	private ImageView commonLisnsImage;
	private ImageView profileFacebookImage;
	private TextView facebookConnectedText;
	private ImageView profileLinkedInImage;
	private String isNearBy;
	private String isImage;
	Bitmap otherProfileFullBitmapImage;
	private TextView userEmail;
	private int imageWidth=0;
	private int imageHeight=0;
	public Thread loaderThread=null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	private String commonFriendsUid=null;			
	public Timer otherProfileTimer;
	public Bitmap otherProfileImage=null;
	public final String RECEIVER_KEY = "receiver";
	public static final String SENDER_KEY = "sender";
	public static final String CONTENT_KEY = "content";
	public static final String DATE_CREATED_KEY = "dateCreated";
	private ImageButton hideButton;
	private TextView declineMessage=null;
	private LinearLayout buttonsGroup;
	private LinearLayout socialButtonsGroup;
	private int msgCount=0;
	private String connectionStatus;
	TextView connectOnText ;
	public static final int OTHER_PROFILE = 1;
	
	
	private ImageView connectedOnFacebookImage;
	private ImageView connectedOnLinkedinImage;
	private ImageView connectedOnLisnxImage;
	private TextView connectedOnText;
	private OtherProfile otherProfile;
	private LinearLayout sendMessageLayout;
	private LinearLayout inviteLayout;
	private EditText inviteMessage;
	private TextView invitationTextView;
	private Button inviteButton;
	private ImageView isNearByIcon;
	private Button notifyButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	uid = extras.getString("lisnerId");
        	lisnId = extras.getString("lisnId");
        	targetUserId=extras.getString("lisnerId");
        	commonFriendsUid=extras.getString("commonFriendsUid");		
        }
        
        setContentView(R.layout.other_profile_new);
        //VISH
        if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"OtherProfileActivity called....");

        layoutInflater = this.getLayoutInflater();
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        connectedOnFacebookImage = (ImageView) findViewById(R.id.connectedOnFacebookImage) ;
        connectedOnLinkedinImage = (ImageView) findViewById(R.id.connectedOnLinkedinImage) ;
        connectedOnLisnxImage = (ImageView) findViewById(R.id.connectedOnLisnxImage) ;
        connectedOnText = (TextView)findViewById(R.id.connectedOnText);
       
        
        setListenersOnTopIcons();
        declineMessage=(TextView) findViewById(R.id.declineMessage);
        
        connectButton = (ImageButton) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(this);
        acceptButton = (ImageButton) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(this);
        sendButton = (Button) findViewById(R.id.lisnxTextButton);
        sendButton.setOnClickListener(this);
        listView = (ListView)findViewById(R.id.privateMessageList);
        ignoreButton = (ImageButton) findViewById(R.id.ignoreButton);
        ignoreButton.setOnClickListener(this);
        hideButton = (ImageButton) findViewById(R.id.hideButton);
        hideButton.setOnClickListener(this);
        
        nameText = (TextView) findViewById(R.id.userName);
        
        commonFriendsText = (TextView) findViewById(R.id.commonFriendsCountText);
        commonLisnsText = (TextView) findViewById(R.id.commonLisnCountText);
        connectOnText = (TextView) findViewById(R.id.connectOnMessage);
        commonFriendsImage=(ImageView)findViewById(R.id.friendsImage);
        commonFriendsImage.setOnClickListener(this);
        commonLisnsImage=(ImageView)findViewById(R.id.lisnImage);
        commonLisnsImage.setOnClickListener(this);
        userEmail = (TextView)findViewById(R.id.userEmail);
        
        profileFacebookImage=(ImageView) findViewById(R.id.profileFacebookImage);
        profileFacebookImage.setOnClickListener(this);
        facebookConnectedText = (TextView) findViewById(R.id.facebookConnectedText);
        profileLinkedInImage=(ImageView) findViewById(R.id.profileLinkedInImage);
        profileLinkedInImage.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        
        adapter = new ListAdapter(availableMessage,OtherProfileActivity.this);		
        listView.setAdapter(adapter);
        socialButtonsGroup = (LinearLayout) findViewById(R.id.socialButtonsLayout);
        buttonsGroup = (LinearLayout) findViewById(R.id.OPLL9);  
        inviteLayout = (LinearLayout) findViewById(R.id.inviteLayout);
        sendMessageLayout = (LinearLayout) findViewById(R.id.sendMessageLayout);
        inviteMessage = (EditText)findViewById(R.id.inviteMessage);
        invitationTextView = (TextView)findViewById(R.id.invitationTextView);
        inviteButton = (Button)findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(this);
        isNearByIcon = (ImageView) findViewById(R.id.nearbyIndicator);
        notifyButton = (Button)findViewById(R.id.notifyButton);
        notifyButton.setOnClickListener(this);
        isNearByIcon.setVisibility(View.VISIBLE);
        
        //Doesn't work
        //setGlowEffect();
		
        
        // Hook up clicks on the thumbnail views.

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(userImage, otherProfileFullBitmapImage, R.id.otherProfileLayout);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetOtherProfileData getOtherProfileData = new GetOtherProfileData();
        			getOtherProfileData.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(13);
        	e.printStackTrace();
        }
        
        
        
    
        
      
	}
	private static final int GLOW_ANIM_DURATION = 10000;
    
	private void setGlowEffect(){
		    final ObjectAnimator objAnim =
	                ObjectAnimator.ofObject(sendButton,
	                                        "backgroundColor", // we want to modify the backgroundColor
	                                        new ArgbEvaluator(), // this can be used to interpolate between two color values
	                                        R.color.white, // start color defined in resources as #ff333333
	                                        R.color.black // end color defined in resources as #ff3355dd
	                );
	        objAnim.setDuration(GLOW_ANIM_DURATION / 2);
	        objAnim.setRepeatMode(ValueAnimator.REVERSE); // start reverse animation after the "growing" phase
	        objAnim.setRepeatCount(ValueAnimator.INFINITE);
	        objAnim.start();
	   
	}
	

	@Override
    protected void onPause() {
        super.onPause();
        try{
        	AppCrashReports.activityPaused();
        	TimerStopper.stopTimers();
        	if(otherProfileTimer != null){
        		otherProfileTimer.cancel();
        	}
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        AppCrashReports.activityResumed();
        try{
        	toCallAsynchronous();
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
	
	private class GetOtherProfileData extends AsyncTask<Void, Void, OtherProfile>{
		public GetOtherProfileData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(OtherProfileActivity.this, null, OtherProfileActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected OtherProfile doInBackground(Void... params) {
			return getOtherProfileData();
		}
		
		@Override
        protected void onPostExecute(OtherProfile otherProfile){
			setOtherProfileData(otherProfile);
			handler.sendEmptyMessage(13);
		}
	}
	
	
	
	public void setOtherProfileData(OtherProfile otherProfile){
		if(otherProfile != null){
			this.otherProfile = otherProfile;
			try{
				buttonsGroup.setVisibility(View.VISIBLE);
				connectionStatus=otherProfile.getConnectionStatus();
				if(Constants.CONNECTION_STATUS_CONNECTED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
					connectButton.setClickable(false);
					connectButton.setVisibility(View.GONE);
					acceptButton.setClickable(false);
					acceptButton.setVisibility(View.GONE);
					ignoreButton.setClickable(false);
					ignoreButton.setVisibility(View.GONE);
					declineMessage.setVisibility(View.GONE);
					if(!otherProfile.isConnectedOnLinkedin()){
						profileLinkedInImage.setVisibility(View.VISIBLE);
						facebookConnectedText.setVisibility(View.VISIBLE);
					}
					if(!otherProfile.isConnectedOnFacebook()){
						profileFacebookImage.setVisibility(View.VISIBLE);
						facebookConnectedText.setVisibility(View.VISIBLE);
					}
					buttonsGroup.setVisibility(View.GONE);
					if(otherProfile.isLisnxUser())
						socialButtonsGroup.setVisibility(View.VISIBLE);
				}
				if(Constants.CONNECTION_STATUS_ACCEPT.equalsIgnoreCase(otherProfile.getConnectionStatus())){
					connectButton.setClickable(false);
					connectButton.setVisibility(View.GONE);
					socialButtonsGroup.setVisibility(View.GONE);
				}
				if(Constants.CONNECTION_STATUS_PENDING.equalsIgnoreCase(otherProfile.getConnectionStatus())){
					acceptButton.setClickable(false);
					acceptButton.setVisibility(View.GONE);
					ignoreButton.setClickable(false);
					ignoreButton.setVisibility(View.GONE);
				
					declineMessage.setVisibility(View.VISIBLE);
					declineMessage.setText("Your friend request is pending.");
					connectButton.setVisibility(View.VISIBLE);
					socialButtonsGroup.setVisibility(View.GONE);
				}
				if(Constants.CONNECTION_STATUS_NOT_CONNECTED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
					acceptButton.setClickable(false);
					acceptButton.setVisibility(View.GONE);
					ignoreButton.setClickable(false);
					ignoreButton.setVisibility(View.GONE);
					socialButtonsGroup.setVisibility(View.GONE);
				}
				if(Constants.CONNECTION_STATUS_IGNORED.equalsIgnoreCase(otherProfile.getConnectionStatus())){
					acceptButton.setClickable(false);
					acceptButton.setVisibility(View.GONE);
					ignoreButton.setClickable(false);
					ignoreButton.setVisibility(View.GONE);
					socialButtonsGroup.setVisibility(View.GONE);
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
				isImage=otherProfile.getIsImage();
			
				if("true".equalsIgnoreCase(isImage)){
					download(uid,userImage);
				}
			
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
			
				isNearBy=otherProfile.getIsNearBy();
				
				if("true".equalsIgnoreCase(isNearBy)){
						isNearByIcon.setVisibility(View.VISIBLE);
						isNearByIcon.setBackgroundResource(R.drawable.ic_online_icon2);
				}
				else {
					notifyButton.setVisibility(View.VISIBLE);
				}
				if(!otherProfile.isLisnxUser()){
					listView.setVisibility(View.GONE);
					sendMessageLayout.setVisibility(View.GONE);
					if(otherProfile.wasInvited()){
						inviteMessage.setVisibility(View.GONE);
						invitationTextView.setVisibility(View.VISIBLE);
						inviteButton.setVisibility(View.GONE);
					}else {
						invitationTextView.setVisibility(View.GONE);
						inviteMessage.setText(otherProfile.getFullname().split("\\s+")[0] + ", check out LISNx app. You can find friends nearby, connect with people you meet instantly. Download the app today!");
					}
					//sendMessageLayout.refreshDrawableState();
					inviteLayout.setVisibility(View.VISIBLE);
					socialButtonsGroup.setVisibility(View.GONE);
					
				}
				else {
					listView.setVisibility(View.VISIBLE);
					sendMessageLayout.setVisibility(View.VISIBLE);
					socialButtonsGroup.setVisibility(View.VISIBLE);
					inviteLayout.setVisibility(View.GONE);
				}
				int socialConnectionCount = 0;
				if(otherProfile.isConnectedOnFacebook()){
					connectedOnFacebookImage.setVisibility(View.VISIBLE);
					socialConnectionCount++;
				}
				if(otherProfile.isConnectedOnLinkedin()){
					connectedOnLinkedinImage.setVisibility(View.VISIBLE);
					socialConnectionCount++;
				}
				if(otherProfile.isConnectedOnLisnx()){
					connectedOnLisnxImage.setVisibility(View.VISIBLE);
					socialConnectionCount++;
				}
				if(socialConnectionCount >0){
					connectedOnText.setVisibility(View.VISIBLE);
				}
				toCallAsynchronous();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public OtherProfile getOtherProfileData(){
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			//handler.sendEmptyMessage(1);
			return null;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			//handler.sendEmptyMessage(1);
			return null;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		
		if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingOtherProfileScreen)){
			params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			params.put(Constants.USER_ID_PARAM, uid);
			params.put(Constants.LISN_ID_PARAM_2, lisnId);
		}
		else if(Constants.CALLING_SCREEN_PEOPLE_NEARBY.equalsIgnoreCase(callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_FRIENDS.equalsIgnoreCase(callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_COMMON_FRIEND.equalsIgnoreCase(callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_CHOOSE_PROFILE.equalsIgnoreCase(callingOtherProfileScreen)||
				Constants.CALLING_SCREEN_NOTIFICATIONS.equalsIgnoreCase(callingOtherProfileScreen)){
			params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			params.put(Constants.OTHER_USER_ID_PARAM, uid);
		}
		
		try {
			otherProfile = Utils.getOtherProfileData(params, this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		return otherProfile;
	}
	
	public void toCallAsynchronous() {
	    TimerTask doAsynchronousTask;
	    final Handler handler = new Handler();
	            doAsynchronousTask = new TimerTask() {
	                @Override
	                public void run() {
	                    handler.post(new Runnable() {
	                        public void run() {
	                            try {
	                            	getPrivateMessages();
	                            } catch (Exception e){}
	                        }
	                    });
	                }
	            };
	            if(otherProfile!=null && otherProfile.isLisnxUser()){
	            		otherProfileTimer = new Timer();
	            		otherProfileTimer.schedule(doAsynchronousTask, 0, Constants.REFRESH_ITERATION_INTERVAL);
	            }
	}
	 public void download(String otherId, ImageView imageView) {
		 BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		 task.execute(otherId);
     }
	 
	 class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		 	@SuppressWarnings("unused")
			private String otherId;
		    private final WeakReference<ImageView> imageViewReference;

		    public BitmapDownloaderTask(ImageView imageView) {
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
		        	//bitmap = 
		        	if (imageViewReference != null) {
		        		ImageView imageView = imageViewReference.get();
		        		if(imageView != null ){
		        			imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
		        		} else {
		        			imageView=(ImageView)findViewById(R.id.userImage);
		        			imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
		        		}
		            }
		        }else {
	        		ImageView imageView = imageViewReference.get();
	        		if(imageView != null ){
	        			imageView.setImageBitmap(defaultBitmap);
	        		} else{
	        			imageView=(ImageView)findViewById(R.id.userImage);
	        			imageView.setImageBitmap(defaultBitmap);
	        		}
		        }
		    }
		}
	
	public Bitmap getImage(String uid){
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.USER_ID_PARAM, uid);
		try {
			otherProfileFullBitmapImage = Utils.getOtherPersonImage(params, OtherProfileActivity.this);
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
		
        if(otherProfileFullBitmapImage==null){
        	handler.sendEmptyMessage(4);
        	return null;
        }else {
        	otherProfileImage = Utils.scaleProportionally(otherProfileFullBitmapImage,imageWidth, imageHeight);
        			//Bitmap.createScaledBitmap(otherProfileFullBitmapImage,imageWidth, imageHeight, true);
        	return otherProfileImage;
        }
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.connectButton:
			   	try{
       				sendFriendRequest();
       			} catch(Exception e){
       				e.printStackTrace();
       			}
			   break;
		   case R.id.acceptButton:
			   	try{
       				acceptFriendRequest();
	       		} catch(Exception e){
	       			e.printStackTrace();
	       		}
			   	runOnUiThread(new Runnable() {
	        		public void run() {
	        			setOtherProfileData(getOtherProfileData());
	        		}
			   	});
			   break;
		   case R.id.hideButton:
			   hideButtons(view);
			   break;
		   case R.id.lisnxTextButton:
			   postMessage(view);
			   break;
		   case R.id.ignoreButton:
			   ignoreRequest(view);
			   break;
		  case R.id.friendsImage:
			   if(otherProfileTimer != null){
				   otherProfileTimer.cancel();
			   }
			   navigateCommonFriendsScreen(view);
			   break;
		   case R.id.lisnImage:
			   if(otherProfileTimer != null){
				   otherProfileTimer.cancel();
			   }
			   navigateCommonLisnsScreen(view);
			   break;
		   case R.id.profileFacebookImage:
			   sendExternalConnectionRequest(view,"facebook");
			   break;
		   case R.id.profileLinkedInImage:
			   sendExternalConnectionRequest(view,"linkedin");
			   break;
		   case R.id.inviteButton:
			   sendInvitation(view);
			   break;
		   case R.id.notifyButton:
			   sendNearbyNotificationRequest(view);
			   break;
		}
	}
	
	private void sendNearbyNotificationRequest(View view) {
		if(otherProfile.getNotificationRequestStatus()==null 
				|| OtherProfile.NOTIFICATION_REQUEST_STATUS.NONE.equals(otherProfile.getNotificationRequestStatus())){
			Map params = getParamsMapWithTokenAndTimeZone(handler);
			params.put("otherProfileId", otherProfile.getId());
			notifyButton.setText("Notification request sent");
			Status status = null;
			try {
				status = Utils.sendNearbyNotificationRequest(params, this.getApplicationContext());
			} catch (NullPointerException e) {
				handler.sendEmptyMessage(12);
			} catch (JSONException e) {
				handler.sendEmptyMessage(11);
			}
			if(status == null){
				handler.sendEmptyMessage(1);
			}
			else if(Constants.SUCCESS.equals(status.getStatus())){
			}
			}
	}




	private Status sendInvitation(View view) {
		Map<String, String> params = getParamsMapWithTokenAndTimeZone(handler);
		//inviteFriends?channel=FACEBOOK&inviteeList=fabc,asd&invitationMessage=welcometolisnx&token=
		params.put(Constants.CHANNEL,"FACEBOOK");
		params.put(Constants.INVITEE_LIST,otherProfile.getId());
		params.put(Constants.INVITATION_MESSAGE,inviteMessage.getText().toString());
		Status status = null;
		
		try {
			status = Utils.sendInvitation(params, this.getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		if(status == null){
			handler.sendEmptyMessage(1);
		}
		else if(Constants.SUCCESS.equals(status.getStatus())){
			inviteMessage.setVisibility(View.GONE);
			invitationTextView.setVisibility(View.VISIBLE);
			otherProfile.setInvited(true);
			inviteButton.setText("Invited");
			inviteButton.setBackgroundResource(R.drawable.green_rectangle);
		}
		return status;
		
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
		
		if("facebook".equalsIgnoreCase(connectTo)){
			params.put(Constants.SOCIAL_NETWORK_PARAM,"facebook");
		} else{
			params.put(Constants.SOCIAL_NETWORK_PARAM,"linkedin");
		}
		
		Status status = null;
		try {
			status = Utils.sendExternalConnectionRequest(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		if(status != null){
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		} else{
			handler.sendEmptyMessage(1);
		}
	}

	public void acceptRequest(View view){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_CONNECT;
		connectionActivity=Constants.ACCEPT_FRIEND_REQUEST;
		navigateChooseProfileScreen(view);
	}
	
	private void postMessage(View view) {	
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
		
		postPrivateMessage(fullMessage);
		
	}
	
	public void postPrivateMessage(final String fullMessage){
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
		params.put(Constants.MESSAGE_RECEIVER_PARAM, uid);
		params.put(Constants.MESSAGE_CONTENT_PARAM, fullMessage);
		params.put(Constants.TIME_STAMP,timeZone);
		
		@SuppressWarnings("unused")
		MessageDetail messageDetail = null;
		try {
			messageDetail = Utils.postMessage(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		printPostedMsg(fullMessage);			
	}
	
	private void printPostedMsg(String fullMessage) {
		HashMap<String, Object> msgData = new HashMap<String, Object>();
    	msgData.put(DATE_CREATED_KEY, Constants.FEW_SECONDS_AGO);
    	msgData.put(CONTENT_KEY, fullMessage);
    	msgData.put(SENDER_KEY, "00");
    	msgData.put(RECEIVER_KEY, "00");
    	availableMessage.add(msgData);
    	
    	runOnUiThread(new Runnable() {
            public void run() {
            	adapter.notifyDataSetChanged();
            }
		});
	}

	public List<PrivateMessage> getPrivateMessages(){				
		String timeZone = Utils.getTimeZone();
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
		
		Map<String , String> params1 = new HashMap<String,String>();
		params1.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params1.put(Constants.MESSAGE_RECEIVER_PARAM, uid);
		params1.put(Constants.TIME_STAMP, timeZone);  
		
		List<PrivateMessage> privateMessage = null;
		try {
			privateMessage = Utils.getPrivateMessage(params1, this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		if(privateMessage != null){
			displayPrivateMessage(privateMessage);
		}
		return privateMessage;
	}
	
	public void displayPrivateMessage(List<PrivateMessage> privateMessage){
		List<PrivateMessage> messageList = privateMessage;
		availableMessage.clear();
        HashMap<String, Object> msgData = null;
        PrivateMessage msg = null;
        
        for(int i = 0 ; i < messageList.size() ; i++){
        	msg = messageList.get(i);
        	msgData = new HashMap<String, Object>();
        	msgData.put(DATE_CREATED_KEY, msg.getDateCreated());
        	msgData.put(CONTENT_KEY, msg.getContent());
        	msgData.put(SENDER_KEY, msg.getSender());
        	msgData.put(RECEIVER_KEY, msg.getReceiver());
        	availableMessage.add(msgData);
        }
        
        if(msgCount!=privateMessage.size()){
        	adapter.notifyDataSetChanged();
        	msgCount=privateMessage.size();
        }     
	}
	
	public void acceptFriendRequest(){
		String shareProfileType2=Constants.PROFILE_SHARE_ALL;//ChooseProfileActivity.shareProfileType1;
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
			finish();
			startActivity(getIntent());
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
	
	public void ignoreRequest(final View view){
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.ignoringRequest), true);
		try{
    	    ignoreFriendRequest(view);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void ignoreFriendRequest(View view){
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
		params.put(Constants.TARGET_USER_ID_PARAM, targetUserId);
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
	
	public void friendRequest(View view){
		ChooseProfileActivity.callingScreen=Constants.CALLING_SCREEN_CONNECT;
		connectionActivity=Constants.SEND_FRIEND_REQUEST;
		navigateChooseProfileScreen(view);
	}
	
	public void hideButtons(View view){
		LinearLayout connectionLayout = (LinearLayout) findViewById(R.id.connectionLayout);
		if(connectionLayout!=null){
			if(connectionLayout.getVisibility()==View.VISIBLE)
				connectionLayout.setVisibility(View.GONE);
			else connectionLayout.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void hideConnect(){
		connectButton.setVisibility(View.GONE);
		handler.sendEmptyMessage(2);
	}
	
	public void sendFriendRequest(){
		String shareProfileType2=Constants.PROFILE_SHARE_ALL;//ChooseProfileActivity.shareProfileType1;
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
        			declineMessage.setVisibility(View.VISIBLE);
        			declineMessage.setText("Your friend request is pending.");
        			connectButton.setVisibility(View.VISIBLE);
        		}
			});
			handler.sendEmptyMessage(2);
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
	public View onCreateView(String name, Context context,AttributeSet attrs) {	
		return null;
	}

	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_OTHER_PROFILE;
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
	
	
	public void navigateFriendsScreen(View view){
		try	{   		
	    	Intent friendsIntent = new Intent(view.getContext(), FriendsActivity.class);	
	    	startActivityForResult(friendsIntent, 0);
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
	
	public void navigateCommonFriendsScreen(View view){
		try{
			Intent commonFriends = new Intent(view.getContext(), CommonFriendsActivity.class);
			Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", uid);
	    	bundleObj.putString("lisnId", lisnId);
	    	commonFriends.putExtras(bundleObj);
	    	startActivityForResult(commonFriends, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		} catch(Exception ex){}
	}
	
	public void navigateCommonLisnsScreen(View view){
		try{
			Intent commonLisns = new Intent(view.getContext(), CommonLisnActivity.class);
			Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", uid);
	    	bundleObj.putString("lisnId", lisnId);
	    	commonLisns.putExtras(bundleObj);
	    	startActivityForResult(commonLisns, 0);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		} catch(Exception ex){}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 2){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_SENT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 3){
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 4){
				CustomToast.showCustomToast(getApplicationContext(), Constants.PROBLEM_GETTING_IMAGE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 5){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_ACCEPT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 6){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_IGNORED_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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
			else if(msg.what == 13){
				if(pd != null)
				pd.dismiss();
			}
		}
	};
	
	private class ListAdapter extends BaseAdapter{
		private ArrayList<HashMap<String, Object>> messages; 
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<HashMap<String, Object>> messages, Context context){
			this.messages = messages;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			OtherProfileViewHolder holder;
			String otherId=(String) messages.get(position).get(OtherProfileActivity.SENDER_KEY);
			String text=(String) messages.get(position).get(OtherProfileActivity.CONTENT_KEY);
			
			if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				 if(OtherProfileActivity.targetUserId != null && OtherProfileActivity.targetUserId.equalsIgnoreCase(otherId))
					 convertView = mInflater.inflate(R.layout.other_profile_private_message, null);
			     else
			    	 convertView = mInflater.inflate(R.layout.other_profile_private_message_reverse, null);
				 
	             holder = new OtherProfileViewHolder();
	             holder.lisnerMessage = (TextView) convertView.findViewById(R.id.messageName);
	             holder.lisnerImage = (ImageView) convertView.findViewById(R.id.lisnerImage);
	             holder.messageDate = (TextView) convertView.findViewById(R.id.txtDate);
	             convertView.setTag(holder);
			 } else {
				 holder = (OtherProfileViewHolder) convertView.getTag();
			 }
		 	
		 	if(OtherProfileActivity.targetUserId != null && OtherProfileActivity.targetUserId.equalsIgnoreCase(otherId)) {
		 		if(otherProfileImage != null) {
		 			holder.lisnerImage.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(otherProfileImage));
		 		} else {
		 			//holder.lisnerImage.setImageBitmap(NowLisnActivity.defaultBitmap);
		 		}
	    	} else {
		 		//if(NowLisnActivity.permanentImage != null) {
		 		//	holder.lisnerImage.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(NowLisnActivity.permanentImage));
		 		//}
		 	}
		 	
		 	String date;
		 	date=(String) messages.get(position).get(OtherProfileActivity.DATE_CREATED_KEY);
        	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   		try {
	   			if(!(date.equalsIgnoreCase(Constants.FEW_SECONDS_AGO))){
	   				Date myDate = format.parse(date);
	   				date=new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(myDate);
	   			}
	   		} catch (ParseException e) {
	   			e.printStackTrace();
	   		}
		 	
	   		holder.messageDate.setText(date);
		 	holder.lisnerMessage.setText(text);
			return convertView;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == OTHER_PROFILE) {
	        if (resultCode == RESULT_OK) {
	        	uid = data.getStringExtra("lisnerId");
	        	
	        	if(Constants.SEND_FRIEND_REQUEST.equalsIgnoreCase(connectionActivity)){
	        		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.sendingRequest), true);
	        		try{
	        			sendFriendRequest();
	        		} catch(Exception e){
	        			e.printStackTrace();
	        		}
	        	}
	        	if(Constants.ACCEPT_FRIEND_REQUEST.equalsIgnoreCase(connectionActivity)){
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