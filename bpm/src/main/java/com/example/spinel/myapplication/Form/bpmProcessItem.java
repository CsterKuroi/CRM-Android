package com.example.spinel.myapplication.Form;

import java.util.Calendar;

/**
 * Created by Spinel on 2015/7/23.
 */
public class bpmProcessItem implements Comparable<bpmProcessItem>{
    String stepName, stepId, summary, opUserId, opUserName;
    Calendar time;

    String datas;

    public bpmProcessItem(String stepName, String stepId, String opUserId, String opUserName, Calendar time, String datas, String summary){
        this.stepName = stepName;
        this.stepId = stepId;
        this.opUserId = opUserId;
        this.opUserName = opUserName;
        this.time = time;
        this.datas = datas;
        this.summary = summary;
    }

    public int compareTo(bpmProcessItem arg){
        return time.before(arg.time)?-1:1;
    }
}
