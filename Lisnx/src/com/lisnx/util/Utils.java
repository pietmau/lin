package com.lisnx.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

import com.lisnx.android.activity.BuildConfig;
import com.lisnx.android.activity.InternetDialogActivity;
import com.lisnx.android.activity.LoginActivity;
import com.lisnx.android.activity.OtherProfileActivity;
import com.lisnx.dao.DatabaseHelper;
import com.lisnx.dao.DatabaseUtility;
import com.lisnx.dao.LisnDao;
import com.lisnx.dao.PastLisnDao;
import com.lisnx.model.CommonLisn;
import com.lisnx.model.Friend;
import com.lisnx.model.Lisn;
import com.lisnx.model.LisnDetail;
import com.lisnx.model.Login;
import com.lisnx.model.MessageDetail;
import com.lisnx.model.MessageNotifications;
import com.lisnx.model.NotificationCount;
import com.lisnx.model.Notification;
import com.lisnx.model.OtherProfile;
import com.lisnx.model.PersonNearBy;
import com.lisnx.model.PrivateMessage;
import com.lisnx.model.Profile;
import com.lisnx.model.Status;
import com.lisnx.model.UserConnectivity;
import com.lisnx.service.BaseActivity;

public class Utils extends LoginActivity{
	
	private static SimpleDateFormat  dateFormat = new SimpleDateFormat("MMM dd, yy HH:mm");
	private static SimpleDateFormat  parseDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   	
	public static Date parseDate(String dateString) throws ParseException{
		return parseDateformat.parse(dateString);
	}
	public static String formatDate(Date date){
		Date now = new Date();
		long diffTime = now.getTime()-date.getTime();
		if(diffTime < 60*60*1000){
			return new SimpleDateFormat("hh:mm a").format(date);
		}else if(diffTime < 365*24*60*60*1000) {
			return new SimpleDateFormat("MMM dd hh:mm a").format(date);
		}else {
			return new SimpleDateFormat("MMM dd yyyy, hh:mm a").format(date);
		}
	}
	public static String parseAndFormat(String dateString) throws ParseException{
		return formatDate(parseDate(dateString));
	}
	
	public static String postData(String url, Map<String , String> params, Context context) {
		String responseString=null;
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "URL : "+url);
		if(!isConnectedToInternet(context) && (AppCrashReports.isActivityVisible() == true)){
			try{
    			Intent i=new Intent(context, InternetDialogActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(i);
    		} catch(Exception e){
    			e.printStackTrace();
    		}
		} else{
			//System.out.println("The url is................"+url);
			responseString = postAndProcess(url, params);
		}
	    return responseString;
	}

