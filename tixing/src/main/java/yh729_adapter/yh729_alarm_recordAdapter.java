package yh729_adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.yanhao.task729.yh729_Constant;
import com.example.yanhao.task729.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yan on 2015/8/9.
 */
public class yh729_alarm_recordAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;

    public yh729_alarm_recordAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public void bindView(View arg0, Context arg1, Cursor arg2) {
        View v = null;
        if (arg0 == null) {
            v = mInflater.inflate(R.layout.yh729_message_item_layout, null);
        } else {
            v = arg0;
        }
        ImageView imageView = (ImageView) v.findViewById(R.id.img_msg_item);
        TextView nameMsg = (TextView) v.findViewById(R.id.name_msg_item);
        TextView contentMsg = (TextView) v.findViewById(R.id.content_msg_item);
        TextView type=(TextView) v.findViewById(R.id.time_msg_item);
        TextView id =(TextView)v.findViewById(R.id.id_msg_item);
        long a = arg2.getLong(arg2.getColumnIndex("date_time"));
        Date date = new Date(a);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        String datetime = sdf.format(date.getTime());

        switch (arg2.getInt(arg2.getColumnIndex("repeat"))){
            case yh729_Constant.Repeat_Once:
                break;
            case yh729_Constant.Repeat_EveryDay:
                type.setText("每天");
                break;
            case yh729_Constant.Repeat_WorkDay:
                type.setText("周一到周五");
                break;
            case yh729_Constant.Repeat_Friday:
                type.setText("周五");
                break;
        }

        imageView.setImageResource(R.drawable.tixing_richeng);
        nameMsg.setText(datetime);
        contentMsg.setText(arg2.getString(arg2.getColumnIndex("content")));
        id.setText(arg2.getString(arg2.getColumnIndex("_id")));

    }
}
