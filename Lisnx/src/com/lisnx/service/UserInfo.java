package com.lisnx.service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisnx.android.activity.NowLisnActivity;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Profile;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class UserInfo {
	
	private Context context=null;
	private int imageWidth=32*2;
	private int imageHeight=32*2;
	private TextView nameText;
	private ImageView userImage;
	public Bitmap userBitmap = null;
	public static Bitmap userImageFullBitMap = null;
	public LayoutInflater layoutInflater; 
	
	public UserInfo(){
	}
	
	public UserInfo(Context context, ImageView userImage, TextView nameText, LayoutInflater layoutInflater){
		this.context=context;
		this.userImage=userImage;
		this.nameText=nameText;
		this.layoutInflater = layoutInflater;
	}
	
	public void getProfileData(){
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return;
		}
		
		if(accessToken == null || accessToken.length() == 0){
			handler.sendEmptyMessage(1); 
			return;
		}

		Map<String , String> params = new HashMap<String,String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		
		Profile profile = null;
		try {
			profile = Utils.getProfileData(params, context);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		
		try{
			if(profile != null){
				NowLisnActivity.permanentName=profile.getFullname();
				nameText.setText(NowLisnActivity.permanentName);
				NowLisnActivity.permanentEmail=profile.getEmail();
		
				if("true".equalsIgnoreCase(profile.getIsImage())){
					download(userImage);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void download(ImageView imageView) {
		 BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
         task.execute();
    }
	
	class BitmapDownloaderTask extends AsyncTask<Void, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;

	    public BitmapDownloaderTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    @Override
	    protected Bitmap doInBackground(Void... params) {
	         return getImage();
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if(bitmap!=null){
	        	if (imageViewReference != null) {
	        		ImageView imageView = imageViewReference.get();
	        		if(imageView != null ){
	        			imageView.setBackgroundColor(Color.TRANSPARENT);
							imageView.setImageBitmap(new RoundedCornerBitmap().getRoundedCornerBitmap(bitmap));
	        		} 
	            }
	        }else {
        		ImageView imageView = imageViewReference.get();
	        	imageView.setImageBitmap(NowLisnActivity.defaultBitmap);
	        }
	    }
	}
	
	public Bitmap getImage(){
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
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
		
	    Bitmap bmImg = null;
		try {
			bmImg = Utils.getUserImage(params, context);
			userImageFullBitMap = bmImg;
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}
		
        if(bmImg==null){
        	handler.sendEmptyMessage(9);
        	return null;
        }else {
        		bmImg = Utils.scaleProportionally(bmImg,imageWidth, imageHeight);//Bitmap.createScaledBitmap(bmImg,imageWidth, imageHeight, true);
        		NowLisnActivity.permanentImage=new RoundedCornerBitmap().getRoundedCornerBitmap(bmImg);
        		handler.sendEmptyMessage(2);
        		return bmImg;
        }
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				CustomToast.showCustomToast(context, Constants.SERVER_DOWN_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}else if(msg.what == 2){
				//pd.dismiss();
			}
			else if(msg.what == 3){
				CustomToast.showCustomToast(context, msg.getData().getString("message"), Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 4){
				CustomToast.showCustomToast(context, Constants.DATABASE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 5){
				CustomToast.showCustomToast(context, Constants.LOCATION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 6){
				//joinLisn(lisnId.get(msg.getData().getInt("position")),msg.getData().getInt("position"));		
			}
			else if(msg.what == 7){
				CustomToast.showCustomToast(context, Constants.LISN_JOINED_ALREADY_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
			}
			else if(msg.what == 8){
				CustomToast.showCustomToast(context, Constants.LISN_JOINED_SUCCESSFUL, Constants.TOAST_VISIBLE_LONG, layoutInflater);
			}
			else if(msg.what == 9){
				CustomToast.showCustomToast(context, Constants.PROBLEM_GETTING_IMAGE_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 11){
				CustomToast.showCustomToast(context, Constants.JSON_EXCEPTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
			else if(msg.what == 12){
				//CustomToast.showCustomToast(context, Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG, layoutInflater);
				//pd.dismiss();
			}
		}
	};
}