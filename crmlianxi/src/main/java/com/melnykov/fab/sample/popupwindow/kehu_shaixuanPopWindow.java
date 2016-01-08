package com.melnykov.fab.sample.popupwindow;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;
import android.view.Gravity;

        import com.melnykov.fab.sample.R;

public class kehu_shaixuanPopWindow extends PopupWindow implements View.OnTouchListener,View.OnClickListener {
    private View conentView;
    private Context context;
    String flag=null;

    LinearLayout qiye;
    ScrollView scrollView_qiye;
    LinearLayout qiye_layout;
    LinearLayout qiye1;
    LinearLayout qiye2;
    LinearLayout qiye3;
    LinearLayout qiye4;
    CheckBox qiye_cb1;
    CheckBox qiye_cb2;
    CheckBox qiye_cb3;
    CheckBox qiye_cb4;
    int qiye_flag1=0;
    int qiye_flag2=0;
    int qiye_flag3=0;
    int qiye_flag4=0;

    LinearLayout kehuleixing;
    ScrollView scrollView_kehuleixing;
    LinearLayout kehuleixing_layout;
    LinearLayout kehuleixing1;
    LinearLayout kehuleixing2;
    LinearLayout kehuleixing3;
    LinearLayout kehuleixing4;
    LinearLayout kehuleixing5;
    CheckBox kehuleixing_cb1;
    CheckBox kehuleixing_cb2;
    CheckBox kehuleixing_cb3;
    CheckBox kehuleixing_cb4;
    CheckBox kehuleixing_cb5;
    int kehuleixing_flag1=0;
    int kehuleixing_flag2=0;
    int kehuleixing_flag3=0;
    int kehuleixing_flag4=0;
    int kehuleixing_flag5=0;

    LinearLayout kehuzhuangtai;
    ScrollView scrollView_kehuzhuangtai;
    LinearLayout kehuzhuangtai_layout;

    LinearLayout kehufenji;
    ScrollView scrollView_kehufenji;
    LinearLayout kehufenji_layout;

    Button b1;
    Button b2;

    public kehu_shaixuanPopWindow(final Activity context) {
        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.crm_kehu_shaixuanpopupwindow, null);
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
        b1=(Button)conentView.findViewById(R.id.qingkong);
        b2=(Button)conentView.findViewById(R.id.queding);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

         qiye= (LinearLayout)conentView.findViewById(R.id.layout_qiye);
         scrollView_qiye=(ScrollView)conentView.findViewById(R.id.scrollView_qiye);
         qiye_layout=(LinearLayout)conentView.findViewById(R.id.scrollView_qiye_layout);
         qiye.setOnTouchListener(this);
         qiye1=(LinearLayout)qiye_layout.findViewById(R.id.scrollView_qiye_layout_suoyouqiye);
         qiye1.setOnTouchListener(this);
         qiye_cb1=(CheckBox)qiye_layout.findViewById(R.id.scrollView_qiye_layout_suoyouqiye_checkbox);

         qiye2=(LinearLayout)qiye_layout.findViewById(R.id.scrollView_qiye_layout_guoqi);
         qiye2.setOnTouchListener(this);
         qiye_cb2=(CheckBox)qiye_layout.findViewById(R.id.scrollView_qiye_layout_guoqi_checkbox);

         qiye3=(LinearLayout)qiye_layout.findViewById(R.id.scrollView_qiye_layout_siqi);
         qiye3.setOnTouchListener(this);
         qiye_cb3=(CheckBox)qiye_layout.findViewById(R.id.scrollView_qiye_layout_siqi_checkbox);

         qiye4=(LinearLayout)qiye_layout.findViewById(R.id.scrollView_qiye_layout_waizi);
         qiye4.setOnTouchListener(this);
         qiye_cb4=(CheckBox)qiye_layout.findViewById(R.id.scrollView_qiye_layout_waizi_checkbox);

        kehuleixing= (LinearLayout)conentView.findViewById(R.id.layout_kehuleixing);
        scrollView_kehuleixing=(ScrollView)conentView.findViewById(R.id.scrollView_kehuleixing);
        kehuleixing_layout=(LinearLayout)conentView.findViewById(R.id.scrollView_kehuleixing_layout);
        kehuleixing.setOnTouchListener(this);
        kehuleixing1=(LinearLayout)kehuleixing_layout.findViewById(R.id.scrollView_kehuleixing_layout_suoyoukehu);
        kehuleixing1.setOnTouchListener(this);
        kehuleixing_cb1=(CheckBox)kehuleixing1.findViewById(R.id.scrollView_kehuleixing_layout_suoyoukehu_checkbox);

