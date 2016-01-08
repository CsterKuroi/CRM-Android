package com.mogujie.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bmj.statistics_all.BMJStatisticsActivity;
import com.example.jogle.attendance.JGMainActivity;
import com.geminy.productshow.Activity.GEMINY_MainActivity;
import com.kuroi.chance.activity.ChanceMainActivity;
import com.kuroi.contract.activity.ConMainActivity;
import com.melnykov.fab.sample.kehu.crm_kehu;
import com.melnykov.fab.sample.lianxiren.crm_lianxiren;
import com.mogujie.tt.R;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.ricky.database.CenterDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import me.tedyin.circleprogressbarlib.CircleProgressBar;

public class CRMFragment extends TTBaseFragment {
    private View curView = null;
    private CircleProgressBar bar3;
    private TextView t2, t4, t6;
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    private final String wsuri2 = "ws://101.200.189.127:1234/ws";
    private String tempjson="";
    private String tempjson2="";
    private WebSocketConnection mConnection;
    private WebSocketConnection mConnection2;
    private String userID="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (curView != null) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_crm, topContentView);

        CenterDatabase cd = new CenterDatabase(getActivity(), null);
        userID = cd.getUID();
        cd.close();

        RelativeLayout statics = (RelativeLayout) curView.findViewById(R.id.statics);
        statics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BMJStatisticsActivity.class));
            }
        });

        bar3 = (CircleProgressBar) curView.findViewById(R.id.bar3);
        t2 = (TextView) curView.findViewById(R.id.textView2);
        t4 = (TextView) curView.findViewById(R.id.textView4);
        t6 = (TextView) curView.findViewById(R.id.textView6);
        double d4=Double.parseDouble(t4.getText().toString());
        double d6=Double.parseDouble(t6.getText().toString());
        if (d6>0) {
            int rate = (int) (d4 / d6 * 100);
            bar3.setProgress(rate);
        }
//        down(downJson());
//        down2(downJson2());

//        new Thread() {
//            private int current = 0;
//            @Override
//            public void run() {
//                super.run();
//                while (current <= 100) {
//                    current++;
//                    bar3.setProgress(current);
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
        RelativeLayout crmAtt = (RelativeLayout) curView.findViewById(R.id.crm_att);
        crmAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JGMainActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout mBtnChance = (RelativeLayout) curView.findViewById(R.id.crm_jihui);
        mBtnChance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChanceMainActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout mBtnContract = (RelativeLayout) curView.findViewById(R.id.crm_hetong);
        mBtnContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ConMainActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout mBtnProductShow = (RelativeLayout) curView.findViewById(R.id.crm_chanpinzhanshi);
        mBtnProductShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GEMINY_MainActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout mBtnKehu = (RelativeLayout) curView.findViewById(R.id.crm_kehu);
        mBtnKehu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), crm_kehu.class));
                //动画跳转
//                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        RelativeLayout mBtnLianxiren = (RelativeLayout) curView.findViewById(R.id.crm_lianxiren);
        mBtnLianxiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), crm_lianxiren.class));
                //动画跳转
//                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


        setTopTitle(getActivity().getString(R.string.main_crm));
        return curView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            down(downJson());
            down2(downJson2());
        }
    }



    /**
     * 当月第一天
     * @return
     */
    private static String getFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        return str.toString();

    }

    /**
     * 当月最后一天
     * @return
     */
    private static String getLastDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 23:59:59");
        return str.toString();

    }

    public String downJson() {
        try {
            JSONObject object = new JSONObject();
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            String start=getFirstDay();
            Date date1 = format.parse(start);
            String end=getLastDay();
            Date date2 = format.parse(end);

            object.put("cmd", "getContractMoney");
            object.put("type", "8");
            object.put("uid", userID);
            object.put("start",String.valueOf(date1.getTime() / 1000));
            object.put("end",String.valueOf(date2.getTime() / 1000));
            return object.toString();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void down(String json) {
        if (mConnection == null) {
            mConnection = new WebSocketConnection();
        }
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json字段" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("getEcho", "发送Json字段"+tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }
                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("getEcho", "Got echo: " + payload);
                        try {
                            JSONObject object=null;
                            object = new JSONObject(payload);
                            String err = object.getString("error");
                            if (err.equals("1")){
                                t4.setText(object.getString("money"));
                                double d4=Double.parseDouble(t4.getText().toString());
                                double d6=Double.parseDouble(t6.getText().toString());
                                if (d6>0) {
                                    int rate = (int) (d4 / d6 * 100);
                                    bar3.setProgress(rate);
                                }
                                else
                                    bar3.setProgress(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("getEcho", "Connection closed: " + reason);
                        if (code == 2) ;
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }


    public String downJson2() {
        try {
            JSONObject object = new JSONObject();

            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            String start=getFirstDay();
            Date date1 = format.parse(start);
            String end=getLastDay();
            Date date2 = format.parse(end);

            object.put("cmd", "getTaskMoney");
            object.put("userId", userID);
            object.put("startTime",String.valueOf(date1.getTime() / 1000));
            object.put("endTime",String.valueOf(date2.getTime() / 1000));
            return object.toString();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void down2(String json) {
        if (mConnection2 == null) {
            mConnection2 = new WebSocketConnection();
        }
        if (mConnection2.isConnected()) {
            mConnection2.sendTextMessage(json);
            Log.d("test222222", "发送Json字段" + json);
        }else {
            try {
                tempjson2=json;
                mConnection2.connect(wsuri2, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段"+tempjson2);
                        mConnection2.sendTextMessage(tempjson2);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test22222", "Got echo: " + payload);
                        try {
                            JSONObject object=null;
                            object = new JSONObject(payload);
                            String err = object.getString("error");
                            if (err.equals("0")){

                                if (err.equals("0")){
                                    t6.setText(object.getString("datas"));
                                    double d4=Double.parseDouble(t4.getText().toString());
                                    double d6=Double.parseDouble(t6.getText().toString());
                                    if (d6>0) {
                                        int rate = (int) (d4 / d6 * 100);
                                        bar3.setProgress(rate);
                                    }
                                    else
                                        bar3.setProgress(0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2) ;
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }

}
