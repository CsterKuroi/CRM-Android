package com.pwp.remind_yanhao;

import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yan on 2015/8/8.
 */
public class yh729_DBUtil {
    public static void update(SQLiteDatabase db,String table,String[] items,String[] Values,String[] key,String[] id){
        String str1="";
        String str2="";
        str1=items[0]+" = '"+Values[0]+"' ";
        str2=key[0]+" ='"+id[0]+"' ";
        for(int i=1;i<items.length-1;i++){
            str1+=", "+items[i]+" = "+"'"+Values[i]+"' ";
        }
        for(int i=1;i<key.length-1;i++){
            str2+="and "+key[i]+" = '"+id[i]+"' ";
        }
        String sql="update "+table+" set "+str1+"where "+str2;
        db.execSQL(sql);
    }

    public static void insert(SQLiteDatabase db,String table,String[] items,String[] Values){
        String str1="";
        if(items.length>1)
            str1="("+items[0];
        else
            str1=items[0];
        for(int i=1;i<items.length-1;i++){
            str1+=", "+items[i];
        }
        if(items.length>1)
            str1+=","+items[items.length-1]+" )";

        String str2;
        if(items.length>1)
            str2="('"+Values[0]+"'";
        else
            str2="'"+Values[0]+"'";

        for(int i=1;i<items.length-1;i++){
            str2+=", '"+Values[i]+"'";
        }
        if(items.length>1)
            str2+=", '"+Values[items.length-1]+"')";
        String sql="insert into "+table+" "+ str1+" values "+str2;
        db.execSQL(sql);
    }

    public static Date String2Date(String date){
        Date DateTime=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        try {
            DateTime=sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime;
    }
}
