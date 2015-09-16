package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays months
 */
public class bpmYearLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmYearLabeler(String formatString) {
        super(200, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addYears(time, val));
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        return bpmUtil.getYear(c, mFormatString);
    }
}