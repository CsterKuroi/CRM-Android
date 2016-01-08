package com.example.dt.testapp3.Graphics;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dt.testapp3.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class VisitLoadingFragment extends Fragment {

    private TextView Text;
    private View layout;
    public VisitLoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VisitMainActivity act = (VisitMainActivity) getActivity();
//        act.setAddAndSaveVisible(false);
//        act.setSubmitVisible(false);
//        act.setFinishVisible(false);
//        act.setTapeVisible(false);
//        act.setPlayVisible(false);
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_visit_loading, container, false);
        Text = (TextView) layout.findViewById(R.id.LoadingText);


        return layout;
    }

    public void setText(String txt){
        if (Text!=null){
            Text.setText(txt);
        }
    }

}
