package com.lisnx.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Status;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;

public class LocationUpdater extends Service{

	public Timer locationUpdaterTimer;
	public String lattitude;
	public String longitude;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
	}
	
	@Override
	public void onDestroy() {
		if(locationUpdaterTimer != null){
			locationUpdaterTimer.cancel();
		}
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		try{
			toCallAsynchronous();
		} catch(Exception e){
			e.printStackTrace(); 
		}
	}
	
	public void toCallAsynchronous() {
	    TimerTask doAsynchronousTask;
	    final Handler handler = new Handler();
	        doAsynchronousTask = new TimerTask() {
	            @Override
	            public void run() {
	                handler.post(new Runnable() {
	                    public void run() {
	                        try {
	                        	sendLocationToServer(); 
	                        } catch (Exception e){}
	                    }
	                });
	            }
	        };
	        
	     locationUpdaterTimer = new Timer();
	     locationUpdaterTimer.schedule(doAsynchronousTask, 0, Constants.UPDATE_LOCATION_ITERATION_INTERVAL);		
	 }
	
	public void sendLocationToServer() {
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		if (accessToken == null || accessToken.length() == 0) {
			return;
		}

		if (!Utils.isDev()) {
			Location lastKnownLocation = getLocation();
			if (lastKnownLocation != null) {
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
		} catch (NullPointerException e) {} 
		catch (JSONException e) {}

		if (status != null && status.getStatus().equalsIgnoreCase(Constants.SUCCESS)) {
			return;
		} else {
			return;
		}
	}
	
	public Location getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
}