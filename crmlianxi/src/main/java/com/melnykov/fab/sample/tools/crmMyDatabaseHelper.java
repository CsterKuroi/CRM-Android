/**
 *
 */
package com.melnykov.fab.sample.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.melnykov.fab.sample.R;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Description:
 * <br/>网站: <a href="http://www.crazyit.org">疯狂Java联盟</a>
 * <br/>Copyright (C), 2001-2014, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author  Yeeku.H.Lee kongyeeku@163.com
 * @version  1.0
 */

public class crmMyDatabaseHelper extends SQLiteOpenHelper
{
	final String wsuri = crmUrlConstant.crmIP;
	WebSocketConnection mConnection = new WebSocketConnection();

	IMApplication app;
	String User_id;
	private Context context;

	final String CREATE_kehuTABLE_SQL =
			"create table customer(_id integer primary " +
					"key autoincrement ,username,userphone,useremail,userfox,useraddress,leixing,xingzhi,guimo,userbeizhu,uid,id,kehustate,kehurank)";
	final String CREATE_lianxiTABLE_SQL =
			"create table lianxiren(_id integer primary " +
					"key autoincrement , username,strsex ,workphone,yidongphone,strqq,strweixin,strinterest,strgrowth,strpaixi,uid,company,email,address,pic,id,relation,degree)";
	final String  CREATE_Updata_KehuLianxi =
			"create table Updata_KehuLianxi(_id integer primary " +
					"key autoincrement,uid,kehu_time,lianxiren_time)";

	final String  CREATE_care_sql =
			"create table care_table(_id integer primary " +
					"key autoincrement, uid text,type text,time text,time2 text,note text,fid text,fname text,fsex text,fphone text,state text)";

