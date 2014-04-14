package com.lisnx.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.lisnx.model.Lisn;
import com.lisnx.util.Constants;

public class LisnDao {
	
	public static final String LISN_TABLE_NAME = "lisn";
	
	public static final String LISN_TABLE_KEY_ID = "_id";
	public static final String LISN_TABLE_KEY_LISN_ID = "lisn_id";
	public static final String LISN_TABLE_KEY_NAME = "name";
	public static final String LISN_TABLE_KEY_DESCRIPTION = "description";
	public static final String LISN_TABLE_KEY_START_DATE = "start_date";
	public static final String LISN_TABLE_KEY_END_DATE = "end_date";
	public static final String LISN_TABLE_KEY_VENUE = "venue";
	public static final String LISN_TABLE_KEY_MEMBER = "member";
	public static final String LISN_TABLE_KEY_RSVP = "rsvp";
	public static final String LISN_TABLE_KEY_FRIENDS = "friends";
	public static final String LISN_TABLE_KEY_COUNT_LISN = "count_lisns";
	public static final String LISN_TABLE_KEY_COUNT_JOINED_LISN = "count_joined_lisn";
	
	public static final int LISN_TABLE_KEY_ID_COLUMN_INDEX = 0;
	public static final int LISN_TABLE_KEY_LISN_ID_COLUMN_INDEX = 1;
	public static final int LISN_TABLE_KEY_NAME_COLUMN_INDEX = 2;
	public static final int LISN_TABLE_KEY_DESCRIPTION_COLUMN_INDEX = 3;
	public static final int LISN_TABLE_KEY_START_DATE_COLUMN_INDEX = 4;
	public static final int LISN_TABLE_KEY_END_DATE_COLUMN_INDEX = 5;
	public static final int LISN_TABLE_KEY_VENUE_COLUMN_INDEX = 6;
	public static final int LISN_TABLE_KEY_MEMBER_COLUMN_INDEX = 7;
	public static final int LISN_TABLE_KEY_RSVP_COLUMN_INDEX = 8;
	public static final int LISN_TABLE_KEY_FRIENDS_COLUMN_INDEX = 9;
	public static final int LISN_TABLE_KEY_COUNT_LISN_COLUMN_INDEX = 10;
	public static final int LISN_TABLE_KEY_COUNT_JOINED_COLUMN_INDEX = 11;
	
	public static final String LISN_TABLE_CREATE_COMMAND =
	        "CREATE TABLE " + LISN_TABLE_NAME + "(" + LISN_TABLE_KEY_ID + " integer primary key autoincrement, "
	        + LISN_TABLE_KEY_LISN_ID + " text not null, " + LISN_TABLE_KEY_NAME + " text not null, " 
	        + LISN_TABLE_KEY_DESCRIPTION + " text not null, " + LISN_TABLE_KEY_START_DATE + " text not null, " 
	        + LISN_TABLE_KEY_END_DATE + " text not null, " + LISN_TABLE_KEY_VENUE + " text not null, " 
	        + LISN_TABLE_KEY_MEMBER + " text not null, " + LISN_TABLE_KEY_RSVP + " text not null, " 
	        + LISN_TABLE_KEY_FRIENDS + " text not null,"
	        + LISN_TABLE_KEY_COUNT_LISN + " text not null, " + LISN_TABLE_KEY_COUNT_JOINED_LISN + " text not null);";
		
	private DatabaseHelper db = null;		
		
	public LisnDao(DatabaseHelper db) {
		this.db = db;
	}
		
