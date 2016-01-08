package com.pwp.myclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by renxin on 2015/8/30.
 */
public class getHttpBitmap {
        /**
         * 获取网落图片资源
         * @param url
         * @return
         */
        public static Bitmap getHttpBitmap(String url){
            URL myFileURL;
            Bitmap bitmap=null;
            try{
                myFileURL = new URL(url);
                //获得连接
                HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                InputStream is = conn.getInputStream();
                //解析得到图片
                bitmap = BitmapFactory.decodeStream(is);
                //关闭数据流
                is.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            return bitmap;

        }
}
