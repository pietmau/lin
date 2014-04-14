package com.lisnx.android.activity;


import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.LocationService;
import com.lisnx.service.NotificationService;
import com.lisnx.ui.ImageAdapter;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;

@SuppressLint("HandlerLeak")
public class MenuActivity extends BaseActivity implements OnClickListener {

	GridView gridView;
	public static String longitude = null;
	public static String lattitude = null;
	ImageView peopleNearByIcon;
	ImageView friendRequestsIcon;

	public enum Item {
		LISN, PROFILE, CONNECTIONS, ABOUT;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		layoutInflater = this.getLayoutInflater();
		//overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		//startService(new Intent(this, LocationService.class));
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(itemClickListener);
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new ImageAdapter(this));

		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(this);
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);
		

		nameText = (TextView) findViewById(R.id.userName);
		nameText.setOnClickListener(this);
		userImage = (ImageView) findViewById(R.id.userImage);
		userImage.setOnClickListener(this);
		updateImageNameStrip();
		if(pd!=null)
			pd.dismiss();
		
		//VISH
		Intent i = new Intent(getApplicationContext(), NotificationService.class);
		startService(i);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		String errorMsg = getIntent().getStringExtra("errorToast");
		if (errorMsg != null && !"".equals(errorMsg.trim())) {
			CustomToast.showCustomToast(getApplicationContext(), errorMsg,
					Constants.TOAST_VISIBLE_LONG, layoutInflater);
		}
	}
	

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			if (String.valueOf(loc.getLatitude()).equalsIgnoreCase(null)
					|| String.valueOf(loc.getLongitude())
							.equalsIgnoreCase(null)
					|| String.valueOf(loc.getLatitude()).equalsIgnoreCase("")
					|| String.valueOf(loc.getLongitude()).equalsIgnoreCase("")) {
			} else {
				lattitude = String.valueOf(loc.getLatitude());
				longitude = String.valueOf(loc.getLongitude());
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public void onBackPressed() {
		  Log.i("HA", "Finishing");
		 /*  Intent intent = new Intent(Intent.ACTION_MAIN);
		   intent.addCategory(Intent.CATEGORY_HOME);
		   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   startActivity(intent);*/
		  finish();
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
			public View onCreateView(final String name, final Context context,
					final AttributeSet attributeSet) {

				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {
						final LayoutInflater f = getLayoutInflater();
						final View view = f
								.createView(name, null, attributeSet);
						// view.setBackgroundResource(R.drawable.menu_selector);

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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.friendRequestNotification:
			//friendRequestsIcon.setAlpha(100);
			notificationsScreenCaller(view);
			break;
		case R.id.userName:
			profileScreenCaller(view);
			break;
		case R.id.userImage:
			//navigateToSocialWizard(view);
			profileScreenCaller(view);
			break;
		case R.id.peopleNearByNotification:
			peopleNearByIcon.setAlpha(100);
			peopleNearbyScreenCaller(view);
			break;
		}
	}

	private void navigateToSocialWizard(View view) {
		try {
			Intent aboutUsIntent = new Intent(view.getContext(),
					SocialWizard.class);
			startActivityForResult(aboutUsIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
		
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case Constants.LISN:
				lisnsScreenCaller(view);
				break;
			case Constants.PROFILE:
				profileScreenCaller(view);
				break;
			case Constants.ABOUT_US:
				navigateAboutUsScreen(view);
				break;
			case Constants.CONNECTIONS:
				friendsScreenCaller(view);
				break;
			}
		}
	};

	public void friendsScreenCaller(View view) {
		pd = ProgressDialog.show(this, null,
				this.getResources().getString(R.string.lodingMessage), true);
		navigateFriendsScreen(view);
	}

	public void profileScreenCaller(View view) {
		navigateProfileScreen(view);
	}

	public void lisnsScreenCaller(View view) {
		pd = ProgressDialog.show(this, null,
				this.getResources().getString(R.string.lodingMessage), true);
		navigateTabScreen(view);
	}

	public void peopleNearbyScreenCaller(View view) {
		pd = ProgressDialog.show(this, null,
				this.getResources().getString(R.string.lodingMessage), true);
		navigatePeopleNearByScreen(view);
	}

	public void notificationsScreenCaller(View view) {
		pd = ProgressDialog.show(this, null,
				this.getResources().getString(R.string.lodingMessage), true);
		navigateFriendRequestScreen(view, Constants.CALLING_SCREEN_MENU);
	}

	public void navigateTabScreen(View view) {
		try {
			Intent tabIntent = new Intent(view.getContext(), TabView.class);
			startActivityForResult(tabIntent, 0);
			pd.dismiss();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateAboutUsScreen(View view) {
		try {
			Intent aboutUsIntent = new Intent(view.getContext(),
					AboutUsActivity.class);
			startActivityForResult(aboutUsIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateProfileScreen(View view) {
		ProfileActivity.callingProfileScreen = Constants.CALLING_SCREEN_MENU;
		try {
			Intent profileIntent = new Intent(view.getContext(),
					ProfileActivity.class);
			startActivityForResult(profileIntent, 0);
			pd.dismiss();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateSettingsScreen(View view) {
		try {
			Intent settingsIntent = new Intent(view.getContext(),
					SettingsActivity.class);
			startActivityForResult(settingsIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigatePeopleNearByScreen(View view) {
		PeopleNearByActivity.callingPeopleNearByScreen = Constants.CALLING_SCREEN_MENU;
		try {
			Intent peopleNearByIntent = new Intent(view.getContext(),
					PeopleNearByActivity.class);
			startActivityForResult(peopleNearByIntent, 0);
			pd.dismiss();
			overridePendingTransition(R.anim.slide_from_right_to_left,R.anim.slide_out_from_right_to_left);
		} catch (Exception ex) {
		}
	}

	

	public void navigateFriendsScreen(View view) {
		try {
			Intent friendsIntent = new Intent(view.getContext(),
					FriendsActivity.class);
			startActivityForResult(friendsIntent, 0);
			overridePendingTransition(R.anim.slide_from_right_to_left,R.anim.slide_out_from_right_to_left);
			pd.dismiss();
		} catch (Exception ex) {
		}
	}

	@SuppressWarnings("unused")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.SERVER_DOWN_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
			} else if (msg.what == 2) {
			} else if (msg.what == 3) {
				CustomToast.showCustomToast(getApplicationContext(), msg
						.getData().getString("message"),
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
			} else if (msg.what == 4) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.DATABASE_ERROR, Constants.TOAST_VISIBLE_LONG,
						layoutInflater);
			} else if (msg.what == 11) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.JSON_EXCEPTION_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
			} else if (msg.what == 12) {
				// CustomToast.showCustomToast(getApplicationContext(),
				// Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG,
				// layoutInflater);
			}
		}
	};
}