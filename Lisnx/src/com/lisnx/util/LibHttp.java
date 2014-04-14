package com.lisnx.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import com.facebook.Session;
import com.lisnx.android.activity.BuildConfig;
import com.lisnx.model.Login;
import com.lisnx.model.NotificationCount;
import com.lisnx.model.PersonNearBy;



/**
 * Contains web call used in app
 */
public class LibHttp
{
	private String url;
	
	private DefaultHttpClient hc;
	
	private ArrayList<NameValuePair> postParams;
	
	private HttpPost htttpPost;
	
	SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Constructor
	 */
	public LibHttp()
	{
		hc = new DefaultHttpClient();
		
		postParams = new ArrayList<NameValuePair>();
	}
	/**
	 * fbLogin webcall to login with facebook
	 * @param fbId
	 * @param fbSession
	 * @param parseId
	 * @return
	 * @throws Exception
	 */
	public Login fbLogin(String fbId,Session fbSession,String parseId) throws Exception
	{
		//url = AppConstants.APP_EMAIL_URL;
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"fbLogin");
		
		url = Constants.LOGIN_WITH_FACEBOOK;
		
		postParams.clear();
		
		postParams.add(new BasicNameValuePair(Constants.FACEBOOK_ID, fbId));
		postParams.add(new BasicNameValuePair(Constants.FACEBOOK_PARAMS.ACCESS_TOKEN.toString(), fbSession.getAccessToken()));
		postParams.add(new BasicNameValuePair(Constants.FACEBOOK_PARAMS.TOKEN_EXPIRATION_DATE.toString(), ""+fbSession.getExpirationDate().getTime()));
		postParams.add(new BasicNameValuePair(Constants.FACEBOOK_PARAMS.PERMISSIONS.toString(), fbSession.getPermissions().toString()));
		postParams.add(new BasicNameValuePair(Constants.PARSE_INSTALLATION_ID, parseId));
		
		
		String json = getJSONResponse();
			
		return JsonParser.parseAccessToken(json);
	}
	/**
	 * VISH
	 * logout webcall 
	 * @param accessToken
	 */
	public void logout(String accessToken) throws Exception
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"logout");
		
		url = Constants.LOGOUT_URL;
		
		postParams.clear();
		
		postParams.add(new BasicNameValuePair(Constants.ACCESS_TOKEN_PARAM, accessToken));
		
		getJSONResponse();		
	}
	
	/**
	 * VISH
	 * webcall to get people nearby list
	 * @param accessToken
	 * @return peopleNearBy list
	 */
	public List<PersonNearBy> getPeopleNearByList(String accessToken) throws Exception
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"getPeopleNearByList");
		
		url = Constants.GET_PEOPLE_NEARBY_URL;
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"URL People NearBy : "+url);
		postParams.clear();
		
		postParams.add(new BasicNameValuePair(Constants.ACCESS_TOKEN_PARAM, accessToken));
		
		String json = getJSONResponse();		
		
		return JsonParser.parsePeopleNearByResponse(json);
	}
	
	/**
	 * VISH
	 * webcall for notificationcounts
	 * @param accessToken, timeStamp
	 * @return notificationCounts
	 * @throws Exception
	 */
	public NotificationCount getNotificationCounts(String accessToken, String timeStamp) throws Exception
	{
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"getNotificationCounts");
		
		url = Constants.NOTIFICATION_COUNT_URL;
		
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,"URL People NearBy : "+url);
		
		postParams.clear();
		
		postParams.add(new BasicNameValuePair(Constants.ACCESS_TOKEN_PARAM, accessToken));
		
		postParams.add(new BasicNameValuePair(Constants.TIME_STAMP, timeStamp));
		
		String json = getJSONResponse();
		
		return JsonParser.parseNotificationCountsResponse(json);
	}
	
	/**
	 * Send request to server and get String response.
	 */
	private String getJSONResponse() throws Exception
	{
		htttpPost = null;
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		
		HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.CONNECTION_TIMEOUT * 1000);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		
		HttpConnectionParams.setSoTimeout(httpParameters, Constants.SOCKET_TIMEOUT * 1000);
		
		hc = new DefaultHttpClient(httpParameters);
		
		if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,url);
		
		htttpPost = new HttpPost(url);
		
		if( postParams.size() > 0 )
		{
			for(NameValuePair postParam : postParams)
			{
				if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,postParam.getName()+":"+postParam.getValue());
			}
			htttpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8) );
		}
		
		HttpResponse res = hc.execute (htttpPost);
		
		if(res.getStatusLine().getStatusCode() == 200)
		{
			int i = 0, t = 0;
			
		 	InputStream is = res.getEntity().getContent(); 
			
			StringBuilder sb = new StringBuilder();
			
			while( (t = is.read()) != -1 )
			{
				if( ((char)t) != '\0'  )
				{
					sb.append((char)t);
				}
			}
			
			if(BuildConfig.DEBUG)Log.v(Constants.DEBUG_TAG,sb.toString());
			
			return sb.toString();
			
		}
		throw new Exception("HTTP Response is NOT OK ("+res.getStatusLine().getStatusCode()+")");
	}	

	
}
