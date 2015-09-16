package com.mogujie.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.code.bmj.groupnotifycation.GroupNotificationMainActivity;
import com.example.bmj.statistics_all.BMJStatisticsActivity;
import com.example.dt.testapp3.VisitMainActivity;
import com.example.jogle.calendar.JGCalendarActivity;
import com.example.renxin.shujia.crmstudy;
import com.mogujie.tt.R;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.pwp.activity.CalendarActivity;
import com.ricky.database.CenterDatabase;

public class AppFragment extends TTBaseFragment {
    private View curView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (curView != null) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_app,
                topContentView);

        LinearLayout mBtnAppCal = (LinearLayout) curView.findViewById(R.id.app_cal);
        mBtnAppCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout mBtnAppStudy = (LinearLayout) curView.findViewById(R.id.app_study);
        mBtnAppStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), crmstudy.class);
                startActivity(intent);
            }
        });
        LinearLayout mBtnAppAtt3 = (LinearLayout) curView.findViewById(R.id.app_att3);
        mBtnAppAtt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CenterDatabase centerDatabase = new CenterDatabase(getActivity(), null);
                int uid = Integer.parseInt(centerDatabase.getUID());
                String name = "";
                centerDatabase.close();

                Intent intent = new Intent(getActivity(), JGCalendarActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        LinearLayout mBtnAppKehubaifang = (LinearLayout) curView.findViewById(R.id.app_kehubaifang);
        mBtnAppKehubaifang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VisitMainActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout mBtnAppShuju = (LinearLayout) curView.findViewById(R.id.app_shuju);
        mBtnAppShuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BMJStatisticsActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout mBtnAppGN = (LinearLayout) curView.findViewById(R.id.group);
        mBtnAppGN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupNotificationMainActivity.class);
                startActivity(intent);
            }
        });

        setTopTitle(getActivity().getString(R.string.main_app));
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
