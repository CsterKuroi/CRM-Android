package com.melnykov.fab.sample.shibie;
/**
 * Created by Administrator on 2015/7/26 0026.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.tools.crmUrlConstant;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:
 * <br/>site: <a href="http://www.crazyit.org">crazyit.org</a>
 * <br/>Copyright (C), 2001-2014, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author  Yeeku.H.Lee kongyeeku@163.com
 * @version  1.0
 */
public class crmcapture extends Activity
{
    private String postUrl = crmUrlConstant.postUrl; //处理POST请求的页面
    private String fileName = "haha.jpg";  //报文中的文件名参数
    private String path = Environment.getExternalStorageDirectory().getPath();  //Don't use "/sdcard/" here
    private String uploadFile = null;    //待上传的文件路径
    String User_id;
    SurfaceView sView;
    SurfaceHolder surfaceHolder;
    int screenWidth, screenHeight;
    // 定义系统所用的照相机
    Camera camera;
    // 是否在预览中
    boolean isPreview = false;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.crm_mains);
        // 获取窗口管理器
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        // 获取屏幕的宽和高
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // 获取界面中SurfaceView组件
        sView = (SurfaceView) findViewById(R.id.sView);
        // 设置该Surface不需要自己维护缓冲区
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 获得SurfaceView的SurfaceHolder
        surfaceHolder = sView.getHolder();
        // 为surfaceHolder添加一个回调监听器

        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crmcapture.this.finish();
            }
        });

        surfaceHolder.addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height)
            {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                // 打开摄像头
                initCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                // 如果camera不为null ,释放摄像头
                if (camera != null)
                {
                    if (isPreview) camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
        User_id = IMApplication.getUserid(this);
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    private void initCamera()
    {
        if (!isPreview)
        {
            // 此处默认打开后置摄像头。
            // 通过传入参数可以打开前置摄像头
            camera = Camera.open(0);  //①
            camera.setDisplayOrientation(90);
        }
        if (camera != null && !isPreview)
        {
            try
            {
                Camera.Parameters parameters = camera.getParameters();
                // 设置预览照片的大小
                parameters.setPreviewSize(screenWidth, screenHeight);
                // 设置预览照片时每秒显示多少帧的最小值和最大值
                parameters.setPreviewFpsRange(4, 10);
                // 设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                // 设置JPG照片的质量
                parameters.set("jpeg-quality", 10);
                // 设置照片的大小
                parameters.setPictureSize(screenWidth, screenHeight);
                // 通过SurfaceView显示取景画面
                camera.setPreviewDisplay(surfaceHolder);  //②
                // 开始预览
                camera.startPreview();  //③
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    /* 上传文件至Server的方法 */
    private void uploadFile()
    {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            URL url = new URL(postUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
            con.setDoOutput(true);
          /* Read from the connection. Default is true.*/
            con.setDoInput(true);
          /* Post cannot use caches */
            con.setUseCaches(false);
          /* Set the post method. Default is GET*/
            con.setRequestMethod("POST");
          /* 设置请求属性 */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

          /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file\";filename=\"" +
                    fileName + "\"" +
                    end);
            ds.writeBytes(end);
          /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
          /* 设置每次写入8192bytes */
            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];   //8k
            int length = -1;
          /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1)
            {
            /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* 关闭流，写入的东西自动生成Http正文*/
            fStream.close();
          /* 关闭DataOutputStream */
            ds.close();
          /* 从返回的输入流读取响应信息 */
            InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }
          /* 显示网页响应内容 */


//            Toast.makeText(capture.this, b.toString().trim(), Toast.LENGTH_SHORT).show();//Post成功

            Toast.makeText(crmcapture.this, "上传成功", Toast.LENGTH_SHORT).show();//Post成功
            crmcapture.this.finish();

        } catch (Exception e)
        {
            /* 显示异常信息 */
            Toast.makeText(crmcapture.this, "上传失败" + e, Toast.LENGTH_SHORT).show();//Post失败
        }
    }

    public void capture(View source)
    {
        if (camera != null)
        {
            // 控制摄像头自动对焦后才拍照
            camera.autoFocus(autoFocusCallback);  //④
        }
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback()
    {
        // 当自动对焦时激发该方法
        @Override
        public void onAutoFocus(boolean success, Camera camera)
        {
            if (success)
            {
                // takePicture()方法需要传入3个监听器参数
                // 第1个监听器：当用户按下快门时激发该监听器
                // 第2个监听器：当相机获取原始照片时激发该监听器
                // 第3个监听器：当相机获取JPG照片时激发该监听器
                camera.takePicture(new Camera.ShutterCallback()
                {
                    public void onShutter()
                    {
                        // 按下快门瞬间会执行此处代码
                    }
                }, new Camera.PictureCallback()
                {
                    public void onPictureTaken(byte[] data, Camera c)
                    {
                        // 此处代码可以决定是否需要保存原始照片信息
                    }
                }, myJpegCallback);  //⑤
            }
        }
    };

    Camera.PictureCallback myJpegCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            // 根据拍照所得的数据创建位图
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            // 加载/layout/save.xml文件对应的布局资源
            View saveDialog = getLayoutInflater().inflate(R.layout.crm_save,
                    null);
            // 获取saveDialog对话框上的ImageView组件
            ImageView show = (ImageView) saveDialog
                    .findViewById(R.id.show);
            // 显示刚刚拍得的照片
            show.setImageBitmap(bm);
            // 使用对话框显示saveDialog组件
            new AlertDialog.Builder(crmcapture.this).setView(saveDialog)
                    .setPositiveButton("上传", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // 创建一个位于SD卡上的文件
                            File root = new File("/mnt/sdcard/mingpian/"+User_id+"/");//+user

                            if (!root.exists()) {
                                root.mkdirs();
                            }

                            //*************************************修改此处代码
                            File file = new File(root,User_id +"-0-0.jpg");
                            FileOutputStream outStream = null;
                            try
                            {
                                // 打开指定文件对应的输出流
                                outStream = new FileOutputStream(file);
                                // 把位图输出到指定文件中
                                bm.compress(Bitmap.CompressFormat.JPEG, 10,
                                        outStream);
                                outStream.close();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            fileName = User_id+ "-0-0.jpg";
                            uploadFile = path + "/mingpian/"+User_id+"/" +fileName;
                            Toast.makeText(crmcapture.this, "正在上传文件。。。请等待", Toast.LENGTH_SHORT).show();//Post成功
                            uploadFile();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    crmcapture.this.finish();

                }
            }).show();
            // 重新浏览
            camera.stopPreview();
            camera.startPreview();
            isPreview = true;
        }
    };
}

