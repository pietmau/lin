package com.lisnx.android.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Session;
import com.facebook.SessionState;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Profile;
import com.lisnx.model.Status;
import com.lisnx.model.UserConnectivity;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.service.UserInfo;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class ProfileActivity extends BaseActivity implements OnClickListener{
	
	private TextView emailText;
	private TextView changePasswordText;
	Bitmap bmImg;
	
	private static final int GALLERY_PIC_REQUEST = 1;
	private static final int TAKE_PIC_REQUEST = 2;
	private int imageWidth=0;
	private int imageHeight=0;
	public String imageFormat=null;
	public Uri mCapturedImageURI=null;
	public static String callingProfileScreen=null;
	public String uid = null;
	public String lisnId = null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	public File photos=null; 
	ImageView backIcon ;
	private String RSVP=null;
	ToggleButton toggleButtonFacebook ; 
	ToggleButton toggleButtonLinkedIn ; 
	ImageView profileSmallCamera ;
	TextView inviteFacebookFriendsTextView;
	TextView inviteLinkedinFriendsTextView;
	
	TextView linkedinText;
	TextView facebookText;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        layoutInflater = this.getLayoutInflater();
        try{
        	Bundle extras = getIntent().getExtras();
        	if(callingProfileScreen != null && Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingProfileScreen)){
        		if(extras != null){
        			lisnId = extras.getString("lisnId");
        			RSVP=extras.getString("RSVP");
        		}
        	}
        	
        	if((extras != null) && (extras.containsKey("facebookLoginStatus"))){
        		lisnId = extras.getString("lisnId");
        		if(Constants.SUCCESS.equalsIgnoreCase(extras.getString("facebookLoginStatus"))){
        			refreshFacebookWidgetAfterSuccessfulConnection();
        			CustomToast.showCustomToast(getApplicationContext(), Constants.CONNECTED_TO_FACEBOOK_ERROR, Constants.TOAST_VISIBLE_EXTRA_LONG, layoutInflater);
        		}
        	}
        	
        	if((extras != null) && (extras.containsKey("linkedInLoginStatus"))){
        		lisnId = extras.getString("lisnId");
        		if(Constants.SUCCESS.equalsIgnoreCase(extras.getString("linkedInLoginStatus"))){
        			refreshLinkedInWidgetAfterSuccessfulConnection();
        			CustomToast.showCustomToast(getApplicationContext(), Constants.CONNECTED_TO_LINKEDIN_ERROR, Constants.TOAST_VISIBLE_EXTRA_LONG, layoutInflater);
        		}
        	}
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        LinearLayout l1 = (LinearLayout) findViewById(R.id.PLL5);
		l1.setOnClickListener(this);
        setListenersOnTopIcons(this);
        changePasswordText = (TextView) findViewById(R.id.changePasswordText);
        changePasswordText.setOnClickListener(this);
        toggleButtonFacebook = (ToggleButton) findViewById(R.id.toggleButtonFacebook);
        toggleButtonLinkedIn = (ToggleButton) findViewById(R.id.toggleButtonLinkedIn);
        
        linkedinText = (TextView)findViewById(R.id.linkedinText);
    	facebookText = (TextView)findViewById(R.id.facebookText);
    	
        
        nameText = (TextView) findViewById(R.id.userName);
        emailText = (TextView) findViewById(R.id.userEmailInStrip);
        
        ImageView editNameImageView = (ImageView)findViewById(R.id.editNameImageView);
        /*editNameImageView.setVisibility(View.VISIBLE);
        editNameImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editItemActivity = new Intent(getApplicationContext(), EditItemActivity.class);
				startActivityForResult(editItemActivity, 10);
			}
		});
		*/
        
        inviteFacebookFriendsTextView = (TextView) findViewById(R.id.inviteFacebookFriendsTextView);
        inviteFacebookFriendsTextView.setOnClickListener(this);
        inviteLinkedinFriendsTextView = (TextView) findViewById(R.id.inviteLinkedinFriendsTextView);
        inviteLinkedinFriendsTextView.setOnClickListener(this);
        emailText.setVisibility(View.VISIBLE);
        userImage = (ImageView)findViewById(R.id.userImage);
        profileSmallCamera = (ImageView)findViewById(R.id.profileSmallCamera);
        profileSmallCamera.setVisibility(View.VISIBLE);
        profileSmallCamera.setOnClickListener(this);
        
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			SetFacebookAndLinkedInStatus setFacebookAndLinkedInStatus = new SetFacebookAndLinkedInStatus();
        			setFacebookAndLinkedInStatus.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(15);
        	e.printStackTrace();
        }
        
        //getProfileData();
       updateImageNameStrip();
       //setProfileImage();
        
