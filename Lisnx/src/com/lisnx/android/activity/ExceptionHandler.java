package com.lisnx.android.activity;

import com.lisnx.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;

//Is this a duplicate of com.lisnx.event.ExceptionHandler.java? If so, REMOVE this.

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	protected static final LayoutInflater LayoutInflater = null;
	private final Context myContext;

	public ExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		
		Intent intent = new Intent(myContext, MenuActivity.class );
		intent.putExtra("errorToast", "App crashed so you are redirecting to Menu screen");
		//if(Utils.isDev())
			Log.i("ExceptionHandler",Log.getStackTraceString(exception));
		myContext.startActivity(intent);
		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
