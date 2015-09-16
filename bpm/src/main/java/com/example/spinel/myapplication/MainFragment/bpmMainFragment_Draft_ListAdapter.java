package com.example.spinel.myapplication.MainFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.List;

/**
 * Created by Spinel on 2015/8/7.
 */
public class bpmMainFragment_Draft_ListAdapter extends BaseAdapter {
    private List<bpmMainFragment_Draft_DataHolder> mList;
    private LayoutInflater mInflater;
    private Context context;

    public bpmMainFragment_Draft_ListAdapter(List<bpmMainFragment_Draft_DataHolder> mList, Context context, LayoutInflater mInflater){
        this.mList = mList;
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
            convertView = mInflater.inflate(R.layout.bpm_listview_item, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.item_title);
            viewHolder.time = (TextView)convertView.findViewById(R.id.item_text);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.title.setText(mList.get(position).title);
        viewHolder.time.setText(bpmTools.Calendar2String(mList.get(position).time, 0));

        return convertView;
    }

    final static class ViewHolder{
        public TextView title, time;
    }

}
