package com.example.jogle.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class JGMain2Activity extends Activity implements Runnable, JGUploadCallBack {
    public static JGDataSet dataSet1;
    public static JGDataSet dataSet2;
    private int option;
    private Thread thread;
    private TextView time;
    //    private AnimationSet animationSet;
    private LocationClient mLocationClient = null;
    private Button b4;
    private Button b5;
    private JGRoundImageView view7;
    private JGRoundImageView view9;
    private RelativeLayout editpos;
    private TextView t12;
    private TextView t35;
    private ImageView paizhao1;
    private ImageView paizhao2;
    private RelativeLayout backButton3;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {

                dataSet1.setLatitude(location.getLatitude());
                dataSet1.setLongitude(location.getLongitude());
                dataSet2.setLatitude(location.getLatitude());
                dataSet2.setLongitude(location.getLongitude());
                if (dataSet1.getPosition() == null)
                    dataSet1.setPosition(location.getAddrStr());
                if (dataSet2.getPosition() == null)
                    dataSet2.setPosition(location.getAddrStr());
                TextView addr = (TextView) findViewById(R.id.address);
                addr.setText(dataSet1.getPosition());
            }
        }
    };

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dataSet1.setTime((String) msg.obj);
            dataSet2.setTime((String) msg.obj);
            time.setText((String) msg.obj);
            //view7.setClickable(true);
            //view9.setClickable(true);

            JGDBOperation operation = new JGDBOperation(getApplicationContext());
            List<JGDataSet> list = operation.getAll();
            for (int i = 0; i < list.size(); i++) {
                JGDataSet item = list.get(i);
                if (item.getTime().split(" ")[0].equals(time.getText().toString().split(" ")[0])) {
                    // same date
                    if (item.getType() == 1) {
                        dataSet1 = item;
                        view7.setImageBitmap(dataSet1.getThumbnail());
                        t12.setText(dataSet1.getTime().split(" ")[1]);
                        b4.setClickable(false);
//                        b4.setBackgroundColor(0xffdddddd);
                        b4.setText("已签到");
                        view7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(JGMain2Activity.this, JGShowActivity.class);
                                intent.putExtra("pic_path", dataSet1.getPicPath());
                                startActivity(intent);
                            }
                        });
                        paizhao1.setVisibility(View.GONE);
                    } else if (item.getType() == 2) {
                        dataSet2 = item;
                        view9.setImageBitmap(dataSet2.getThumbnail());
                        t35.setText(dataSet2.getTime().split(" ")[1]);
                        b5.setClickable(false);
//                        b5.setBackgroundColor(0xffdddddd);
                        b5.setText("已签退");
                        view9.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(JGMain2Activity.this, JGShowActivity.class);
                                intent.putExtra("pic_path", dataSet2.getPicPath());
                                startActivity(intent);
                            }
                        });
                        paizhao2.setVisibility(View.GONE);
                    }
                }
            }
        }
    };
    private static final int CAPTURE_REQUEST_CODE1 = 101;
    private static final int CAPTURE_REQUEST_CODE2 = 102;


    @Override
    public void onResume() {
        super.onResume();
        time = (TextView) findViewById(R.id.time);
        if (dataSet1.getTime() != null)
            time.setText(dataSet1.getTime());
        TextView addr = (TextView) findViewById(R.id.address);
        if (dataSet1.getPosition() != null)
            addr.setText(dataSet1.getPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_REQUEST_CODE1) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet1.generateThumbnail();
                    view7.setImageBitmap(dataSet1.getThumbnail());
                    b4.setClickable(true);
//                    b4.setBackgroundColor(0xff01aff4);
                    view7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(JGMain2Activity.this, JGShowActivity.class);
                            intent.putExtra("pic_path", dataSet1.getPicPath());
                            startActivity(intent);
                        }
                    });
                    paizhao1.setVisibility(View.GONE);
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet1.setTimeStamp(null);
                    break;
            }
        } else if (requestCode == CAPTURE_REQUEST_CODE2) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet2.generateThumbnail();
                    view9.setImageBitmap(dataSet2.getThumbnail());
                    b5.setClickable(true);
