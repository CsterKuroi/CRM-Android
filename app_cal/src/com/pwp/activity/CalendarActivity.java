package com.pwp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderEditText;
import com.pwp.borderText.BorderTextView;
import com.pwp.constant.CalendarConstant;
import com.pwp.dao.DBOpenHelper;
import com.pwp.dao.ScheduleDAO;
import com.pwp.dao.UserDAO;
import com.pwp.myclass.NetworkDetector;
import com.pwp.myservices.update_user_schedule;
import com.pwp.myweather.Weather;
import com.pwp.myweather.WeatherAdaptor;
import com.pwp.popupwindow.MorePopWindow;
import com.pwp.vo.ScheduleDateTag;
import com.pwp.vo.ScheduleVO;
import com.ricky.database.CenterDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

//import android.util.Log;
//import com.pwp.
/**
 * 日历显示activity
 *
 * @author dayuan
 *
 */
public class CalendarActivity extends Activity implements OnGestureListener {
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CalendarView calV = null;
	private GridView gridView = null;
	private TextView topText = null;
	private Drawable draw = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private ImageButton back;
	private Handler mHandler;
	private ImageButton add;
	private ImageButton previousyear;
	private ImageButton nextyear;
	private ScheduleDAO dao = null;
	private UserDAO userdao= null;
	private int flag1 = 0;
	private int flag2 = 0;
	private String scheduleDay;
	String scheduleYear;
	String scheduleMonth;
	String week;

	//final String wsuri = "ws://"+ UrlConstant.IP+":8001/ws";
	ArrayList<String> scheduleDate;
	ArrayList<String> scheduleID2;
	LinearLayout l2 = null;
	LinearLayout l22 = null;
	TextView textTop = null;
	BorderTextView info = null;
	BorderTextView date = null;
	BorderTextView type = null;
	BorderEditText editInfo = null;
	ScheduleDAO dao2 = null;
	ScheduleVO scheduleVO = null;
	TextView tt = null;
	TextView type2 = null;
	LinearLayout l1 = null;
	LinearLayout l4;
	private ImageButton setButton;
	private Button addButton;
	private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
	WebSocketConnection mConnection = new WebSocketConnection();

	// int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
	ArrayList<String> aa = null;// 判断两次点击日期是否为同一日期
	int ii = 0;
	int jj = 0;

	int width;
	int height;
	private MyReceiver receiver = null;
	//IMApplication imapp = (IMApplication) getApplication();
	//String createrID=imapp.getUserid();
	//String createrID="101";

