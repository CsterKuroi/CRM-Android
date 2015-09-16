package com.example.spinel.myapplication.MainFragment;

import java.util.Calendar;

/**
 * Created by Spinel on 2015/7/21.
 */
public class bpmMainFragment_Task_DataHolder implements Comparable<bpmMainFragment_Task_DataHolder>{
    public String activityId, activityName, taskId, status, submitUser;
    public Calendar startTime, endTime;
    public String[] processIds;

    public bpmMainFragment_Task_DataHolder(String activityId, String activityName, String taskId, String status, String submitUser, String[] processIds, Calendar startTime, Calendar endTime){

        this.activityId = activityId;
        this.activityName = activityName;
        this.taskId = taskId;
        this.status = status;
        this.submitUser = submitUser;
        this.processIds = processIds;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    @Override
    public int compareTo(bpmMainFragment_Task_DataHolder a){
        return startTime.after(a.startTime)?-1:1;
    }
}
