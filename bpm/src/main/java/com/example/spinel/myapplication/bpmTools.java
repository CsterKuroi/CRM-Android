package com.example.spinel.myapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Spinel on 2015/7/23.
 */
public class bpmTools {
    public static String intToZH(int i)
    {
        String[] zh = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};

        String str = "";
        StringBuffer sb = new StringBuffer(String.valueOf(i));
        sb = sb.reverse();
        int r = 0;
        int l = 0;
        for (int j = 0; j < sb.length(); j++)
        {
            /**
             * 当前数字
             */
            r = Integer.valueOf(sb.substring(j, j+1));

            if (j != 0)
            /**
             * 上一个数字
             */
                l = Integer.valueOf(sb.substring(j-1, j));

            if (j == 0)
            {
                if (r != 0 || sb.length() == 1)
                    str = zh[r];
                continue;
            }

            if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 9)
            {
                if (r != 0)
                    str = zh[r] + unit[j] + str;
                else if (l != 0)
                    str = zh[r] + str;
                continue;
            }

            if (j == 4 || j == 8)
            {
                str =  unit[j] + str;
                if ((l != 0 && r == 0) || r != 0)
                    str = zh[r] + str;
                continue;
            }
        }
        return str;
    }

    public static String Int2String(int num){
        return ((Integer)num).toString();
    }

    public static int String2Int(String str){
        return Integer.parseInt(str);
    }

    public static String Double2String(double num) { return ((Double)num).toString();}

    public static JSONObject getJSONObject(String id, String value){
        JSONObject object = new JSONObject();
        try {
            object.put("id", id).put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }



    public static String converntListToString(ArrayList<String> list){
        String str = "";
        boolean exists = false;

        for(int i=0; i<list.size(); i++){
            if(exists)
                str += ", ";
            else
                exists = true;

            str += list.get(i);
        }

        return str;
    }

    public static String getUserIdListString(ArrayList<Integer> list){
        ArrayList<String> idList = new ArrayList<>();

        for(int i=0; i<list.size(); i++){
            idList.add(bpmMainActivity.structure.userList.get(list.get(i)).id);
        }

        return converntListToString(idList);
    }

    public static String convertUserIdStrings2UserName(String[] list){

        ArrayList<String> nameList = new ArrayList<>();

        for(int i=0; i<list.length; i++){
            nameList.add(bpmMainActivity.structure.getUserName(list[i]));
        }

        return converntListToString(nameList);
    }

    public static String getUserNameListString(ArrayList<Integer> list){
        ArrayList<String> nameList = new ArrayList<>();

        for(int i=0; i<list.size(); i++){
            nameList.add(bpmMainActivity.structure.userList.get(list.get(i)).name);
        }

        return converntListToString(nameList);

    }

    //-------------------------------Calendar转换---------------------------------------

    /**     Calendar类型输出为字符串
     *
     * @param calendar
     * @param type 0: xxxx
     * @return
     */
    public static final int DATE_FILE=-1, DATE_POIONT_DATETIME=0, DATE_PERIOD_DAY=1, DATE_PERIOD_WEEK=2, DATE_PERIOD_MONTH=3, DATE_PERIOD_YEAR=4, DATE_PERIOD_SEASON=5;
    public static String Calendar2String(Calendar calendar, int type){

        if(calendar==null)
            return "";
        Date date =calendar.getTime();

        SimpleDateFormat df = new SimpleDateFormat("");
        switch (type){
            case DATE_FILE:
                df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                break;
            case DATE_POIONT_DATETIME:
                df=new SimpleDateFormat("y年 MMMM d日  HH:mm");
                break;
            case DATE_PERIOD_DAY:
                df = new SimpleDateFormat("y年 MMMM d日");
                break;
            case DATE_PERIOD_WEEK:
                String str="";

                //一周第一天
                df = new SimpleDateFormat("y年 MMMM 第W周 （M/d~");
                str += df.format(date);

                //最后一天
                date = getLastDay(calendar, "WEEK").getTime();
                df = new SimpleDateFormat("M/d）");
                str += df.format(date);

                return str;
            case DATE_PERIOD_MONTH:
                df = new SimpleDateFormat("y年 MMMM");
                break;
            case DATE_PERIOD_YEAR:
                df = new SimpleDateFormat("y年");
                break;
            case DATE_PERIOD_SEASON:
                df=new SimpleDateFormat("y年 "+getSeasonName(calendar));
                break;
        }
        return df.format(date);
    }

    public static String getSeasonName(Calendar c){
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String season = "";
        if (currentMonth >= 1 && currentMonth <= 3)
            season = "第一季度";
        else if (currentMonth >= 4 && currentMonth <= 6)
            season = "第二季度";
        else if (currentMonth >= 7 && currentMonth <= 9)
            season = "第三季度";
        else if (currentMonth >= 10 && currentMonth <= 12)
            season = "第四季度";
        return season;
    }


    public static Calendar Millisecond2Calendar(long milli){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        return calendar;
    }

    public static Calendar Millisecond2Calendar(String str){
        if(str==null)
            return null;

        try {

            long time = Math.round(Double.parseDouble(str) * 1000);
            return Millisecond2Calendar(time);

        }catch(NumberFormatException e){
            return null;
        }

    }

    public static String Calendar2Millisecond(Calendar c){
        DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
        df.setGroupingUsed(false);

        return df.format(((double)c.getTimeInMillis())/1000);
    }

    public static boolean isADay(Calendar calendar){
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)
                && now.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)
                && now.get(Calendar.YEAR)==calendar.get(Calendar.YEAR))
            return true;
        else
            return false;
    }

    public static boolean isADay(Calendar a, Calendar b){

        if(a.get(Calendar.DAY_OF_MONTH)==b.get(Calendar.DAY_OF_MONTH)
                && a.get(Calendar.MONTH)==b.get(Calendar.MONTH)
                && a.get(Calendar.YEAR)==b.get(Calendar.YEAR))
            return true;
        else
            return false;
    }

    public static boolean isAWeek(Calendar calendar){
        Calendar now = Calendar.getInstance();

        long l=now.getTimeInMillis()-calendar.getTimeInMillis();
        int days=new Long(l/(1000*60*60*24)).intValue();
        if(days<7)
            return true;
        else
            return false;
    }

    public static boolean isAMonth(Calendar calendar){
        Calendar now = Calendar.getInstance();

        long l=now.getTimeInMillis()-calendar.getTimeInMillis();
        int days=new Long(l/(1000*60*60*24)).intValue();
        if(days<30)
            return true;
        else
            return false;
    }

    public static double calculateDay(Calendar start, Calendar end){

        if(start.after(end))
            return 0;
        int a = start.get(Calendar.AM_PM);
        int b = end.get(Calendar.AM_PM);

        if(bpmTools.isADay(start, end))
            return a==b ? 0.5 : 1;


        double c = (a==0 && b==1) ? 2 : (a==b) ? 1.5 : 1;

        Calendar newStart = (Calendar)start.clone(), newEnd = (Calendar)end.clone();
        newStart.add(Calendar.DATE, 1);
        newStart.set(Calendar.HOUR_OF_DAY, 0); newStart.set(Calendar.MINUTE, 0); newStart.set(Calendar.SECOND, 0);
        newEnd.set(Calendar.HOUR_OF_DAY, 0); newEnd.set(Calendar.MINUTE, 0); newEnd.set(Calendar.SECOND, 0);



        long l=newEnd.getTimeInMillis()-newStart.getTimeInMillis();
        int days=new Long(l/(1000*60*60*24)).intValue();
        return days+c;
    }

    public static Calendar getLastDay(Calendar c, String type){

        Log.e("getLastDay", type);
        Calendar end = (Calendar)c.clone();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(type.equals("WEEK")){
            end.setFirstDayOfWeek(Calendar.MONDAY);
            end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            end.set(year, month, day, 23, 59, 59);
            end.set(Calendar.MILLISECOND, 999);
        }
        else if(type.equals("MONTH")){
            end.set(year, month, end.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            end.set(Calendar.MILLISECOND, 999);
        }
        else if(type.equals("SEASON")){
            int currentMonth = c.get(Calendar.MONTH) + 1;
            if (currentMonth >= 1 && currentMonth <= 3) {
                end.set(Calendar.MONTH, 2);
            }
            else if (currentMonth >= 4 && currentMonth <= 6) {
                end.set(Calendar.MONTH, 5);
            }
            else if (currentMonth >= 7 && currentMonth <= 9) {
                end.set(Calendar.MONTH, 8);
            }
            else if (currentMonth >= 10 && currentMonth <= 12) {
                end.set(Calendar.MONTH, 11);
            }

            end.set(year, end.get(Calendar.MONTH), end.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            end.set(Calendar.MILLISECOND, 999);
        }
        else if(type.equals("YEAR")){

            end.set(year, 11, 31, 23, 59, 59);
            c.set(Calendar.MILLISECOND, 999);
        }

        return end;
    }


}

