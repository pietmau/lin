package com.lisnx.model;

public class Notification {
	private String notificationName;
	private String isImage;
	private String notificationID;
	private String msgType;
	private boolean isNotified;
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getIsImage() {
		return isImage;
	}
	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
	public String getNotificationName() {
		return notificationName;
	}
	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}
	public String getNotificationID() {
		return notificationID;
	}
	public void setNotificationID(String notificationID) {
		this.notificationID = notificationID;
	}
	public boolean isNotified() {
		return isNotified;
	}
	public void setNotified(boolean isNotified) {
		this.isNotified = isNotified;
	}
}