        kehuleixing2=(LinearLayout)kehuleixing_layout.findViewById(R.id.scrollView_kehuleixing_layout_xiansuokehu);
        kehuleixing2.setOnTouchListener(this);
        kehuleixing_cb2 =(CheckBox)kehuleixing2.findViewById(R.id.scrollView_kehuleixing_layout_xiansuokehu_checkbox);

        kehuleixing3=(LinearLayout)kehuleixing_layout.findViewById(R.id.scrollView_kehuleixing_layout_qianzaikehu);
        kehuleixing3.setOnTouchListener(this);
        kehuleixing_cb3 =(CheckBox)kehuleixing3.findViewById(R.id.scrollView_kehuleixing_layout_qianzaikehu_checkbox);

        kehuleixing4=(LinearLayout)kehuleixing_layout.findViewById(R.id.scrollView_kehuleixing_layout_chengjiaokehu);
        kehuleixing4.setOnTouchListener(this);
        kehuleixing_cb4 =(CheckBox)kehuleixing4.findViewById(R.id.scrollView_kehuleixing_layout_chengjiaokehu_checkbox);

        kehuleixing5=(LinearLayout)kehuleixing_layout.findViewById(R.id.scrollView_kehuleixing_layout_gonghaikehu);
        kehuleixing5.setOnTouchListener(this);
        kehuleixing_cb5 =(CheckBox)kehuleixing5.findViewById(R.id.scrollView_kehuleixing_layout_gonghaikehu_checkbox);


        kehuzhuangtai= (LinearLayout)conentView.findViewById(R.id.layout_kehuzhuangtai);
        scrollView_kehuzhuangtai=(ScrollView)conentView.findViewById(R.id.scrollView_kehuzhuangtai);
        kehuzhuangtai_layout=(LinearLayout)conentView.findViewById(R.id.scrollView_kehuzhuangtai_layout);
        kehuzhuangtai.setOnTouchListener(this);

