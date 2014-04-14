package com.lisnx.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.lisnx.android.activity.InternetDialogActivity;
import com.lisnx.util.AppCrashReports;

public class ConnectivityChangedReceiver extends BroadcastReceiver{

	  public boolean causedByPhoneCall = false;
	  TelephonyManager telephonyManager;

	  @Override
	  public void onReceive( Context context, Intent intent ) {
		  telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		  telephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		  
		  if((AppCrashReports.isActivityVisible() == true) && (causedByPhoneCall == false))  {
			 ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
			 NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			 NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			  
			 if ((mWifi != null && mWifi.isConnected()) || (mMobile != null && mMobile.isConnected())) {
	    	 } else {
	    		 try{
	    			 Intent i=new Intent(context, InternetDialogActivity.class);
	    			 i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			 context.startActivity(i);
	    		 } catch(Exception e){
	    			 e.printStackTrace();
	    		 }
	    	 }
		 }
	  }
	  
	  private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		  public void onCallStateChanged(int state, String incomingNumber) {
		     try {
		       switch (state) {
		       case TelephonyManager.CALL_STATE_RINGING:
		    	   causedByPhoneCall = true;
		    	   break;
		       case TelephonyManager.CALL_STATE_OFFHOOK:
		    	   causedByPhoneCall = true;
		    	   break;
		       case TelephonyManager.CALL_STATE_IDLE:
		    	   causedByPhoneCall = false;
		    	   break;
		       default:
		    	   break;
		       }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
	    }
	};
}