package com.pwp.myservices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.dao.UserDAO;
import com.pwp.myclass.NetworkDetector;
import com.pwp.vo.ScheduleDateTag;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by 大源 on 2015/7/26.
 */
public class myservice extends Service {
    private LayoutInflater mFactory;
    private UserDAO userdao= null;
    DBOpenHelper dbOpenHelper=new DBOpenHelper(myservice.this, "schedules.db");
    private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    private Handler mHandler;
    private final WebSocketConnection mConnection = new WebSocketConnection();
    final String wsuri = "ws://"+ com.pwp.constant.UrlConstant.IP+":8001/ws";
    private String  TAG="myservice";
    String time="0";
    String time2="0";
    String update_flag="0";
    String update_flag2="0";
    private ScheduleDAO dao =new ScheduleDAO(this);
    //IMApplication imapp = (IMApplication) getApplication();
    //String authorid=imapp.getUserid();

    String userid="101";
    update_user_schedule flag=null;
    final WebSocketHandler wsHandler=null;

    @Override
	public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
	public void onCreate(){
      /*  wsHandler = new WebSocketHandler() {
            @Override
            public void onOpen() {
                Log.d(TAG, "Status: Connected to " + wsuri);
        *//*        String str = "{\"type\":\"1\", \"cmd\":\"1\", \"time\":\""+time+"\"}";
                Toast.makeText(myservice.this,"发�??:"+ str, Toast.LENGTH_SHORT).show();
                mConnection.sendTextMessage(str);*//*
            }
            @Override
            public void onTextMessage(String payload) {
                Log.d(TAG, "Got echo: " + payload);
                Toast.makeText(myservice.this, "收到"+payload, Toast.LENGTH_SHORT).show();
                parseJSON(payload);
            }
            @Override
            public void onClose(int code, String reason) {
                Log.d(TAG, "Connection lost.");
            }
        };*/
    }
    @Override
	public int onStartCommand(Intent intent,int flags,int startId){
        try {
            do {
                Thread.sleep(10*1000);
                if(NetworkDetector.detect(myservice.this)){
                    if(flag==null){
                        //flag= new update_user_schedule(myservice.this,userid,Url);
                    }
                    else{
                        flag.tt.start();
                    }
                   /* try{
                        if (!mConnection.isConnected()) {
                            mConnection.connect(wsuri, wsHandler);
                        }
                    }catch (WebSocketException e){
                    }*/
                }
                else{
                    flag.tt.wait();
                    flag=null;
                }
                //Toast.makeText(CalendarActivity.this,"handler启动",Toast.LENGTH_LONG).show();
            } while (!Thread.interrupted());
        }
        catch (InterruptedException e){
            Looper.prepare();
            Toast.makeText(myservice.this,e.toString(),Toast.LENGTH_LONG).show();
            Looper.loop();
        }
        return super.onStartCommand(intent,flags,startId);
    }

}
