package com.lisnx.android.activity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.dao.LisnDao;
import com.lisnx.dao.TokenDao;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.Login;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

public class LoginActivityNew extends BaseActivity{
	
	public static String callingNowLisnScreen = null;
	
	//private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		openFbSso();
		
		/* uiHelper = new UiLifecycleHelper(this, new StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					onSessionStateChange(session, state, exception);
					
				}
			});
		 
		 uiHelper.onCreate(savedInstanceState);		*/
		
		
	}
	@Override
	public void onResume() {
	    super.onResume();
	    /*Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }	  */  
	   // uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	   // uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	   // uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	   // uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	   // uiHelper.onSaveInstanceState(outState);
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
	    if (state.isOpened()) 
	    {
	    	Toast.makeText(this, "Session opened", Toast.LENGTH_LONG).show();
	        if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "Session opened");
	        
	        // Request user data and show the results
	        Request request = Request.newMeRequest(session,
	                new Request.GraphUserCallback() {
	                    // callback after Graph API response with user object

	                    @Override
	                    public void onCompleted(GraphUser user,
	                            Response response) {
	                        
	                        if (user != null) {
	                            try
	                            {
	                            	if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"Facebook Use: "+user.asMap().toString());
	                            	
	                            	user.asMap().toString();
	                            	
		                            //etName.setText(user.asMap().get("email").toString());
	                            }
	                            catch(Exception e)
	                            {
	                            	if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"Can not retrieve Email from Facebook account. Please type your email ");
	                            }

	                            /*etName.setText(user.getName() + ","
	                                    + user.getUsername() + ","
	                                    + user.getId() + "," + user.getLink()
	                                    + "," + user.getFirstName()+ user.asMap().get("email"));*/

	                        }
	                    }
	                });
	        
	        Request.executeBatchAsync(request);
	    }
	    else if (state.isClosed())
	    {
	    	//userInfoTextView.setVisibility(View.INVISIBLE);
	    	if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "Session closed");
	    }
	}	
	private void openFbSso()
	{
		
		//Session.op
		
		Session session = new Session(getApplicationContext());
		
        Session.setActiveSession(session);
        
        Session.OpenRequest fbReq = new Session.OpenRequest(LoginActivityNew.this) ;
        
        fbReq.setCallback(new StatusCallback() {
			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);
				
			}
		});
        
        fbReq.setPermissions(Arrays.asList("email"));
        
        session.openForRead(fbReq);		
		
	}	
	
}
