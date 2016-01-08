package com.example.jogle.calendar;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jogle.attendance.JGDBOperation;
import com.example.jogle.attendance.JGDataSet;
import com.example.jogle.attendance.JGDownload;
import com.example.jogle.attendance.JGDownloadCallBack;
import com.example.jogle.attendance.JGPeopleListAdapter;
import com.example.jogle.attendance.R;
import com.example.jogle.calendar.doim.JGCalendarViewBuilder;
import com.example.jogle.calendar.doim.JGCustomDate;
import com.example.jogle.calendar.widget.JGCalendarView;
import com.example.jogle.calendar.widget.JGCalendarView.CallBack;
import com.example.jogle.calendar.widget.JGCalendarViewPagerLisenter;
import com.example.jogle.calendar.widget.JGCustomViewPagerAdapter;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")

public class JGCalendarActivity extends Activity implements OnClickListener, CallBack, JGDownloadCallBack {
	
	private ViewPager viewPager;
	private JGCalendarView[] views;
	private TextView showYearView;
	private TextView showMonthView;
//	private TextView showWeekView;
	private JGCalendarViewBuilder builder = new JGCalendarViewBuilder();
	private View mContentPager;
	private JGCustomDate mClickDate = null;

	private LinearLayout myStatistics;
	private TabHost tabHost;

	private Spinner spinner;
	private String[] mData = {"我的签到统计", "下属签到统计"};
	private TextView signInTime;
	private TextView signOutTime;
	private TextView outTime;
	private TextView outPosition;
	private TextView noSelection;
	private TextView passedDays;
	private TextView outDays;
	private TextView inDays;
	private TextView signedRate;
	private TextView signedBar;
	private TextView unsignedBar;
	public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";

	private ListView peopleListweek;
	private ListView peopleListmonth;
	private JGPeopleListAdapter peopleListWeekAdapter;
	private JGPeopleListAdapter peopleListMonthAdapter;
	private List<JGDataSet> myListItems;
	private List<JGDataSet> depListItems;
	private List<Map<String, Object>> depListWeek;
	private List<Map<String, Object>> depListMonth;

	private int uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jg_activity_calendar);

		uid = getIntent().getIntExtra("uid", -1);
		String name = getIntent().getStringExtra("name");

		myListItems = new ArrayList<JGDataSet>();

		JGDBOperation operation = new JGDBOperation(this);
		JGCalendarView.list = operation.getByUID(String.valueOf(uid));
