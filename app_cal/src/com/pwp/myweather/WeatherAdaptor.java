package com.pwp.myweather;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class WeatherAdaptor {

	public static String getWeather(String city){
		String bufferStr = null;
		try{
				WeatherAdaptor.getWeatherData(city);
				bufferStr = Weather.getSmsMsg();
				//bufferStr = wu.readXML(xml,city);
		}catch(Throwable e){
			e.printStackTrace();
		}
		return bufferStr;
	}

//	private	String getXMLCode(String city) throws UnsupportedEncodingException{
//			// TODO Auto-generated method stub
//		String requestUrl = "http://api.map.baidu.com/telematics/v3/weather?location="
//			+city+"&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
//			StringBuffer buffer =	null;
//		try{
//			System.out.println(0);
//			URL pathUrl = new URL(requestUrl);    //创建一个URL对象
//			System.out.println(1);			HttpURLConnection urlConnect = 
//					(HttpURLConnection) pathUrl.openConnection();  //打开一个HttpURLConnection连接
//			System.out.println(2);
//			urlConnect.setConnectTimeout(3000);  // 设置连接超时时间 
//			System.out.println(3);
//			urlConnect.connect(); 
//			System.out.println(4);
//			InputStreamReader in = new InputStreamReader(urlConnect.getInputStream(),"utf-8"); //得到读取的内容
//			System.out.println(5);
//			BufferedReader reader = new BufferedReader(in);  //为输出创建BufferedReader 
//			System.out.println(6);
//			String inputLine = null; 
//			System.out.println(7);
//			buffer = new StringBuffer();
//			System.out.println(8);
//			while (((inputLine = reader.readLine()) != null)){
//				buffer.append(inputLine);			
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//			System.out.println(buffer.toString());
//			return buffer.toString();
//	}

	private static void getWeatherData(String city) throws IOException,Throwable{
		String requestUrl = "http://api.map.baidu.com/telematics/v3/weather?location=" +city+"&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
//		System.out.println(city);
//		URL url=new URL(requestUrl);
//		URLConnection connection=url.openConnection();
//		connection.connect();
		String result = request("POST",requestUrl);
		InputStream streamTemp = new ByteArrayInputStream(result.getBytes());

//		System.out.println(result+"111111111111111111111111111111");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(streamTemp,"UTF-8");
		while(parser.next()!=XmlPullParser.END_DOCUMENT){
            String element = parser.getName();
            if(element != null && element.equals("weather_data")){
                for(int dayCount = 0;dayCount<4;++dayCount){
                    if(dayCount == 0){
                    	Weather.city = city;
                    	System.out.println(city);
                    }
                    while(parser.next()!=XmlPullParser.END_DOCUMENT){
                        element = parser.getName();
                        if((element != null)){
                            if (element.equals("date")) {
                                if(dayCount == 0)
                                	Weather.current_date_time = parser.nextText();
                                else
                                	Weather.day[dayCount-1].day_of_week = parser.nextText();
                            } else if (element.equals("dayPictureUrl")) {
                                if(dayCount == 0)
                            	Weather.current_dayPictureUrl = parser.nextText();
                                else
                                	Weather.day[dayCount-1].dayPictureUrl = parser.nextText();
                            } else if (element.equals("nightPictureUrl")) {
                                if(dayCount == 0)
                            	Weather.current_nightPictureUrl = parser.nextText();
                                else
                                	Weather.day[dayCount-1].nightPictureUrl = parser.nextText();
                            } else if (element.equals("weather")) {
                                if(dayCount == 0)
                                	Weather.current_weather = parser.nextText();
                                else
                                	Weather.day[dayCount-1].weather = parser.nextText();
                            } else if (element.equals("wind")) {
                                if(dayCount == 0)
                                	Weather.current_wind = parser.nextText();
                                else
                                	Weather.day[dayCount-1].wind = parser.nextText();
                            } else if (element.equals("temperature")) {
                                if(dayCount == 0)
                                	Weather.current_temp = parser.nextText();
                                else
                                	Weather.day[dayCount-1].temperature = parser.nextText();
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
	}

	 private static String request(String method, String url) {

	        HttpResponse httpResponse = null;

	        StringBuffer result = new StringBuffer();

	        try {

	            if (method.equals("GET")) {

	                // 1.閫氳繃url鍒涘缓HttpGet瀵硅薄

	                HttpGet httpGet = new HttpGet(url);

	                // 2.閫氳繃DefaultClient鐨別xcute鏂规硶鎵ц杩斿洖涓€涓狧ttpResponse瀵硅薄

	                HttpClient httpClient = new DefaultHttpClient();

	                httpResponse = httpClient.execute(httpGet);

	                // 3.鍙栧緱鐩稿叧淇℃伅

	                // 鍙栧緱HttpEntiy

	                HttpEntity httpEntity = httpResponse.getEntity();

	                // 寰楀埌涓€浜涙暟鎹?
	                // 閫氳繃EntityUtils骞舵寚瀹氱紪鐮佹柟寮忓彇鍒拌繑鍥炵殑鏁版嵁

	                result.append(EntityUtils.toString(httpEntity, "utf-8"));

	                //寰楀埌StatusLine鎺ュ彛瀵硅薄

	                StatusLine statusLine = httpResponse.getStatusLine();



	                //寰楀埌鍗忚

	                ;

	                result.append("鍗忚:" + statusLine.getProtocolVersion() + "\r\n");

	                int statusCode = statusLine.getStatusCode();



	                result.append("鐘舵€佺爜:" + statusCode + "\r\n");



	            } else if (method.equals("POST")) {



	                // 1.閫氳繃url鍒涘缓HttpGet瀵硅薄

	                HttpPost httpPost = new HttpPost(url);

	                // 2.閫氳繃DefaultClient鐨別xcute鏂规硶鎵ц杩斿洖涓€涓狧ttpResponse瀵硅薄

	                HttpClient httpClient = new DefaultHttpClient();

	                httpResponse = httpClient.execute(httpPost);

	                // 3.鍙栧緱鐩稿叧淇℃伅

	                // 鍙栧緱HttpEntiy

	                HttpEntity httpEntity = httpResponse.getEntity();

	                // 寰楀埌涓€浜涙暟鎹?
	                // 閫氳繃EntityUtils骞舵寚瀹氱紪鐮佹柟寮忓彇鍒拌繑鍥炵殑鏁版嵁

	                result.append(EntityUtils.toString(httpEntity, "utf-8"));




	            }

	        } catch (Exception e) {

	            e.printStackTrace();

	        }

	        return result.toString();

	    }

}