package com.kuroi.contract.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.kuroi.contract.model.Contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Administrator on 15-8-19.
 */
public class ConPicURL extends Thread{
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;
    private ConPicURLCallBack callBack;
    private String name;
    private String tupian;
    private String userID;

    public ConPicURL(ConPicURLCallBack callBack, String userID) {
        this.callBack = callBack;
        this.userID = userID;
    }
    public ConPicURL(ConPicURLCallBack callBack,String name,String tupian, String userID) {
        this.callBack = callBack;
        this.name= name;
        this.tupian=tupian;
        this.userID = userID;
    }

    public String downpicJson(Contract contract) {
        try {
            JSONObject object = new JSONObject();
            String hetongid = String.valueOf(contract.getId());
            object.put("hetongid", hetongid);
            object.put("cmd", "hetongtupian");
            object.put("type", "8");
            object.put("userID", userID);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void downpic(String json) {
        mConnection = new WebSocketConnection();
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json字段" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段"+tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test", "Got echo: " + payload);
                        callBack.picURLCallBack(payload);
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2)
                            callBack.picURLCallBack("{\"cmd\": \"hetongtupian\", \"hetong\": [], \"error\": \"2\"}");
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        downloadFile(name,tupian);
    }
    public void downloadFile(String name,String tupian) {
        String urlStr="http://101.200.189.127:8001/static";
        OutputStream output=null;
        try {
            URL url=new URL(urlStr+name);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(20000);// 设置连接主机超时
            conn.setReadTimeout(20000);// 设置从主机读取数据超时
            conn.setRequestMethod("GET");
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Contract");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return;
                }
            }
            File file=new File(mediaStorageDir.getPath() + File.separator + tupian);
            InputStream input=conn.getInputStream();
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPreferredConfig = Bitmap.Config.RGB_565;
            option.inPurgeable = true;
            option.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(input,null,option);
            output=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
            callBack.picURLCallBack("{\"cmd\": \"hetongtupian\", \"hetong\": [], \"error\": \"9\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        }
    }
}
