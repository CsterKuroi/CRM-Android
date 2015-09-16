package com.example.spinel.myapplication.DateSlider.labeler;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;

import com.example.spinel.myapplication.DateSlider.bpmTimeObject;
import com.example.spinel.myapplication.DateSlider.timeview.bpmTimeTextView;
import com.example.spinel.myapplication.DateSlider.timeview.bpmTimeView;
import com.example.spinel.myapplication.bpmTools;

import java.util.Calendar;

/**
 * A Labeler that displays months
 */
public class bpmSeasonLabeler extends bpmLabeler {
    private final String mFormatString;

    public bpmSeasonLabeler(String formatString) {
        super(180, 60);
        mFormatString = formatString;
    }

    @Override
    public bpmTimeObject add(long time, int val) {
        return timeObjectfromCalendar(bpmUtil.addSeasons(time, val));
    }

    /**
     * We implement this as custom code rather than a method in Util because there
     * is no format string that shows the week of the year as an integer, so we just
     * format the week directly rather than extracting it from a Calendar object.
     */
    @Override
    protected bpmTimeObject timeObjectfromCalendar(Calendar c) {
        Calendar start = (Calendar)c.clone();
        Calendar end = (Calendar)c.clone();

        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            start.set(Calendar.MONTH, 0);
            end.set(Calendar.MONTH, 2);
        }
        else if (currentMonth >= 4 && currentMonth <= 6) {
            start.set(Calendar.MONTH, 3);
            end.set(Calendar.MONTH, 5);
        }
        else if (currentMonth >= 7 && currentMonth <= 9) {
            start.set(Calendar.MONTH, 6);
            end.set(Calendar.MONTH, 8);
        }
        else if (currentMonth >= 10 && currentMonth <= 12) {
            start.set(Calendar.MONTH, 9);
            end.set(Calendar.MONTH, 11);
        }


        // set calendar to first millisecond of the season
        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the season
        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        end.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();

        return new bpmTimeObject(bpmTools.getSeasonName(start), startTime, endTime);
    }

    /**
     * create our customized TimeTextView and return it
     */
    public bpmTimeView createView(Context context, boolean isCenterView) {
        return new CustomTimeTextView(context, isCenterView, 25);
    }

    /**
     * Here we define our Custom TimeTextView which will display the fonts in its very own way.
     */
    private static class CustomTimeTextView extends bpmTimeTextView {

        public CustomTimeTextView(Context context, boolean isCenterView, int textSize) {
            super(context, isCenterView, textSize);
        }

        /**
         * Here we set up the text characteristics for the TextView, i.e. red colour,
         * serif font and semi-transparent white background for the centerView... and shadow!!!
         */
        @Override
        protected void setupView(boolean isCenterView, int textSize) {
            setGravity(Gravity.CENTER);
            setTextColor(0xFF883333);
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            setTypeface(Typeface.SERIF);
            if (isCenterView) {
                setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                setBackgroundColor(0x55FFFFFF);
                setShadowLayer(2.5f, 3, 3, 0xFF999999);
            }
        }

    }
}