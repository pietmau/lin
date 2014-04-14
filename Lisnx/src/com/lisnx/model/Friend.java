package com.lisnx.model;


public class Friend {
	
	private String friendName;
	private String isImage;
	private String friendID;
	private boolean connectedOnLisnx;
	
	private boolean connectedOnLinkedin;
	private boolean connectedOnFacebook;
	
	private boolean invited;
	
	public boolean isConnectedOnLinkedin() {
		return connectedOnLinkedin;
	}
	public void setConnectedOnLinkedin(boolean connectedOnLinkedin) {
		this.connectedOnLinkedin = connectedOnLinkedin;
	}
	public boolean isConnectedOnFacebook() {
		return connectedOnFacebook;
	}
	public void setConnectedOnFacebook(boolean connectedOnFacebook) {
		this.connectedOnFacebook = connectedOnFacebook;
	}
	
	public String getIsImage() {
		return isImage;
	}
	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
	public String getFriendName() {
		return friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	public String getFriendID() {
		return friendID;
	}
	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}
	public boolean isConnectedOnLisnx() {
		return connectedOnLisnx;
	}
	public void setConnectedOnLisnx(boolean connectedOnLisnx) {
		this.connectedOnLisnx = connectedOnLisnx;
	}
	public boolean wasInvited() {
		return invited;
	}
	public void setInvited(boolean invited) {
		this.invited = invited;
	}
	
}