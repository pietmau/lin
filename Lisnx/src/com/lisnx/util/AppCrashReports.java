package com.lisnx.util;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;

import com.lisnx.service.BaseActivity;

import android.app.Application;
import android.content.SharedPreferences;

	//@ReportsCrashes(formKey = "dEk1WUZ0YTAwdXJGQUdUcHZTQ0g5UUE6MQ")
	@ReportsCrashes(formKey = "dDhadFhvcF92dVlFZnBlMTVHYXJfUGc6MQ")
	//@ReportsCrashes(formKey = "dHY4bjVaUVRjWDU2TFdJd2tCNXJjdEE6MQ")	

   public class AppCrashReports extends Application {
	
	public static boolean activityVisible;
	
	@Override
    public void onCreate() {
		try{
			ErrorReporter.getInstance().putCustomData("Build No: ", Constants.VERSION_COUNT);
			SharedPreferences settings = getSharedPreferences("Lisnx", 0);
			String userEmail = settings.getString("useremail", null);
			ErrorReporter.getInstance().putCustomData("User Email: ",userEmail);
			
			if(Utils.isDev()){
				ErrorReporter.getInstance().putCustomData("Crash Report From: ", "Developer");
			} else{
				ErrorReporter.getInstance().putCustomData("Crash Report From: ", "User");
			}
		}catch (Exception e){
		}
		BaseActivity.getParsePushId(this.getApplicationContext());
		
        ACRA.init(this);
        super.onCreate();
    }
	
	public static boolean isActivityVisible() {
	    return activityVisible;
	}  

	public static void activityResumed() {
	    activityVisible = true;
	}

	public static void activityPaused() {
	    activityVisible = false;
	}
}