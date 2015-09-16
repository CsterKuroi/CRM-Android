package com.example.spinel.myapplication.MainFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Spinel on 2015/7/21.
 */
public class bpmMainFragment_Submit_ListitemAdapter extends BaseAdapter {
    private List<bpmMainFragment_Submit_DataHolder> mList;
    private Context context;
    private LayoutInflater mInflater;

    public bpmMainFragment_Submit_ListitemAdapter(Context context, List<bpmMainFragment_Submit_DataHolder> list){
        this.context = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.bpm_listview_item_myprocess, null);
            holder.time = (TextView)convertView.findViewById(R.id.workshow_item_time);
            holder.type = (TextView)convertView.findViewById(R.id.workshow_item_type);
            holder.shenhe = (TextView)convertView.findViewById(R.id.workshow_item_shenhe);
            holder.state = (TextView)convertView.findViewById(R.id.workshow_item_state);
            holder.submit = (TextView)convertView.findViewById(R.id.workshow_item_submit);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        Calendar startTime = mList.get(position).startTime;
        String time = bpmTools.Calendar2String(startTime, 0);

        holder.time.setText(time);
        holder.type.setText(mList.get(position).activityName);
        holder.shenhe.setText(mList.get(position).stepName);
        holder.submit.setText(mList.get(position).submitUserName);

        String statusName = mList.get(position).statusName;
        holder.state.setText(statusName);


        if(statusName.equals("正在审核"))
            holder.state.setTextColor(Color.parseColor("#ffffc721"));
        else if(statusName.equals("已通过"))
            holder.state.setTextColor(Color.parseColor("#ffa4f632"));
        else if(statusName.equals("未通过"))
            holder.state.setTextColor(Color.parseColor("#ffff0000"));
        else if(statusName.equals("过期") || statusName.equals("已撤销"))
            holder.state.setTextColor(Color.parseColor("#ff89898a"));
        else if(statusName.equals("超时打回"))
            holder.state.setTextColor(Color.parseColor("#FFE595FF"));
        else if(statusName.equals("超时作废"))
            holder.state.setTextColor(Color.parseColor("#FFFF982A"));


        return convertView;
    }

    final static class ViewHolder{
        TextView time, type, shenhe, state, submit;
    }
}