//                    b5.setBackgroundColor(0xff01aff4);
                    view9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(JGMain2Activity.this, JGShowActivity.class);
                            intent.putExtra("pic_path", dataSet2.getPicPath());
                            startActivity(intent);
                        }
                    });
                    paizhao2.setVisibility(View.GONE);
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet2.setTimeStamp(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void run() {
        try {
            while (true) {
                try {
                    URL url = new URL("http://www.baidu.com");// 取得资源对象 
                    URLConnection uc = url.openConnection();// 生成连接对象    
                    uc.connect(); // 发出连接 
                    long ldate = uc.getDate(); // 取得网站日期时间（时间戳）
                    Date date = new Date(ldate);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    String dateStr = sdf.format(c.getTime());
                    Message message = new Message();
                    message.obj = dateStr;
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Register Baidu Map SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.jg_activity_main2);
        // Set up animation
//        animationSet = new AnimationSet(true);
//        animationSet.addAnimation(scaleAnimation());
//        animationSet.addAnimation(disappearAnimation());

        // find view by id
        t12 = (TextView) findViewById(R.id.textView12);
        t35 = (TextView) findViewById(R.id.textView35);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        view7 = (JGRoundImageView) findViewById(R.id.imageView7);
        view9 = (JGRoundImageView) findViewById(R.id.imageView9);
        paizhao1 = (ImageView) findViewById(R.id.paizhao1);
        paizhao2 = (ImageView) findViewById(R.id.paizhao2);
        backButton3 = (RelativeLayout) findViewById(R.id.back);

        final LinearLayout shangban1 = (LinearLayout) findViewById(R.id.shangban1);
        final RelativeLayout shangban2 = (RelativeLayout) findViewById(R.id.shangban2);
        final LinearLayout xiaban1 = (LinearLayout) findViewById(R.id.xiaban1);
        final RelativeLayout xiaban2 = (RelativeLayout) findViewById(R.id.xiaban2);
        final TextView tabShangban = (TextView) findViewById(R.id.tab_shangban);
        final TextView tabXiaban = (TextView) findViewById(R.id.tab_xiaban);
        tabShangban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabShangban.setTextColor(getResources().getColor(R.color.my_tab_sel_color));
                tabShangban.setBackgroundResource(R.drawable.my_tab_sel);
                tabXiaban.setTextColor(getResources().getColor(android.R.color.black));
                tabXiaban.setBackgroundColor(getResources().getColor(android.R.color.white));
                shangban1.setVisibility(View.VISIBLE);
                shangban2.setVisibility(View.VISIBLE);
                b4.setVisibility(View.VISIBLE);
                xiaban1.setVisibility(View.GONE);
                xiaban2.setVisibility(View.GONE);
                b5.setVisibility(View.GONE);
            }
        });
        tabXiaban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabXiaban.setTextColor(getResources().getColor(R.color.my_tab_sel_color));
                tabXiaban.setBackgroundResource(R.drawable.my_tab_sel);
                tabShangban.setTextColor(getResources().getColor(android.R.color.black));
                tabShangban.setBackgroundColor(getResources().getColor(android.R.color.white));
                xiaban1.setVisibility(View.VISIBLE);
                xiaban2.setVisibility(View.VISIBLE);
                b5.setVisibility(View.VISIBLE);
                shangban1.setVisibility(View.GONE);
                shangban2.setVisibility(View.GONE);
                b4.setVisibility(View.GONE);
            }
        });
        // refresh dataSet
        int uid = getIntent().getIntExtra("uid", -1);
        String name = getIntent().getStringExtra("name");
//        if (dataSet1 == null || dataSet1.getTimeStamp() == null) {
        dataSet1 = new JGDataSet();
        dataSet1.setType(1);
        dataSet1.setUserID(uid);
        dataSet1.setUserName(name);
//        }

//        if (dataSet2 == null || dataSet2.getTimeStamp() == null) {
        dataSet2 = new JGDataSet();
        dataSet2.setType(2);
        dataSet2.setUserID(uid);
        dataSet2.setUserName(name);
//        }

        time = (TextView) findViewById(R.id.time);
        if (dataSet1.getTime() != null)
            time.setText(dataSet1.getTime());
        thread = new Thread(this);
        thread.start();

        // Set up Baidu Location Listener
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        TextView alarm = (TextView) findViewById(R.id.setalarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JGMain2Activity.this, JGAlarmActivity.class);
                startActivity(intent);
            }
        });
        TextView addr = (TextView) findViewById(R.id.address);
//        addr.setText(dataSet1.getPosition());
//        addr.setText(dataSet2.getPosition());

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //b4.startAnimation(animationSet);
                option = 1;
                uploadToServer();
                if (dataSet1.getTime() != null)
                    t12.setText(dataSet1.getTime().substring(dataSet1.getTime().length() - 8));
//                b4.setBackgroundColor(0xffdddddd);
                b4.setClickable(false);
                b4.setText("已签到");
                //dataSet1 = new JGDataSet();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //b5.startAnimation(animationSet);
                option = 2;
                uploadToServer();
                if (dataSet2.getTime() != null)
                    t35.setText(dataSet2.getTime().substring(dataSet2.getTime().length() - 8));
