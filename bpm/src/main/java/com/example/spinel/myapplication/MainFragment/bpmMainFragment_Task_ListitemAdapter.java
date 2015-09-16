package com.example.spinel.myapplication.MainFragment;

import android.content.Context;
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
public class bpmMainFragment_Task_ListitemAdapter extends BaseAdapter {
    private List<bpmMainFragment_Task_DataHolder> mList;
    private Context context;
    private LayoutInflater mInflater;

    public bpmMainFragment_Task_ListitemAdapter(Context context, List<bpmMainFragment_Task_DataHolder> list){
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
        holder.shenhe.setText("");
        holder.submit.setText(mList.get(position).submitUser);

        String status = getStateName(mList.get(position).status);
        holder.state.setText("");
/*


        if(status.equals("正在审核"))
            holder.state.setTextColor(Color.parseColor("#ffffc721"));
        else if(status.equals("已通过"))
            holder.state.setTextColor(Color.parseColor("#ffa4f632"));
        else if(status.equals("未通过"))
            holder.state.setTextColor(Color.parseColor("#ffff0000"));
        else if(status.equals("已撤销"))
            holder.state.setTextColor(Color.parseColor("#ff89898a"));
        else if(status.equals("超时，需重填"))
            holder.state.setTextColor(Color.parseColor("#FFE595FF"));
        else if(status.equals("超时"))
            holder.state.setTextColor(Color.parseColor("#FFFF982A"));

*/

        return convertView;
    }

    private String getStateName(String state){
        if(state.equals("processing"))
            return "正在审核";
        else if(state.equals("abort"))
            return "未通过";
        else if(state.equals("end"))
            return "已通过";
        else if(state.equals("cancelled"))
            return "已撤销";
        else if(state.equals("callback"))
            return "超时，需重填";
        else if(state.equals("timeout"))
            return "超时";
        else
            return "";
    }

    final static class ViewHolder{
        TextView time, type, shenhe, state, submit;
    }
}