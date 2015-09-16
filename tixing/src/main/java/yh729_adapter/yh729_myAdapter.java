package yh729_adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import yh729_UI.yh729_CircleView;
import com.example.yanhao.task729.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yan on 2015/8/6.
 */
public class yh729_myAdapter extends SimpleCursorAdapter {

    private LayoutInflater mInflater;

    public yh729_myAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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
        TextView id=(TextView)v.findViewById(R.id.id_msg_item);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_msg_item);
        TextView nameMsg = (TextView) v.findViewById(R.id.name_msg_item);
        TextView contentMsg = (TextView) v.findViewById(R.id.content_msg_item);
        TextView timeMsg = (TextView) v.findViewById(R.id.time_msg_item);
        yh729_CircleView numMsg=(yh729_CircleView)v.findViewById(R.id.num_msg_item);
        FrameLayout frameLayout=(FrameLayout)v.findViewById(R.id.f_msg_item);

        long a = arg2.getLong(arg2.getColumnIndex("time"))*1000;
        int num = arg2.getInt(arg2.getColumnIndex("num"));
        Date date = new Date(a);
        Calendar lala =Calendar.getInstance();
        lala.setTime(date);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        String datetime = sdf.format(date.getTime());*/
        String howlong= gethowlong(lala);


        switch (arg2.getString(arg2.getColumnIndex("_id"))){
            case "1":
                imageView.setImageResource(R.drawable.ic_photo_workremind);
                break;
            case "2":
                imageView.setImageResource(R.drawable.ic_photo_instructionremind);
                break;
            case "3":
                imageView.setImageResource(R.drawable.ic_photo_diaryremind);
                break;
            case "4":
                imageView.setImageResource(R.drawable.ic_photo_checkremind);
                break;
            case "5":
                imageView.setImageResource(R.drawable.ic_photo_focus);
                break;
            case "6":
                imageView.setImageResource(R.drawable.schedule_remind);
                break;
            case "7":
                imageView.setImageResource(R.drawable.needreply);
                break;
            case "8":
                imageView.setImageResource(R.drawable.reply);
                break;
            case "9":
                imageView.setImageResource(R.drawable.atme);
                break;
            case "10":
                imageView.setImageResource(R.drawable.atmyteam);
                break;
            case "11":
                imageView.setImageResource(R.drawable.send);
                break;
            case "12":
                imageView.setImageResource(R.drawable.praise);
        }
        id.setText(arg2.getString(arg2.getColumnIndex("_id")));
//        imageView.setImageResource(arg2.getInt(arg2.getColumnIndex("photo")));
        nameMsg.setText(arg2.getString(arg2.getColumnIndex("name")));
        contentMsg.setText(arg2.getString(arg2.getColumnIndex("content")));
        timeMsg.setText(a>0?howlong:"");
        if (num > 0) {
            numMsg.setBackgroundColor(Color.RED);
            numMsg.setNotifiText(num);
        }
        else{
            numMsg.setBackgroundColor(0x00000000);
            numMsg.setTextColor(0x00000000);
        }
    }
    public String gethowlong(Calendar calendar){
        Calendar now=Calendar.getInstance();
        Date d1= calendar.getTime();
        Date d2=now.getTime();
        long diff = d2.getTime() - d1.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
        if(calendar.get(Calendar.YEAR)!=now.get(Calendar.YEAR))
            return calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        else if(days>5)
            return (calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        else if(days>0)
            return " "+days+"天前";
        else if(hours>0)
            return " "+hours+"小时前";
        else if(minutes>0)
            return " "+minutes+"分钟前";
        else
            return "刚刚";
    }
}


