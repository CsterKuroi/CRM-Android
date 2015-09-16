package com.example.renxin.shujia.karl.reader;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.renxin.shujia.R;
import com.example.renxin.shujia.UrlTools;
import com.example.renxin.shujia.karl.view.LabelView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
//界面布局bug
//bug之json通讯
public class BookShelfActivity extends AppCompatActivity implements WaveSwipeRefreshLayout.OnRefreshListener{
	private GridView bookShelf;
	ArrayList<String> name;
	ArrayList<String> data;
	ShlefAdapter adapter=new ShlefAdapter();
	private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
	Button search;
	Button back;

	private String packageName = "cn.wps.moffice_eng";
	private String className = "cn.wps.moffice.documentmanager.PreStartActivity2";
	private String module = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.crm_study_main);
		bookShelf = (GridView) findViewById(R.id.bookShelf);
		Intent intent = getIntent();
		module = intent.getStringExtra("module");
		TextView head = (TextView) findViewById(R.id.tv_head);
		head.setText(module);
		name = new ArrayList<String>();
		data = new ArrayList<String>();
		initView();
		mWaveSwipeRefreshLayout.setRefreshing(true);
		studyjson();


		name = (ArrayList<String>) data.clone();

		search = (Button) findViewById(R.id.btn_rightTop);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] str = new String[]{"测试公司"};
				str = name.toArray(new String[name.size()]);
				Intent intent = new Intent(BookShelfActivity.this, com.example.renxin.shujia.SortListView.crmSortListMainActivity.class);
				intent.putExtra("title", "客户");
				intent.putExtra("data", str);
				startActivityForResult(intent, 1);
			}
		});


		back = (Button) findViewById(R.id.btn_leftTop);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BookShelfActivity.this.finish();
			}
		});


	}

	private void initView() {
		mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
		mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
		mWaveSwipeRefreshLayout.setOnRefreshListener(this);
		mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
	}

	public void studyjson()
	{

		final String wsuri = UrlTools.crmIP;
		final WebSocketConnection mConnection = new WebSocketConnection();
		mConnection.disconnect();
		try {
			mConnection.connect(wsuri, new WebSocketHandler() {
				@Override
				public void onOpen() {
					Toast.makeText(BookShelfActivity.this, "正在连接服务器。。。", Toast.LENGTH_SHORT);
					String str;
					str = "{\"cmd\":\"crmstudy\"," +
								"\"type\":\"9\"," +
							     "\"time\":\"0\"," +
								"\"filetype\":\"" + module + "\"}";

					Log.e("发送json 字符串", str);
					mConnection.sendTextMessage(str);
				}
				@Override
				public void onTextMessage(String payload) {
					Toast.makeText(BookShelfActivity.this, "正在与服务器同步。。。  ", Toast.LENGTH_SHORT);
					try {
						JSONObject jsonObject = new JSONObject(payload);
						String result=jsonObject.getString("error");
						if(result.equals("1")) {
							JSONArray content = jsonObject.getJSONArray("filelist");
							for (int i = 0; i < content.length(); i++) {
								JSONObject jsonObject2 = content.getJSONObject(i);
								String url = jsonObject2.getString("url");
								String dstname = jsonObject2.getString("name");
								String changetime = jsonObject2.getString("changetime");
								String createtime = jsonObject2.getString("createtime");
								//data每增加一个文件就开始下载
								data.add(dstname);
								name.add(dstname);
								downFile(url, dstname);
							}
							refresh();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					mConnection.disconnect();
				}
				@Override
				public void onClose(int code, String reason) {
					Log.e("close", "Connection lost.");
				}
			});
		} catch (WebSocketException e) {
			Toast.makeText(BookShelfActivity.this, "服务器已断开  ", Toast.LENGTH_SHORT);
		}
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String searchdata = data.getStringExtra("data");
		if (searchdata == null) {
			return;
		}
		name.clear();
		name.add(searchdata);
		bookShelf.setAdapter(adapter);
		bookShelf.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				if(arg2>=name.size()){

				}else{
					openFile("/mnt/sdcard/crmstudy/"+name.get(arg2));
				}
			}
		});
	}

	private boolean isAppInstalled(Context context,String packagename)
	{
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
		}catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo == null){
			return false;
		}else{
			return true;
		}
	}

	public boolean openFile(String path)
	{
		if(isAppInstalled(getBaseContext(),packageName))
		{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("OPEN_MODE", "READ_ONLY");
			bundle.putBoolean("SEND_CLOSE_BROAD", true);
			bundle.putString("THIRD_PACKAGE", "selfPackageName");
			bundle.putBoolean("CLEAR_BUFFER", true);
			bundle.putBoolean("CLEAR_TRACE", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setClassName(packageName, className);
			File file = new File(path);
			if (file == null || !file.exists())
			{                        return false;
			}

			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			intent.putExtras(bundle);
			try
			{
				startActivity(intent);
			}
			catch (ActivityNotFoundException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("是否下载office套件以打开应用？")
					.setCancelable(false)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									Uri uri = Uri.parse("market://details?id="+packageName);//id为包名
									Intent it = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(it);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
		return true;
	}

	public void downFile(String filename,String destfile)
	{
		String savefile =  "/mnt/sdcard/crmstudy/";
		File destDir = new File(savefile);

		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		FinalHttp fh = new FinalHttp();
		final HttpHandler handler = fh.download(UrlTools.download_url+filename, //这里是下载的路径
				savefile + destfile, //这是保存到本地的路径
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {

					}

					@Override
					public void onSuccess(File t) {

					}

				});
	}


	class ShlefAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return name.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			contentView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.crm_study_item1, null);
			TextView view=(TextView) contentView.findViewById(R.id.imageView1);
			LabelView label = new LabelView(getApplicationContext());
			label.setText("New!");
			label.setBackgroundColor(0xff03a9f4);
			label.setTargetView(view,10, LabelView.Gravity.RIGHT_TOP);
			view.setText(name.get(position));
			view.setBackgroundResource(R.drawable.cover_txt);
			return contentView;
		}

	}


	private void refresh(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mWaveSwipeRefreshLayout.setRefreshing(false);
				bookShelf.setAdapter(adapter);
				bookShelf.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
											long arg3) {
						// TODO Auto-generated method stub
						if(arg2>=name.size()){

						}else{
							openFile("/mnt/sdcard/crmstudy/"+name.get(arg2));
						}
					}
				});
			}
		}, 2000);
	}

	@Override
	protected void onResume() {
	/*	mWaveSwipeRefreshLayout.setRefreshing(true);
		refresh();*/
		super.onResume();
	}

	@Override
	public void onRefresh() {
		//重新加载name,更新视图
		name.clear();
		name = (ArrayList<String>) data.clone();
		refresh();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认退出吗")
					.setCancelable(false)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}