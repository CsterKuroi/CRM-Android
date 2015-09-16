package com.melnykov.fab.sample.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class Imageviewcluss extends ImageView {

    public Imageviewcluss(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Imageviewcluss(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Imageviewcluss(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取控件需要重新绘制的区域
        Rect rect=canvas.getClipBounds();
        rect.bottom--;
        rect.right--;
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawRect(rect, paint);
    }

}