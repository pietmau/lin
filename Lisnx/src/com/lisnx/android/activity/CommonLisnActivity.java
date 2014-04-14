package com.lisnx.android.activity;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.event.ILisner;
import com.lisnx.model.CommonLisn;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.OtherProfile;
import com.lisnx.model.Status;
import com.lisnx.model.ViewHolder;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class CommonLisnActivity extends BaseActivity implements ILisner,OnClickListener,Runnable{
	
	public static ArrayList<String> lisnId = new ArrayList<String>();
	public String longitude;
	public String lattitude;
	public String uid=null;
	
	@SuppressWarnings("unused")
	private EventNotifier eventNotifier;
	private ArrayList <HashMap<String, Object>> availableLisn;
	public static final String LISN_DETAIL_KEY = "lisnDetail";
	public static final String LISNER_COUNT_KEY = "lisnerCount";
	public static final String LISN_KEY = "lisnStatus";
	public static final String LISN_NAME_KEY = "lisnName";
	public static final String CREATED_KEY = "created";
	public static final String IDKEY = "id";
	public static final String TOTAL_MESSAGE_KEY = "totalMessage";
	public static ListView listView = null;
	public boolean addHeader = true;
	public ListAdapter adapter = null;
	public View tempView=null;
	public boolean noLisn=false;
	
	Dialog profileDialog;
	public boolean fullProfile=false;
	public boolean casualProfile=false;
	public boolean professionalProfile=false;
	public boolean emailOnlyProfile=false;
	public String shareProfileType=null;
	public boolean flag=false;
	public static final int NOW_LISN = 0;
	private int imageWidth=0;
	private int imageHeight=0;
	public static Bitmap permanentImage=null;
	public static String permanentName=null;
	public static String permanentEmail=null;
	
	public static String peopleCount=null;
	public static String notificationsCount=null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	private ImageButton connectButton;
	private ImageView backIcon;
	private ImageButton acceptButton;
	private ImageButton ignoreButton;
	private ImageButton hideButton;
	private ImageView commonFriendsImage;
	@SuppressWarnings("unused")
	private ImageView commonLisnsImage;
	private TextView commonFriendsText;
	private TextView commonLisnsText;
	private ImageView profileFacebookImage;
	private ImageView profileLinkedInImage;
	private TextView declineMessage=null;
	private LinearLayout buttonsGroup;
	
	private String connectionStatus;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ;
	private TextView noCommonLisnsYetText;
	public String lisn_Id=null;
	public static final int OTHER_PROFILE = 4;
	public Bitmap otherProfileImage=null;
	TextView connectOnText ;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	uid = extras.getString("uid");
        	lisn_Id = extras.getString("lisnId");
        }
		        
        setContentView(R.layout.common_lisn_list);
        layoutInflater = this.getLayoutInflater();
        eventNotifier = new EventNotifier (this);
        
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        
        peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        noCommonLisnsYetText=(TextView) findViewById(R.id.noCommonLisnsYetBox);
        connectOnText = (TextView) findViewById(R.id.connectOnMessage);
       
        TextView listIntroductionText=(TextView) findViewById(R.id.listIntroText);
        listIntroductionText.setText(R.string.commonLisns);
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
        commonFriendsImage.setOnClickListener(this);
        commonLisnsImage=(ImageView)findViewById(R.id.lisnImage);
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
        			GetCommonLisnsData getCommonLisnsData = new GetCommonLisnsData();
        			getCommonLisnsData.execute();
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
	
	private class GetCommonLisnsData extends AsyncTask<Void, Void, List<CommonLisn>>{
		public GetCommonLisnsData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(CommonLisnActivity.this, null, CommonLisnActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected List<CommonLisn> doInBackground(Void... params) {
			return getCommonLisn();
		}
		
		@Override
        protected void onPostExecute(List<CommonLisn> commonLisnsData){
			List<CommonLisn> dataExist = commonLisnsData;
            if((dataExist!=null) && (!(dataExist.isEmpty()))){
            	displayCommonLisns(dataExist);
    		} else{
    			listView.setVisibility(View.GONE);
				noCommonLisnsYetText.setVisibility(View.VISIBLE);
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
				connectionStatus=otherProfile.getConnectionStatus();
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
					download(uid,userImage);
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
			//handler.sendEmptyMessage(1);
			return null;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			//handler.sendEmptyMessage(1);
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
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		return otherProfile;
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
		
	    Bitmap bmImg = null;
		try {
			bmImg = Utils.getOtherPersonImage(params, CommonLisnActivity.this);
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
	
	public void displayCommonLisns(List<CommonLisn> lisnsList){
		List<CommonLisn> lisnList = lisnsList;
		availableLisn = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> lisnData = null;
        CommonLisn lisn = null;
        
        for(int i = 0 ; i < lisnList.size() ; i++){
        	lisn = lisnList.get(i);
        	lisnData = new HashMap<String, Object>();
        	 
           	if(lisn.getMember().equalsIgnoreCase("null")){
        		lisnData.put(LISNER_COUNT_KEY,  "0" );
        	}else{
        	    lisnData.put(LISNER_COUNT_KEY, lisn.getMember()+"");
        	}
        	lisnData.put(IDKEY, lisn.getLisnID());
        	lisnData.put(LISN_NAME_KEY, lisn.getName());
        	lisnData.put(TOTAL_MESSAGE_KEY, lisn.getTotalMessage());
        	lisnData.put(LISN_DETAIL_KEY, lisn.getDescription());
        	
        	availableLisn.add(lisnData);
        }
        
        adapter = new ListAdapter(availableLisn,CommonLisnActivity.this);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        OnItemClickListener listener = new OnItemClickListener (){
    		@Override
    		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> lisn = (HashMap<String, Object>) adapter.getAdapter().getItem(position);
    			try	{   
            		Intent lisnDetailIntent = new Intent(view.getContext(), LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    	Bundle bundleObj = new Bundle();	
    		    	bundleObj.putString("id", (String)lisn.get(IDKEY));
    		    	bundleObj.putString("RSVP","In");		
    		    	lisnDetailIntent.putExtras(bundleObj);
    		    	view.getContext().startActivity(lisnDetailIntent);
    		    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	    	} catch(Exception ex) {
    	    	}
    		}
        };
        listView.setOnItemClickListener (listener);
	}
	
	public List<CommonLisn> getCommonLisn(){
		String timeZone=Utils.getTimeZone();
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
		params.put(Constants.TIME_STAMP,timeZone);
		params.put("otherUserId",uid);

		List<CommonLisn> lisnList = null;
		try {
			lisnList = Utils.getCommonLisn(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}
		
		return lisnList;
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
			}else if(msg.what == 6){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_IGNORED_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}else if(msg.what == 7){
				CustomToast.showCustomToast(getApplicationContext(), Constants.LISN_JOINED_ALREADY_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 8){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_SENT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 9){
				CustomToast.showCustomToast(getApplicationContext(), Constants.PROBLEM_GETTING_IMAGE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 10){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REQUEST_ACCEPT_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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

	@Override
	public void run() {}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   /*case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;*/
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   navigateFriendRequestScreen(view);
			   break;
		   case R.id.friendsImage:
			   navigateCommonFriendsScreen(view);
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
		   case R.id.hideButton:
			   hideButtons(view);
			   break;
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

	public void navigateCommonFriendsScreen(View view){
		try{
			Intent commonFriends = new Intent(view.getContext(), CommonFriendsActivity.class);
			Bundle bundleObj = new Bundle();
	    	bundleObj.putString("uid", uid);
	    	bundleObj.putString("lisnId", lisn_Id);
	    	commonFriends.putExtras(bundleObj);
	    	startActivityForResult(commonFriends, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		} catch(Exception ex){}
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
	
	public void hideConnect(){
		connectButton.setVisibility(View.GONE);
		hideButton.setVisibility(View.GONE);
		handler.sendEmptyMessage(8);
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
		   handler.sendEmptyMessage(10);
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
	
	public void hideButtons(View view){
		if(buttonsGroup.getVisibility()==View.VISIBLE){
			buttonsGroup.setVisibility(View.GONE);			
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_down_arrow)));
		} else {
			buttonsGroup.setVisibility(View.VISIBLE);
			hideButton.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_up_arrow)));
		}
		
		buttonsGroup.refreshDrawableState();
		listView.refreshDrawableState();	
	}

	@Override
	public void updateLocation() {
		//locationText = (TextView) findViewById(R.id.location);
		DatabaseHelper db = new DatabaseHelper(this);
    	DatabaseUtility database = new DatabaseUtility(db);	
    	
    	CurrentLocation location = database.getLocationData();
    	if(location != null){
    		//locationText.setText(location.getAddress());
    	}
	}
	
	public Location getLocation(){
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
	
	/* Class My Location Listener */
    public class MyLocationListener implements LocationListener
    {

      @Override
      public void onLocationChanged(Location loc)
      {

    	  if(String.valueOf(loc.getLatitude()).equalsIgnoreCase(null)||String.valueOf(loc.getLongitude()).equalsIgnoreCase(null)||	  
    	          String.valueOf(loc.getLatitude()).equalsIgnoreCase("")||String.valueOf(loc.getLongitude()).equalsIgnoreCase("")){
    	        	  
    	      //handler.sendEmptyMessage(5);
    	   } else{
    	        lattitude=String.valueOf(loc.getLatitude());
    	        longitude=String.valueOf(loc.getLongitude());
    	   }
      }

      @Override
      public void onProviderDisabled(String provider)
      {
      }

      @Override
      public void onProviderEnabled(String provider)
      {
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras)
      {
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

	public void refreshCommonLisnsList(){
		try{
			runOnUiThread(new Runnable() {
				public void run() {
					List<CommonLisn> dataExist = getCommonLisn();
                    if((dataExist != null) && (dataExist.size() != 0)){
                    	listView.setVisibility(View.VISIBLE);
            			noCommonLisnsYetText.setVisibility(View.GONE);
            			displayCommonLisns(dataExist);
            		} else {
            			listView.setVisibility(View.GONE);
            			noCommonLisnsYetText.setVisibility(View.VISIBLE);
                    }
				}
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_COMMON_LISN;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_COMMON_LISN;
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
	
	public void navigateFriendRequestScreen(View view){
		NotificationsActivity.callingNotificationsScreen=Constants.CALLING_SCREEN_COMMON_LISN;
		try	{   		
	    	Intent friendRequestIntent = new Intent(view.getContext(), NotificationsTabView.class);	
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("uid", uid);
	    	friendRequestIntent.putExtras(bundleObj);
	    	startActivityForResult(friendRequestIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void refreshLisn(){
		runOnUiThread(new Runnable() {
            public void run() {
            	lisnId.clear();
	    		addHeader = false;
	    		pd = ProgressDialog.show(CommonLisnActivity.this, null, CommonLisnActivity.this.getResources().getString(R.string.lodingMessage), true);
        		List<CommonLisn> dataExist = getCommonLisn();
	            if(dataExist !=null){
	    			displayCommonLisns(dataExist);
	    		}
	            addHeader = true;
	            handler.sendEmptyMessage(2);
           }
       });
	}
	
	public void navigateChooseProfileScreen(View view, String id, int position){
		try	{   		
	    	Intent chooseProfileIntent = new Intent(view.getContext(), ChooseProfileActivity.class);
	    	Bundle bundleObj = new Bundle();	
	    	bundleObj.putString("id", id);
	    	bundleObj.putString("position", Integer.toString(position));
	    	chooseProfileIntent.putExtras(bundleObj);
	    	startActivityForResult(chooseProfileIntent, NOW_LISN);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	private class ListAdapter extends BaseAdapter{
		private ArrayList<HashMap<String, Object>> lisns; 
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<HashMap<String, Object>> lisns, Context context){
			this.lisns = lisns;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return lisns.size();
		}

		@Override
		public Object getItem(int position) {
			return lisns.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			 if (convertView == null) {
	             convertView = mInflater.inflate(R.layout.common_lisn_list_view, null);
	             holder = new ViewHolder();
	             holder.lisnName = (TextView) convertView.findViewById(R.id.lisnName);
	             holder.lisnerImage = (ImageView) convertView.findViewById(R.id.lisnerImage);
	             holder.numberOfLisn = (TextView) convertView.findViewById(R.id.lisnerCount);
	             holder.numberOfMessages = (TextView) convertView.findViewById(R.id.messageCount);
	             holder.description=(TextView) convertView.findViewById(R.id.lisnDescription);
	             convertView.setTag(holder);
			 }else {
				 holder = (ViewHolder) convertView.getTag(); 
			 }
		 
			 holder.lisnName.setText((String) lisns.get(position).get(CommonLisnActivity.LISN_NAME_KEY));
			 DisplayMetrics displayMetrics = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        switch(displayMetrics.densityDpi){ 
		        	case DisplayMetrics.DENSITY_LOW:
		        		holder.numberOfLisn.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		holder.numberOfMessages.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
		        		break; 
		        }
			
			holder.numberOfLisn.setText((String) lisns.get(position).get(CommonLisnActivity.LISNER_COUNT_KEY));
			holder.numberOfMessages.setText((String) lisns.get(position).get(PastLisnActivity.TOTAL_MESSAGE_KEY));
			
			String desc = (String)lisns.get(position).get(CommonLisnActivity.LISN_DETAIL_KEY);
			holder.description.setText(desc);
			if(desc == null || desc.length()==0 || "null".equalsIgnoreCase(desc)){
				holder.description.setVisibility(View.GONE);
			} else {
				holder.description.setVisibility(View.VISIBLE);
			}
			
			lisnId.add(lisns.get(position).get(CommonLisnActivity.IDKEY).toString());
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