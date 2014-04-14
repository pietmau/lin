package com.lisnx.android.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lisnx.android.activity.R;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class ForgotPasswordActivity extends BaseActivity implements OnClickListener,Runnable{
	
	public EditText userEmailField;
	public ImageButton submitButton;
	public String userEmail = null; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.forgot_password);
        layoutInflater = this.getLayoutInflater();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        userEmailField = (EditText) findViewById(R.id.emailTemporaryField);
        submitButton = (ImageButton) findViewById(R.id.submitEmailButton);
        submitButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.submitEmailButton:
			submitButtonClickListener(view);
			break;
		}
	}
	
	public void submitButtonClickListener(final View view){
		userEmail = userEmailField.getText().toString().trim();
		if (userEmail == null || userEmail.length() == 0) {
			CustomToast.showCustomToast(this, Constants.USER_EMAIL_FIELD_EMPTY_ERROR, Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			userEmailField.requestFocus();
			return;
		}
		
		try {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {}
		
		pd = ProgressDialog.show(this, null, this.getResources().getString(R.string.sendingEmail), true);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				sendEmailForTemporaryPassword(view);
			}
		});
		thread.start();
	}
	
	public void sendEmailForTemporaryPassword(View view){
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.PASSWORD_RESET_EMAIL_PARAM, userEmail);
		Status status = null;
		
		try {
			status = Utils.sendEmailForTemporaryPassword(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}
		
		if(status == null){
			handler.sendEmptyMessage(13);
		} else if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			handler.sendEmptyMessage(1);
			navigateLoginScreen(view);
			return;
		} else{
			Message msg = new Message();
			msg.what = 2;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	private Handler handler = new Handler() { 
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.TEMPORARY_PASSWORD_SENT_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 2) {
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 11) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 12) {
				//CustomToast.showCustomToast(getApplicationContext(), Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			} else if (msg.what == 13) {
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
		}
	};
	
	public void navigateLoginScreen(View view){
		try	{   		
	    	Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);		
	    	startActivityForResult(loginIntent, 0);		
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}

	@Override
	public void run() {
	}
}