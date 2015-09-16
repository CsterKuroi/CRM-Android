package com.example.jogle.attendance;

import android.os.StrictMode;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jiagao on 2015/8/9.
 */
public class JGUploadPic extends Thread {
    private JGDataSet dataSet;

    public JGUploadPic(JGDataSet dataSet) {
        this.dataSet = dataSet;
    }

    public void run() {
        uploadFile();
    }

    private void uploadFile()
    {
        String uploadFile = dataSet.getPicPath();    //���ϴ����ļ�·��
    /*	String postUrl = "http://172.29.247.1/index.php"; //����POST�����ҳ��*/
        String postUrl = "http://101.200.189.127:8001/kaoqintupianbaocun/"; //����POST�����ҳ��

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
          /* ������������ */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

          /*����StrictMode ����HTTPURLConnection����ʧ�ܣ���Ϊ�������������н�����������*/
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* ����DataOutputStream��getOutputStream��Ĭ�ϵ���connect()*/
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file\";filename=\"" +
                    dataSet.getPicName() + "\"" +
                    end);
            ds.writeBytes(end);
          /* ȡ���ļ���FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
          /* ����ÿ��д��8192bytes */
            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];   //8k
            int length = -1;
          /* ���ļ���ȡ������������ */
            while ((length = fStream.read(buffer)) != -1)
            {
            /* ������д��DataOutputStream�� */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* �ر�����д��Ķ����Զ�����Http����*/
            fStream.close();
          /* �ر�DataOutputStream */
            ds.close();
          /* �ӷ��ص���������ȡ��Ӧ��Ϣ */
            InputStream is = con.getInputStream();  //input from the connection ��ʽ����HTTP����
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }
          /* ��ʾ��ҳ��Ӧ���� */
            //Toast.makeText(JGMainActivity.this, b.toString().trim(), Toast.LENGTH_SHORT).show();//Post�ɹ�
            Log.e("HTTP send success", b.toString());
        } catch (Exception e)
        {
            Log.e("HTTP send failed\n", e.toString());
        }
    }
}
