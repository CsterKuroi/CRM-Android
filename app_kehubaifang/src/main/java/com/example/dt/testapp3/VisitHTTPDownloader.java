package com.example.dt.testapp3;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by dt on 2015/8/24.
 */
public class VisitHTTPDownloader implements Runnable{
//    final static private String urlDir = "http://192.168.50.11:8000/static/";
//    final static private String wsuri = "ws://192.168.50.11:8000/ws";
    final static private String urlDir = VISITCONSTANT.DOWNLOADERDIR;
    final static private String wsuri = VISITCONSTANT.DATABASEURI;
    private String dir = Environment.getExternalStorageDirectory() + File.separator + "SaleCircle";
    final static private String suffix = "\r\n";
    final static private String prefix = "--";
    final static private String boundary = "*****";

    private CountDownLatch latch;
    private String dir2;
    private int id;
    private String uid;
    private String fname;
    private String downlaodName;

    public VisitHTTPDownloader(int _id, String _uid, String _fname){
        id = _id;
        uid = _uid;
        fname = _fname;
        latch = new CountDownLatch(1);
        File f = new File(dir);
        if (!f.exists())
            f.mkdirs();
    }

    public void run(){
        sendJson(this);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //downloadName : json包中的url字段
        //fname : 要保存的文件名
        downloadFile(downlaodName,fname);
    }

    public void sendJson(final VisitHTTPDownloader der){

        final JSONObject json = new JSONObject();
        final WebSocketConnection mConnection = new WebSocketConnection();
        try {
            //tape_100_10001_1.amr
            json.put("type","6");
            json.put("cmd","getVisitTape");
            json.put("baifang",String.valueOf(id));         //id
            json.put("uid",uid);
            json.put("bianhao","1");
//            json.put("bianhao", "1234");

            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(json.toString());
                    Log.e("Send Json Package", json.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        Log.e("Download Json.", "connection failed, code = " + code + ", reason" + reason);
                    else
                        Log.e("Download Json ", "successfully");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.e("HTTP TextMessage", "running here.");
                    try {
                        JSONObject rt = new JSONObject(payload);
                        int error = rt.isNull("error") ? -1 : Integer.parseInt(rt.getString("error"));
                        Log.e("error code = ", String.valueOf(error));
                        if (error == -1)
                            return;
                        else if (error == 1) {
                            downlaodName = rt.isNull("url") ? "" : rt.getString("url");
                            Log.e("json dir",downlaodName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
//                    der.downloadFile(fname,downlaodName);
                    Log.e("http json", "is finishing");
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void downloadFile(String name,String tupian) {
//        String urlStr="http://192.168.50.11:8000/static";
        BufferedInputStream input = null;
        OutputStream output=null;
        Log.e("Http", "download code is running");
        try {
            URL url=new URL(urlDir+name);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);// 设置连接主机超时
            conn.setReadTimeout(10000);// 设置从主机读取数据超时
            conn.setRequestMethod("GET");
            File tapeStorageDir = new File(dir);
            if (!tapeStorageDir.exists()) {
                if (!tapeStorageDir.mkdirs()) {
                    return;
                }
            }
            File file=new File(tapeStorageDir.getPath() + File.separator + tupian);
            input = new BufferedInputStream(conn.getInputStream());
            output=new FileOutputStream(file);
            int b;
            while ((b = input.read())!= -1){
                output.write(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
