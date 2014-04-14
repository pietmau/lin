package com.lisnx.service;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.lisnx.android.activity.R;

public class CustomToast extends Activity{

	public static void showCustomToast(Context context, String message, int duration, LayoutInflater layoutInflater){
		try{
			View layout = layoutInflater.inflate(R.layout.custom_toast, null);
			TextView text = (TextView) layout.findViewById(R.id.toastText);
			text.setText(message);

			Toast toast = new Toast(context);
			toast.setDuration(duration);
			toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
			toast.setView(layout);
			toast.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
