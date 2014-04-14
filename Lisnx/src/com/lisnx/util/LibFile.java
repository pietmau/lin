package com.lisnx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class LibFile 
{
	Context context;
	
	SharedPreferences settings;
	
	private static LibFile instance;
	
	public static LibFile getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new LibFile(context);
			
		}
		return instance;
	}
	private LibFile(Context context)
	{	
		this.context  = context;
		
	    settings = context.getSharedPreferences(Constants.PREFS_FILE_NAME_PARAM , 0);

	}
	public String getAccessToken()
	{
		 return settings.getString(Constants.SHARED_PREFERENCES_PARAM , null);		
	}
	public void setAccessToken(String token)
	{
		if(token == null)
		{
			settings.edit().remove(Constants.SHARED_PREFERENCES_PARAM).commit();
		}
		else
		{
			settings.edit().putString(Constants.SHARED_PREFERENCES_PARAM, token).commit();	
		}
	}
	
	
	
}
