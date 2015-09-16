package com.example.spinel.myapplication.Form;

/**
 * Created by Spinel on 2015/7/26.
 */

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;
import com.example.spinel.myapplication.bpmTools;

import java.util.Calendar;
import java.util.List;

public class bpmForm_Adapter_Write extends BaseAdapter{
    private List<bpmForm_Item> mList;
    private List<bpmForm_Item> hideList;
    final private bpmFormActivity activity;
    private LayoutInflater mInflater;

    private int currentType;

    private int groupIndex;

    public bpmForm_Adapter_Write(bpmFormActivity activity, List<bpmForm_Item> list, List<bpmForm_Item> hideList, int groupIndex){
            this.activity = activity;
            this.mList = list;
            this.mInflater = LayoutInflater.from(activity);
        this.hideList = hideList;
        this.groupIndex = groupIndex;
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

            return mList.get(position).viewtype;
        }



        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            bpmForm_Item item = mList.get(position);

            currentType = getItemViewType(position);
            String rule = item.rule;//--------------------------------规则，是否可编辑


            //-------------------编辑文本-------------------
            if(currentType == bpmForm_Item.EDITTEXT_VIEW) {
                EditTextHolder holder = null;

                if (convertView == null) {
                    holder = new EditTextHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_edittext, null);
                    holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                    holder.editText = (EditText) convertView.findViewById(R.id.item_form_edittext);

                    holder.editText.setTag(position);

                    holder.editText.addTextChangedListener(new MyTextWatcher(holder));


                    convertView.setTag(holder);

                } else {
                    holder = (EditTextHolder) convertView.getTag();
                    holder.editText.setTag(position);
                }

                holder.title.setText(item.name);
                holder.editText.setText(item.value);

