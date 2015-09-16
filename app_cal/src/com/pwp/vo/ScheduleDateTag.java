package com.pwp.vo;

/**
 * 需要标记的日程日期
 * @author song
 *
 */
public class ScheduleDateTag {

	private int tagID;
	private int scheduleID;
	private String userid;
	private int scheduleTypeID;
	private int remindID;
	private int month;
	private int year;
	private int day;
	public ScheduleDateTag() {

	}
	public ScheduleDateTag(int tagID, int scheduleID,String userid, int year, int month, int day) {
		this.tagID = tagID;
		this.scheduleID=scheduleID;
		this.userid=userid;
		this.month = month;
		this.year = year;
		this.day = day;
	}
	public ScheduleDateTag(int tagID, int scheduleID, int year, int month, int day) {

		this.tagID = tagID;
		this.scheduleID=scheduleID;
		//this.createrID=createrID;
		this.month = month;
		this.year = year;
		this.day = day;
	}


	public int getTagID() {
		return tagID;
	}
	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	public int getscheduleID() {
		return scheduleID;
	}
	public void setscheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public String getuserid() {
		return userid;
	}
	public void setuserid(String userid) {
		this.userid = userid;
	}

	public int getScheduleTypeID() {
		return scheduleTypeID;
	}
	public void setScheduleTypeID(int scheduleTypeID) {
		this.scheduleTypeID = scheduleTypeID;
	}

	public int getRemindID() {
		return remindID;
	}
	public void setRemindID(int remindID) {
		this.remindID = remindID;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}

}
