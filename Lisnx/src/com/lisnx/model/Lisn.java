package com.lisnx.model;

public class Lisn {

	private String lisnID;
	private String name;
	private String description;
	private String startDate;
	private String endDate;
	private String venue;
	private String member;
	private String rsvp;
	private String friend;
	private String countLisns;
	private String countJoinedLisns;
	private String totalMessage;
	
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
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public String getRsvp() {
		return rsvp;
	}
	public void setRsvp(String rsvp) {
		this.rsvp = rsvp;
	}
	public String getFriend() {
		return friend;
	}
	public void setFriend(String friend) {
		this.friend = friend;
	}
	public String getCountLisns() {
		return countLisns;
	}
	public void setCountLisns(String countLisns) {
		this.countLisns = countLisns;
	}
	public String getCountJoinedLisns() {
		return countJoinedLisns;
	}
	public void setCountJoinedLisns(String countJoinedLisns) {
		this.countJoinedLisns = countJoinedLisns;
	}
}
