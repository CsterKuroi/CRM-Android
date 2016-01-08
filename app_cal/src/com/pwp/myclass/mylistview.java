package com.pwp.myclass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by dell on 2015/9/3.
 */
public class mylistview extends ListView {
        public mylistview(Context context) {
            super(context);
        }

        public mylistview(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public mylistview(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
