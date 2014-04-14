package com.lisnx.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.lisnx.android.activity.BuildConfig;
import com.lisnx.android.activity.ChangePasswordActivity;
import com.lisnx.android.activity.CommonFriendsActivity;
import com.lisnx.android.activity.FacebookActivity;
import com.lisnx.android.activity.InternetDialogActivity;
import com.lisnx.android.activity.LoginActivity;
import com.lisnx.android.activity.MenuActivity;
import com.lisnx.android.activity.NotificationsActivity;
import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.android.activity.PeopleNearByActivity;
import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.dao.TokenDao;
import com.lisnx.event.ExceptionHandler;
import com.lisnx.model.AppData;
import com.lisnx.model.Friend;
import com.lisnx.model.Login;
import com.lisnx.model.NotificationCount;
import com.lisnx.util.AppCrashReports;
import com.lisnx.util.AsyncTaskResult;
import com.lisnx.util.Constants;
import com.lisnx.util.LibFile;
import com.lisnx.util.LibHttp;
import com.lisnx.util.Utils;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class BaseActivity extends Activity {

	public Intent i;
	public static ProgressDialog pd; // VISH - Why static ? 
	
	protected ProgressDialog progressDialog; // VISH
	protected boolean finishOnOk; // VISH

	protected TextView nameText;
	protected ImageView userImage;
	protected LayoutInflater layoutInflater;
	

	public ImageView backIcon;
	public ImageView peopleNearByIcon;
	public ImageView friendRequestsIcon;
	
	 // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    protected Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    protected int mShortAnimationDuration;
    
    Map<String, String> paramsWithAccessToken;
	public static String permanentUserId = null;
	
	private static String parsePushId = null;
	
	private DatabaseHelper helper = new DatabaseHelper(this);

	//VISH
	private AppData appData;
	private Timer tNotifUpdater;
	//VISH private static  NotificationCount notificationCount = new NotificationCount();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//VISH
		appData = AppData.getInstance(getApplicationContext());
		
		initUi();
		
	}
	/**
	 * VISH
	 * Timer to update Ui components
	 */
	private void initUi()
	{
		tNotifUpdater = new Timer();
		
		tNotifUpdater.scheduleAtFixedRate(new TimerTask()
		{
			
			@Override
			public void run() 
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						updateUi();
					}
				});
				
			}
		}, 0, Constants.UPDATE_LOCATION_ITERATION_INTERVAL);
	}

	protected void setListenersOnTopIcons() {
		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		if(peopleNearByIcon!=null)
			peopleNearByIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					navigatePeopleNearByScreen(view);
				}
			});
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		if(friendRequestsIcon!=null)
			friendRequestsIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					navigateFriendRequestScreen(view);
				}
			});
		backIcon = (ImageView) findViewById(R.id.backIcon);
		if(backIcon!=null)
			backIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					navigateMenuScreen(view);
				}
			});
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppCrashReports.activityPaused();
		TimerStopper.stopTimers();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		//VISH
		initUi();
		
		ImageView homeIcon = (ImageView) findViewById(R.id.backIcon);
		if (homeIcon != null) {
			homeIcon.setAlpha(250);
		}

		if (AppCrashReports.isActivityVisible() == false) {
			try {
				checkInternetConnectivity();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		AppCrashReports.activityResumed();
	}

	public void checkInternetConnectivity() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();

		if (!isConnected) {
			try {
				Intent i = new Intent(getApplicationContext(),
						InternetDialogActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			} catch (Exception e) {
			}
		}
	}
	public DatabaseHelper getDatabaseHelper(){
		if(!helper.isOpen()){
			helper.open();
		}
		return helper;
	}

	public void destroyVariables() {
		TimerStopper.stopTimers();
		BaseActivity.permanentUserId = null;
		NowLisnActivity.permanentImage = null;
		NowLisnActivity.permanentIsImage = null;
		NowLisnActivity.permanentName = null;
		NowLisnActivity.permanentIsImage = null;
		NowLisnActivity.notificationsCount = null;
		NowLisnActivity.peopleCount = null;
		NowLisnActivity.permanentEmail = null;
		LoginActivity.callingNowLisnScreen = null;
		CommonFriendsActivity.IS_IMAGE_KEY = null;
		NotificationsActivity.IS_IMAGE_KEY = null;
		PeopleNearByActivity.IS_IMAGE_KEY = null;
		
		//Un-subscribe the installation id
		SharedPreferences preferences = getSharedPreferences(Constants.PARSE_INSTALLATION_ID, Context.MODE_PRIVATE);
		String userChannel = preferences.getString(Constants.PARSE_INSTALLATION_ID, "");
		PushService.unsubscribe(this, userChannel);
	}

	public void navigateMenuScreen(View view) {
		navigateMenuScreen(view.getContext());
	}
	public void navigateMenuScreen(Context context) {
		try {
			if(pd!=null){
				pd.dismiss();
			}
			pd = ProgressDialog.show(this, null,
					this.getResources().getString(R.string.lodingMessage), true);
			Intent menuIntent = new Intent(context,
					MenuActivity.class);
			startActivityForResult(menuIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}
	public String getAccessToken(){
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		return dao.getAccessToken();
	}
	public Map<String, String> getMapWithToken(){
		if(paramsWithAccessToken == null){
			paramsWithAccessToken = new HashMap<String, String>();
			paramsWithAccessToken.put(Constants.ACCESS_TOKEN_PARAM, getAccessToken());
		}
		return paramsWithAccessToken;
	}
	
	public SharedPreferences getSharedPreferences(){
		return getSharedPreferences("Lisnx", 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logoutMenu:
			// Call AsynchTask to perform logout
			new AsyncHttpFbLogOut().execute("");
			break;
		/**
		 * case R.id.settingsMenu: i=new
		 * Intent(getApplicationContext(),SettingsActivity.class);
		 * startActivity(i); break; case R.id.aboutUsMenu: i=new
		 * Intent(getApplicationContext(),AboutUsActivity.class);
		 * startActivity(i); break;
		 **/
		case R.id.refreshMenu:
			break;
		}
		return true;
	}
	
	
	/*VISH   Removed, Not in use
	  
	  public void logoutButtonClickListener() {
		pd = ProgressDialog.show(this, null,
				this.getResources().getString(R.string.logoutUser), true);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				logoutUser();
			}
		});
		thread.start();
		
	}	*/

	/*VISH	Removed, Not in use  
	  
	 public void logoutUser() {
		try {
			stopService(new Intent(this, LocationUpdater.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = dao.getAccessToken();
		if (accessToken != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
			Utils.postData(Constants.LOGOUT_URL, params,
					getApplicationContext());
			dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
			try {
				SharedPreferences settings = getSharedPreferences(
						Constants.PREFS_FILE_NAME_PARAM, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove(Constants.SHARED_PREFERENCES_PARAM);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

			destroyVariables();
			pd.dismiss();
			Session activeSession = Session.getActiveSession();
			if(activeSession!=null && activeSession.isOpened())
				activeSession.close();
			i = new Intent(getApplicationContext(), LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		} else {
			dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
			try {
				SharedPreferences settings = getSharedPreferences(
						Constants.PREFS_FILE_NAME_PARAM, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove(Constants.SHARED_PREFERENCES_PARAM);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

			destroyVariables();
			pd.dismiss();
			i = new Intent(getApplicationContext(), LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}*/

	public void updateImageNameStrip() {
		if ((NowLisnActivity.permanentName == null)
				|| ((NowLisnActivity.permanentImage == null) && (("true")
						.equalsIgnoreCase(NowLisnActivity.permanentIsImage)))) {
			try {
				new UserInfo(getApplicationContext(), userImage, nameText,
						layoutInflater).getProfileData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				nameText.setText(NowLisnActivity.permanentName);
				if (NowLisnActivity.permanentImage != null) {
					userImage.setBackgroundColor(Color.TRANSPARENT);
					userImage.setImageBitmap(NowLisnActivity.permanentImage);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void setListenersOnTopIcons(View.OnClickListener onClickListener) {
		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(onClickListener);
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(onClickListener);
	}

	public void navigatePeopleNearByScreen(View view) {
		try {
			Intent peopleNearByIntent = new Intent(view.getContext(),
					PeopleNearByActivity.class);
			startActivityForResult(peopleNearByIntent, 0);
			pd.dismiss();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			finish();
		} catch (Exception ex) {
		}
	}
	
	public void navigateFriendRequestScreen(View view) {
		try {
			Intent friendRequestIntent = new Intent(view.getContext(),
					NotificationsActivity.class);
			startActivityForResult(friendRequestIntent, 0);
			pd.dismiss();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateFriendRequestScreen(View view, String callingScreen) {
		NotificationsActivity.callingNotificationsScreen = callingScreen;
		try {
			Intent friendRequestIntent = new Intent(view.getContext(),
					NotificationsActivity.class);
			startActivityForResult(friendRequestIntent, 0);
			pd.dismiss();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}
	/*
	@Override
	public void onBackPressed() 
	{
		//finish();
		overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
	}
	*/
	protected void zoomImageFromThumb(final View thumbView, Bitmap expandedBitmap, int layoutId) {
	    // If there's an animation in progress, cancel it
	    // immediately and proceed with this one.
	    if (mCurrentAnimator != null) {
	        mCurrentAnimator.cancel();
	    }
	
	    // Load the high-resolution "zoomed-in" image.
	    final ImageView expandedImageView = (ImageView) findViewById(
	            R.id.expanded_image);
	    expandedImageView.setImageBitmap(expandedBitmap);
	
	    // Calculate the starting and ending bounds for the zoomed-in image.
	    // This step involves lots of math. Yay, math.
	    final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();
	
	    // The start bounds are the global visible rectangle of the thumbnail,
	    // and the final bounds are the global visible rectangle of the container
	    // view. Also set the container view's offset as the origin for the
	    // bounds, since that's the origin for the positioning animation
	    // properties (X, Y).
	    thumbView.getGlobalVisibleRect(startBounds);
	    findViewById(layoutId)
	            .getGlobalVisibleRect(finalBounds, globalOffset);
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);
	
	    // Adjust the start bounds to be the same aspect ratio as the final
	    // bounds using the "center crop" technique. This prevents undesirable
	    // stretching during the animation. Also calculate the start scaling
	    // factor (the end scaling factor is always 1.0).
	    float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        // Extend start bounds horizontally
	        startScale = (float) startBounds.height() / finalBounds.height();
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        // Extend start bounds vertically
	        startScale = (float) startBounds.width() / finalBounds.width();
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }
	
	    // Hide the thumbnail and show the zoomed-in view. When the animation
	    // begins, it will position the zoomed-in view in the place of the
	    // thumbnail.
	    thumbView.setAlpha(0f);
	    expandedImageView.setVisibility(View.VISIBLE);
	
	    // Set the pivot point for SCALE_X and SCALE_Y transformations
	    // to the top-left corner of the zoomed-in view (the default
	    // is the center of the view).
	    expandedImageView.setPivotX(0f);
	    expandedImageView.setPivotY(0f);
	
	    // Construct and run the parallel animation of the four translation and
	    // scale properties (X, Y, SCALE_X, and SCALE_Y).
	    AnimatorSet set = new AnimatorSet();
	    set
	            .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
	                    startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
	                    startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
	            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
	                    View.SCALE_Y, startScale, 1f));
	    set.setDuration(mShortAnimationDuration);
	    set.setInterpolator(new DecelerateInterpolator());
	    set.addListener(new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	            mCurrentAnimator = null;
	        }
	
	        @Override
	        public void onAnimationCancel(Animator animation) {
	            mCurrentAnimator = null;
	        }
	    });
	    set.start();
	    mCurrentAnimator = set;
	
	    // Upon clicking the zoomed-in image, it should zoom back down
	    // to the original bounds and show the thumbnail instead of
	    // the expanded image.
	    final float startScaleFinal = startScale;
	    expandedImageView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
	            if (mCurrentAnimator != null) {
	                mCurrentAnimator.cancel();
	            }
	
	            // Animate the four positioning/sizing properties in parallel,
	            // back to their original values.
	            AnimatorSet set = new AnimatorSet();
	            set.play(ObjectAnimator
	                        .ofFloat(expandedImageView, View.X, startBounds.left))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.Y,startBounds.top))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_X, startScaleFinal))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_Y, startScaleFinal));
	            set.setDuration(mShortAnimationDuration);
	            set.setInterpolator(new DecelerateInterpolator());
	            set.addListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }
	
	                @Override
	                public void onAnimationCancel(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }
	            });
	            set.start();
	            mCurrentAnimator = set;
	        }
	    });
	}

	protected Session getFacebookSession(Activity activity, StatusCallback callback) {
	    Session activeSession = Session.getActiveSession();
	    if (activeSession == null || activeSession.getState().isClosed()) {
	        activeSession = new Session.Builder(activity).setApplicationId(Constants.FACEBOOK_KEY).build();
	        Session.setActiveSession(activeSession);
	    }
	    if (!activeSession.isOpened() && !activeSession.isClosed()) {
	    	activeSession.openForRead(new Session.OpenRequest(this)
		        .setPermissions(Arrays.asList(
		        		//"basic_info"
		        		new String[] {"basic_info", "email", "friends_about_me", "user_about_me"}
		        		))
		        .setCallback(callback));
		} else {
			activeSession = Session.openActiveSession(this, true, callback);
		}
	    return activeSession;
	}

	public static String getParsePushId(Context context) {
		if(parsePushId == null){
			Parse.initialize(context, Constants.APP_ID, Constants.CLIENT_KEY);
			PushService.setDefaultPushCallback(context, NotificationsActivity.class);
			ParseInstallation.getCurrentInstallation().saveInBackground();
			StringBuilder userChannel = new StringBuilder("user_"); 
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			userChannel.append(installation.getInstallationId());
			
			PushService.subscribe(context, userChannel.toString(), NotificationsActivity.class);
			
			SharedPreferences preferences = context.getSharedPreferences(Constants.PARSE_INSTALLATION_ID, Context.MODE_PRIVATE);
			SharedPreferences.Editor parseEditor = preferences.edit();
			parseEditor.putString(Constants.PARSE_INSTALLATION_ID, userChannel.toString());
			parseEditor.commit();
			parsePushId = userChannel.toString();
		}
		return parsePushId;
	}
	
	public void navigateChangePasswordScreen(View view, String previousScreen) {
		try {
			Intent changePasswordIntent = new Intent(view.getContext(), ChangePasswordActivity.class); 
			Bundle bundleObj = new Bundle();
	    	bundleObj.putString("previousScreen", previousScreen);
	    	changePasswordIntent.putExtras(bundleObj);
			startActivityForResult(changePasswordIntent, 0); 
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); 
		} catch (Exception ex) {
			CustomToast.showCustomToast(this, Constants.APPLICATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
		}
	}
	
	protected boolean isLoggedIn(){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE_NAME_PARAM, MODE_PRIVATE);
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"BaseActivity -- isLoggedIn : "+settings.getString(Constants.SHARED_PREFERENCES_PARAM, null));
		return settings.getString(Constants.SHARED_PREFERENCES_PARAM, null)!=null;
	}

	protected void postAuthenticate(Login accessToken) {
				DatabaseUtility dao = new DatabaseUtility(getDatabaseHelper());
				dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
				dao.populateTokenTable(accessToken.getToken());
				
				try{
					SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE_NAME_PARAM, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Constants.SHARED_PREFERENCES_PARAM , dao.getAccessToken());
					editor.commit();
				} catch (Exception e){
					e.printStackTrace();
				}
				
				try {
					BaseActivity.permanentUserId = accessToken.getId();
				} catch (Exception eee) {
					eee.printStackTrace();
					BaseActivity.permanentUserId = "";
				}
				
				try{
					Intent intent = new Intent(this, LocationUpdater.class);
					startService(intent);
				} catch(Exception e){
					e.printStackTrace();
				}
				
			}

	protected void inviteFacebookFriends(StatusCallback callback) {
		Bundle params = new Bundle();
	    params.putString("message", "Learn how to make your Android apps social");
	    params.putString("data",
	            "{\"badge_of_awesomeness\":\"1\"," +
	            "\"social_karma\":\"5\"}");
	    
	    params.putString("message", "Learn how to make your Android apps social");
	    final Activity thisActivity = this;
	
	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(this,
	            getFacebookSession(this, callback),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {
	
	                public void onComplete(Bundle values,
	                    FacebookException error) {
	                    if (error != null) {
	                        if (error instanceof FacebookOperationCanceledException) {
	                            Toast.makeText(thisActivity.getApplicationContext(), 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(thisActivity.getApplicationContext(), 
	                                "Network Error", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    } else {
	                        final String requestId = values.getString("request");
	                        if (requestId != null) {
	                            Toast.makeText(thisActivity.getApplicationContext(), 
	                                "Request sent",  
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(thisActivity.getApplicationContext(), 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    }   
	                }
	
	            })
	            .build();
	    requestsDialog.show();
	}

	protected Map<String, String> getParamsMapWithTokenAndTimeZone(Handler handler) {
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
		params.put(Constants.TIME_STAMP, Utils.getTimeZone());
		return params;
	}
	
	/*	VISH
	 new notification update method implemented
	 
	 public void updateNotificationAndPeopleNearbyCounts() {
		try {
			if (NowLisnActivity.peopleCount != null
					&& !("0".equalsIgnoreCase(NowLisnActivity.peopleCount))) {
				TextView peopleNearByCount = (TextView) findViewById(R.id.notificationBoxTextPeople);
				peopleNearByCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(
						displayMetrics);
				switch (displayMetrics.densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					peopleNearByCount
							.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
					break;
				}
				peopleNearByCount.setText(NowLisnActivity.peopleCount);
			}

			if (NowLisnActivity.notificationsCount != null
					&& !("0".equalsIgnoreCase(NowLisnActivity.notificationsCount))) {
				//VISH initialize friendRequestCount
				// VISH friendRequestCount = (TextView) findViewById(R.id.notificationBoxTextNotifications);
				friendRequestCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(
						displayMetrics);
				switch (displayMetrics.densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					friendRequestCount
							.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
					break;
				}
				friendRequestCount.setText(NowLisnActivity.notificationsCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	public static Timer notificationUpdaterTimer;
	public static boolean taskSet;
	
	/**
	 * VISH
	 * Update Component
	 * @throws Exception 
	 */
	private void updateUi()
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"updateUi --- People NearBy Count : "+appData.getNotificationObj().getPeopleNearByCount());
		if (appData.getNotificationObj().getPeopleNearByCount() > 0) 
		{
			TextView peopleNearByCount = (TextView) findViewById(R.id.notificationBoxTextPeople);
			
			if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"People NearBy Count : "+appData.getNotificationObj().getPeopleNearByCount());
			
			if(peopleNearByCount!=null)
			{
				peopleNearByCount
						.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay()
						.getMetrics(displayMetrics);
				switch (displayMetrics.densityDpi) 
				{
					case DisplayMetrics.DENSITY_LOW:
						peopleNearByCount
							.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
					break;
				}
				peopleNearByCount.setText(""+appData.getNotificationObj().getPeopleNearByCount());
			}
		}
		
		TextView friendRequestCount = (TextView)findViewById(R.id.notificationBoxTextNotifications);
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FriendRequest : "+appData.getNotificationObj().getFriendRequestCount());
		if (appData.getNotificationObj().getFriendRequestCount() > 0)
		{	
			if(friendRequestCount!=null)
			{	
				if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"updateNotificationCountsOnUI -- friendRequestCount!=null");
				friendRequestCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				switch (displayMetrics.densityDpi) 
				{
					case DisplayMetrics.DENSITY_LOW:
						friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
					break;
				}
				friendRequestCount.setText(""+appData.getNotificationObj().getFriendRequestCount());
			}
			
		}
		//VISH
		else
		{
			if(friendRequestCount!=null)
			{
				//if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"updateNotificationCountsOnUI -- else -- "+friendRequestCount);
				friendRequestCount.setVisibility(View.GONE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				switch (displayMetrics.densityDpi) 
				{
					case DisplayMetrics.DENSITY_LOW:
						friendRequestCount.setTextSize(0);
					break;
				}
			}
		}			
	}
	
	
	
	/*	VISH
	 new method implemented for  notification update
	  
	 private void fetchNotificationUpdateFromServer(){
		 TimerTask doAsynchronousTask;
		 if(this instanceof MenuActivity && !taskSet){
		 doAsynchronousTask = new TimerTask() {
				            @Override
				            public void run() {
				                handler.post(new Runnable() {
				                    public void run() {
				                        try {
				                        	//VISH if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"fetchNotificationUpdateFromServer ======");
				                        	getNotificationCounts(); 
				                        	
				                        	//new UpdateUiComponent().execute("");
				                        } catch (Exception e){
				                        	e.printStackTrace();
				                        }
				                    }
				                });
				            }
				        };
		        
		        notificationUpdaterTimer = new Timer();
		        notificationUpdaterTimer.schedule(doAsynchronousTask, 0, Constants.UPDATE_LOCATION_ITERATION_INTERVAL);
		        taskSet = true;
		 }		 
	}*/
	
	/*	VISH
	 new method implemented for notification update
	  
	 private void updateNotificationUIElementsTask(){
		 TimerTask doAsynchronousTask;
			 
		 doAsynchronousTask = new TimerTask() {
				            @Override
				            public void run() {
				                handler.post(new Runnable() {
				                    public void run() {
				                        try { 
				                        	//VISH if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"updateNotificationUIElementsTask");
				                        	updateNotificationCountsOnUI();
				                        	
				                        } catch (Exception e){
				                        	
				                        }
				                    }
				                });
				            } 
				        };
		        
		        Timer notificationUIUpdaterTimer = new Timer();
		       
		        notificationUIUpdaterTimer.schedule(doAsynchronousTask, 5, Constants.UPDATE_LOCATION_ITERATION_INTERVAL);
		        
	}*/
	
	/*	VISH
	  new method implemented for notification update
	  Not in use
	 
	private void updateNotificationCountsOnUI()
	{
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (notificationCount != null) 
					{
						if (notificationCount.getPeopleNearByCount() > 0) 
						{
							TextView peopleNearByCount = (TextView) findViewById(R.id.notificationBoxTextPeople);
							if(peopleNearByCount!=null)
							{
								peopleNearByCount
										.setVisibility(View.VISIBLE);
								DisplayMetrics displayMetrics = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(displayMetrics);
								switch (displayMetrics.densityDpi) 
								{
									case DisplayMetrics.DENSITY_LOW:
										peopleNearByCount
											.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
									break;
								}
								peopleNearByCount.setText(""+notificationCount.getPeopleNearByCount());
							}
						}
						
						if (notificationCount.getFriendRequestCount() > 0)
						{
							friendRequestCount = (TextView) findViewById(R.id.notificationBoxTextNotifications);							if(friendRequestCount!=null)
							{	
								//VISH if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"updateNotificationCountsOnUI -- friendRequestCount!=null");
								friendRequestCount.setVisibility(View.VISIBLE);
								DisplayMetrics displayMetrics = new DisplayMetrics();
								getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
								switch (displayMetrics.densityDpi) 
								{
									case DisplayMetrics.DENSITY_LOW:
										friendRequestCount.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
									break;
								}
								if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"REQUEST COUNT : "+notificationCount.getFriendRequestCount());
								friendRequestCount.setText(""+notificationCount.getFriendRequestCount());
							}
						}
						//VISH
						else
						{
							friendRequestCount.setVisibility(View.GONE);
							DisplayMetrics displayMetrics = new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
							switch (displayMetrics.densityDpi) 
							{
								case DisplayMetrics.DENSITY_LOW:
									friendRequestCount.setTextSize(0);
								break;
							}
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	     /* VISH
	       
	       new method implemented for notification update
	       Not in use
	       
	      private void getNotificationCounts() throws NullPointerException,
			JSONException {
	    	 
				if (isLoggedIn()) {
					Map<String, String> params = getParamsMapWithTokenAndTimeZone(handler);
					notificationCount = Utils
							.getNotificationCounts(params,
									getApplicationContext());
				}
			}*/

	protected String getVersion() {
			PackageInfo packageInfo;
			String versionName = "1.0";
		    try {
		        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		        versionName = "version " + packageInfo.versionName;
		    } catch (NameNotFoundException e) {
		        //e.printStackTrace();
		    }
		    return versionName;
		}
	public Handler handler = new Handler() {
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
	protected List <Friend> friendList;


	/**
	 * VISH: Show progress dialog
	 * @param title title of dialog
	 * @param message message of dialog
	 */
	protected void showProgressDialog(String title, String message)
	{
			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			progressDialog = new ProgressDialog(this);
			
			progressDialog.setTitle(title);

			progressDialog.setMessage(message);

			progressDialog.show();
		}
	/**
	 * VISH: Show alert dialog
	 * @param title title of dialog
	 * @param msg message of dialog
	 */
	protected void showOKAlertMsg(String title,String msg)
	{
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		dialogBuilder.setNeutralButton( "OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) 
			{
				
				dialog.dismiss();
				
				if(finishOnOk)
				{
					finish();
				}

			}
		});

		dialogBuilder.setTitle(title);

		dialogBuilder.setMessage(msg);

		dialogBuilder.show();

	}
	/**
	 * VISH
	 * AsynchTask 
	 * to perform Logout operation
	 */
	private class AsyncHttpFbLogOut extends AsyncTask<String, Void, AsyncTaskResult<Object>> 
	{
		
		protected void onPreExecute()
		{
			showProgressDialog("","Please wait ...");
		}
		
		@Override
		protected AsyncTaskResult<Object> doInBackground(String... params) 
		{
			stopService(new Intent(getApplicationContext(), LocationService.class));
			stopService(new Intent(getApplicationContext(), LocationUpdater.class));
			
			LibHttp http = new LibHttp();
			
			try
			{
				DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
				DatabaseUtility dao = new DatabaseUtility(helper);
				String accessToken = dao.getAccessToken();
				//VISH if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "BAseActivity - logOut ---  AccessToken : "+accessToken);
				
				if (accessToken != null) 
				{
					//create web call
					//VISH if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "BAseActivity - logOut --- accessToken != null ");
					
					http.logout(accessToken);
				} 
				// delete all records from token table
				dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
				
				destroyVariables();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return new AsyncTaskResult<Object>("");
		}
	    protected void onPostExecute(AsyncTaskResult<Object> result) 
	    {
	    	if(progressDialog != null)progressDialog.dismiss();
	    	// result contains error
	    	if( result.getError() != null )
	    	{
	    		result.getError().printStackTrace();
    			finishOnOk = true;
    			//TODO: Message to be changed.
    			showOKAlertMsg("Critical Error", result.getError().getMessage());
	    	}
	    	else
	    	{
	    		i = new Intent(getApplicationContext(), FacebookActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
	    		LibFile.getInstance(getApplicationContext()).setAccessToken(null);	//set accessToken to null
	    		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "BAseActivity - logOut --- LoginActivity Called");
	    	}
	    }
	}		
	
	
}