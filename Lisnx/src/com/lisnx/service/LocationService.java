package com.lisnx.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.model.CurrentLocation;
import com.lisnx.util.Utils;

public class LocationService extends Service implements Runnable{

	LocationManager locationManager = null;
	LocationListener locationListener = null;
	Thread thread = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void onDestroy() {
		thread.interrupt();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
	}

	@Override
	public void run() {
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	if(location != null){
			    	CurrentLocation currentLocation = new CurrentLocation();
			    	currentLocation.setLongitude(String.valueOf(location.getLongitude()));
			    	currentLocation.setLattitude(String.valueOf(location.getLatitude()));
			    	
			    	String address = Utils.getAddress(location.getLongitude(),location.getLatitude(),getApplicationContext());
			    	currentLocation.setAddress(address);
			    	DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
			    	DatabaseUtility dao = new DatabaseUtility(helper);
					
					dao.populateLocationTable(currentLocation);
					EventNotifier event = new EventNotifier();
					event.notifyEvent();
		    	}
		    }
		    
		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };
		  for(; ; ){
			  handler.sendEmptyMessage(1);
			  try {
				Thread.currentThread();
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				break;
			}
		  }
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
		}
	};
}