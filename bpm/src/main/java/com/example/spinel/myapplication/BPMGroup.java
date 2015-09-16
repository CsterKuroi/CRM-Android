package com.example.spinel.myapplication;

import java.util.ArrayList;

/**
 * Created by Spinel on 2015/8/11.
 */
public class BPMGroup {
    public String name, type;
    public ArrayList<BPMItem> list;
    public String[] searchTypes;

    public BPMGroup(String type, String name){
        this.name = name;
        this.type = type;
        list = new ArrayList<>();
    }

    public void addItem(BPMItem item){
        list.add(item);
    }

    public void setSearchTypes(){
        searchTypes = new String[list.size()+1];
        searchTypes[0]="全部";
        for(int i=0; i<list.size(); i++)
            searchTypes[i+1] = list.get(i).activityName;
    }
}
