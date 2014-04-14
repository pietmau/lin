package com.lisnx.android.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.dao.TokenDao;
import com.lisnx.model.Login;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.LocationUpdater;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

@SuppressLint("HandlerLeak")
public class RegistrationActivity extends BaseActivity implements OnClickListener,OnKeyListener,Runnable{

	static final int DATE_DIALOG_ID = 0;
	public String fullName;
	public String email;
	public String password;
	public String confirmPassword;
	public String token;
	public boolean flag=false;
	public boolean keyFlag=false;
	private EditText emailText;
	private EditText passwordText;
	private EditText confirmPasswordText; 
	
	private static final int CAMERA_PIC_REQUEST = 2;
	private int imageWidth=0;
	private int imageHeight=0;
	public String imageFormat=null;
	public Uri mCapturedImageURI=null;
	public boolean pictureIsSet=false;
	public File photos=null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        layoutInflater = this.getLayoutInflater();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageButton registerBlueButton = (ImageButton) findViewById(R.id.registerBlueButton);
        registerBlueButton.setOnClickListener(this);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        
        ImageButton uploadPicture = (ImageButton) findViewById(R.id.uploadPicture);
        uploadPicture.setOnClickListener(this);
        ImageButton takePicture = (ImageButton) findViewById(R.id.takePicture);
        takePicture.setOnClickListener(this);
        
