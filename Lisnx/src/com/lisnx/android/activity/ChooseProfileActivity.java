package com.lisnx.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.UserInfo;
import com.lisnx.util.Constants;

public class ChooseProfileActivity extends BaseActivity implements OnClickListener{
	
	public static String shareProfileType1=null;
	public static String callingScreen=null;
	public String lisnId = null;
	public String uid = null;
	public int position = 0;
	
	ImageView backIcon;
	private String isIgnored;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	if(Constants.CALLING_SCREEN_JOIN.equalsIgnoreCase(callingScreen)){
        		Bundle extras = getIntent().getExtras();
        		if(extras != null){
        			lisnId = extras.getString("id");
        			position = Integer.parseInt(extras.getString("position"));
        		}
        	}
        	if(Constants.CALLING_SCREEN_CONNECT.equalsIgnoreCase(callingScreen)){
        		Bundle extras = getIntent().getExtras();
        		if(extras != null){
        			uid = extras.getString("uid");
        			isIgnored = extras.getString("ignored");
        		}
        	}
        	if(Constants.CALLING_SCREEN_NOTIFICATIONS.equalsIgnoreCase(callingScreen)){
        		Bundle extras = getIntent().getExtras();
        		if(extras != null){
        			uid = extras.getString("uid");
        			position = Integer.parseInt(extras.getString("position"));
        		}
        	}
        	if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingScreen)){
        		Bundle extras = getIntent().getExtras();
        		if(extras != null){
        			uid = extras.getString("id");
        		}
        	}
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.choose_profile_type);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        ImageButton selectButton = (ImageButton) findViewById(R.id.selectButton);
        selectButton.setOnClickListener(this);
        nameText = (TextView) findViewById(R.id.userName);
        userImage=(ImageView)findViewById(R.id.userImage);
        
        //VISH updateNotificationAndPeopleNearbyCounts();
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.selectButton:
			   selectButtonClickListener(view);
			   break;
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		}
	}
	
	public void selectButtonClickListener(final View view){
		@SuppressWarnings("unused")
		RadioGroup profileTypes=(RadioGroup) findViewById(R.id.profileRadioGroup);
		RadioButton completeRadioButton=(RadioButton) findViewById(R.id.completeProfileRadioButton);
		RadioButton casualRadioButton=(RadioButton) findViewById(R.id.casualProfileRadioButton);
		RadioButton professionalRadioButton=(RadioButton) findViewById(R.id.professionalProfileRadioButton);
		RadioButton emailRadioButton=(RadioButton) findViewById(R.id.emailProfileRadioButton);
		
		if(completeRadioButton.isChecked()){
			shareProfileType1=Constants.PROFILE_SHARE_ALL;
		}else if(casualRadioButton.isChecked()){
			shareProfileType1=Constants.PROFILE_SHARE_CASUAL;
		}else if(professionalRadioButton.isChecked()){
			shareProfileType1=Constants.PROFILE_SHARE_PROFESSIONAL;
		}else if(emailRadioButton.isChecked()){
			shareProfileType1=Constants.PROFILE_SHARE_EMAIL;
		}else {
			shareProfileType1=null;
		}
		
		if(shareProfileType1==null||shareProfileType1.length()==0){
			CustomToast.showCustomToast(getApplicationContext(), Constants.NO_PROFILE_TYPE_CHOSEN_ERROR, Constants.TOAST_VISIBLE_LONG, this.getLayoutInflater());
		}else {
			if(Constants.CALLING_SCREEN_CREATE.equalsIgnoreCase(callingScreen)){
				Intent returnIntent = new Intent();
				setResult(RESULT_OK,returnIntent);        
				finish();
			}
			else if(Constants.CALLING_SCREEN_JOIN.equalsIgnoreCase(callingScreen)){
				Intent returnIntent = new Intent();
				returnIntent.putExtra("lisnId",lisnId);
				returnIntent.putExtra("position",Integer.toString(position));
				setResult(RESULT_OK,returnIntent);        
				finish();
			}
			else if(Constants.CALLING_SCREEN_CONNECT.equalsIgnoreCase(callingScreen)){
				if(Constants.SEND_FRIEND_REQUEST.equalsIgnoreCase(OtherProfileActivity.connectionActivity)){
					OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_CHOOSE_PROFILE;
					Intent returnIntent = new Intent();
					returnIntent.putExtra("lisnerId", uid);
					returnIntent.putExtra("uid", uid);
					returnIntent.putExtra("ignored", isIgnored);
					setResult(RESULT_OK,returnIntent);        
			    	finish();
				}
				if(Constants.ACCEPT_FRIEND_REQUEST.equalsIgnoreCase(OtherProfileActivity.connectionActivity)){
					OtherProfileActivity.callingOtherProfileScreen=Constants.CALLING_SCREEN_CHOOSE_PROFILE;
					Intent returnIntent = new Intent();
					returnIntent.putExtra("lisnerId", uid);
					returnIntent.putExtra("uid", uid);
					setResult(RESULT_OK,returnIntent);        
					finish();
				}
			}
			else if(Constants.CALLING_SCREEN_NOTIFICATIONS.equalsIgnoreCase(callingScreen)){
				Intent returnIntent = new Intent();
				returnIntent.putExtra("lisnerId",uid);
				returnIntent.putExtra("position",Integer.toString(position));
				setResult(RESULT_OK,returnIntent);        
				finish();
			}
			else if(Constants.CALLING_SCREEN_LISN_DETAIL.equalsIgnoreCase(callingScreen)){
				Intent returnIntent = new Intent();
				returnIntent.putExtra("id",uid);
				setResult(RESULT_OK,returnIntent);        
				finish();
			}
		}
	}
}