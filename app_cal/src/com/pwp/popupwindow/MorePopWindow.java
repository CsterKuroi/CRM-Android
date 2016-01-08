package com.pwp.popupwindow;
        import android.app.Activity;

        import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
        import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
        import android.widget.Button;
        import android.widget.PopupWindow;
        import android.view.Gravity;

        
/*import com.mogujie.tt.R;*/
        import com.pwp.activity.CalendarConvert;
        import com.pwp.activity.R;
import com.pwp.activity.ScheduleAll;

public class MorePopWindow extends PopupWindow {
    private View conentView;
    String flag=null;
    public MorePopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.calendar_popupwindow, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        //this.setWidth(w / 2 + 50);
        this.setWidth(LayoutParams.WRAP_CONTENT);
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
        this.setAnimationStyle(R.style.AnimationPreview);

      Button btn_add=(Button)conentView.findViewById(R.id.btn_add);
        btn_add.setFocusable(true); btn_add.setClickable(true);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag="add";
                Intent intent =new Intent();
                intent.setAction(flag);
                context.sendBroadcast(intent);

                dismiss();
            }
        });

        Button all=(Button)conentView.findViewById(R.id.text_allschedule);
        all.setClickable(true); all.setFocusable(true);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ScheduleAll.class);
                context.startActivity(intent);
                dismiss();
            }
        });

        Button today=(Button)conentView.findViewById(R.id.text_today);
        today.setFocusable(true);today.setClickable(true);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent();
                intent.setAction("todayfrompopup");
                context.sendBroadcast(intent);
*/
                flag = "today";
                Intent intent = new Intent();
                intent.setAction(flag);
                context.sendBroadcast(intent);

                dismiss();

            }
        });

        Button jump=(Button)conentView.findViewById(R.id.text_jump);
        jump.setFocusable(true);jump.setClickable(true);
        jump.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setClass(context, CalendarActivity.class);
                context.startActivity(intent);*/
                flag="jump";
                Intent intent = new Intent();
                intent.setAction(flag);
                context.sendBroadcast(intent);

                dismiss();
            }
        });
        Button datechange=(Button)conentView.findViewById(R.id.btn_datechange);
        datechange.setFocusable(true);datechange.setClickable(true);
        datechange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setClass(context, CalendarActivity.class);
                context.startActivity(intent);*/
                Intent intent1 = new Intent();
                intent1.setClass(context, CalendarConvert.class);
                intent1.putExtra("date", new int[] { 2015, 7, 29 });
                context.startActivity(intent1);
                dismiss();

            }
        });
/*        Button cancel=(Button)conentView.findViewById(R.id.btn_cancel);
        cancel.setFocusable(true);cancel.setClickable(true);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, CalendarActivity.class);
                context.startActivity(intent);
                flag="cancel";
                Intent intent = new Intent();
                intent.setAction(flag);
                context.sendBroadcast(intent);
            }
        });
*/


    }
/*    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.main);

    }*/
   

    public void showPopupWindow(View parent) {
    	 int[] location = new int[2];
    	 parent.getLocationOnScreen(location);  
        if (!this.isShowing()) {
            //this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
            //this.showAsDropDown(parent, parent.getLayoutParams().width / 2-80, -30);
        	this.showAtLocation(parent,Gravity.NO_GRAVITY, location[0]+parent.getWidth(), location[1]+parent.getHeight());
        } else {
            this.dismiss();
        }
    }



}
