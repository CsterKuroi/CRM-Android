package com.pwp.activity;

import java.util.ArrayList;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderEditText;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.myservices.update_user_schedule;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ScheduleInfoView extends Activity  implements  View.OnClickListener {
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO = null;
	TextView time,time2,participant,note,richeng;
	RatingBar priority;
	Button edit,delete;
	RelativeLayout back;
	String userid,aboutid,scid,temp_userid,temp_aboutid,wsuri,str ;
	WebSocketConnection mConnection;

	@Override
	protected void  onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.schedule_detail);

		SysApplication.getInstance().addActivity(this);

		CenterDatabase centerDatabase = new CenterDatabase(this, null);
		userid = centerDatabase.getUID();
		wsuri = CenterDatabase.URI;
		centerDatabase.close();

		mConnection = new WebSocketConnection();
		dao = new ScheduleDAO(this);
		time=(TextView)findViewById(R.id.time);
		time2=(TextView)findViewById(R.id.time2);
		priority=(RatingBar)findViewById(R.id.rating);
		participant=(TextView)findViewById(R.id.participant);
		note=(TextView)findViewById(R.id.note);

		Intent intent = getIntent();
		scid = intent.getStringExtra("scheduleID");
		scheduleVO = dao.getScheduleByID(Integer.parseInt(scid));
		time.setText(scheduleVO.getScheduleDate());
		time2.setText(scheduleVO.getScheduleDate2());
		priority.setRating(scheduleVO.getpriority());    priority.setClickable(false); priority.setEnabled(false);
		participant.setText(idtoname(scheduleVO.getparticipant()));
		//Toast.makeText(ScheduleInfoView.this,scheduleVO.getparticipant(),Toast.LENGTH_SHORT).show();
		note.setText(scheduleVO.getScheduleContent());

		back=(RelativeLayout)findViewById(R.id.back);
		richeng=(TextView)findViewById(R.id.richeng);
		back.setOnClickListener(this);
		richeng.setOnClickListener(this);

		edit=(Button)findViewById(R.id.edit);
		delete=(Button)findViewById(R.id.delete);



		DBOpenHelper dbOpenHelper=new DBOpenHelper(ScheduleInfoView.this, "schedules.db");
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select createrID,aboutID from schedule where scheduleID='"+scid+"'",null);
		if(cursor.moveToFirst()) {
            temp_userid=cursor.getString(0);
			temp_aboutid=cursor.getString(1);
			if (!(temp_userid.trim().equals(userid) && temp_aboutid.trim().equals(userid))){
				edit.setClickable(false);
				edit.setVisibility(View.GONE);
			}
			//Toast.makeText(ScheduleInfoView.this,aboutid,Toast.LENGTH_LONG).show();
		}
		cursor.close();

		final ArrayList<String> scheduleID2 = new ArrayList<String>();
		scheduleID2.add(String.valueOf(scid));

		edit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(ScheduleInfoView.this,scid,Toast.LENGTH_LONG).show();
				if (temp_userid.trim().equals(userid) && temp_aboutid.trim().equals(userid)) {
					Intent intent = new Intent();
					intent.putStringArrayListExtra("rewrite", scheduleID2);
					intent.setClass(ScheduleInfoView.this, add.class);
					startActivity(intent);
				/*	Intent i = new Intent();
					i.putStringArrayListExtra("scheduleDate", scheduleDate);
					i.setClass(ScheduleInfoView2.this, add.class);
					startActivity(i);*/
				} else {
					new AlertDialog.Builder(ScheduleInfoView.this)
							.setTitle("不能编辑")
							.setMessage("该日程不能被编辑")
							.setPositiveButton("确定", null)
							.show();
				}
			}
		});
		delete.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				//final String scheduleID = String.valueOf(v.getTag());

					new AlertDialog.Builder(ScheduleInfoView.this).setTitle("删除日程").setMessage("确认删除").setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*Intent intent1 = new Intent();
							intent1.putExtra("scheduleID", scheduleID2);
							intent1.setClass(ScheduleInfoView.this, ScheduleAll.class);
							startActivity(intent1);*/
							if (temp_userid.trim().equals(userid) && temp_aboutid.trim().equals(userid)) {
								//连接数据库，将位置为0
								try {
									ScheduleVO vo = dao.getScheduleByID(Integer.parseInt(scid));

									//Toast.makeText(CalendarActivity.this, vo.toString(), Toast.LENGTH_LONG).show();

									String scheduleid = vo.getscheduleid2();
									//Toast.makeText(ScheduleInfoView.this, "删除日程在服务器端的id" + scheduleid, Toast.LENGTH_LONG).show();

									str = "{\"type\":\"1\", \"cmd\":\"1-3\",\"id\":\"" + scheduleid + "\"}";
									mConnection.connect(wsuri, new WebSocketHandler() {
										@Override
										public void onOpen() {
											Log.d("删除日程提交语句： ", str);
											mConnection.sendTextMessage(str);
										}

										@Override
										public void onTextMessage(String payload) {
											Log.d("删除日程服务器返回结果：", payload);
										//
										// .makeText(ScheduleInfoView.this, "删除日程服务器返回结果：" + payload, Toast.LENGTH_LONG).show();
										}

										@Override
										public void onClose(int code, String reason) {
											//Log.d(TAG, "Connection lost.");
										}

									});
								} catch (WebSocketException e) {
									//Log.d(TAG, e.toString());
								}
							}
							if (update_user_schedule.mConnection.isConnected()) {
								update_user_schedule.mConnection.sendTextMessage(str);
							}

							dao.delete(Integer.parseInt(scid));
							startActivity(new Intent().setClass(ScheduleInfoView.this, CalendarActivity.class)
							);
						}
					}).setNegativeButton("取消", null).show();

				/*else {
					new  AlertDialog.Builder(ScheduleInfoView.this)
							.setTitle("不能删除" )
							.setMessage("该日程不能被删除")
							.setPositiveButton("确定", null)
							.show();
				}*/
			}
		});
	}

	public String idtoname(String id){
		String ids[]=id.split(",");
		String strname="";
		for (int i=0;i<ids.length;i++) {
			if (ids[i] != "")
				strname += new CenterDatabase(ScheduleInfoView.this, null).getNameByUID(ids[i]) + ",";
		}
		strname = strname.substring(0, strname.length() - 1);
		return strname;
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.back){
			startActivity(new Intent(ScheduleInfoView.this,CalendarActivity.class));
			this.finish();
		}
		else if(v.getId()==R.id.richeng){
			startActivity(new Intent().setClass(ScheduleInfoView.this,CalendarActivity.class));
		}
	}
}
