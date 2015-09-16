package yh729_activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.example.jogle.attendance.JGMain2Activity;
import com.example.yanhao.task729.yh729_Constant;
import com.ricky.database.CenterDatabase;

import java.util.Calendar;
import java.util.List;

import yh729_DB.yh729_LocalDataBase;
import yh729_Fragment.yh729_mFragment;
import yh729_ServiceAndThread.yh729_AlarmNotificationService;

/**
 * Created by Yanhao on 15-7-31.
 */
public class yh729_AlarmActivity extends Activity {
    private yh729_AlarmActivity mActivity;
    private PowerManager.WakeLock wl;
    private CenterDatabase cdb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplication(), RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_ALARM));
        cdb = new CenterDatabase(getApplicationContext(), null);
        mActivity = this;
        yh729_mFragment.now = null;
        Log.i("test", "yh729_AlarmActivity started successfully");
        switch (getIntent().getStringExtra("type")) {
            case "schedule":
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                try {
                    Log.i("test", "show dialog");
                    String[] settime_item = {"查看提醒", "五分钟后再响", "取消"};
                    new AlertDialog.Builder(this)
                            .setItems(settime_item, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //wakeUpAndUnlock(this);
                                    switch (which) {
                                        case 0:
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", yh729_Constant.SCHEDULE_REMIND);
                                            Intent intent = new Intent(mActivity, yh729_MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtras(bundle);
                                            mActivity.startActivity(intent);
                                            mediaPlayer.stop();
                                            finish();
                                            break;
                                        case 1:
                                            mediaPlayer.stop();
                                            SQLiteDatabase db = (new yh729_LocalDataBase(getApplicationContext(), null)).getDataBase();
                                            db.execSQL("insert into alarm_record (repeat ,content ,date_time,type,way) values ('" + "" + yh729_Constant.Repeat_Once + "' ," + "'','" + (5 * 1000 * 60 + Calendar.getInstance().getTime().getTime()) + "','schedule','1')");
                                            finish();
                                            break;
                                        case 2:
                                            mediaPlayer.stop();
                                            finish();
                                            break;
                                    }
                                }
                            }).setCancelable(false).show();
                } catch (Exception e) {
                    Log.e("test", e.toString());
                }
                break;
            case "approval":
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                new AlertDialog.Builder(this).setTitle("审批提醒").setMessage(getIntent()
                        .getStringExtra("content"))
                        .setCancelable(false)
                        .setPositiveButton("查 看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaPlayer.stop();
                                finish();
                            }
                        })
                        .setNegativeButton("取 消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaPlayer.stop();
                                finish();
                            }
                        }).show();
                break;
            case "inside_work":
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                new AlertDialog.Builder(this).setTitle("内勤提醒").setMessage(getIntent()
                        .getStringExtra("content"))
                        .setCancelable(false)
                        .setPositiveButton("查 看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mActivity, JGMain2Activity.class);
                                mActivity.startActivity(intent);
                                mediaPlayer.stop();
                                finish();
                            }
                        })
                        .setNegativeButton("取 消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaPlayer.stop();
                                finish();
                            }
                        }).show();
                break;
            case "outside_work":
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                new AlertDialog.Builder(this).setTitle("外勤提醒").setMessage(getIntent()
                        .getStringExtra("content"))
                        .setCancelable(false)
                        .setPositiveButton("查 看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaPlayer.stop();
                                finish();
                            }
                        })
                        .setNegativeButton("取 消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaPlayer.stop();
                                finish();
                            }
                        }).show();
                break;


        }
    }

    public void wakeUpAndUnlock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "BusSnoozeAlarm");
        //点亮屏幕
        wl.acquire();
        // wl.release();
    }

    private void checkservice() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            ComponentName serviceName = runningServiceInfo.service;
            if ((serviceName.getPackageName().equals("com.example.yanhao.task729"))
                    && (serviceName.getClassName().equals("com.example.yanhao.task729"
                    + ".yh729_AlarmNotificationService")))
                return;
            else {
                Intent i = new Intent(getApplicationContext(), yh729_AlarmNotificationService.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(yh729_Constant.NOTIFY_SERVICE_FLAG);
                getApplicationContext().startService(i);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        //wl.release();
    }
}
