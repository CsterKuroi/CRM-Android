package com.example.spinel.myapplication.MainFragment;

import java.util.Calendar;

/**
 * Created by Spinel on 2015/7/21.
 */
public class bpmMainFragment_Submit_DataHolder implements Comparable<bpmMainFragment_Submit_DataHolder>{
    public String activityId, activityName, processId, status, statusName, stepId, stepName, submitUserId, submitUserName;
    public Calendar startTime, endTime;


    public bpmMainFragment_Submit_DataHolder(String activityId, String activityName, String processId, String status, String stepId, String stepName, String submitUserId, String submitUserName, Calendar startTime, Calendar endTime){

        this.activityId = activityId;
        this.activityName = activityName;
        this.processId = processId;
        this.status = status;
        this.stepId = stepId;
        this.stepName = stepName;
        this.submitUserId = submitUserId;
        this.submitUserName = submitUserName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.statusName = getStateName(status);
    }

    @Override
    public int compareTo(bpmMainFragment_Submit_DataHolder a){
        return startTime.after(a.startTime)?-1:1;
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
        else if(state.equals("callback")) {
            return "超时打回";
        }
        else if(state.equals("timeout"))
            return "过期";
        else if(state.equals("backToStart"))
            return "超时作废";
        else
            return "";
    }
}
