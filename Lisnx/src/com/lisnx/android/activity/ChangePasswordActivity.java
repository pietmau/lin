package com.lisnx.android.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.UserInfo;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class ChangePasswordActivity extends BaseActivity implements OnClickListener{

	public String password;
	public String confirmPassword;
	ImageView backIcon;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ; 
	public TextView resetPasswordText;
	public String previousScreen = null;
	public static String callingChangePasswordScreen=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        layoutInflater = this.getLayoutInflater();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        try{
        	Bundle extras = getIntent().getExtras();
        	if(extras != null){
        		previousScreen = extras.getString("previousScreen");
        	}
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        ImageView instantBackIcon = (ImageView) findViewById(R.id.instantBackButton);
        instantBackIcon.setOnClickListener(this);
        
        EditText password = (EditText) findViewById(R.id.passwordField);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);
        confirmPassword.setTypeface(Typeface.DEFAULT);
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        
        resetPasswordText = (TextView) findViewById(R.id.resetPasswordText);
       /* nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);*/
        
       
	}
	
	
	
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.saveButton:
			   checkPassword(view);
			   break;
		   case R.id.instantBackButton:
			   navigateSettingsScreen(view);
			   break;
		   case R.id.cancelButton:
			   navigatePreviousScreen(view);
			   break;
		  /* case R.id.userName:
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
		}
	}
	
	public void checkPassword(final View view){
		EditText passwordText = (EditText) findViewById(R.id.passwordField);
		EditText confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordField);
		password = passwordText.getText().toString();
		confirmPassword = confirmPasswordText.getText().toString();
		
		if(password == null || password.length() == 0){
			CustomToast.showCustomToast(this, Constants.PASSWORD_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			passwordText.requestFocus();
			return;
		}
		else if(confirmPassword == null || confirmPassword.length() == 0){
			CustomToast.showCustomToast(this, Constants.CONFIRM_PASSWORD_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			confirmPasswordText.requestFocus();
			return;
		}
		
		if(!password.equals(confirmPassword)){
			CustomToast.showCustomToast(this, Constants.PASSWORD_AND_CONFIRM_PASSWORD_NOT_MATCHED_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			confirmPasswordText.requestFocus();
			return;
		}
		
		try{
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		} catch(Exception e){
			
		}
		
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.changingPassword), true);
		Thread thread=new Thread(new Runnable(){
	        public void run(){
	            changePassword(view);
	        }
        });
        thread.start();
	}
	
	public void changePassword(View view){
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
		params.put(Constants.PASSWORD_PARAM, password);
		params.put(Constants.CONFIRM_PASSWORD_PARAM, confirmPassword);
		
		Status changePassword = null;
		try {
			changePassword = Utils.changePassword(params, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(changePassword == null){
			handler.sendEmptyMessage(1);
		} else if(changePassword.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			handler.sendEmptyMessage(2);
			navigatePreviousScreen(view);
			return;
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", changePassword.getMessage());
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
                    } catch (final Exception e) {}
                }
                return null;
            }
        });
    }
	
	@Override
	public View onCreateView(String name, Context context,AttributeSet attrs) {
		return null;
	}

	public void refreshPasswordScreen(){
		EditText password = (EditText) findViewById(R.id.passwordField);
		EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);
		password.setText("");
		confirmPassword.setText("");
	}
	
	public void navigatePreviousScreen(View view){
		if(Constants.CALLING_SCREEN_PROFILE.equalsIgnoreCase(callingChangePasswordScreen)){
			navigateProfileScreen(view);
		} else if(Constants.CALLING_SCREEN_SETTINGS.equalsIgnoreCase(callingChangePasswordScreen)){
			navigateSettingsScreen(view);
		} else{
			navigateProfileScreen(view);
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
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_CHANGE_PASSWORD;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_CHANGE_PASSWORD;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);	
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
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_CHANGE_PASSWORD_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 3){
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
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
}