        kehufenji= (LinearLayout)conentView.findViewById(R.id.layout_kehufenji);
        scrollView_kehufenji=(ScrollView)conentView.findViewById(R.id.scrollView_kehufenji);
        kehufenji_layout=(LinearLayout)conentView.findViewById(R.id.scrollView_kehufenji_layout);
        kehufenji.setOnTouchListener(this);


    }
    String select="";
    public void onClick(View v){
        select="";
        if(v.getId()==R.id.qingkong){
            qiye_cb1.setChecked(false);
            qiye_flag1 = 0;
            qiye_cb2.setChecked(false);
            qiye_flag2 = 0;
            qiye_cb3.setChecked(false);
            qiye_flag3 = 0;
            qiye_cb4.setChecked(false);
            qiye_flag4 = 0;
            kehuleixing_cb1.setChecked(false);
            kehuleixing_flag1 = 0;
            kehuleixing_cb2.setChecked(false);
            kehuleixing_flag2 = 0;
            kehuleixing_cb3.setChecked(false);
            kehuleixing_flag3 = 0;
            kehuleixing_cb4.setChecked(false);
            kehuleixing_flag4 = 0;
            kehuleixing_cb5.setChecked(false);
            kehuleixing_flag5 = 0;
           // Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();
        }
         if(v.getId()==R.id.queding){
             //if(qiye_flag1==1)  select+="所有企业";
         if(qiye_cb2.isChecked())  select+="国企;";
         if(qiye_cb3.isChecked())  select+="民企;";
         if(qiye_cb4.isChecked())  select+="外资;";


       // if(qiye_flag1==1)  select+="所有客户";
        if(kehuleixing_cb2.isChecked())  select+="线索客户;";
        if(kehuleixing_cb3.isChecked())  select+="潜在客户;";
        if(kehuleixing_cb4.isChecked())  select+="成交客户;";
        if(kehuleixing_cb5.isChecked())  select+="公海客户";
             Intent intent=new Intent();
             intent.putExtra("select",select);
             intent.setAction("popshaixuan");
             context.sendBroadcast(intent);
             this.dismiss();
         }


    }

    public boolean onTouch(View v,MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.layout_qiye) {
                    qiye.setBackgroundColor(Color.LTGRAY);
                    kehuleixing.setBackgroundColor(Color.WHITE);
                    kehuzhuangtai.setBackgroundColor(Color.WHITE);
                    kehufenji.setBackgroundColor(Color.WHITE);

                      /*  scrollView_qiye.setBackgroundColor(Color.LTGRAY);
                        scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                        scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                        scrollView_kehufenji.setBackgroundColor(Color.WHITE);*/
                } else if (v.getId() == R.id.layout_kehuleixing) {
                    qiye.setBackgroundColor(Color.WHITE);
                    kehuleixing.setBackgroundColor(Color.LTGRAY);
                    kehuzhuangtai.setBackgroundColor(Color.WHITE);
                    kehufenji.setBackgroundColor(Color.WHITE);

                     /*   scrollView_qiye.setBackgroundColor(Color.WHITE);
                        scrollView_kehuleixing.setBackgroundColor(Color.LTGRAY);
                        scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                        scrollView_kehufenji.setBackgroundColor(Color.WHITE);*/
                } else if (v.getId() == R.id.layout_kehuzhuangtai) {
                    qiye.setBackgroundColor(Color.WHITE);
                    kehuleixing.setBackgroundColor(Color.WHITE);
                    kehuzhuangtai.setBackgroundColor(Color.LTGRAY);
                    kehufenji.setBackgroundColor(Color.WHITE);

                       /* scrollView_qiye.setBackgroundColor(Color.WHITE);
                        scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                        scrollView_kehuzhuangtai.setBackgroundColor(Color.LTGRAY);
                        scrollView_kehufenji.setBackgroundColor(Color.WHITE);*/
                } else if (v.getId() == R.id.layout_kehufenji) {
                    qiye.setBackgroundColor(Color.WHITE);
                    kehuleixing.setBackgroundColor(Color.WHITE);
                    kehuzhuangtai.setBackgroundColor(Color.WHITE);
                    kehufenji.setBackgroundColor(Color.LTGRAY);

                      /*  scrollView_qiye.setBackgroundColor(Color.WHITE);
                        scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                        scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                        scrollView_kehufenji.setBackgroundColor(Color.LTGRAY);*/
                }
                break;
                    case MotionEvent.ACTION_UP:
                        if (v.getId() == R.id.layout_qiye) {

                            scrollView_qiye.setVisibility(View.VISIBLE);
                            scrollView_kehuleixing.setVisibility(View.GONE);
                            scrollView_kehuzhuangtai.setVisibility(View.GONE);
                            scrollView_kehufenji.setVisibility(View.GONE);

                            // LinearLayout
                        } else if (v.getId() == R.id.layout_kehuleixing) {
                            scrollView_qiye.setVisibility(View.GONE);
                            scrollView_kehuleixing.setVisibility(View.VISIBLE);
                            scrollView_kehuzhuangtai.setVisibility(View.GONE);
                            scrollView_kehufenji.setVisibility(View.GONE);
                        } else if (v.getId() == R.id.layout_kehuzhuangtai) {
                            scrollView_qiye.setVisibility(View.GONE);
                            scrollView_kehuleixing.setVisibility(View.GONE);
                            scrollView_kehuzhuangtai.setVisibility(View.VISIBLE);
                            scrollView_kehufenji.setVisibility(View.GONE);
                        } else if (v.getId() == R.id.layout_kehufenji) {
                            scrollView_qiye.setVisibility(View.GONE);
                            scrollView_kehuleixing.setVisibility(View.GONE);
                            scrollView_kehuzhuangtai.setVisibility(View.GONE);
                            scrollView_kehufenji.setVisibility(View.VISIBLE);
                        } else if (v.getId() == R.id.scrollView_qiye_layout_suoyouqiye) {
                            if (qiye_flag1 == 1) {
                                qiye_cb1.setChecked(false);
                                qiye_flag1 = 0;
                                qiye_cb2.setChecked(false);
                                qiye_flag2 = 0;
                                qiye_cb3.setChecked(false);
                                qiye_flag3 = 0;
                                qiye_cb4.setChecked(false);
                                qiye_flag4 = 0;
                            } else {
                                qiye_cb1.setChecked(true);
                                qiye_flag1 = 1;
                                qiye_cb2.setChecked(true);
                                qiye_flag2 = 1;
                                qiye_cb3.setChecked(true);
                                qiye_flag3 = 1;
                                qiye_cb4.setChecked(true);
                                qiye_flag4 = 1;
                            }

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_qiye_layout_guoqi) {
                            if (qiye_flag1 == 1) {
                                qiye_cb1.setChecked(false);
                                qiye_flag1 = 0;
                                qiye_flag2 = 1;
                                qiye_cb2.setChecked(true);
                                qiye_cb3.setChecked(false);
                                qiye_flag2 = 0;
                                qiye_cb4.setChecked(false);
                                qiye_flag2 = 0;
                            } else {
                                qiye_flag2 = 1;
                                qiye_cb2.setChecked(true);
                            }
                                /*if(qiye_flag2==0){
                                    qiye_cb2.setChecked(true);
                                    qiye_flag2=1;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb3.setChecked(false); qiye_flag3=0;
                                    qiye_cb4.setChecked(false); qiye_flag4=0;
                                }else {
                                    qiye_cb2.setChecked(false);
                                    qiye_flag2=0;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb3.setChecked(false); qiye_flag3=0;
                                    qiye_cb4.setChecked(false); qiye_flag4=0;
                                }*/


//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_qiye_layout_siqi) {
                            if (qiye_flag1 == 1) {
                                qiye_cb1.setChecked(false);
                                qiye_flag1 = 0;
                                qiye_flag3 = 1;
                                qiye_cb3.setChecked(true);
                                qiye_cb2.setChecked(false);
                                qiye_flag2 = 0;
                                qiye_cb4.setChecked(false);
                                qiye_flag2 = 0;
                            } else {
                                qiye_flag3 = 1;
                                qiye_cb3.setChecked(true);
                            }
                               /* if(qiye_flag3==0){
                                    qiye_cb3.setChecked(true);
                                    qiye_flag3=1;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb2.setChecked(false); qiye_flag2=0;
                                    qiye_cb4.setChecked(false); qiye_flag4=0;
                                }else {
                                    qiye_cb3.setChecked(false);
                                    qiye_flag3=0;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb2.setChecked(false); qiye_flag2=0;
                                    qiye_cb4.setChecked(false); qiye_flag4=0;
                                }*/
//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_qiye_layout_waizi) {
                            if (qiye_flag1 == 1) {
                                qiye_cb1.setChecked(false);
                                qiye_flag1 = 0;
                                qiye_flag4 = 1;
                                qiye_cb4.setChecked(true);
                                qiye_cb2.setChecked(false);
                                qiye_cb3.setChecked(false);
                                ;
                            } else {
                                qiye_flag4 = 1;
                                qiye_cb4.setChecked(true);
                            }
                              /*  if(qiye_flag4==0){
                                    qiye_cb4.setChecked(true);
                                    qiye_flag4=1;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb3.setChecked(false); qiye_flag3=0;
                                    qiye_cb2.setChecked(false); qiye_flag2=0;
                                }else {
                                    qiye_cb2.setChecked(false);
                                    qiye_flag2=0;
                                    qiye_cb1.setChecked(false); qiye_flag1=0;
                                    qiye_cb3.setChecked(false); qiye_flag3=0;
                                    qiye_cb4.setChecked(false); qiye_flag4=0;
                                }*/

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_kehuleixing_layout_suoyoukehu) {
                            if (kehuleixing_flag1 == 1) {
                                kehuleixing_cb1.setChecked(false);
                                kehuleixing_flag1 = 0;
                                kehuleixing_cb2.setChecked(false);
                                kehuleixing_flag2 = 0;
                                kehuleixing_cb3.setChecked(false);
                                kehuleixing_flag3 = 0;
                                kehuleixing_cb4.setChecked(false);
                                kehuleixing_flag4 = 0;
                                kehuleixing_cb5.setChecked(false);
                                kehuleixing_flag5 = 0;
                            } else {
                                kehuleixing_cb1.setChecked(true);
                                kehuleixing_flag1 = 1;
                                kehuleixing_cb2.setChecked(true);
                                kehuleixing_flag2 = 0;
                                kehuleixing_cb3.setChecked(true);
                                kehuleixing_flag3 = 0;
                                kehuleixing_cb4.setChecked(true);
                                kehuleixing_flag4 = 0;
                                kehuleixing_cb5.setChecked(true);
                                kehuleixing_flag5 = 0;
                            }

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_kehuleixing_layout_xiansuokehu) {
                            if (kehuleixing_flag1 == 0) {
                                kehuleixing_cb2.setChecked(true);
                                kehuleixing_flag2 = 1;
                            } else {
                                kehuleixing_cb1.setChecked(false);
                                kehuleixing_flag1 = 0;
                                kehuleixing_cb2.setChecked(true);
                                kehuleixing_flag2 = 1;
                                kehuleixing_cb3.setChecked(false);
                                kehuleixing_flag3 = 0;
                                kehuleixing_cb4.setChecked(false);
                                kehuleixing_flag4 = 0;
                                kehuleixing_cb5.setChecked(false);
                                kehuleixing_flag5 = 0;
                            }
                            /*    if(kehuleixing_flag2==0){
                                    kehuleixing_cb2.setChecked(true);
                                    kehuleixing_flag2=1;
                                    kehuleixing_cb1.setChecked(false);
                                    kehuleixing_cb3.setChecked(false);
                                    kehuleixing_cb4.setChecked(false);
                                    kehuleixing_cb5.setChecked(false);
                                }else {
                                    kehuleixing_cb2.setChecked(false);
                                    kehuleixing_flag2=0;
                                    kehuleixing_cb2.setChecked(false);
                                    kehuleixing_cb3.setChecked(false);
                                    kehuleixing_cb4.setChecked(false);
                                    kehuleixing_cb5.setChecked(false);
                                }*/

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_kehuleixing_layout_qianzaikehu) {
                            // kehuleixing3
                            if (kehuleixing_flag1 == 0) {
                                kehuleixing_cb3.setChecked(true);
                                kehuleixing_flag3 = 1;
                            } else {
                                kehuleixing_cb1.setChecked(false);
                                kehuleixing_flag1 = 0;
                                kehuleixing_cb2.setChecked(false);
                                kehuleixing_flag2 = 0;
                                kehuleixing_cb3.setChecked(true);
                                kehuleixing_flag3 = 1;
                                kehuleixing_cb4.setChecked(false);
                                kehuleixing_flag4 = 0;
                                kehuleixing_cb5.setChecked(false);
                                kehuleixing_flag5 = 0;
                            }

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_kehuleixing_layout_chengjiaokehu) {
                            // kehuleixing4
                            if (kehuleixing_flag1 == 0) {
                                kehuleixing_cb4.setChecked(true);
                                kehuleixing_flag4 = 1;
                            } else {
                                kehuleixing_cb1.setChecked(false);
                                kehuleixing_flag1 = 0;
                                kehuleixing_cb2.setChecked(false);
                                kehuleixing_flag2 = 0;
                                kehuleixing_cb3.setChecked(false);
                                kehuleixing_flag3 = 0;
                                kehuleixing_cb4.setChecked(true);
                                kehuleixing_flag4 = 1;
                                kehuleixing_cb5.setChecked(false);
                                kehuleixing_flag5 = 0;
                            }

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();

                        } else if (v.getId() == R.id.scrollView_kehuleixing_layout_gonghaikehu) {
                            // kehuleixing4
                            if (kehuleixing_flag1 == 0) {
                                kehuleixing_cb5.setChecked(true);
                                kehuleixing_flag5 = 1;
                            } else {
                                kehuleixing_cb1.setChecked(false);
                                kehuleixing_flag1 = 0;
                                kehuleixing_cb2.setChecked(false);
                                kehuleixing_flag2 = 0;
                                kehuleixing_cb3.setChecked(false);
                                kehuleixing_flag3 = 0;
                                kehuleixing_cb4.setChecked(false);
                                kehuleixing_flag4 = 0;
                                kehuleixing_cb5.setChecked(true);
                                kehuleixing_flag5 = 1;
                            }

//                                Toast.makeText(context,"ok",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (v.getId() == R.id.layout_qiye) {
                            scrollView_qiye.setBackgroundColor(Color.WHITE);
                            scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                            scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                            scrollView_kehufenji.setBackgroundColor(Color.WHITE);
                        } else if (v.getId() == R.id.layout_kehuleixing) {
                            scrollView_qiye.setBackgroundColor(Color.WHITE);
                            scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                            scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                            scrollView_kehufenji.setBackgroundColor(Color.WHITE);
                        } else if (v.getId() == R.id.layout_kehuzhuangtai) {
                            scrollView_qiye.setBackgroundColor(Color.WHITE);
                            scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                            scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                            scrollView_kehufenji.setBackgroundColor(Color.WHITE);
                        } else if (v.getId() == R.id.layout_kehufenji) {
                            scrollView_qiye.setBackgroundColor(Color.WHITE);
                            scrollView_kehuleixing.setBackgroundColor(Color.WHITE);
                            scrollView_kehuzhuangtai.setBackgroundColor(Color.WHITE);
                            scrollView_kehufenji.setBackgroundColor(Color.WHITE);
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
        	this.showAtLocation(parent,Gravity.NO_GRAVITY, location[0],  location[1]+parent.getHeight()+3);
        } else {
            this.dismiss();
        }
    }
}
