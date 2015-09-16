package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays months
 */
public class bpmMonthLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmMonthLabeler(String formatString) {
        super(180, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addMonths(time, val));
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        return bpmUtil.getMonth(c, mFormatString);
    }
}