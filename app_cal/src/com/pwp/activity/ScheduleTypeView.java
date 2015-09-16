package com.pwp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;

import java.util.ArrayList;
/*import com.mogujie.tt.R;*/

/**
 * 日程类型选择
 * @author song
 *
 */
public class ScheduleTypeView extends Activity {
	private CalendarConstant cc = null;
	private int sch_typeID = 0;
	private int remindID = 0;
	private LinearLayout layout; // 布局 ， 可以在xml布局中获得
	private LinearLayout layButton;
	private RadioGroup group; // 点选按钮组
	private BorderTextView textTop = null;
	private RadioButton radio = null;
	private BorderTextView btSave = null;
	private BorderTextView btCancel = null;
	private TextView bt = null;
	private int schType_temp = 0;
	private int remind_temp = 0;
	private static ArrayList<String> pass=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SysApplication.getInstance().addActivity(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		cc = new CalendarConstant();
		layout = new LinearLayout(this); // 实例化布局对象,this为本activity
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundResource(R.drawable.schedule_bk);
		layout.setLayoutParams(params);
		group = new RadioGroup(this);
		btSave = new BorderTextView(this,null);
		btCancel = new BorderTextView(this, null);
		textTop = new BorderTextView(this, null);
		textTop.setTextColor(Color.BLACK);
		textTop.setBackgroundResource(R.drawable.top_day);
		textTop.setText("日程类型");
		textTop.setHeight(47);
		textTop.setGravity(Gravity.CENTER);
		layout.addView(textTop);

		Intent intent = getIntent();//get the intent that started this activity		
		int sch_remind[] = intent.getIntArrayExtra("sch_remind");  //从ScheduleView传来的值,日程类型与提醒类型
		pass=intent.getStringArrayListExtra("pass");

		if(sch_remind != null){
			sch_typeID = sch_remind[0];
			remindID = sch_remind[1];
		}
		for(int i = 0 ; i < CalendarConstant.sch_type.length ; i++){
			radio = new RadioButton(this);
			if(i == sch_typeID){
				radio.setChecked(true);
			}
			radio.setText(CalendarConstant.sch_type[i]);
			radio.setTextSize(25);
			radio.setId(i);
			radio.setTextColor(Color.BLACK);
			radio.setHeight(85);
			group.addView(radio);
		}
		group.setHorizontalGravity(1);
		layout.addView(group);
		layButton = new LinearLayout(this);
		layButton.setOrientation(LinearLayout.HORIZONTAL);
		//layButton.setBackgroundResource(R.drawable.schedule_bk);
		layButton.setLayoutParams(params);
		btSave.setTextColor(Color.BLACK);
		btSave.setBackgroundResource(R.drawable.top_day);
		btSave.setText("确定");
		btSave.setTextSize(25);
		btSave.setHeight(60);
		btSave.setWidth(160);
		btSave.setGravity(Gravity.CENTER);
		btSave.setClickable(true);
		btCancel.setTextColor(Color.BLACK);
		btCancel.setBackgroundResource(R.drawable.top_day);
		btCancel.setText("取消");
		btCancel.setTextSize(25);
		btCancel.setHeight(60);
		btCancel.setWidth(160);
		btCancel.setGravity(Gravity.CENTER);
		btCancel.setClickable(true);

		bt=new TextView(this);
		bt.setWidth(10);
		layButton.addView(btSave);
		layButton.addView(bt);
		layButton.addView(btCancel);
		layButton.setHorizontalGravity(1);
		layout.addView(layButton);
		this.setContentView(layout);

		//触发radioButton
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				schType_temp = checkedId;
				new AlertDialog.Builder(ScheduleTypeView.this).setTitle("提醒类型")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setSingleChoiceItems(
								new String[] { CalendarConstant.remind[0], CalendarConstant.remind[1], CalendarConstant.remind[2], CalendarConstant.remind[3], CalendarConstant.remind[4], CalendarConstant.remind[5], CalendarConstant.remind[6], CalendarConstant.remind[7] }, remindID,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,int which) {
										remind_temp = which;//提醒类型
									}
								}).setPositiveButton("确认", null).setNegativeButton("取消", null).show();
			}
		});

		//触发提醒类型确定按钮
		btSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sch_typeID = schType_temp;
				remindID = remind_temp;

				Intent intent = new Intent();
				intent.setClass(ScheduleTypeView.this, add.class);
				intent.putExtra("schType_remind", new int[]{sch_typeID,remindID});
				intent.putStringArrayListExtra("pass", pass);
				startActivity(intent);
			}
		});

		//触发取消按钮
		btCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(ScheduleTypeView.this, add.class);
				intent.putExtra("schType_remind", new int[]{sch_typeID,remindID});
				startActivity(intent);
			}
		});
	}
	
	/*public static String[] getSchType() {
		return sch_type;
	}

	public static String[] getRemind() {
		return remind;
	}*/
}
