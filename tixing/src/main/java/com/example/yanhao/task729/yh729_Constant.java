package com.example.yanhao.task729;

/**
 * Created by Yanhao on 15-7-29.
 */
public class yh729_Constant {

    public static final String MY_REMIND = "我的提醒";

    public static final String WORK_REMIND="工作提醒";
    public static final String INSTRUCTION_REMIND="指令提醒";
    public static final String DAIRY_REMIND="日志提醒";
    public static final String CHECK_REMIND="审批提醒";
    public static final String FOCUS_REMIND="我关注的";
    public static final String SCHEDULE_REMIND="日程提醒";
    public static final String GROUP_REMIND="群通知";

    public static final String NEEDREPLY="需要回执ִ";
    public static final String REPLYME="回复我的";
    public static final String ATME="@我的";
    public static final String ATMYSECTOR="@我部门的";
    public static final String SEND="我发出的";
    public static final String PRAISE="我收到的赞";

    public static final String INSTRUCTION_REMIND1="待处理";
    public static final String INSTRUCTION_REMIND2="已处理";
    public static final String DAIRY_REMIND1="待点评";
    public static final String DAIRY_REMIND2="已点评";
    public static final String CHECK_REMIND1="待批复";
    public static final String CHECK_REMIND2="已批复";
    public static final String FOCUS_REMIND1="我关注的";
    public static final String FOCUS_REMIND2="我关注的回复";
    public static final String NEEDREPLY1="未回执";
    public static final String NEEDREPLY2="已回执";
    public static final String ATME1="@我的工作";
    public static final String ATME2="@我的回复";
    public static final String ATMYSECTOR1="@我部门的工作";
    public static final String ATMYSECTOR2="@我部门的回复";
    public static final String SCHEDULE_REMIND1="日程信息";
    public static final String SCHEDULE_REMIND2="定时提醒";

    public static final String SETTING="提醒设置";

    public static final String NOTIFY_SERVICE_FLAG="yh729_AlarmNotificationService";
    public static final String ALARM_SERVICE_FLAG="AlarmService";

    public static final int Repeat_EveryDay=0;
    public static final int Repeat_WorkDay=1;
    public static final int Repeat_Once=2;
    public static final int Repeat_Friday=3;

    public static boolean notification=true;
    public static boolean sound=true;
    public static boolean vibrate=true;

    //审批
    public static boolean censor_notification=true;
    public static boolean censor_sound=true;
    public static boolean censor_vibrate=true;


    //日志
    public static boolean diary_notification=true;
    public static boolean diary_sound=true;
    public static boolean diary_vibrate=true;

    //日程
    public static boolean schedule_notification=true;
    public static boolean schedule_sound=true;
    public static boolean schedule_vibrate=true;
}

