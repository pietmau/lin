package com.lisnx.dao;

import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.Lisn;
import com.lisnx.util.Constants;

public class DatabaseUtility {

	DatabaseHelper db = null;
	
	public DatabaseUtility(DatabaseHelper db){
		this.db = db;
	}
	
	public synchronized void cleanDatabase(){
		Thread thread =	new Thread (){
			public void run(){
				try{
					deleteAllRecords(TokenDao.ACCESS_TOKEN_TABLE_NAME);
					deleteAllRecords(LocationDao.CURRENT_LOCATION_TABLE_NAME);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
		    		db.close();
		    	}
			}
	 };
		 thread.start();
		 try {
			    thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void deleteAllRecords(String tableName){
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		sqLite.delete(tableName, null,null);	
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }	
	
	public synchronized void deleteAllRecordsTokenTable(String tableName){
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		sqLite.delete(tableName, null,null);		
    	} catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		db.close();
    	}
    }
	
	public synchronized void deleteAllRecordsLisnTable(String tableName){
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		sqLite.delete(tableName, null,null);		
    	} catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		db.close();
    	}
    }
	
	public synchronized void populateTokenTable(String accessToken){
		deleteAllRecordsTokenTable(TokenDao.ACCESS_TOKEN_TABLE_NAME);
		TokenDao tokenDao = new TokenDao(db);		
		tokenDao.insertAccessToken(accessToken);
	}
	
	public synchronized boolean populateLisnTable(List<Lisn> lisnList){
		LisnDao lisnDao = new LisnDao(db);		
		return lisnDao.insertLisnData(lisnList);
	}
	
	public synchronized boolean populatePastLisnTable(List<Lisn> lisnList){
		deleteAllRecords(PastLisnDao.PAST_LISN_TABLE_NAME);
		PastLisnDao pastLisnDao = new PastLisnDao(db);		
		return pastLisnDao.insertPastLisnData(lisnList);
	}
	
	public synchronized List<Lisn> getLisnData(){
		LisnDao lisnDao = new LisnDao(db);		
		return lisnDao.getLisnData();
	}
	
	public synchronized List<Lisn> getPastLisnData(){
		PastLisnDao pastLisnDao = new PastLisnDao(db);		
		return pastLisnDao.getPastLisnData();
	}
	
	public synchronized boolean checkLisnDataExist(){
		LisnDao lisnDao = new LisnDao(db);		
		return lisnDao.checkLisnRecordExist();
	}
	
	public synchronized boolean checkPastLisnDataExist(){
		PastLisnDao lisnDao = new PastLisnDao(db);		
		return lisnDao.checkPastLisnRecordExist();
	}
	
	public String getAccessToken(){
		TokenDao tokenDao = new TokenDao(db);		
		return tokenDao.getAccessToken();
	}
	
	public synchronized void populateLocationTable(CurrentLocation location){
		if(location == null || location.getLongitude() == null || location.getLongitude().length() == 0 ||location.getLattitude() == null || location.getLattitude().length() == 0 || location.getAddress().length() == 0 || location.getAddress().equalsIgnoreCase(Constants.ERROR_NO_ADDRESS_FOUND)){
			return;
		}
		deleteAllRecords(LocationDao.CURRENT_LOCATION_TABLE_NAME);
		LocationDao locationDao = new LocationDao(db);		
		locationDao.insertLocationData(location.getLongitude(), location.getLattitude(), location.getAddress());
	}
	
	public synchronized CurrentLocation getLocationData(){
		LocationDao locationDao = new LocationDao(db);			
		return locationDao.getLocationData();
	}
	
	public synchronized String getRsvpById(String id){
		LisnDao lisnDao = new LisnDao(db);	
		return lisnDao.getRsvpById(id);
	}
	
	public synchronized int updateRsvp(String id){
		LisnDao lisnDao = new LisnDao(db);	
		return lisnDao.updateRsvp(id);
	}
}