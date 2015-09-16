package com.example.spinel.myapplication;

import org.json.JSONArray;

/**
 * Created by Spinel on 2015/8/11.
 */
public class BPMItem implements Comparable<BPMItem>{
    public String info, activityId, type, activityName;
    public JSONArray datas;
    public BPMItem(String info, String activityId, String type, String activityName, JSONArray datas){
        this.info = info; this.activityId = activityId; this.type = type; this.activityName = activityName;
        this.datas = datas;
    }

    public int compareTo(BPMItem a){

        int compare;

        return (compare=activityId.compareTo(a.activityId))!=0 ? compare :
               (compare=activityName.compareTo(a.activityName))!=0 ? compare :
               (compare=type.compareTo(a.type))!=0 ? compare :
               (compare=info.compareTo(a.info))!=0 ? compare : 0;

    }

    public boolean equals(BPMItem item){
        return info.equals(item.info) && activityId.equals(item.activityId) && type.equals(item.type)
                && activityName.equals(item.activityName) && datas.toString().equals(item.datas.toString());
    }
}
