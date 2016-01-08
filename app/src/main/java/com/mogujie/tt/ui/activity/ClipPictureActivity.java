package com.mogujie.tt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.mogujie.tt.R;
import com.mogujie.tt.config.UrlConstant;
import com.mogujie.tt.ui.widget.ClipImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipPictureActivity extends Activity implements OnTouchListener,
        OnClickListener {
    private ImageView srcPic;
    private View sure;
    private ClipImageView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int PHOTO_CLIP = 119;// 截取图片
    private String picname;
    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt_activity_clip_picture);

        srcPic = (ImageView) this.findViewById(R.id.src_pic);
        srcPic.setOnTouchListener(this);

        Intent in = getIntent();
        if (in != null) {
            byte[] bis = in.getByteArrayExtra("bitmap");
            picname = in.getStringExtra("picname");
            //bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            Bitmap bitmaptemp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image.jpg");
            bitmap = zoomBitmap(bitmaptemp, bitmaptemp.getWidth()/3, bitmaptemp.getHeight() / 3);
            ViewTreeObserver observer = srcPic.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                public void onGlobalLayout() {
                    srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    initClipView(srcPic.getTop());
                }
            });
        }

        sure = (View) this.findViewById(R.id.sure);
        sure.setOnClickListener(this);
    }

    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        clipview = new ClipImageView(ClipPictureActivity.this);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new ClipImageView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
            }
        });

        this.addContentView(clipview, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void onClick(View v) {
        Bitmap clipBitmap = getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();

        savePicture(picname, clipBitmap);
        //gotopre(bitmapByte);
        setResult(PHOTO_CLIP);
        ClipPictureActivity.this.finish();
    }

    private void gotopre(byte[] bitmapByte) {
//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(), PreviewClipResultActivity.class);
//        intent.putExtra("bitmap", bitmapByte);
//
//        startActivity(intent);
    }

    /**保存图片到本应用下**/
    private void savePicture(String fileName,Bitmap bitmap) {

        FileOutputStream fos =null;
        //try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(UrlConstant.TOUXIANG_ADD2);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(UrlConstant.TOUXIANG_ADD2, fileName); //在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                        + statusBarHeight, clipview.getClipWidth(),
                clipview.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

}