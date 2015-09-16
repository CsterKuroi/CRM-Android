package com.melnykov.fab.sample.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.melnykov.fab.sample.R;

public class lianxiren_popupwindow extends PopupWindow implements View.OnTouchListener{
    private View conentView;
    private Context context;
    String flag=null;
    LinearLayout l1;
    LinearLayout l2;
    LinearLayout l3;
    public lianxiren_popupwindow(final Activity context) {
        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.crm_lianxiren_popupwindow, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        //this.setWidth(w / 2 + 50);
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果

       // this.setAnimationStyle(R.style.AnimationPreview);
        l1=(LinearLayout)conentView.findViewById(R.id.l1);
        l2=(LinearLayout)conentView.findViewById(R.id.l2);
        l3=(LinearLayout)conentView.findViewById(R.id.l3);
        l1.setOnTouchListener(this);
        l2.setOnTouchListener(this);
        l3.setOnTouchListener(this);

    }
    public boolean onTouch(View v,MotionEvent e){
          switch (e.getAction()){
              case  MotionEvent.ACTION_DOWN:
                  if(v.getId()==R.id.l1){
                      l1.setBackgroundColor(Color.LTGRAY);
                  }
                  if(v.getId()==R.id.l2){
                      l2.setBackgroundColor(Color.LTGRAY);
                  }
                  if(v.getId()==R.id.l3){
                      l3.setBackgroundColor(Color.LTGRAY);
                  }
              break;
              case  MotionEvent.ACTION_UP:
                  if(v.getId()==R.id.l1){
                      l1.setBackgroundColor(Color.WHITE);

//                      Toast.makeText(context, "ok", Toast.LENGTH_LONG).show();
                      this.dismiss();

                  }
                  if(v.getId()==R.id.l2){
                      l2.setBackgroundColor(Color.WHITE);

//                      Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();
                      this.dismiss();

                  }
                  if(v.getId()==R.id.l3){
                      l3.setBackgroundColor(Color.WHITE);

//                      Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();
                      this.dismiss();

                  }
                  break;
              case  MotionEvent.ACTION_CANCEL:
                  if(v.getId()==R.id.l1){
                      l1.setBackgroundColor(Color.WHITE);
                  }
                  if(v.getId()==R.id.l2){
                      l1.setBackgroundColor(Color.WHITE);
                  }
                  if(v.getId()==R.id.l3){
                      l1.setBackgroundColor(Color.WHITE);
                  }
                  break;
          }

        return true;
    }
    public void showPopupWindow(View parent) {
    	 int[] location = new int[2];  
    	 parent.getLocationOnScreen(location);  
        if (!this.isShowing()) {
            //this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
            //this.showAsDropDown(parent, parent.getLayoutParams().width / 2-80, -30);
        	this.showAtLocation(parent,Gravity.NO_GRAVITY, location[0], location[1]+150);
        } else {
            this.dismiss();
        }
    }
}
