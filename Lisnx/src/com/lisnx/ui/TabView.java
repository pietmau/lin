package com.lisnx.ui;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;

import com.lisnx.android.activity.R;
import com.lisnx.android.activity.CreateLisnActivity;
import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.android.activity.PastLisnActivity;

@SuppressWarnings("deprecation")
public class TabView extends TabActivity implements TabBarLayout.OnTabChangedListener{
	
	private TabBarLayout tabBar;
	private TabButtonLayout nowLisnTabButton;
	private TabButtonLayout createLisnTabButton;
	private TabButtonLayout pastLisnTabButton;
	private TabHost tabHost;
	private ProgressDialog pd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
		setContentView(R.layout.tab_view);
		
		ImageView nowLisnImage=(ImageView) findViewById(R.id.now_lisn_image);
		nowLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_now_lisn)));
		ImageView createLisnImage=(ImageView) findViewById(R.id.create_lisn_image);
		createLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_create_lisn_gray)));
		ImageView pastLisnImage=(ImageView) findViewById(R.id.past_lisn_image);
		pastLisnImage.setBackgroundDrawable((getResources().getDrawable(R.drawable.ic_past_lisn_gray)));
		
		try{
			new ProcessUITask().execute();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void findViews() {
		tabBar = (TabBarLayout) findViewById(R.id.maintabbar);
		nowLisnTabButton = (TabButtonLayout) findViewById(R.id.now_lisn_tabbutton);
		createLisnTabButton = (TabButtonLayout) findViewById(R.id.create_lisn_tabbutton);
		pastLisnTabButton = (TabButtonLayout) findViewById(R.id.past_lisn_tabbutton);
	}
	
	private void setListeners() {
		tabBar.setOnTabChangedListener(this);
		nowLisnTabButton.setOnTouchListener(nowLisnTabButton);
		nowLisnTabButton.setOnClickListener(tabBar);
		createLisnTabButton.setOnTouchListener(createLisnTabButton);
		createLisnTabButton.setOnClickListener(tabBar);
		pastLisnTabButton.setOnTouchListener(pastLisnTabButton);
		pastLisnTabButton.setOnClickListener(tabBar);
	}
	
	private void setupTabBar() {
		nowLisnTabButton.registerTabBar(tabBar);
		createLisnTabButton.registerTabBar(tabBar);
		pastLisnTabButton.registerTabBar(tabBar);
		tabBar.selectTab(nowLisnTabButton);
	}

	public void onTabChanged(int tabId) {
		switch (tabId) {
        case R.id.now_lisn_tabbutton:
            tabHost.setCurrentTab(0);
            break;
        case R.id.create_lisn_tabbutton:
        	tabHost.setCurrentTab(1);
            break;
        case R.id.past_lisn_tabbutton:
        	tabHost.setCurrentTab(2);
            break;
        }
	}
	
	public void launchActivity(int activityId) {
		tabHost.setCurrentTab(activityId);
	}
	
	private class ProcessUITask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
        	pd = ProgressDialog.show(TabView.this, null, TabView.this.getResources().getString(R.string.lodingMessage), true);
        }	

        @Override
        protected Void doInBackground(Void... params) 
        {
        	runOnUiThread(new Runnable() {
                public void run() {
                	tabHost = getTabHost();   
            	    TabHost.TabSpec spec;
            	    Intent intent;

            	    intent = new Intent(TabView.this.getApplicationContext(), NowLisnActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
            	    spec = tabHost.newTabSpec("NowTab").setIndicator("NowTab").setContent(intent); 
            	    tabHost.addTab(spec);  
            	    
            	    intent = new Intent(TabView.this.getApplicationContext(), CreateLisnActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
            	    spec = tabHost.newTabSpec("CreateTab").setIndicator("CreateTab").setContent(intent);  
            	    tabHost.addTab(spec); 
            	    
            	    intent = new Intent(TabView.this.getApplicationContext(), PastLisnActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
            	    spec = tabHost.newTabSpec("PastTab").setIndicator("PastTab").setContent(intent);
            	    tabHost.addTab(spec); 
            	    
            	    tabHost.setCurrentTab(0);
            	    
            	    findViews();
            	    setListeners();
            	    setupTabBar();
            	    
                }});
        	return null;
        }

        @Override
        protected void onPostExecute(Void result){
        	if(pd != null)
        		pd.dismiss();
        }
    }
}