//                b5.setBackgroundColor(0xffdddddd);
                b5.setClickable(false);
                b5.setText("已签退");
                //dataSet2 = new JGDataSet();
            }
        });

        if (dataSet1.getTimeStamp() == null) {
            b4.setClickable(false);
//            b4.setBackgroundColor(0xffdddddd);
            paizhao1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    view7.startAnimation(animationSet);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                    Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE1);
                }

                public Uri getOutputMediaFileUri() {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Attendance");
                    // This location works best if you want the created images to be shared
                    // between applications and persist after your app has been uninstalled.
                    // Create the storage directory if it does not exist
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            return null;
                        }
                    }
                    // Create a media file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File mediaFile;
                    dataSet1.setTimeStamp(timeStamp);
                    mediaFile = new File(dataSet1.getPicPath());
                    return Uri.fromFile(mediaFile);
                }
            });
            //view7.setClickable(false);
        }

        if (dataSet2.getTimeStamp() == null) {
            b5.setClickable(false);
//            b5.setBackgroundColor(0xffdddddd);
            paizhao2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    view9.startAnimation(animationSet);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                    Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE2);
                }

                public Uri getOutputMediaFileUri() {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Attendance");
                    // This location works best if you want the created images to be shared
                    // between applications and persist after your app has been uninstalled.
                    // Create the storage directory if it does not exist
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            return null;
                        }
                    }
                    // Create a media file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File mediaFile;
                    dataSet2.setTimeStamp(timeStamp);
                    mediaFile = new File(dataSet2.getPicPath());
                    return Uri.fromFile(mediaFile);
                }
            });
            //view9.setClickable(false);
        }

        backButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveDialog();
            }
        });
        editpos = (RelativeLayout) findViewById(R.id.editpos);
        editpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToLocate();
            }
        });
    }

    private void intentToLocate() {
        Intent intent = new Intent(JGMain2Activity.this, JGLocate2Activity.class);
        startActivity(intent);
    }

    private void uploadToServer() {
        JGUpload upload = new JGUpload(this);
        String JSONString = null;
        if (option == 1)
            JSONString = upload.changeArrayDateToJson(dataSet1);
        else if (option == 2)
            JSONString = upload.changeArrayDateToJson(dataSet2);
        upload.up(JSONString);
        if (option == 1) {
            JGUploadPic uploadThread = new JGUploadPic(dataSet1);
            uploadThread.start();
        } else if (option == 2) {
            JGUploadPic uploadThread = new JGUploadPic(dataSet2);
            uploadThread.start();
        }
    }

    public void uploadCallBack(String payload) {
        if (payload.equals("1")) {
            JGDBOperation operation = new JGDBOperation(getApplicationContext());
            if (option == 1) {
                operation.save(dataSet1);
                successDialog();
            } else if (option == 2) {
                operation.save(dataSet2);
                successDialog2();
            }
//            successDialog();
        } else if (payload.equals("2")) {
            failDialog();
        }
    }

    private void successDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("内勤签到成功！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(JGMain2Activity.this, JGentry.class);
//                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    private void successDialog2() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("内勤签退成功！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(JGMain2Activity.this, JGentry.class);
//                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    private void failDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("上传数据失败，请检查网络。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(JGMain2Activity.this, JGentry.class);
//                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showLeaveDialog();
            return false;
        }
        return false;
    }

    private void showLeaveDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否取消考勤签到？")
                .setPositiveButton("否", null)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dataSet1.getTimeStamp() != null && !b4.getText().toString().equals("已签到")) {
                            File pic = new File(dataSet1.getPicPath());
                            if (pic.exists()) {
                                pic.delete();
                            }
                            File thumbnail = new File(dataSet1.getThumbnailPath());
                            if (thumbnail.exists()) {
                                thumbnail.delete();
                            }
                        }

                        if (dataSet2.getTimeStamp() != null && !b5.getText().toString().equals("已签到")) {
                            File pic = new File(dataSet2.getPicPath());
                            if (pic.exists()) {
                                pic.delete();
                            }
                            File thumbnail = new File(dataSet2.getThumbnailPath());
                            if (thumbnail.exists()) {
                                thumbnail.delete();
                            }
                        }

                        finish();
//                        Intent intent = new Intent(JGMain2Activity.this, entry.class);
//                        startActivity(intent);
                    }
                }).show();
    }

    protected Animation scaleAnimation() {
        ScaleAnimation animationScale = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationScale.setInterpolator(new AccelerateInterpolator());
        animationScale.setDuration(500);
        animationScale.setFillAfter(false);
        return animationScale;
    }

    protected Animation disappearAnimation() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);//设置动画持续时间
        return animation;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onStop() {
        thread.interrupt();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        thread.interrupt();
        super.onDestroy();
    }

    private void showRequireShotDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("请先拍照。")
                .setPositiveButton("确定", null).show();
    }
}
