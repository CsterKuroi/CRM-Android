package com.example.spinel.myapplication.Form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.List;

/**
 * Created by Spinel on 2015/7/24.
 */
public class bpmForm_Adapter_Read extends BaseAdapter{
    private List<bpmForm_Item> mList;
    private Context context;
    private LayoutInflater mInflater;


    private boolean isSummary;
    private int currentType;

    public bpmForm_Adapter_Read(Context context, List<bpmForm_Item> list, boolean isSummary){
        this.context = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
        this.isSummary = isSummary;
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
    public int getViewTypeCount() {
        return bpmForm_Item.VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position){

        if(mList.get(position).viewtype == bpmForm_Item.VOICE_VIEW)
            return bpmForm_Item.VOICE_VIEW;
        else
            return bpmForm_Item.TEXT_VIEW;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        bpmForm_Item item = mList.get(position);
        currentType = getItemViewType(position);

        if(currentType == bpmForm_Item.VOICE_VIEW){

            VoiceViewHolder holder = null;

            if (convertView == null) {
                holder = new VoiceViewHolder();
                convertView = mInflater.inflate(R.layout.bpm_listview_item_form_voice, null);
                holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                convertView.findViewById(R.id.item_form_play).setVisibility(View.GONE);
                holder.play = (Button) convertView.findViewById(R.id.item_form_record);

                convertView.setTag(holder);

            } else {
                holder = (VoiceViewHolder) convertView.getTag();
            }

            holder.title.setText(item.name);

            item.recorder.setButton(holder.play);
        }
        else if(currentType == bpmForm_Item.TEXT_VIEW) {

            TextViewHolder holder = null;

            if (convertView == null) {
                holder = new TextViewHolder();
                convertView = mInflater.inflate(
                        isSummary ? R.layout.bpm_listview_item_form_textview_summary : R.layout.bpm_listview_item_form_textview, null);
                holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                holder.content = (TextView) convertView.findViewById(R.id.item_form_textview);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            holder.title.setText(item.name);

            String value;
            switch (item.type) {
                case bpmForm_Item.CHECKBOX:
                case bpmForm_Item.SWITCH:
                case bpmForm_Item.RADIO:
                    value = item.value.equals("true") ? "是" : "否";
                    break;
                case bpmForm_Item.STAFF:
                    if (item.rule.equals("MULTI")) {
                        String[] strings = item.value.split(", ");
                        value = bpmTools.convertUserIdStrings2UserName(strings);
                    } else
                        value = bpmMainActivity.structure.getUserName(item.value);
                    break;
                case bpmForm_Item.DATE:

                    value = bpmTools.Calendar2String(bpmTools.Millisecond2Calendar(item.value), 0);
                    //为空就是时间点 datetime
                    if(item.rule.isEmpty())
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_POIONT_DATETIME);
                    else if(item.rule.equals("DAY"))
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_DAY);
                    else if(item.rule.equals("WEEK"))
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_WEEK);
                    else if(item.rule.equals("MONTH"))
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_MONTH);
                    else if(item.rule.equals("YEAR"))
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_YEAR);
                    else if(item.rule.equals("SEASON"))
                        value = bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_SEASON);


                    break;

                case bpmForm_Item.CLIENT: {
                    if (item.value.isEmpty())
                        value = "";
                    else {
                        String[] values = item.value.split(" ")[0].split("_");
                        value = values.length > 1 ? values[1] : values[0];
                    }
                    break;
                }
                default:
                    value = item.value;

            }
            holder.content.setText(value);
        }

        return convertView;

    }

    final static class TextViewHolder{
        TextView title, content;
    }

    final static class VoiceViewHolder{
        TextView title;
        Button record, play;
    }
}
