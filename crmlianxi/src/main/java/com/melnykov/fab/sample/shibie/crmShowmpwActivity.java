package com.melnykov.fab.sample.shibie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.intsig.openapilib.OpenApi;
import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.crm;
import com.melnykov.fab.sample.kehu.crm_addkehu;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class crmShowmpwActivity extends Activity {

	final String wsuri = crmUrlConstant.crmIP;
	WebSocketConnection mConnection = new WebSocketConnection();
	private String postUrl = crmUrlConstant.postUrl; //处理POST请求的页面

	private String fileName = "haha.jpg";  //报文中的文件名参数
	private String uploadFile = null;    //待上传的文件路径

	ArrayList<String> countries;

	String User_id;
	EditText  editName = null;
	EditText  company = null;
	EditText telephone = null;
	String trim;
	EditText qq;
	EditText email;
	EditText address;
	EditText weixin;
	EditText interest;
	EditText growth;
	EditText paixi;
	EditText yidongphone;
	ImageView backimg;
	TextView saveimg;
	Switch detail;
	String strsex="男";
	crmMyDatabaseHelper dbHelper;

	public String updatatime()
	{
		Date d = new Date();
		String s =String.valueOf(d.getTime());
		return s;
	}


	protected ArrayList<String>
	converCursorToList(Cursor cursor) {
		ArrayList<String> result =
				new ArrayList<String>();
		// ����Cursor�����
		while (cursor.moveToNext()) {
			// ��������е����ݴ���ArrayList��
			String map = new String();
			if(User_id.equals(cursor.getString(10))) {
				map = cursor.getString(1);
				result.add(map);
			}
		}
		return result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crm_show_result);

		User_id = IMApplication.getUserid(this);

		backimg = (ImageView) findViewById(R.id.iv_back);
		saveimg = (TextView) findViewById(R.id.shibie_add);

		detail = (Switch) findViewById(R.id.detailswitch);
		qq = (EditText) findViewById(R.id.fox);
		weixin = (EditText) findViewById(R.id.tv_fxis);
		interest = (EditText) findViewById(R.id.interest);
		growth = (EditText) findViewById(R.id.chengzhangs);
		paixi = (EditText) findViewById(R.id.paixi);
		email = (EditText) findViewById(R.id.textemail);
		address = (EditText) findViewById(R.id.textaddress);
		editName = (EditText) findViewById(R.id.tv_name);
		company = (EditText) findViewById(R.id.tv_com);
		telephone = (EditText) findViewById(R.id.tv_fxid);
		address = (EditText) findViewById(R.id.email);
		yidongphone = (EditText) findViewById(R.id.yidongphone);

		TextView title = (TextView)findViewById(R.id.shibie_head);
		title.setText("名片王识别结果");

		dbHelper = new crmMyDatabaseHelper(this, "customer.db3", 1);
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
		countries = converCursorToList(cursor);

		RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
		//绑定一个匿名监听器
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) crmShowmpwActivity.this.findViewById(radioButtonId);
				//更新文本内容，以符合选中项
				strsex = rb.getText().toString().trim();
			}
		});


		final RelativeLayout detail1 = (RelativeLayout) findViewById(R.id.detail1);
		final RelativeLayout detail2 = (RelativeLayout) findViewById(R.id.detail2);
		final RelativeLayout detail3 = (RelativeLayout) findViewById(R.id.detail3);

		detail1.setVisibility(View.GONE);
		detail2.setVisibility(View.GONE);
		detail3.setVisibility(View.GONE);

		detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (detail.isChecked() == false) {
					detail1.setVisibility(View.GONE);
					detail2.setVisibility(View.GONE);
					detail3.setVisibility(View.GONE);

				} else {
					{
						detail1.setVisibility(View.VISIBLE);
						detail2.setVisibility(View.VISIBLE);
						detail3.setVisibility(View.VISIBLE);

					}
				}
			}
		});

		detail1.setOnDragListener(new View.OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {

				if (detail.isChecked() == false) {
					detail1.setVisibility(View.GONE);
					detail2.setVisibility(View.GONE);
					detail3.setVisibility(View.GONE);

				} else {
					{
						detail1.setVisibility(View.VISIBLE);
						detail2.setVisibility(View.VISIBLE);
						detail3.setVisibility(View.VISIBLE);

					}
				}
				return true;
			}
		});

		Bitmap img = null;
		Intent data = getIntent();
		trim = data.getStringExtra(OpenApi.EXTRA_KEY_IMAGE);

		if (!TextUtils.isEmpty(trim)) {
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			img = BitmapFactory.decodeFile(trim);
			imageView.setImageBitmap(img);
		}

		final String tupian = convertIconToString(img);
		String vcf = data.getStringExtra(OpenApi.EXTRA_KEY_VCF);
		VCardParser parser = new VCardParser();
		VDataBuilder builder = new VDataBuilder();

		try {
			boolean parseCan = parser.parse(vcf,"UTF-8",builder);
		} catch (VCardException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String strusername = null;
		String strtelephone = null;
		String straddress = null;
		String strorg = null;
		String stremail = null;

		List<VNode> pimContacts = builder.vNodeList;
		for(VNode contact:pimContacts)
		{
			ArrayList<PropertyNode> props = contact.propList;
			for(PropertyNode prop:props)
			{
				if("FN".equals(prop.propName))
				{
					strusername = prop.propValue;
				}
				if("TEL".equals(prop.propName))
				{
					strtelephone = prop.propValue;
				}
				if("EMAIL".equals(prop.propName))
				{
					stremail = prop.propValue;
				}
				if("ADR".equals(prop.propName))
				{
					straddress = prop.propValue;
				}
				if("ORG".equals(prop.propName))
				{
					strorg = prop.propValue;
				}
			}

			editName.setText(strusername);
			company.setText(strorg);
			telephone.setText(strtelephone);
			address.setText(straddress);
			email.setText(stremail);

			saveimg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//获取各个字段数据，保存数据库

					final String username = editName.getText().toString().trim();
					final String usercompany = company.getText().toString().trim();
					final String userphone = telephone.getText().toString().trim();
					final String useraddress = address.getText().toString().trim();
					final String strqq = qq.getText().toString().trim();
					final String strweixin = weixin.getText().toString().trim();
					final String strinterest = interest.getText().toString().trim();
					final String strgrowth = growth.getText().toString().trim();
					final String strpaixi = paixi.getText().toString().trim();
					final String stremail = email.getText().toString().trim();
					final String stryidongphone = yidongphone.getText().toString().trim();

					if (username.equals("") || username.contains("联系人名称")) {
						editName.setError("联系人名称错误");
						Dialog dialog = new AlertDialog.Builder(crmShowmpwActivity.this).setTitle("请输入联系人名称").create();
						dialog.show();
						return;
					}

					if (userphone.equals("") || !isNumeric(userphone)) {
						telephone.setError("工作电话不正确");
						Dialog dialog = new AlertDialog.Builder(crmShowmpwActivity.this).setTitle("请输入正确的工作电话").create();
						dialog.show();
						return;
					}

					if (userphone.equals("+")) {
						telephone.setError("请更改识别结果，不要包含+号");
						Dialog dialog = new AlertDialog.Builder(crmShowmpwActivity.this).setTitle("请更改识别结果，不要包含+号").create();
						dialog.show();
						return;
					}

					if (usercompany.equals("")) {
						company.setError("请输入正确的客户");
						Dialog dialog = new AlertDialog.Builder(crmShowmpwActivity.this).setTitle("请选择客户").create();
						dialog.show();
						return;
					}

					dbHelper = new crmMyDatabaseHelper(getBaseContext(), "customer.db3", 1);
					Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from customer where username  is not null", null);
					countries = converCursorToList(cursor);

					boolean tag = true;
					for(String country:countries)
						if(country.equals(usercompany)) {
							tag = false;
						}

					if(tag == true)
					{
						Dialog	dialog = new AlertDialog.Builder(crmShowmpwActivity.this).setIcon(
								android.R.drawable.btn_star).setTitle("提示").setMessage(
								"该客户尚未存在，是否进行创建？").setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(crmShowmpwActivity.this, crm_addkehu.class);
										startActivityForResult(intent, 0);
									}
								}).setNegativeButton("否", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								return;
							}
						}).create();
						dialog.show();
						return;
					}

					final String str = "{\"cmd\":\"addContacter\"," +
							"\"type\":\"2\"," +
							"\"uid\":\"" + User_id + "\"," +
							"\"customer\":\"" + usercompany + "\"," +//company就是customer
							"\"username\":\"" + username + "\", " +
							"\"strsex\":\"" + strsex + "\"," +
							"\"workphone\":\"" + userphone + "\"," +
							"\"yidongdianhua\":\"" + stryidongphone + "\"," +
							"\"strqq\":\"" + strqq + "\"," +
							"\"strweixin\":\"" + strweixin + "\"," +
							"\"strinterest\":\"" + strinterest + "\"," +
							"\"email\":\"" + stremail + "\"," +
							"\"address\":\"" + useraddress + "\"," +
							"\"pic\":\"" + " " + "\"," +  //pic怎么算
							"\"relation\":\"" + " " + "\"," +
							"\"degree\":\"" + " " + "\"," +
							"\"strgrowth\":\"" + strgrowth + "\", " +
							"\"strpaixi\":\"" + strpaixi + "\"}";
					mConnection.disconnect();

			/*		String pic = trim;
					Log.e("名片王pic", trim);
					//复制此文件改变名片
					fileName = User_id + "-1-24" + ".jpg";
					uploadFile = "/mnt/sdcard/mingpian/" + User_id + "/" + fileName;
					Log.e(uploadFile, uploadFile);
					copyFile(pic, uploadFile);
					uploadFile();*/

					try {
						mConnection.connect(wsuri, new WebSocketHandler() {
							@Override
							public void onOpen() {
								Log.e("发送的数据", str);
								mConnection.sendTextMessage(str);
							}
							@Override
							public void onTextMessage(String payload) {
								Log.e("haha", "Got echo: " + payload);
								try {
									JSONObject jsonObject = new JSONObject(payload);
									String result=jsonObject.getString("error");
									String cmd=jsonObject.getString("cmd");
									String time = jsonObject.getString("time");
									if(result.equals("1")&&cmd.equals("addContacter")) {
										crmMyDatabaseHelper dbHelper;
										dbHelper = new crmMyDatabaseHelper(getBaseContext(), "customer.db3", 1);
										String id =jsonObject.getString("id");
										String pic = trim;
										Log.e("名片王pic",trim);
										//复制此文件改变名片
										fileName= User_id+"-1-" + id + ".jpg";
										uploadFile = "/mnt/sdcard/mingpian/"+User_id+"/"+fileName;
										Log.e(uploadFile, uploadFile);
										copyFile(pic, uploadFile);
										uploadFile();

										insertData(dbHelper.getReadableDatabase(), username, strsex, userphone, stryidongphone, strqq, strweixin, strinterest, strgrowth, strpaixi, User_id, usercompany, stremail, useraddress, uploadFile, id, "", "");
										dbHelper.getWritableDatabase().execSQL("update Updata_KehuLianxi set uid = ?,kehu_time = ?,lianxiren_time = ? where uid=?",
												new String[]{User_id, time, "", User_id});
										crmShowmpwActivity.this.finish();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
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
			});

			backimg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					crmShowmpwActivity.this.finish();
				}
			});

		}
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0) {
			String change01 = data.getStringExtra("data");
			if (change01 == null) {
				return;
			}
			company.setText(change01);

		}
		// 根据上面发送过去的请求吗来区别
	}


	/* 上传文件至Server的方法 */
	private void uploadFile()
	{
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try
		{
			URL url = new URL(postUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
			con.setDoOutput(true);
          /* Read from the connection. Default is true.*/
			con.setDoInput(true);
          /* Post cannot use caches */
			con.setUseCaches(false);
          /* Set the post method. Default is GET*/
			con.setRequestMethod("POST");
          /* 设置请求属性 */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
          /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " +
					"name=\"file\";filename=\"" +
					fileName + "\"" +
					end);
			ds.writeBytes(end);
          /* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
          /* 设置每次写入8192bytes */
			int bufferSize = 8192;
			byte[] buffer = new byte[bufferSize];   //8k
			int length = -1;
          /* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1)
			{
            /* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* 关闭流，写入的东西自动生成Http正文*/
			fStream.close();
          /* 关闭DataOutputStream */
			ds.close();
          /* 从返回的输入流读取响应信息 */
			InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1)
			{
				b.append((char) ch);
			}
          /* 显示网页响应内容 */
			Toast.makeText(crmShowmpwActivity.this, "上传成功", Toast.LENGTH_SHORT).show();//Post成功

		} catch (Exception e)
		{
            /* 显示异常信息 */
			Toast.makeText(crmShowmpwActivity.this, "Fail:" + e, Toast.LENGTH_SHORT).show();//Post失败
		}
	}

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
	}

	private void insertData(SQLiteDatabase db, String username, String strsex, String workphone, String yidongphone, String strqq, String strweixin
			, String strinterest, String growth, String paixi,String uid,String strcompany,String stremail,String straddress,String pic,String id,String yuliu1,String yuliu2) {
		// 执行插入语句
		db.execSQL("insert into lianxiren values(null, ? , ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)"
				, new String[]{username, strsex, workphone, yidongphone, strqq, strweixin, strinterest, growth, paixi,uid,strcompany,stremail,straddress,pic,id,"",""});
	}

	public static String convertIconToString(Bitmap bitmap)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] appicon = baos.toByteArray();// 转为byte数组
		return Base64.encodeToString(appicon, Base64.DEFAULT);
	}

}
