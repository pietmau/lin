package com.lisnx.model;

public class OtherProfile {
	
	public enum NOTIFICATION_REQUEST_STATUS{
		NONE, REQUEST_SENT, REQUEST_ACCEPTED
	}

	private String id;
	private String fullname;
	private String dob;
	private String email;
	private String profileShareType;
	private String linkedinId;
	private String facebookId;
	private String isNearBy;
	private String connectionStatus;
	private String commonLisnsCount;
	private String commonFriendsCount;
	private String isImage;
	private String viewConnection;
	private String isDecline;
	private boolean connectedOnFacebook;
	private boolean connectedOnLinkedin;
	private boolean connectedOnLisnx;
	private boolean isLisnxUser = false;
	private boolean invited = false;
	private NOTIFICATION_REQUEST_STATUS notificationRequestStatus;
	
	public boolean wasInvited() {
		return invited;
	}
	public void setInvited(boolean invited) {
		this.invited = invited;
	}
	public boolean isConnectedOnLisnx() {
		return connectedOnLisnx;
	}
	public String getIsDecline() {
		return isDecline;
	}
	public void setIsDecline(String isDecline) {
		this.isDecline = isDecline;
	}
	public String getViewConnection() {
		return viewConnection;
	}
	public void setViewConnection(String viewConnection) {
		this.viewConnection = viewConnection;
	}
	public String getIsImage() {
		return isImage;
	}
	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
	public String getConnectionStatus() {
		return connectionStatus;
	}
	public void setConnectionStatus(String connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfileShareType() {
		return profileShareType;
	}
	public void setProfileShareType(String profileShareType) {
		this.profileShareType = profileShareType;
	}
	public String getLinkedinId() {
		return linkedinId;
	}
	public void setLinkedinId(String linkedinId) {
		this.linkedinId = linkedinId;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getIsNearBy() {
		return isNearBy;
	}
	public void setIsNearBy(String isNearBy) {
		this.isNearBy = isNearBy;
	}
	public String getCommonLisnsCount() {
		return commonLisnsCount;
	}
	public void setCommonLisnsCount(String commonLisnsCount) {
		this.commonLisnsCount = commonLisnsCount;
	}
	public String getCommonFriendsCount() {
		return commonFriendsCount;
	}
	public void setCommonFriendsCount(String commonFriendsCount) {
		this.commonFriendsCount = commonFriendsCount;
	}
	public boolean isConnectedOnFacebook() {
		return connectedOnFacebook;
	}
	public void setConnectedOnFacebook(boolean connectedOnFacebook) {
		this.connectedOnFacebook = connectedOnFacebook;
	}
	public boolean isConnectedOnLinkedin() {
		return connectedOnLinkedin;
	}
	public void setConnectedOnLinkedin(boolean connectedOnLinkedin) {
		this.connectedOnLinkedin = connectedOnLinkedin;
	}
	public void setConnectedOnLisnx(boolean connectedOnLisnx) {
		this.connectedOnLisnx = connectedOnLisnx;
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isLisnxUser() {
		return isLisnxUser;
	}
	public void setLisnxUser(boolean isLisnxUser) {
		this.isLisnxUser = isLisnxUser;
	}
	public NOTIFICATION_REQUEST_STATUS getNotificationRequestStatus() {
		return notificationRequestStatus;
	}
	public void setNotificationRequestStatus(NOTIFICATION_REQUEST_STATUS notificationRequestStatus) {
		this.notificationRequestStatus = notificationRequestStatus;
	}
}