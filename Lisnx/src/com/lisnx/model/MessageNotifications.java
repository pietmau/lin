package com.lisnx.model;

public class MessageNotifications {
	private String messageCount;
	private String lisnName;
	private String messageNotificationID;
	private String sender;
	private String message;
	private String msgType;
	private String isImage;
	private String senderName;
	private String privateMessageCount;
	private String messageDate;
	
	public String getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getPrivateMessageCount() {
		return privateMessageCount;
	}
	public void setPrivateMessageCount(String privateMessageCount) {
		this.privateMessageCount = privateMessageCount;
	}
	public String getIsImage() {
		return isImage;
	}
	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(String messageCount) {
		this.messageCount = messageCount;
	}	
	public String getLisnName() {
		return lisnName;
	}
	public void setLisnName(String lisnName) {
		this.lisnName = lisnName;
	}
	public String getMessageNotificationID() {
		return messageNotificationID;
	}
	public void setMessageNotificationID(String messageNotificationID) {
		this.messageNotificationID = messageNotificationID;
	}
}