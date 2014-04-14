package com.lisnx.android.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.util.Constants;

	@SuppressLint("HandlerLeak")
	public class AboutUsActivity extends BaseActivity implements OnClickListener{
	private TextView abtText;
	ImageView backIcon;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);	
		layoutInflater = this.getLayoutInflater();
		
		backIcon = (ImageView) findViewById(R.id.backIcon);  
        backIcon.setOnClickListener(this);
        TextView versionCount=(TextView) findViewById(R.id.buildVersion);
        
        
        versionCount.setText(getVersion());   
        
        abtText=(TextView)findViewById(R.id.aboutText);
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			SetAboutUsContent setAboutUsContent = new SetAboutUsContent();
        			setAboutUsContent.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(2);
        	e.printStackTrace();
        }
        
        setListenersOnTopIcons(this);
        
	}
	
	private class SetAboutUsContent extends AsyncTask<Void, Void, String>{
		public SetAboutUsContent(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(AboutUsActivity.this, null, AboutUsActivity.this.getResources().getString(R.string.lodingMessage), true);
		}

		@Override
		protected String doInBackground(Void... params) {
			return getAboutUsContent();
		}
		
		@Override
        protected void onPostExecute(String aboutUsContent){
			try{
				abtText.setText(aboutUsContent);
				handler.sendEmptyMessage(2);
			} catch(Exception e){
				handler.sendEmptyMessage(2);
				//e.printStackTrace();
			}
		}
	}
	
	private String getAboutUsContent(){
		StringBuilder total = new StringBuilder();
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httppost = new HttpGet(Constants.ABOUT_US_URL);
			try{
			    HttpResponse response = httpclient.execute(httppost);
			    HttpEntity ht = response.getEntity();
			    try {
			        BufferedHttpEntity buf = new BufferedHttpEntity(ht);
			        InputStream is = buf.getContent();
			        BufferedReader r = new BufferedReader(new InputStreamReader(is));
			        String line;
			       
			        try {
			        	while ((line = r.readLine()) != null) {
			        		total.append(line + "\n");
			        	}
			        } catch(Exception ee){
			        	ee.printStackTrace();
			        }
			    } catch(Exception e){
			        e.printStackTrace();
			    }
			} catch(Exception ex){
				ex.printStackTrace();
			}
		} catch (NullPointerException e1) {
			handler.sendEmptyMessage(12);
		}
		
		return total.toString();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			if(msg.what == 2){
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 11){
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
			else if(msg.what == 12){
				//CustomToast.showCustomToast(getApplicationContext(), Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
				pd.dismiss();
			}
		}
	};
	
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   navigateFriendRequestScreen(view, Constants.CALLING_SCREEN_ABOUT_US);
			   break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.device_menu, menu);
	    setMenuBackground();
	    return true;
	}
	
	private void setMenuBackground() {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {
            public View onCreateView(final String name,final Context context,final AttributeSet attributeSet) {
            	
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {  
                    try {
                        final LayoutInflater f = getLayoutInflater();
                        final View view = f.createView(name, null, attributeSet);
                        
                        new Handler().post(new Runnable() {
                            public void run() {
                               view.setBackgroundResource(R.drawable.menu_selector);               
                            }
                        });
                        return view;
                    } catch (final Exception e) {                                                                        
                    }
                }
                return null;
            }
        });
    }
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return null;
	}
}