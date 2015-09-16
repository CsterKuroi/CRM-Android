package com.pwp.myservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by 大源 on 2015/7/26.
 */
public class mybootcompletereceiver extends BroadcastReceiver{
    @Override
	public void onReceive(Context context,Intent intent){
        Toast.makeText(context,"�?机启动了",Toast.LENGTH_LONG).show();
        Intent intent1=new Intent();
        intent1.setClass(context, myservice.class);
        context.startService(intent1);
    }
}
