package com.lisnx.service;

import com.lisnx.android.activity.LisnDetailActivity;
import com.lisnx.android.activity.MessageBoardActivity;

public class TimerStopper {

	public static void stopTimers(){
		try{
			if(LisnDetailActivity.lisnDetailTimer != null){
				LisnDetailActivity.lisnDetailTimer.cancel();
			}
		
			if(MessageBoardActivity.timer != null){
				MessageBoardActivity.timer.cancel();
			} 
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
