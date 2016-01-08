package yh729_adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.yanhao.task729.R;

/**
 * Created by yan on 2015/8/8.
 */
public class yh729_scheduleAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;

    public yh729_scheduleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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
        TextView timeMsg = (TextView) v.findViewById(R.id.time_msg_item);
        TextView id =(TextView)v.findViewById(R.id.id_msg_item);
        FrameLayout frameLayout=(FrameLayout)v.findViewById(R.id.f_msg_item);
        String a=arg2.getString(arg2.getColumnIndex("scheduleDate"));
        //long a = arg2.getLong(arg2.getColumnIndex("scheduleDate"));
        /*Date date = new Date(a);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        String datetime = sdf.format(date.getTime());*/


        imageView.setImageResource(R.drawable.tixing_richeng);
        nameMsg.setText(a);
        contentMsg.setText(arg2.getString(arg2.getColumnIndex("scheduleContent")));
        id.setText(arg2.getString(arg2.getColumnIndex("_id")));
        if(arg2.getString(arg2.getColumnIndex("read")).equals("false")){
            v.setBackgroundColor(0x111AFF1C);
        }
        else
            v.setBackgroundColor(0x000000);
    }
}
