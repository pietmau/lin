/*package com.lisnx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Status;
import com.lisnx.model.ViewHolder;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

public class ListAdapter extends BaseAdapter{

	private ProgressDialog pd;
	private static final String TAG = "ListAdapter";
	private ArrayList<HashMap<String, Object>> lisns; 
	private LayoutInflater mInflater;
	public static ArrayList<String> lisnId = new ArrayList<String>();
	Context context;
	
	public ListAdapter(ArrayList<HashMap<String, Object>> lisns, Context context){
		this.lisns = lisns;
		mInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		return lisns.size();
	}

	@Override
	public Object getItem(int position) {
		return lisns.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
		ViewHolder holder;
		
		// When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null
		 if (convertView == null) {
             convertView = mInflater.inflate(R.layout.list_view, null);
             // Creates a ViewHolder and store references to the two children views
             // we want to bind data to.
             holder = new ViewHolder();
             holder.lisnDetail = (TextView) convertView.findViewById(R.id.lisnDetail);
             holder.numberOfLisn = (TextView) convertView.findViewById(R.id.lisnerCount);
             holder.button = (Button) convertView.findViewById(R.id.lisnButton);
             convertView.setTag(holder);
                
		 }else {
			 // Get the ViewHolder back to get fast access to the TextView
             // and the ImageView.
			 holder = (ViewHolder) convertView.getTag(); 
		 }
		 
		 if(position % 2 == 0){
			 convertView.setBackgroundDrawable(convertView.getContext().getResources().getDrawable(R.drawable.list_even));
		 }
		 else{
			 convertView.setBackgroundDrawable(convertView.getContext().getResources().getDrawable(R.drawable.list_odd));
		 }
		 
	 	// Bind the data with the holder.
	 
		holder.lisnDetail.setText((String) lisns.get(position).get(NowLisnActivity.LISN_DETAIL_KEY));
		
		holder.numberOfLisn.setText((String) lisns.get(position).get(NowLisnActivity.LISNER_COUNT_KEY));
		
		holder.button.setText(lisns.get(position).get(NowLisnActivity.LISN_KEY).toString());
		lisnId.add(lisns.get(position).get(NowLisnActivity.IDKEY).toString());
		
		holder.button.setOnClickListener( new View.OnClickListener(){
        public void onClick(View view){
        	Message msg = new Message();
			msg.what = 1;
			Bundle bundle = new Bundle();
			bundle.putInt("position", position);
			context = view.getContext();
			msg.setData(bundle);
			handler.sendMessage(msg);
        }});
		
		return convertView;
	}
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				joinLisn(lisnId.get(msg.getData().getInt("position")));
			}
			else if(msg.what == 2){
				Utils.showErrorMessage(context, Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG);
			}
			else if(msg.what == 3){
				Utils.showErrorMessage(context, Constants.LISN_JOINED_ALREADY_ERROR, Constants.TOAST_VISIBLE_LONG);
			}
		}
	};
	
	public void joinLisn(String id){
		Log.i(TAG, "joinLisn()");
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		String rsvp = dao.getRsvpById(id);
		if(rsvp == null){
			handler.sendEmptyMessage(2);
			return;
		}
		if(rsvp.trim().equalsIgnoreCase(Constants.RSVP)){
			handler.sendEmptyMessage(3);
			return;
		}
		pd = ProgressDialog.show(context, null, context.getResources().getString(R.string.joinLisn), true);
		joinLisnAndUpdateLisn(id);
		context = null;
	}
	
	public void joinLisnAndUpdateLisn(String id){
		Log.i(TAG, "joinLisnAndUpdateLisn()");
		//Get Access Token
		
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(2);
			return;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(2);
			return;
		}
		
		
		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LISN_ID_PARAM, id);
		
		Status status = null;
		
		try {
			status = Utils.joinLisn(params, context);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(2);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(2);
			return;
		}
		
		// If lisn joined successfully then update lisn
		if(status.getStatus().equalsIgnoreCase(Constants.SUCCESS)){
			// ArrayList<HashMap<String, Object>> lisns;  get item at that poisition and change value
			notifyDataSetChanged();
		}
		else{
			handler.sendEmptyMessage(2);
			return;
		}
		
	}
}
*/