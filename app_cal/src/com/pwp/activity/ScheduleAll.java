package com.pwp.activity;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pwp.constant.CalendarConstant;

/*import com.mogujie.tt.R;*/

/**
 * 显示/修改所有日程的activity
 * @author jack_peng
 *
 */
public class ScheduleAll extends Activity implements OnClickListener{

	ListView listView;
	RelativeLayout back;
	TextView richeng,nothing;
	String userid;
	int width,height;
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO = null;
	private ArrayList<ScheduleVO> schList = new ArrayList<ScheduleVO>();
	private String scheduleInfo = "";
	int []scheduleID;

	String mytype[]=new String[]{};
	String mypriorty[]=new String[]{};
	String mystart[]=new String[]{};
	String myend[]=new String[]{};

	int ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_schedule);

		SysApplication.getInstance().addActivity(this);

		CenterDatabase centerDatabase = new CenterDatabase(this, null);
		userid = centerDatabase.getUID();
		dao = new ScheduleDAO(this);

		WindowManager wm = this.getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();

		back=(RelativeLayout)findViewById(R.id.back);
		richeng=(TextView)findViewById(R.id.richeng);
		back.setOnClickListener(this);
		richeng.setOnClickListener(this);

		getScheduleAll(userid);
		ab=myend.length;

			if (schList==null||schList.size() == 0) {
				nothing=(TextView)findViewById(R.id.nothing);
				nothing.setVisibility(View.VISIBLE);
				//scheduleInfo = "没有日程!";
			}
		else {
	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
 for (int jj=0;jj<ab;jj++) {
		if (mytype[jj] != null) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("start", mystart[jj]);
			listItem.put("type", mytype[jj]);
			listItem.put("end", myend[jj]);
			listItem.put("priorty", mypriorty[jj]);
			listItems.add(listItem);
		}
	}
	SimpleAdapter simpleAdapter = new SimpleAdapter(ScheduleAll.this, listItems, R.layout.alll_item, new String[]{"type", "start","end","priorty"}, new int[]{R.id.all_type, R.id.all_start,R.id.all_end, R.id.all_priorty});
	listView = (ListView) findViewById(R.id.all);
	listView.setAdapter(simpleAdapter);

	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			//获得选中项的HashMap对象
			//Toast.makeText(ScheduleAll.this, "点击了" + arg2, Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setClass(ScheduleAll.this, ScheduleInfoView.class);
			intent.putExtra("scheduleID", String.valueOf(scheduleID[arg2]));
			startActivity(intent);
		}
	});
		}
}

	public void getScheduleAll(String aboutid){
		schList = dao.getAllSchedule(aboutid);
		if(schList != null){
			int ii=0;
			scheduleID=new int[schList.size()];
			mytype=new String [schList.size()];
			mypriorty=new String [schList.size()];
			mystart=new String [schList.size()];
			myend=new String [schList.size()];
			for (ScheduleVO vo : schList) {
				String content = vo.getScheduleContent();
				int startLine = content.indexOf("\n");
				if(startLine > 0){
					content = content.substring(0, startLine)+"...";
				}else if(content.length() > 30){
					content = content.substring(0, 30)+"...";
				}
				scheduleID[ii] = vo.getScheduleID();
				mytype[ii]=CalendarConstant.sch_type[vo.getScheduleTypeID()];
				mystart[ii]=vo.getScheduleDate();
				myend[ii]=vo.getScheduleDate2();
				mypriorty[ii]=pri(vo.getpriority());
				ii++;
			}
		}else{
			scheduleInfo = "没有日程";
			//createInfotext(scheduleInfo,-1);
		}
	}
	public String pri(int id){
		String aa;
		switch (id){
			case 1:
				aa="一般";
			break;
			case 2:
				aa="叫重要";
				break;
			case 3:
				aa="重要";
				break;
			case 4:
				aa="很重要";
				break;
			case 5:
				aa="最为重要";
				break;
			default:aa="";
		}
		return aa;
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.back) this.finish();
		if(v.getId()==R.id.richeng) {
			startActivity(new Intent().setClass(ScheduleAll.this,CalendarActivity.class));
		}
	}
}
