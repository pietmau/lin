package com.lisnx.model;

public class PersonNearBy {
	
	private String personNearByName;
	private String isImage;
	private String personID;
	private String connectionStatus;

	public String getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(String connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getPersonNearByName() {
		return personNearByName;
	}

	public void setPersonNearByName(String personNearByName) {
		this.personNearByName = personNearByName;
	}

	public String getIsImage() {
		return isImage;
	}

	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
}