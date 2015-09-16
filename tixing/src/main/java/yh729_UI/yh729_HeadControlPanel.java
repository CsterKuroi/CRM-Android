package yh729_UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yanhao.task729.R;

import yh729_activity.yh729_SettingActivity;


/**
 * Created by Yanhao on 15-7-29.
 */
public class yh729_HeadControlPanel extends LinearLayout {

    private Activity mContext;
    private TextView mMidleTitle;
    private TextView mRightTitle;
    private LinearLayout mLeftTitle;
    private TextView left_text;
    private static final int default_background_color = Color.rgb(255, 255, 255);

    public yh729_HeadControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=(Activity)context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        left_text=(TextView)findViewById(R.id.left_text);
        mMidleTitle = (TextView)findViewById(R.id.midle_title);
        mRightTitle = (TextView)findViewById(R.id.btn_head_right);
        mLeftTitle=(LinearLayout)findViewById(R.id.btn_head_left);
        mLeftTitle.setVisibility(INVISIBLE);
        super.onFinishInflate();
    }
    public void setMiddleTitle(String s){
        mMidleTitle.setText(s);
    }
    public void setmRightTitle(String s){
        mRightTitle.setVisibility(VISIBLE);
        mRightTitle.setText(s);
        mRightTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getContext(), yh729_SettingActivity.class);
                    getContext().startActivity(intent);
                }catch (Exception e){
                    Log.i("test", e.toString());
                }
            }
        });
    }
    public void setLeftTitle(){
        mLeftTitle.setVisibility(VISIBLE);
        left_text.setText("返回");
        mLeftTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onBackPressed();
            }
        });

    }



}
