package com.pwp.myservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 大源 on 2015/7/31.
 */
public class alarmreceiver extends BroadcastReceiver {
    /*  public void onReceive(Context context,Intent intent){
      Intent i=new Intent();
        i.setClass(context,AlarmActivity.class);
       //5rfi.putExtra("scheduleid",intent.getExtras().getString("scheduleid"));
        Log.d("提醒接收器启�?:", intent.getExtras().getString("scheduleid"));
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("提醒接收器启�?:", intent.getExtras().getString("scheduleid"));
          //  ArrayList<String> stringList = (ArrayList<String>) intent.getStringArrayListExtra("ListString");
            //Intent i = new Intent(context, AlarmActivity.class);
          //  intent.putStringArrayListExtra("ListString", stringList);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // context.startActivity(i);
    }
}
