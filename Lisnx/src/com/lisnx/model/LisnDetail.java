package com.lisnx.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LisnDetail {

	public String name;
	public String description;
	public String startDate;
	public String endDate;
	public String members;
	public String messageCount;
	
	public ArrayList<HashMap<String, String>> lisnersName;
	
	public String getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(String messageCount) {
		this.messageCount = messageCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getMembers() {
		return members;
	}
	public void setMembers(String members) {
		this.members = members;
	}
	public ArrayList<HashMap<String, String>> getLisnersName() {
		return lisnersName;
	}
	public void setLisnersName(ArrayList<HashMap<String, String>> lisnersName) {
		this.lisnersName = lisnersName;
	}
}
