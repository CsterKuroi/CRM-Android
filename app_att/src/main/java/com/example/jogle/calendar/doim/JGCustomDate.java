package com.example.jogle.calendar.doim;

import java.io.Serializable;
import java.util.Calendar;

import com.example.jogle.calendar.util.JGDateUtil;

public class JGCustomDate implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	public int year;
	public int month;
	public int day;
	public int week;
	
	public JGCustomDate(int year, int month, int day){
		if(month > 12){
			month = 1;
			year++;
		}else if(month <1){
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public JGCustomDate(){
		this.year = JGDateUtil.getYear();
		this.month = JGDateUtil.getMonth();
		this.day = JGDateUtil.getCurrentMonthDay();
	}
	
	public static JGCustomDate modifiDayForObject(JGCustomDate date,int day){
		JGCustomDate modifiDate = new JGCustomDate(date.year,date.month,day);
		return modifiDate;
	}
	@Override
	public String toString() {
		return year+"-"+month+"-"+day;
	}

	public Calendar toCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		return calendar;
	}
}
