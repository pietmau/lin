package com.lisnx.event;

import com.lisnx.android.activity.MenuActivity;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	protected static final LayoutInflater LayoutInflater = null;
	private final Context myContext;

	public ExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		
		Intent intent = new Intent(myContext, MenuActivity.class );
		intent.putExtra("errorToast", Constants.APP_CRASH_MSG);
		if(Utils.isDev())
			Log.i("com.lisnx.event.ExceptionHandler",Log.getStackTraceString(exception));
		myContext.startActivity(intent);

		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
