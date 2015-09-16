package yh729_Fragment;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Constant;
import yh729_UI.yh729_HeadControlPanel;


/**
 * Created by Yanhao on 15-7-31.
 */
public class yh729_MainFragment extends Fragment {

    public static yh729_MainFragment yh729MainFragment;
    private Activity  mMainActivity;
    private View Layout;
    private FragmentManager fragmentManager = null;

    private String type= yh729_Constant.MY_REMIND;
    private String tab="";

    private TextView tv1;
    private TextView tv2;
    private View underline1;
    private View underline2;

    private int CHECKED_COLOR = Color.rgb(16,107,185);
    private int UNCHECKED_COLOR = Color.rgb(149,149,149);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
                Layout = inflater.inflate(R.layout.yh729_fragment_main, container, false);
                yh729MainFragment =this;
                mMainActivity = getActivity();
                tv1 = (TextView)Layout.findViewById(R.id.title1);
                tv2 = (TextView)Layout.findViewById(R.id.title2);
                underline1 = Layout.findViewById(R.id.title1_bar);
                underline2 = Layout.findViewById(R.id.title2_bar);
                try {
                    fragmentManager = getFragmentManager();
                }catch(Exception e){
                    Log.i("test", e.toString());
                    e.printStackTrace();
                }
                InitUI();
        }catch(Exception e){
            Log.i("test",e.toString());
            e.printStackTrace();
        }
        return Layout;
    }
    /*
        在MainFragment的OnCreateView()方法中调用，根据intent中的type字段的值初始化标题栏和tab
    */
    private void InitUI(){
        FragmentTransaction fragmentTransaction;
        Intent intent=mMainActivity.getIntent();
        if(intent.getExtras()!=null){
            Bundle bundle=intent.getExtras();
            type=bundle.getString("type");
        }
        yh729_HeadControlPanel headControlPanel=(yh729_HeadControlPanel)Layout.findViewById(R.id.m_head);
        headControlPanel.setMiddleTitle(type);
        if(type.equals(yh729_Constant.MY_REMIND))
        {
            headControlPanel.setmRightTitle("设置");
        }
        else
            headControlPanel.setLeftTitle();
        initTab();
        tab=(String)tv1.getText();
        Checked(tv1, underline1);
        unChecked(tv2, underline2);
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.m_context, new yh729_mFragment(), tab.equals("") ? type : tab);
        fragmentTransaction.commit();
    }
    public String getType(){
        return type;
    }
    public String getTab(){
        return tab;
    }

    /*
    *设置tab是否被选取，
    * tv、underline表示对应的textView、下划线
    */
    private void Checked(TextView tv,View underline){
        tv.setTextColor(CHECKED_COLOR);
        underline.setBackgroundColor(CHECKED_COLOR);
    }
    private void unChecked(TextView tv,View underline){
        tv.setTextColor(UNCHECKED_COLOR);
        underline.setBackgroundColor(Color.WHITE);
    }
    /*
    * 根据intent中的type初始化tab，设置对应的文字，和tab的Visibility
    * */
    private void initTab(){
        switch (type){
            case yh729_Constant.INSTRUCTION_REMIND:
            case yh729_Constant.DAIRY_REMIND:
            case yh729_Constant.CHECK_REMIND:
            case yh729_Constant.FOCUS_REMIND:
            case yh729_Constant.NEEDREPLY:
            case yh729_Constant.ATME:
            case yh729_Constant.ATMYSECTOR:
            case yh729_Constant.SCHEDULE_REMIND:
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                underline1.setVisibility(View.VISIBLE);
                underline2.setVisibility(View.VISIBLE);
                setTabname();
                FrameLayout frameLayout = (FrameLayout)Layout.findViewById(R.id.m_context);
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, R.id.base_activity_subline);
                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFragment(tv1);
                        Checked(tv1,underline1);
                        unChecked(tv2, underline2);
                    }
                });
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFragment(tv2);
                        Checked(tv2,underline2);
                        unChecked(tv1, underline1);
                    }
                });
                break;
            default:
                tv1.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                underline1.setVisibility(View.INVISIBLE);
                underline2.setVisibility(View.INVISIBLE);
                break;
        }
    }
    private void attachFragment(TextView tv)
   {
        fragmentManager.beginTransaction().replace(R.id.m_context,new yh729_mFragment(),(String)tv.getText()).commit();
    }
    /*
    * 根据type设置tab的text
    * */
    private void setTabname() {
        switch (type) {
            case yh729_Constant.INSTRUCTION_REMIND:
                tv1.setText(yh729_Constant.INSTRUCTION_REMIND1);
                tv2.setText(yh729_Constant.INSTRUCTION_REMIND2);
                break;
            case yh729_Constant.DAIRY_REMIND:
                tv1.setText(yh729_Constant.DAIRY_REMIND1);
                tv2.setText(yh729_Constant.DAIRY_REMIND2);
                break;
            case yh729_Constant.CHECK_REMIND:
                tv1.setText(yh729_Constant.CHECK_REMIND1);
                tv2.setText(yh729_Constant.CHECK_REMIND2);
                break;
            case yh729_Constant.FOCUS_REMIND:
                tv1.setText(yh729_Constant.FOCUS_REMIND1);
                tv2.setText(yh729_Constant.FOCUS_REMIND2);
                break;
            case yh729_Constant.NEEDREPLY:
                tv1.setText(yh729_Constant.NEEDREPLY1);
                tv2.setText(yh729_Constant.NEEDREPLY2);
                break;
            case yh729_Constant.ATME:
                tv1.setText(yh729_Constant.ATME1);
                tv2.setText(yh729_Constant.ATME2);
                break;
            case yh729_Constant.ATMYSECTOR:
                tv1.setText(yh729_Constant.ATMYSECTOR1);
                tv2.setText(yh729_Constant.ATMYSECTOR2);
                break;
            case yh729_Constant.SCHEDULE_REMIND:
                tv1.setText(yh729_Constant.SCHEDULE_REMIND1);
                tv2.setText(yh729_Constant.SCHEDULE_REMIND2);
                break;
            default:
                tv1.setText("");
                tv2.setText("");
        }
    }
}
