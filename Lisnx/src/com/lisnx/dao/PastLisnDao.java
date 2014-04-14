package com.lisnx.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.lisnx.model.Lisn;

public class PastLisnDao {
	
	public static final String PAST_LISN_TABLE_NAME = "past_lisn";
	
	public static final String PAST_LISN_TABLE_KEY_ID = "_id";
	public static final String PAST_LISN_TABLE_KEY_LISN_ID = "lisn_id";
	public static final String PAST_LISN_TABLE_KEY_NAME = "name";
	public static final String PAST_LISN_TABLE_KEY_DESCRIPTION = "description";
	public static final String PAST_LISN_TABLE_KEY_START_DATE = "start_date";
	public static final String PAST_LISN_TABLE_KEY_END_DATE = "end_date";
	public static final String PAST_LISN_TABLE_KEY_VENUE = "venue";
	public static final String PAST_LISN_TABLE_KEY_MEMBER = "member";
	public static final String PAST_LISN_TABLE_KEY_RSVP = "rsvp";
	public static final String PAST_LISN_TABLE_KEY_FRIENDS = "friends";
	public static final String PAST_LISN_TABLE_KEY_COUNT_LISN = "count_lisns";
	public static final String PAST_LISN_TABLE_KEY_COUNT_JOINED_LISN = "count_joined_lisn";
	
	public static final int PAST_LISN_TABLE_KEY_ID_COLUMN_INDEX = 0;
	public static final int PAST_LISN_TABLE_KEY_LISN_ID_COLUMN_INDEX = 1;
	public static final int PAST_LISN_TABLE_KEY_NAME_COLUMN_INDEX = 2;
	public static final int PAST_LISN_TABLE_KEY_DESCRIPTION_COLUMN_INDEX = 3;
	public static final int PAST_LISN_TABLE_KEY_START_DATE_COLUMN_INDEX = 4;
	public static final int PAST_LISN_TABLE_KEY_END_DATE_COLUMN_INDEX = 5;
	public static final int PAST_LISN_TABLE_KEY_VENUE_COLUMN_INDEX = 6;
	public static final int PAST_LISN_TABLE_KEY_MEMBER_COLUMN_INDEX = 7;
	public static final int PAST_LISN_TABLE_KEY_RSVP_COLUMN_INDEX = 8;
	public static final int PAST_LISN_TABLE_KEY_FRIENDS_COLUMN_INDEX = 9;
	public static final int PAST_LISN_TABLE_KEY_COUNT_LISN_COLUMN_INDEX = 10;
	public static final int PAST_LISN_TABLE_KEY_COUNT_JOINED_COLUMN_INDEX = 11;
	
	public static final String PAST_LISN_TABLE_CREATE_COMMAND =
	        "CREATE TABLE " + PAST_LISN_TABLE_NAME + "(" + PAST_LISN_TABLE_KEY_ID + " integer primary key autoincrement, "
	        + PAST_LISN_TABLE_KEY_LISN_ID + " text not null, " + PAST_LISN_TABLE_KEY_NAME + " text not null, " 
	        + PAST_LISN_TABLE_KEY_DESCRIPTION + " text not null, " + PAST_LISN_TABLE_KEY_START_DATE + " text not null, " 
	        + PAST_LISN_TABLE_KEY_END_DATE + " text not null, " + PAST_LISN_TABLE_KEY_VENUE + " text not null, " 
	        + PAST_LISN_TABLE_KEY_MEMBER + " text not null, " + PAST_LISN_TABLE_KEY_RSVP + " text not null, " 
	        + PAST_LISN_TABLE_KEY_FRIENDS + " text not null,"
	        + PAST_LISN_TABLE_KEY_COUNT_LISN + " text not null, " + PAST_LISN_TABLE_KEY_COUNT_JOINED_LISN + " text not null);";
		
	private DatabaseHelper db = null;		
		
	public PastLisnDao(DatabaseHelper db) {
		this.db = db;
	}
		
