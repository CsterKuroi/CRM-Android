package com.example.renxin.shujia.karl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.renxin.shujia.R;
import com.example.renxin.shujia.UrlTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import java.io.File;

public class downfile extends AppCompatActivity {

    String path;
    String  url;
    TextView textview;

    LinearLayout start;
    LinearLayout cancel;

    public void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
            textview.setText(file + "删除成功！" + "\n");
        } else {
            textview.setText("文件不存在！"+"\n");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downfile);
        Intent intent = getIntent();
        url  = intent.getStringExtra("url");
        path = intent.getStringExtra("path");
        textview = (TextView) findViewById(R.id.text);
        downFile(url,path);
        ImageView back = (ImageView)findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        start = (LinearLayout) findViewById(R.id.ll_login_qq);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downFile(url,path);
            }
        });
        cancel = (LinearLayout) findViewById(R.id.ll_login_sina);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/mnt/sdcard/crmstudy/" + path);
                if(file.exists())
                file.delete();
            }
        });
    }


    public void downFile(String filename,String destfile)
    {
        String savefile =  "/mnt/sdcard/crmstudy/";
        File destDir = new File(savefile);

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        FinalHttp fh = new FinalHttp();
        final HttpHandler handler = fh.download(UrlTools.download_url+filename, //这里是下载的路径
                savefile + destfile, //这是保存到本地的路径
                new AjaxCallBack<File>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        textview.setText("开始下载,请稍等....！");
                    }

                    @Override
                    public void onLoading(long count, long current) {
                        super.onLoading(count, current);

                        textview.setText("下载进度：" + 100*current/count + "%" );
                    }

                    @Override
                    public void onSuccess(File file) {
                        super.onSuccess(file);
                        textview.setText(">>>>>>>>..恭喜，下载成功!两秒钟后自动 关闭本页面....<<<<<<<< " );
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        textview.setText(">>>>>>>>.下载失败，请检查网络！....<<<<<<<< ");
                        File file = new File("/mnt/sdcard/crmstudy/" + path);
                        if(file.exists())
                            file.delete();
                    }
                });

    }

}
