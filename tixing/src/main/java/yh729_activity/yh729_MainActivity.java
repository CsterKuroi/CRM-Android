package yh729_activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Constant;

import java.util.List;

import yh729_ServiceAndThread.yh729_AlarmNotificationService;


public class yh729_MainActivity extends FragmentActivity {
    public static boolean first=true;
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.yh729_activity);
            if(first){
                first=false;
                Log.i("test","check service");
                initService();
            }

        }catch(Exception e){
            Log.i("test", e.toString());
        }
    }
    protected void onDestroy(){

        super.onDestroy();
    }

    public void initService() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            ComponentName serviceName = runningServiceInfo.service;
            if ((serviceName.getPackageName().equals("com.example.yanhao.task729"))
                    && (serviceName.getClassName().equals("com.example.yanhao.task729"
                    + ".yh729_AlarmNotificationService")))
                return;
        }
        Intent intent = new Intent(getApplicationContext(), yh729_AlarmNotificationService.class);
        intent.setAction(yh729_Constant.NOTIFY_SERVICE_FLAG);
        startService(intent);
    }
}
