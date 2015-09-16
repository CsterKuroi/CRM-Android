package com.pwp.myweather;/*
package com.pwp.myweather;

import java.io.BufferedReader;


import org.apache.commons.logging.Log; 

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.R;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Weather1 extends Activity {
	private TextView textview1;
	private ImageView image1;
	private ImageView image2;
	private String city;
	private String ip;
	StringBuffer sb;
	URLConnection conn = null;
	InputStream fis = null;
	InputStreamReader in = null;
	BufferedReader buffer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.main);

		
		image1 = (ImageView) findViewById(R.id.image1);
		image2 = (ImageView) findViewById(R.id.image2);
		
		getLocalIPAddress();	
		
		//getCityIP();
		
		//getCityByIp();
		 new Thread(networkTask).start();


		//Toast.makeText(this, "mmm", Toast.LENGTH_LONG).show();
	}

	// 获取IP地址

	
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
//			Toast.makeText(WeatherDemo.this,"sgfdg",Toast.LENGTH_LONG).show();
			Bundle data = msg.getData();
	        String val = data.getString("value");
	        Toast.makeText(Weather1.this,Weather.current_weather,Toast.LENGTH_LONG).show();
	       
//	        setup_city.setText("镇江");
  firstday_temp.setText("温度："+Weather.current_date_time+"气候："+Weather.current_weather);
	        firstday_wind.setText("风力："+Weather.current_wind);
	        city.setText(Weather.city);
	        
	        secondday_temp.setText(Weather.day[0].temperature+Weather.day[0].weather_service);
	        thirdday_temp.setText(Weather.day[1].temperature+Weather.day[1].weather_service);
	        forthday_temp.setText(Weather.day[2].temperature+Weather.day[2].weather_service);

		}
		
	};

	
	
	
	Runnable networkTask = new Runnable() {  
		   @Override  
		    public void run() {  
		       // TODO  
		       // 在这里进行 http request.网络请求相关操作  
			  
			   try {
					 Looper.prepare();
					URL url = new URL("http://whois.pconline.com.cn/ip.jsp");
					HttpURLConnection connect = (HttpURLConnection) url.openConnection();
					
					InputStream is = connect.getInputStream();
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					
					
					
					byte[] buff = new byte[256];
					int rc = 0;
					while ((rc = is.read(buff, 0, 256)) > 0) {
						outStream.write(buff, 0, rc);
						
					}
					
					System.out.println(outStream);
					
					
					byte[] b = outStream.toByteArray();
				

					// 关闭
					outStream.close();
					is.close();
					connect.disconnect();
					
				
					String address = new String(b,"gbk");
					byte []by=address.getBytes("utf-8");
					String add=new String (by,0,by.length);
					String add2=add.substring(0,add.indexOf("市")).trim();
					 Toast.makeText(Weather1.this, add2, Toast.LENGTH_LONG).show();
					setCity(address.substring(0,address.indexOf("市")));
					
					
					
					//String iso = new String(address.getBytes("UTF-8"),"ISO-8859-1");   
					 //address=new String(iso.getBytes("ISO-8859-1"),"UTF-8"); 
					//String a = new String(address.getBytes("gbk"),"utf-8"); 
					
if (address.startsWith("北")||address.startsWith("上")||address.startsWith("重")){
						setCity(address.substring(0,address.indexOf("市")));
						Toast.makeText(Weather.this,"iiii",  Toast.LENGTH_LONG).show();
					}
					if(address.startsWith("香")){
						setCity(address.substring(0,address.indexOf("港")));
						Toast.makeText(Weather.this, getCity(), Toast.LENGTH_LONG).show();
					}
					if(address.startsWith("澳")){
						setCity(address.substring(0,address.indexOf("门")));
						Toast.makeText(Weather.this, getCity(), Toast.LENGTH_LONG).show();
					}
					if (address.indexOf("省") != -1) {
						setCity(address.substring(address.indexOf("省") + 1, address.indexOf("市")));
						Toast.makeText(Weather.this, getCity(), Toast.LENGTH_LONG).show();
					}

					
				
					
					
					Message msg = new Message();
			        Bundle data = new Bundle();
					data.putString("value",WeatherAdaptor.getWeather(add2));		
			        msg.setData(data);
			        handler.sendMessage(msg);
				
			Looper.loop();
			   
			
					SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
					rpc.addProperty("theCityName", getCity());
					
					

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.bodyOut = rpc;
					envelope.dotNet = true;
					envelope.setOutputSoapObject(rpc);
					HttpTransportSE ht = new HttpTransportSE(URL);

					
					
					ht.debug = true;
					
					Toast.makeText(Weather.this, getCity(), Toast.LENGTH_LONG).show();
                    Looper.loop();

					ht.call(SOAP_ACTION, envelope);
					
					 
					detail = (SoapObject) envelope.getResponse();
					  

					parseWeather(detail);

					
				} catch (Exception e) {
					e.printStackTrace();
					
					Toast.makeText(Weather1.this, e.toString(), Toast.LENGTH_LONG).show();
				   
				}
			   	
		     Message msg = new Message();  
		     Bundle data = new Bundle();  
		     data.putString("value", "cityname");  
		     msg.setData(data);  
		     handler.sendMessage(msg);  
		    }
	};

		//根据IP得到城市
public void getCityByIp() {
			try {
				
				URL url = new URL("http://whois.pconline.com.cn/ip.jsp?ip=" + getIp());
				HttpURLConnection connect = (HttpURLConnection) url.openConnection();
				
				InputStream is = connect.getInputStream();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				
				Toast.makeText(Weather.this, url.toString(), Toast.LENGTH_LONG).show();
				
				byte[] buff = new byte[256];
				int rc = 0;
				while ((rc = is.read(buff, 0, 256)) > 0) {
					outStream.write(buff, 0, rc);
					
				}
				
				System.out.println(outStream);
				
				
				byte[] b = outStream.toByteArray();
			

				// 关闭
				outStream.close();
				is.close();
				connect.disconnect();
				String address = new String(b,"UTF-8");
				if (address.startsWith("北")||address.startsWith("上")||address.startsWith("重")){
					setCity(address.substring(0,address.indexOf("市")));
				}
				if(address.startsWith("香")){
					setCity(address.substring(0,address.indexOf("港")));
				}
				if(address.startsWith("澳")){
					setCity(address.substring(0,address.indexOf("门")));
				}
				if (address.indexOf("省") != -1) {
					setCity(address.substring(address.indexOf("省") + 1, address.indexOf("市")));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(Weather.this, e.toString(), Toast.LENGTH_LONG).show();

				
			}
		}


	// 判断IP是wifi还是GPRS
	public void getLocalIPAddress() {
		 WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
	        //判断wifi是否开启  
	        if (!wifiManager.isWifiEnabled()) {  
	        	Toast.makeText(this, "没有可用的网络", Toast.LENGTH_LONG).show();
	            wifiManager.setWifiEnabled(true);    
	        }  
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
	        int ipAddress = wifiInfo.getIpAddress();   
	        ip = intToIp(ipAddress);   
		    Toast.makeText(this, ip, Toast.LENGTH_LONG).show();

	} 
	private String intToIp(int i) {       
        
        return (i & 0xFF ) + "." +       
      ((i >> 8 ) & 0xFF) + "." +       
      ((i >> 16 ) & 0xFF) + "." +       
      ( i >> 24 & 0xFF) ;  
   }   

	// 获取天气
	private static final String NAMESPACE = "http://WebXml.com.cn/";

	// WebService地址
	private static String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	private static final String METHOD_NAME = "getWeatherbyCityName ";

	private static String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName ";
	private String weatherToday;
	private SoapObject detail;
	private String weatherNow;
	private String weatherWillBe;

	private void setIcon(String weather_service, ImageView imageview) {
		Toast.makeText(this, weather_service, Toast.LENGTH_LONG).show();

		if (weather_service.equalsIgnoreCase("nothing.gif"))
			imageview.setBackgroundResource(R.drawable.a_nothing);
		if (weather_service.equalsIgnoreCase("0.gif"))
			imageview.setBackgroundResource(R.drawable.a_0);
		if (weather_service.equalsIgnoreCase("1.gif"))
			imageview.setBackgroundResource(R.drawable.a_1);
		if (weather_service.equalsIgnoreCase("2.gif"))
			imageview.setBackgroundResource(R.drawable.a_2);
		if (weather_service.equalsIgnoreCase("3.gif"))
			imageview.setBackgroundResource(R.drawable.a_3);
		if (weather_service.equalsIgnoreCase("4.gif"))
			imageview.setBackgroundResource(R.drawable.a_4);
		if (weather_service.equalsIgnoreCase("5.gif"))
			imageview.setBackgroundResource(R.drawable.a_5);
		if (weather_service.equalsIgnoreCase("6.gif"))
			imageview.setBackgroundResource(R.drawable.a_6);
		if (weather_service.equalsIgnoreCase("7.gif"))
			imageview.setBackgroundResource(R.drawable.a_7);
		if (weather_service.equalsIgnoreCase("8.gif"))
			imageview.setBackgroundResource(R.drawable.a_8);
		if (weather_service.equalsIgnoreCase("9.gif"))
			imageview.setBackgroundResource(R.drawable.a_9);
		if (weather_service.equalsIgnoreCase("10.gif"))
			imageview.setBackgroundResource(R.drawable.a_10);
		if (weather_service.equalsIgnoreCase("11.gif"))
			imageview.setBackgroundResource(R.drawable.a_11);
		if (weather_service.equalsIgnoreCase("12.gif"))
			imageview.setBackgroundResource(R.drawable.a_12);
		if (weather_service.equalsIgnoreCase("13.gif"))
			imageview.setBackgroundResource(R.drawable.a_13);
		if (weather_service.equalsIgnoreCase("14.gif"))
			imageview.setBackgroundResource(R.drawable.a_14);
		if (weather_service.equalsIgnoreCase("15.gif"))
			imageview.setBackgroundResource(R.drawable.a_15);
		if (weather_service.equalsIgnoreCase("16.gif"))
			imageview.setBackgroundResource(R.drawable.a_16);
		if (weather_service.equalsIgnoreCase("17.gif"))
			imageview.setBackgroundResource(R.drawable.a_17);
		if (weather_service.equalsIgnoreCase("18.gif"))
			imageview.setBackgroundResource(R.drawable.a_18);
		if (weather_service.equalsIgnoreCase("19.gif"))
			imageview.setBackgroundResource(R.drawable.a_19);
		if (weather_service.equalsIgnoreCase("20.gif"))
			imageview.setBackgroundResource(R.drawable.a_20);
		if (weather_service.equalsIgnoreCase("21.gif"))
			imageview.setBackgroundResource(R.drawable.a_21);
		if (weather_service.equalsIgnoreCase("22.gif"))
			imageview.setBackgroundResource(R.drawable.a_22);
		if (weather_service.equalsIgnoreCase("23.gif"))
			imageview.setBackgroundResource(R.drawable.a_23);
		if (weather_service.equalsIgnoreCase("24.gif"))
			imageview.setBackgroundResource(R.drawable.a_24);
		if (weather_service.equalsIgnoreCase("25.gif"))
			imageview.setBackgroundResource(R.drawable.a_25);
		if (weather_service.equalsIgnoreCase("26.gif"))
			imageview.setBackgroundResource(R.drawable.a_26);
		if (weather_service.equalsIgnoreCase("27.gif"))
			imageview.setBackgroundResource(R.drawable.a_27);
		if (weather_service.equalsIgnoreCase("28.gif"))
			imageview.setBackgroundResource(R.drawable.a_28);
		if (weather_service.equalsIgnoreCase("29.gif"))
			imageview.setBackgroundResource(R.drawable.a_29);
		if (weather_service.equalsIgnoreCase("30.gif"))
			imageview.setBackgroundResource(R.drawable.a_30);
		if (weather_service.equalsIgnoreCase("31.gif"))
			imageview.setBackgroundResource(R.drawable.a_31);
	}

public void getWeather(String cityName) {
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			rpc.addProperty("theCityName", cityName);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(URL);

			ht.debug = true;

			ht.call(SOAP_ACTION, envelope);
			detail = (SoapObject) envelope.getResponse();
			parseWeather(detail);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(Weather.this, e.toString(), Toast.LENGTH_LONG).show();
		
		}
	}


	private void parseWeather(SoapObject detail)
			throws UnsupportedEncodingException {
		textview1 = (TextView) this.findViewById(R.id.TextView01);

		String date = detail.getProperty(6).toString();

		// 当天天气
		weatherToday = "\n天气：" + date.split(" ")[1];
		weatherToday = weatherToday + "\n气温："
				+ detail.getProperty(5).toString();
		weatherToday = weatherToday + "\n风力："
				+ detail.getProperty(7).toString() + "\n";

		weatherNow = detail.getProperty(8).toString();
		weatherWillBe = detail.getProperty(9).toString();

		textview1.setText(getIp() + '\n' + getCity() + "\n今天"
				+ weatherToday);
		setIcon(weatherNow, image1);
		setIcon(weatherWillBe, image2);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
*/
