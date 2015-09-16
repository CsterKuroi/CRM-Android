package yh729_adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yanhao.task729.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import yh729_UI.yh729_CircleView;
import yh729_bean.my_remindBean;

/**
 * Created by Yanhao on 15-7-29.
 */
public class yh729_mBaseAdapter extends BaseAdapter {
    private ArrayList<my_remindBean> mListMsgBean;
    private Context mContext;
    private LayoutInflater mInflater;
    public yh729_mBaseAdapter(ArrayList<my_remindBean> mListMsgBean, Context context){
        this.mListMsgBean=mListMsgBean;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListMsgBean.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mListMsgBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = mInflater.inflate(R.layout.yh729_message_item_layout, null);

        TextView id=(TextView)v.findViewById(R.id.id_msg_item);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_msg_item);
        TextView nameMsg = (TextView) v.findViewById(R.id.name_msg_item);
        TextView contentMsg = (TextView) v.findViewById(R.id.content_msg_item);
        TextView timeMsg = (TextView) v.findViewById(R.id.time_msg_item);
        yh729_CircleView numMsg=(yh729_CircleView)v.findViewById(R.id.num_msg_item);

        long a = mListMsgBean.get(position).getMessageTime()*1000;
        int num = mListMsgBean.get(position).getNum();
        Date date = new Date(a);
        Calendar lala =Calendar.getInstance();
        lala.setTime(date);
        String howlong= gethowlong(lala);

        imageView.setImageResource(mListMsgBean.get(position).getPhotoDrawableId());
        nameMsg.setText(mListMsgBean.get(position).getMessageTitle());
        contentMsg.setText(mListMsgBean.get(position).getMessageContent());
        timeMsg.setText(a>0?howlong:"");
        if(mListMsgBean.get(position).getImportant()){
            contentMsg.setTextColor(Color.RED);
            TextPaint tp = contentMsg.getPaint();
            tp.setFakeBoldText(true);
        }
        if (num > 0) {
            numMsg.setBackgroundColor(Color.RED);
            numMsg.setNotifiText(num);
        }
        else{
            numMsg.setBackgroundColor(0x00000000);
            numMsg.setTextColor(0x00000000);
        }
        return v;
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
