package com.lisnx.android.activity;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity implements OnClickListener, OnKeyListener {

	String emailText;
	String passwordText;
	public boolean flag = false;
	public String longitude;
	public String lattitude;
	public static String callingNowLisnScreen = null;
	EditText passwordTextBox; 
	EditText emailTextBox;
	String emailForConfigChanges = null;
	String passwordForConfigChanges = null;
	TextView versionNo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			switch(displayMetrics.densityDpi){ 
        		case DisplayMetrics.DENSITY_LOW:
        			setContentView(R.layout.login_with_facebook);
        			break; 
        		default:
        			setContentView(R.layout.login_with_facebook);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		try{	
			Thread lisnThread = new Thread() {
				public void run() {
					cleanLisnTable();
				}
			};
			lisnThread.start();
		} catch (Exception e){
			e.printStackTrace();
		}
 
		try{  
			layoutInflater = this.getLayoutInflater();
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE_NAME_PARAM , MODE_PRIVATE);
			String accessToken = settings.getString(Constants.SHARED_PREFERENCES_PARAM , null);
			
			if(accessToken!=null && accessToken.trim().length()!=0){ 
				DatabaseUtility dao = new DatabaseUtility(getDatabaseHelper());
				dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
				dao.populateTokenTable(accessToken);
				
				sendLocationToServer();
				Intent intent=new Intent(getApplicationContext(),MenuActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
			
		
		
		LoginButton fbLoginButton = (LoginButton)findViewById(R.id.login_button);
		fbLoginButton.setOnClickListener(this);

		
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.loginButton:
			loginButtonClickListener(view);
			break;
		case R.id.registerOrangeButton:
			navigateRegistrationScreen(view);
			break;
		case R.id.forgotPasswordText:
			navigateForgotPasswordScreen(view);
			break;
		case R.id.loginFacebookImage:
			navigateFacebookActivity(view);
			break;
		case R.id.login_button:
			navigateFacebookActivity(view);
			break;
		}
	}
	
	private void navigateFacebookActivity(final View view) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Intent facebookLoginIntent = new Intent(view.getContext(), FacebookActivity.class); 
					facebookLoginIntent.putExtra("caller","LoginActivity" );
					startActivityForResult(facebookLoginIntent, 0); 
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); 
				} catch (Exception ex) {
					CustomToast.showCustomToast(view.getContext(), Constants.APPLICATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				}
			}
		});
		thread.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		flag = false;

		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (event.getAction() != KeyEvent.ACTION_DOWN) {
				flag = false;
			} else if (event.getAction() != KeyEvent.ACTION_UP) {

				if (emailTextBox.hasFocus()) {
					passwordText = passwordTextBox.getText().toString();

					if (passwordText == null || passwordText.length() == 0) {
						passwordTextBox.requestFocus();
					} else {
						loginButtonClickListener(view);
					}
				} else if (passwordTextBox.hasFocus()) {
					loginButtonClickListener(view);
				}
				flag = true;
			}
		}
		return flag;
	}

	public void loginButtonClickListener(final View view) {
		emailTextBox = (EditText) findViewById(R.id.emailLogin);
		passwordTextBox = (EditText) findViewById(R.id.passwordLogin);
		emailText = emailTextBox.getText().toString().trim();
		
		SharedPreferences settings = getSharedPreferences("Lisnx", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("useremail", emailText);
		editor.commit();

		passwordText = passwordTextBox.getText().toString();
		if (emailText == null || emailText.length() == 0) {
			CustomToast.showCustomToast(this, Constants.USER_EMAIL_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			emailTextBox.requestFocus();
			return;
		} else if (passwordText == null || passwordText.length() == 0) {
			CustomToast.showCustomToast(this, Constants.PASSWORD_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			passwordTextBox.requestFocus();
			return;
		}

		try {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {}
		
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.authenticateUser), true);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				authenticateUser(view);
			}
		});
		thread.start();
	}

	public void cleanLisnTable() {
		try{
			DatabaseUtility database = new DatabaseUtility(getDatabaseHelper());
			database.deleteAllRecordsLisnTable(LisnDao.LISN_TABLE_NAME);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void saveLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {
			DatabaseUtility database = new DatabaseUtility(getDatabaseHelper());
			CurrentLocation currentLocation = new CurrentLocation();
			currentLocation.setLongitude(String.valueOf(location.getLongitude()));
			currentLocation.setLattitude(String.valueOf(location.getLatitude()));
			String address = Utils.getAddress(location.getLongitude(), location.getLatitude(), getApplicationContext());
			currentLocation.setAddress(address);

			database.populateLocationTable(currentLocation);
		}
	}

	public void authenticateUser(View view) {
		Login loginToken = null;
		String resetPassword = null;

		try {
			loginToken = Utils.getloginToken(getApplicationContext());
		} catch (NullPointerException e1) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e1) {
			handler.sendEmptyMessage(11);
			return;
		}

		if (Constants.SUCCESS.equalsIgnoreCase(loginToken.getStatus())) {
			//Register to Parse sever and subscribe with installation id
			String parsePushId = getParsePushId(view.getContext());
			
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constants.USERNAME_PARAM, emailText);
			params.put(Constants.PASSWORD_PARAM, passwordText);
			params.put(Constants.LOGIN_TOKEN_PARAM, loginToken.getToken());
			params.put(Constants.PARSE_INSTALLATION_ID, parsePushId);

			Login accessToken = null;
			try {
				accessToken = Utils.getAccessToken(params, getApplicationContext());
			} catch (NullPointerException e) {
				handler.sendEmptyMessage(12);
				return;
			} catch (JSONException e) {
				handler.sendEmptyMessage(11);
				return;
			}

			if (accessToken != null && accessToken.getStatus().equalsIgnoreCase(Constants.SUCCESS)) {
				postAuthenticate(accessToken);
				handler.sendEmptyMessage(3);
				if((Boolean.parseBoolean(accessToken.getResetPassword())) == false){
					navigateChangePasswordScreen(view, "loginScreen");
				} else{
					navigateMenuScreen(view);
				}
				
			}

			else {
				handler.sendEmptyMessage(1);
				return;
			}
			handler.sendEmptyMessage(3);
		}else {
			handler.sendEmptyMessage(1);
			return;
		}
	}

	public void sendLocationToServer() {
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(2);
			// return false;
		}

		if (accessToken == null || accessToken.length() == 0) {
			handler.sendEmptyMessage(2);
			// return false;
		}

		if (!Utils.isDev()) {
			Location lastKnownLocation = getLocation();
			if (lastKnownLocation == null) {
				handler.sendEmptyMessage(5);
			} else {
				longitude = String.valueOf(lastKnownLocation.getLongitude());
				lattitude = String.valueOf(lastKnownLocation.getLatitude());
			}
		} else {
			lattitude = "28.663628";
			longitude = "77.369767";
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LATITUDE_PARAM, lattitude);
		params.put(Constants.LONGITUDE_PARAM, longitude);

		Status status = null;
		try {
			status = Utils.sendLocationToServer(params, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}

		if (status != null && status.getStatus().equalsIgnoreCase(Constants.SUCCESS)) {
			// handler.sendEmptyMessage(2);
			return;
		}
	}

	public Location getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			if (String.valueOf(loc.getLatitude()).equalsIgnoreCase(null)
					|| String.valueOf(loc.getLongitude()).equalsIgnoreCase(null)
					|| String.valueOf(loc.getLatitude()).equalsIgnoreCase("")
					|| String.valueOf(loc.getLongitude()).equalsIgnoreCase("")) {

				// handler.sendEmptyMessage(5);
			} else {
				lattitude = String.valueOf(loc.getLatitude());
				longitude = String.valueOf(loc.getLongitude());
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	public void navigateRegistrationScreen(View view) {
		try {
			Intent menuIntent = new Intent(view.getContext(), RegistrationActivity.class); 
			startActivityForResult(menuIntent, 0); 
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); 
		} catch (Exception ex) {
			CustomToast.showCustomToast(this, Constants.APPLICATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
		}
	}
	
	
	
	public void navigateForgotPasswordScreen(View view) {
		try {
			Intent forgotPasswordIntent = new Intent(view.getContext(), ForgotPasswordActivity.class); 
			startActivityForResult(forgotPasswordIntent, 0); 
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); 
		} catch (Exception ex) {
			CustomToast.showCustomToast(this, Constants.APPLICATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
		}
	}

	public void navigateTabScreen(View view) {
		callingNowLisnScreen = Constants.CALLING_SCREEN_LOGIN;
		try {
	    	Intent tabIntent = new Intent(view.getContext(), TabView.class);		
			startActivityForResult(tabIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
		} catch (Exception ex) {
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.WRONG_USERNAME_PASSWORD_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 2) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 3) {
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 11) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 12) {
				//CustomToast.showCustomToast(getApplicationContext(), Constants.NO_CONNECTION_ERROR,
						//Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try{
			emailForConfigChanges = emailTextBox.getText().toString().trim();
			passwordForConfigChanges = passwordTextBox.getText().toString().trim();
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			switch(displayMetrics.densityDpi){ 
        		case DisplayMetrics.DENSITY_LOW:
        			setContentView(R.layout.login_small);
        			break; 
        		default:
        			setContentView(R.layout.login);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
			
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ImageButton loginButton = (ImageButton) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		ImageButton registerOrangeButton = (ImageButton) findViewById(R.id.registerOrangeButton);
		registerOrangeButton.setOnClickListener(this);
		TextView forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
		forgotPasswordText.setOnClickListener(this);

		passwordTextBox = (EditText) findViewById(R.id.passwordLogin);
		passwordTextBox.setText(passwordForConfigChanges);
		passwordTextBox.setOnKeyListener(this);
		emailTextBox = (EditText) findViewById(R.id.emailLogin);
		emailTextBox.setText(emailForConfigChanges);
		emailTextBox.setOnKeyListener(this);
			
		emailTextBox.setTypeface(Typeface.DEFAULT);
		passwordTextBox.setTypeface(Typeface.DEFAULT);
		passwordTextBox.setTransformationMethod(new PasswordTransformationMethod());
	} 
}