    public boolean insertPastLisnData(List<Lisn> lisnList) 
    {  
    	Lisn lisn = null;
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
	        ContentValues initialValues = new ContentValues();
	        for(int i = 0 ; i < lisnList.size() ; i++){
	        	lisn = lisnList.get(i);
		        initialValues.put(PAST_LISN_TABLE_KEY_LISN_ID, lisn.getLisnID());
		        initialValues.put(PAST_LISN_TABLE_KEY_NAME, lisn.getName());
		        initialValues.put(PAST_LISN_TABLE_KEY_DESCRIPTION, lisn.getDescription());
		        initialValues.put(PAST_LISN_TABLE_KEY_START_DATE, lisn.getStartDate());
		        initialValues.put(PAST_LISN_TABLE_KEY_END_DATE, lisn.getEndDate());
		        initialValues.put(PAST_LISN_TABLE_KEY_VENUE, lisn.getVenue());
		        initialValues.put(PAST_LISN_TABLE_KEY_MEMBER, lisn.getMember());
		        initialValues.put(PAST_LISN_TABLE_KEY_RSVP, lisn.getRsvp());
		        initialValues.put(PAST_LISN_TABLE_KEY_FRIENDS, lisn.getFriend());
		        initialValues.put(PAST_LISN_TABLE_KEY_COUNT_LISN, lisn.getCountLisns());
		        initialValues.put(PAST_LISN_TABLE_KEY_COUNT_JOINED_LISN, lisn.getCountJoinedLisns());
		           
		        sqLite.insert(PAST_LISN_TABLE_NAME, null, initialValues);		
	        }
    	} catch (Exception ex) {
    		return false;
		}finally{
			db.close();
		}        
        return true;
    }
    
    public List<Lisn> getPastLisnData() throws SQLException {
    	List<Lisn> lisnList = new ArrayList<Lisn>();
    	Lisn lisn = null;
    	
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		Cursor cursor = null;
			cursor = sqLite.query(PAST_LISN_TABLE_NAME, null, null, null, null, null, null);
			cursor.moveToFirst();		
	        while (cursor.isAfterLast() == false) {
	        	lisn = new Lisn();
	        	
	        	lisn.setLisnID(cursor.getString(PAST_LISN_TABLE_KEY_LISN_ID_COLUMN_INDEX));
	        	lisn.setDescription(cursor.getString(PAST_LISN_TABLE_KEY_DESCRIPTION_COLUMN_INDEX));
	        	lisn.setEndDate(cursor.getString(PAST_LISN_TABLE_KEY_END_DATE_COLUMN_INDEX));
	        	lisn.setFriend(cursor.getString(PAST_LISN_TABLE_KEY_FRIENDS_COLUMN_INDEX));
	        	lisn.setMember(cursor.getString(PAST_LISN_TABLE_KEY_MEMBER_COLUMN_INDEX));
	        	lisn.setName(cursor.getString(PAST_LISN_TABLE_KEY_NAME_COLUMN_INDEX));
	        	lisn.setRsvp(cursor.getString(PAST_LISN_TABLE_KEY_RSVP_COLUMN_INDEX));
	        	lisn.setStartDate(cursor.getString(PAST_LISN_TABLE_KEY_START_DATE_COLUMN_INDEX));
	        	lisn.setVenue(cursor.getString(PAST_LISN_TABLE_KEY_VENUE_COLUMN_INDEX));
	        	lisn.setCountLisns(cursor.getString(PAST_LISN_TABLE_KEY_COUNT_LISN_COLUMN_INDEX));
	        	lisn.setCountJoinedLisns(cursor.getString(PAST_LISN_TABLE_KEY_COUNT_JOINED_COLUMN_INDEX));
	        	lisnList.add(lisn);
	        	cursor.moveToNext();
	        }
			
    		cursor.close();		
    	} catch(Exception ex) {
    		throw new SQLException();
    	}finally{
    		db.close();
    	}
    	return lisnList;		
    }    
	
    public boolean checkPastLisnRecordExist() {
    	
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		long numberOfRecords = DatabaseUtils.queryNumEntries(sqLite,PAST_LISN_TABLE_NAME);
    		if (numberOfRecords > 0){
    			return true;
    		}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}finally{
    		db.close();
    	}
    	return false;		
    }    
}
