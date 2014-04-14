package com.lisnx.android.activity;

import java.net.HttpURLConnection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class LinkedInActivity extends BaseActivity{

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(Constants.LINKEDIN_KEY,Constants.LINKEDIN_SECRET);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(Constants.LINKEDIN_KEY, Constants.LINKEDIN_SECRET);	
	
	LinkedInRequestToken liToken;
	LinkedInApiClient client;
	public boolean flag=false;
	private String lisnId;
	public String verifier ;
	public String fromScreen;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		layoutInflater = this.getLayoutInflater();
		
		Bundle extras = getIntent().getExtras();
        if(extras != null){
			lisnId = extras.getString("lisnId");
			fromScreen = extras.getString("fromScreen"); 
		}
        
		try{
			liToken = oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(liToken.getAuthorizationUrl()));
			startActivity(i);
		} catch(Exception e){
			handler.sendEmptyMessage(13);
			e.printStackTrace();
		}
	}
	
	public boolean checkIfAccessTokenExists(){
		boolean accessTokenExists = false;
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			return false;
		}
		
		if(accessToken != null && accessToken.trim().length() != 0){ 
			accessTokenExists = true;
		} else{
			accessTokenExists = false;
		}
		return accessTokenExists;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try{
			if(verifier == null){
				if(checkIfAccessTokenExists()){
					Intent i=new Intent(getApplicationContext(),ProfileActivity.class);
					Bundle bundleObj = new Bundle();	
					bundleObj.putString("linkedInLoginStatus", Constants.ERROR);
					bundleObj.putString("lisnId", lisnId);
					i.putExtras(bundleObj);
					startActivity(i);
				} else{
					Intent i=new Intent(getApplicationContext(), LoginActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} else{
				verifier = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(fromScreen == null){
			finish();
		} else{
			fromScreen = null;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		try{
			if(flag==true){
				liToken = oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(liToken.getAuthorizationUrl()));
				startActivity(i);
				flag=false;
			}else {
				if(intent.getData()==null){
					liToken = oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(liToken.getAuthorizationUrl()));
					startActivity(i);
					flag=false;
				}else {
					verifier = intent.getData().getQueryParameter("oauth_verifier");
					if(verifier==null){
						if(checkIfAccessTokenExists()){
							Intent i=new Intent(getApplicationContext(),ProfileActivity.class);
							Bundle bundleObj = new Bundle();	
							bundleObj.putString("linkedInLoginStatus", Constants.ERROR);
							bundleObj.putString("lisnId", lisnId);
							i.putExtras(bundleObj);
							startActivity(i);
						} else{
							Intent i=new Intent(getApplicationContext(), LoginActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
						}
		    			flag=true;
					}else{	
						LinkedInAccessToken linkedInAccessToken = oAuthService.getOAuthAccessToken(liToken, verifier);
						client = factory.createLinkedInApiClient(linkedInAccessToken);
						Person person = client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID, ProfileField.PUBLIC_PROFILE_URL));
						String lid=person.getId();
						String linkedinProfileUrl = person.getPublicProfileUrl();
					
						if(lid != null && lid.length() > 0){
							String accessToken = null;
							DatabaseHelper helper = new DatabaseHelper(this);
							DatabaseUtility dao = new DatabaseUtility(helper);
							try {
								accessToken = dao.getAccessToken();
							} catch (Exception e1) {
								handler.sendEmptyMessage(1);
								return;
							}
							Map<String , String> params = new HashMap<String,String>();
							params.put(Constants.LINKEDIN_ID, lid);
							params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
							params.put(Constants.LINKEDIN_PROFILE_URL, linkedinProfileUrl);
							params.put("LINKEDIN_ACCESS_TOKEN", linkedInAccessToken.getToken());
							params.put("LINKEDIN_ACCESS_TOKEN_SEC",linkedInAccessToken.getTokenSecret());
							params.put("TOKEN_EXPIRATION_DATE", ""+linkedInAccessToken.getExpirationTime().getTime());
							

							Log.i(this.getClass().getCanonicalName(), params.toString());
							
							Status status = null;
							try {
								status = Utils.addLinkedInAccount(params, LinkedInActivity.this);
							} catch (NullPointerException e) {
								handler.sendEmptyMessage(12);
							} catch (JSONException e) {
								handler.sendEmptyMessage(11);
							}
							
							if(status != null && status.getStatus().equalsIgnoreCase(Constants.ERROR)){
								Message msg = new Message();
								msg.what = 2;
								Bundle bundle = new Bundle();
								bundle.putString("message", status.getMessage());
								msg.setData(bundle);
		    					handler.sendMessage(msg);
							}
    		
							if(checkIfAccessTokenExists()){
								Intent i=new Intent(getApplicationContext(),ProfileActivity.class);
								if((status != null) && (status.getStatus().equalsIgnoreCase(Constants.SUCCESS))){
									Bundle bundleObj = new Bundle();	
									bundleObj.putString("linkedInLoginStatus", Constants.SUCCESS);
									bundleObj.putString("lisnId", lisnId);
									i.putExtras(bundleObj);
								}
								startActivity(i);
							} else{
								Intent i=new Intent(getApplicationContext(), LoginActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
							flag=true;
						}	
					}
				}
			}
		} catch(Exception e){
			handler.sendEmptyMessage(13);
			e.printStackTrace();
		}
	}
	 
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
			}else if(msg.what == 2){
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
			}
			else if(msg.what == 11){
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 12){
				//CustomToast.showCustomToast(getApplicationContext(), Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 13){
				CustomToast.showCustomToast(getApplicationContext(), Constants.NO_RESPONSE_FROM_SERVICE_PROVIDER_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
			}
		}
	};
}