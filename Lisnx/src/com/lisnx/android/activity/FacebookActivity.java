package com.lisnx.android.activity;

import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.lisnx.model.Login;
import com.lisnx.service.BaseActivity;
import com.lisnx.util.AsyncTaskResult;
import com.lisnx.util.Constants;
import com.lisnx.util.LibHttp;

/**
 * Lastest Facebook SDK used
 *
 */
public class FacebookActivity extends BaseActivity{
	
	public static String callingNowLisnScreen = null;
	private UiLifecycleHelper uiHelper;
	private LoginButton loginBtn;
	private String fbId;
	private Session fbSession;
	private String parsePushId;
	private boolean isFbRequestProgress;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_with_facebook);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		
	    uiHelper.onCreate(savedInstanceState);
	    
	    loginBtn = ((LoginButton)findViewById(R.id.login_button));
	    
	    loginBtn.setReadPermissions(Arrays.asList( "email"));
	     
	    initData();
	    
		if(isLoggedIn())
		{
			if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FacebookActivity - isLoggedIn Checking ");
			gotoMenu();
		}
	}
	@Override
	public void onResume() {
	    super.onResume();
	    if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FacebookActivity - onResume() ");
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
		    if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FacebookActivity - onResume() - session not null");
	    	fbSession = session;
	        onSessionStateChange(session, session.getState(), null);
	    }
	    //loginBtn.setVisibility(View.VISIBLE);
	    uiHelper.onResume();
	}
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		finish();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onPause() {
	    super.onPause();
	    if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FacebookActivity - onPause() ");
	    uiHelper.onPause();
	}
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"FacebookActivity - onDestroy() ");
	    uiHelper.onDestroy();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}	
	private void initData()
	{
		parsePushId = getParsePushId(getApplicationContext());
	}
	private void gotoMenu()
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "gotoMenu");
		Intent intent=new Intent(getApplicationContext(),MenuActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_from_right_to_left, R.anim.slide_out_from_right_to_left);
		//VISH
		finish();
	}
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "onSessionStateChange");
		
	    if (state.isOpened() && !isFbRequestProgress) 
	    {
	    	loginBtn.setVisibility(View.INVISIBLE);
	    	//showProgressDialog("","Please wait ...");
	        if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "Session opened");
	        fbSession = session;
	        
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
	                            	
	                            	fbId = user.getId();
	                            	
	                             	new AsyncHttpFbLogin().execute("");
	                            	
	                            	response.getRequest().getSession().closeAndClearTokenInformation();
	                            	
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
	        
	        isFbRequestProgress = true;
	        Request.executeBatchAsync(request);
	        
	    }
	    else if (state.isClosed())
	    {
	    	if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "Session closed");
	    }
	    else
	    {
	    	if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "onSessionStateChange -- else -- State : "+state.toString());
	    }
	}	
	private class AsyncHttpFbLogin extends AsyncTask<String, Void, AsyncTaskResult<Object>> 
	{
		
		protected void onPreExecute()
		{
			showProgressDialog("","Please wait ...");
		}
		
		@Override
		protected AsyncTaskResult<Object> doInBackground(String... params) 
		{
			try
			{
				LibHttp http = new LibHttp();
				
				Login login = (Login)http.fbLogin(fbId, fbSession, parsePushId);
				
				postAuthenticate(login);
				
				return new AsyncTaskResult<Object>(login);
			}
			catch(Exception e)
			{
				return new AsyncTaskResult<Object>(e);
			}
		}
	    protected void onPostExecute(AsyncTaskResult<Object> result) 
	    {
	    	if(progressDialog != null)progressDialog.dismiss();
	    	
	    	if( result.getError() != null )
	    	{
	    		//TODO
	    		result.getError().printStackTrace();
    			finishOnOk = true;
    			showOKAlertMsg("Critical Error", Constants.LOGIN_FAILED_TO_FACEBOOK_ERROR);
	    		
	    	}
	    	else
	    	{
	    		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "FacebookActivity -- onpostExecute ");
	    		gotoMenu();
	    		
	    	}
	    	//VISH VISIBLE to INVISIBLE
    		loginBtn.setVisibility(View.INVISIBLE);
			
	    	
	    }
	}		
}