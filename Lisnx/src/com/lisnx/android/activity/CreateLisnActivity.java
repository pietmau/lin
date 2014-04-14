package com.lisnx.android.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lisnx.android.activity.R;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.event.EventNotifier;
import com.lisnx.event.ILisner;
import com.lisnx.model.CurrentLocation;
import com.lisnx.model.Status;
import com.lisnx.service.BaseActivity;
import com.lisnx.service.CustomToast;
import com.lisnx.ui.DateTimePicker;
import com.lisnx.ui.LisnDetailTabView;
import com.lisnx.ui.NotificationsTabView;
import com.lisnx.ui.TabView;
import com.lisnx.util.Constants;
import com.lisnx.util.Utils;

@SuppressLint("HandlerLeak")
public class CreateLisnActivity extends BaseActivity implements ILisner,
		OnClickListener, Runnable {

	@SuppressWarnings("unused")
	private EventNotifier eventNotifier;
	public static String name;
	public static String lisnDescription;
	public static String endDateTime;
	public static String token;
	public static String longitude;
	public static String lattitude;
	public static String timePredictor;
	public static String callingCreateLisnScreen = null;
	public String lisnId = null;
	Thread thread = null;
	ImageView backIcon;
	ImageView peopleNearByIcon;
	ImageView friendRequestsIcon;

	public static final int CREATE_LISN = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		eventNotifier = new EventNotifier(this);
		setContentView(R.layout.create_lisn);
		layoutInflater = this.getLayoutInflater();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		EditText dateTimePicker = (EditText) findViewById(R.id.endDateAndTimePicker);
		dateTimePicker.setOnClickListener(this);

		ImageButton createLisnButton = (ImageButton) findViewById(R.id.createButton);
		createLisnButton.setOnClickListener(this);
		ImageButton cancelButton = (ImageButton) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
		backIcon = (ImageView) findViewById(R.id.backIcon);
		backIcon.setOnClickListener(this);

		peopleNearByIcon = (ImageView) findViewById(R.id.peopleNearByNotification);
		peopleNearByIcon.setOnClickListener(this);
		friendRequestsIcon = (ImageView) findViewById(R.id.friendRequestNotification);
		friendRequestsIcon.setOnClickListener(this);
		/*
		 * nameText = (TextView) findViewById(R.id.userName);
		 * nameText.setOnClickListener(this);
		 * userImage=(ImageView)findViewById(R.id.userImage);
		 * userImage.setOnClickListener(this);
		 */

		//VISH updateNotificationAndPeopleNearbyCounts();
	}

	@Override
	public void updateLocation() {
		DatabaseHelper db = new DatabaseHelper(this);
		DatabaseUtility database = new DatabaseUtility(db);
		CurrentLocation location = database.getLocationData();
		if (location != null) {
		}
	}

	@Override
	public void run() {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.endDateAndTimePicker:
			showDateTimeDialog();
			break;
		case R.id.createButton:
			createButtonClickListener(view);
			break;
		case R.id.cancelButton:
			navigateTabScreen(view);
			break;
		case R.id.backIcon:
			backIcon.setAlpha(100);
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
			navigateFriendRequestScreen(view);
			break;
		}
	}

	private void showDateTimeDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);

		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.HOUR_OF_DAY, 1);
		mDateTimePicker.updateTime(c1.get(Calendar.HOUR_OF_DAY),
				c1.get(Calendar.MINUTE));
		mDateTimePicker.updateDate(c1.get(Calendar.YEAR),
				c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));

		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		final String timeS = android.provider.Settings.System.getString(
				getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));
		mDateTimePicker.setIs24HourView(true);

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.clearFocus();
						String dateTime = (mDateTimePicker.get(Calendar.MONTH) + 1)
								+ "/"
								+ mDateTimePicker.get(Calendar.DAY_OF_MONTH)
								+ "/"
								+ mDateTimePicker.get(Calendar.YEAR)
								+ "  ";

						if (mDateTimePicker.is24HourView()) {
							if (mDateTimePicker.get(Calendar.MINUTE) < 10) {
								dateTime += mDateTimePicker
										.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ "0"
										+ mDateTimePicker.get(Calendar.MINUTE);
							} else {
								dateTime += mDateTimePicker
										.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ mDateTimePicker.get(Calendar.MINUTE);
							}
						}

						else {
							timePredictor = (mDateTimePicker
									.get(Calendar.AM_PM) == Calendar.AM ? "AM"
									: "PM");

							if (timePredictor == "PM"
									&& ((mDateTimePicker.get(Calendar.HOUR)) == 0)) {
								if (mDateTimePicker.get(Calendar.MINUTE) < 10) {
									dateTime += 12
											+ ":"
											+ "0"
											+ mDateTimePicker
													.get(Calendar.MINUTE)
											+ " "
											+ (mDateTimePicker
													.get(Calendar.AM_PM) == Calendar.AM ? "AM"
													: "PM");
								} else {
									dateTime += 12
											+ ":"
											+ mDateTimePicker
													.get(Calendar.MINUTE)
											+ " "
											+ (mDateTimePicker
													.get(Calendar.AM_PM) == Calendar.AM ? "AM"
													: "PM");
								}
							}

							else {
								if (mDateTimePicker.get(Calendar.HOUR) < 10) {
									if (mDateTimePicker.get(Calendar.MINUTE) < 10) {
										dateTime += "0"
												+ mDateTimePicker
														.get(Calendar.HOUR)
												+ ":"
												+ "0"
												+ mDateTimePicker
														.get(Calendar.MINUTE)
												+ " "
												+ (mDateTimePicker
														.get(Calendar.AM_PM) == Calendar.AM ? "AM"
														: "PM");
									} else {
										dateTime += "0"
												+ mDateTimePicker
														.get(Calendar.HOUR)
												+ ":"
												+ mDateTimePicker
														.get(Calendar.MINUTE)
												+ " "
												+ (mDateTimePicker
														.get(Calendar.AM_PM) == Calendar.AM ? "AM"
														: "PM");
									}
								} else {
									if (mDateTimePicker.get(Calendar.MINUTE) < 10) {
										dateTime += mDateTimePicker
												.get(Calendar.HOUR)
												+ ":"
												+ "0"
												+ mDateTimePicker
														.get(Calendar.MINUTE)
												+ " "
												+ (mDateTimePicker
														.get(Calendar.AM_PM) == Calendar.AM ? "AM"
														: "PM");
									} else {
										dateTime += mDateTimePicker
												.get(Calendar.HOUR)
												+ ":"
												+ mDateTimePicker
														.get(Calendar.MINUTE)
												+ " "
												+ (mDateTimePicker
														.get(Calendar.AM_PM) == Calendar.AM ? "AM"
														: "PM");
									}
								}
							}
						}

						((EditText) findViewById(R.id.endDateAndTimePicker))
								.setText(dateTime);
						mDateTimeDialog.dismiss();
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	public void createButtonClickListener(final View view) {
		EditText nameText = (EditText) findViewById(R.id.nameField);
		EditText lisnDescriptionText = (EditText) findViewById(R.id.descriptionField);
		EditText endDateTimeText = (EditText) findViewById(R.id.endDateAndTimePicker);

		name = nameText.getText().toString().trim();
		lisnDescription = lisnDescriptionText.getText().toString().trim();
		endDateTime = endDateTimeText.getText().toString().trim();

		if (name == null || name.length() == 0) {
			CustomToast.showCustomToast(this,
					Constants.USER_NAME_FIELD_EMPTY_ERROR,
					Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			return;
		} else if (endDateTime == null || endDateTime.length() == 0) {
			CustomToast.showCustomToast(this,
					Constants.END_DATE_TIME_FIELD_EMPTY_ERROR,
					Constants.TOAST_VISIBLE_SHORT, layoutInflater);
			return;
		}

		try {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
		} catch (Exception e) {

		}

		ChooseProfileActivity.callingScreen = Constants.CALLING_SCREEN_CREATE;
		createLisn(view);
		// navigateChooseProfileScreen(view);
	}

	public void createLisn(View view) {
		String[] tempEndDateTime = null;
		try {
			if (timePredictor == "PM") {
				tempEndDateTime = endDateTime.split(" ");
				String[] tempTime = tempEndDateTime[2].split(":");
				int tempHours = Integer.parseInt(tempTime[0]);
				if (tempHours == 12) {
					tempHours = 12;
				} else {
					tempHours = tempHours + 12;
				}

				tempTime[0] = tempHours + "";
				tempEndDateTime[2] = tempTime[0] + ":" + tempTime[1];
				endDateTime = tempEndDateTime[0] + " " + tempEndDateTime[2];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (timePredictor == "AM") {
			tempEndDateTime = endDateTime.split(" ");
			endDateTime = tempEndDateTime[0] + " " + tempEndDateTime[2];
		}

		String timeZone = Utils.getTimeZone();
		String shareProfileType2 = Constants.PROFILE_SHARE_ALL;// ChooseProfileActivity.shareProfileType1;
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

		if (!Utils.isDev()) {
			Location lastKnownLocation = getLocation();
			if (lastKnownLocation == null) {
				handler.sendEmptyMessage(5);
				return;
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
		params.put(Constants.CREATE_LISN_PARAM, name);
		params.put(Constants.LISN_DESCRIPTION_PARAM, lisnDescription);
		params.put(Constants.END_DATE_TIME_PARAM, endDateTime);
		params.put(Constants.POST_ON_FACEBOOK_PARAM, "off");
		params.put(Constants.VENUE_PARAM, "");
		params.put(Constants.TIME_STAMP, timeZone);
		params.put(Constants.PROFILE_TYPE_PARAM, shareProfileType2);

		Status status = null;
		try {
			status = Utils.createLisn(params, getApplicationContext());
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
			String createLisnResponse = status.getMessage();
			try {
				String[] temp1 = createLisnResponse.split(":");
				String[] temp2 = temp1[1].split(" ");
				lisnId = temp2[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(2);
			navigateLisnDetailScreen(view);
			return;
		} else {
			Message msg = new Message();
			msg.what = 3;
			Bundle bundle = new Bundle();
			bundle.putString("message", status.getMessage());
			msg.setData(bundle);
			handler.sendMessage(msg);
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
				CustomToast.showCustomToast(getApplicationContext(),
						Constants.SUCCESSFUL_LISN_CREATION,
						Constants.TOAST_VISIBLE_LONG, layoutInflater);
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

	public Location getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

	public void navigateProfileScreen(View view) {
		ProfileActivity.callingProfileScreen = Constants.CALLING_SCREEN_CREATE_LISN;
		try {
			Intent profileIntent = new Intent(view.getContext(),
					ProfileActivity.class);
			startActivityForResult(profileIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigateChooseProfileScreen(View view) {
		try {
			Intent chooseProfileIntent = new Intent(view.getContext(),
					ChooseProfileActivity.class);
			startActivityForResult(chooseProfileIntent, CREATE_LISN);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	public void navigatePeopleNearByScreen(View view) {
		PeopleNearByActivity.callingPeopleNearByScreen = Constants.CALLING_SCREEN_CREATE_LISN;
		try {
			Intent peopleNearByIntent = new Intent(view.getContext(),
					PeopleNearByActivity.class);
			startActivityForResult(peopleNearByIntent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}
	}

	

	public void navigateLisnDetailScreen(View view) {
		try {
			i = new Intent(getApplicationContext(), LisnDetailTabView.class);
			Bundle bundleObj = new Bundle();
			bundleObj.putString("id", lisnId);
			i.putExtras(bundleObj);
			startActivity(i);
		} catch (Exception ex) {
		}
	}
	/*
	@Override
	public void onBackPressed() {
		try {
			Intent i = new Intent(getApplicationContext(), MenuActivity.class);
			startActivity(i);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (Exception ex) {
		}

	}
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CREATE_LISN) {
			if (resultCode == RESULT_OK) {
				pd = ProgressDialog.show(this, null, this.getResources()
						.getString(R.string.createLisn), true);
				thread = new Thread(new Runnable() {
					public void run() {
						createLisn(getCurrentFocus());
					}
				});
				thread.start();
			}
		}
	}
}