package com.lisnx.service;

import java.util.Timer;
import java.util.TimerTask;

import com.lisnx.android.activity.BuildConfig;
import com.lisnx.model.AppData;
import com.lisnx.model.NotificationCount;
import com.lisnx.util.AsyncTaskResult;
import com.lisnx.util.Constants;
import com.lisnx.util.LibFile;
import com.lisnx.util.LibHttp;
import com.lisnx.util.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to get Notifications (PeopleNearby and FriendRequest)
 */
public class NotificationService extends Service {
		
   private Timer tHttp;
	
   @Override
   public IBinder onBind(Intent arg0) {
       return null;
   }

   @Override
   public void onCreate() {
   }

   @Override
   public void onStart(Intent intent, int startId) {
   //Note: You can start a new thread and use it for long background processing from here.
	   tHttp = new Timer();
	   
	   tHttp.schedule(new TimerTask()
		{
			@Override
			public void run() 
			{
				new UpdateUiComponent().execute("");
			}
		}, 5000, Constants.UPDATE_LOCATION_ITERATION_INTERVAL);
   }

   @Override
   public void onDestroy() 
   {
	   tHttp.cancel();
   }
   
   /**
    * AsynchTask to update notifications
    */
   private class UpdateUiComponent extends AsyncTask<String, Void, AsyncTaskResult<Object>> 
   {
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected AsyncTaskResult<Object> doInBackground(String... params) 
		{
			try 
			{
				LibHttp http = new LibHttp();
				
				String accessToken = LibFile.getInstance(getApplicationContext()).getAccessToken();
				String timeStamp = Utils.getTimeZone();
				
				return new AsyncTaskResult<Object>(http.getNotificationCounts(accessToken, timeStamp));
			} 
			catch (Exception e) 
			{
				return new AsyncTaskResult<Object>(e);
			} 
		}
		
		@Override
		protected void onPostExecute(AsyncTaskResult<Object> result)
		{
			super.onPostExecute(result);
			
			if( result.getError() != null )
	    	{
	    		result.getError().printStackTrace();
	    	}
			else
	    	{
				NotificationCount nCount = (NotificationCount)(((Object) result.getResult()));
				
				AppData.getInstance(getApplicationContext()).setNotificationObj(nCount);
				
				if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "RESULT : "+ nCount.getFriendRequestCount()
						+ ", People NearBy : "+nCount.getPeopleNearByCount());
	    	}			
		}
	}	
   
}



