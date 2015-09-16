package com.pwp.myservices;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.pwp.myweather.WeatherAdaptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by 大源 on 2015/8/17.
 */
public class weather_service {
    Context context;
    private String city;
    private String ip;
    StringBuffer sb;
    URLConnection conn = null;
    InputStream fis = null;
    InputStreamReader in = null;
    BufferedReader buffer = null;
    Bundle data;
    //Handler handler;

    public weather_service(Context context) {
        this.context = context;
        //this.handler=handler;
    }
    public Bundle start(){
       // ip=getLocalIPAddress();
       return getcityfromid();
    }
    public Bundle getcityfromid(){
        try {
            URL url = new URL("http://whois.pconline.com.cn/ip.jsp");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream is = connect.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buff = new byte[256];
            int rc = 0;
            while ((rc = is.read(buff, 0, 256)) > 0) {
                outStream.write(buff, 0, rc);
            }
            System.out.println(outStream);
            byte[] b = outStream.toByteArray();
            // 关闭
            outStream.close();
            is.close();
            connect.disconnect();
            String address = new String(b,"gbk");
            byte []by=address.getBytes("utf-8");
            String add=new String (by,0,by.length);
            String add2=add.substring(0,add.indexOf("市")).trim();
            
          //  Toast.makeText(context, add2, Toast.LENGTH_LONG).show();


            data = new Bundle();
            data.putString("value", WeatherAdaptor.getWeather(add2));


           // setCity(address.substring(0,address.indexOf("市")));

        } catch (Exception e) {
            e.printStackTrace();
           // Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

        return data;
    }

}