    public boolean insertLisnData(List<Lisn> lisnList) {  
    	Lisn lisn = null;
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
	        ContentValues initialValues = new ContentValues();
	        for(int i = 0 ; i < lisnList.size() ; i++){
	        	lisn = lisnList.get(i);
		        initialValues.put(LISN_TABLE_KEY_LISN_ID, lisn.getLisnID());
		        initialValues.put(LISN_TABLE_KEY_NAME, lisn.getName());
		        initialValues.put(LISN_TABLE_KEY_DESCRIPTION, lisn.getDescription());
		        initialValues.put(LISN_TABLE_KEY_START_DATE, lisn.getStartDate());
		        initialValues.put(LISN_TABLE_KEY_END_DATE, lisn.getEndDate());
		        initialValues.put(LISN_TABLE_KEY_VENUE, lisn.getVenue());
		        initialValues.put(LISN_TABLE_KEY_MEMBER, lisn.getMember());
		        initialValues.put(LISN_TABLE_KEY_RSVP, lisn.getRsvp());
		        initialValues.put(LISN_TABLE_KEY_FRIENDS, lisn.getFriend());
		        initialValues.put(LISN_TABLE_KEY_COUNT_LISN, lisn.getCountLisns());
		        initialValues.put(LISN_TABLE_KEY_COUNT_JOINED_LISN, lisn.getCountJoinedLisns());
		           
		        sqLite.insert(LISN_TABLE_NAME, null, initialValues);	
	        }
    	} catch (Exception ex) {
    		return false;
		}finally{
			db.close();
		}        
        return true;
    }
    
    public List<Lisn> getLisnData() throws SQLException {
    	List<Lisn> lisnList = new ArrayList<Lisn>();
    	Lisn lisn = null;
    	
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		Cursor cursor = null;
			cursor = sqLite.query(LISN_TABLE_NAME, null, null, null, null, null, null);
			cursor.moveToFirst();	
	        while (cursor.isAfterLast() == false) {
	        	lisn = new Lisn();
	        	lisn.setLisnID(cursor.getString(LISN_TABLE_KEY_LISN_ID_COLUMN_INDEX));
	        	lisn.setDescription(cursor.getString(LISN_TABLE_KEY_DESCRIPTION_COLUMN_INDEX));
	        	lisn.setEndDate(cursor.getString(LISN_TABLE_KEY_END_DATE_COLUMN_INDEX));
	        	lisn.setFriend(cursor.getString(LISN_TABLE_KEY_FRIENDS_COLUMN_INDEX));
	        	lisn.setMember(cursor.getString(LISN_TABLE_KEY_MEMBER_COLUMN_INDEX));
	        	lisn.setName(cursor.getString(LISN_TABLE_KEY_NAME_COLUMN_INDEX));
	        	lisn.setRsvp(cursor.getString(LISN_TABLE_KEY_RSVP_COLUMN_INDEX));
	        	lisn.setStartDate(cursor.getString(LISN_TABLE_KEY_START_DATE_COLUMN_INDEX));
	        	lisn.setVenue(cursor.getString(LISN_TABLE_KEY_VENUE_COLUMN_INDEX));
	        	lisn.setCountLisns(cursor.getString(LISN_TABLE_KEY_COUNT_LISN_COLUMN_INDEX));
	        	lisn.setCountJoinedLisns(cursor.getString(LISN_TABLE_KEY_COUNT_JOINED_COLUMN_INDEX));
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
	
    public boolean checkLisnRecordExist() {
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		long numberOfRecords = DatabaseUtils.queryNumEntries(sqLite,LISN_TABLE_NAME);
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
	
    public String getRsvpById(String id) {
    	String rsvp = null;
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		Cursor cursor = null;
			cursor = sqLite.query(LISN_TABLE_NAME, new String[]{LISN_TABLE_KEY_RSVP}, LISN_TABLE_KEY_LISN_ID + " = ?", new String[] {id}, null, null, null);
			cursor.moveToFirst();
			rsvp = cursor.getString(0);
			if(cursor != null){
				cursor.close();
			}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}finally{
    		db.close();
    	}
    	return rsvp;		
    }    
    
    public int updateRsvp(String id){
    	try {
    		db.open();
    		SQLiteDatabase sqLite = db.getSQLiteDatabaseObject();
    		ContentValues values = new ContentValues();
    		values.put(LISN_TABLE_KEY_RSVP, Constants.RSVP_IN);
    		return sqLite.update(LISN_TABLE_NAME, values, LISN_TABLE_KEY_LISN_ID + "=?", new String[]{id});
    	} catch(Exception ex) {
    		return 0;
    	}finally{
    		db.close();
    	}
    }
}
