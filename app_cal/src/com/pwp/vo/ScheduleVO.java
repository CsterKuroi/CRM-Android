package com.pwp.vo;

/**
 * 日程的VO类
 * @author song
 *
 */
public class ScheduleVO {


	//scheduleID integer, scheduleTypeID integer,remindID integer,participant text, issendmail integer,scheduleDate text,scheduleDate2 text,priority integer,scheduleContent text
	private int scheduleID;
	private String createrID;
	private int scheduleTypeID;
	private int remindID;
	private String participant;
	private int issendmail=0;
	private String scheduleDate;
	private String scheduleDate2;
	private int priority;
	private String scheduleContent;
	private String scheduleid2="0";
	private String aboutID;


	public ScheduleVO(){

	}
	public ScheduleVO(int scheduleID,String createrID,int scheduleTypeID,int remindID,String participant,
					  int issendmail,String scheduleDate,String scheduleDate2,int priority,String scheduleContent,String scheduleid2,String aboutID){
		this.scheduleID = scheduleID;
		this.createrID=createrID;
		this.scheduleTypeID = scheduleTypeID;
		this.remindID = remindID;
		this.participant=participant;
		this.issendmail=issendmail;
		this.scheduleDate = scheduleDate;
		this.scheduleDate2 = scheduleDate2;
		this.priority=priority;
		this.scheduleContent = scheduleContent;
		this.scheduleid2=scheduleid2;
		this.aboutID=aboutID;
		
	}
	public ScheduleVO(int scheduleID,int scheduleTypeID,int remindID,String scheduleContent,String scheduleDate){
		this.scheduleID = scheduleID;
		this.scheduleTypeID = scheduleTypeID;
		this.remindID = remindID;
		this.scheduleContent = scheduleContent;
		this.scheduleDate = scheduleDate;
	}

	public int getScheduleID() {
		return scheduleID;
	}
	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public String getcreaterID() {
		return createrID;
	}
	public void setcreaterID(String createrID) {
		this.createrID= createrID;
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

	public String getparticipant() {
		return participant;
	}
	public void setparticipant(String  participant) {
		this.participant = participant;
	}

	public int getissendmail() {
		return issendmail;
	}
	public void setissendmail(int issendmail) {
		this.issendmail = issendmail;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getScheduleDate2() {
		return scheduleDate2;
	}
	public void setScheduleDate2(String scheduleDate2) {
		this.scheduleDate2 = scheduleDate2;
	}

	public int getpriority() {
		return priority;
	}
	public void setpriority(int priority) {
		this.priority = priority;
	}

	public String getScheduleContent() {
		return scheduleContent;
	}
	public void setScheduleContent(String scheduleContent) {
		this.scheduleContent = scheduleContent;
	}
	public String getscheduleid2() {
		return scheduleid2;
	}
	public void setscheduleid2(String scheduleid2) {
		this.scheduleid2 = scheduleid2;
	}
	public String getaboutID() {
		return aboutID;
	}
	public void setaboutID(String aboutID) {
		this.aboutID = aboutID;
	}
}
