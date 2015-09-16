package com.geminy.productshow;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.util.Log;


import com.geminy.productshow.Activity.GEMINY_MainActivity;
import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Model.GEMINY_Tb_product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class GEMINY_JSON {
    private JSONObject jsonObject;
    private WebSocketConnection mConnection;
    private static final String IPPORT="101.200.189.127:8001";
    private static final String IPPORT1="192.168.50.11:8000";
    private static final String wsuri = "ws://"+IPPORT+"/ws";
    private static final String picpreLoc="http://"+IPPORT+"/static/";
    private GEMINY_ProductDAO productDAO;
    private String localtime;
    private String picLoc;
    private String proIdOfPic;
    private CountDownLatch latch;
    public static final int ALL_FINISH=0;

    private int count=1;

    public GEMINY_JSON(GEMINY_ProductDAO productDAO){
        this.productDAO=productDAO;
    }

    public void linkJSON() {
        mConnection=new WebSocketConnection();
        try {
            jsonObject = new JSONObject();
            jsonObject.put("cmd","product");
            jsonObject.put("type","10");

            if(!productDAO.isDataExist()){
                localtime="0";
            }else{
                localtime=productDAO.getFirstData().getTime();
            }
            jsonObject.put("time",localtime);
        }catch (org.json.JSONException jsone){
            Log.e("geminyjson",jsone.toString());
        }
        try {

            mConnection.connect(wsuri, new WebSocketHandler() {
                private boolean success = false;

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage(jsonObject.toString());
                    Log.e("test", "发送Json字段 " + jsonObject.toString());
                }

                @Override
                public void onClose(int code, String reason) {
                    if (!success)
                        Log.e("test", "connection lost" + code + reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        JSONObject json = new JSONObject(payload);
                        int errorCode = json.isNull("error")?-1:Integer.parseInt(json.getString("error"));
                        Log.e("收到json字段：",json.toString());
                        Log.e("errorCode = ", String.valueOf(errorCode));
                        if(!json.isNull("cmd")){
                            if(json.getString("cmd").equals("product")) {

                                parseJSON(json);
                                if(GEMINY_MainActivity.mainActivity!=null){
                                    Message message0=new Message();
                                    message0.what=ALL_FINISH;
                                    GEMINY_MainActivity.mainActivity.mHandler.sendMessage(message0);
                                }
                            }
                        }else{
                            Log.e("cmd error:","cmd is null!");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mConnection.disconnect();
                }
            });
        }catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void parseJSON(JSONObject jsonObject){
        JSONArray productlist;
        final JSONArray piclist;
        String servertime;
        try {
            if(!jsonObject.isNull("time")) {
                servertime = jsonObject.getString("time");
                if(Double.parseDouble(servertime)>Double.parseDouble(localtime)){
                    if(!jsonObject.isNull("productlist")) {
                        productlist = jsonObject.getJSONArray("productlist");


                        if(productlist.length()!=0) {
                            for (int i = 0; i < productlist.length(); i++) {
                                String productId = productlist.getJSONObject(i).getString("id");
                                String productName = productlist.getJSONObject(i).getString("name");
                                String productType = productlist.getJSONObject(i).getString("type");
                                String productDetail = productlist.getJSONObject(i).getString("desc");
                                String productStatus = productlist.getJSONObject(i).getString("status");
                                GEMINY_Tb_product tb_product = new GEMINY_Tb_product(
                                        Integer.parseInt(productId),
                                        productName,
                                        productType,
                                        "",
                                        ";",
                                        productDetail,
                                        ";",
                                        1,
                                        ";",
                                        0,
                                        "",
                                        "",
                                        servertime
                                );
                                if (localtime.equals("0")||productDAO.selectById(Integer.parseInt(productId))==null) {
                                    productDAO.add(tb_product);
                                } else {
                                    productDAO.update(tb_product);
                                }
                            }
                        }
                    }
                    else
                        Log.e("productlist error:","productlist is null!");
                    if(!jsonObject.isNull("piclist")){
                        piclist=jsonObject.getJSONArray("piclist");
                        if(piclist.length()!=0) {


                            for (int i = 0; i < piclist.length(); i++) {

                                latch=new CountDownLatch(1);
                                picLoc= piclist.getJSONObject(i).getString("url");
                                proIdOfPic = piclist.getJSONObject(i).getString("product");

                                new Thread(){
                                    @Override
                                    public void run() {
                                        downloadPic(picLoc, proIdOfPic);
                                        latch.countDown();
                                    }
                                }.start();
                                try {
                                    Log.e("piclist i",i+"");
                                    latch.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
                    else
                        Log.e("piclist error:","piclist is null!");
                }
            }
            else
                Log.e("time error:","time is null!");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void downloadPic(String picUrl,String proidofpic){
        OutputStream outputStream;
        URL myPicURL;
        Bitmap bitmap;
        try {

            String picdir=Environment.getExternalStorageDirectory().getPath()+File.separator+"ProductPictures"+File.separator+proidofpic;
            File mediaStorageDir=new File(picdir);
            String picname=picUrl.substring(picUrl.lastIndexOf("/")+1,picUrl.length());
            String filePath=mediaStorageDir.getPath()+File.separator+picname;
            Log.e("pic download count", count + "");
            count++;
            File file=new File(filePath);
            if(!mediaStorageDir.exists()){
                mediaStorageDir.mkdirs();
            }else{
                if(file.exists()) {
                    GEMINY_Tb_product tb_product = productDAO.selectById(Integer.parseInt(proidofpic));
                    tb_product.setPicture(filePath);
                    productDAO.update(tb_product);
                    return;
                }
            }



            //得到数据流
            myPicURL=new URL(picpreLoc+java.net.URLEncoder.encode(picUrl));
            HttpURLConnection conn=(HttpURLConnection)myPicURL.openConnection();
            InputStream is = conn.getInputStream();


            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            options.inPurgeable=true;
            options.inInputShareable=true;
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is,null,options);

            outputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            //关闭数据流
            is.close();

            GEMINY_Tb_product tb_product = productDAO.selectById(Integer.parseInt(proidofpic));
            tb_product.setPicture(filePath);
            productDAO.update(tb_product);
        } catch (Exception e) {
            Log.e("getBitmapError:",e.toString());
        }
    }



}
