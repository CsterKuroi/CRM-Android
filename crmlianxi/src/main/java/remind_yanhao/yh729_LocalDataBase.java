package remind_yanhao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ricky.database.CenterDatabase;


/**
 * Created by yan on 2015/8/21.
 */
public class yh729_LocalDataBase {
    private yh729_mSQLiteOpenHelper mSQLiteOpenHelper;
    public yh729_LocalDataBase(Context context, SQLiteDatabase.CursorFactory factory){
        CenterDatabase cdb=new CenterDatabase(context,null);
        String id=cdb.getUID();
        mSQLiteOpenHelper=new yh729_mSQLiteOpenHelper(context,"db_"+id,factory,1);
    }
    public SQLiteDatabase getDataBase(){
        return mSQLiteOpenHelper.getWritableDatabase();
    }
}
