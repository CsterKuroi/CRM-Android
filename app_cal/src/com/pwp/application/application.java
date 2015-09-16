package com.pwp.application;

public class application {
   private String userID=null;
   private String Users[];
   public application(String userID){
	   this.userID=userID;
   }
   
   public application(String[] users){
	   this.Users= users; 
   }
   
   public application() {
	   
   }
   public void setuserid(String userid) {
	   this.userID=userID;
   }
   public String getuserid() {
	   return userID;
   }
   public void setusers(String[] users) {
	   this.Users=users;
   }
   public String[] getusers() {
	   return Users;
   }
}