	public crmMyDatabaseHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
		this.context = context;
		User_id = IMApplication.getUserid(context);
	}



	private void parseJSON2(String jsonData,String Uid) {
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			String result=jsonObject.getString("error");
			String cmd=jsonObject.getString("cmd");
			String updatatime=jsonObject.getString("servertime");
			if(result.equals("1")&&cmd.equals("updatecrm")) {

				SQLiteDatabase db = this.getWritableDatabase();
				Log.e(updatatime, updatatime);
				db.execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
						new String[]{Uid, updatatime,"",Uid});
				//json解析有错误
				JSONArray content=jsonObject.getJSONArray("customers");
				for (int i = 0; i < content.length(); i++) {
					JSONObject jsonObject2 = content.getJSONObject(i);

					String  useraddress = jsonObject2.getString("useraddress");
					String useremail  =  jsonObject2.getString("useremail");
					String uid  =  jsonObject2.getString("uid");
					String guimo  =  jsonObject2.getString("guimo");
					String userfox  =  jsonObject2.getString("userfox");
					String username  =  jsonObject2.getString("uname");
					String userbeizhu  =  jsonObject2.getString("userbeizhu");
					String xingzhi  =  jsonObject2.getString("xingzhi");
					String leixing  =  jsonObject2.getString("leixing");
					String id  =  jsonObject2.getString("id");
					String userphone  =  jsonObject2.getString("userphone");
					String kehustate  =  jsonObject2.getString("kehustate");
					String kehurank  =  jsonObject2.getString("kehurank");

					if(jsonObject2.getString("status").equals("1"))
					db.execSQL("insert into customer values(null, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
							, new String[]{username, userphone, useremail, userfox, useraddress, leixing, xingzhi, guimo, userbeizhu,uid,id,kehustate,kehurank});
					else
						db.execSQL("delete from customer where id = ?"
								, new String[]{id});


				}
//				"key autoincrement , username,strsex ,workphone,yidongphone,strqq,strweixin,strinterest,strgrowth,strpaixi,uid,company,email,address,pic,id,relation,degree)";
				JSONArray  contacter=jsonObject.getJSONArray("contacters");
				for (int i = 0; i < contacter.length(); i++) {

					JSONObject jsonObject2 = contacter.getJSONObject(i);
					String  username = jsonObject2.getString("username");
					String strsex  =  jsonObject2.getString("strsex");
					String workphone  =  jsonObject2.getString("workphone");
					String yidongphone =  jsonObject2.getString("yidongphone");
					String strqq  =  jsonObject2.getString("strqq");
					String strweixin  =  jsonObject2.getString("strweixin");
					String strinterest  =  jsonObject2.getString("strinterest");
					String strgrowth  =  jsonObject2.getString("strgrowth");
					String strpaixi  =  jsonObject2.getString("strpaixi");
					String uid  =  jsonObject2.getString("uid");
					String company  =  jsonObject2.getString("customer");
					String email  =  jsonObject2.getString("email");
					String address  =  jsonObject2.getString("address");
					String picurl  =  jsonObject2.getString("pic");
					String id  =  jsonObject2.getString("id");
					String relation  =  jsonObject2.getString("relation");
					String degree  =  jsonObject2.getString("degree");

					if (picurl.equals(""))
						picurl = "haha.jpg";
			/*		if(!picurl.contains("haha.jpg"))
					{
						String pic = "/mnt/sdcard/mingpian/" + User_id +"/"+ updatatime()+".jpg";
*//*
						downFile(picurl,pic);

							*//**//*String savefile =  "/mnt/sdcard/mingpian/"+ User_id +"/";
							File destDir = new File(savefile);
							if (!destDir.exists()) {
								destDir.mkdirs();
							}
							new MultipartThreadDownloador(crmUrlConstant.download_url+picurl,
									"/mnt/sdcard/mingpian/" + User_id , updatatime()+".jpg", 2).download();*//*

					    	picurl=pic;
					}*/

					if(jsonObject2.getString("status").equals("1"))
					db.execSQL("insert into lianxiren values(null, ? , ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)"
							, new String[]{username, strsex, workphone, yidongphone, strqq, strweixin, strinterest, strgrowth, strpaixi, uid, company, email, address, picurl,id,relation,degree});
					else
						db.execSQL("delete from lianxiren where id = ?",new String[]{id});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void downFile(String filename,String destfile)
	{
		String savefile =  "/mnt/sdcard/mingpian/"+ User_id +"/";
		File destDir = new File(savefile);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		FinalHttp fh = new FinalHttp();
		final HttpHandler handler = fh.download(crmUrlConstant.download_url+filename, //这里是下载的路径
				destfile, //这是保存到本地的路径
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {

					}

					@Override
					public void onSuccess(File t) {
					Log.e("下载成功！","图片");
					}

				});
	}

	//数据库更新
	public String updatetime(String uid)
	{
		Cursor cursor = this.getReadableDatabase().rawQuery("select * from Updata_KehuLianxi where uid  is not null", null);
		while (cursor.moveToNext())
		{
			if(uid.equals(cursor.getString(1)))
				return cursor.getString(2);
		}
		return "0";
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		final String User_id = IMApplication.getUserid(context);

		boolean user_exit = false;
		Cursor cursor = db.rawQuery("select * from Updata_KehuLianxi where uid  is not null", null);
		while (cursor.moveToNext())
		{
			if(User_id.equals(cursor.getString(1)))
				user_exit = true;
		}

		if(user_exit == false)
	    	db.execSQL("insert into Updata_KehuLianxi values(null, ? , ?, ?)"
				, new String[]{User_id, "0", "0"});

		try {
			mConnection.connect(wsuri, new WebSocketHandler() {
				@Override
				public void onOpen() {
					String time = updatetime(User_id);
					//修改1，换成此申请，需要重新定义
					String str = "{\"cmd\":\"updatecrm\"," +
							"\"type\":\"2\"," +
							"\"updatetime\":\""+time+"\","+
							"\"uid\":\""+User_id+"\"}";
					Log.e(time+"读取服务器时间",time);
					mConnection.sendTextMessage(str);
				}
				@Override
				public void onTextMessage(String payload) {
					Log.e("数据开始同步。。。。。", "Got echo: " + payload);
					parseJSON2(payload, User_id);
					//mConnection.disconnect();
				}
				@Override
				public void onClose(int code, String reason) {
					Log.e("close", "Connection lost.");
				}
			});
		} catch (WebSocketException e) {
			Log.e("cuowu", e.toString());
		}

	}


	public String updatatime()
	{
		Date d = new Date();
		String s =String.valueOf(d.getTime());
		return s;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// 第一次使用数据库时自动建表
		db.execSQL(CREATE_kehuTABLE_SQL);
		db.execSQL(CREATE_lianxiTABLE_SQL);
		db.execSQL(CREATE_Updata_KehuLianxi);
		db.execSQL(CREATE_care_sql);
		copyToSD(context);



	}

	public  void copyToSD(Context context) {
		InputStream is = null;
		FileOutputStream fos = null;

		String authorid = IMApplication.getUserid(context);

		try {
			String path = android.os.Environment.getExternalStorageDirectory().getPath();
			path = path + "/mingpian/";
			String dbPathAndName = path + "haha.jpg";
			File file = new File(path);
			if (file.exists() == false)
			{
				file.mkdir();
			}
			File dbFile = new File(dbPathAndName);
			if (!dbFile.exists()) {
				is = context.getResources().openRawResource(R.drawable.hi);
				fos = new FileOutputStream(dbFile);
				byte[] buffer = new byte[8 * 1024];// 8K
				while (is.read(buffer) > 0)// >
				{
					fos.write(buffer);
				}
			}

		} catch (Exception e) {

		} finally {
			try {
				if (is != null) {
					is.close();
				}

				if (fos != null) {
					fos.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db
			, int oldVersion, int newVersion)
	{
		System.out.println("--------onUpdate Called--------"
				+ oldVersion + "--->" + newVersion);
	}
}
