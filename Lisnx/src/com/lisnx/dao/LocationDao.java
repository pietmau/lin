package com.lisnx.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.lisnx.model.CurrentLocation;

public class LocationDao {

	public static final String CURRENT_LOCATION_TABLE_NAME = "current_location";
	
	public static final String CURRENT_LOCATION_TABLE_KEY_ID = "_id";
	public static final String CURRENT_LOCATION_TABLE_KEY_LONGITUDE = "longitude";
	public static final String CURRENT_LOCATION_TABLE_KEY_LATTITUDE = "lattitude";
	public static final String CURRENT_LOCATION_TABLE_KEY_ADDRESS = "address";
	
	public static final int CURRENT_LOCATION_TABLE_KEY_ID_COLUMN_INDEX = 0;
	public static final int CURRENT_LOCATION_TABLE_KEY_LONGITUDE_COLUMN_INDEX = 1;
	public static final int CURRENT_LOCATION_TABLE_KEY_LATTITUDE_COLUMN_INDEX = 2;
	public static final int CURRENT_LOCATION_TABLE_KEY_ADDRESS_COLUMN_INDEX = 3;
	
	public static final String CURRENT_LOCATION_TABLE_CREATE_COMMAND =
	        "CREATE TABLE " + CURRENT_LOCATION_TABLE_NAME + "(" + CURRENT_LOCATION_TABLE_KEY_ID + " integer primary key autoincrement, "
	        + CURRENT_LOCATION_TABLE_KEY_LONGITUDE + " text not null, " + CURRENT_LOCATION_TABLE_KEY_LATTITUDE + " text not null, " 
	        + CURRENT_LOCATION_TABLE_KEY_ADDRESS + " text not null);" ;
		
	private DatabaseHelper db = null;	
		
	public LocationDao(DatabaseHelper db) {
		this.db = db;
	}
		
    public boolean insertLocationData(String longitude , String lattitude , String address) 
    {  
    	boolean exception = false;
    	
    	try {
    		db.open();
    		long check = 0;	     
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(CURRENT_LOCATION_TABLE_KEY_LONGITUDE, longitude);
	        initialValues.put(CURRENT_LOCATION_TABLE_KEY_LATTITUDE, lattitude);
	        initialValues.put(CURRENT_LOCATION_TABLE_KEY_ADDRESS, address);
	           
	        check = sqLite.insert(CURRENT_LOCATION_TABLE_NAME, null, initialValues);	
	        if(check == -1){
	        	exception  = true;
	        }
    	} catch (Exception ex) {
    		ex.printStackTrace();
		}finally{
			db.close();
		}        
        return exception;
    }
    
    public CurrentLocation getLocationData() {
    	CurrentLocation location = null;
    	
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		Cursor cursor = null;
    		long numberOfRecords = DatabaseUtils.queryNumEntries(sqLite,LocationDao.CURRENT_LOCATION_TABLE_NAME);
    		if (numberOfRecords > 0){
    			cursor = sqLite.query(CURRENT_LOCATION_TABLE_NAME, null, null, null, null, null, null);
    			cursor.moveToFirst();		
    			location = new CurrentLocation();
    			location.setLongitude(cursor.getString(CURRENT_LOCATION_TABLE_KEY_LONGITUDE_COLUMN_INDEX));
    			location.setLattitude(cursor.getString(CURRENT_LOCATION_TABLE_KEY_LATTITUDE_COLUMN_INDEX));
    			location.setAddress(cursor.getString(CURRENT_LOCATION_TABLE_KEY_ADDRESS_COLUMN_INDEX));
    		}
			
			if(cursor != null){
				cursor.close();		
			}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}finally{
    		db.close();
    	}
    	return location;		
    }    
}