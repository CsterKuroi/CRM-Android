package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays minutes
 */
public class bpmMinuteLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmMinuteLabeler(String formatString) {
        super(45, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addMinutes(time, val, minuteInterval));
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
    	if (minuteInterval>1) {
    		int minutes = c.get(Calendar.MINUTE);
    		c.set(Calendar.MINUTE, minutes-(minutes%minuteInterval));
    	}
        return bpmUtil.getMinute(c, mFormatString, minuteInterval);
    }

}
