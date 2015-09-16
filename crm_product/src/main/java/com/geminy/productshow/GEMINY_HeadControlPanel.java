package com.geminy.productshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geminy.productshow.Activity.GEMINY_MainActivity;
import com.geminy.productshow.R;




/**
 * Created by Yanhao on 15-7-29.
 */
public class GEMINY_HeadControlPanel extends LinearLayout {

    private Activity mContext;
    private TextView mMidleTitle;
    private LinearLayout mRightTitle;
    private LinearLayout mLeftTitle;
    private TextView left_text;
    private TextView right_text;
    private static final int default_background_color = Color.rgb(255, 255, 255);

    public GEMINY_HeadControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=(Activity)context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        left_text=(TextView)findViewById(R.id.left_text);
        right_text=(TextView)findViewById(R.id.right_text);
        mMidleTitle = (TextView)findViewById(R.id.middle_title);
        mRightTitle = (LinearLayout)findViewById(R.id.btn_head_right);
        mLeftTitle=(LinearLayout)findViewById(R.id.btn_head_left);
       // setBackgroundColor(default_background_color);
        super.onFinishInflate();
    }
    public void setMiddleTitle(String s){
        mMidleTitle.setText(s);
    }
    public void setRightTitle(String s){
        mRightTitle.setVisibility(VISIBLE);
        right_text.setText(s);
    }
    public void setLeftTitle(){
        mLeftTitle.setVisibility(VISIBLE);
        left_text.setText("返回");

    }



}
