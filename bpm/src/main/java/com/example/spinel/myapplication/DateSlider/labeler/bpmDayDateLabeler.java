package com.example.spinel.myapplication.DateSlider.labeler;

import android.content.Context;

import com.example.spinel.myapplication.DateSlider.timeview.bpmDayTimeLayoutView;
import com.example.spinel.myapplication.DateSlider.timeview.bpmTimeView;

/**
 * A Labeler that displays days using DayTimeLayoutViews.
 */
public class bpmDayDateLabeler extends bpmDayLabeler {
    /**
     * The format string that specifies how to display the day. Since this class
     * uses a DayTimeLayoutView, the format string should consist of two strings
     * separated by a space.
     *
     * @param formatString
     */
    public bpmDayDateLabeler(String formatString) {
        super(formatString);
    }

    @Override
    public bpmTimeView createView(Context context, boolean isCenterView) {
        return new bpmDayTimeLayoutView(context, isCenterView,30,8,0.8f);
    }
}