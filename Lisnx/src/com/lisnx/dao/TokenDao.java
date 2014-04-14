package com.lisnx.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TokenDao {

	public static final String ACCESS_TOKEN_TABLE_NAME = "access_token";
	
	public static final String ACCESS_TOKEN_TABLE_KEY_ID = "_id";
	public static final String ACCESS_TOKEN_TABLE_KEY_TOKEN = "token";
	
	public static final int ACCESS_TOKEN_TABLE_KEY_ID_COLUMN_INDEX = 0;
	public static final int ACCESS_TOKEN_TABLE_KEY_TOKEN_COLUMN_INDEX = 1;
	
	public static final String ACCESS_TOKEN_TABLE_CREATE_COMMAND =
	        "CREATE TABLE " + ACCESS_TOKEN_TABLE_NAME + "(" + ACCESS_TOKEN_TABLE_KEY_ID + " integer primary key autoincrement, "
	        + ACCESS_TOKEN_TABLE_KEY_TOKEN + " text not null);" ;
		
	private DatabaseHelper db = null;		
		
	public TokenDao(DatabaseHelper db) {
		this.db = db;
	}
		
    public boolean insertAccessToken(String accessToken) 
    {  
    	boolean exception = false;
    	
    	try {
    		db.open();
    		long check = 0;	    
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
	        ContentValues initialValues = new ContentValues();
	        
	        initialValues.put(ACCESS_TOKEN_TABLE_KEY_TOKEN, accessToken);
	        check = sqLite.insert(ACCESS_TOKEN_TABLE_NAME, null, initialValues);		
	        
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
    
    public String getAccessToken() {
    	String accessToken = "";
    	
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
			Cursor cur = sqLite.query(ACCESS_TOKEN_TABLE_NAME, new String[] {ACCESS_TOKEN_TABLE_KEY_TOKEN}, null, null, null, null, null);
			cur.moveToFirst();		
			accessToken = cur.getString(ACCESS_TOKEN_TABLE_KEY_TOKEN_COLUMN_INDEX - 1);
	        cur.close();	
    	} catch(Exception ex) {
    	}finally{
    		db.close();
    	}
    	return accessToken;		
    }    
}