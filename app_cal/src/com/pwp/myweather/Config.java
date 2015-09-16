package com.pwp.myweather;

public class Config {
	
	public static String CityName;
	public static String RefreshSpeed;
	public static String ProvideSmsService;
	public static String SaveSmsInfo;
	public static String KeyWord;
	
	public static void LoadDefaultConfig(){
		CityName="北京";
		RefreshSpeed="60";
		ProvideSmsService="true";
		SaveSmsInfo="true";
		KeyWord="NY";
	}
}
