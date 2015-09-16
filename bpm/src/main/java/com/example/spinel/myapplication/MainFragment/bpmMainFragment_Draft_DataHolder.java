package com.example.spinel.myapplication.MainFragment;

import java.util.Calendar;

/**
 * Created by Spinel on 2015/8/7.
 */
public class bpmMainFragment_Draft_DataHolder {
    public String activityId, title;
    Calendar time;
    public int id;

    public String datas, blankdatas;

    public bpmMainFragment_Draft_DataHolder(int id, String activityId, String title, Calendar time, String datas, String blankdatas){
        this.activityId = activityId;
        this.title = title;
        this.time = time;
        this.id = id;
        this.datas = datas;
        this.blankdatas = blankdatas;
    }
}
