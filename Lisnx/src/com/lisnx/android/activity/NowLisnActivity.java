package com.lisnx.android.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.event.ILisner;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.Lisn;
import com.lisnx.model.Status;
import com.lisnx.model.ViewHolder;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.service.TimerStopper;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class NowLisnActivity extends BaseActivity implements ILisner,
		OnClickListener, Runnable {

	public static ArrayList<String> lisnId = new ArrayList<String>();
	public String longitude;
	public String lattitude;

	@SuppressWarnings("unused")
	private EventNotifier eventNotifier;
	private ArrayList<HashMap<String, Object>> availableLisn;
	public static final String LISN_DETAIL_KEY = "lisnDetail";
	public static final String LISNER_COUNT_KEY = "lisnerCount";
	public static final String LISN_KEY = "lisnStatus";
	public static final String LISN_NAME_KEY = "lisnName";
	public static final String CREATED_KEY = "created";
	public static final String IDKEY = "id";
	public static final String TOTAL_MESSAGE_KEY = "totalMessage";
	public static final String DESCRIPTION_KEY = "description";
	public static final String callingNowLisnScreen = null;
	public static ListView listView = null;
	public boolean addHeader = true;
	public ListAdapter adapter = null;
	public View tempView = null;
	public boolean noLisn = false;

	Dialog profileDialog;
	public boolean fullProfile = false;
	public boolean casualProfile = false;
	public boolean professionalProfile = false;
	public boolean emailOnlyProfile = false;
	public String shareProfileType = null;
	public boolean flag = false;
	public static final int NOW_LISN = 0;
	// private TextView nameText;
	// private ImageView userImage;
	private int imageWidth = 0;
	private int imageHeight = 0;

	public static Bitmap permanentImage = null;
	public static String permanentName = null;
	public static String permanentEmail = null;
	public static String permanentIsImage = null;
	public static String peopleCount = null;
	public static String notificationsCount = null;
	public boolean isLandScapeMode = false;
	public static Resources res;
	public static Bitmap defaultBitmap;
	ImageView backIcon;
	ImageView peopleNearByIcon;
	ImageView friendRequestsIcon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.now_lisn);

		layoutInflater = this.getLayoutInflater();
		isLandScapeMode = false;
		eventNotifier = new EventNotifier(this);
		lisnId.clear();
		res = getResources();
		defaultBitmap = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.ic_place_holder);

		backIcon = (ImageView) findViewById(R.id.backIcon);
		backIcon.setOnClickListener(this);
		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(this);
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);
		// nameText = (TextView) findViewById(R.id.userName);
		// nameText.setOnClickListener(this);
		// userImage=(ImageView)findViewById(R.id.userImage);
		// userImage.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);

		try {
			runOnUiThread(new Runnable() {
				public void run() {
					GetNowLisnData getNowLisnData = new GetNowLisnData();
					getNowLisnData.execute();
				}
			});
		} catch (Exception e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
		}

		if (Constants.CALLING_SCREEN_LOGIN
				.equalsIgnoreCase(LoginActivity.callingNowLisnScreen)) {
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						GetOtherData getOtherData = new GetOtherData();
						getOtherData.execute();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			LoginActivity.callingNowLisnScreen = "null";
		} else {
			

			/*
			 * if((permanentName == null) || ((permanentImage == null) &&
			 * (("true").equalsIgnoreCase(permanentIsImage)))){ try{ new
			 * UserInfo(getApplicationContext(),userImage,nameText,
			 * layoutInflater).getProfileData(); } catch(Exception e){
			 * e.printStackTrace(); } } else{ try{
			 * nameText.setText(permanentName); if(permanentImage != null){
			 * userImage.setImageBitmap(permanentImage); } } catch(Exception e){
			 * e.printStackTrace(); } }
			 */
		}
	}

	private class GetNowLisnData extends AsyncTask<Void, Void, List<Lisn>> {
		public GetNowLisnData() {
			super();
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(
					NowLisnActivity.this,
					null,
					NowLisnActivity.this.getResources().getString(
							R.string.lodingMessage), true);
		}

		@Override
		protected List<Lisn> doInBackground(Void... params) {
			return getLisnAroundMe();
		}

		@Override
		protected void onPostExecute(List<Lisn> nowLisns) {
			List<Lisn> dataExist = nowLisns;
			if (dataExist != null) {
				displayLisns(dataExist);
			} else {
				handler.sendEmptyMessage(2);
			}
			handler.sendEmptyMessage(2);
		}
	}

	private class GetOtherData extends AsyncTask<Void, Void, Void> {
		public GetOtherData() {
			super();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}

	/*
	 * public void getProfileData() throws NullPointerException, JSONException{
	 * DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
	 * DatabaseUtility dao = new DatabaseUtility(helper); String accessToken =
	 * null; try { accessToken = dao.getAccessToken(); } catch (Exception e1) {
	 * //handler.sendEmptyMessage(1); return; }
	 * 
	 * if(accessToken == null || accessToken.length() == 0){
	 * //handler.sendEmptyMessage(1); return; }
	 * 
	 * Map<String , String> params = new HashMap<String,String>();
	 * params.put(Constants.ACCESS_TOKEN_PARAM, accessToken); final Profile
	 * profile = Utils.getProfileData(params, NowLisnActivity.this);
	 * 
	 * try{ runOnUiThread(new Runnable() { public void run() { if(profile !=
	 * null){ nameText.setText(profile.getFullname());
	 * permanentName=profile.getFullname(); permanentEmail=profile.getEmail();
	 * permanentIsImage = profile.getIsImage();
	 * 
	 * if("true".equalsIgnoreCase(profile.getIsImage())){
	 * userImage=(ImageView)findViewById(R.id.userImage); download(userImage); }
	 * } } }); } catch(Exception e){ e.printStackTrace(); } }
	 */

	public void download(ImageView imageView) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute();
	}

	public class BitmapDownloaderTask extends AsyncTask<Void, Void, Bitmap> {
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

			if (bitmap != null) {
				if (imageViewReference != null) {
					ImageView imageView = imageViewReference.get();
					if (imageView != null) {
						Bitmap roundedCornerBitmap = new RoundedCornerBitmap()
								.getRoundedCornerBitmap(bitmap);
						imageView.setImageBitmap(roundedCornerBitmap);
						permanentImage = roundedCornerBitmap;
					}
				}
			} else {
				ImageView imageView = imageViewReference.get();
				imageView.setImageBitmap(defaultBitmap);
				permanentImage = defaultBitmap;
			}
		}
	}

	public Bitmap getImage() {
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		String accessToken = null;
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return null;
		}

		if (accessToken == null || accessToken.length() == 0) {
			handler.sendEmptyMessage(1);
			return null;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);

		Bitmap bmImg = null;
		try {
			bmImg = Utils.getUserImage(params, NowLisnActivity.this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return null;
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		switch (displayMetrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			if (isLandScapeMode == true) {
				imageWidth = Constants.LOW_WIDTH_LANDSCAPE;
				imageHeight = Constants.LOW_HEIGHT_LANDSCAPE;
			} else {
				imageWidth = Constants.LOW_WIDTH;
				imageHeight = Constants.LOW_HEIGHT;
			}
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			if (isLandScapeMode == true) {
				imageWidth = Constants.MEDIUM_WIDTH_LANDSCAPE;
				imageHeight = Constants.MEDIUM_HEIGHT_LANDSCAPE;
			} else {
				imageWidth = Constants.MEDIUM_WIDTH;
				imageHeight = Constants.MEDIUM_HEIGHT;
			}
			break;
		case DisplayMetrics.DENSITY_HIGH:
			if (isLandScapeMode == true) {
				imageWidth = Constants.HIGH_WIDTH_LANDSCAPE;
				imageHeight = Constants.HIGH_HEIGHT_LANDSCAPE;
			} else {
				imageWidth = Constants.HIGH_WIDTH;
				imageHeight = Constants.HIGH_HEIGHT;
			}
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			if (isLandScapeMode == true) {
				imageWidth = Constants.HIGH_WIDTH_LANDSCAPE;
				imageHeight = Constants.HIGH_HEIGHT_LANDSCAPE;
			} else {
				imageWidth = Constants.HIGH_WIDTH;
				imageHeight = Constants.HIGH_HEIGHT;
			}
			break;
		default:
			if (isLandScapeMode == true) {
				imageWidth = Constants.HIGH_WIDTH_LANDSCAPE;
				imageHeight = Constants.HIGH_HEIGHT_LANDSCAPE;
			} else {
				imageWidth = Constants.HIGH_WIDTH;
				imageHeight = Constants.HIGH_HEIGHT;
			}
			break;
		}

		if (bmImg == null) {
			handler.sendEmptyMessage(9);
			return null;
		} else {
			bmImg = Bitmap.createScaledBitmap(bmImg, imageWidth, imageHeight,
					true);
			if (isLandScapeMode == false) {
				permanentImage = bmImg;
			}
			handler.sendEmptyMessage(2);
			return bmImg;
		}
	}

	@SuppressWarnings("deprecation")
	public void displayLisns(List<Lisn> lisnsList) {
		List<Lisn> lisnList = lisnsList;
		availableLisn = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> lisnData = null;
		Lisn lisn = null;

		for (int i = 0; i < lisnList.size(); i++) {
			lisn = lisnList.get(i);
			lisnData = new HashMap<String, Object>();

			if (lisn.getMember().equalsIgnoreCase("null")) {
				lisnData.put(LISNER_COUNT_KEY, "0");
			} else {
				lisnData.put(LISNER_COUNT_KEY, lisn.getMember() + "");
			}
			lisnData.put(LISN_KEY, lisn.getRsvp().trim());
			lisnData.put(IDKEY, lisn.getLisnID());
			lisnData.put(LISN_NAME_KEY, lisn.getName());
			lisnData.put(TOTAL_MESSAGE_KEY, lisn.getTotalMessage());
			lisnData.put(DESCRIPTION_KEY, lisn.getDescription());
			availableLisn.add(lisnData);
		}

		TextView lisnCountDetails = (TextView) findViewById(R.id.created);
		if ((lisnList != null) && (!lisnList.isEmpty())) {
			lisnCountDetails.setText(lisnList.get(0).getCountLisns()
					+ " Nearby");
		} else {
			lisnCountDetails.setText("No Lisns Nearby");
			listView.setVisibility(View.GONE);
			View linearLayout = findViewById(R.id.now_lisn_view);
			TextView noLisnText = new TextView(this);
			noLisnText.setText(R.string.noLisnText);
			noLisnText.setId(5);
			noLisnText.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			noLisnText.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			noLisnText.setTextColor(Color.BLACK);
			((LinearLayout) linearLayout).addView(noLisnText);
		}

		adapter = new ListAdapter(availableLisn, NowLisnActivity.this);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> lisn = (HashMap<String, Object>) adapter
						.getAdapter().getItem(position);
				try {
					Intent lisnDetailIntent = new Intent(view.getContext(),
							LisnDetailTabView.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundleObj = new Bundle();
					bundleObj.putString("id", (String) lisn.get(IDKEY));
					bundleObj.putString("RSVP",
							lisn.get(NowLisnActivity.LISN_KEY).toString());
					lisnDetailIntent.putExtras(bundleObj);
					view.getContext().startActivity(lisnDetailIntent);
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right); // Specify an
																// explicit
																// transition
																// animation to
																// perform next

				} catch (Exception ex) {
				}
			}
		};
		listView.setOnItemClickListener(listener);
	}

	public List<Lisn> getLisnAroundMe() {
		String timeZone = Utils.getTimeZone();
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return null;
		}

		if (accessToken == null || accessToken.length() == 0) {
			handler.sendEmptyMessage(1);
			return null;
		}
		if (!Utils.isDev()) {
			Location lastKnownLocation = getLocation();
			if (lastKnownLocation == null) {
				handler.sendEmptyMessage(5);
				return null;
			} else {
				longitude = String.valueOf(lastKnownLocation.getLongitude());
				lattitude = String.valueOf(lastKnownLocation.getLatitude());
			}
		} else {
			lattitude = "28.663628";
			longitude = "77.369767";
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LATITUDE_PARAM, lattitude);
		params.put(Constants.LONGITUDE_PARAM, longitude);
		params.put(Constants.TIME_STAMP, timeZone);

		List<Lisn> lisnList = null;
		try {
			lisnList = Utils.getLisnAroundMe(params, this);
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
	public void run() {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backIcon:
			backIcon.setAlpha(100);
			navigateMenuScreen(view);
			break;
		case R.id.userName:
			navigateProfileScreen(view);
			break;
		case R.id.userImage:
			navigateProfileScreen(view);
			break;
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

	@Override
	public void updateLocation() {
		DatabaseHelper db = new DatabaseHelper(this);
		DatabaseUtility database = new DatabaseUtility(db);
		CurrentLocation location = database.getLocationData();
		if (location != null) {
		}
	}

	public Location getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

	public void destroyVariables() {
		TimerStopper.stopTimers();
		BaseActivity.permanentUserId = null;
		permanentImage = null;
		permanentName = null;
		notificationsCount = null;
		peopleCount = null;
		permanentEmail = null;
		permanentIsImage = null;
		LoginActivity.callingNowLisnScreen = null;
		FriendsActivity.IS_IMAGE_KEY = null;
		NotificationsActivity.IS_IMAGE_KEY = null;
		PeopleNearByActivity.IS_IMAGE_KEY = null;
	}

	public void navigateProfileScreen(View view) {
		ProfileActivity.callingProfileScreen = Constants.CALLING_SCREEN_NOW_LISN;
		try {
			Intent profileIntent = new Intent(view.getContext(),
					ProfileActivity.class);
			startActivityForResult(profileIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigatePeopleNearByScreen(View view) {
		PeopleNearByActivity.callingPeopleNearByScreen = Constants.CALLING_SCREEN_NOW_LISN;
		try {
			Intent peopleNearByIntent = new Intent(view.getContext(),
					PeopleNearByActivity.class);
			startActivityForResult(peopleNearByIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	

	public void refreshLisn() {
		runOnUiThread(new Runnable() {
			public void run() {
				lisnId.clear();
				addHeader = false;
				pd = ProgressDialog.show(
						NowLisnActivity.this,
						null,
						NowLisnActivity.this.getResources().getString(
								R.string.lodingMessage), true);
				List<Lisn> dataExist = getLisnAroundMe();
				if (dataExist != null) {
					displayLisns(dataExist);
				}
				addHeader = true;
				handler.sendEmptyMessage(2);
			}
		});
	}

	public void joinLisn(String id, int position) {
		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseUtility dao = new DatabaseUtility(helper);
		String rsvp = dao.getRsvpById(id);
		rsvp = rsvp.trim();
		if (rsvp == null) {
			handler.sendEmptyMessage(1);
			return;
		}
		if (rsvp.equalsIgnoreCase((Constants.RSVP_IN).trim())) {
			handler.sendEmptyMessage(7);
			return;
		} else {
			joinLisnAndUpdateLisn(id, position);
		}
	}

	public void joinLisnAndUpdateLisn(String id, int position) {
		String shareProfileType2 = Constants.PROFILE_SHARE_ALL;
		String accessToken = null;
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		DatabaseUtility dao = new DatabaseUtility(helper);
		try {
			accessToken = dao.getAccessToken();
		} catch (Exception e1) {
			handler.sendEmptyMessage(1);
			return;
		}

		if (accessToken == null || accessToken.length() == 0) {
			handler.sendEmptyMessage(1);
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		params.put(Constants.LISN_ID_PARAM, id);
		params.put(Constants.PROFILE_TYPE_PARAM, shareProfileType2);

		Status status = null;
		try {
			status = Utils.joinLisn(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return;
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			return;
		}

		if (status == null) {
			handler.sendEmptyMessage(1);
		} else if (status.getStatus().equalsIgnoreCase(Constants.SUCCESS)) {
			try {
				Intent lisnDetailIntent = new Intent(this,
						LisnDetailTabView.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundleObj = new Bundle();
				bundleObj.putString("id", id);
				bundleObj.putString("RSVP", "In");
				lisnDetailIntent.putExtras(bundleObj);
				startActivityForResult(lisnDetailIntent, 0);
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
				handler.sendEmptyMessage(8);
			} catch (Exception ex) {
			}
		} else {
			handler.sendEmptyMessage(2);
			return;
		}
	}
	/*
	@Override
	public void onBackPressed() {
		try {
			Intent loginIntent = new Intent(getApplicationContext(),
					MenuActivity.class);
			startActivity(loginIntent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}

	}
	*/
	public void navigateChooseProfileScreen(View view, String id, int position) {
		try {
			Intent chooseProfileIntent = new Intent(view.getContext(),
					ChooseProfileActivity.class);
			Bundle bundleObj = new Bundle();
			bundleObj.putString("id", id);
			bundleObj.putString("position", Integer.toString(position));
			chooseProfileIntent.putExtras(bundleObj);
			startActivityForResult(chooseProfileIntent, NOW_LISN);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	private class ListAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> lisns;
		private LayoutInflater mInflater;

		public ListAdapter(ArrayList<HashMap<String, Object>> lisns,
				Context context) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.now_lisn_list_view,
						null);
				holder = new ViewHolder();
				holder.lisnName = (TextView) convertView
						.findViewById(R.id.lisnName);
				holder.lisnerImage = (ImageView) convertView
						.findViewById(R.id.lisnerImage);
				holder.numberOfLisn = (TextView) convertView
						.findViewById(R.id.lisnerCount);
				holder.button = (ImageButton) convertView
						.findViewById(R.id.lisnButton);
				holder.numberOfMessages = (TextView) convertView
						.findViewById(R.id.messageCount);
				holder.description = (TextView) convertView
						.findViewById(R.id.lisnDescription);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.lisnName.setText((String) lisns.get(position).get(
					NowLisnActivity.LISN_NAME_KEY));
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				holder.numberOfLisn
						.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
				holder.numberOfMessages
						.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
				break;
			}

			holder.numberOfLisn.setText((String) lisns.get(position).get(
					NowLisnActivity.LISNER_COUNT_KEY));
			holder.numberOfMessages.setText((String) lisns.get(position).get(
					NowLisnActivity.TOTAL_MESSAGE_KEY));

			lisnId.add(lisns.get(position).get(NowLisnActivity.IDKEY)
					.toString());
			String desc = (String) lisns.get(position).get(
					NowLisnActivity.DESCRIPTION_KEY);
			holder.description.setText(desc);
			if (desc == null
					|| (desc.length() == 0 || "null".equalsIgnoreCase(desc)))
				holder.description.setVisibility(View.GONE);
			else
				holder.description.setVisibility(View.VISIBLE);

			if (Constants.RSVP_IN.equalsIgnoreCase(lisns.get(position)
					.get(NowLisnActivity.LISN_KEY).toString())) {
				holder.button.setBackgroundDrawable((getApplication()
						.getResources().getDrawable(R.drawable.ic_in)));
			} else {
				holder.button.setBackgroundDrawable((getApplication()
						.getResources().getDrawable(R.drawable.ic_lisn_in)));
			}
			holder.button.setFocusable(false);
			holder.button.setFocusableInTouchMode(false);

			holder.button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {

					Message msg = new Message();
					msg.what = 6;
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			});
			return convertView;
		}

	}

	private class ProcessJoinLisn extends AsyncTask<Void, Void, Void> {
		String id;
		int position;

		public ProcessJoinLisn(String id, int position) {
			super();
			this.id = id;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(NowLisnActivity.this, null,
					NowLisnActivity.this.getString(R.string.join), true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			runOnUiThread(new Runnable() {
				public void run() {
					joinLisnAndUpdateLisn(id, position);
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			handler.sendEmptyMessage(2);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		setContentView(R.layout.now_lisn);
		layoutInflater = this.getLayoutInflater();
		isLandScapeMode = false;
		eventNotifier = new EventNotifier(this);
		lisnId.clear();
		res = getResources();
		defaultBitmap = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.ic_place_holder);

		ImageView backIcon = (ImageView) findViewById(R.id.backIcon);
		backIcon.setOnClickListener(this);
		ImageView peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(this);
		ImageView friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.list);

		try {
			runOnUiThread(new Runnable() {
				public void run() {
					GetNowLisnData getNowLisnData = new GetNowLisnData();
					getNowLisnData.execute();
				}
			});
		} catch (Exception e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
		}

		if (Constants.CALLING_SCREEN_LOGIN
				.equalsIgnoreCase(LoginActivity.callingNowLisnScreen)) {
			LoginActivity.callingNowLisnScreen = "null";
		} else {
			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NOW_LISN) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String lisnId = data.getStringExtra("lisnId");
					String position = data.getStringExtra("position");
					new ProcessJoinLisn(lisnId, Integer.parseInt(position))
							.execute();
				}
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.SERVER_DOWN_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 2) {
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 3) {
				CustomToast.showCustomToast(getApplicationContext(), msg
						.getData().getString("message"),
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 4) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.DATABASE_ERROR, Constants.TOAST_VISIBLE_LONG,
						layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 5) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.LOCATION_ERROR, Constants.TOAST_VISIBLE_LONG,
						layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 6) {
				joinLisn(lisnId.get(msg.getData().getInt("position")), msg
						.getData().getInt("position"));
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 7) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.LISN_JOINED_ALREADY_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 8) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.LISN_JOINED_SUCCESSFUL,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 9) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.PROBLEM_GETTING_IMAGE_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 11) {
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.JSON_EXCEPTION_ERROR,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
				if (pd != null)
					pd.dismiss();
			} else if (msg.what == 12) {
				// CustomToast.showCustomToast(getApplicationContext(),
				// Constants.NO_CONNECTION_ERROR, Constants.TOAST_VISIBLE_LONG,
				// layoutInflater);
				if (pd != null)
					pd.dismiss();
			}
		}
	};
	
	
}