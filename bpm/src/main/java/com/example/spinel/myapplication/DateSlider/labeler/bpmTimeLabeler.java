package com.example.spinel.myapplication.DateSlider.labeler;

import java.util.Calendar;

import android.util.Log;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;

/**
 * A Labeler that displays times in increments of {@value #MINUTEINTERVAL} minutes.
 */
public class bpmTimeLabeler extends bpmLabeler {
    public static int MINUTEINTERVAL = 15;

    private final String mFormatString;

    public bpmTimeLabeler(String formatString) {
        super(80, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addMinutes(time, val * MINUTEINTERVAL));
    }

    /**
     * override this method to set the inital TimeObject to a multiple of MINUTEINTERVAL
     */
    @Override
    public bpmTimeObject getElem(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)/MINUTEINTERVAL*MINUTEINTERVAL);
        Log.v("GETELEM","getelem: "+c.get(Calendar.MINUTE));
        return timeObjectfromCalendar(c);
    }

    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        return bpmUtil.getTime(c, mFormatString, MINUTEINTERVAL);
    }
}