	String scheduleInfo = ""; // 日程信息被修改前的内容
	String scheduleChangeInfo = ""; // 日程信息被修改之后的内容
	final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	final LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	final LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	final LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, 100);

	DBOpenHelper dbOpenHelper=new DBOpenHelper(CalendarActivity.this, "schedules.db");
	String str;	
	String aboutid;

	Bundle bundle;
	ImageView imageview_w;
	TextView textview_w;
	TextView day;
	TextView month;
    String month_en;
	Runnable networkTask;

	String userid;
	String wsuri;

	com.pwp.myservices.update_user_schedule aaa=null;

	Thread ttt2=null;
	public CalendarActivity() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");// turn date
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
		dao = new ScheduleDAO(this);
		dao2 = new ScheduleDAO(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.main);

		CenterDatabase centerDatabase = new CenterDatabase(this, null);
		userid = centerDatabase.getUID();
		wsuri = CenterDatabase.URI;
		centerDatabase.close();

		WindowManager wm = this.getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();

		//更新日程天气
		imageview_w=(ImageView)findViewById(R.id.weather_icon);
		textview_w=(TextView)findViewById(R.id.weather_tempre);
		day=(TextView)findViewById(R.id.day);
		month=(TextView)findViewById(R.id.month);
		switch (month_c){
			case 1:
				month_en="一月";//"January";
				break;
			case 2:
				month_en="二月";//"February";
				break;
			case 3:
				month_en="三月";//"March";
				break;
			case 4:
				month_en="四月";//"April";
				break;
			case 5:
				month_en="五月";//"May ";
				break;
			case 6:
				month_en="六月";//"June ";
				break;
			case 7:
				month_en="七月";//"July ";
				break;
			case 8:
				month_en="八月";//"August";
				break;
			case 9:
				month_en="九月";//"September";
				break;
			case 10:
				month_en="十月";//"October";
				break;
			case 11:
				month_en="十一月";//"November";
				break;
			case 12:
				month_en="十二月";//"December";
				break;
		}
		day.setText(String.valueOf(day_c));
		month.setText(month_en);

		/*SimpleDateFormat sdf = new SimpleDateFormat("HH");
		int hour= Integer.parseInt(sdf.format(new Date()));*/
		/*if(hour>19||hour<6)
		{
			imageview_w.setImageResource(R.drawable.weathericon_condition_15);
		}else {
			imageview_w.setImageResource(R.drawable.weathericon_condition_01);
		}*/

		//判断网络是否可用
		if(NetworkDetector.detect(this)){
			//Toast.makeText(CalendarActivity.this, "net ok",Toast.LENGTH_LONG).show();
			  ttt2=new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
						//Toast.makeText(CalendarActivity.this, "thread ok1", Toast.LENGTH_LONG).show();
					try {
					do {
								URL url = new URL("http://whois.pconline.com.cn/ip.jsp");

								//Toast.makeText(CalendarActivity.this, "do ok1", Toast.LENGTH_LONG).show();
								HttpURLConnection connect = (HttpURLConnection) url.openConnection();
								InputStream is = connect.getInputStream();
								ByteArrayOutputStream outStream = new ByteArrayOutputStream();
								byte[] buff = new byte[256];
								int rc = 0;
								while ((rc = is.read(buff, 0, 256)) > 0) {
									outStream.write(buff, 0, rc);
								}
								System.out.println(outStream);
								byte[] b = outStream.toByteArray();// 关闭
								outStream.close();
								is.close();
								connect.disconnect();
								String address = new String(b, "gbk");
								byte[] by = address.getBytes("utf-8");
								String add = new String(by, 0, by.length);
								String add2 = add.substring(0, add.indexOf("市")).trim();
						        WeatherAdaptor.getWeather(add2);
								//Toast.makeText(CalendarActivity.this, add2, Toast.LENGTH_LONG).show();
								//Toast.makeText(CalendarActivity.this, WeatherAdaptor.getWeather(add2), Toast.LENGTH_LONG).show();
								//Toast.makeText(CalendarActivity.this, Weather.current_dayPictureUrl, Toast.LENGTH_LONG).show();
						      //  Log.e("vvvvvddddddddddddddddd", "dddddddddddddddddd");	Log.e("dkkkkkkkkkk", Weather.current_dayPictureUrl);
							//	Log.e("dlllllllllll", Weather.current_nightPictureUrl);
								Message msg = new Message();
								msg.what = 1;
								Bundle data = new Bundle();
								data.putString("value", WeatherAdaptor.getWeather(add2));
								msg.setData(data);
								handler.sendMessage(msg);
								Thread.sleep(1000 * 10);//一分钟更新一次天气
								Looper.loop();
					} while (!Thread.interrupted());

					} catch (Exception e) {
						e.printStackTrace();
						Log.e("dvvvvvvvvvvvv", e.toString());
						//Toast.makeText(CalendarActivity.this, e.toString(), Toast.LENGTH_LONG).show();
					}

				}
			});

			ttt2.start();

			aaa=new com.pwp.myservices.update_user_schedule(CalendarActivity.this,userid,wsuri);
		}
         //更新日程，是否有关于用户自己的日程
		/* if(update_user_schedule.mConnection.isConnected()){
			  update_user_schedule.mConnection.sendTextMessage(str);
		   }*/
		 
		// 动态方式注册广播接收者
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("add");
		filter.addAction("today");
		filter.addAction("jump");
		filter.addAction("cancel");
		this.registerReceiver(receiver, filter);

		//加载日历界面
		dao=new ScheduleDAO(this);
		gestureDetector = new GestureDetector(this);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarView(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		addGridView();

		gridView.setAdapter(calV);
		// flipper.addView(gridView);
		flipper.addView(gridView, 0);
		topText = (TextView) findViewById(R.id.toptext);
		addTextToTopTextView(topText);
		back = (ImageButton) findViewById(R.id.imageButton1);
		//add = (ImageButton) findViewById(R.id.imageButton2);
		setButton = (ImageButton) findViewById(R.id.imageButton2);
		previousyear = (ImageButton) findViewById(R.id.imageButton11);
		nextyear = (ImageButton) findViewById(R.id.imageButton12);
		back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// DataBaseHelper myDbHelper = new DataBaseHelper();
				// ImageButton事件响应
				// CalendarActivity.this.finish();
                aaa.stopThread();//关掉线程及连接

				SysApplication.getInstance().exit();
			}
		});
		setButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MorePopWindow morePopWindow = new MorePopWindow(CalendarActivity.this);
				morePopWindow.showPopupWindow(setButton);
			}
		});
		previousyear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ((LinearLayout) findViewById(R.id.ll) != null) {
					findViewById(R.id.ll).setVisibility(View.GONE);
				}
				int gvFlagff1 = 0; // 每次添加gridview到viewflipper中时给的标记
				// ImageButton事件响应
				addGridView(); // 添加一个gridView
				jumpMonth -= 12; // 下一个月

				calV = new CalendarView(CalendarActivity.this, getResources(),
						jumpMonth, jumpYear, year_c, month_c, day_c);
				gridView.setAdapter(calV);
				// flipper.addView(gridView);
				addTextToTopTextView(topText);
				gvFlagff1++;
				flipper.addView(gridView, gvFlagff1);
				flipper = (ViewFlipper) findViewById(R.id.flipper);
				flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this, R.anim.push_right_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this, R.anim.push_right_out));
				flipper.showNext();
				flipper.removeViewAt(0);

			}
		});
		nextyear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ((LinearLayout) findViewById(R.id.ll) != null) {
					findViewById(R.id.ll).setVisibility(View.GONE);
				}
				// ImageButton事件响应
				int gvFlagff1 = 0; // 每次添加gridview到viewflipper中时给的标记
				// ImageButton事件响应
				addGridView(); // 添加一个gridView
				jumpMonth += 12; // 下一个月

				calV = new CalendarView(CalendarActivity.this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
				gridView.setAdapter(calV);
				// flipper.addView(gridView);
				addTextToTopTextView(topText);
				gvFlagff1++;
				flipper.addView(gridView, gvFlagff1);
				flipper = (ViewFlipper) findViewById(R.id.flipper);
				flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this, R.anim.push_left_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this, R.anim.push_left_out));
				flipper.showNext();
				flipper.removeViewAt(0);
			}
		});
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1){
				//Toast.makeText(CalendarActivity.this,"sgfdg",Toast.LENGTH_LONG).show();
				Bundle data = msg.getData();
				String val = data.getString("value");
				update_weather();
				//Toast.makeText(CalendarActivity.this, "天气"+Weather.current_weather,Toast.LENGTH_LONG).show();
			}
		}
	};
    //更新天气图标与温度
	public void update_weather(){
		//0.晴 1.多云 2.阴 6.雨夹雪 7.小雨 8.中雨 13.阵雪 14.小雪

		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		int hour= Integer.parseInt(sdf.format(new Date()));
		if(hour>19||hour<6)
		{
			imageview_w.setImageResource(R.drawable.weathericon_condition_15);
		}else{
		switch (Weather.current_weather){
			case "晴":
				imageview_w.setImageResource(R.drawable.weathericon_condition_01);
				break;
			case "多云":
				imageview_w.setImageResource(R.drawable.weathericon_condition_04);
				break;
			case "阴":
				imageview_w.setImageResource(R.drawable.weathericon_condition_04);
				break;
			case "雨夹雪":
				imageview_w.setImageResource(R.drawable.weathericon_condition_14);
				break;
			case "小雨":
				imageview_w.setImageResource(R.drawable.weathericon_condition_08);
				break;
			case "中雨":
				imageview_w.setImageResource(R.drawable.weathericon_condition_09);
				break;
			case "阵雪":
				imageview_w.setImageResource(R.drawable.weathericon_condition_09);
				break;
			case "小雪":
				imageview_w.setImageResource(R.drawable.weathericon_condition_12);
				break;
			default:imageview_w.setImageResource(R.drawable.sun);
		}}
	textview_w.setText(Weather.current_temp);
	}

	String update_flag11="0";
	String update_flag22="0";

	public void handleDate(int remindID, String year, String month, String day, int scheduleID) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
		String d = year + "-" + month + "-" + day;
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(format.parse(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 封装要标记的日期
		if (remindID >= 0 && remindID <= 4) {
			// "提醒一次","隔10分钟","隔30分钟","隔一小时"（只需标记当前这一天）
			ScheduleDateTag dateTag = new ScheduleDateTag();
			dateTag.setYear(Integer.parseInt(year));
			dateTag.setMonth(Integer.parseInt(month));
			dateTag.setDay(Integer.parseInt(day));
			dateTag.setscheduleID(scheduleID);
			//dateTag.setcreaterID(aid.getuserid());
			dateTagList.add(dateTag);
		}
		/*else if (remindID == 4) {
			// 每天重复(从设置的日程的开始的之后每一天都要标记)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4 * 7; i++) {
				if (i == 0) {
					cal.add(Calendar.DATE, 0);
				} else {
					cal.add(Calendar.DATE, 1);
				}
				handleDate(cal, scheduleID);
			}
		}*/
		else if (remindID == 5) {
			// 每周重复(从设置日程的这天(星期几)，接下来的每周的这一天多要标记)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4; i++) {
				if (i == 0) {
					cal.add(Calendar.WEEK_OF_MONTH, 0);
				} else {
					cal.add(Calendar.WEEK_OF_MONTH, 1);
				}
				handleDate(cal, scheduleID);
			}
		} else if (remindID == 6) {
			// 每月重复(从设置日程的这天(几月几号)，接下来的每月的这一天多要标记)
			for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12; i++) {
				if (i == 0) {
					cal.add(Calendar.MONTH, 0);
				} else {
					cal.add(Calendar.MONTH, 1);
				}
				handleDate(cal, scheduleID);
			}
		} else if (remindID == 7) {
			// 每年重复(从设置日程的这天(哪一年几月几号)，接下来的每年的这一天多要标记)
			for (int i = 0; i <= 2049 - Integer.parseInt(year); i++) {
				if (i == 0) {
					cal.add(Calendar.YEAR, 0);
				} else {
					cal.add(Calendar.YEAR, 1);
				}
				handleDate(cal, scheduleID);
			}
		}
		// 将标记日期存入数据库中
		dao.saveTagDate(dateTagList);
	}

	public void handleDate(Calendar cal, int scheduleID) {
		ScheduleDateTag dateTag = new ScheduleDateTag();
		dateTag.setYear(cal.get(Calendar.YEAR));
		dateTag.setMonth(cal.get(Calendar.MONTH) + 1);
		dateTag.setDay(cal.get(Calendar.DATE));
		dateTag.setuserid(userid);
		dateTag.setscheduleID(scheduleID);
		dateTagList.add(dateTag);
	}

	public class MyReceiver extends BroadcastReceiver//作为内部类的广播接收者
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			switch (intent.getAction()){
				case "add":
					add();
					break;
				case "today":
					//Toast.makeText(context,"ggggggggggggggg！！！",Toast.LENGTH_LONG).show();
                     today();
					break;
				case "jump":
					jump();
					break;
				case "cancel":
					break;
			}
		}
		protected void onDestroy() {
			if(receiver!=null) {
				super.abortBroadcast();
				unregisterReceiver(receiver);
			}
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.imageButton2) {
				MorePopWindow morePopWindow = new MorePopWindow(CalendarActivity.this);
				morePopWindow.showPopupWindow(setButton);
		} else {

		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// 日历左右滑动，下方清空

		findViewById(R.id.listview_today).setVisibility(View.GONE);
		ffff=0;

		int gvFlagff = 0; // 每次添加gridview到viewflipper中时给的标记
		if (e1.getX() - e2.getX() > 120) {
			// 往左滑动
			addGridView(); // 添加一个gridView
			jumpMonth++; // 下一个月

			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
					year_c, month_c, day_c);
			gridView.setAdapter(calV);
			// flipper.addView(gridView);
			addTextToTopTextView(topText);
			gvFlagff++;
			flipper.addView(gridView, gvFlagff);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			// 向右滑动
			addGridView(); // 添加一个gridView
			jumpMonth--; // 上一个月

			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
					year_c, month_c, day_c);
			gridView.setAdapter(calV);
			gvFlagff++;
			addTextToTopTextView(topText);
			// flipper.addView(gridView);
			flipper.addView(gridView, gvFlagff);

			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}
	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, Menu.FIRST, "今天");
		menu.add(0, Menu.FIRST + 1, Menu.FIRST + 1, "跳转");
		menu.add(0, Menu.FIRST + 2, Menu.FIRST + 2, "所有日程");
		menu.add(0, Menu.FIRST + 3, Menu.FIRST + 3, "日期转换");
		return super.onCreateOptionsMenu(menu);
	}

	private View conentView;
	public void MorePopWindow(Context context){


	}
	/**
	 * 选择菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case Menu.FIRST:
				// 跳转到今天
				if ((LinearLayout) findViewById(R.id.ll) != null) {
					findViewById(R.id.ll).setVisibility(View.GONE);
				}
				int xMonth = jumpMonth;
				int xYear = jumpYear;
				int gvFlag = 0;
				jumpMonth = 0;
				jumpYear = 0;
				addGridView(); // 添加一个gridView
				year_c = Integer.parseInt(currentDate.split("-")[0]);
				month_c = Integer.parseInt(currentDate.split("-")[1]);
				day_c = Integer.parseInt(currentDate.split("-")[2]);
				calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
						year_c, month_c, day_c);
				gridView.setAdapter(calV);
				addTextToTopTextView(topText);
				gvFlag++;
				flipper.addView(gridView, gvFlag);
				if (xMonth == 0 && xYear == 0) {
					// nothing to do
				} else if ((xYear == 0 && xMonth > 0) || xYear > 0) {
					this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
							R.anim.push_left_in));
					this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
							R.anim.push_left_out));
					this.flipper.showNext();
				} else {
					this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
							R.anim.push_right_in));
					this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
							R.anim.push_right_out));
					this.flipper.showPrevious();
				}
				flipper.removeViewAt(0);
				break;
			case Menu.FIRST + 1:

				if ((LinearLayout) findViewById(R.id.ll) != null) {
					findViewById(R.id.ll).setVisibility(View.GONE);
				}
				new DatePickerDialog(this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
										  int monthOfYear, int dayOfMonth) {
						// 1901-1-1 ----> 2049-12-31
						if (year < 1901 || year > 2049) {
							// 不在查询范围内
							new AlertDialog.Builder(CalendarActivity.this)
									.setTitle("错误日期")
									.setMessage("跳转日期范围(1901/1/1-2049/12/31)")
									.setPositiveButton("确认", null).show();
						} else {
							int gvFlag = 0;
							addGridView(); // 添加一个gridView
							calV = new CalendarView(CalendarActivity.this,
									CalendarActivity.this.getResources(), year,
									monthOfYear + 1, dayOfMonth);
							gridView.setAdapter(calV);
							addTextToTopTextView(topText);
							gvFlag++;
							flipper.addView(gridView, gvFlag);
							if (year == year_c && monthOfYear + 1 == month_c) {
								// nothing to do
							}
							if ((year == year_c && monthOfYear + 1 > month_c)
									|| year > year_c) {
								CalendarActivity.this.flipper
										.setInAnimation(AnimationUtils
												.loadAnimation(
														CalendarActivity.this,
														R.anim.push_left_in));
								CalendarActivity.this.flipper
										.setOutAnimation(AnimationUtils
												.loadAnimation(
														CalendarActivity.this,
														R.anim.push_left_out));
								CalendarActivity.this.flipper.showNext();
							} else {
								CalendarActivity.this.flipper
										.setInAnimation(AnimationUtils
												.loadAnimation(
														CalendarActivity.this,
														R.anim.push_right_in));
								CalendarActivity.this.flipper
										.setOutAnimation(AnimationUtils
												.loadAnimation(
														CalendarActivity.this,
														R.anim.push_right_out));
								CalendarActivity.this.flipper.showPrevious();
							}
							flipper.removeViewAt(0);
							// 跳转之后将跳转之后的日期设置为当期日期
							year_c = year;
							month_c = monthOfYear + 1;
							day_c = dayOfMonth;
							jumpMonth = 0;
							jumpYear = 0;
						}
					}
				}, year_c, month_c - 1, day_c).show();
				break;
			case Menu.FIRST + 2:
				Intent intent = new Intent();
				intent.setClass(CalendarActivity.this, ScheduleAll.class);
				startActivity(intent);
				break;
			case Menu.FIRST + 3:
				Intent intent1 = new Intent();
				intent1.setClass(CalendarActivity.this, CalendarConvert.class);
				intent1.putExtra("date", new int[] { year_c, month_c, day_c });
				startActivity(intent1);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	public void add() {
		// ImageButton事件响应
		ArrayList<String> scheduleDate = new ArrayList<String>();
		scheduleDate.add(currentDate.split("-")[0]);
		scheduleDate.add(currentDate.split("-")[1]);
		scheduleDate.add(currentDate.split("-")[2]);
		//Toast.makeText(CalendarActivity.this, scheduleDate.toString(), Toast.LENGTH_LONG).show();
		// scheduleDate.add(week);
		Intent intent = new Intent();
		intent.putStringArrayListExtra("scheduleDate", scheduleDate);
		intent.setClass(CalendarActivity.this, add.class);	
		startActivity(intent);
	}
	public void today(){
		if ((LinearLayout) findViewById(R.id.ll) != null) {
			findViewById(R.id.ll).setVisibility(View.GONE);
		}
		int xMonth = jumpMonth;
		int xYear = jumpYear;
		int gvFlag = 0;
		jumpMonth = 0;
		jumpYear = 0;
		addGridView(); // 添加一个gridView
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
		calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
				year_c, month_c, day_c);
		gridView.setAdapter(calV);
		addTextToTopTextView(topText);
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		if (xMonth == 0 && xYear == 0) {
			// nothing to do
		} else if ((xYear == 0 && xMonth > 0) || xYear > 0) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper.showNext();
		} else {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
		}
		flipper.removeViewAt(0);
	}
	public void jump(){
		if ((LinearLayout) findViewById(R.id.ll) != null) {
			findViewById(R.id.ll).setVisibility(View.GONE);
		}
		new DatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year,
								  int monthOfYear, int dayOfMonth) {
				// 1901-1-1 ----> 2049-12-31
				if (year < 1901 || year > 2049) {
					// 不在查询范围内
					new AlertDialog.Builder(CalendarActivity.this)
							.setTitle("错误日期")
							.setMessage("跳转日期范围(1901/1/1-2049/12/31)")
							.setPositiveButton("确认", null).show();
				} else {
					int gvFlag = 0;
					addGridView(); // 添加一个gridView
					calV = new CalendarView(CalendarActivity.this,
							CalendarActivity.this.getResources(), year,
							monthOfYear + 1, dayOfMonth);
					gridView.setAdapter(calV);
					addTextToTopTextView(topText);
					gvFlag++;
					flipper.addView(gridView, gvFlag);
					if (year == year_c && monthOfYear + 1 == month_c) {
						// nothing to do
					}
					if ((year == year_c && monthOfYear + 1 > month_c)
							|| year > year_c) {
						CalendarActivity.this.flipper
								.setInAnimation(AnimationUtils
										.loadAnimation(
												CalendarActivity.this,
												R.anim.push_left_in));
						CalendarActivity.this.flipper
								.setOutAnimation(AnimationUtils
										.loadAnimation(
												CalendarActivity.this,
												R.anim.push_left_out));
						CalendarActivity.this.flipper.showNext();
					} else {
						CalendarActivity.this.flipper
								.setInAnimation(AnimationUtils
										.loadAnimation(
												CalendarActivity.this,
												R.anim.push_right_in));
						CalendarActivity.this.flipper
								.setOutAnimation(AnimationUtils
										.loadAnimation(
												CalendarActivity.this,
												R.anim.push_right_out));
						CalendarActivity.this.flipper.showPrevious();
					}
					flipper.removeViewAt(0);
					// 跳转之后将跳转之后的日期设置为当期日期
					year_c = year;
					month_c = monthOfYear + 1;
					day_c = dayOfMonth;
					jumpMonth = 0;
					jumpYear = 0;
				}
			}
		}, year_c, month_c - 1, day_c).show();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	// 添加头部的年份 闰哪月等信息
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		/*draw = getResources().getDrawable(R.drawable.bgbar2);
		view.setBackgroundDrawable(draw);*/
		textDate.append(calV.getShowYear()).append("年")
				.append(calV.getShowMonth()).append("月").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(")
				.append(calV.getCyclical()).append("年)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	ListView listView;
	ImageButton dot;
	int ffff=0;
	// 添加gridview
	private void addGridView() {
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(43);
		//gridView.setPadding(30,0,10,0);
		//gridView

		if (Width == 480 && Height == 800) {
			gridView.setColumnWidth(30);
		}

		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		//gridView.setSelector(null);
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		//gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setBackgroundColor(Color.WHITE);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return CalendarActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView中的每一个item的点击事件
			int flag3 = 0;// 判断 每一天的第几条日程

			int which=0;
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position && position != endPosition) {
					// String scheduleDay = calV.getDateByClickItem(position)
					// .split("\\.")[0]; // 这一天的阳历
					scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
					// String scheduleLunarDay =
					// calV.getDateByClickItem(position).split("\\.")[1];
					// //这一天的阴历
					scheduleYear = calV.getShowYear();
					scheduleMonth = calV.getShowMonth();
					week = "";

					// 通过日期查询这一天是否被标记，如果标记了日程就查询出这天的所有日程信息
					String[] scheduleIDs = dao.getScheduleByTagDate(
							userid,
							Integer.parseInt(scheduleYear),
							Integer.parseInt(scheduleMonth),
							Integer.parseInt(scheduleDay));
					String mytime[] = new String[scheduleIDs.length];
					String mytype[] = new String[scheduleIDs.length];
					final String schids[] = new String[scheduleIDs.length];

					if (scheduleIDs != null && scheduleIDs.length > 0) {

						if(ffff==0||which!=position) {

							int mmm = 0;
							for (ii = 0; ii < scheduleIDs.length; ii++) {
								//Toast.makeText(CalendarActivity.this, scheduleIDs[ii], Toast.LENGTH_LONG).show();
								scheduleVO = dao2.getScheduleByID(Integer.parseInt(scheduleIDs[ii]));
								final ArrayList<String> scheduleID2 = new ArrayList<String>();
								scheduleID2.add(scheduleIDs[ii]);

								DBOpenHelper dbOpenHelper = new DBOpenHelper(CalendarActivity.this, "schedules.db");
								SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
								Cursor cursor = db.rawQuery("select scheduleDate, scheduleTypeID from schedule where scheduleID='" + scheduleIDs[ii] + "'", null);

								if (cursor.moveToFirst()) {
									//Toast.makeText(CalendarActivity.this,scheduleIDs[ii],Toast.LENGTH_SHORT).show();
									//aboutid=cursor.getString(0);
									mytime[mmm] = cursor.getString(0);
									mytime[mmm] = mytime[mmm].substring(11, mytime[mmm].length());
									mytype[mmm] = CalendarConstant.sch_type[cursor.getInt(1)];
									schids[mmm] = scheduleIDs[ii];
									mmm++;
								}
								cursor.close();
							}
							List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
							for (int ii = 0; ii < mytime.length; ii++) {
								if (mytime[ii] != null) {
									Map<String, Object> listItem = new HashMap<String, Object>();
									listItem.put("time", mytime[ii].substring(0,mytime[ii].length()-3));
									listItem.put("type", mytype[ii]);
									listItems.add(listItem);
								}
							}
							SimpleAdapter simpleAdapter = new SimpleAdapter(CalendarActivity.this, listItems, R.layout.schedule_item, new String[]{"time", "type"}, new int[]{R.id.time, R.id.type});
							listView = (ListView) findViewById(R.id.listview_today);
							listView.setVisibility(View.VISIBLE);
							listView.setAdapter(simpleAdapter);

							listView.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
									//获得选中项的HashMap对象
									//Toast.makeText(CalendarActivity.this, "点击了" + arg2, Toast.LENGTH_LONG).show();
									Intent intent = new Intent();
									intent.setClass(CalendarActivity.this, ScheduleInfoView.class);
									intent.putExtra("scheduleID", schids[arg2]);
									startActivity(intent);
								}
							});
							ffff=1;
						}else {
                                  listView.setVisibility(View.GONE);
							      ffff=0;
						}
					} else {
						scheduleDate = new ArrayList<String>();
						scheduleDate.add(scheduleYear);
						scheduleDate.add(scheduleMonth);
						scheduleDate.add(scheduleDay);
						scheduleDate.add(week);
						Intent intent = new Intent();
						intent.putStringArrayListExtra("scheduleDate", scheduleDate);
						intent.setClass(CalendarActivity.this, add.class);
						startActivity(intent);
					}

					which=position;


			}
		}
	});
		gridView.setLayoutParams(params);
	}

	/**
	 * 显示日程所有信息
	 */
	public void handlerInfo(int scheduleID, int num) {
		final int fff = num;
		final int scheduleid = scheduleID;

		params3.setMargins(0, 1, 0, 0);

		l22 = new LinearLayout(CalendarActivity.this); // 实例化布局对象
		l22.setOrientation(LinearLayout.VERTICAL);
		// l22.setBackgroundResource(R.drawable.schedule_bk);
		l22.setLayoutParams(params3);
		l22.setId(num * 10);
		l22.removeAllViews();

		BorderTextView date = new BorderTextView(this, null);
		date.setTextColor(Color.BLACK);
		date.setBackgroundColor(Color.WHITE);
		date.setLayoutParams(params3);
		date.setGravity(Gravity.CENTER_VERTICAL);
		date.setHeight(80);
		date.setPadding(10, 0, 10, 0);

		BorderTextView type = new BorderTextView(this, null);
		type.setTextColor(Color.BLACK);
		type.setBackgroundColor(Color.WHITE);
		type.setLayoutParams(params3);
		type.setGravity(Gravity.LEFT);
		type.setHeight(100);
		type.setId(num);
		// type.setPadding(10, 0, 10, 0);
		type.setTag(scheduleID);
		// type.setId(ff);
		type.setGravity(Gravity.CENTER);

		final BorderTextView info = new BorderTextView(this, null);
		info.setTextColor(Color.BLACK);
		info.setBackgroundColor(Color.WHITE);
		info.setGravity(Gravity.CENTER_VERTICAL);
		info.setLayoutParams(params3);
		info.setHeight(150);
		info.setPadding(10, 5, 10, 5);
		// info.setVisibility(View.GONE);
		info.setId(num * 13);

		LinearLayout l13 = new LinearLayout(CalendarActivity.this);
		l13.setOrientation(LinearLayout.VERTICAL);
		l13.setLayoutParams(params3);
		l13.removeAllViews();

		LinearLayout l3 = new LinearLayout(CalendarActivity.this);
		l3.setOrientation(LinearLayout.VERTICAL);
		// l3.setBackgroundResource(R.drawable.schedule_bk);
		l3.setLayoutParams(params4);
		l3.setId(num * 11);
		l3.setVisibility(View.GONE);

		LinearLayout lll = new LinearLayout(CalendarActivity.this);
		lll.setOrientation(LinearLayout.HORIZONTAL);
		lll.setLayoutParams(params5);
		lll.setBackgroundResource(R.drawable.top_day);
		lll.setId(num * 14);
		lll.setBackgroundColor(Color.GRAY);

		TextView ttt = new TextView(CalendarActivity.this);
		ttt.setWidth(width/4);
		TextView ttt2 = new TextView(CalendarActivity.this);
		ttt2.setWidth(width/9);
		final Button bb = new Button(this, null);
		bb.setText("编辑");
		bb.setHeight(40);
		bb.setWidth(width/5);
		// bb.setGravity(1);
		// bb.
		final Button bb2 = new Button(this, null);
		bb2.setText("删除");
		bb2.setId(num * 13);
		bb2.setHeight(40);
		bb2.setWidth(width/5);
		//bb.setPadding(10, 10, 50, 10);
		lll.addView(ttt);
		lll.addView(bb);
		lll.addView(ttt2);
		lll.addView(bb2);
		// lll.addView(ttt);

		l3.addView(info);
		l3.addView(lll);
		// l3.addView(bb);l3.addView(bb2);

		scheduleVO = dao2.getScheduleByID(scheduleID);

		final ArrayList<String> scheduleID2 = new ArrayList<String>();
		scheduleID2.add(String.valueOf(scheduleid));
		
		DBOpenHelper dbOpenHelper=new DBOpenHelper(CalendarActivity.this, "schedules.db");
		 SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
       Cursor cursor = db.rawQuery("select aboutID from schedule where scheduleID='"+scheduleid+"'",null);
       if(cursor.moveToFirst()) {
       	 aboutid=cursor.getString(0);
       }

		bb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 if(aboutid=="0") {
					 Intent intent = new Intent();
						intent.putStringArrayListExtra("scheduleDate", scheduleDate);
						intent.putStringArrayListExtra("rewrite", scheduleID2);
						intent.setClass(CalendarActivity.this, add.class);
						startActivity(intent);
						flag1 = 0;
						l1.removeAllViews();
						l2.removeAllViews();
				 }else {
					 new  AlertDialog.Builder(CalendarActivity.this)
						.setTitle("不能编辑" )
						.setMessage("该日程不能被编辑")
						.setPositiveButton("确定" ,  null )
						.show();
				 }
				
			}
		});

		bb2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type2 = new TextView(CalendarActivity.this);
				type2 = (TextView) findViewById(v.getId() / 13);
				// (LinearLayout)findViewById()
				final String scheduleID = String.valueOf(type2.getTag());
				if(aboutid=="0") {
				new AlertDialog.Builder(CalendarActivity.this).setTitle("删除日程")
						.setMessage("确认删除")
						.setPositiveButton("确认", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,int which) {
								   //连接数据库，将位置为0
								   try {
									   ScheduleVO vo=dao.getScheduleByID(Integer.parseInt(scheduleID));
									   
									   
									   //Toast.makeText(CalendarActivity.this, vo.toString(), Toast.LENGTH_LONG).show();
									   
									   
									   String scheduleid=vo.getscheduleid2();
									  // Toast.makeText(CalendarActivity.this, "删除日程在服务器端的id"+scheduleid, Toast.LENGTH_LONG).show();

									   str = "{\"type\":\"1\", \"cmd\":\"1-3\",\"id\":\""+scheduleid+"\"}";
										mConnection.connect(wsuri, new WebSocketHandler() {
											@Override
											public void onOpen() {
												Log.d("删除日程提交语句： ", str);
												mConnection.sendTextMessage(str);
											}
											@Override
											public void onTextMessage(String payload) {
												Log.d("删除日程服务器返回结果：", payload);
												 //  Toast.makeText(CalendarActivity.this, "删除日程服务器返回结果："+payload, Toast.LENGTH_LONG).show();

											}
											@Override
											public void onClose(int code, String reason) {
												//Log.d(TAG, "Connection lost.");
											}
										
									} );
									}
										catch (WebSocketException e) {
										//Log.d(TAG, e.toString());
									}
								dao.delete(Integer.parseInt(scheduleID));
								Intent intent1 = new Intent();
								intent1.setClass(CalendarActivity.this,
										CalendarActivity.class);
								startActivity(intent1);
                           if(update_user_schedule.mConnection.isConnected()){
							  update_user_schedule.mConnection.sendTextMessage(str);
						   }
							}
						}).setNegativeButton("取消", null).show();
				}else {
					new  AlertDialog.Builder(CalendarActivity.this)
					.setTitle("不能删除" )
					.setMessage("该日程不能被删除")
					.setPositiveButton("确定" ,  null )
					.show();
				}
				

			}
		});
		
		l22.addView(type);
		// calendar_popup_view.addView(date);
		// l2.addView(info);
		l22.addView(l3);
		l1.addView(l22);
		/*
		 * calendar_popup_view.addView(bb); calendar_popup_view.addView(bb2);
		 * 
		 * Intent intent = getIntent(); int scheduleID =
		 * Integer.parseInt(intent.getStringExtra("scheduleID"));
		 */

		// date.setText(scheduleVO.getScheduleDate());
		String ss="";
		
	    int f=scheduleVO.getpriority();
		if(f<2) ss="一般";else if(f<4)ss="较重要";
		else if(f<6)ss="重要";else if(f<8)ss="很重要";
	
		type.setText(CalendarConstant.sch_type[scheduleVO.getScheduleTypeID()]
				+ "         " + scheduleVO.getScheduleDate()+"         "+ss);
		
		info.setText(Html.fromHtml("<b> 参与人: </b>"+scheduleVO.getparticipant()+"<br>"+"内容："+scheduleVO.getScheduleContent()));

		type.setClickable(true);
		type.setFocusable(true);

		tt = new TextView(CalendarActivity.this, null);

		l4 = new LinearLayout(CalendarActivity.this, null);
		type.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tt = (TextView) ((LinearLayout) findViewById(v.getId() * 10))
						.findViewById(v.getId() * 13);
				l4 = (LinearLayout) findViewById(v.getId() * 11);// 每个type对应的id*11
				if (flag2 == 0) {
					flag2 = 1;
					tt.setVisibility(View.VISIBLE);
					l4.setVisibility(View.VISIBLE);
				} else {
					flag2 = 0;
					tt.setVisibility(View.GONE);
					l4.setVisibility(View.GONE);
				}
			}
		});

		// 长时间按住日程类型textview就提示是否删除日程信息
		/*
		 * type.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * 
		 * final String scheduleID = String.valueOf(v.getTag());
		 * 
		 * new AlertDialog.Builder(CalendarActivity.this).setTitle("删除日程")
		 * .setMessage("确认删除") .setPositiveButton("确认", new OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * 
		 * dao.delete(Integer.parseInt(scheduleID)); Intent intent1 = new
		 * Intent(); intent1.setClass(CalendarActivity.this,
		 * CalendarActivity.class); startActivity(intent1); }
		 * }).setNegativeButton("取消", null).show();
		 * 
		 * return true; } });
		 */
	}

	protected void onStart() {
		super.onStart();

	}

	protected void onRestart(){
		super.onRestart();

	}

	protected void onResume(){
super.onResume();
	}

	protected void onFreeze(Bundle outIcicle){
	}

	protected void onPause(){
super.onPause();
	}

	protected void onStop(){
super.onStop();
	}
	protected void onDestroy(){
		super.onDestroy();
		aaa.stopThread();
	}
}