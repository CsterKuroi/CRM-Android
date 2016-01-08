package com.example.dt.testapp3.Graphics;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.dt.testapp3.R;

import java.util.Calendar;

public class VisitDatePickerFragment extends DialogFragment {

    private TextView enter;
    private TextView cancel;
    private VisitDatePickerFragment thispoint = this;
    private StringBuffer date2;
    private OnFragmentDatePickListener mListener;
    public VisitDatePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View layout = inflater.inflate(R.layout.fragment_visit_date_picker, container, false);
        enter = (TextView) layout.findViewById(R.id.Enter);
        cancel = (TextView) layout.findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thispoint.dismiss();
            }
        });
        final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date2 = new StringBuffer();
                date2.append(String.format("%d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth()+1,
                        datePicker.getDayOfMonth()));
                mListener.onFragmentDatePick(date2.toString());
                thispoint.dismiss();
            }
        });
        Calendar now = Calendar.getInstance();
        datePicker.init(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DATE),null);
        return layout;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String date) {
        if (mListener != null) {
            mListener.onFragmentDatePick(date);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentDatePickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentDatePickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentDatePickListener {
        // TODO: Update argument type and name
        public void onFragmentDatePick(String date);
    }
}
