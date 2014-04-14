package com.lisnx.model;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageDetail { 
	
	public ArrayList<HashMap<String, String>> oldMessageName;
	public ArrayList<HashMap<String, String>> newMessageName;
	public String newMessageCount;
	public String oldMessageCount;
	public String lastMessageId ; 
	public String lastViewedMessageId;

	public String getLastViewedMessageId() {
		return lastViewedMessageId;
	}

	public void setLastViewedMessageId(String lastViewedMessageId) {
		this.lastViewedMessageId = lastViewedMessageId;
	}

	public String getLastMessageId() {
		return lastMessageId;
	}

	public void setLastMessageId(String lastMessageId) {
		this.lastMessageId = lastMessageId;
	}

	public String getNewMessageCount() {
		return newMessageCount;
	}

	public void setNewMessageCount(String newMessageCount) {
		this.newMessageCount = newMessageCount;
	}

	public String getOldMessageCount() {
		return oldMessageCount;
	}

	public void setOldMessageCount(String oldMessageCount) {
		this.oldMessageCount = oldMessageCount;
	}
	
	public ArrayList<HashMap<String, String>> getNewMessageName() {
		return newMessageName;
	}

	public void setNewMessageName(ArrayList<HashMap<String, String>> newMessageName) {
		this.newMessageName = newMessageName;
	}

	public ArrayList<HashMap<String, String>> getOldMessageName() {
		return oldMessageName;
	}

	public void setOldMessageName(ArrayList<HashMap<String, String>> oldMessageName) {
		this.oldMessageName = oldMessageName;
	}
}