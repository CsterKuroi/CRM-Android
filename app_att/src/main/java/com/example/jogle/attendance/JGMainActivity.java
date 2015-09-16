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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.ricky.database.CenterDatabase;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class JGMainActivity extends Activity implements Runnable, JGUploadCallBack {
    public static JGDataSet dataSet;
    private TextView time;
    private JGRoundImageView picshow;
    private TextView customer;
    private LinearLayout shotButton;
    private LinearLayout editcustomer;
    private LinearLayout editpos;
    private Button finishButton;
    private ImageView cancelButton;
    private AnimationSet animationSet;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;

            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {

                dataSet.setLatitude(location.getLatitude());
                dataSet.setLongitude(location.getLongitude());
                if (dataSet.getPosition() == null && location.getAddrStr() != null) {
                    dataSet.setPosition(location.getAddrStr());
                    TextView addr = (TextView) findViewById(R.id.address);
                    addr.setText(dataSet.getPosition());
                }
            }
        }
    };
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dataSet.setTime((String) msg.obj);
            time.setText((String) msg.obj);
        }
    };

    private static final int CAPTURE_REQUEST_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet.generateThumbnail();
                    picshow.setImageBitmap(dataSet.getThumbnail());
                    finishButton.setClickable(true);
                    finishButton.setBackgroundColor(0xff01aff4);
                    picshow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(JGMainActivity.this, JGShowActivity.class);
                            intent.putExtra("pic_path", dataSet.getPicPath());
                            startActivity(intent);
                        }
                    });
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet.setTimeStamp(null);
                    break;
            }
        }
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(10000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Register Baidu Map SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.jg_activity_main);

        // Set up animation
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation());
        animationSet.addAnimation(disappearAnimation());

        // refresh dataSet
            CenterDatabase cd = new CenterDatabase(this, null);
            String UID = cd.getUID();
            int uid = Integer.parseInt(UID);
            String name = cd.getNameByUID(UID);
            cd.close();
            dataSet = new JGDataSet();
            dataSet.setUserID(uid);
            dataSet.setUserName(name);

        customer = (TextView) findViewById(R.id.customer);
        if (dataSet.getCustomers() != null)
            customer.setText(dataSet.getCustomers());
        time = (TextView) findViewById(R.id.time);
        if (dataSet.getTime() != null)
            time.setText(dataSet.getTime());
        new Thread(this).start();

        // Set up Baidu Location Listener
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();

        shotButton = (LinearLayout) findViewById(R.id.shot);
        picshow = (JGRoundImageView) findViewById(R.id.picshow);

        shotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shotButton.startAnimation(animationSet);
                if (dataSet.getTimeStamp() != null) {
                    File pic = new File(dataSet.getPicPath());
                    if (pic.exists()) {
                        pic.delete();
                    }
                    File thumbnail = new File(dataSet.getThumbnailPath());
                    if (thumbnail.exists()) {
                        thumbnail.delete();
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_REQUEST_CODE);
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
                dataSet.setTimeStamp(timeStamp);
                mediaFile = new File(dataSet.getPicPath());
                return Uri.fromFile(mediaFile);
            }
        });

        editcustomer = (LinearLayout) findViewById(R.id.editcustomer);
        editcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editcustomer.startAnimation(animationSet);
                Intent intent = new Intent(JGMainActivity.this, JGCustomerPickerActivity.class);
                startActivity(intent);
            }
        });

        editpos = (LinearLayout) findViewById(R.id.editpos);
        editpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editpos.startAnimation(animationSet);
                intentToLocate();
            }
        });

        finishButton = (Button) findViewById(R.id.button6);
        finishButton.setClickable(false);
        finishButton.setBackgroundColor(0xffdddddd);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataSet.getTimeStamp() == null) {
                    showRequireShotDialog();
                    return;
                }
                uploadToServer();
                //dataSet = new JGDataSet();
                finishButton.setBackgroundColor(0xffdddddd);
                finishButton.setClickable(false);
                finishButton.setText("已签到");
            }
        });

        cancelButton = (ImageView) findViewById(R.id.imageView);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButton.startAnimation(animationSet);
                showCancelDialog();
            }
        });

        if (dataSet.getTimeStamp() != null) {
            picshow.setImageBitmap(dataSet.getThumbnail());
            picshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(JGMainActivity.this, JGShowActivity.class);
                    intent.putExtra("pic_path", dataSet.getPicPath());
                    startActivity(intent);
                }
            });
            finishButton.setVisibility(View.VISIBLE);
        }

        TextView addr = (TextView) findViewById(R.id.address);
        if (dataSet.getPosition() != null)
            addr.setText(dataSet.getPosition());

        EditText editText = (EditText) findViewById(R.id.editText);
        if (dataSet.getContent() != null)
            editText.setText(dataSet.getContent());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataSet.setContent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        customer = (TextView) findViewById(R.id.customer);
        if (dataSet.getCustomers() != null)
            customer.setText(dataSet.getCustomers());
        time = (TextView) findViewById(R.id.time);
        if (dataSet.getTime() != null)
            time.setText(dataSet.getTime());
        TextView addr = (TextView) findViewById(R.id.address);
        if (dataSet.getPosition() != null)
            addr.setText(dataSet.getPosition());
    }

    private void uploadToServer() {
        JGUpload upload = new JGUpload(this);
        String JSONString = upload.changeArrayDateToJson(dataSet);
        upload.up(JSONString);
        JGUploadPic uploadThread = new JGUploadPic(dataSet);
        uploadThread.start();
    }

    public void uploadCallBack(String payload) {
        if (payload.equals("1")) {
            JGDBOperation operation = new JGDBOperation(getApplicationContext());
            operation.save(dataSet);
            successDialog();
            dataSet = new JGDataSet();
        }
        else if (payload.equals("2")) {
            failDialog();
            dataSet = new JGDataSet();
        }
    }

    private void intentToLocate() {
        Intent intent = new Intent(JGMainActivity.this, JGLocateActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showCancelDialog();
            return false;
        }
        return false;
    }

    protected Animation scaleAnimation()
    {
        ScaleAnimation animationScale = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationScale.setInterpolator(new AccelerateInterpolator());
        animationScale.setDuration(500);
        animationScale.setFillAfter(false);
        return animationScale;
    }

    protected Animation disappearAnimation()
    {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);//设置动画持续时间
        return animation;
    }

    private void initLocation(){
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

    private void showRequireShotDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("请先拍照。")
                .setPositiveButton("确定", null).show();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否取消此次考勤打卡？")
                .setPositiveButton("否", null)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dataSet.getTimeStamp() != null) {
                            File pic = new File(dataSet.getPicPath());
                            if (pic.exists()) {
                                pic.delete();
                            }
                            File thumbnail = new File(dataSet.getThumbnailPath());
                            if (thumbnail.exists()) {
                                thumbnail.delete();
                            }
                        }
                        dataSet = new JGDataSet();
                        finish();
                    }
                }).show();
    }

    private void successDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("外勤签到成功！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                        finish();
                    }
                }).show();
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