                String[] rules = item.rule.split(" ");
                if(!rule.isEmpty() && rules[0].equals("NUM"))
                    holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                else
                    holder.editText.setInputType(InputType.TYPE_CLASS_TEXT);

            }

            //-------------------单选-------------------
            else if(currentType == bpmForm_Item.RADIO_VIEW){
                RadioButtonHolder holder = null;

                if(convertView == null){
                    holder = new RadioButtonHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_radio, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.radioButton = (RadioButton)convertView.findViewById(R.id.item_form_radio);
                    convertView.setTag(holder);
                }
                else {
                    holder = (RadioButtonHolder) convertView.getTag();
                }

                holder.title.setText(item.name);
                holder.radioButton.setChecked(item.value.equals("true"));

                if(!item.value.equals("true"))
                    item.value="false";
            }

            //-------------------checkbox-------------------
            else if(currentType == bpmForm_Item.CHECKBOX_VIEW){
                CheckBoxHolder holder = null;

                if(convertView == null){
                    holder = new CheckBoxHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_checkbox, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.checkbox = (CheckBox)convertView.findViewById(R.id.item_form_checkbox);
                    convertView.setTag(holder);
                }
                else {
                    holder = (CheckBoxHolder) convertView.getTag();
                }

                holder.title.setText(item.name);
                holder.checkbox.setChecked(item.value.equals("true"));

                item.value = item.value.equals("true")? "true" : "false";

            }

            //-------------------文本-------------------
            else if(currentType == bpmForm_Item.TEXT_VIEW){
                TextViewHolder holder = null;

                if(convertView == null){
                    holder = new TextViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_textview, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.textView = (TextView)convertView.findViewById(R.id.item_form_textview);
                    convertView.setTag(holder);
                }
                else{
                    holder = (TextViewHolder) convertView.getTag();
                }

                holder.title.setText(item.name);

                switch (item.type){
                    case bpmForm_Item.STAFF:
                        item.value = bpmMainActivity.userId;
                        if(bpmMainActivity.structure.isEmpty())
                            holder.textView.setText("");
                        else
                            holder.textView.setText(bpmMainActivity.user.name+"，"+ bpmMainActivity.user.deptName+"，"+ bpmMainActivity.user.levelName);
                        break;
                    case bpmForm_Item.CALCULATE:
                        item.value = item.calculate(mList);
                        holder.textView.setText(item.value);
                        break;
                    case bpmForm_Item.DATE:
                        item.valueTime = Calendar.getInstance();
                        item.value =  ((Double)(((double)item.valueTime.getTimeInMillis())/1000)).toString();
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, 0));
                        break;
                    case bpmForm_Item.CLIENT:
                        String[] str = item.value.split(" ");
                        holder.textView.setText(str.length>0 ? str[0] : "");
                    default:
                        holder.textView.setText(item.value);
                        break;
                }

            }
            //-------------------搜索-------------------
            else if(currentType == bpmForm_Item.SEARCH_VIEW){
                TextViewHolder holder = null;

                if(convertView == null){
                    holder = new TextViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_arrow, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.textView = (TextView)convertView.findViewById(R.id.item_form_textview);
                    convertView.setTag(holder);
                }
                else{
                    holder = (TextViewHolder) convertView.getTag();
                }

                holder.title.setText(item.name);

                if(item.type== bpmForm_Item.STAFF)
                    holder.textView.setText(bpmTools.getUserNameListString(item.valueList));
                else if(item.type== bpmForm_Item.CLIENT){
                    if(item.value.isEmpty())
                        holder.textView.setText("");
                    else{
                        String[] values = item.value.split(" ")[0].split("_");
                        holder.textView.setText(values.length>1?values[1]:values[0]);
                    }
                }
                else
                    holder.textView.setText(item.value);

            }

            //-------------------时间-------------------
            else if(currentType == bpmForm_Item.DATE_VIEW){
                TextViewHolder holder = null;

                if(convertView == null){
                    holder = new TextViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_arrow, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.textView = (TextView)convertView.findViewById(R.id.item_form_textview);
                    convertView.setTag(holder);
                }
                else{
                    holder = (TextViewHolder) convertView.getTag();
                }

                holder.title.setText(item.name);

                if(item.valueTime==null) {
                    item.value="";
                    holder.textView.setText("");
                }
                else{
                    item.value =  bpmTools.Calendar2Millisecond(item.valueTime);

                    //为空就是时间点 datetime
                    if(item.rule.isEmpty())
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_POIONT_DATETIME));
                    else if(item.rule.equals("DAY"))
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_DAY));
                    else if(item.rule.equals("WEEK"))
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_WEEK));
                    else if(item.rule.equals("MONTH"))
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_MONTH));
                    else if(item.rule.equals("YEAR"))
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_YEAR));
                    else if(item.rule.equals("SEASON"))
                        holder.textView.setText(bpmTools.Calendar2String(item.valueTime, bpmTools.DATE_PERIOD_SEASON));

                }

            }

            //-------------------选择-------------------
            else if(currentType == bpmForm_Item.SELECT_VIEW){
                TextViewHolder holder = null;

                if(convertView == null){
                    holder = new TextViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_arrow, null);
                    holder.title = (TextView)convertView.findViewById(R.id.item_form_title);
                    holder.textView = (TextView)convertView.findViewById(R.id.item_form_textview);
                    convertView.setTag(holder);
                }
                else{
                    holder = (TextViewHolder) convertView.getTag();
                }

                holder.title.setText(item.name);

                item.setSelectString();
                holder.textView.setText(item.value);

            }

            //-------------------TEXTAREA-------------------
            else if(currentType == bpmForm_Item.TEXTAREA_VIEW){

                EditTextHolder holder = null;

                if (convertView == null) {
                    holder = new EditTextHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_textarea, null);
                    holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                    holder.editText = (EditText) convertView.findViewById(R.id.item_form_edittext);

                    holder.editText.setTag(position);

                    holder.editText.addTextChangedListener(new MyTextWatcher(holder));


                    convertView.setTag(holder);

                } else {
                    holder = (EditTextHolder) convertView.getTag();
                    holder.editText.setTag(position);
                }

                holder.title.setText(item.name);
                holder.editText.setText(item.value);

                if(item.rule.equals("NUM"))
                    holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            }

            //-------------------开关-------------------
            else if(currentType == bpmForm_Item.SWITCH_VIEW){
                SwitchViewHolder holder = null;

                if(convertView == null){
                    holder = new SwitchViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_switch, null);
                    holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                    holder.aSwitch = (Switch)convertView.findViewById(R.id.item_form_switch);

                    holder.aSwitch.setTag(position);

                    convertView.setTag(holder);
                } else {
                    holder = (SwitchViewHolder) convertView.getTag();
                    holder.aSwitch.setTag(position);
                }

                holder.title.setText(item.name);

                item.value = item.value.equals("true")?"true":"false";

                holder.aSwitch.setChecked(item.value.equals("true"));

                holder.aSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener(holder));

            }

            //-------------------录音-------------------
            else if(currentType == bpmForm_Item.VOICE_VIEW){

                VoiceViewHolder holder = null;

                if (convertView == null) {
                    holder = new VoiceViewHolder();
                    convertView = mInflater.inflate(R.layout.bpm_listview_item_form_voice, null);
                    holder.title = (TextView) convertView.findViewById(R.id.item_form_title);
                    holder.record = (Button) convertView.findViewById(R.id.item_form_record);
                    holder.play = (Button) convertView.findViewById(R.id.item_form_play);

                    convertView.setTag(holder);

                } else {
                    holder = (VoiceViewHolder) convertView.getTag();
                }

                holder.title.setText(item.name);

                item.recorder.setButton(holder.record, holder.play);


            }

            return convertView;



        }

    private void setHide(String[] strings){

        for(int i=1; i<strings.length; i++){
            for(int j=0; j<mList.size(); j++){
                if(strings[i].equals(mList.get(j).id)){
                    mList.get(j).isHide = true;
                    break;
                }
            }
        }

        setHide();
    }

    private void setHide(){

        for(int i=0; i<mList.size(); i++)
            if(mList.get(i).isHide){
                hideList.add(mList.get(i));
                mList.remove(i);
                i--;
            }
    }

    private void setShow(String[] strings){

        for(int i=1; i<strings.length; i++){
            for(int j=0; j<hideList.size(); j++){
                if(strings[i].equals(hideList.get(j).id)){
                    hideList.get(j).isHide = false;

                    bpmForm_Item item = hideList.get(j);
                    hideList.remove(j);



                    bpmForm_Item previousItem = item.previousItem;
                    while(previousItem!=null && previousItem.isHide)
                        previousItem = previousItem.previousItem;

                    if(previousItem==null)
                        mList.add(0, item);
                    else{
                        for(int k=0; k<mList.size(); k++)
                            if(mList.get(k).id.equals(previousItem.id)) {

                                mList.add(k + 1, item);
                                break;
                            }
                    }

                    break;
                }
            }
        }


        //隐藏被显示的
        for(int i=0; i<mList.size(); i++){
            bpmForm_Item item = mList.get(i);

            if(item.type== bpmForm_Item.SWITCH && !item.value.equals("true")){
                String[] rules = item.rule.split(" ");
                for(int j=1; j<rules.length; j++){
                    for(int k=0; k<mList.size(); k++)
                        if(rules[j].equals(mList.get(k).id))
                            mList.get(k).isHide=true;
                }
            }
        }

        setHide();

    }

    final static class VoiceViewHolder{
        TextView title;
        Button record, play;
    }

    final static class SwitchViewHolder{
        TextView title;
        Switch aSwitch;
    }
    final static class TextViewHolder{
        TextView title, textView;
    }
    final static class EditTextHolder{
        TextView title;
        EditText editText;
    }

    final static  class RadioButtonHolder {
        TextView title;
        RadioButton radioButton;
    }

    final static  class CheckBoxHolder{
        TextView title;
        CheckBox checkbox;
    }

    class MyTextWatcher implements TextWatcher {
        public MyTextWatcher(EditTextHolder holder) {
            mHolder = holder;
        }

        private EditTextHolder mHolder;

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !"".equals(s.toString())) {
                int position = (Integer) mHolder.editText.getTag();
                mList.get(position).value=s.toString();// 当EditText数据发生改变的时候存到data变量中
            }
        }
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        public MyOnCheckedChangeListener(SwitchViewHolder holder){
            mHolder = holder;
        }

        private SwitchViewHolder mHolder;

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int position = (Integer) mHolder.aSwitch.getTag();
            bpmForm_Item item = mList.get(position);
            item.value = b? "true" : "false";

            if(item.rule.split(" ")[0].equals("HIDE")){
                if(item.value.equals("true")){
                    setShow(item.rule.split(" "));
                }
                else{
                    setHide(item.rule.split(" "));
                }
                notifyDataSetChanged();
                activity.scrollToMeasuredHeight(groupIndex);
            }
        }
    }
}