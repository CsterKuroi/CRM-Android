package com.example.spinel.myapplication.DateSlider.labeler;

import android.content.Context;

import com.example.spinel.myapplication.DateSlider.timeview.bpmTimeLayoutView;
import com.example.spinel.myapplication.DateSlider.timeview.bpmTimeView;

/**
 * A Labeler that displays months using TimeLayoutViews.
 */
public class bpmMonthYearLabeler extends bpmMonthLabeler {
    /**
     * The format string that specifies how to display the month. Since this class
     * uses a TimeLayoutView, the format string should consist of two strings
     * separated by a space.
     *
     * @param formatString
     */
    public bpmMonthYearLabeler(String formatString) {
        super(formatString);
    }

    @Override
    public bpmTimeView createView(Context context, boolean isCenterView) {
        return new bpmTimeLayoutView(context, isCenterView, 25, 8, 0.95f);
    }
}