//
     
        // Hook up clicks on the thumbnail views.

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if(NowLisnActivity.permanentImage!=null)
            		zoomImageFromThumb(userImage, UserInfo.userImageFullBitMap, R.id.profileLayout);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
	}

	private class SetFacebookAndLinkedInStatus extends AsyncTask<Void, Void, UserConnectivity>{
		public SetFacebookAndLinkedInStatus(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(ProfileActivity.this, null, ProfileActivity.this.getResources().getString(R.string.lodingMessage), true);
		}

		@Override
		protected UserConnectivity doInBackground(Void... params) {
			return checkFacebookAndLinkedIn();
		}
		
		@Override
        protected void onPostExecute(UserConnectivity userConnectivity){
			setFacebookAndLinkedInStatus(userConnectivity);
			handler.sendEmptyMessage(15);
		}
	}
	
	public void setFacebookAndLinkedInStatus(UserConnectivity userConnectivity){
		try{
			String facebookConnectivity=userConnectivity.getFacebookConnectivity();
			String linkedInConnectivity=userConnectivity.getLinkedInConnectivity();
			
			if("true".equalsIgnoreCase(facebookConnectivity)){
				refreshFacebookWidgetAfterSuccessfulConnection();
			}else {
				toggleButtonFacebook.setChecked(false);
			}
				
			if("true".equalsIgnoreCase(linkedInConnectivity)){
				refreshLinkedInWidgetAfterSuccessfulConnection();
			}else {	
				toggleButtonLinkedIn.setChecked(false);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void refreshFacebookWidgetAfterSuccessfulConnection() {
		toggleButtonFacebook.setVisibility(View.GONE);
		inviteFacebookFriendsTextView.setVisibility(View.VISIBLE);
		facebookText.setVisibility(View.VISIBLE);
	}

	private void refreshLinkedInWidgetAfterSuccessfulConnection() {
		toggleButtonLinkedIn.setVisibility(View.GONE);
		inviteLinkedinFriendsTextView.setVisibility(View.VISIBLE);
		linkedinText.setVisibility(View.VISIBLE);
	}
	
	public UserConnectivity checkFacebookAndLinkedIn(){
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
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			
		UserConnectivity userConnectivity=null;
		try {
			userConnectivity = Utils.getUserConnectivity(params, ProfileActivity.this);
		} catch (NullPointerException e) {
			//handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			//handler.sendEmptyMessage(11);
		}
		
		return userConnectivity;
	 }

	public void getImage(){
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
		
	    try {
			bmImg = Utils.getUserImage(params, ProfileActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		setProfileImage();
	}

	private void setProfileImage() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch(displayMetrics.densityDpi){ 
        	case DisplayMetrics.DENSITY_LOW: 
        		imageWidth=Constants.LOW_WIDTH+10;
        		imageHeight=Constants.LOW_HEIGHT+10;
        		break; 
        	case DisplayMetrics.DENSITY_MEDIUM: 
        		imageWidth=Constants.MEDIUM_WIDTH+10;
        		imageHeight=Constants.MEDIUM_HEIGHT+10;
        		break; 
        	case DisplayMetrics.DENSITY_HIGH: 
        		imageWidth=Constants.HIGH_WIDTH+20;
        		imageHeight=Constants.HIGH_HEIGHT+20;
        		break; 
        	case DisplayMetrics.DENSITY_XHIGH: 
        		imageWidth=Constants.HIGH_WIDTH+20;
        		imageHeight=Constants.HIGH_HEIGHT+20;
        		break;
        	default:
        		imageWidth=Constants.HIGH_WIDTH+20;
        		imageHeight=Constants.HIGH_HEIGHT+20;
        		break;
        } 
		
        if(bmImg==null){
        	handler.sendEmptyMessage(4);
        	userImage.setBackgroundDrawable((getApplication().getResources().getDrawable(R.drawable.ic_place_holder)));
        }else {
        	bmImg = Utils.scaleProportionally(bmImg, imageHeight,imageWidth);
        	userImage.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bmImg));
        }
	}
	
	public void updateUserProfile(){
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
		
		Status status = null;
		try {
			status = Utils.updateUserProfile(params, ProfileActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(status != null && status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			handler.sendEmptyMessage(2);
			return;
		}else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	public void getProfileData(){
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
		
		Profile profile = null;
		try {
			profile = Utils.getProfileData(params, ProfileActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		try{
			if(profile != null){
				nameText.setText(profile.getFullname());
				emailText.setText(profile.getEmail());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.profileSmallCamera:
			   showDialogForImageUplaod(view);
			   break;
		   case R.id.changePasswordText:
			   navigateChangePasswordScreen(view);
			   break;
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   navigateFriendRequestScreen(view);
			   break;
		   case R.id.inviteFacebookFriendsTextView:
		   		inviteFbFriends();
		   		break;
		   case R.id.inviteLinkedinFriendsTextView:
			   CustomToast.showCustomToast(view.getContext(), "Coming soon..", Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			   break;
		}
	}
	
	private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
        	//TODO
        	Log.w("OtherProfileActivity", "Facebook Session state changed");
            //onSessionStateChange(session, state, exception);
        }
    };
	
	private void inviteFbFriends() {
		inviteFacebookFriends(callback);
		
	}

	public void onToggleClickedFacebook(View view) {
		try{
			boolean facebookOn = ((ToggleButton) view).isChecked();
			
			
			
			if (facebookOn) {
				Intent facebookIntent = new Intent(view.getContext(), LoginActivityNew.class);
				facebookIntent.putExtra("caller","ProfileActivity" );
				startActivityForResult(facebookIntent, 3);
				
			} else {
				toggleButtonClickListener(Constants.FB);
				Session facebookSession = createSession(this);
				facebookSession.closeAndClearTokenInformation();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	 private Session createSession(Activity activity) {
	        Session activeSession = Session.getActiveSession();
	        if (activeSession == null || activeSession.getState().isClosed()) {
	            activeSession = new Session.Builder(activity).setApplicationId(Constants.FACEBOOK_KEY).build();
	            Session.setActiveSession(activeSession);
	        }
	        return activeSession;
	    }
	
	

	public void onToggleClickedLinkedIn(View view) {
		try{
			boolean linkedInOn = ((ToggleButton) view).isChecked();
	    	if (linkedInOn) {
	    		navigateLinkedInScreen();
	    	} else {
	    		toggleButtonClickListener(Constants.LINKEDIN);
	    	}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void toggleButtonClickListener(final String networkIdentifier){
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.disablingNetwork), true);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				disableSocialNetworkSettings(networkIdentifier);
			}
		});
		thread.start();
	}
	
	public void disableSocialNetworkSettings(final String networkIdentifier){
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
		params.put(Constants.DISABLE_PARAM, networkIdentifier);
		
		Status status=null;
		try {
			status = Utils.disableSocialNetworkSettings(params, ProfileActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			if(Constants.FB.equalsIgnoreCase(networkIdentifier)){
				handler.sendEmptyMessage(13);
			} else if(Constants.LINKEDIN.equalsIgnoreCase(networkIdentifier)){
				handler.sendEmptyMessage(14);
			}
			return;
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
			runOnUiThread(new Runnable() {
	    		public void run() {
	    			if(Constants.FB.equalsIgnoreCase(networkIdentifier)){
	    				toggleButtonFacebook.setChecked(true);
	    			} else if(Constants.LINKEDIN.equalsIgnoreCase(networkIdentifier)){
	    				toggleButtonLinkedIn.setChecked(true);
	    			}
	    		}
			});
			handler.sendEmptyMessage(15);
		}
	}
	
	public void showDialogForImageUplaod(final View view){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Upload or Capture image ?");
			builder.setCancelable(true);
			builder.setNegativeButton("Capture", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					takePicture(view);
					}
        		});
			builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					uploadPicture(view);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void uploadPicture(View view){
		try{
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("return-data", true);
			startActivityForResult(intent, GALLERY_PIC_REQUEST);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void takePicture(View view){
		try{
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			startActivityForResult(cameraIntent, TAKE_PIC_REQUEST);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
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
        
        switch (requestCode) {
            case GALLERY_PIC_REQUEST:
                if(requestCode == 1 && data != null && data.getData() != null){
                	try{
                    Uri _uri = data.getData();

                    if (_uri != null) {
                        Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                        cursor.moveToFirst();

                        //Link to the image
                        final String imageFilePath = cursor.getString(0);
                        photos= new File(imageFilePath);
                        int dotposition= imageFilePath.lastIndexOf(".");
                        imageFormat = imageFilePath.substring(dotposition + 1, imageFilePath.length());
                        ImageView imageView = (ImageView) findViewById(R.id.userImage);
                        ExifInterface exifInterface = new ExifInterface(photos.getAbsolutePath());
                        int orientation =  exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotate = 0;
                        switch(orientation){
                        case ExifInterface.ORIENTATION_ROTATE_270: rotate = 270; break;
                        case ExifInterface.ORIENTATION_ROTATE_180: rotate = 180; break;
                        case ExifInterface.ORIENTATION_ROTATE_90: rotate = 90; break;
                        }
                        
                        upload(imageFormat,imageView);
                    }
                	}catch(Exception e){
                		e.printStackTrace();
                	}
                }
                super.onActivityResult(requestCode, resultCode, data);
                break;
                
            case TAKE_PIC_REQUEST:
            	String path=null;
            	try{
            		@SuppressWarnings("unused")
					Bitmap thumbnail = (Bitmap) data.getExtras().get("data"); 
            		path=getLastImagePath();
            		int dotposition= path.lastIndexOf(".");
            		imageFormat = path.substring(dotposition + 1, path.length());
            		photos= new File(path);
            		ImageView imageView = (ImageView) findViewById(R.id.userImage);  
            		upload(imageFormat,imageView);
            		break;
            	}catch(Exception ee){
            		ee.printStackTrace();
            	}
            case 3:
            	//facebookActivity.makeMeRequest();
        	case 10:
        		updateName(data.getExtras().get("newValue"));
        	}
        }
    
    private String updateName(Object name) {
		Log.i("ProfileActivity", name.toString());
		return null;
	}

	private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            final int REQUIRED_SIZE=500;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    public Bitmap uploadPictureToServerCaller(String imageFormat){
    	Bitmap bmImg=uploadPictureToServer(photos, imageFormat);
    	getImage();
    	return bmImg;
    }
    
    public Bitmap uploadPictureToServer(File photos, String imageFormat){
    	DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
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
    	
		Status status = null;
		try {
			status = Utils.setProfilePictureWithAccessToken(accessToken, photos, imageFormat, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
			return null;
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			handler.sendEmptyMessage(2);
			Bitmap b = decodeFile(photos);
			if(b != null){
				b = Bitmap.createScaledBitmap(b,imageWidth, imageHeight, true);
				NowLisnActivity.permanentImage = new RoundedCornerBitmap().getRoundedCornerBitmap(b);
				NowLisnActivity.permanentIsImage = "true";
			}
	        
	        try	{   		
		    	Intent profileIntent = new Intent(this, ProfileActivity.class);		
		    	startActivityForResult(profileIntent, 0);	
		    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		    	
	    	} catch(Exception ex){}
	        
	        if(b != null){
	        	return new RoundedCornerBitmap().getRoundedCornerBitmap(b);
	        } else{
	        	return null;
	        }
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
			return null;
		}
    }
    
    public String getLastImagePath(){
    	try{
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        @SuppressWarnings("deprecation")
		Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if(imageCursor.moveToFirst()){
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            return fullPath;
        }else{
            return null;
        }
    	} catch(Exception e){
    		return null;
    	}
    }
    
    public void upload(String imageFormat, ImageView imageView) {
		 BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
         task.execute(imageFormat);
    }
	 
	 class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		    private final WeakReference<ImageView> imageViewReference;

		    public BitmapDownloaderTask(ImageView imageView) {
		        imageViewReference = new WeakReference<ImageView>(imageView);
		    }
		    
		    @Override
	        protected void onPreExecute(){
	        	pd = ProgressDialog.show(ProfileActivity.this, null, ProfileActivity.this.getString(R.string.lodingMessage), true);
	        }

		    @Override
		    protected Bitmap doInBackground(String... params) {
		         return uploadPictureToServerCaller(params[0]);
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
		        			imageView.setImageBitmap(bitmap);
		        		} else {
		        			imageView=(ImageView)findViewById(R.id.userImage);
		        			imageView.setImageBitmap(bitmap);
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
		        handler.sendEmptyMessage(15);
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

		public void navigateFriendsScreen(View view){
		try	{   		
	    	Intent friendsIntent = new Intent(view.getContext(), FriendsActivity.class);	
	    	startActivityForResult(friendsIntent, 0);
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
	
	public void navigateFacebookScreen(){
		try	{   		
			i=new Intent(getApplicationContext(),LoginActivityNew.class);
			Bundle bundleObj = new Bundle();	
			bundleObj.putString("lisnId", lisnId);
			i.putExtras(bundleObj);
			startActivity(i);		
    	} catch(Exception ex) {
    	}
	}
	
	
	
	public void navigateChangePasswordScreen(View view){
		ChangePasswordActivity.callingChangePasswordScreen = Constants.CALLING_SCREEN_PROFILE;
		try	{   		
	    	Intent changePasswordIntent = new Intent(view.getContext(), ChangePasswordActivity.class);	
	    	startActivityForResult(changePasswordIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
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
  
    public void navigateLinkedInScreen(){
	    try	{   		
	    	i=new Intent(getApplicationContext(),LinkedInActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	Bundle bundleObj = new Bundle();	
			bundleObj.putString("lisnId", lisnId);
			bundleObj.putString("fromScreen", "Profile");
			i.putExtras(bundleObj);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
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
	    	view.getContext().startActivity(lisnDetailIntent);
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
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_UPDATE_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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
				CustomToast.showCustomToast(getApplicationContext(), Constants.DISCONNECTED_TO_FACEBOOK_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 14){
				CustomToast.showCustomToast(getApplicationContext(), Constants.DISCONNECTED_TO_LINKEDIN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 15){
				if(pd != null)
					pd.dismiss();
			}
		}
	};
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(pd != null && pd.isShowing()){
        	pd.dismiss();
        }
        setContentView(R.layout.profile);
        try{
        	if(callingProfileScreen != null && Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingProfileScreen)){
        		Bundle extras = getIntent().getExtras();
        		if(extras != null){
        			lisnId = extras.getString("lisnId");
        			RSVP=extras.getString("RSVP");
        		}
        	}
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        layoutInflater = this.getLayoutInflater();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        res = getResources();
        defaultBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_place_holder);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        
        setListenersOnTopIcons(this);
        changePasswordText = (TextView) findViewById(R.id.changePasswordText);
        changePasswordText.setOnClickListener(this);
        toggleButtonFacebook = (ToggleButton) findViewById(R.id.toggleButtonFacebook);
        toggleButtonLinkedIn = (ToggleButton) findViewById(R.id.toggleButtonLinkedIn);
        
        nameText = (TextView) findViewById(R.id.userName);
        emailText = (TextView) findViewById(R.id.userEmailInStrip);
        emailText.setVisibility(View.VISIBLE);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        profileSmallCamera = (ImageView)findViewById(R.id.profileSmallCamera);
        profileSmallCamera.setVisibility(View.VISIBLE);
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			SetFacebookAndLinkedInStatus setFacebookAndLinkedInStatus = new SetFacebookAndLinkedInStatus();
        			setFacebookAndLinkedInStatus.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(15);
        	e.printStackTrace();
        }
        
      //VISH updateNotificationAndPeopleNearbyCounts();
	}
}