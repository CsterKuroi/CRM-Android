package com.example.spinel.myapplication.Form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.List;

/**
 * Created by Spinel on 2015/8/7.
 */
public class bpmTraceProcess_ListAdapter extends BaseAdapter {
    private List<bpmProcessItem> mList;
    private LayoutInflater mInflater;
    private Context context;

    public bpmTraceProcess_ListAdapter(List<bpmProcessItem> processList, Context context, LayoutInflater mInflater){
        this.mList = processList;
        this.mInflater = mInflater;
        this.context = context;
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
        ViewHolder viewHolder;

        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.bpm_listview_item_trace_process, null);
            viewHolder.user = (TextView)convertView.findViewById(R.id.traceprocess_item_user);
            viewHolder.time = (TextView)convertView.findViewById(R.id.traceprocess_item_time);
            viewHolder.btn = (Button)convertView.findViewById(R.id.traceprocess_item_button);
            viewHolder.summary = (TextView)convertView.findViewById(R.id.traceprocess_item_summary);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.user.setText(mList.get(position).opUserName);
        viewHolder.time.setText(bpmTools.Calendar2String(mList.get(position).time, 0));
        viewHolder.summary.setText(mList.get(position).summary);
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    final static class ViewHolder{
        public TextView user, time, summary;
        public Button btn;
    }
}