//		JGCalendarView.list = operation.getAll();

		Calendar now = Calendar.getInstance();
		Calendar firstDay = Calendar.getInstance();
		now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		firstDay.set(firstDay.get(Calendar.YEAR), firstDay.get(Calendar.MONTH), 1, 0, 0, 0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowString = sdf.format(now.getTime()) + "_235959";
		String firstString = sdf.format(firstDay.getTime()) + "_000000";

//		JGDownload download1 = new JGDownload(this);
//		download1.down(0, uid, firstString, nowString);

		JGDownload JGDownload2 = new JGDownload(this);
		JGDownload2.down(1, uid, firstString, nowString);

		findViewbyId();

		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("本日").setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("最近一周").setContent(R.id.tab2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("本月").setContent(R.id.tab3));

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.myspinner_att, mData);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					myStatistics.bringToFront();
				} else if (position == 1) {
					tabHost.bringToFront();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setVisibility(View.VISIBLE);

		depListWeek = new ArrayList<Map<String, Object>>();
		peopleListweek = (ListView) findViewById(R.id.peoplelistweek);
		peopleListWeekAdapter = new JGPeopleListAdapter(getApplicationContext(), depListWeek);
		peopleListweek.setAdapter(peopleListWeekAdapter);

		depListMonth = new ArrayList<Map<String, Object>>();
		peopleListmonth = (ListView) findViewById(R.id.peoplelistmonth);
		peopleListMonthAdapter = new JGPeopleListAdapter(getApplicationContext(), depListMonth);
		peopleListmonth.setAdapter(peopleListMonthAdapter);

		RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void findViewbyId() {
		signInTime = (TextView) findViewById(R.id.sign_in_time);
		signOutTime = (TextView) findViewById(R.id.sign_out_time);
		outTime = (TextView) findViewById(R.id.out_time);
		outPosition = (TextView) findViewById(R.id.out_pos);
		noSelection = (TextView) findViewById(R.id.no_selection);
		passedDays = (TextView) findViewById(R.id.passed_days);
		signedRate = (TextView) findViewById(R.id.signed_rate);
		outDays = (TextView) findViewById(R.id.out_days);
		inDays = (TextView) findViewById(R.id.in_days);
		signedBar = (TextView) findViewById(R.id.signed_bar);
		unsignedBar = (TextView) findViewById(R.id.unsigned_bar);

		myStatistics = (LinearLayout) findViewById(R.id.my_statistics);
		tabHost = (TabHost) findViewById(R.id.tabHost);

		viewPager = (ViewPager) this.findViewById(R.id.viewpager);
		showMonthView = (TextView)this.findViewById(R.id.show_month_view);
		showYearView = (TextView)this.findViewById(R.id.show_year_view);
		views = builder.createMassCalendarViews(this, 5, this);
		mContentPager = this.findViewById(R.id.contentPager);
		setViewPager();
	}


	public void setViewPager() {
		JGCustomViewPagerAdapter<JGCalendarView> viewPagerAdapter = new JGCustomViewPagerAdapter<JGCalendarView>(views);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(498); 
		viewPager.setOnPageChangeListener(new JGCalendarViewPagerLisenter(viewPagerAdapter));
	}

 @Override  
 protected void onDestroy() {  
     super.onDestroy();  
 }  

 public void setShowDateViewText(int year ,int month){
	 showYearView.setText(year+"");
	 showMonthView.setText(month + "月");
//	 showWeekView.setText(JGDateUtil.weekName[JGDateUtil.getWeekDay()-1]);
 }

	@Override
	public void onMesureCellHeight(int cellSpace) {}

	@Override
	public void setStatistics(int passedDays, int signedDays, int outDays, int inDays) {
		this.passedDays.setText(String.valueOf(passedDays));
		this.signedRate.setText(String.valueOf((int)(signedDays * 100.0 / passedDays)));
		this.outDays.setText(String.valueOf(outDays));
		this.inDays.setText(String.valueOf(inDays));
		if (passedDays > 0) {
			signedBar.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT, passedDays - signedDays
			));
			signedBar.setBackgroundColor(0xAB79FF56); // green
			unsignedBar.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT, signedDays
			));
			unsignedBar.setBackgroundColor(0xB6FF5E3A); // red
		}
		else {
			signedBar.setBackgroundColor(0xffcfcfcf); // gray
			unsignedBar.setBackgroundColor(0xffcfcfcf); //gray
		}
	}

	@Override
	public void clickDate(JGCustomDate date) {
		mClickDate = date;
		noSelection.setVisibility(View.GONE);

		signInTime.setText("暂无数据");
		signOutTime.setText("暂无数据");
		outTime.setText("暂无数据");
		outPosition.setText("");

		for (int i = 0; i < JGCalendarView.list.size(); i++) {
			JGDataSet item = JGCalendarView.list.get(i);
			if (item.hasSameDate(mClickDate.toCalendar())) {
				switch (item.getType()) {
					case 0: outTime.setText(item.getTime().split(" ")[1]);
							outPosition.setText(item.getPosition());
						break;
					case 1: signInTime.setText(item.getTime().split(" ")[1]);
						break;
					case 2: signOutTime.setText(item.getTime().split(" ")[1]);
				}
			}
		}
	}

	@Override
	public void changeDate(JGCustomDate date) {
		setShowDateViewText(date.year, date.month);
	}

	@Override
	public void onClick(View v) {}

//	public List<Map<String, Object>> getData() {
//		listItems = new ArrayList<Map<String, Object>>();

