package com.example.spinel.myapplication.Form;

import android.os.Message;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by dt on 2015/8/24.
 */
public class bpmHTTPDownloader implements Runnable{
/*    final static private String urlDir = "http://192.168.50.11:8000/static/";
    final static private String wsuri = "ws://192.168.50.11:8000/ws";*/
    final static private String urlDir = "http://101.200.189.127:8001/static/";
    final static private String wsuri = "ws://101.200.189.127:8001/ws";
    private WebSocketConnection mConnection;
    private boolean isConnected=false;

    String dir;
    ArrayList<String> dFiles;
    Map<String, String> url_file;
    bpmFormActivity activity;


    private CountDownLatch latch;

    public bpmHTTPDownloader(ArrayList<String> dFiles, bpmFormActivity activity){

        this.activity = activity;
        dir = activity.getFilesDir().getAbsolutePath();
        latch = new CountDownLatch(dFiles.size());
        this.dFiles = dFiles;
        mConnection = new WebSocketConnection();
        url_file = new HashMap<>();

    }

    public void run(){
        sendJson(this);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Map.Entry<String, String> entry: url_file.entrySet())
            downloadFile(entry.getKey(), entry.getValue());

        Message msg = Message.obtain();
        msg.arg1=0;
        activity.handler.sendMessage(msg);
    }

    public void sendJson(final bpmHTTPDownloader der){

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    isConnected = true;
                    for(String file: dFiles)
                        sendRequest(file);
                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.e("TextMessage", payload);
                    try {
                        JSONObject response = new JSONObject(payload);
                        String error = response.getString("error");
                        if (!error.equals("1"))
                            return;

                        //解析json
                        JSONObject tape = response.getJSONObject("tape");
                        String url = tape.isNull("url") ? "" : tape.getString("url");
                        String uid = tape.isNull("uid") ? "" : tape.getString("uid");
                        String stepid = tape.isNull("stepid") ? "" : tape.getString("stepid");
                        String processid = tape.isNull("processid") ? "" : tape.getString("processid");
                        String itemid = tape.isNull("itemid") ? "" : tape.getString("itemid");
                        url_file.put(url, uid+"_"+stepid+"_"+processid+"_"+itemid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(String file){
        //如果没有连接就返回
        if(mConnection==null || !mConnection.isConnected() || !isConnected) {
            return;
        }
        JSONObject request = new JSONObject();

        try {
            String[] strs = file.split("_");
            request.put("cmd", "shenheluyin").put("type", "12").put("uid", strs[0]).put("stepid", strs[1]).put("processid", strs[2]).put("itemid", strs[3]);
            mConnection.sendTextMessage(request.toString());
            Log.e("Send Json Package", request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String furl, String filename) {
        BufferedInputStream input = null;
        OutputStream output=null;
        Log.e("Http", "download code is running");
        try {
            URL url=new URL(urlDir+furl);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);// 设置连接主机超时
            conn.setReadTimeout(10000);// 设置从主机读取数据超时
            conn.setRequestMethod("GET");


            File file=new File(dir + File.separator + filename);
            input = new BufferedInputStream(conn.getInputStream());
            output=new FileOutputStream(file);
            int b;
            boolean received;
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
