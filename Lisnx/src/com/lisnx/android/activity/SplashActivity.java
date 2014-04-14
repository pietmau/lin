package com.lisnx.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Unnecessary native threading removed.
 * Code optimized to use single xml. We need two images for landscape and portrait mode not two layout xmls.
 * Need to write event on click not touch. Touch event fires multiple time when user make single click
 */
public class SplashActivity extends Activity {
	
	private Timer tSplash;
	
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        initData();
        
        initUi();
        
    }
	
	private void initData()
	{
		tSplash = new Timer();
		
		tSplash.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						gotoLogin();
						
					}
				});
				
				
			}
		}, 4000);
	}
	
	private void initUi()
	{
        setContentView(R.layout.splash);
	
		findViewById(R.id.ivSplashImage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				gotoLogin();
				
			}
		});
	}
	
	private void gotoLogin()
	{
		tSplash.cancel();
		
        Intent i = new Intent(getApplicationContext(),FacebookActivity.class);
        
		startActivity(i);
		
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        overridePendingTransition(R.anim.slide_in_from_right_to_left,R.anim.slide_out_from_right_to_left);
        
        finish();
		
	}
	
}
/*
 
---------------- Previous Code - Vish Commented ------------------  

public class SplashActivity extends Activity {
    private Thread mSplashThread;
    Intent i;
    final SplashActivity sPlashScreen = this;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.splash);
        
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(4000);
                    }
                }catch(InterruptedException ex){                   
                }
                finish();
                
                i=new Intent(getApplicationContext(),LoginActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    			startActivity(i);
    			interrupt();
            }
            
        }; 
        mSplashThread.start();        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }  
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.splash);
        
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(4000);
                    }
                }catch(InterruptedException ex){                   
                }
                finish();
                
                i=new Intent(getApplicationContext(),LoginActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    			startActivity(i);
            
    			interrupt();
            }
            
        }; 
        mSplashThread.start(); 
    }   
} 
*/