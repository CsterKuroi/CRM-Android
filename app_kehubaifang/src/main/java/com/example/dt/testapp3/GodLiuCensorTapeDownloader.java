package com.example.dt.testapp3;

import android.os.Environment;
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
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by dt on 2015/8/24.
 */
public class GodLiuCensorTapeDownloader implements Runnable{
/*    final static private String urlDir = "http://192.168.50.11:8000/static/";
    final static private String wsuri = "ws://192.168.50.11:8000/ws";*/
    final static private String urlDir = VISITCONSTANT.DOWNLOADERDIR;
    final static private String wsuri = VISITCONSTANT.DATABASEURI;
    private final String dir = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "SaleCircle" + File.separator + "VisitCensorTape";
    private WebSocketConnection mConnection;
    private boolean isConnected=false;

    private String urlName;
    private VisitMainActivity act;

//    String dir;
//    ArrayList<String> dFiles;
    private String fname;
//    Map<String, String> url_file;
    private String url_file;
//    FormActivity activity;


    private CountDownLatch latch;

    public GodLiuCensorTapeDownloader(String _fname){

        latch = new CountDownLatch(1);
        fname = _fname;
//        this.dFiles = dFiles;
        mConnection = new WebSocketConnection();
//        url_file = "";
        File f = new File(dir);
        if (!f.exists())
            f.mkdirs();

    }

    public GodLiuCensorTapeDownloader setAct(VisitMainActivity _act){
        act = _act;
        return this;
    }

    public void run(){
        Log.e("Downlaod Censor Tape", "Running.");
        sendJson();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        for(Map.Entry<String, String> entry: url_file.entrySet())
            downloadFile();

    }

    public void sendJson(){

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    isConnected = true;
                    sendRequest(fname);
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
                        urlName = tape.isNull("url") ? "" : tape.getString("url");
                        String uid = tape.isNull("uid") ? "" : tape.getString("uid");
                        String stepid = tape.isNull("stepid") ? "" : tape.getString("stepid");
                        String processid = tape.isNull("processid") ? "" : tape.getString("processid");
                        String itemid = tape.isNull("itemid") ? "" : tape.getString("itemid");
//                        url_file.put(urlName, uid+"_"+stepid+"_"+processid+"_"+itemid);
                        url_file = uid+"_"+stepid+"_"+processid+"_"+itemid;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        latch.countDown();
                    }

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
            Log.e("Download Censor Tape", request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile() {
        BufferedInputStream input = null;
        OutputStream output=null;
        Log.e("Http", "download code is running");
//        Log.e("furl", furl);
//        Log.e("filename", filename);
        try {
            URL url=new URL(urlDir+urlName);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);// 设置连接主机超时
            conn.setReadTimeout(10000);// 设置从主机读取数据超时
            conn.setRequestMethod("GET");


            File file=new File(dir + File.separator + fname);
            input = new BufferedInputStream(conn.getInputStream());
            output=new FileOutputStream(file);
            Log.e("HTTP", "fileOutputStream");
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
//        act.callEvaluation();
        act.evaluationHandler.sendMessage(new Message());
    }

}
