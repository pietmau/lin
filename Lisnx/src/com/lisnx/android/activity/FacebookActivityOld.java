package com.lisnx.android.activity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.SessionTracker;
import com.facebook.model.GraphUser;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Login;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

public class FacebookActivityOld extends BaseActivity{
	
	Session session = null;
	public ProgressDialog pd;

    //private final Activity activity  = null;
    private final Context context = null;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    
    private String caller;
    
    UiLifecycleHelper uiHelper = null;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
			super.onCreate(savedInstanceState);
			if(getIntent().getExtras()!=null){
				caller = getIntent().getExtras().getString("caller");
				Log.i("FacebookActivity", caller);
			}
			pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.authenticateUser), true);
			
			uiHelper = new UiLifecycleHelper(this, callback);
			uiHelper.onCreate(savedInstanceState);
			session = getFacebookSession(this, callback);
			finishActivity(3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    private void setSession (Session thisSession){
    	session = thisSession;
    }
    
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            setSession(session);
            makeMeRequest();
        }
    }
    
    public void makeMeRequest() {
        // Make an API call to get user data and define a 
        // new callback to handle the response.
       Request request = Request.newMeRequest(session, 
                new Request.GraphUserCallback() {
		     @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	sendFacebookIdToServer(getApplicationContext(), user, session);
                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                }
            }
        });
        request.executeAsync();
    } 
    private SessionTracker getSessionTracker(Activity activity){
    	SessionTracker sessionTracker = new SessionTracker(activity, new StatusCallback(){

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// TODO Auto-generated method stub
				
			}
    		
    	}, null, false);
    	return sessionTracker;
    	
    }
    
    public void sendFacebookIdToServer(Context context, GraphUser user, Session session){
    	String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			//e1.printStackTrace();
		}
    	Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.FACEBOOK_ID, user.getId());
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.FACEBOOK_PARAMS.ACCESS_TOKEN.toString(), session.getAccessToken());
		params.put(Constants.FACEBOOK_PARAMS.TOKEN_EXPIRATION_DATE.toString(), ""+session.getExpirationDate().getTime());
		params.put(Constants.FACEBOOK_PARAMS.PERMISSIONS.toString(), session.getPermissions().toString());
		params.put(Constants.PARSE_INSTALLATION_ID, getParsePushId(context));
		
		Log.i("FacebookActivity:", params.toString());
		Status status = null;
		Login login = null;
		try {
			if(caller ==null)
				status = Utils.addFacebookAccount(params, context);
			else if ("LoginActivity".equals(caller)){
				login = Utils.loginWithFacebook(params, context);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(isStatusSuccess(status)){
			navigateProfileScreenWithSuccess();
		}else if (isLoginSuccess(login)){
			navigateMenuScreen(getApplicationContext());
			postAuthenticate(login);
		}
		
    }

	private boolean isLoginSuccess(Login login) {
		return (login != null) && (login.getStatus().equalsIgnoreCase(Constants.SUCCESS));
	}
	
	private boolean isStatusSuccess(Status status) {
		return (status != null) && (status.getStatus().equalsIgnoreCase(Constants.SUCCESS));
	}
    
    private void navigateProfileScreenWithError(){
    	Intent i=new Intent(this, ProfileActivity.class);
	    Bundle bundleObj = new Bundle();	
		bundleObj.putString("facebookLoginStatus", Constants.ERROR);
		i.putExtras(bundleObj);
		this.startActivity(i);
    }
    
    public void navigateProfileScreenWithSuccess(){
    	Intent i=new Intent(this, ProfileActivity.class);
	    Bundle bundleObj = new Bundle();	
		bundleObj.putString("facebookLoginStatus", Constants.SUCCESS);
		i.putExtras(bundleObj);
		this.startActivity(i);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        makeMeRequest();
        
        //if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        //}
       finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}