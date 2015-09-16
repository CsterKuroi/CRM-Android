/*
package com.pwp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderEditText;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.vo.ScheduleVO;

import java.util.ArrayList;

public class ScheduleInfoView2 extends Activity  {

	private LinearLayout layout = null;
	private LinearLayout layouttop = null;
	private  TextView textTop = null;
	private BorderTextView info = null;
	private BorderTextView date = null;
	private BorderTextView type = null;
	private BorderEditText editInfo = null;
	private LinearLayout ll;
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO = null;
	ArrayList<String>  scheduleDate;
	private ScheduleDAO dao2 = null;
	int width;int height;

	
	//private String scheduleInfo = "";    //日程信息被修改前的内容
    //private String scheduleChangeInfo = "";  //日程信息被修改之后的内容
	private final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	private final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	private final LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,100);
	private final LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,100);
	//private final LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,70);
	String[] scheduleIDs;
	
	int flag=0;
	String aboutid="0";
	
	@Override
	protected void  onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SysApplication.getInstance().addActivity(this);
		dao = new ScheduleDAO(this);
		WindowManager wm = this.getWindowManager();
	      width = wm.getDefaultDisplay().getWidth();
	      height = wm.getDefaultDisplay().getHeight();
	    final LayoutParams params4 = new LayoutParams(width/5, 80);
	    final LayoutParams params5 = new LayoutParams(4*width/25, 100);
        //final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 0);
		layout = new LinearLayout(this); // 实例化布局对象
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundResource(R.drawable.schedule_bk);
		layout.setLayoutParams(params);
		
		layouttop=new LinearLayout(this);
		layouttop.setOrientation(LinearLayout.HORIZONTAL);
		//layouttop.setBackgroundResource(R.drawable.schedule_bk);
		layouttop.setLayoutParams(params3);
		layouttop.setBackgroundResource(R.drawable.bgbar);
		
		textTop = new TextView(this, null);
		textTop.setTextColor(Color.WHITE); 
		//textTop.setBackgroundResource(R.drawable.top_day);
		textTop.setText(Html.fromHtml("<b>日程详情</b>"));
		textTop.setLayoutParams(params4);
		textTop.setGravity(Gravity.CENTER);
		TextView tt=new TextView (this);
		tt.setWidth(6*width/25);
		TextView tt2=new TextView (this);
		tt2.setWidth(6*width/25);
		
		TextView tv=new TextView (this);
		tv.setText("日程");
		tv.setTextSize(16);
		//tv.setHeight(45);
	    tv.getPaint().setFakeBoldText(true);//textview 加粗
		
		tv.setTextColor(Color.WHITE);
		tv.setWidth(4*width/25);
		//tv.setHeight(40);
		tv.setClickable(true);tv.setFocusable(true);
	   tv.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
               Intent i=new Intent();
               i.setClass(ScheduleInfoView2.this, CalendarActivity.class);
               startActivity(i);
				
			}
		});
		
		ImageButton im1=new ImageButton(ScheduleInfoView2.this);
		im1.setBackgroundResource(R.drawable.back);
		im1.setLayoutParams(params5);
		im1.setFocusable(true);
		im1.setClickable(true);
	im1.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
               finish();
				
			}
		});
		layouttop.addView(im1);layouttop.addView(tt);layouttop.addView(textTop);layouttop.addView(tt2);layouttop.addView(tv);
		layout.addView(layouttop);
		editInfo = new BorderEditText(ScheduleInfoView2.this, null);
		editInfo.setTextColor(Color.BLACK); 
		editInfo.setBackgroundColor(Color.WHITE);
		editInfo.setHeight(400);
		editInfo.setGravity(Gravity.TOP);
		editInfo.setLayoutParams(params);
		editInfo.setPadding(10, 5, 10, 5);

		
		//calendar_popup_view.addView(textTop);
	
		Intent intent = getIntent();
			String scid = intent.getStringExtra("scheduleID");

			new  AlertDialog.Builder(ScheduleInfoView2.this)
			.setTitle("标题")
			.setMessage(scid)
			.setPositiveButton("确定" ,  null )  
			.show();

			
			
		//scheduleID = Integer.parseInt(intent.getStringExtra("scheduleID"));
		//一个日期可能对应多个标记日程(scheduleID)
		
		this.setContentView(layout);	
	
	      	//显示日程详细信息
		handlerInfo(Integer.parseInt(scid));
		
for(int i = 0; i< scheduleIDs.length; i++){
			handlerInfo(Integer.parseInt(scheduleIDs[i]),i);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(1, Menu.FIRST, Menu.FIRST, "所有日程");
		menu.add(1, Menu.FIRST+1, Menu.FIRST+1,"添加日程");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent intent = new Intent();
			intent.setClass(ScheduleInfoView2.this, ScheduleAll.class);
			startActivity(intent);
			break;
        case Menu.FIRST+1:
        	Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
		t.setToNow(); // 取得系统时间。  
		int year = t.year;  
		int month = t.month;  
		int date = t.monthDay;  
		int hour = t.hour; // 0-23  
		int minute = t.minute;  
		int second = t.second;  
			 ArrayList<String> scheduleDate = new ArrayList<String>();
        scheduleDate.add(Integer.toString(year));
        scheduleDate.add(Integer.toString(month));
        scheduleDate.add(Integer.toString(date));
        scheduleDate.add("");
        //scheduleDate.add(scheduleLunarDay);
        
        
        Intent intent1 = new Intent();
        intent1.putStringArrayListExtra("scheduleDate", scheduleDate);
        //intent.setClass(CalendarActivity.this, ScheduleView.class);
			intent1.setClass(ScheduleInfoView2.this,add.class);
			this.startActivity(intent1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	


	public void handlerInfo(int scheduleID){
		//final int scheduleID2=scheduleID;
		final int ss=scheduleID;
		BorderTextView date = new BorderTextView(this, null);
		date.setTextColor(Color.BLACK); 
		date.setBackgroundColor(Color.WHITE);
		date.setLayoutParams(params);
		date.setGravity(Gravity.CENTER_VERTICAL);
		date.setHeight(80);
		date.setPadding(10, 0, 10, 0);
		
		BorderTextView type = new BorderTextView(this, null);
		type.setTextColor(Color.BLACK); 
		type.setBackgroundColor(Color.WHITE);
		type.setLayoutParams(params5);
		//type.setGravity(Gravity.CENTER);
		type.setGravity(Gravity.CENTER_VERTICAL);
		type.setGravity(Gravity.CENTER);
		//type.setHeight(50);
		type.setPadding(10, 0, 10, 0);
		type.setTag(scheduleID);
		type.setFocusable(true);
		type.setClickable(true);
		BorderTextView type2 = new BorderTextView(this, null);
		type2.setTextColor(Color.BLACK); 
		type2.setBackgroundColor(Color.LTGRAY);
		type2.setLayoutParams(params);
		//type.setGravity(Gravity.CENTER);
		type2.setGravity(Gravity.CENTER_VERTICAL);
		type2.setHeight(200);
		type2.setPadding(10, 0, 10, 0);
		type2.setTag(scheduleID);
		type2.setFocusable(true);
		type2.setClickable(true);
		
		final BorderTextView info = new BorderTextView(this, null);
		info.setTextColor(Color.BLACK); 
		info.setTextSize(16);
		info.setBackgroundColor(Color.WHITE);
		info.setGravity(Gravity.CENTER_VERTICAL);
		info.setLayoutParams(params);
		//info.setHeight(80);
		info.setPadding(10, 5, 10, 5);

		TextView tttt=new TextView (this);
		tttt.setHeight(30);
		layout.addView(tttt);

		//calendar_popup_view.addView(tttt);
		layout.addView(type);
		//calendar_popup_view.addView(type2);
		//calendar_popup_view.addView(date);
		layout.addView(info);
Intent intent = getIntent();
		int scheduleID = Integer.parseInt(intent.getStringExtra("scheduleID"));

		scheduleVO = dao.getScheduleByID(ss);
		//date.setText(scheduleVO.getScheduleDate());
		type.setText(Html.fromHtml("  <b><font size='20'>"+CalendarConstant.sch_type[scheduleVO.getScheduleTypeID()]+"</font></b>"));
	//	type2.setText(Html.fromHtml("<b>时间:</b>"+scheduleVO.getScheduleDate().trim()+"—>"+scheduleVO.getScheduleDate2()+"<br><b> 提醒：</b>"+scheduleVO.getpriority()+" "+CalendarConstant.remind[scheduleVO.getRemindID()]));
		info.setText(Html.fromHtml("<b> 时间:&nbsp&nbsp&nbsp&nbsp&nbsp</b>"+scheduleVO.getScheduleDate().trim()+"<font color='#dd0000'>—></font>"+scheduleVO.getScheduleDate2()+
				"<br>"+"<b> 优先级: </b>"+scheduleVO.getpriority()+
				" "+"<br><b> 参与人: </b>"+scheduleVO.getparticipant()+"<br>"+"<b> 备注:&nbsp&nbsp&nbsp&nbsp&nbsp</b>"+scheduleVO.getScheduleContent()));
    ll=new LinearLayout(this);
		ll.setLayoutParams(params2);
		ll.setVisibility(View.GONE);
		ll.setTag("do");
		ll.setId(1);


		LinearLayout lll = new LinearLayout(ScheduleInfoView2.this);
		lll.setOrientation(LinearLayout.HORIZONTAL);
		lll.setLayoutParams(params5);
		lll.setId(2);
		lll.setBackgroundColor(Color.LTGRAY);
		//lll.setHeight(15);

		TextView ttt = new TextView(ScheduleInfoView2.this);
		ttt.setWidth(width/4);ttt.setHeight(20);
		TextView ttt2 = new TextView(ScheduleInfoView2.this);
		ttt2.setWidth(width/9);ttt2.setHeight(20);
		final Button bb = new Button(this, null);
		bb.setText("编辑");
		bb.setHeight(45);
		bb.setWidth(width/5);
		// bb.setGravity(1);
		// bb.
		final Button bb2 = new Button(this, null);
		bb2.setText("删除");
		bb2.setId(2);
		bb2.setHeight(45);
		bb2.setWidth(width/5);
		//bb.setPadding(10, 10, 50, 10);
		lll.addView(ttt);
		lll.addView(bb);
		lll.addView(ttt2);
		lll.addView(bb2);
	
		//b2.setGravity(Gravity.CENTER);
		//ll.addView(bb);ll.addView(bb2);
ll.addView(lll);
		ll.setGravity(Gravity.CENTER);

		layout.addView(lll);
		
		//scheduleVO = dao.getScheduleByID(ss);

		final ArrayList<String> scheduleID2 = new ArrayList<String>();
		scheduleID2.add(String.valueOf(ss));
		
		
		Toast.makeText(ScheduleInfoView2.this, "chanxun"+"select aboutID from schedule where scheduleID='"+scheduleID+"'", Toast.LENGTH_LONG).show();
		
		
		//Log.d("dddddddddddddddddddd","chanxun"+"select aboutID from schedule where scheduleID='"+scheduleID+"'");
		
		DBOpenHelper dbOpenHelper=new DBOpenHelper(ScheduleInfoView2.this, "schedules.db");
		 SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select aboutID from schedule where scheduleID='"+ss+"'",null);
        if(cursor.moveToFirst()) {
        	 aboutid=cursor.getString(0);
        }
		 
		 //System.out.print(""+scheduleVO.getScheduleDate().split("-")[0].toString()+scheduleVO.getScheduleDate().split("-")[1].toString()+scheduleVO.getScheduleDate().split("-")[2].toString());
		bb.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				if(aboutid=="0") {
					Intent intent = new Intent();
					//intent.putStringArrayListExtra("scheduleDate", scheduleDate);
					intent.putStringArrayListExtra("rewrite", scheduleID2);
					intent.setClass(ScheduleInfoView2.this, add.class);
					startActivity(intent);
Intent i=new Intent();
					i.putStringArrayListExtra("scheduleDate",scheduleDate);
					i.setClass(ScheduleInfoView2.this, add.class);
					startActivity(i);

				}
				else {
					 new  AlertDialog.Builder(ScheduleInfoView2.this)
						.setTitle("不能编辑" )
						.setMessage("该日程不能被编辑")
						.setPositiveButton("确定" ,  null )
						.show();
				}
				
			}
		});
		bb2.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
	           //final String scheduleID = String.valueOf(v.getTag());
				
				
				if(aboutid=="0") {
			
				new AlertDialog.Builder(ScheduleInfoView2.this).setTitle("删除日程").setMessage("确认删除").setPositiveButton("确认", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dao.delete(ss);
						Intent intent1 = new Intent();
						intent1.putExtra("scheduleID", scheduleID2);
						intent1.setClass(ScheduleInfoView2.this, ScheduleAll.class);
						startActivity(intent1);
					}
				}).setNegativeButton("取消", null).show();
				
				}else {
					new  AlertDialog.Builder(ScheduleInfoView2.this)
					.setTitle("不能删除" )
					.setMessage("该日程不能被删除")
					.setPositiveButton("确定" ,  null )
					.show();
				}
			}
		});
		
		
		//长时间按住日程类型textview就提示是否删除日程信息
	type.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
                if(flag==0){ ll.setVisibility(View.VISIBLE); flag=1;}
                else {ll.setVisibility(View.GONE); flag=0;}
			
				
			}
		});


		
	}
}
*/
