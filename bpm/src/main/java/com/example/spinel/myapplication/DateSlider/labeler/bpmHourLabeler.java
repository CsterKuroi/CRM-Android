package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays hours
 */
public class bpmHourLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmHourLabeler(String formatString) {
        super(90, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addHours(time, val));
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        return bpmUtil.getHour(c, mFormatString);
    }
}
