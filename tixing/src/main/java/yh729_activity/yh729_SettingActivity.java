package yh729_activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.yanhao.task729.R;
import com.example.yanhao.task729.yh729_Constant;
import com.ricky.database.CenterDatabase;

import yh729_DB.yh729_mSQLiteOpenHelper;
import yh729_UI.yh729_HeadControlPanel;

/**
 * Created by yan on 2015/8/4.
 */
public class yh729_SettingActivity extends Activity {

    private CenterDatabase cdb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cdb=new CenterDatabase(getApplicationContext(),null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.yh729_setting_activity);
        yh729_HeadControlPanel head = (yh729_HeadControlPanel) findViewById(R.id.s_head);
        head.setMiddleTitle("设置");
        head.setLeftTitle();

        Switch remind = (Switch) findViewById(R.id.setting_remind);
        final Switch sound = (Switch) findViewById(R.id.sound);
        final Switch vibration = (Switch) findViewById(R.id.vibration);
        final Switch schedule_remind = (Switch) findViewById(R.id.schedule_remind);
        final Switch diary_remind = (Switch) findViewById(R.id.diary_remind);
        final Switch censor_remind = (Switch) findViewById(R.id.censor_remind);
        final Switch censor_sound = (Switch) findViewById(R.id.sound_censor);
        final Switch schedule_sound = (Switch) findViewById(R.id.sound_schedule);
        final Switch diary_sound = (Switch) findViewById(R.id.sound_diary);
        final Switch censor_vibration = (Switch) findViewById(R.id.vibration_censor);
        final Switch diary_vibration = (Switch) findViewById(R.id.vibration_diary);
        final Switch schedule_vibration = (Switch) findViewById(R.id.vibration_schedule);

        final RelativeLayout censor = (RelativeLayout) findViewById(R.id.censor);
        final RelativeLayout diary = (RelativeLayout) findViewById(R.id.diary);
        final RelativeLayout schedule = (RelativeLayout) findViewById(R.id.schedule);

        final View bar1 = findViewById(R.id.bar1);
        final View bar2 = findViewById(R.id.bar2);
        final View bar3 = findViewById(R.id.bar3);

        remind.setChecked(yh729_Constant.notification);
        sound.setChecked(yh729_Constant.sound);
        vibration.setChecked(yh729_Constant.vibrate);
        schedule_remind.setChecked(yh729_Constant.schedule_notification);
        schedule_sound.setChecked(yh729_Constant.schedule_sound);
        schedule_vibration.setChecked(yh729_Constant.schedule_vibrate);
        censor_remind.setChecked(yh729_Constant.censor_notification);
        censor_sound.setChecked(yh729_Constant.censor_sound);
        censor_vibration.setChecked(yh729_Constant.censor_vibrate);
        diary_remind.setChecked(yh729_Constant.diary_notification);
        diary_sound.setChecked(yh729_Constant.diary_sound);
        diary_vibration.setChecked(yh729_Constant.diary_vibrate);

        if(!remind.isChecked()) {
            vibration.setVisibility(View.GONE);
            sound.setVisibility(View.GONE);
            censor.setVisibility(View.GONE);
            schedule.setVisibility(View.GONE);
            diary.setVisibility(View.GONE);
        }
        else{
            if(!sound.isChecked()||!censor_remind.isChecked())
                censor_sound.setVisibility(View.GONE);
            if(!sound.isChecked()||!diary_remind.isChecked())
                diary_sound.setVisibility(View.GONE);
            if(!sound.isChecked()||!schedule_remind.isChecked())
                schedule_sound.setVisibility(View.GONE);
            if(!vibration.isChecked()||!censor_remind.isChecked())
                censor_vibration.setVisibility(View.GONE);
            if(!vibration.isChecked()||!diary_remind.isChecked())
                diary_vibration.setVisibility(View.GONE);
            if(!vibration.isChecked()||!schedule_remind.isChecked())
                schedule_vibration.setVisibility(View.GONE);
        }



        remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sound.setVisibility(View.VISIBLE);
                    vibration.setVisibility(View.VISIBLE);
                    censor.setVisibility(View.VISIBLE);
                    diary.setVisibility(View.VISIBLE);
                    schedule.setVisibility(View.VISIBLE);
                    yh729_Constant.notification=true;
                } else {
                    sound.setVisibility(View.GONE);
                    vibration.setVisibility(View.GONE);
                    censor.setVisibility(View.GONE);
                    diary.setVisibility(View.GONE);
                    schedule.setVisibility(View.GONE);
                    yh729_Constant.notification=false;
                }
            }
        });

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    censor_sound.setVisibility(View.VISIBLE);
                    diary_sound.setVisibility(View.VISIBLE);
                    schedule_sound.setVisibility(View.VISIBLE);
                    yh729_Constant.sound=true;
                } else {
                    censor_sound.setVisibility(View.GONE);
                    diary_sound.setVisibility(View.GONE);
                    schedule_sound.setVisibility(View.GONE);
                    yh729_Constant.sound=false;
                }
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    censor_vibration.setVisibility(View.VISIBLE);
                    diary_vibration.setVisibility(View.VISIBLE);
                    schedule_vibration.setVisibility(View.VISIBLE);
                    yh729_Constant.vibrate=true;
                } else {
                    censor_vibration.setVisibility(View.GONE);
                    diary_vibration.setVisibility(View.GONE);
                    schedule_vibration.setVisibility(View.GONE);
                    yh729_Constant.vibrate=false;
                }
            }
        });

        schedule_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    schedule_sound.setVisibility(View.VISIBLE);
                    schedule_vibration.setVisibility(View.VISIBLE);
                    yh729_Constant.schedule_notification=true;
                } else {
                    schedule_sound.setVisibility(View.GONE);
                    schedule_vibration.setVisibility(View.GONE);
                    yh729_Constant.schedule_notification=false;
                }
            }
        });

        diary_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    diary_sound.setVisibility(View.VISIBLE);
                    diary_vibration.setVisibility(View.VISIBLE);
                    yh729_Constant.diary_notification=true;
                } else {
                    diary_sound.setVisibility(View.GONE);
                    diary_vibration.setVisibility(View.GONE);
                    yh729_Constant.diary_notification=false;
                }
            }
        });

        censor_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    censor_sound.setVisibility(View.VISIBLE);
                    censor_vibration.setVisibility(View.VISIBLE);
                    yh729_Constant.censor_notification=true;
                } else {
                    censor_sound.setVisibility(View.GONE);
                    censor_vibration.setVisibility(View.GONE);
                    yh729_Constant.censor_notification=false;
                }
            }
        });

        censor_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    yh729_Constant.censor_sound=isChecked;
            }
        });

        censor_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    yh729_Constant.censor_vibrate=isChecked;
            }
        });

        schedule_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    yh729_Constant.censor_sound=isChecked;
            }
        });

        schedule_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    yh729_Constant.schedule_vibrate=isChecked;
            }
        });

        diary_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                yh729_Constant.diary_sound=isChecked;
            }
        });

        diary_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                yh729_Constant.diary_vibrate=isChecked;
            }
        });
    }

    public void onDestroy(){
        super.onDestroy();
        SQLiteDatabase db=new yh729_mSQLiteOpenHelper(getApplicationContext(), "remind_"+cdb.getUID(), null, 1).getWritableDatabase();
        db.execSQL("update remind_setting set " +
                "notification='"+ yh729_Constant.notification+"',"+
                "sound='"+ yh729_Constant.sound+"',"+
                "vibration='"+ yh729_Constant.vibrate+"',"+
                "censor_notification='"+ yh729_Constant.censor_notification+"',"+
                "censor_sound='"+ yh729_Constant.censor_sound+"',"+
                "censor_vibration='"+ yh729_Constant.censor_vibrate+"',"+
                "schedule_notification='"+ yh729_Constant.schedule_notification+"',"+
                "schedule_sound='"+ yh729_Constant.schedule_sound+"',"+
                "schedule_vibration='"+ yh729_Constant.schedule_vibrate+"',"+
                "diary_notification='"+ yh729_Constant.diary_notification+"',"+
                "diary_sound='"+ yh729_Constant.diary_sound+"',"+
                "diary_vibration='"+ yh729_Constant.diary_vibrate+"'"+
                "where id ='0'");
    }
}
