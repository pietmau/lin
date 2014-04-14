package com.lisnx.android.activity;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.model.Friend;
import com.lisnx.model.FriendsViewHolder;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.service.RoundedCornerBitmap;
import com.lisnx.ui.FastSearchListView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;



@SuppressLint({ "ParserError", "HandlerLeak" })
public class FriendsActivity extends BaseActivity implements OnClickListener,
Runnable {

	public String uid = null;
	public static FastSearchListView listView = null;
	private List<Friend> availableFriends;
	public static final String FRIENDS_NAME_KEY = "friendName";
	public static final String IDKEY = "id";
	public static String IS_IMAGE_KEY = "isImage";
	public static final String POSITION_KEY = "position";
	public static final String BITMAP_KEY = "bitmap";
	public ListAdapter adapter = null;
	public String longitude;
	public String lattitude;
	ImageView backIcon;
	// ImageLoader imageloader;
	public Bitmap bmImg = null;
	private int imageWidth = 0;
	private int imageHeight = 0;
	private ImageView userOwnImage;
	public static Resources res;
	public static Bitmap defaultBitmap;
	public boolean fromItemClickListener = false;
	ImageView peopleNearByIcon;
	ImageView friendRequestsIcon;
	private TextView noFriendsYetText;
	public BitmapDownloaderTask task;
	private EditText inputSearch;
	ImageSize targetSize;
	ImageLoader imageLoader;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		layoutInflater = this.getLayoutInflater();
		res = getResources();
		defaultBitmap = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.ic_place_holder);

		

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
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(false).
		cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).	
		build();
		
		ImageLoaderConfiguration config =new ImageLoaderConfiguration.Builder(
					getApplicationContext()).defaultDisplayImageOptions(
							defaultOptions).build();
		ImageLoader.getInstance().init(config);
		imageLoader= ImageLoader.getInstance();
		targetSize = new ImageSize(imageWidth, imageHeight);

		backIcon = (ImageView) findViewById(R.id.backIcon);
		backIcon.setOnClickListener(this);
		listView = (FastSearchListView) findViewById(R.id.list);
		listView.setFastScrollEnabled(true);

		adapter = new ListAdapter(FriendsActivity.this,
				android.R.layout.simple_list_item_1, new ArrayList<Friend>());
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);

		inputSearch = (EditText) findViewById(R.id.inputSearch);

		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() == 0)
					adapter = new ListAdapter(FriendsActivity.this,
							android.R.layout.simple_list_item_1,
							availableFriends);
				// adapter = new SimpleAdapter(availableFriends,
				// FriendsActivity.this);
				else {
					Pattern pattern = Pattern.compile("" + cs,
							Pattern.CASE_INSENSITIVE);
					List<Friend> filteredFriends = new ArrayList<Friend>();
					for (Friend friend : availableFriends) {
						Matcher matcher = pattern.matcher(friend
								.getFriendName());
						if (matcher.find()) {
							filteredFriends.add(friend);
						}
					}
					adapter = new ListAdapter(FriendsActivity.this,
							android.R.layout.simple_list_item_1,
							filteredFriends);
					// adapter = new SimpleAdapter(filteredFriends,
					// FriendsActivity.this);
				}
				listView.setAdapter(adapter);
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		/*
		 * nameText = (TextView) findViewById(R.id.userName);
		 * nameText.setOnClickListener(this);
		 * userOwnImage=(ImageView)findViewById(R.id.userImage);
		 * userOwnImage.setOnClickListener(this);
		 */

		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(this);
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);
		noFriendsYetText = (TextView) findViewById(R.id.noFriendsYetBox);
		ImageView instantBackIcon = (ImageView) findViewById(R.id.instantBackButton);
		instantBackIcon.setOnClickListener(this);
		TextView listIntroductionText = (TextView) findViewById(R.id.listIntroText);
		listIntroductionText.setText(R.string.friends);

		populateFriends();

		// VISH updateNotificationAndPeopleNearbyCounts();
	}

	private void populateFriends() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					GetFriendsData getFriendsData = new GetFriendsData();
					getFriendsData.execute();
				}
			});
		} catch (Exception e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
		}
	}

	private class GetFriendsData extends AsyncTask<Void, Void, List<Friend>> {
		public GetFriendsData() {
			super();
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(
					FriendsActivity.this,
					null,
					FriendsActivity.this.getResources().getString(
							R.string.lodingMessage), true);
		}

		@Override
		protected List<Friend> doInBackground(Void... params) {
			return getFriendsList();
		}

		@Override
		protected void onPostExecute(List<Friend> friendsData) {
			List<Friend> dataExist = friendsData;
			if ((dataExist != null) && (!(dataExist.isEmpty()))) {
				displayFriends(dataExist);
			} else {
				listView.setVisibility(View.GONE);
				noFriendsYetText.setVisibility(View.VISIBLE);
			}
			handler.sendEmptyMessage(2);
		}
	}

	public void run() {
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backIcon:
			backIcon.setAlpha(100);
			navigateMenuScreen(view);
			break;
		case R.id.instantBackButton:
			navigateMenuScreen(view);
			break;
			/*
			 * case R.id.userName: navigateProfileScreen(view); break; case
			 * R.id.userImage: navigateProfileScreen(view); break;
			 */
		case R.id.peopleNearByNotification:
			peopleNearByIcon.setAlpha(100);
			navigatePeopleNearByScreen(view);
			break;
		case R.id.friendRequestNotification:
			friendRequestsIcon.setAlpha(100);
			navigateFriendRequestScreen(view, Constants.CALLING_SCREEN_FRIENDS);
			break;
		case R.id.lisnxLayout:
			sendInvitation(view);
			break;
		}
	}

	private void sendInvitation(View view) {
		Map<String, String> params = getParamsMapWithTokenAndTimeZone(handler);
		// inviteFriends?channel=FACEBOOK&inviteeList=fabc,asd&invitationMessage=welcometolisnx&token=
		params.put(Constants.CHANNEL, "FACEBOOK");
		View rootView = view.getRootView();
		FriendsViewHolder holder = (FriendsViewHolder) ((View) view.getParent()
				.getParent().getParent().getParent()).getTag();
		params.put(Constants.INVITEE_LIST, friendList.get(holder.position)
				.getFriendID());// otherProfile.getId());
		params.put(Constants.INVITATION_MESSAGE, "DEFAULT");
		Status status = null;

		try {
			status = Utils.sendInvitation(params, this.getApplicationContext());
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
		}
		if (status != null) {
			adapter.allFriends.get(holder.position).setInvited(true);
			adapter.notifyDataSetChanged();
			// holder.lisnxLayout.setBackgroundResource(R.drawable.green_rectangle);
			// holder.lisnxImageText.setText("Invited");
		} else {
			handler.sendEmptyMessage(1);
		}

	}

	public List<Friend> getFriendsList() {
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

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.ACCESS_TOKEN_PARAM, accessToken);

		List<Friend> friendsList = null;
		try {
			friendsList = Utils.getFriendsList(params, this);
		} catch (NullPointerException e) {
			handler.sendEmptyMessage(12);
			return null;
		} catch (JSONException e) {
			// handler.sendEmptyMessage(11);
			return null;
		}
		return friendsList;
	}

	public void displayFriends(List<Friend> friendList) {
		Collections.sort(friendList, new Comparator<Friend>() {
			@Override
			public int compare(Friend lhs, Friend rhs) {
				return lhs.getFriendName().compareTo(rhs.getFriendName());
			}
		});
		List<Friend> friendsList = friendList;
		availableFriends = new ArrayList<Friend>();
		for (Friend friend : friendsList) {
			availableFriends.add(friend);
		}
		try {
			adapter = new ListAdapter(FriendsActivity.this,
					android.R.layout.simple_list_item_1, friendList);
			// adapter = new SimpleAdapter(friendList, FriendsActivity.this);
			listView.setAdapter(adapter);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			OnItemClickListener listener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					OtherProfileActivity.callingOtherProfileScreen = Constants.CALLING_SCREEN_FRIENDS;
					fromItemClickListener = true;
					@SuppressWarnings("unchecked")
					Friend friend = (Friend) adapter.getAdapter().getItem(
							position);
					try {
						Intent otherProfileIntent = new Intent(
								view.getContext(), OtherProfileActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Bundle bundleObj = new Bundle();
						bundleObj.putString("lisnerId",
								(String) friend.getFriendID());
						bundleObj.putString("commonFriendsUid", uid);
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
			bmImg = Utils.getOtherPersonImage(params, FriendsActivity.this);
		} catch (NullPointerException e) {
			// handler.sendEmptyMessage(1);
		} catch (JSONException e) {
			// handler.sendEmptyMessage(11);
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

	public void refreshFriendsList() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					List<Friend> dataExist = getFriendsList();
					if ((dataExist != null) && (dataExist.size() != 0)) {
						listView.setVisibility(View.VISIBLE);
						noFriendsYetText.setVisibility(View.GONE);
						displayFriends(dataExist);
					} else {
						listView.setVisibility(View.GONE);
						noFriendsYetText.setVisibility(View.VISIBLE);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void navigateProfileScreen(View view) {
		ProfileActivity.callingProfileScreen = Constants.CALLING_SCREEN_FRIENDS;
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
		PeopleNearByActivity.callingPeopleNearByScreen = Constants.CALLING_SCREEN_FRIENDS;
		try {
			Intent peopleNearByIntent = new Intent(view.getContext(),
					PeopleNearByActivity.class);
			startActivityForResult(peopleNearByIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	@SuppressLint("HandlerLeak")
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
						Constants.NO_FRIENDS_ERROR,
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

	private static int currentDownloads = 0;

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
			while (true) {
				if (currentDownloads < 10) {
					currentDownloads++;
					return getOtherUserImage(otherId);
				} else
					continue;
			}

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
				currentDownloads--;
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

	// private Map<String, String> nameIndexMap;

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

	private class ListAdapter extends ArrayAdapter<Friend> implements
	SectionIndexer {
		private List<Friend> allFriends;
		private LayoutInflater mInflater;
		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public ListAdapter(Context context, int textViewResourceId,
				List<Friend> allFriends) {
			super(context, textViewResourceId, allFriends);
			this.allFriends = allFriends;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return allFriends.size();
		}

		@Override
		public Friend getItem(int position) {
			return allFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			 final FriendsViewHolder holder;
			if (convertView == null||!(convertView instanceof TextView)||!(convertView instanceof ImageView)) {
				convertView = mInflater.inflate(R.layout.friend_list_view, null);

				holder = new FriendsViewHolder();
				holder.lisnxLayout = (LinearLayout)convertView.findViewById(R.id.lisnxLayout);
				holder.lisnxLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendInvitation(v);
					}
				});
				holder.friendName = (TextView) convertView.findViewById(R.id.friendName);
				holder.friendImage = (ImageView) convertView.findViewById(R.id.friendImage);
				holder.imagePosition = position ;
				holder.connectedOnFacebookImage = (ImageView) convertView.findViewById(R.id.connectedOnFacebookImage) ;
				holder.connectedOnLinkedinImage = (ImageView) convertView.findViewById(R.id.connectedOnLinkedinImage) ;
				holder.connectedOnLisnxImage = (ImageView) convertView.findViewById(R.id.connectedOnLisnxImage) ;
				holder.lisnxImageText = (TextView) convertView.findViewById(R.id.lisnxImageText);
				holder.position = position;

				convertView.setTag(holder);
			}else {
				holder = (FriendsViewHolder) convertView.getTag(); 
			}


			Friend friend = allFriends.get(position);
			holder.friendName.setText((String) friend.getFriendName());
			String otherId=(String) friend.getFriendID();
			String isImage=(String) friend.getIsImage();

			if("true".equalsIgnoreCase(isImage)){



				//Picasso.with(FriendsActivity.this).load(Constants.GET_OTHER_PROFILE_PICTURE_URL+"?"+Constants.USER_ID_PARAM+"="+ otherId).resize(imageWidth, imageHeight).into(holder.friendImage);
				//BitmapFactory.Options op = new BitmapFactory.Options();

				//ImageSizeUtils.defineTargetSizeForView( holder.friendImage, targetSize);
				//imageLoader.displayImage(Constants.GET_OTHER_PROFILE_PICTURE_URL+"?"+Constants.USER_ID_PARAM+"="+ otherId, targetSize ,holder.friendImage);
				
				imageLoader.loadImage(Constants.GET_OTHER_PROFILE_PICTURE_URL+"?"+Constants.USER_ID_PARAM+"="+ otherId, targetSize,  new HardRefSimpleImageLoadingListener() {
				    @Override
				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				    	
				    	holder.friendImage.setImageBitmap(loadedImage);
				    }
				});
				
				
				//downloadImage(otherId , holder.friendImage, position);	
			}
			if(friend.isConnectedOnFacebook())
				holder.connectedOnFacebookImage.setVisibility(View.VISIBLE);

			if(friend.isConnectedOnLinkedin())
				holder.connectedOnLinkedinImage.setVisibility(View.VISIBLE);

			if(friend.isConnectedOnLisnx()){
				holder.connectedOnLisnxImage.setVisibility(View.VISIBLE);
				holder.lisnxImageText.setText("Say hi");
			}
			else if(friend.wasInvited()){
				holder.lisnxLayout.setBackgroundResource(R.drawable.green_rectangle);
				holder.lisnxImageText.setText("Invited");
			}
			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			Log.d("ListView", "Get position for section");
			for (int i = 0; i < this.getCount(); i++) {
				String item = this.getItem(i).getFriendName().toLowerCase();
				if (item.charAt(0) == Character.toLowerCase(mSections
						.charAt(section))) {
					return i;
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			Log.d("ListView", "Get section");
			return 0;
		}

		@Override
		public Object[] getSections() {
			Log.d("ListView", "Get sections");
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++) {
				sections[i] = String.valueOf(mSections.charAt(i));
			}
			return sections;
		}

	}
	public class HardRefSimpleImageLoadingListener extends SimpleImageLoadingListener {
	    private View mView;

	    @Override
	    public void onLoadingStarted(String imageUri, View view) {
	        mView = view;
	    }
	}
}