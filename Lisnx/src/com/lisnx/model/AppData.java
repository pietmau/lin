package com.lisnx.model;

import android.content.Context;

public class AppData 
{
	private static AppData instance;
	private Context context;
	private NotificationCount notificationObj;
	
	private AppData(Context  context )
	{
		this.context = context;
		setNotificationObj(new NotificationCount());
	}
	
	public static AppData getInstance(Context  context)
	{
		if(instance == null)
		{
			instance = new AppData(context);
		}
		return instance;
	}

	public NotificationCount getNotificationObj() 
	{
		return notificationObj;
	}

	public void setNotificationObj(NotificationCount notificationObj) 
	{
		this.notificationObj = notificationObj;
	}
}