        nameText = (EditText) findViewById(R.id.nameField);
        nameText.setOnKeyListener(this);
		emailText = (EditText) findViewById(R.id.emailField);
		emailText.setOnKeyListener(this);
		passwordText = (EditText) findViewById(R.id.passwordField);
		passwordText.setOnKeyListener(this);
		confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordField);
		confirmPasswordText.setOnKeyListener(this);
		
		passwordText.setTypeface(Typeface.DEFAULT);
		passwordText.setTransformationMethod(new PasswordTransformationMethod());
		confirmPasswordText.setTypeface(Typeface.DEFAULT);
		confirmPasswordText.setTransformationMethod(new PasswordTransformationMethod());
    }
	
	@Override
	public void run() {}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.registerBlueButton:
			   registerButtonClickListener(view);
			   break;
		   case R.id.cancelButton:
			   navigateLoginScreen(view);
			   break;
		   case R.id.uploadPicture:
			   uploadPicture(view);
			   break;
		   case R.id.takePicture:
			   takePicture(view);
			   break;
		}
	}
	
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event){
		     keyFlag=false;
		     nameText = (EditText) findViewById(R.id.nameField);
			 emailText = (EditText) findViewById(R.id.emailField);
			 passwordText = (EditText) findViewById(R.id.passwordField);
		     confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordField);
		     
		     fullName = nameText.getText().toString().trim();
			 email = emailText.getText().toString().trim();
		     password = passwordText.getText().toString();
			 confirmPassword = confirmPasswordText.getText().toString();
		 
		 if (keyCode == KeyEvent.KEYCODE_ENTER) {
		      if (event.getAction()!=KeyEvent.ACTION_DOWN){
                  keyFlag=false;
                  }
		      else if(event.getAction()!=KeyEvent.ACTION_UP){
		    	  if (fullName.length()!=0 && (email.length()==0 && email.equalsIgnoreCase(null))){
		    		 emailText.requestFocus();
		    	  }
		    	  else if (email.length()!=0 && (password.length()==0 && password.equalsIgnoreCase(null))){
		    		 passwordText.requestFocus();
		    	  }
		    	  else if(password.length()!=0 && (confirmPassword.length()==0 && confirmPassword.equalsIgnoreCase(null))){
		    		 confirmPasswordText.requestFocus();
		    	  }
		    	  else if(confirmPassword.length()!=0){
		    		 registerButtonClickListener(view); 
		    	  }
		    	  
		    	  keyFlag=true;
		      } 
		 } 	 
		 return keyFlag;
	 }
		
	// Creating dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar calender = Calendar.getInstance();
		int year = calender.get(Calendar.YEAR)-18;
		int month = calender.get(Calendar.MONTH);
		int day = calender.get(Calendar.DAY_OF_MONTH);
		switch (id) {
			case DATE_DIALOG_ID:
			return new DatePickerDialog(this,  mDateSetListener,  year, month, day);
		}
		return null;
	}
	
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		@SuppressWarnings("unused")
		String selectedDate = String.valueOf(monthOfYear+1)+"/"+String.valueOf(dayOfMonth)+"/"+String.valueOf(year);
		}
	};
	
	
	public void registerButtonClickListener(final View view){
		EditText nameText = (EditText) findViewById(R.id.nameField);
		EditText emailText = (EditText) findViewById(R.id.emailField);
		EditText passwordText = (EditText) findViewById(R.id.passwordField);
		EditText confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordField);
		
		fullName = nameText.getText().toString().trim();
		email = emailText.getText().toString().trim();
		password = passwordText.getText().toString();
		confirmPassword = confirmPasswordText.getText().toString();
		
		if(fullName == null || fullName.length() == 0){
			CustomToast.showCustomToast(this, Constants.USER_NAME_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			nameText.requestFocus();
			return;
		}
		else if(email == null || email.length() == 0){
			CustomToast.showCustomToast(this, Constants.USER_EMAIL_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			 emailText.requestFocus();
			return;
		}
		else if(password == null || password.length() == 0){
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
			passwordText.requestFocus();
			return;
		}
		
		try{
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		} catch(Exception e){
			
		}
		
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.registerUser), true);
		Thread thread=new Thread(new Runnable(){
	        public void run(){
	            registerUser(view);
	        }
        });
        thread.start();
	}
	
	public void registerUser(View view){
		Login loginToken = null;
		
		try {
			loginToken = Utils.getloginToken(getApplicationContext());
		} catch (NullPointerException e1) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e1) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.FULL_NAME_PARAM, fullName);
		params.put(Constants.USERNAME_PARAM, email);
		params.put(Constants.PASSWORD_PARAM, password);
		params.put(Constants.CONFIRM_PASSWORD_PARAM, confirmPassword);
		params.put(Constants.LOGIN_TOKEN_PARAM, loginToken.getToken());
		
		Status register = null;
		try {
			register = Utils.registerUser(params, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(register == null){
			handler.sendEmptyMessage(1);
		} else if(register.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			Editor editor = getSharedPreferences().edit();
			editor.putString("useremail", email);
			editor.putString("password", password);
			editor.commit();
			if(pictureIsSet == true){
				uploadPictureToServer(email,photos,imageFormat, view);
			} else{
				handler.sendEmptyMessage(2);
				navigateMenuScreen(view);
			}
			flag=true;
			return;
		} else{
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", register.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	public void uploadPicture(View view){
		try{
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("return-data", true);
			startActivityForResult(intent, 1);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void takePicture(View view){
		try{
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
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
            case 1:
                if(requestCode == 1 && data != null && data.getData() != null){
                	try{
                    Uri _uri = data.getData();

                    if (_uri != null) {
                        //User had pick an image.
                        Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                        cursor.moveToFirst();

                        //Link to the image
                        final String imageFilePath = cursor.getString(0);
                        photos= new File(imageFilePath);
                        
                        try{
                        int dotposition= imageFilePath.lastIndexOf(".");
                        imageFormat = imageFilePath.substring(dotposition + 1, imageFilePath.length());
                        }catch(Exception eee){
                        	eee.printStackTrace();
                        }
                        
                        Bitmap b = decodeFile(photos);
                        b = Bitmap.createScaledBitmap(b,imageWidth, imageHeight, true);
                        ImageView imageView = (ImageView) findViewById(R.id.userRegisterImage);
                        imageView.setImageBitmap(b);
                        
                        pictureIsSet=true;
                    }
                	} catch (Exception e){
                		e.printStackTrace();
                	}
                }
                super.onActivityResult(requestCode, resultCode, data);
                break;
                
            case 2:
            	String path=null;
            	
            	try{
            		Bitmap thumbnail = (Bitmap) data.getExtras().get("data"); 
            		path=getLastImagePath();
            		int dotposition= path.lastIndexOf(".");
            		imageFormat = path.substring(dotposition + 1, path.length());
            		photos= new File(path);
                
            		thumbnail = Bitmap.createScaledBitmap(thumbnail,imageWidth, imageHeight, true);
            		ImageView image = (ImageView) findViewById(R.id.userRegisterImage);  
            		image.setImageBitmap(thumbnail); 
            		
            		pictureIsSet=true;
            		break;
            	}catch(Exception ee){
            		ee.printStackTrace();
            	}
            }
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

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    public String getLastImagePath(){
    	try{
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        @SuppressWarnings("deprecation")
		Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if(imageCursor.moveToFirst()){
            @SuppressWarnings("unused")
			int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            return fullPath;
        }else{
            return null;
        }
    	} catch(Exception e){
    		return null;
    	}
    }
    
    public void uploadPictureToServer(String userName, File photos, String imageFormat, View view){
		Status status = null;
		try {
			status = Utils.setProfilePictureWithUsername(userName, photos, imageFormat, getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(status == null){
			handler.sendEmptyMessage(1);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			handler.sendEmptyMessage(2);
			navigateMenuScreen(view);
			return;
		} else{
			ImageView image = (ImageView) findViewById(R.id.userRegisterImage);
			image.setBackgroundDrawable((getApplication().getResources().getDrawable(R.drawable.ic_place_holder)));
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
    }

	public void navigateLoginScreen(View view){
		try	{   		
	    	Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);
	    	loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivityForResult(loginIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	public void navigateMenuScreen(View view){
		try	{
			authenticateUser(view);
	    	Intent menuIntent = new Intent(view.getContext(), MenuActivity.class);
	    	menuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivityForResult(menuIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	public void authenticateUser(View view) {
		Login loginToken = null;
		
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
			Parse.initialize(this, Constants.APP_ID, Constants.CLIENT_KEY);
			PushService.setDefaultPushCallback(this, NotificationsActivity.class);
			ParseInstallation.getCurrentInstallation().saveInBackground();
			StringBuilder userChannel = new StringBuilder("user_"); 
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			userChannel.append(installation.getInstallationId());
			
			PushService.subscribe(this, userChannel.toString(), NotificationsActivity.class);
			
			SharedPreferences preferences = getSharedPreferences(Constants.PARSE_INSTALLATION_ID, Context.MODE_PRIVATE);
			SharedPreferences.Editor parseEditor = preferences.edit();
			parseEditor.putString(Constants.PARSE_INSTALLATION_ID, userChannel.toString());
			parseEditor.commit();
			
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constants.USERNAME_PARAM, email);
			params.put(Constants.PASSWORD_PARAM, password);
			params.put(Constants.LOGIN_TOKEN_PARAM, loginToken.getToken());
			params.put(Constants.PARSE_INSTALLATION_ID, userChannel.toString());

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
				DatabaseUtility dao = new DatabaseUtility(getDatabaseHelper());
				dao.deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
				dao.populateTokenTable(accessToken.getToken());
				handler.sendEmptyMessage(3);
				
				try{
					SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE_NAME_PARAM, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
	            	editor.putString(Constants.SHARED_PREFERENCES_PARAM , dao.getAccessToken());
	            	editor.commit();
				} catch (Exception e){
					e.printStackTrace();
				}
				
				try {
					permanentUserId = accessToken.getId();
				} catch (Exception eee) {
					eee.printStackTrace();
					permanentUserId = "";
				}
				
				try{
					startService(new Intent(this, LocationUpdater.class));
				} catch(Exception e){
					e.printStackTrace();
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
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}else if(msg.what == 2){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SUCCESSFUL_REGISTER_MESSAGE, Constants.TOAST_VISIBLE_LONG, layoutInflater);
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