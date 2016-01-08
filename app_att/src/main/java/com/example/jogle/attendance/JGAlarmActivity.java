package com.example.jogle.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;


public class JGAlarmActivity extends Activity {
    private Switch aSwitch;
    private RelativeLayout l16;
    private RelativeLayout l18;
    private TextView t32;
    private TextView t36;
    private RelativeLayout ib;
    private String s;
    private String i1;
    private String i2;
    private String wt;
    private String ot;
    private JGDBOperation operation;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jg_activity_alarm);
        aSwitch = (Switch) findViewById(R.id.switch1);

        l16 = (RelativeLayout) findViewById(R.id.linearLayout16);
        l18 = (RelativeLayout) findViewById(R.id.linearLayout18);
        t32 = (TextView) findViewById(R.id.textView32);
        t36 = (TextView) findViewById(R.id.textView36);
        ib = (RelativeLayout) findViewById(R.id.back);
        operation = new JGDBOperation(getApplicationContext());
        List list=operation.getremind();
        Log.e("xxx", "0000000000");
        if (!list.isEmpty()) {
            Log.e("xxx", "11111111");
            if (list.get(0).toString().equals("1"))
                aSwitch.setChecked(true);
            t32.setText(list.get(3).toString());
            t36.setText(list.get(4).toString());
            s=aSwitch.isChecked()?"1":"0";
            i1=list.get(1).toString();
            i2=list.get(2).toString();
            wt=list.get(3).toString();
            ot=list.get(4).toString();
        }
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        l16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });

        l18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(2);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.e("cc","ccccccccccc");
                    //提醒
                    //i1=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                    //i2=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                    s="1";
                    i1="-1";
                    i2="-2";
                    wt=t32.getText().toString();
                    ot=t36.getText().toString();
                    operation.deleteremind();
                    operation.saveremind(s,i1,i2,wt,ot);
                } else {
                    //yh729_AlarmNotificationService.mService.cancelRemind(i1);
                    //yh729_AlarmNotificationService.mService.cancelRemind(i2);
                    operation.deleteremind();
                }
            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final ChoiceOnClickListener choiceListener =
                        new ChoiceOnClickListener();
                builder.setSingleChoiceItems(R.array.att_type, 0, choiceListener);
                DialogInterface.OnClickListener btnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                int choiceWhich = choiceListener.getWhich();
                                String typ =
                                        getResources().getStringArray(R.array.att_type)[choiceWhich];
                                t32.setText(typ);
                                s=aSwitch.isChecked()?"1":"0";
                                wt=t32.getText().toString();
                                ot=t36.getText().toString();
                                if(s.equals("1")){
                                    //yh729_AlarmNotificationService.mService.cancelRemind(i1);
                                    //yh729_AlarmNotificationService.mService.cancelRemind(i2);
                                    operation.deleteremind();
                                    i1="-1";//
                                    i2="-2";
                                    //i1=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                                    //i2=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                                    operation.saveremind(s,i1,i2,wt,ot);
                                }
                            }
                        };
                builder.setPositiveButton("确定", btnListener);
                dialog = builder.create();
                break;
            case 2:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                final ChoiceOnClickListener choiceListener2 =
                        new ChoiceOnClickListener();
                builder2.setSingleChoiceItems(R.array.att_type, 0, choiceListener2);
                DialogInterface.OnClickListener btnListener2 =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                int choiceWhich = choiceListener2.getWhich();
                                String typ =
                                        getResources().getStringArray(R.array.att_type)[choiceWhich];
                                t36.setText(typ);
                                s=aSwitch.isChecked()?"1":"0";
                                wt=t32.getText().toString();
                                ot=t36.getText().toString();
                                if(s.equals("1")){
                                    //yh729_AlarmNotificationService.mService.cancelRemind(i1);
                                    //yh729_AlarmNotificationService.mService.cancelRemind(i2);
                                    operation.deleteremind();
                                    i1="-1";//
                                    i2="-2";
                                    //i1=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                                    //i2=yh729_AlarmNotificationService.mService.setRemind("2015-08-20 14:15","请注意签到","inside_work",0,1)
                                    operation.saveremind(s,i1,i2,wt,ot);
                                }

                            }
                        };
                builder2.setPositiveButton("确定", btnListener2);
                dialog = builder2.create();
                break;
        }
        return dialog;
    }
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }
}
