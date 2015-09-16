package com.melnykov.fab.sample.kehu;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.sample.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class crmaddcustomFragment extends Fragment {

    public crmaddcustomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.crm_fragment_addcustom, container, false);
    }
}
