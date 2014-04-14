package com.lisnx.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
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

@SuppressLint({ "ParserError" })
public class JsonParser {

	public static Login parseLoginToken(String json) throws JSONException{
		Login loginToken = new Login();
		JSONObject jsonObject = new JSONObject(json);
		loginToken.setToken(jsonObject.getString("message"));
		loginToken.setStatus(jsonObject.getString("status"));
		return loginToken;
	}
	
	public static Login parseAccessToken(String json) throws JSONException{
		Login loginToken = new Login();
		JSONObject jsonObject = new JSONObject(json);
		loginToken.setStatus(jsonObject.getString("status"));
		
		if(Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			
			JSONObject message = new JSONObject(jsonObject.getString("message"));
			loginToken.setToken(message.getString("token"));
			loginToken.setId(message.getString("id"));
			loginToken.setResetPassword(message.getString("resetPassword"));
		}
		else if (Constants.ERROR.equalsIgnoreCase(jsonObject.getString("status")) )
		{
			throw new JSONException(jsonObject.getString("message"));
		}
		return loginToken;
	}
	
	public static Status parseFacebookResponse(String json) throws JSONException{
		Status registerFacebook = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			registerFacebook.setStatus(Constants.SUCCESS);
			registerFacebook.setMessage(jsonObject.getString("message"));
			return registerFacebook;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			registerFacebook.setStatus(Constants.ERROR);
			registerFacebook.setMessage(jsonObject.getString("message"));
		}
		return registerFacebook;
	}
	
	public static Status parseLinkedInResponse(String json) throws JSONException{
		Status registerLinkedIn = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			registerLinkedIn.setStatus(Constants.SUCCESS);
			registerLinkedIn.setMessage(jsonObject.getString("message"));
			return registerLinkedIn;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			registerLinkedIn.setStatus(Constants.ERROR);
			registerLinkedIn.setMessage(jsonObject.getString("message"));
		}
		return registerLinkedIn;
	}
	
	public static Status parseRegisterResponse(String json) throws JSONException{
		Status register = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			register.setStatus(Constants.SUCCESS);
			return register;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			register.setStatus(Constants.ERROR);
			register.setMessage(jsonObject.getString("message"));
		}
		return register;
	}
	
	public static Status parseChangePasswordResponse(String json) throws JSONException{
		Status changePassword = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			changePassword.setStatus(Constants.SUCCESS);
			return changePassword;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			changePassword.setStatus(Constants.ERROR);
			changePassword.setMessage(jsonObject.getString("message"));
		}
		return changePassword;
	}
	
	public static Status parseSendLocationResponse(String json) throws JSONException{
		Status sendLocation = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			sendLocation.setStatus(Constants.SUCCESS);
			return sendLocation;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			sendLocation.setStatus(Constants.ERROR);
			sendLocation.setMessage(jsonObject.getString("message"));
		}
		return sendLocation;
	}
	
	public static Status parseSendLastViewedMessageId(String json) throws JSONException{
		Status lastViewedMessageId = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			lastViewedMessageId.setStatus(Constants.SUCCESS);
			return lastViewedMessageId;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			lastViewedMessageId.setStatus(Constants.ERROR);
			lastViewedMessageId.setMessage(jsonObject.getString("message"));
		}
		return lastViewedMessageId;
	}
	
	public static Status parseNotifyForAcceptedRequestSeen(String json) throws JSONException{
		Status notifyForAcceptedRequestSeen = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			notifyForAcceptedRequestSeen.setStatus(Constants.SUCCESS);
			return notifyForAcceptedRequestSeen;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			notifyForAcceptedRequestSeen.setStatus(Constants.ERROR);
			notifyForAcceptedRequestSeen.setMessage(jsonObject.getString("message"));
		}
		return notifyForAcceptedRequestSeen;
	}
	
	public static Status parseSendFriendRequestResponse(String json) throws JSONException{
		Status sendFriendRequestResponse = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			sendFriendRequestResponse.setStatus(Constants.SUCCESS);
			return sendFriendRequestResponse;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			sendFriendRequestResponse.setStatus(Constants.ERROR);
			sendFriendRequestResponse.setMessage(jsonObject.getString("message"));
		}
		return sendFriendRequestResponse;
	}
	
	public static Status parseAcceptFriendRequestResponse(String json) throws JSONException{
		Status acceptFriendRequestResponse = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			acceptFriendRequestResponse.setStatus(Constants.SUCCESS);
			return acceptFriendRequestResponse;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			acceptFriendRequestResponse.setStatus(Constants.ERROR);
			acceptFriendRequestResponse.setMessage(jsonObject.getString("message"));
		}
		return acceptFriendRequestResponse;
	}
	
	public static Status parseIgnoreFriendRequestResponse(String json) throws JSONException{
		Status ignoreFriendRequestResponse = new Status();
		JSONObject jsonObject = new JSONObject(json);
		String status = jsonObject.getString("status");
		if(Constants.SUCCESS.equalsIgnoreCase(status)){
			ignoreFriendRequestResponse.setStatus(Constants.SUCCESS);
			return ignoreFriendRequestResponse;
		}
		else if(Constants.ERROR.equalsIgnoreCase(status)){
			ignoreFriendRequestResponse.setStatus(Constants.ERROR);
			ignoreFriendRequestResponse.setMessage(jsonObject.getString("message"));
		}
		return ignoreFriendRequestResponse;
	}
	
	public static Status parseCreateLisnResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Status parseJoinLisnResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Status parseSendEmailForTemporaryPasswordResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Status parseDisableSocialNetworkSettingsResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Status parseUpdateUserProfileResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Status parseProfileUploadPictureResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	public static Profile parseProfileResponse(String json) throws JSONException{
		Profile profile = new Profile();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		profile.setFullname(message.getString("fullName"));
		profile.setEmail(message.getString("username"));
		profile.setIsImage(message.getString("isImage"));
	
		if(message.getString("dateOfBirth") == null || (message.getString("dateOfBirth").toString().equalsIgnoreCase(null)||message.getString("dateOfBirth").toString().equalsIgnoreCase(""))){
			profile.setDob(null);
		}else{
			profile.setDob(message.getString("dateOfBirth"));
		}
		return profile;
	}
	
	public static UserConnectivity parseUserConnectivityResponse(String json) throws JSONException{
		UserConnectivity userConnectivity = new UserConnectivity();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		userConnectivity.setFacebookConnectivity(message.getString("facebook"));
		userConnectivity.setLinkedInConnectivity(message.getString("linkedIn"));
	
		return userConnectivity;
	}
	
	public static NotificationCount parseNotificationCountsResponse(String json) throws JSONException{
		NotificationCount notificationCount = new NotificationCount();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		int count = 0;
		if(message.getString("connectionNotification")!=null){
			try{
				count = Integer.parseInt(message.getString("connectionNotification"));
			}catch(Exception e){}
		}
		notificationCount.setFriendRequestCount(count);
		
		if(message.getString("peopleNearByNotification")!=null){
			try{
				count = Integer.parseInt(message.getString("peopleNearByNotification"));
			}catch(Exception e){}
		}
		notificationCount.setPeopleNearByCount(count);
		return notificationCount;
	}
	
	public static OtherProfile parseOtherProfileResponse(String json) throws JSONException{
		OtherProfile otherProfile = new OtherProfile();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		otherProfile.setFullname(message.getString("fullName"));
		otherProfile.setIsImage(message.getString("isImage"));
		
		if(message.has("id")){
			otherProfile.setId(message.getString("id"));
		}
		if(message.has("isLisnxUser")){
			otherProfile.setLisnxUser(message.getBoolean("isLisnxUser"));
		}
		if(message.has("wasInvited")){
			otherProfile.setInvited(message.getBoolean("wasInvited"));
		}
		if(message.has("username")){
			otherProfile.setEmail(message.getString("username"));
		}
		
		try{
			if(message.has("isDecline")){
				otherProfile.setIsDecline(message.getString("isDecline"));
			}
		}catch(Exception e){}
		
		if(message.has("viewConnection"))
			otherProfile.setViewConnection(message.getString("viewConnection"));
		
		if(message.has("profileShareType")){
			otherProfile.setProfileShareType(message.getString("profileShareType"));
			
			if(otherProfile.getProfileShareType().equalsIgnoreCase("All")||otherProfile.getProfileShareType().equalsIgnoreCase("Casual")){
				otherProfile.setFacebookId(message.getString("facebook"));
			}
			if(otherProfile.getProfileShareType().equalsIgnoreCase("All")||otherProfile.getProfileShareType().equalsIgnoreCase("Professional")){
				otherProfile.setLinkedinId(message.getString("linkedin"));
			}
		} else{
			if(message.has("facebook")){
				otherProfile.setFacebookId(message.getString("facebook"));
			}
			if(message.has("linkedin")){
				otherProfile.setLinkedinId(message.getString("linkedin"));
			}
		}
		
		if(message.has("commonFriendsCount")){
			otherProfile.setCommonFriendsCount(message.getString("commonFriendsCount"));
		}
		
		if(message.has("commonLisnsCount")){
			otherProfile.setCommonLisnsCount(message.getString("commonLisnsCount"));
		}
		
		if(message.has("connectionStatus")){
			otherProfile.setConnectionStatus(message.getString("connectionStatus"));
		}
		
		if(message.has("isNearBy")){
			otherProfile.setIsNearBy(message.getString("isNearBy"));
		}
		
		if(message.has("dateOfBirth")){
			if(message.getString("dateOfBirth").toString().equalsIgnoreCase(null)||message.getString("dateOfBirth").toString().equalsIgnoreCase("")){
				otherProfile.setDob(null);
			}else{
				otherProfile.setDob(message.getString("dateOfBirth"));
			}
		}
		if(message.has("connectedOnFacebook")){
			otherProfile.setConnectedOnFacebook(message.getBoolean("connectedOnFacebook"));
		}
		
		if(message.has("connectedOnLinkedin")){
			otherProfile.setConnectedOnLinkedin(message.getBoolean("connectedOnLinkedin"));
		}
		if(message.has("connectedOnLisnx")){
			otherProfile.setConnectedOnLisnx(message.getBoolean("connectedOnLisnx"));
		}
		
		
		return otherProfile;
	}
	
	public static LisnDetail parseLisnDetailResponse(String json) throws JSONException{
		LisnDetail lisnDetail = new LisnDetail();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		
		try {
			value = message.getString("description");
			lisnDetail.setDescription(value);
		} catch (Exception e) {
			lisnDetail.setDescription("");
		}
		try {
			value = message.getString("name");
			lisnDetail.setName(value);
		} catch (Exception e) {
			lisnDetail.setName("");
		}
		try {
			value = message.getString("startDate");
			lisnDetail.setStartDate(value);
		} catch (Exception e) {
			lisnDetail.setStartDate("");
		}
		try {
			value = message.getString("endDate");
			lisnDetail.setEndDate(value);
		} catch (Exception e) {
			lisnDetail.setEndDate("");
		}
		try {
			value = message.getString("member");
			lisnDetail.setMembers(value);
		} catch (Exception e) {
			lisnDetail.setMembers("");
		}
		try {
			value = message.getString("messageCount");
			lisnDetail.setMessageCount(value);
		} catch (Exception e) {
			lisnDetail.setMessageCount("");
		}
		
		JSONObject joins = new JSONObject(message.getString("joins"));
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = joins.keys();
		
		ArrayList<HashMap<String, String>> lisnersDataList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> lisnersData = null;
		JSONObject innerMessage = null;
		while(iterator.hasNext()){
			lisnersData = new HashMap<String, String>();
			String key = (String)iterator.next();
			innerMessage = new JSONObject(joins.getString(key));
			
			try {
				value = innerMessage.getString("id");
				lisnersData.put("id", value);
			} catch (Exception e) {
				lisnersData.put("id", "");
			}
			try {
				value = innerMessage.getString("fullName");
				lisnersData.put("fullName", value);
			} catch (Exception e) {
				lisnersData.put("fullName", "");
			}
			try {
				value = innerMessage.getString("connectionStatus");
				lisnersData.put("connectionStatus", value);
			} catch (Exception e) {
				lisnersData.put("connectionStatus", "");
			}
			try {
				value = innerMessage.getString("isImage");
				lisnersData.put("isImage", value);
			} catch (Exception e) {
				lisnersData.put("isImage", "");
			}
			
			lisnersDataList.add(lisnersData);
		}
		lisnDetail.setLisnersName(lisnersDataList);
		return lisnDetail;
	}
	
	public static MessageDetail parseMessageDetailResponse(String json) throws JSONException{
		MessageDetail messageDetail = new MessageDetail();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		
		try {
			value = message.getString("newMessagesCount");
			messageDetail.setNewMessageCount(value);
		} catch (Exception e) {
			messageDetail.setNewMessageCount("");
		}
		try {
			value = message.getString("oldMessagesCount");
			messageDetail.setOldMessageCount(value);
		} catch (Exception e) {
			messageDetail.setOldMessageCount("");
		}
		
		JSONObject oldMessages = new JSONObject(message.getString("oldMessages"));
		@SuppressWarnings("unchecked")
		Iterator<String> unsortedIterator = oldMessages.keys();
		
		List<Integer> list = new ArrayList<Integer>();
	      while (unsortedIterator.hasNext()) {
	    	  list.add(Integer.parseInt(unsortedIterator.next()));
	      }
	      Collections.sort(list);
	      Iterator<Integer> iterator=list.iterator(); 
	      int totalMessageCount = list.size();
		
		ArrayList<HashMap<String, String>> messageDataList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> messageData = null;
		JSONObject innerMessage = null;
		int count = 0;
		
		while(iterator.hasNext()){
			messageData = new HashMap<String, String>();
			String key = Integer.toString(iterator.next());
			innerMessage = new JSONObject(oldMessages.getString(key));
			
			try {
				value = innerMessage.getString("id");
				messageData.put("messageId", value);
				
			} catch (Exception e) {
				messageData.put("messageId", "");
			}
			try {
				value = innerMessage.getString("dateCreated");
				messageData.put("createdDate", value);
			} catch (Exception e) {
				messageData.put("createdDate", "");
			}
			try {
				value = innerMessage.getString("content");
				messageData.put("content", value);
			} catch (Exception e) {
				messageData.put("content", "");
			}
			try {
				value = innerMessage.getString("fullName");
				messageData.put("fullName", value);
			} catch (Exception e) {
				messageData.put("fullName", "");
			}
			try {
				value = innerMessage.getString("user");
				messageData.put("userId", value);
			} catch (Exception e) {
				messageData.put("userId", "");
			}
			try {
				value = innerMessage.getString("isImage");
				messageData.put("isImage", value);
			} catch (Exception e) {
				messageData.put("isImage", "");
			}
			try {
				value = innerMessage.getString("isNew");
				messageData.put("isNew", value);
			} catch (Exception e) {
				messageData.put("isNew", "");
			}
			
			if(totalMessageCount != 0){
				count = count + 1;
				if(count == totalMessageCount){
					try {
						value = innerMessage.getString("id");
						messageDetail.setLastMessageId(value);
					} catch (Exception e) {
						messageDetail.setLastMessageId("0");
					}
				}
			} else{
				try{
					messageDetail.setLastMessageId(Integer.toString(0));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			messageDataList.add(messageData);
		}
		
		JSONObject newMessages = new JSONObject(message.getString("newMessages"));
		@SuppressWarnings("unchecked")
		Iterator<String> unsortedNewIterator = newMessages.keys();
		
		List<Integer> newList = new ArrayList<Integer>();
	      while (unsortedNewIterator.hasNext()) {
	    	  newList.add(Integer.parseInt(unsortedNewIterator.next()));
	      }
	      Collections.sort(newList);
	      Iterator<Integer> newIterator=newList.iterator(); 
		
		ArrayList<HashMap<String, String>> newMessageDataList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> newMessageData = null;
		JSONObject newInnerMessage = null;
		
		while(newIterator.hasNext()){
			newMessageData = new HashMap<String, String>();
			String key = Integer.toString(newIterator.next());
			newInnerMessage = new JSONObject(newMessages.getString(key));
			
			try {
				value = newInnerMessage.getString("id");
				newMessageData.put("messageId", value);
				
			} catch (Exception e) {
				newMessageData.put("messageId", "");
			}
			try {
				value = newInnerMessage.getString("dateCreated");
				newMessageData.put("createdDate", value);
			} catch (Exception e) {
				newMessageData.put("createdDate", "");
			}
			try {
				value = newInnerMessage.getString("content");
				newMessageData.put("content", value);
			} catch (Exception e) {
				newMessageData.put("content", "");
			}
			try {
				value = newInnerMessage.getString("fullName");
				newMessageData.put("fullName", value);
			} catch (Exception e) {
				newMessageData.put("fullName", "");
			}
			try {
				value = newInnerMessage.getString("user");
				newMessageData.put("userId", value);
			} catch (Exception e) {
				newMessageData.put("userId", "");
			}
			try {
				value = newInnerMessage.getString("isImage");
				newMessageData.put("isImage", value);
			} catch (Exception e) {
				newMessageData.put("isImage", "");
			}
			try {
				value = newInnerMessage.getString("isNew");
				newMessageData.put("isNew", value);
			} catch (Exception e) {
				newMessageData.put("isNew", "");
			}
			
			newMessageDataList.add(newMessageData);
		}
		
		messageDetail.setOldMessageName(messageDataList);
		messageDetail.setNewMessageName(newMessageDataList);
		return messageDetail;
	}
	
	@SuppressWarnings("unchecked")
	public static List<PrivateMessage> parsePrivateMessageResponse(String json) throws JSONException{
		List<PrivateMessage> privateMessage = new ArrayList<PrivateMessage>();
		
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		PrivateMessage msg = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject msgData = null;
		
		Iterator<String> unsortedIterator = message.keys();
		List<Integer> list = new ArrayList<Integer>();
	      while (unsortedIterator.hasNext()) {
	    	  list.add(Integer.parseInt(unsortedIterator.next()));
	      }
	      Collections.sort(list);
	      Iterator<Integer> iterator=list.iterator(); 

		while(iterator.hasNext()){
			try {
				msg = new PrivateMessage();
				String key = Integer.toString(iterator.next());
				msg.setMsgID(key);
				msgData = new JSONObject(message.getString(key));
				
				try {
					value = msgData.getString("receiver");
					msg.setReceiver(value);
				} catch (Exception e) {
					msg.setReceiver("");
				}
				
				try {
					value = msgData.getString("content");
					msg.setContent(value);
				} catch (Exception e) {
					msg.setContent("");
				}
				
				try {
					value = msgData.getString("dateCreated");
					msg.setDateCreated(value);
				} catch (Exception e) {
					msg.setDateCreated("");
				}
				
				try {
					value = msgData.getString("sender");
					msg.setSender(value);
				} catch (Exception e) {
					msg.setSender("");
				}
				
				privateMessage.add(msg);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return privateMessage;
	}
	
	public static MessageDetail parseMessagePostDetailResponse(String json) throws JSONException{
		MessageDetail messageDetail = new MessageDetail();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = message.keys();
		
		ArrayList<HashMap<String, String>> messageDataList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> messageData = null;
		JSONObject innerMessage = null;
		while(iterator.hasNext()){
			messageData = new HashMap<String, String>();
			String key = (String)iterator.next();
			innerMessage = new JSONObject(message.getString(key));
			
			try {
				value = innerMessage.getString("id");
				messageData.put("messageId", value);
			} catch (Exception e) {
				messageData.put("messageId", "");
			}
			try {
				value = innerMessage.getString("dateCreated");
				messageData.put("createdDate", value);
			} catch (Exception e) {
				messageData.put("createdDate", "");
			}
			try {
				value = innerMessage.getString("content");
				messageData.put("content", value);
			} catch (Exception e) {
				messageData.put("content", "");
			}
			try {
				value = innerMessage.getString("user");
				messageData.put("userId", value);
			} catch (Exception e) {
				messageData.put("userId", "");
			}
			try {
				value = innerMessage.getString("lastViewedMessageId");
				messageDetail.setLastViewedMessageId(value);
			} catch (Exception e) {
				messageDetail.setLastViewedMessageId("");
			}
			
			messageDataList.add(messageData);
		}
		messageDetail.setOldMessageName(messageDataList);
		return messageDetail;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Lisn> parseLisnResponse(String json) throws JSONException{
		List<Lisn> lisnList = new ArrayList<Lisn>();
		
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		String countLisns = jsonObject.getString("countLisns");
		String countJoinedLisns = jsonObject.getString("countJoinedLisns");
		Lisn lisn = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject lisnData = null;
		
		Iterator<String> unsortedIterator = message.keys();
		List<Integer> list = new ArrayList<Integer>();
	      while (unsortedIterator.hasNext()) {
	    	  list.add(Integer.parseInt(unsortedIterator.next()));
	      }
	      Collections.sort(list);
	      Collections.reverse(list);
	      Iterator<Integer> iterator=list.iterator(); 
		
		while(iterator.hasNext()){
			try {
				lisn = new Lisn();
				String key = Integer.toString(iterator.next());
				lisn.setLisnID(key);
				lisnData = new JSONObject(message.getString(key));
				try {
					value = lisnData.getString("description");
					lisn.setDescription(value);
				} catch (Exception e) {
					lisn.setDescription("");
				}
				try {
					value = lisnData.getString("endDate");
					lisn.setEndDate(value);
				} catch (Exception e) {
					lisn.setEndDate("");
				}
				try {
					value = lisnData.getString("Friends");
					lisn.setFriend(value);
				} catch (Exception e) {
					lisn.setFriend("");
				}
				try {
					value = lisnData.getString("member");
					lisn.setMember(value);
				} catch (Exception e) {
					lisn.setMember("");
				}
				try {
					value = lisnData.getString("name");
					lisn.setName(value);
				} catch (Exception e) {
					lisn.setName("");
				}
				try {
					value = lisnData.getString("RSVP");
					lisn.setRsvp(value);
				} catch (Exception e) {
					lisn.setRsvp("");
				}
				try {
					value = lisnData.getString("startDate");
					lisn.setStartDate(value);
				} catch (Exception e) {
					lisn.setStartDate("");
				}
				try {
					value = lisnData.getString("venue");
					lisn.setVenue(value);
				} catch (Exception e) {
					lisn.setVenue("");
				}
				try {
					value = lisnData.getString("totalMessage");
					lisn.setTotalMessage(value);
				} catch (Exception e) {
					lisn.setTotalMessage("");
				}

				lisn.setCountLisns(countLisns);
				lisn.setCountJoinedLisns(countJoinedLisns);
				lisnList.add(lisn);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return lisnList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<CommonLisn> parseCommonLisnResponse(String json) throws JSONException{
		List<CommonLisn> lisnList = new ArrayList<CommonLisn>();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		CommonLisn lisn = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject lisnData = null;
		
		Iterator<String> unsortedIterator = message.keys();
		List<Integer> list = new ArrayList<Integer>();
	      while (unsortedIterator.hasNext()) {
	    	  list.add(Integer.parseInt(unsortedIterator.next()));
	      }
	      Collections.sort(list);
	      Collections.reverse(list);
	      Iterator<Integer> iterator=list.iterator(); 
		
		while(iterator.hasNext()){
			try {
				lisn = new CommonLisn();
				String key = Integer.toString(iterator.next());
				lisn.setLisnID(key);
				lisnData = new JSONObject(message.getString(key));
				
				try {
					value = lisnData.getString("name");
					lisn.setName(value);
				} catch (Exception e) {
					lisn.setName("");
				}
				try {
					value = lisnData.getString("member");
					lisn.setMember(value);
				} catch (Exception e) {
					lisn.setMember("");
				}
				try {
					value = lisnData.getString("totalMessage");
					lisn.setTotalMessage(value);
				} catch (Exception e) {
					lisn.setTotalMessage("");
				}
				try {
					value = lisnData.getString("description");
					lisn.setDescription(value);
				} catch (Exception e) {
					lisn.setDescription("");
				}
				lisnList.add(lisn);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return lisnList;
	}
	
	public static Status parseExternalConnectionRequestResponse(String json) throws JSONException{
		Status status = new Status();
		JSONObject jsonObject = new JSONObject(json);
		status.setMessage(jsonObject.getString("message"));
		status.setStatus(jsonObject.getString("status"));
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public static List<PersonNearBy> parsePeopleNearByResponse(String json) throws JSONException{
		List<PersonNearBy> personNearByList = new ArrayList<PersonNearBy>();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		PersonNearBy personNearBy = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject personNearByData = null;
		Iterator<String> iterator = message.keys();
		while(iterator.hasNext()){
			try {
				personNearBy = new PersonNearBy();
				String key = (String)iterator.next();
				personNearBy.setPersonID(key);
				personNearByData = new JSONObject(message.getString(key));
				try {
					value = personNearByData.getString("fullName");
					personNearBy.setPersonNearByName(value);
				} catch (Exception e) {
					personNearBy.setPersonNearByName("");
				}
				try {
					value = personNearByData.getString("isImage");
					personNearBy.setIsImage(value);
				} catch (Exception e) {
					personNearBy.setIsImage("");
				}
				try {
					value = personNearByData.getString("connectionStatus");
					personNearBy.setConnectionStatus(value);
				} catch (Exception e) {
					personNearBy.setConnectionStatus("");
				}
				
				personNearByList.add(personNearBy);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return personNearByList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Notification> parseNotificationsResponse(String json) throws JSONException{
		List<Notification> notificationsList = new ArrayList<Notification>();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		Notification notification = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject friendRequests = new JSONObject(message.getString("friendRequestList"));
		
		JSONObject notificationsData = null;
		Iterator<String> iterator = friendRequests.keys();
		while(iterator.hasNext()){
			try {
				notification = new Notification();
				String key = (String)iterator.next();
				notification.setNotificationID(key);
				notificationsData = new JSONObject(friendRequests.getString(key));
				try {
					value = notificationsData.getString("fullName");
					notification.setNotificationName(value);
				} catch (Exception e) {
					notification.setNotificationName("");
				}
				try {
					value = notificationsData.getString("isImage");
					notification.setIsImage(value);
				} catch (Exception e) {
					notification.setIsImage("");
				}
				try {
					value = Constants.MESSAGE_TYPE_FRIEND_REQUEST;
					notification.setMsgType(value);
				} catch (Exception e) {
					notification.setMsgType("");
				}
				try {
					Boolean isNotified = notificationsData.getBoolean("isNotified");
					notification.setNotified(isNotified);
				} catch (Exception e) {
					notification.setMsgType("");
				}
				
				notificationsList.add(notification);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		JSONObject acceptedRequests = new JSONObject(message.getString("yourAcceptedFriendRequestList"));
		Iterator<String> acceptedRequestsIterator = acceptedRequests.keys();
		while(acceptedRequestsIterator.hasNext()){
			try {
				notification = new Notification();
				String key = (String)acceptedRequestsIterator.next();
				notification.setNotificationID(key);
				notificationsData = new JSONObject(acceptedRequests.getString(key));
				try {
					value = notificationsData.getString("fullName");
					notification.setNotificationName(value);
				} catch (Exception e) {
					notification.setNotificationName("");
				}
				try {
					value = notificationsData.getString("isImage");
					notification.setIsImage(value);
				} catch (Exception e) {
					notification.setIsImage("");
				}
				try {
					value = Constants.MESSAGE_TYPE_ACCEPTED_REQUEST;
					notification.setMsgType(value);
				} catch (Exception e) {
					notification.setMsgType("");
				}
				try {
					Boolean isNotified = notificationsData.getBoolean("isNotified");
					notification.setNotified(isNotified);
				} catch (Exception e) {
					notification.setMsgType("");
				}
				
				notificationsList.add(notification);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return notificationsList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<MessageNotifications> parseMessageNotificationsResponse(String json) throws JSONException{
		List<MessageNotifications> messageNotificationsList = new ArrayList<MessageNotifications>();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		MessageNotifications messageNotifications = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject dbmessage = new JSONObject(message.getString("dashBoardMessages"));
		JSONObject messageNotificationsData = null;
		Iterator<String> iterator = dbmessage.keys();
		while(iterator.hasNext()){
			try {
				messageNotifications = new MessageNotifications();
				String key = (String)iterator.next();
				messageNotifications.setMessageNotificationID(key);
				messageNotificationsData = new JSONObject(dbmessage.getString(key));
				try {
					value = messageNotificationsData.getString("count");
					messageNotifications.setMessageCount(value);
				} catch (Exception e) {
					messageNotifications.setMessageCount("");
				}
				try {
					value = messageNotificationsData.getString("lisnName");
					messageNotifications.setLisnName(value);
				} catch (Exception e) {
					messageNotifications.setLisnName("");
				}
				try {
					value = Constants.MESSAGE_TYPE_LISNS;
					messageNotifications.setMsgType(value);
				} catch (Exception e) {
					messageNotifications.setMsgType("");
				}
				
				messageNotificationsList.add(messageNotifications);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		JSONObject pmmessage = new JSONObject(message.getString("newPrivateMessages"));
		Iterator<String> iterator2 = pmmessage.keys();
		while(iterator2.hasNext()){
			try {
				messageNotifications = new MessageNotifications();
				String key = (String)iterator2.next();
				messageNotifications.setMessageNotificationID(key);
				
				messageNotificationsData = new JSONObject(pmmessage.getString(key));
				try {
					value = messageNotificationsData.getString("sender");
					messageNotifications.setSender(value);
				} catch (Exception e) {
					messageNotifications.setSender("");
				}
				try {
					value = messageNotificationsData.getString("senderName");
					messageNotifications.setSenderName(value);
				} catch (Exception e) {
					messageNotifications.setSenderName("");
				}
				try {
					value = messageNotificationsData.getString("privateMessageCount");
					messageNotifications.setPrivateMessageCount(value);
				} catch (Exception e) {
					messageNotifications.setPrivateMessageCount("");
				}
				try {
					value = messageNotificationsData.getString("isImage");
					messageNotifications.setIsImage(value);
				} catch (Exception e) {
					messageNotifications.setIsImage("");
				}
				try {
					value = messageNotificationsData.getString("latestMessageContent");
					messageNotifications.setMessage(value);
				} catch (Exception e) {
					messageNotifications.setMessage("");
				}
				try {
					value = messageNotificationsData.getString("date_created");
					messageNotifications.setMessageDate(value);
				} catch (Exception e) {
					messageNotifications.setMessageDate("");
				}
				try {
					value = Constants.MESSAGE_TYPE_CHAT;
					messageNotifications.setMsgType(value);
				} catch (Exception e) {
					messageNotifications.setMsgType("");
				}
				
				messageNotificationsList.add(messageNotifications);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return messageNotificationsList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Friend> parseFriendsResponse(String json) throws JSONException{
		List<Friend> friendsList = new ArrayList<Friend>();
		JSONObject jsonObject = new JSONObject(json);
		if(!Constants.SUCCESS.equalsIgnoreCase(jsonObject.getString("status"))){
			throw new JSONException(null);
		}
		
		Friend friend = null;
		String value = "";
		JSONObject message = new JSONObject(jsonObject.getString("message"));
		JSONObject friendsData = null;
		Iterator<String> iterator = message.keys();
		while(iterator.hasNext()){
			try {
				friend = new Friend();
				String key = (String)iterator.next();
				friend.setFriendID(key);
				friendsData = new JSONObject(message.getString(key));
				try {
					value = friendsData.getString("fullName");
					friend.setFriendName(value);
				} catch (Exception e) {
					friend.setFriendName("");
				}
				try {
					value = friendsData.getString("isImage");
					friend.setIsImage(value);
				} catch (Exception e) {
					friend.setIsImage("");
				}
				friend.setConnectedOnLinkedin(getBoolean(friendsData, "connectedOnLinkedin"));
				friend.setConnectedOnLisnx(getBoolean(friendsData, "connectedOnLisnx"));
				friend.setConnectedOnFacebook(getBoolean(friendsData, "connectedOnFacebook"));
				friend.setInvited(getBoolean(friendsData, "wasInvited"));
				
				friendsList.add(friend);
			} catch (Exception e) {
				throw new JSONException(null);
			}
		}
		
		return friendsList;
	}

	private static boolean getBoolean(JSONObject friendsData, String paramName) {
		boolean connected = false;
		try{
			connected = friendsData.getBoolean(paramName);
		}catch(Exception e){
			
		}
		return connected;
	}
}