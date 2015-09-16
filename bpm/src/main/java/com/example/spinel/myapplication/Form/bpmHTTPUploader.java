package com.example.spinel.myapplication.Form;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dt on 2015/8/24.
 */
public class bpmHTTPUploader implements Runnable{
//    final static private String urlDir = "http://192.168.50.11:8000/shenheluyin/";
    final static private String urlDir = "http://101.200.189.127:8001/shenheluyin/";
    private String dir;
    private Activity activity;
    final static private String end = "\r\n";
    final static private String twoHyphens = "--";
    final static private String boundary = "*****";
    private String fname;

    public bpmHTTPUploader(Activity activity, String _fname){
        this.activity = activity;
        fname = _fname;
        dir = activity.getFilesDir().getAbsolutePath();
    }

    public void run(){

        OutputStream ostream = null;
        InputStream istream = null;
        DataOutputStream ds = null;
        FileInputStream fStream = null;
        InputStream is = null;
        try {
            URL url = new URL(urlDir);
            Log.e("connection", "url = " + urlDir);
            Log.e("connection", "file = " + fname);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.e("connection", "connection is setting");
            conn.setDoOutput(true);     //permission to write data
            conn.setDoInput(true);      //permission to read data
            conn.setUseCaches(false);   //permission to use cache
            conn.setRequestMethod("POST");

            Log.e("connection", "connection is setting RequestProperty");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


            Log.e("HTTP request", conn.toString());
            Log.e("connection", "connection is writing bytes");
            ostream = conn.getOutputStream();
            ds = new DataOutputStream(ostream);
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file\";filename=\"" +
                    fname + "\"" +
                    end);
            ds.writeBytes(end);

            fStream = activity.openFileInput(fname);
            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1)
            {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//            fStream.close();
//            ds.close();
            is = conn.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }


            int code = conn.getResponseCode();
            Log.e("HTTP transfer : ",(code==200?"successfully":"failed code = " + code));

        } catch (MalformedURLException e) {
            Log.e("connect failed:","MalformedURLException");
            e.printStackTrace();
        }catch (IOException e) {
            Log.e("connect failed:","IOException");
            e.printStackTrace();
        }
        finally {
            try {
                if (ostream != null)
                    ostream.close();
                if (istream != null)
                    istream.close();
                if (ds != null)
                    ds.close();
                if (fStream != null)
                    fStream.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
