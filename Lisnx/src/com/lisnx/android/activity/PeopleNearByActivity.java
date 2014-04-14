package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.PeopleNearByViewHolder;
import com.lisnx.model.PersonNearBy;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.LibHttp;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class PeopleNearByActivity extends BaseActivity implements
		OnClickListener, Runnable {

	public static ListView listView = null;
	private ArrayList<HashMap<String, Object>> nearbyPeople;
	public static final String PEOPLE_NEARBY_NAME_KEY = "personNearBy";
	public static final String IDKEY = "id";
	public static String IS_IMAGE_KEY = "isImage";
	public static final String POSITION_KEY = "position";
	public static final String CONNECTION_STATUS_KEY = "connectionStatus";
	public static final String BITMAP_KEY = "bitmap";
	public ListAdapter adapter = null;
	public String longitude;
	public String lattitude;

	public Bitmap bmImg = null;
	private int imageWidth = 0;
	private int imageHeight = 0;
	public static String callingPeopleNearByScreen = null;
	public String uid = null;
	public String lisnId = null;
	public static Resources res;
	public static Bitmap defaultBitmap;
	private String RSVP = null;
	ImageView backIcon;
	ImageView friendRequestsIcon;
	private TextView noPeopleNearbyText;
	private TextView peopleNearByCount;
	public BitmapDownloaderTask task;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_nearby);
		layoutInflater = this.getLayoutInflater();
		res = getResources();
		defaultBitmap = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.ic_place_holder);

		if (Constants.CALLING_SCREEN_LISN_DETAIL
				.equalsIgnoreCase(callingPeopleNearByScreen)) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				lisnId = extras.getString("lisnId");
				RSVP = extras.getString("RSVP");
			}
		}
		if (Constants.CALLING_SCREEN_OTHER_PROFILE
				.equalsIgnoreCase(callingPeopleNearByScreen)
				|| Constants.CALLING_SCREEN_COMMON_LISN
						.equalsIgnoreCase(callingPeopleNearByScreen)
				|| Constants.CALLING_SCREEN_COMMON_FRIEND
						.equalsIgnoreCase(callingPeopleNearByScreen)) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				uid = extras.getString("uid");
			}
		}

		backIcon = (ImageView) findViewById(R.id.backIcon);
		backIcon.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list);
		/*
		 * nameText = (TextView) findViewById(R.id.userName);
		 * nameText.setOnClickListener(this);
		 * userImage=(ImageView)findViewById(R.id.userImage);
		 * userImage.setOnClickListener(this);
		 */

		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);
		noPeopleNearbyText = (TextView) findViewById(R.id.noPeopleNearbyBox);
		peopleNearByCount = (TextView) findViewById(R.id.notificationBoxTextPeople);
		TextView listIntroductionText = (TextView) findViewById(R.id.listIntroText);
		listIntroductionText.setText(R.string.lisnersNearby);

		try {
			runOnUiThread(new Runnable() {
				public void run() {
					GetPeopleNearByData getPeopleNearByData = new GetPeopleNearByData();
					getPeopleNearByData.execute();
				}
			});
		} catch (Exception e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
		}

	

		//VISH updateNotificationAndPeopleNearbyCounts();
	}

	private class GetPeopleNearByData extends
			AsyncTask<Void, Void, List<PersonNearBy>> {
		public GetPeopleNearByData() {
			super();
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(
					PeopleNearByActivity.this,
					null,
					PeopleNearByActivity.this.getResources().getString(
							R.string.lodingMessage), true);
		}

		@Override
		protected List<PersonNearBy> doInBackground(Void... params) {
			return getPeopleNearByList();
		}

		@Override
		protected void onPostExecute(List<PersonNearBy> peopleNearByData) {
			List<PersonNearBy> dataExist = peopleNearByData;
			if ((dataExist != null) && (!(dataExist.isEmpty()))) {
				displayPeopleNearBy(dataExist);
			} else {
				listView.setVisibility(View.GONE);
				noPeopleNearbyText.setVisibility(View.VISIBLE);
				peopleNearByCount.setVisibility(View.GONE);
			}
			handler.sendEmptyMessage(2);
		}
	}

	

	public void run() {
	}

	public List<PersonNearBy> getPeopleNearByList() {
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

		//VISH Map<String, String> params = new HashMap<String, String>();
		//VISH params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);
		LibHttp http = new LibHttp();
		List<PersonNearBy> peopleNearByList = null;
		try {
			//VISH peopleNearByList = Utils.getPeopleNearByList(params, this);
			peopleNearByList = http.getPeopleNearByList(accessToken);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			// handler.sendEmptyMessage(11);
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return peopleNearByList;
	}

	public void displayPeopleNearBy(List<PersonNearBy> peopleNearByLists) {
		List<PersonNearBy> peopleNearByList = peopleNearByLists;
		nearbyPeople = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> personNearByData = null;
		PersonNearBy personNearBy = null;

		try {
			if (peopleNearByList.size() != 0)
			{
				peopleNearByCount.setVisibility(View.VISIBLE);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(
						displayMetrics);
				switch (displayMetrics.densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					peopleNearByCount
							.setTextSize(Constants.NOTIFICATION_TEXT_SIZE);
					break;
				}
				peopleNearByCount.setText(Integer.toString(peopleNearByList
						.size()));
			}
			
			for (int i = 0; i < peopleNearByList.size(); i++) {
				personNearBy = peopleNearByList.get(i);
				personNearByData = new HashMap<String, Object>();
				personNearByData.put(PEOPLE_NEARBY_NAME_KEY,
						personNearBy.getPersonNearByName());
				personNearByData.put(IS_IMAGE_KEY, personNearBy.getIsImage());
				personNearByData.put(CONNECTION_STATUS_KEY,
						personNearBy.getConnectionStatus());
				personNearByData.put(IDKEY, personNearBy.getPersonID());
				nearbyPeople.add(personNearByData);
			}

			adapter = new ListAdapter(nearbyPeople, PeopleNearByActivity.this);
			listView.setAdapter(adapter);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			OnItemClickListener listener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					OtherProfileActivity.callingOtherProfileScreen = Constants.CALLING_SCREEN_PEOPLE_NEARBY;
					@SuppressWarnings("unchecked")
					HashMap<String, Object> personNearBy = (HashMap<String, Object>) adapter
							.getAdapter().getItem(position);
					try {
						Intent otherProfileIntent = new Intent(
								view.getContext(), OtherProfileActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Bundle bundleObj = new Bundle();
						bundleObj.putString("lisnerId",
								(String) personNearBy.get(IDKEY));
						otherProfileIntent.putExtras(bundleObj);
						view.getContext().startActivity(otherProfileIntent);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
					} catch (Exception ex) {
					}
				}
			};
			listView.setOnItemClickListener(listener);
		} catch (Exception e) {
			handler.sendEmptyMessage(6);
			return;
		}
	}

	public synchronized Bitmap getOtherUserImage(String otherUserId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.USER_ID_PARAM, otherUserId);

		try {
			bmImg = Utils
					.getOtherPersonImage(params, PeopleNearByActivity.this);
		} catch (NullPointerException e) {
			// handler.sendEmptyMessage(1);
		} catch (JSONException e) {
			// handler.sendEmptyMessage(1);
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		switch (displayMetrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			imageWidth = Constants.LOW_WIDTH_SMALL;
			imageHeight = Constants.LOW_HEIGHT_SMALL;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			imageWidth = Constants.MEDIUM_WIDTH_SMALL;
			imageHeight = Constants.MEDIUM_HEIGHT_SMALL;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			imageWidth = Constants.HIGH_WIDTH_SMALL;
			imageHeight = Constants.HIGH_HEIGHT_SMALL;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			imageWidth = Constants.HIGH_WIDTH_SMALL;
			imageHeight = Constants.HIGH_HEIGHT_SMALL;
			break;
		default:
			imageWidth = Constants.HIGH_WIDTH_SMALL;
			imageHeight = Constants.HIGH_HEIGHT_SMALL;
			break;
		}

		if (bmImg == null) {
			return null;
		} else {
			bmImg = Bitmap.createScaledBitmap(bmImg, imageWidth, imageHeight,
					true);
			return bmImg;
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

	public void refreshPeopleNearbyList() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					List<PersonNearBy> dataExist = getPeopleNearByList();
					if ((dataExist != null) && (dataExist.size() != 0)) {
						listView.setVisibility(View.VISIBLE);
						noPeopleNearbyText.setVisibility(View.GONE);
						displayPeopleNearBy(dataExist);
					} else {
						listView.setVisibility(View.GONE);
						noPeopleNearbyText.setVisibility(View.VISIBLE);
						peopleNearByCount.setVisibility(View.GONE);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backIcon:
			backIcon.setAlpha(100);
			navigateMenuScreen(view);
			break;
		/*
		 * case R.id.userName: navigateProfileScreen(view); break; case
		 * R.id.userImage: navigateProfileScreen(view); break;
		 */
		case R.id.friendRequestNotification:
			friendRequestsIcon.setAlpha(100);
			navigateFriendRequestScreen(view);
			break;
		}
	}

	public void navigateProfileScreen(View view) {
		ProfileActivity.callingProfileScreen = Constants.CALLING_SCREEN_PEOPLE_NEARBY;
		try {
			Intent profileIntent = new Intent(view.getContext(),
					ProfileActivity.class);
			startActivityForResult(profileIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateFriendsScreen(View view) {
		try {
			Intent friendsIntent = new Intent(view.getContext(),
					FriendsActivity.class);
			startActivityForResult(friendsIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateNowLisnScreen(View view) {
		try {
			Intent loginIntent = new Intent(view.getContext(), TabView.class);
			startActivityForResult(loginIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateChangePasswordScreen(View view) {
		try {
			Intent changePasswordIntent = new Intent(view.getContext(),
					ChangePasswordActivity.class);
			startActivityForResult(changePasswordIntent, 0);
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

	public void navigateOtherProfileScreen(View view) {
		OtherProfileActivity.callingOtherProfileScreen = Constants.CALLING_SCREEN_NOTIFICATIONS;
		try {
			Intent otherProfileIntent = new Intent(view.getContext(),
					OtherProfileActivity.class);
			Bundle bundleObj = new Bundle();
			bundleObj.putString("lisnerId", uid);
			otherProfileIntent.putExtras(bundleObj);
			startActivityForResult(otherProfileIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateLisnDetailScreen(View view) {
		try {
			Intent lisnDetailIntent = new Intent(view.getContext(),
					LisnDetailTabView.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundleObj = new Bundle();
			bundleObj.putString("id", lisnId);
			lisnDetailIntent.putExtras(bundleObj);
			lisnDetailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			pd = ProgressDialog
					.show(this,
							null,
							this.getResources().getString(
									R.string.lodingMessage), true);
			view.getContext().startActivity(lisnDetailIntent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			pd.dismiss();
		} catch (Exception ex) {
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
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.NO_PEOPLE_NEARBY_ERROR,
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

	public void downloadImage(String otherId, ImageView imageView, int position) {
		// resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(otherId);

		if (bitmap == null) {
			download(otherId, imageView, position);
		} else {
			cancelPotentialDownload(otherId, imageView, position);
			imageView.setImageBitmap(bitmap);
		}
	}

	public void download(String otherId, ImageView imageView, int position) {
		if (otherId == null) {
			imageView.setImageDrawable(null);
			return;
		}

		if (cancelPotentialDownload(otherId, imageView, position)) {
			task = new BitmapDownloaderTask(imageView, position);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(
					task, res, defaultBitmap);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(otherId);
		}
	}

	private static boolean cancelPotentialDownload(String otherId,
			ImageView imageView, int position) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapId = bitmapDownloaderTask.otherId;
			if ((bitmapId == null) || (!bitmapId.equals(otherId))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same id is already being downloaded.
				return false;
			}
		}
		return true;
	}

	private static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		private DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask,
				Resources resources, Bitmap bitmap) {
			super(res, defaultBitmap);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String otherId;
		private final WeakReference<ImageView> imageViewReference;
		@SuppressWarnings("unused")
		private int position;

		public BitmapDownloaderTask(ImageView imageView, int imagePosition) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			position = imagePosition;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			otherId = params[0];
			return getOtherUserImage(otherId);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (bitmap != null) {
				addBitmapToCache(otherId,
						new RoundedCornerBitmap()
								.getRoundedCornerBitmap(bitmap));
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				if ((this == bitmapDownloaderTask)) {
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(new RoundedCornerBitmap()
								.getRoundedCornerBitmap(bitmap));
					}
				}
			}
		}
	}

	private Bitmap getBitmapFromCache(String otherId) {
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(otherId);
			if (bitmap != null) {
				sHardBitmapCache.remove(otherId);
				sHardBitmapCache.put(otherId, bitmap);
				return bitmap;
			}
		}

		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(otherId);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				return bitmap;
			} else {
				sSoftBitmapCache.remove(otherId);
			}
		}

		return null;
	}

	@SuppressWarnings("serial")
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			Constants.HARD_CACHE_CAPACITY / 2, 0.75f, true) {

		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > Constants.HARD_CACHE_CAPACITY) {
				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			Constants.HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};

	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}

	@SuppressWarnings("unused")
	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, Constants.DELAY_BEFORE_PURGE);
	}

	private void addBitmapToCache(String otherId, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(otherId, bitmap);
			}
		}
	}

	private class ListAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> nearbyPersons;
		private LayoutInflater mInflater;

		public ListAdapter(ArrayList<HashMap<String, Object>> nearbyPersons,
				Context context) {
			this.nearbyPersons = nearbyPersons;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return nearbyPersons.size();
		}

		@Override
		public Object getItem(int position) {
			return nearbyPersons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			PeopleNearByViewHolder holder;
			if (convertView == null || !(convertView instanceof TextView)
					|| !(convertView instanceof ImageView)) {
				convertView = mInflater.inflate(R.layout.people_nearby_view,
						null);

				holder = new PeopleNearByViewHolder();
				holder.personNearByName = (TextView) convertView
						.findViewById(R.id.personNearName);
				holder.personNearByImage = (ImageView) convertView
						.findViewById(R.id.personNearImage);
				holder.isConnectedImage = (ImageView) convertView
						.findViewById(R.id.isConnectedImage);
				holder.notConnectedImage = (ImageView) convertView
						.findViewById(R.id.notConnectedImage);
				convertView.setTag(holder);
			} else {
				holder = (PeopleNearByViewHolder) convertView.getTag();
			}

			holder.personNearByName.setText((String) nearbyPersons
					.get(position).get(
							PeopleNearByActivity.PEOPLE_NEARBY_NAME_KEY));
			try {
				if (Constants.CONNECTION_STATUS_CONNECTED
						.equalsIgnoreCase(((String) nearbyPersons
								.get(position)
								.get(PeopleNearByActivity.CONNECTION_STATUS_KEY)))) {
					holder.isConnectedImage.setVisibility(View.VISIBLE);
					holder.notConnectedImage.setVisibility(View.GONE);
				} else {
					holder.notConnectedImage.setVisibility(View.VISIBLE);
					holder.isConnectedImage.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String otherId = (String) nearbyPersons.get(position).get(
					PeopleNearByActivity.IDKEY);
			String isImage = (String) nearbyPersons.get(position).get(
					PeopleNearByActivity.IS_IMAGE_KEY);

			if ("true".equalsIgnoreCase(isImage)) {
				downloadImage(otherId, holder.personNearByImage, position);
			}
			return convertView;
		}
	}// End of list adapter
}