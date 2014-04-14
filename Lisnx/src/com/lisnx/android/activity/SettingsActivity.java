package com.lisnx.android.activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.service.BaseActivity;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;

public class SettingsActivity extends BaseActivity implements OnClickListener{
	
	private TextView emailText;
	ImageView backIcon ;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings); 
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        ImageView instantBackIcon = (ImageView) findViewById(R.id.instantBackButton);
        instantBackIcon.setOnClickListener(this);
        
        nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        emailText = (TextView) findViewById(R.id.userEmail);
        TextView changePassword=(TextView) findViewById(R.id.changePasswordText);
        changePassword.setOnClickListener(this);
    
      //VISH updateNotificationAndPeopleNearbyCounts();
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

	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.instantBackButton:
			   navigateMenuScreen(view);
			   break;
			   
		   case R.id.changePasswordText:
			   navigateChangePasswordScreen(view);
			   break;
		   case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;
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
	
	
	public void navigateFacebookScreen(View view){
		try	{   		
	    	Intent facebookIntent = new Intent(view.getContext(), LoginActivityNew.class);		 
	    	startActivityForResult(facebookIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_SETTINGS;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);	
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_SETTINGS;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
    public void navigateLinkedInScreen(View view){
	    try	{   		
    	   Intent linkedInIntent = new Intent(view.getContext(), LinkedInActivity.class);		
    	   startActivityForResult(linkedInIntent, 0);		
    	   overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
	    } catch(Exception ex) {
	    }
    }
    
    public void navigateChangePasswordScreen(View view){
		ChangePasswordActivity.callingChangePasswordScreen = Constants.CALLING_SCREEN_SETTINGS;
		try	{   		
	    	Intent changePasswordIntent = new Intent(view.getContext(), ChangePasswordActivity.class);	
	    	startActivityForResult(changePasswordIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        ImageView backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        ImageView peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        ImageView friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
        ImageView instantBackIcon = (ImageView) findViewById(R.id.instantBackButton);
        instantBackIcon.setOnClickListener(this);
        
        nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        emailText = (TextView) findViewById(R.id.userEmail);
        TextView changePassword=(TextView) findViewById(R.id.changePasswordText);
        changePassword.setOnClickListener(this);
        
      //VISH updateNotificationAndPeopleNearbyCounts();
    }
}	