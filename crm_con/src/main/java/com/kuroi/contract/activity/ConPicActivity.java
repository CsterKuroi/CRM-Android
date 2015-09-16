package com.kuroi.contract.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuroi.contract.R;
import com.kuroi.contract.model.Contract;
import com.kuroi.contract.service.ConPic;
import com.kuroi.contract.service.ConService;
import com.kuroi.contract.service.ConUPLOAD;
import com.kuroi.contract.service.ConUploadCallBack;
import com.ricky.database.CenterDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.kuroi.contract.activity.ConShowActivity.getExifOrientation;

public class ConPicActivity extends Activity implements ConUploadCallBack{

    private ImageButton ib1;
    private ImageButton ib2;
    private ImageButton ib3;
    private Button btt1;
    private Button btt2;
    private String picName="";
    private String userID="101";
    private Contract cc;
    private ConService service=null;

    private ImageView iv13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_con_pic);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        service = new ConService(this);
        cc=new Contract();
        cc.setId(service.getMax()+1);
        ib1=(ImageButton)findViewById(R.id.imageView3);
        ib2=(ImageButton)findViewById(R.id.imageView4);
        ib3=(ImageButton)findViewById(R.id.imageView5);
        btt1=(Button)findViewById(R.id.button);
        btt2=(Button)findViewById(R.id.button2);
        iv13=(ImageView)findViewById(R.id.imageView13);
        ib1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = getOutputMediaFileUri("100"); // create a file to save the image
                Log.d("pn1", fileUri.toString());
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent2, 100);
            }
        });
        ib2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = getOutputMediaFileUri("200"); // create a file to save the image
                Log.d("pn2", fileUri.toString());
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent2, 200);
            }
        });
        ib3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = getOutputMediaFileUri("300"); // create a file to save the image
                Log.d("pn3", fileUri.toString());
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent2, 300);
            }
        });
        btt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadToServer();
            }
        });
        btt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Contract");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }
                File f1 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"100.jpg");
                File f2 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"200.jpg");
                File f3 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"300.jpg");
                if (f1.exists())  // 判断文件是否存在
                    f1.delete();
                if (f2.exists())  // 判断文件是否存在
                    f2.delete();
                if (f2.exists())  // 判断文件是否存在
                    f2.delete();
                finish();
            }
        });
        iv13.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Contract");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }
                File f1 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"100.jpg");
                File f2 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"200.jpg");
                File f3 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"300.jpg");
                if (f1.exists())  // 判断文件是否存在
                    f1.delete();
                if (f2.exists())  // 判断文件是否存在
                    f2.delete();
                if (f2.exists())  // 判断文件是否存在
                    f2.delete();
                finish();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Contract");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return false;
                }
            }
            File f1 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"100.jpg");
            File f2 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"200.jpg");
            File f3 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+cc.getId()+"_"+"300.jpg");
            if (f1.exists())  // 判断文件是否存在
                f1.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            finish();
            return false;
        }
        return false;
    }
    public Uri getOutputMediaFileUri(String imb) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Contract");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        picName = userID+"_" + cc.getId()+"_"+imb+".jpg";
        Log.d("pn", picName);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + picName);
        return Uri.fromFile(mediaFile);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            Log.d("rrr","ok");
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d("rrrr", picName);
                    cc.setImg(picName);
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Contract");
                    String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + cc.getImg();
                    if(new File(uploadFile).isFile()){
                        int digree=getExifOrientation(uploadFile);
                        Bitmap bm;
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 10;
                        bm = BitmapFactory.decodeFile(uploadFile, option);
                        if (digree != 0) {
                            // 旋转图片
                            Matrix m = new Matrix();
                            m.postRotate(digree);
                            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                    bm.getHeight(), m, true);
                        }
                        ib1.setImageBitmap(bm);
                        ib1.setClickable(false);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("rrrrrrr", "nonnnnnnnnnnnnnnnnnnnnn");
                    break;
            }
        }
        else if (requestCode == 200) {
            Log.d("rrr","ok");
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d("rrrr", picName);
                    cc.setImg2(picName);
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Contract");
                    String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + cc.getImg2();
                    if(new File(uploadFile).isFile()) {
                        int digree = getExifOrientation(uploadFile);
                        Bitmap bm;
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 10;
                        bm = BitmapFactory.decodeFile(uploadFile, option);
                        if (digree != 0) {
                            // 旋转图片
                            Matrix m = new Matrix();
                            m.postRotate(digree);
                            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                    bm.getHeight(), m, true);
                        }
                        ib2.setImageBitmap(bm);
                        ib2.setClickable(false);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("rrrrrrr", "nonnnnnnnnnnnnnnnnnnnnn");
                    break;
            }
        }
        else if (requestCode == 300) {
            Log.d("rrr","ok");
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d("rrrr", picName);
                    cc.setImg3(picName);
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Contract");
                    String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + cc.getImg3();
                    if(new File(uploadFile).isFile()) {
                        int digree = getExifOrientation(uploadFile);
                        Bitmap bm;
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 10;
                        bm = BitmapFactory.decodeFile(uploadFile, option);
                        if (digree != 0) {
                            // 旋转图片
                            Matrix m = new Matrix();
                            m.postRotate(digree);
                            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                    bm.getHeight(), m, true);
                        }
                        ib3.setImageBitmap(bm);
                        ib3.setClickable(false);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("rrrrrrr", "nonnnnnnnnnnnnnnnnnnnnn");
                    break;
            }
        }
    }
    private void uploadToServer() {
        ConUPLOAD upload = new ConUPLOAD(this, userID);
        String JSONString = upload.changeArrayDateToJson(cc);
        if(!cc.getImg().equals("")) {
            ConPic uploadThread1 = new ConPic(cc.getImg());
            uploadThread1.start();
        }
        if(!cc.getImg2().equals("")) {
            ConPic uploadThread2 = new ConPic(cc.getImg2());
            uploadThread2.start();
        }
        if(!cc.getImg3().equals("")) {
            ConPic uploadThread3 = new ConPic(cc.getImg3());
            uploadThread3.start();
        }
        if(cc.getImg3().equals("")&&cc.getImg2().equals("")&&cc.getImg().equals("")) {
            Toast.makeText(this, "没有图片", Toast.LENGTH_LONG).show();
            return;
        }
        upload.up(JSONString);
    }

    public void uploadCallBack(String payload) {
        if (payload.equals("1")) {
            boolean flag = service.save(cc);
            if(flag) {
                Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
                finish();
            }
            else
                Toast.makeText(this, "添加失败,请检查网络", Toast.LENGTH_LONG).show();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "添加失败,请检查网络", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_con_pic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
