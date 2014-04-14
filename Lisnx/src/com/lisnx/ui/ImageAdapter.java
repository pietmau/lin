package com.lisnx.ui;

import java.lang.ref.WeakReference;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

public class ImageAdapter extends BaseAdapter{

	BaseActivity baseActivity;
	
	public ImageAdapter(BaseActivity context){
		this.baseActivity = context;
	}

	@Override
	public int getCount() {
		return Constants.GRID_ICON_COUNT;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view  = convertView;
		
		LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.grid_item, null);
		TextView textView = (TextView) view.findViewById(R.id.grid_item_text);
		ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
		
		if(position == Constants.LISN){
			textView.setText(R.string.lisnIconText);
			imageView.setImageResource(R.drawable.lisn_icon);
		}
		else if(position == Constants.PROFILE){
			textView.setText(R.string.profileIconText);
			imageView.setImageResource(R.drawable.profile_icon);
			//BitmapDownloaderTask bitmapDownloaderTask = new BitmapDownloaderTask(imageView);
			//bitmapDownloaderTask.execute();
		}
		else if(position == Constants.CONNECTIONS){
			textView.setText(R.string.friendsText);
			imageView.setImageResource(R.drawable.friends_icon);
		}
		else if(position == Constants.ABOUT_US){
			textView.setText(R.string.aboutUsIconText);
			imageView.setImageResource(R.drawable.about_us_icon);
		}
		return view;
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
			Bitmap bitmap = null;
			try {
				bitmap= Utils.getUserImage(baseActivity.getMapWithToken(),  baseActivity);
			} catch (NullPointerException e) {
				
			} catch (JSONException e) {
				
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (bitmap != null) {
				if (imageViewReference != null) {
					ImageView imageView = imageViewReference.get();
					if (imageView != null) {
						Bitmap roundedCornerBitmap = new RoundedCornerBitmap()
								.getRoundedCornerBitmap(bitmap);
						ViewGroup.LayoutParams imageLayoutParams = imageView.getLayoutParams();
						imageView.setBackgroundColor(Color.TRANSPARENT);
						imageView.setImageBitmap(roundedCornerBitmap);
						imageView.setLayoutParams(imageLayoutParams);
						
					}
				}
			} 
		}
	}
}