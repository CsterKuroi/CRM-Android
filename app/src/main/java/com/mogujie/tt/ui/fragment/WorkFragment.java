package com.mogujie.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.jogle.attendance.JGMain2Activity;
import com.example.spinel.myapplication.bpmMainActivity;
import com.mogujie.tt.R;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.ricky.database.CenterDatabase;

public class WorkFragment extends TTBaseFragment {
    private View curView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (curView != null) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_work, topContentView);

        RelativeLayout button2 = (RelativeLayout) curView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CenterDatabase centerDatabase = new CenterDatabase(getActivity(), null);
                int uid = Integer.parseInt(centerDatabase.getUID());
                String name = "";
                centerDatabase.close();

                Intent intent = new Intent(getActivity(), JGMain2Activity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        RelativeLayout button3 = (RelativeLayout) curView.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), bpmMainActivity.class);
                CenterDatabase cd = new CenterDatabase(getActivity(), null);
                intent.putExtra("userId", cd.getUID());
                cd.close();
                startActivity(intent);
            }
        });

        RelativeLayout button4 = (RelativeLayout) curView.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), bpmMainActivity.class);
                CenterDatabase cd = new CenterDatabase(getActivity(), null);
                intent.putExtra("userId", cd.getUID());
                cd.close();
                // TODO

                startActivity(intent);
            }
        });

        RelativeLayout button5 = (RelativeLayout) curView.findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), bpmMainActivity.class);
                CenterDatabase cd = new CenterDatabase(getActivity(), null);
                intent.putExtra("userId", cd.getUID());
                cd.close();
                // TODO

                startActivity(intent);
            }
        });

        setTopTitle(getActivity().getString(R.string.main_work));
        return curView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initHandler() {
    }

}