//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("name", "员工1");
//		map.put("department", "部门1");
//		map.put("signedDays", 5);
//		map.put("passedDays", 16);
//		map.put("uid", 1);
//		listItems.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("name", "员工2");
//		map.put("department", "部门2");
//		map.put("signedDays", 15);
//		map.put("passedDays", 16);
//		map.put("uid", 2);
//		listItems.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("name", "员工3");
//		map.put("department", "部门3");
//		map.put("signedDays", 9);
//		map.put("passedDays", 16);
//		map.put("uid", 3);
//		listItems.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("name", "员工4");
//		map.put("department", "部门3");
//		map.put("signedDays", 7);
//		map.put("passedDays", 16);
//		map.put("uid", 4);
//		listItems.add(map);
//
//		return listItems;
//	}

	private void getData() {
		//List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Integer> uids = new ArrayList<Integer>();
		List<Integer> wdays = new ArrayList<Integer>();
		List<Integer> mdays = new ArrayList<Integer>();
		int passedDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Calendar firstday = Calendar.getInstance();
		firstday.add(Calendar.DAY_OF_MONTH, -7);
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 00:00:00");
		String firstdayString = sdf.format(firstday.getTime());
		sdf = new SimpleDateFormat("yyyy年MM月dd日");
		String todayString = sdf.format(today.getTime());

		for (JGDataSet ds : depListItems) {
			if (!uids.contains(Integer.valueOf(ds.getUserID()))) {
				uids.add(Integer.valueOf(ds.getUserID()));
			}
		}
		int numOfSignedToday = 0;
		for (Integer uid : uids) {
			double md = 0, wd = 0, count = 0;
			for (JGDataSet ds : depListItems) {
				if (ds.getUserID() == uid.intValue()) {
					if (ds.getType() == 0) {
						md++;
						if (ds.getTime().compareTo(firstdayString) > 0)
							wd++;
						if (ds.getTime().startsWith(todayString))
							count++;
					}
					else if (ds.getType() == 1 || ds.getType() == 2) {
						md += 0.5;
						if (ds.getTime().compareTo(firstdayString) > 0)
							wd += 0.5;
						if (ds.getTime().startsWith(todayString))
							count += 0.5;
					}
				}
			}
			Integer i = new Integer((int) md);
			mdays.add(i);
			i = new Integer((int) wd);
			wdays.add(i);
			if (count >= 1)
				numOfSignedToday++;
		}

		CenterDatabase cd = new CenterDatabase(this, null);
		SQLiteDatabase db = cd.getWritableDatabase();

		depListMonth.clear();
		for (int i = 0; i < uids.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String name = "";
			Cursor cursor = db.rawQuery("select name from " + CenterDatabase.USER + " where uid = ?", new String[]{String.valueOf(uids.get(i).intValue())});
			if (cursor.moveToFirst()) {
				name = cursor.getString(0);
			}
			cursor.close();
			map.put("name", name);
			map.put("department", "");
			map.put("signedDays", mdays.get(i).intValue());
			map.put("passedDays", passedDays);
			map.put("uid", -1);
			depListMonth.add(map);
		}

		depListWeek.clear();
		for (int i = 0; i < uids.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String name = "";
			Cursor cursor = db.rawQuery("select name from " + CenterDatabase.USER + " where uid = ?", new String[]{String.valueOf(uids.get(i).intValue())});
			if (cursor.moveToFirst()) {
				name = cursor.getString(0);
			}
			cursor.close();
			map.put("name", name);
			map.put("department", "");
			map.put("signedDays", wdays.get(i).intValue());
			map.put("passedDays", 7);
			map.put("uid", uids.get(i).intValue());
			depListWeek.add(map);
		}

		db.close();
		cd.close();

		TextView allPeople = (TextView) findViewById(R.id.total_people);
		allPeople.setText(String.valueOf(uids.size()));
		TextView signedToday = (TextView) findViewById(R.id.signed_today);
		signedToday.setText(String.valueOf(numOfSignedToday));
		TextView unsignedToday = (TextView) findViewById(R.id.unsigned_today);
		unsignedToday.setText(String.valueOf(uids.size() - numOfSignedToday));
		TextView signedRate = (TextView) findViewById(R.id.signed_rate2);
		if (uids.size()!=0) {
			signedRate.setText(String.valueOf(numOfSignedToday * 100 / uids.size()));
		}
	}

	@Override
	public void downloadCallBack(int type, String msg) {
		//Log.e("callback: ", msg);
		List<JGDataSet> newList = new ArrayList<JGDataSet>();
		if (msg == null) {
			Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(msg);
			int error = jsonObject.getInt("error");
			if (error == 1) {
				JSONArray array = jsonObject.getJSONArray("result");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.get(i);
					JGDataSet dataSet = new JGDataSet();
					dataSet.setType(obj.getInt("type"));
					if (obj.has("uid"))
						dataSet.setUserID(obj.getInt("uid"));
					else
						dataSet.setUserID(uid);
					dataSet.setTime(obj.getString("time"));
					dataSet.setPosition(obj.getString("position"));
					dataSet.setContent(obj.getString("content"));
					newList.add(dataSet);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (type == 0) {
			JGCalendarView.list = newList;
		}
		else if (type == 1) {
			depListItems = newList;

			getData();
			peopleListMonthAdapter.notifyDataSetChanged();
			peopleListWeekAdapter.notifyDataSetChanged();
		}

	}
}