	private static String postAndProcess(String url, Map<String, String> params) {
		String responseString;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		StringBuilder builder = null;
		try {
			if(params != null){
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    for (Map.Entry<String , String> entry : params.entrySet()) { 
		    	//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		    	nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		    }
		    UrlEncodedFormEntity form = new UrlEncodedFormEntity(nameValuePairs);
		    httppost.setEntity(form);
			}
			
			httppost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line);
			}
			reader.close();   
			//System.out.println("======= Response ======= " + builder.toString());
			responseString=builder.toString();
		} catch (ClientProtocolException e) {
			responseString= null;
		} catch (IOException e) {
			responseString= null;
		}
		return responseString;
	} 
	
	public static class PostDataTask extends AsyncTask<String, String, String>{
		
		private String url;
		private Map<String, String> paramsMap;
		private String responseString;

		public PostDataTask(String url, Map<String, String> paramsMap, String responseString){
			this.url = url;
			this.paramsMap = paramsMap;
			this.responseString = responseString;
		}

		@Override
		protected String doInBackground(String... params) {
			return postAndProcess(url, paramsMap);
		}

		@Override
		protected void onPostExecute(String result) {
			responseString = result;
		}
		
		
	}
	
	public static Bitmap scaleProportionally(Bitmap bm, int targetHeight, int targetWidth){
		int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) targetWidth) / width;
	    float scaleHeight = ((float) targetHeight) / height;
	    
	    
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleWidth);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	    return resizedBitmap;
	}
	
	public static MessageDetail getMessageDetails(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseMessageDetails = postData(Constants.GET_MESSAGE_DETAILS_FOR_LISN_URL, params, context);
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "RESPONSE MESSAGE DETAILS : "+responseMessageDetails);
		
		if(responseMessageDetails == null || responseMessageDetails.length() == 0){
			throw new NullPointerException();
		}
		MessageDetail messageDetail = null;
		try {
			messageDetail = JsonParser.parseMessageDetailResponse(responseMessageDetails);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return messageDetail;
	}
	
	public static List<PersonNearBy> getPeopleNearByList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responsePeopleNearBy = postData(Constants.GET_PEOPLE_NEARBY_URL, params, context);
		if(responsePeopleNearBy == null || responsePeopleNearBy.length() == 0){
			throw new NullPointerException();
		}
		
		List<PersonNearBy> personNearByList = null;
		try {
			personNearByList = JsonParser.parsePeopleNearByResponse(responsePeopleNearBy);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
	
		return personNearByList;
	}
	
	public static Login getloginToken(Context context) throws NullPointerException, JSONException{
		String responseLoginToken = postData(Constants.LOGIN_TOKEN_URL, null, context);
		if(responseLoginToken == null || responseLoginToken.length() == 0){
			throw new NullPointerException();
		}
		Login loginToken = null;
		try {
			loginToken = JsonParser.parseLoginToken(responseLoginToken);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return loginToken;
	}
	
	public static Login getAccessToken(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseLogin = null;
		responseLogin = postData(Constants.LOGIN_URL, params, context);
		
		if(responseLogin == null || responseLogin.length() == 0){
			throw new NullPointerException();
		}
		Login accessToken = null;
		try {
			accessToken = JsonParser.parseAccessToken(responseLogin);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return accessToken;
	}
	
	public static Status registerUser(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseRegister = postData(Constants.REGISTRATION_URL, params, context);
		if(responseRegister == null || responseRegister.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		
		try {
			status = JsonParser.parseRegisterResponse(responseRegister);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status changePassword(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseChangePassword = postData(Constants.CHANGE_PASSWORD_URL, params, context);
		if(responseChangePassword == null || responseChangePassword.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseChangePasswordResponse(responseChangePassword);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status sendLocationToServer(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseSendLocation = postData(Constants.SEND_LOCATION_TO_SERVER_URL, params, context);
		if(responseSendLocation == null || responseSendLocation.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseSendLocationResponse(responseSendLocation);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status sendLastViewedMessageId(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseSendLastViewedMessageId = postData(Constants.SET_LAST_VIEWED_MESSAGE_ID_URL, params, context);
		if(responseSendLastViewedMessageId == null || responseSendLastViewedMessageId.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseSendLastViewedMessageId(responseSendLastViewedMessageId);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status notifyForAcceptedRequestSeen(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseNotifyForAcceptedRequestSeen = postData(Constants.FRIEND_REQUEST_IS_ACCEPTED_URL, params, context);
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"Accepted Request Seen : "+responseNotifyForAcceptedRequestSeen);
		if(responseNotifyForAcceptedRequestSeen == null || responseNotifyForAcceptedRequestSeen.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseNotifyForAcceptedRequestSeen(responseNotifyForAcceptedRequestSeen);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status sendFriendRequest(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseSendFriendRequest = postData(Constants.SEND_FRIEND_REQUEST_URL, params, context);
		if(responseSendFriendRequest == null || responseSendFriendRequest.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseSendFriendRequestResponse(responseSendFriendRequest);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status sendInvitation(Map<String, String> params, Context applicationContext) throws JSONException {
		Status acceptFriendRequestResponse = new Status();
		String response = postData(Constants.INVITE_FRIENDS_URL, params, applicationContext);
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusString = jsonObject.getString("status");
			acceptFriendRequestResponse.setMessage(jsonObject.getString("message"));
			acceptFriendRequestResponse.setStatus(statusString);
		}catch(JSONException e){
			throw new JSONException(null);
		}
		return acceptFriendRequestResponse;
	}
	
	public static Status acceptFriendRequest(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseAcceptFriendRequest = postData(Constants.ACCEPT_FRIEND_REQUEST_URL, params, context);
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"Accept Friend Request Response : "+responseAcceptFriendRequest); 
		if(responseAcceptFriendRequest == null || responseAcceptFriendRequest.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseAcceptFriendRequestResponse(responseAcceptFriendRequest);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status ignoreFriendRequest(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseIgnoreFriendRequest = postData(Constants.IGNORE_FRIEND_REQUEST_URL, params, context);
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG, "Ignore Friend Request Response : "+responseIgnoreFriendRequest); 
		if(responseIgnoreFriendRequest == null || responseIgnoreFriendRequest.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseIgnoreFriendRequestResponse(responseIgnoreFriendRequest);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static String getTimeZone()
	{
		Date date = new Date();
		@SuppressWarnings("deprecation")
		double time=(-date.getTimezoneOffset())/60.00;
		String timeZone=time+"";
		return timeZone;
	}
	
	public static Status createLisn(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseCreateLisn = postData(Constants.CREATE_LISN_URL, params, context);
		if(responseCreateLisn == null || responseCreateLisn.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseCreateLisnResponse(responseCreateLisn);
			
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static List<Lisn> getLisnAroundMe(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseLisnAroundMe = postData(Constants.LISNS_AROUND_ME_URL, params, context);
		if(responseLisnAroundMe == null || responseLisnAroundMe.length() == 0){
			throw new NullPointerException();
		}
		
		List<Lisn> lisnList = null;
		try {
			lisnList = JsonParser.parseLisnResponse(responseLisnAroundMe);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		dao.deleteAllRecords(LisnDao.LISN_TABLE_NAME);
		dao.populateLisnTable(lisnList);
		return lisnList;
	}
	
	public static List<CommonLisn> getCommonLisn(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseCommonLisn = postData(Constants.GET_COMMON_LISNS_URL, params, context);
		if(responseCommonLisn == null || responseCommonLisn.length() == 0){
			throw new NullPointerException();
		}
		
		List<CommonLisn> lisnList = null;
		try {
			lisnList = JsonParser.parseCommonLisnResponse(responseCommonLisn);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return lisnList;
	}
	
	public static Status sendExternalConnectionRequest(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String response = postData(Constants.SEND_EXTERNAL_CONNECTION_REQUEST_URL, params, context);
		if(response == null || response.length() == 0){
			throw new NullPointerException();
		}
		
		Status status = null;
		try {
			status = JsonParser.parseExternalConnectionRequestResponse(response);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static List<Lisn> getPastLisn(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responsePastLisn = postData(Constants.PAST_LISNS_URL, params, context);		
		if(responsePastLisn == null || responsePastLisn.length() == 0){
			throw new NullPointerException();
		}
		
		List<Lisn> lisnList = null;
		try {
			lisnList = JsonParser.parseLisnResponse(responsePastLisn);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseUtility dao = new DatabaseUtility(helper);
		dao.deleteAllRecords(PastLisnDao.PAST_LISN_TABLE_NAME);
		dao.populatePastLisnTable(lisnList);
		return lisnList;
	}
	
	public static List<Notification> getNotificationsList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseNotifications = postData(Constants.GET_NOTIFICATION_URL, params, context);
		
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"Response Notifications : "+responseNotifications);
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "PARAMS IN UTILS : "+params.toString());
		if(responseNotifications == null || responseNotifications.length() == 0){
			throw new NullPointerException();
		}
		
		List<Notification> notificationsList = null;
		try {
			notificationsList = JsonParser.parseNotificationsResponse(responseNotifications);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return notificationsList;
	}
	public static List<Notification> getMessagesList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseNotifications = postData(Constants.GET_NOTIFICATION_URL, params, context);
		if(responseNotifications == null || responseNotifications.length() == 0){
			throw new NullPointerException();
		}
		
		List<Notification> notificationsList = null;
		try {
			notificationsList = JsonParser.parseNotificationsResponse(responseNotifications);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return notificationsList;
	}
	
	public static List<MessageNotifications> getMessageNotificationsList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseMessageNotifications = postData(Constants.GET_UNREAD_MESSAGE_COUNT_URL, params, context);
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG,"Response Message Notifications : "+responseMessageNotifications);
		
		if(responseMessageNotifications == null || responseMessageNotifications.length() == 0){
			throw new NullPointerException();
		}
		
		List<MessageNotifications> messageNotificationsList = null;
		try {
			messageNotificationsList = JsonParser.parseMessageNotificationsResponse(responseMessageNotifications);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return messageNotificationsList;
	}
	
	public static List<Friend> getFriendsList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseFriends="";
		responseFriends = postData(Constants.GET_FRIENDS_URL, params, context);
		if(responseFriends == null || responseFriends.length() == 0){
			throw new NullPointerException();
		}
		
		List<Friend> friendsList = null;
		try {
			friendsList = JsonParser.parseFriendsResponse(responseFriends);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return friendsList;
	}
	
	public static List<Friend> getCommonFriendsList(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseFriends="";
		responseFriends = postData(Constants.GET_COMMON_FRIENDS_URL, params, context);
		if(responseFriends == null || responseFriends.length() == 0){
			throw new NullPointerException();
		}
		
		List<Friend> friendsList = null;
		try {
			friendsList = JsonParser.parseFriendsResponse(responseFriends);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return friendsList;
	}
	
	public static LisnDetail getLisnDetails(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseLisnDetails = postData(Constants.LISNX_DETAIL_URL, params, context);		
		if(responseLisnDetails == null || responseLisnDetails.length() == 0){
			throw new NullPointerException();
		}
		LisnDetail lisnDetail = null;
		try {
			lisnDetail = JsonParser.parseLisnDetailResponse(responseLisnDetails);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return lisnDetail;
	}
	
	public static MessageDetail getMessagePostDetails(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseMessagePostDetails = postData(Constants.GET_MESSAGE_POST_DETAILS_FOR_LISN_URL, params, context);
		if(responseMessagePostDetails == null || responseMessagePostDetails.length() == 0){
			throw new NullPointerException();
		}
		MessageDetail messageDetail = null;
		try {
			messageDetail = JsonParser.parseMessagePostDetailResponse(responseMessagePostDetails);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return messageDetail;
	}
	
	public static MessageDetail postMessage(Map<String , String> params, Context context) throws NullPointerException, JSONException{														
		@SuppressWarnings("unused")
		String responseMessagePostDetails = postData(Constants.SEND_PRIVATE_MESSAGE_URL, params, context);
		MessageDetail messageDetail = null;
		return messageDetail;
	}
	
	public static List<PrivateMessage> getPrivateMessage(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responsePrivateMessage = postData(Constants.GET_PRIVATE_MESSAGE_URL, params, context);
		List<PrivateMessage> privateMessage = null;
		
		try {
			privateMessage = JsonParser.parsePrivateMessageResponse(responsePrivateMessage);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return privateMessage;
	}
	
	public static Profile getProfileData(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		Profile profile = null;
		try{
			String responseProfileData = postData(Constants.USER_PROFILE_URL, params, context);
			if(responseProfileData == null || responseProfileData.length() == 0){
				throw new NullPointerException();
			}
			try {
				profile = JsonParser.parseProfileResponse(responseProfileData);
			} catch (JSONException e) {
				throw new JSONException(null);
			}
		}catch(Exception e){
			Log.e(Utils.class.getName(), "getProfileData", e);
		}
		return profile;
	}
	
	public static UserConnectivity getUserConnectivity(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseUserConnectivity = postData(Constants.GET_BUCKET_DETAILS_URL, params, context);
		if(responseUserConnectivity == null || responseUserConnectivity.length() == 0){
			throw new NullPointerException();
		}
		UserConnectivity userConnectivity = null;
		userConnectivity = JsonParser.parseUserConnectivityResponse(responseUserConnectivity);
		return userConnectivity;
	}
	
	public static NotificationCount getNotificationCounts(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseNotificationCounts = postData(Constants.NOTIFICATION_COUNT_URL, params, context);
		if(BuildConfig.DEBUG) Log.v(Constants.DEBUG_TAG, "Notification Count Response : "+responseNotificationCounts);
		if(responseNotificationCounts == null || responseNotificationCounts.length() == 0){
			throw new NullPointerException();
		}
		NotificationCount notificationCount = null;
		notificationCount = JsonParser.parseNotificationCountsResponse(responseNotificationCounts);
		return notificationCount;
	}
	
	public static OtherProfile getOtherProfileData(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseOtherProfileData=null;
		if(OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_LISN_DETAIL)){
			responseOtherProfileData = postData(Constants.USER_DETAILS_FOR_LISN_URL, params, context);
			
		}else if(OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_PEOPLE_NEARBY)||
				OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_FRIENDS)||
				OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_COMMON_FRIEND)||
				OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_CHOOSE_PROFILE)||
				OtherProfileActivity.callingOtherProfileScreen.equalsIgnoreCase(Constants.CALLING_SCREEN_NOTIFICATIONS)){
			responseOtherProfileData = postData(Constants.GET_PEOPLE_NEARBY_PROFILE_URL, params, context);
		}
		
		if(responseOtherProfileData == null || responseOtherProfileData.length() == 0){
			throw new NullPointerException();
		}
		OtherProfile otherProfile = null;
		otherProfile = JsonParser.parseOtherProfileResponse(responseOtherProfileData);
		return otherProfile;
	}
	
	public static Status updateUserProfile(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseProfileUpdate = postData(Constants.UPDATE_PROFILE_URL, params, context);
		if(responseProfileUpdate == null || responseProfileUpdate.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseUpdateUserProfileResponse(responseProfileUpdate);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status setProfilePictureWithAccessToken(String accessToken,File photos, String imageFormat, Context context) throws NullPointerException, JSONException{
		if(!isConnectedToInternet(context) && (AppCrashReports.isActivityVisible() == true)){
			try{
    			Intent i=new Intent(context, InternetDialogActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(i);
    			return null;
    		} catch(Exception e){
    			e.printStackTrace();
    			return null;
    		}
		} else{
			StringBuilder builder = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.SET_PROFILE_PICTURE_URL);
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    		try {
    			mpEntity.addPart(Constants.ACCESS_TOKEN_PARAM, new StringBody(accessToken));
    			ExifInterface exif = new ExifInterface(photos.getAbsolutePath());
    			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
    			
    			mpEntity.addPart("orientation", new StringBody(""+orientation));
    			if(imageFormat.equalsIgnoreCase("png")){
    				mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/png"));
    			}else if(imageFormat.equalsIgnoreCase("jpg")){
    				mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/jpeg"));
            	}else if(imageFormat.equalsIgnoreCase("jpeg")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/jpeg"));
            	}else if(imageFormat.equalsIgnoreCase("gif")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/gif"));
           	 	}else if(imageFormat.equalsIgnoreCase("bmp")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/bmp"));
           	 	}else {
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/gif"));
            	}
    		} catch (Exception e1) {
    			e1.printStackTrace();
    		}
    		
    		httppost.setEntity(mpEntity);
    		try{
       	 		HttpResponse response = httpclient.execute(httppost);
       	 		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        		builder = new StringBuilder();
        		for (String line = null; (line = reader.readLine()) != null;) {
        			builder.append(line);
        		}
        		reader.close();
        		//System.out.println("================== Response ================= " + builder.toString());
    		} catch (ClientProtocolException e) {
    		} catch (IOException e) {}
    	
    		String responseProfileUploadPicture=builder.toString();
    		if(responseProfileUploadPicture == null || responseProfileUploadPicture.length() == 0){
    			throw new NullPointerException();
    		}
			Status status = null;
			try {
				status = JsonParser.parseProfileUploadPictureResponse(responseProfileUploadPicture);
			} catch (JSONException e) {
				throw new JSONException(null);
			}
			return status;
		}
	}
	
	public static Status setProfilePictureWithUsername(String userName, File photos, String imageFormat, Context context) throws NullPointerException, JSONException{
		if(!isConnectedToInternet(context) && (AppCrashReports.isActivityVisible() == true)){
			try{
    			Intent i=new Intent(context, InternetDialogActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(i);
    			return null;
    		} catch(Exception e){
    			e.printStackTrace();
    			return null;
    		}
		} else{
			StringBuilder builder = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.SET_PROFILE_PICTURE_WITH_USERNAME_URL);
		
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				mpEntity.addPart(Constants.USERNAME_PARAM_2, new StringBody(userName));
				if(imageFormat.equalsIgnoreCase("png")){
					mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/png"));
            	}else if(imageFormat.equalsIgnoreCase("jpg")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/jpeg"));
            	}else if(imageFormat.equalsIgnoreCase("jpeg")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/jpeg"));
            	}else if(imageFormat.equalsIgnoreCase("gif")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/gif"));
            	}else if(imageFormat.equalsIgnoreCase("bmp")){
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/bmp"));
            	}else {
            		mpEntity.addPart(Constants.PROFILE_PICTURE_PARAM, new FileBody(photos, "image/gif"));
            	}   
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	
			httppost.setEntity(mpEntity);
			try{
       	 		HttpResponse response = httpclient.execute(httppost);
       	 		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
       	 		builder = new StringBuilder();
       	 		for (String line = null; (line = reader.readLine()) != null;) {
       	 			builder.append(line);
       	 		}
       	 		reader.close();
       	 		//System.out.println("================== Response ================= " + builder.toString());
			} catch (ClientProtocolException e) {
			} catch (IOException e) {}
    	
			String responseProfileUploadPicture=builder.toString();
    		if(responseProfileUploadPicture == null || responseProfileUploadPicture.length() == 0){
				throw new NullPointerException();
    		}
			Status status = null;
			try {
				status = JsonParser.parseProfileUploadPictureResponse(responseProfileUploadPicture);
			} catch (JSONException e) {
				throw new JSONException(null);
			}
			return status;
		}
	}
	
	@SuppressWarnings("finally")
	public static Bitmap getUserImage(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		Bitmap bmImg = null;
		if(!isConnectedToInternet(context) && (AppCrashReports.isActivityVisible() == true)){
			try{
    			Intent i=new Intent(context, InternetDialogActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(i);
    			return null;
    		} catch(Exception e){
    			e.printStackTrace();
    			return null;
    		}
		} else{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.GET_PROFILE_PICTURE_URL);
			try {
		    	if(params != null){
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        for (Map.Entry<String , String> entry : params.entrySet()) { 
		        	//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		        	nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		        }
		        
		        UrlEncodedFormEntity form = new UrlEncodedFormEntity(nameValuePairs);
		        httppost.setEntity(form);
		    	}
	   
		    	HttpResponse response = httpclient.execute(httppost);
		    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				InputStream ins=response.getEntity().getContent();
				int bytesRead;
				byte[] pic = new byte[5000*1024];
	        
				while ((bytesRead = ins.read(pic, 0, pic.length)) != -1) {
					buffer.write(pic, 0, bytesRead);
				}

				buffer.flush();
				pic=buffer.toByteArray();
				bmImg = BitmapFactory.decodeByteArray(pic, 0, pic.length);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			finally{
				return bmImg;
			}
		}
	}
	private static LruCache<String, Bitmap> mMemoryCache;

	public static void setupBitmapCache() {
	    // Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	@SuppressWarnings("finally")
	public static Bitmap getOtherPersonImage(Map<String, String> params,
			Context context) throws NullPointerException, JSONException {
		Bitmap bmImg = null;
		if(mMemoryCache==null){
			setupBitmapCache();
		}
		bmImg = mMemoryCache.get(params.get(Constants.USER_ID_PARAM));
		if (bmImg == null) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					Constants.GET_OTHER_PROFILE_PICTURE_URL);
			try {
				if (params != null) {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> entry : params.entrySet()) {
						// System.out.println("Key = " + entry.getKey() +
						// ", Value = " + entry.getValue());
						nameValuePairs.add(new BasicNameValuePair(entry
								.getKey(), entry.getValue()));
					}

					UrlEncodedFormEntity form = new UrlEncodedFormEntity(
							nameValuePairs);
					httppost.setEntity(form);
				}

				HttpResponse response = httpclient.execute(httppost);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				InputStream ins = response.getEntity().getContent();

				int bytesRead;
				byte[] pic = new byte[5000 * 1024];

				while ((bytesRead = ins.read(pic, 0, pic.length)) != -1) {
					buffer.write(pic, 0, bytesRead);
				}

				buffer.flush();
				pic = buffer.toByteArray();
				bmImg = BitmapFactory.decodeByteArray(pic, 0, pic.length);
				mMemoryCache.put(params.get(Constants.USER_ID_PARAM), bmImg);

			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				return bmImg;
			}
		} else
			return bmImg;
	}
	
	public static Status addFacebookAccount(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseFacebook = postData(Constants.ADD_FACEBOOK_URL, params, context);
		if(responseFacebook == null || responseFacebook.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseFacebookResponse(responseFacebook);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	public static Login loginWithFacebook(Map<String , String> params, Context context) throws NullPointerException, JSONException{
			String responseFacebook = postData(Constants.LOGIN_WITH_FACEBOOK, params, context);
			if(responseFacebook == null || responseFacebook.length() == 0){
				throw new NullPointerException();
			}
			Login login = null;
			try {
				login = JsonParser.parseAccessToken(responseFacebook);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		return login;
	}
	
	public static Status addLinkedInAccount(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseLinkedIn = postData(Constants.ADD_LINKEDIN_URL, params, context);
		if(responseLinkedIn == null || responseLinkedIn.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseLinkedInResponse(responseLinkedIn);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status joinLisn(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseJoinLisn = postData(Constants.JOIN_LISN_URL, params, context);
		if(responseJoinLisn == null || responseJoinLisn.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseJoinLisnResponse(responseJoinLisn);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status sendEmailForTemporaryPassword(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseSendEmail = postData(Constants.FORGOT_PASSWORD_URL, params, context);
		if(responseSendEmail == null || responseSendEmail.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseSendEmailForTemporaryPasswordResponse(responseSendEmail);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}
	
	public static Status disableSocialNetworkSettings(Map<String , String> params, Context context) throws NullPointerException, JSONException{
		String responseDisableSocialNetworkSettings = postData(Constants.DISABLE_SOCIAL_NETWORKS_URL, params, context);
		if(responseDisableSocialNetworkSettings == null || responseDisableSocialNetworkSettings.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseDisableSocialNetworkSettingsResponse(responseDisableSocialNetworkSettings);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
	}

	public static String getAddress(double longitude, double lattitude , Context context){
		Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);   
		StringBuilder strReturnedAddress = new StringBuilder();
		try {
			  List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, Constants.MAX_ADDRESS_TO_RETURN);
			  if(addresses != null && addresses.size() > 0) {
			  
			    Address returnedAddress = addresses.get(0);
			    
			    for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
				     strReturnedAddress.append(returnedAddress.getAddressLine(i));
				     if(i<returnedAddress.getMaxAddressLineIndex() - 1){
				    	 strReturnedAddress.append(" ");
				     }
			    }
			  }
			  else{
				  strReturnedAddress.append(Constants.ERROR_NO_ADDRESS_FOUND);
			  }
		} catch (IOException e) {
			 strReturnedAddress.append(Constants.ERROR_NO_ADDRESS_FOUND);
		}
		return strReturnedAddress.toString();
	}

	public static boolean isDev() {
		return false;	 		//Dynamic coordinates
		//return true;			//Static coordinates
	}
	
	public static boolean isDevMode() {
		return false;			//No IP changer.
		//return true;			//IP changer shows.
	}
	
	public static boolean isConnectedToInternet(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
		return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public static Status sendNearbyNotificationRequest(Map params,
			Context applicationContext) throws JSONException{
		//return null;
		String responseSendLocation = postData(Constants.SEND_NOTIFICATION_REQUEST, params, applicationContext);
		if(responseSendLocation == null || responseSendLocation.length() == 0){
			throw new NullPointerException();
		}
		Status status = null;
		try {
			status = JsonParser.parseSendLocationResponse(responseSendLocation);
		} catch (JSONException e) {
			throw new JSONException(null);
		}
		return status;
		
	}

	
}