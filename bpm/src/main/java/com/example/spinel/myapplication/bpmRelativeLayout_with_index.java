package com.example.spinel.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Spinel on 2015/7/21.
 */
public class bpmRelativeLayout_with_index extends RelativeLayout{

    public int index;

    public bpmRelativeLayout_with_index(Context context){
        super(context);
    }

    public bpmRelativeLayout_with_index(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public bpmRelativeLayout_with_index(Context context, AttributeSet attrs,
                                        int defStyle) {
        super(context, attrs, defStyle);
    }


}
