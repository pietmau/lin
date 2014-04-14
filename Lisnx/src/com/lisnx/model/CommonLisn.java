package com.lisnx.model;

public class CommonLisn {

	private String lisnID;
	private String name;
	private String member;
	private String totalMessage;	
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTotalMessage() {
		return totalMessage;
	}
	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}
	public String getLisnID() {
		return lisnID;
	}
	public void setLisnID(String lisnID) {
		this.lisnID = lisnID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
}
