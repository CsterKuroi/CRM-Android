package yh729_ServiceAndThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.yanhao.task729.yh729_Constant;

import yh729_ServiceAndThread.yh729_AlarmNotificationService;
import yh729_activity.yh729_AlarmActivity;

/**
 * Created by Yanhao on 15-7-30.
 */
public class yh729_AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("test", "yh729_AlarmReceiver received broadcast");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            Intent i = new Intent(context, yh729_AlarmNotificationService.class);
//            i.setAction(yh729_Constant.NOTIFY_SERVICE_FLAG);
//            context.startService(i);
//            Log.i("test", "System started");
        } else if (intent.getAction().equals("com.example.yanhao.task729.alarm")) {
            Log.i("test", "23333");
            Intent i = new Intent();
            i.putExtra("type",intent.getStringExtra("type"));
            i.putExtra("content",intent.getStringExtra("content"));
            i.setClass(context, yh729_AlarmActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(i);
        }
    }
}
