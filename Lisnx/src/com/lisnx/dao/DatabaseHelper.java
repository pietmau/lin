package com.lisnx.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper {
	private static final String DATABASE_NAME = "lisnx.db";			
	private static final int DATABASE_VERSION = 1;								

    private final Context context;    		
    private DBHelper dbHelper;				
    private SQLiteDatabase db = null;	
    private boolean isOpen = false;
    
    public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public DatabaseHelper(Context ctx) {    	
    	this.context = ctx;
    	dbHelper = new DBHelper(context);
    }
    
    private static class DBHelper extends SQLiteOpenHelper {
    	public DBHelper(Context context){
    		super(context,DATABASE_NAME,null,DATABASE_VERSION);
    	}
    	
    	@Override
    	public void onCreate(SQLiteDatabase db){
			db.execSQL(TokenDao.ACCESS_TOKEN_TABLE_CREATE_COMMAND);
			db.execSQL(LocationDao.CURRENT_LOCATION_TABLE_CREATE_COMMAND);
			db.execSQL(LisnDao.LISN_TABLE_CREATE_COMMAND);
			db.execSQL(PastLisnDao.PAST_LISN_TABLE_CREATE_COMMAND);
    	}
    	
    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ 
              db.execSQL("DROP TABLE IF EXISTS " + TokenDao.ACCESS_TOKEN_TABLE_NAME);
              db.execSQL("DROP TABLE IF EXISTS " + LocationDao.CURRENT_LOCATION_TABLE_NAME);
              db.execSQL("DROP TABLE IF EXISTS " + LisnDao.LISN_TABLE_NAME);
              db.execSQL("DROP TABLE IF EXISTS " + PastLisnDao.PAST_LISN_TABLE_CREATE_COMMAND);
              onCreate(db);
    	}
    }
    
    public SQLiteDatabase getSQLiteDatabaseObject()
    {
    	return db;
    }
    
    public DatabaseHelper open() throws SQLException 
    {
    	try {
	        db = dbHelper.getWritableDatabase();	
	        isOpen = true;
    	} catch (Exception ex) {
		}
    	return this;
    }    
    
    public void close() 
    {
    	try {
	    	dbHelper.close();	
    	} catch (Exception ex) {
		}	    	
    }
}