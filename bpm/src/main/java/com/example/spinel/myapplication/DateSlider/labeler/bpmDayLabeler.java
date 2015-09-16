package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays days
 */
public class bpmDayLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmDayLabeler(String formatString) {
        super(150, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addDays(time, val));
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        return bpmUtil.getDay(c, mFormatString);
    }
}