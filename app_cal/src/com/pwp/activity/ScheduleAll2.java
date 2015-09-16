package com.pwp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.ScheduleDAO;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import java.util.ArrayList;

/*import com.mogujie.tt.R;*/

/**
 * 显示/修改所有日程的activity
 * @author jack_peng
 *
 */
public class ScheduleAll2 extends Activity {

	private ScrollView sv = null;
	private LinearLayout layout = null;
	private LinearLayout layouttop = null;
	private  TextView textTop = null;
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO = null;
	private ArrayList<ScheduleVO> schList = new ArrayList<ScheduleVO>();
	private String scheduleInfo = "";
	private final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	private final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	private final LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,100);
	private final LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);

	private int scheduleID = -1;
	private String userid="";
	
	int width;int height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SysApplication.getInstance().addActivity(this);


		CenterDatabase centerDatabase = new CenterDatabase(this, null);
		userid = centerDatabase.getUID();

		dao = new ScheduleDAO(this);
		//sv = new ScrollView(this);
		LinearLayout l1=new LinearLayout(this);
		l1.setLayoutParams(params);
		l1.setOrientation(LinearLayout.VERTICAL);
		
		WindowManager wm = this.getWindowManager();
	      width = wm.getDefaultDisplay().getWidth();
	      height = wm.getDefaultDisplay().getHeight();
	      
	    final LayoutParams params4 = new LayoutParams(width/5, 100);
		final LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, height);
		  final LayoutParams params5 = new LayoutParams(4*width/25, 80);
		l1.setLayoutParams(params);
		params.setMargins(0, 5, 0, 0);
		layout = new LinearLayout(this); // 实例化布局对象
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundResource(R.drawable.schedule_bk);
		layout.setBackgroundColor(Color.WHITE);
		layout.setLayoutParams(params);
		
		layouttop=new LinearLayout(this);
		layouttop.setOrientation(LinearLayout.HORIZONTAL);
		//layouttop.setBackgroundResource(R.drawable.schedule_bk);
		layouttop.setLayoutParams(params3);
		layouttop.setBackgroundResource(R.drawable.bgbar);
		//layouttop.setPadding(0, 0, 0, 20);
		
		textTop = new TextView(this, null);
		textTop.setTextColor(Color.WHITE); 
		//textTop.setBackgroundResource(R.drawable.top_day);
		textTop.setText(Html.fromHtml("<b>所有日程</b>"));
		textTop.setLayoutParams(params4);
		textTop.setGravity(Gravity.CENTER);
		TextView tt=new TextView (this);
		tt.setWidth(6*width/25);
		TextView tt2=new TextView (this);
		tt2.setWidth(6*width/25);
		
		ImageButton im1=new ImageButton(ScheduleAll2.this);
		im1.setBackgroundResource(R.drawable.back);
		im1.setLayoutParams(params5);
		im1.setFocusable(true);
		im1.setClickable(true);
		TextView tv=new TextView (this);
		tv.setText("日程");
		tv.setTextSize(16);
		//tv.setHeight(50);
	    tv.getPaint().setFakeBoldText(true);//textview 加粗

		tv.setTextColor(Color.WHITE);
		tv.setWidth(4*width/25);
	im1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
               finish();
			}
		});
	tv.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
          Intent intent=new Intent();
          intent.setClass(ScheduleAll2.this, CalendarActivity.class);
          startActivity(intent);
		}
	});
		layouttop.addView(im1);layouttop.addView(tt);layouttop.addView(textTop);layouttop.addView(tt2);layouttop.addView(tv);
		layout.addView(layouttop);
		
		TextView ttt=new TextView (this);
		ttt.setHeight(20);
		layout.addView(ttt);
		
		l1.addView(layout);
		/*LinearLayout ll2 = new LinearLayout(this);
		ll2.setOrientation(LinearLayout.VERTICAL);
		ll2.setLayoutParams(params6);
		ll2.setBackgroundColor(Color.WHITE);*/
		//sv.addView(ll2);
		//sv.getChildAt(0).getMeasuredHeight();
		//l1.setLayoutParams(paramss);
		
		setContentView(l1);
		
		//application userid=new application();
		
		

		getScheduleAll(userid);
	}
	
	/**
	 * 得到所有的日程信息
	 */
	public void getScheduleAll(String createrID){
		schList = dao.getAllSchedule( createrID);
		if(schList != null){
			for (ScheduleVO vo : schList) {
				String content = vo.getScheduleContent();
				int startLine = content.indexOf("\n");
				if(startLine > 0){
					content = content.substring(0, startLine)+"...";
				}else if(content.length() > 30){
					content = content.substring(0, 30)+"...";
				}
				scheduleInfo = " <font color='#dd0000'>"+CalendarConstant.sch_type[vo.getScheduleTypeID()]+"</font> <font color='#666600'>"+vo.getScheduleDate().trim()+"—>"+vo.getScheduleDate2().trim()+"</font> <font color='#dd0000'>"+vo.getpriority()+"</font> ";
				scheduleID = vo.getScheduleID();
				createInfotext(scheduleInfo, scheduleID);
			}
		}else{
			scheduleInfo = "没有日程";
			createInfotext(scheduleInfo,-1);
		}
	}
	
	/**
	 * 创建放日程信息的textview
	 */
	public void createInfotext(String scheduleInfo, int scheduleID){
		final BorderTextView info = new BorderTextView(this, null);
		info.setText(Html.fromHtml(scheduleInfo));
		info.setHeight(80);
		info.setTextColor(Color.BLACK); 
		info.setBackgroundColor(Color.WHITE);
		info.setLayoutParams(params3);
		info.setGravity(Gravity.CENTER_VERTICAL);
		info.setPadding(10, 5, 10, 5);
		info.setTag(scheduleID);
		layout.addView(info);
		
		if(scheduleInfo=="没有日程") {
			info.setFocusable(false);info.setClickable(false);
		}
		
		else {
			//点击每一个textview就跳转到shceduleInfoView中显示详细信息
		info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String schID = String.valueOf(v.getTag());
				String scheduleID= schID;
				Intent intent = new Intent();
				intent.setClass(ScheduleAll2.this, ScheduleInfoView.class);
				intent.putExtra("scheduleID", scheduleID);
				startActivity(intent);
			}
		});
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(1, Menu.FIRST, Menu.FIRST, "返回日历");
		menu.add(1, Menu.FIRST+1, Menu.FIRST+1, "添加日程");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case Menu.FIRST:
			Intent intent = new Intent();
			intent.setClass(ScheduleAll2.this, CalendarActivity.class);
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
			intent1.setClass(ScheduleAll2.this,add.class);
			this.startActivity(intent1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
