package com.lisnx.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.event.ILisner;
import com.lisnx.model.Lisn;
import com.lisnx.model.PastLisnViewHolder;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class PastLisnActivity extends BaseActivity implements ILisner,OnClickListener,Runnable{
	
	@SuppressWarnings("unused")
	private EventNotifier eventNotifier;
	private ArrayList <HashMap<String, Object>> availableLisn;
	public static final String LISN_DETAIL_KEY = "lisnDetail";
	public static final String LISN_NAME_KEY = "lisnName";
	public static final String LISNER_COUNT_KEY = "lisnerCount";
	public static final String TOTAL_MESSAGE_KEY = "totalMessage";
	public static final String IDKEY = "id";
	public static ListView listView = null;
	public boolean addHeader = true;
	public ListAdapter adapter = null;
	public boolean noLisn=false;
	ImageView backIcon;
	ImageView peopleNearByIcon ;
	ImageView friendRequestsIcon ;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.past_lisn);
        layoutInflater = this.getLayoutInflater();
        eventNotifier = new EventNotifier (this);
        
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(this);
        peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
        peopleNearByIcon.setOnClickListener(this);
        friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
        friendRequestsIcon.setOnClickListener(this);
           
        listView = (ListView)findViewById(R.id.pastLisnList);
        /*nameText = (TextView) findViewById(R.id.userName);
        nameText.setOnClickListener(this);
        userImage=(ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(this);*/
        
        try{
        	runOnUiThread(new Runnable() {
        		public void run() {
        			GetPastLisnData GetPastLisnData = new GetPastLisnData();
        			GetPastLisnData.execute();
        		}
        	});
        } catch(Exception e){
        	handler.sendEmptyMessage(2);
        	e.printStackTrace();
        }
        
      //VISH updateNotificationAndPeopleNearbyCounts();
    }
	
	private class GetPastLisnData extends AsyncTask<Void, Void, List<Lisn>>{
		public GetPastLisnData(){
			super();
		}
		
		@Override
        protected void onPreExecute(){
			pd = ProgressDialog.show(PastLisnActivity.this, null, PastLisnActivity.this.getResources().getString(R.string.lodingMessage), true);
		}
		
		@Override
		protected List<Lisn> doInBackground(Void... params) {
			return getPastLisn();
		}
		
		@Override
        protected void onPostExecute(List<Lisn> pastLisns){
			List<Lisn> dataExist = pastLisns;
            if(dataExist != null){
            	displayLisns(dataExist);
    		} else{
    			handler.sendEmptyMessage(2);
            }
			handler.sendEmptyMessage(2);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void displayLisns(List<Lisn> dataExist){
		List<Lisn> lisnList=dataExist;
		availableLisn = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> pastLisnData = null;;
        Lisn lisn = null;
        
        for(int i = 0 ; i < lisnList.size() ; i++){
        	lisn = lisnList.get(i);
        	pastLisnData = new HashMap<String, Object>();
        	pastLisnData.put(LISN_DETAIL_KEY, lisn.getDescription());
        	pastLisnData.put(LISN_NAME_KEY, lisn.getName());
        	pastLisnData.put(IDKEY, lisn.getLisnID());
        	pastLisnData.put(LISNER_COUNT_KEY, lisn.getMember());
        	pastLisnData.put(TOTAL_MESSAGE_KEY, lisn.getTotalMessage());
        	availableLisn.add(pastLisnData);
        }
       
        TextView lisnCountDetails = (TextView) findViewById(R.id.pastLisnCountDetails); 
        if((lisnList != null) && (!lisnList.isEmpty())){
       	    lisnCountDetails.setText((lisnList.get(0).getCountLisns() + ""));
        } else{
    	    lisnCountDetails.setText("No Past Lisns.");
    	    listView.setVisibility(View.GONE);
       	    View linearLayout =  findViewById(R.id.past_lisn_view);
            TextView noLisnText = new TextView(this);
            noLisnText.setText(R.string.noPastLisnText);
            noLisnText.setId(5);
            noLisnText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
            noLisnText.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            noLisnText.setTextColor(Color.BLACK);
            ((LinearLayout) linearLayout).addView(noLisnText);
        }
       
        adapter = new ListAdapter(availableLisn,PastLisnActivity.this);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        OnItemClickListener listener = new OnItemClickListener (){
    		@Override
    		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> lisn = (HashMap<String, Object>) adapter.getAdapter().getItem(position);
    			try	{   
            		Intent lisnDetailIntent = new Intent(view.getContext(), LisnDetailTabView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    	Bundle bundleObj = new Bundle();	
    		    	bundleObj.putString("id", (String)lisn.get(IDKEY));
    		    	bundleObj.putString("RSVP","In");			
    		    	lisnDetailIntent.putExtras(bundleObj);
    		    	view.getContext().startActivity(lisnDetailIntent);
    		    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		//Specify an explicit transition animation to perform next
    		    	
    	    	} catch(Exception ex) {
    	    	}
    		}
        };
        listView.setOnItemClickListener (listener);
	}

	public List<Lisn> getPastLisn(){
		String timeZone=Utils.getTimeZone();
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return null;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(1);
			return null;
		}
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);	
		params.put(Constants.TIME_STAMP,timeZone);
		
		List<Lisn> lisnList=null;
		try {
			lisnList = Utils.getPastLisn(params,this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}
		return lisnList;
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(getApplicationContext(), Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}else if(msg.what == 2){
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 3){
				CustomToast.showCustomToast(getApplicationContext(), msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 4){
				CustomToast.showCustomToast(getApplicationContext(), Constants.DATABASE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 5){
				CustomToast.showCustomToast(getApplicationContext(), Constants.LOCATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 11){
				CustomToast.showCustomToast(getApplicationContext(), Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if(pd != null)
					pd.dismiss();
			}
			else if(msg.what == 12){
				if(pd != null)
					pd.dismiss();
			}
		}
	};

	@Override
	public void run() {}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		   case R.id.backIcon:
			   backIcon.setAlpha(100);
			   navigateMenuScreen(view);
			   break;
		   /*case R.id.userName:
			   navigateProfileScreen(view);
			   break;
		   case R.id.userImage:
			   navigateProfileScreen(view);
			   break;*/
		   case R.id.peopleNearByNotification:
			   peopleNearByIcon.setAlpha(100);
			   navigatePeopleNearByScreen(view);
			   break;
		   case R.id.friendRequestNotification:
			   friendRequestsIcon.setAlpha(100);
			   navigateFriendRequestScreen(view);
			   break;
		}
	}
	
	public void navigateProfileScreen(View view){
		ProfileActivity.callingProfileScreen=Constants.CALLING_SCREEN_PAST_LISN;
		try	{   		
	    	Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);		
	    	startActivityForResult(profileIntent, 0);	
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
    	} catch(Exception ex) {
    	}
	}
	
	public void navigatePeopleNearByScreen(View view){
		PeopleNearByActivity.callingPeopleNearByScreen=Constants.CALLING_SCREEN_PAST_LISN;
		try	{   		
	    	Intent peopleNearByIntent = new Intent(view.getContext(), PeopleNearByActivity.class);	
	    	startActivityForResult(peopleNearByIntent, 0);
	    	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);		
    	} catch(Exception ex) {
    	}
	}
	
	
	
	public void refreshPastLisn(){
		runOnUiThread(new Runnable() {
            public void run() {
        		addHeader = false;
        		pd = ProgressDialog.show(PastLisnActivity.this, null, PastLisnActivity.this.getResources().getString(R.string.lodingMessage), true);
        		List<Lisn> dataExist = getPastLisn();
                if(dataExist != null){
        			displayLisns(dataExist);
        		}
                addHeader = true;
                handler.sendEmptyMessage(2);
           }
       });
	}
	/*
	@Override
	public void onBackPressed() {
		try {
			Intent loginIntent = new Intent(getApplicationContext(),
					MenuActivity.class);
			startActivityForResult(loginIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}

	}
	*/
	private class ListAdapter extends BaseAdapter{
		private ArrayList<HashMap<String, Object>> pastLisns; 
		private LayoutInflater mInflater;
		
		public ListAdapter(ArrayList<HashMap<String, Object>> pastLisns, Context context){
			this.pastLisns = pastLisns;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return pastLisns.size();
		}

		@Override
		public Object getItem(int position) {
			return pastLisns.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			PastLisnViewHolder holder;
		
			 if (convertView == null) {
	             convertView = mInflater.inflate(R.layout.past_lisn_list_view, null);
	         
	             holder = new PastLisnViewHolder();
	             holder.lisnName = (TextView) convertView.findViewById(R.id.pastLisnName);
	             holder.lisnerImage = (ImageView) convertView.findViewById(R.id.lisnerImage);
	             holder.numberOfLisn = (TextView) convertView.findViewById(R.id.pastLisnCount);
	             holder.numberOfMessages = (TextView) convertView.findViewById(R.id.messageCount);
	             holder.description=(TextView) convertView.findViewById(R.id.pastLisnDescription);
	             convertView.setTag(holder);
			 }else {
				 holder = (PastLisnViewHolder) convertView.getTag(); 
			 }		 
			holder.lisnName.setText((String) pastLisns.get(position).get(PastLisnActivity.LISN_NAME_KEY));
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        switch(displayMetrics.densityDpi){ 
	        	case DisplayMetrics.DENSITY_LOW:
	        		holder.numberOfLisn.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        		holder.numberOfMessages.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
	        		break; 
	        }
			holder.numberOfLisn.setText((String) pastLisns.get(position).get(PastLisnActivity.LISNER_COUNT_KEY));
			holder.numberOfMessages.setText((String) pastLisns.get(position).get(PastLisnActivity.TOTAL_MESSAGE_KEY));
			
			String desc = (String)pastLisns.get(position).get(PastLisnActivity.LISN_DETAIL_KEY);
			holder.description.setText(desc);
			if((desc == null) || (desc.length()==0 || "null".equalsIgnoreCase(desc))){
				holder.description.setVisibility(View.GONE);
			} else{
				holder.description.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
	}

	@Override
	public void updateLocation() {
	}
}