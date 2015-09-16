package com.pwp.activity;

import com.pwp.application.SysApplication;
import com.pwp.borderText.BorderTextView;
import com.pwp.calendar.LunarCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 日期转换
 * @author dayuan
 *
 */
public class CalendarConvert extends Activity implements OnTouchListener{

	private LunarCalendar lc = null;
	private BorderTextView convertDate = null;
	private BorderTextView convertBT = null;
	private TextView lunarDate = null;
	private ImageButton back;

	private int year_c;
	private int month_c;
	private int day_c;

	public CalendarConvert(){
		lc = new LunarCalendar();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.convert);

		back=(ImageButton)findViewById(R.id.ImageButton_back);
		back.setOnTouchListener(this);
		convertDate = (BorderTextView) findViewById(R.id.convertDate);
		convertBT = (BorderTextView) findViewById(R.id.convert);
		lunarDate = (TextView) findViewById(R.id.convertResult);

		Intent intent = getIntent();
		int[] date = intent.getIntArrayExtra("date");
		year_c = date[0];
		month_c = date[1];
		day_c = date[2];
		convertDate.setText(year_c+"年"+month_c+"月"+day_c);
		convertDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new DatePickerDialog(CalendarConvert.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth) {

						if(year < 1901 || year > 2049){
							//不在查询范围内
							new AlertDialog.Builder(CalendarConvert.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
						}else{
							year_c = year;
							month_c = monthOfYear+1;
							day_c = dayOfMonth;
							convertDate.setText(year_c+"年"+month_c+"月"+day_c);
						}
					}
				}, year_c, month_c-1, day_c).show();
			}
		});

		convertBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String lunarDay = getLunarDay(year_c,month_c,day_c);
				String lunarYear = String.valueOf(lc.getYear());
				String lunarMonth = lc.getLunarMonth();

				lunarDate.setText(lunarYear+"年"+lunarMonth+lunarDay);
			}
		});

	}

	/**
	 * 根据日期的年月日返回阴历日期
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(v.getId()==R.id.ImageButton_back) {
            	this.finish();
            }
		}
		return true;
	}
	public String getLunarDay(int year, int month, int day) {
		String lunarDay = lc.getLunarDate(year, month, day, true);
		// {由于在取得阳历对应的阴历日期时，如果阳历日期对应的阴历日期为"初一"，就被设置成了月份(如:四月，五月。。。等)},所以在此就要判断得到的阴历日期是否为月份，如果是月份就设置为"初一"
		if (lunarDay.substring(1, 2).equals("月")) {
			lunarDay = "初一";
		}
		return lunarDay;
	}
}
