package com.pwp.myweather;


public class Weather {
	
	public static String city;
	public static String current_date_time;
	public static String current_dayPictureUrl;
	public static String current_nightPictureUrl;
	public static String current_weather;
	public static String current_wind;
	public static String current_temp;
	public static String current_temprature;

	
	public static Forecast[] day = new Forecast[3];
	
	static{
		for(int i = 0;i<day.length;i++){
			day[i] = new Forecast();
		}
	}
	
	public static String getSmsMsg(){
		String msg = "";
		msg += city+",";
		msg += current_date_time + ", " + current_temp + ".";
		msg += day[0].day_of_week + ", " + day[0].weather + ", " 
				+ day[0].temperature + "/" + day[0].wind;
		return msg;
	}

}
