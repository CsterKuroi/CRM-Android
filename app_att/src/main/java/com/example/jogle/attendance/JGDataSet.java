package com.example.jogle.attendance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jogle on 15/7/24.
 */
public class JGDataSet {
    private int type;
    /* 0 -> 外勤; 1 -> 考勤签到; 2 -> 考勤签退 */
    private int userID;
    private String userName;
    private String time;
    /*Format: YYYY年MM月DD日 hh:mm:ss*/
    private String position;
    private String content;
    private String timeStamp;
    private String customers;
    /*Picture Name: IMG_userID_timeStamp.jpg*/
    /*Thumbnail Name: Thumbnail_userID_timeStamp.jpg*/
    private double latitude;
    private double longitude;

    public JGDataSet() {
        type = 0;
        userID = -1;
        userName ="";
        time = null;
        position = null;
        content = "";
        timeStamp = null;
        customers = null;
    }

    public static Bitmap getPicBitMap(String picPath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        FileInputStream is = null;
        try {
            is = new FileInputStream(picPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
        double rate1 = bmp.getWidth() / 2000.0;
        double rate2 = bmp.getHeight() / 2000.0;
        if (bmp.getWidth() >= 2000 || bmp.getHeight() >= 2000) {
            double rate = (rate1 > rate2) ? rate1: rate2;
            // extract picture size that can fill the screen to avoid OutOfMemoryException
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bmp,
                    (int) (bmp.getWidth() / rate), (int) (bmp.getHeight() / rate), 0);
            return thumbnailBitmap;
        }
        return bmp;
    }

    public Bitmap getPicBitMap() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        FileInputStream is = null;
        try {
            is = new FileInputStream(getPicPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
        double rate1 = bmp.getWidth() / 2000.0;
        double rate2 = bmp.getHeight() / 2000.0;
        if (bmp.getWidth() >= 2000 || bmp.getHeight() >= 2000) {
            double rate = (rate1 > rate2) ? rate1: rate2;
            // extract picture size that can fill the screen to avoid OutOfMemoryException
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bmp,
                    (int) (bmp.getWidth() / rate), (int) (bmp.getHeight() / rate), 0);
            return thumbnailBitmap;
        }
        return bmp;
    }

    public String getPicName() {
        return "IMG_" + userID + "_" + timeStamp + ".jpg";
    }

    public String getPicPath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Attendance");
        String path = mediaStorageDir.getAbsolutePath() + File.separator +
                "IMG_" + userID + "_" + timeStamp + ".jpg";
        return path;
    }

    public String getThumbnailPath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Attendance");
        String path = mediaStorageDir.getAbsolutePath() + File.separator +
                "thumbnail_" + userID + "_" + timeStamp + ".jpg";
        return path;
    }

    public Bitmap getThumbnail() {
        FileInputStream is = null;
        try {
            is = new FileInputStream(getThumbnailPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(is);
        return bmp;
    }

    public void generateThumbnail() {
        Bitmap bmp = getPicBitMap();
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmp, 100, 100, 0);
        bmp.recycle();
        File thumbnailFile = new File(getThumbnailPath());
        if (thumbnailFile.exists())
            thumbnailFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasSameDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String timeString = sdf.format(calendar.getTime());
        if (time.split(" ")[0].equals(timeString))
            return true;
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCustomers() {
        return customers;
    }

    public void setCustomers(String customers) {
        this.customers = customers;
    }
}
