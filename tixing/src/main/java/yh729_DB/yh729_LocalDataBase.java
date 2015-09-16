package yh729_DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ricky.database.CenterDatabase;

/**
 * Created by yan on 2015/8/21.
 */
public class yh729_LocalDataBase {
    private yh729_mSQLiteOpenHelper mSQLiteOpenHelper;
    private SQLiteDatabase db;
    public yh729_LocalDataBase(Context context, SQLiteDatabase.CursorFactory factory){
        CenterDatabase cdb=new CenterDatabase(context,null);
        String id=cdb.getUID();
        mSQLiteOpenHelper=new yh729_mSQLiteOpenHelper(context,"db_"+id,factory,1);
        db=mSQLiteOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDataBase(){
        return mSQLiteOpenHelper.getWritableDatabase();
    }

    public String setRemind(String time, String content, String type, int way, int repeat) {
        yh729_DBUtil.insert(db, "alarm_record", new String[]{"date_time", "content", "type", "way", "repeat"}, new String[]{yh729_DBUtil.String2Date(time).getTime() + "", content, type, way + "", repeat + ""});
        Cursor c = db.rawQuery("select * from alarm_record where date_time='" + yh729_DBUtil.String2Date(time).getTime() + "' and type='" + type + "'", null);
        c.moveToLast();
        String id = c.getString(c.getColumnIndex("_id"));
        c.close();
        return id;
    }

    public void cancelRemind(String id) {
        db.execSQL("delete from alarm_record where _id='" + id + "'");
    }
}
