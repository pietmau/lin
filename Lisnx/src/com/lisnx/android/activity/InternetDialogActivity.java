package com.lisnx.android.activity;

import com.lisnx.android.activity.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class InternetDialogActivity extends Activity implements OnClickListener{
	
	@SuppressWarnings("deprecation")   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_dialog_box);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Button retryButton = (Button) findViewById(R.id.retryInternetButton);
		retryButton.setOnClickListener(this);
	}
	
	public boolean checkConnectivity(){
		ConnectivityManager connectivityManager	 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	   
	    if (connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
	    	finish();
	        return true;
	    } else {
	    	Intent intent = getIntent();
	    	finish();
	    	startActivity(intent);
	        return false; 
	    }
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.retryInternetButton:
			   try{
				   checkConnectivity();
			   } catch(Exception e){
			   }
			   break;
		}
	}
	
	@Override
	public void onBackPressed() {
	}
}