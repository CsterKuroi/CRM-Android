package com.code.bmj.groupnotifycation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ricky.database.CenterDatabase;

//import com.mogujie.tt.DB.CenterDatabase;

/**
 * Created by yan on 2015/8/21.
 */
public class N_LocalDataBase {
    private N_mSQLiteOpenHelper mSQLiteOpenHelper;
    public N_LocalDataBase(Context context, SQLiteDatabase.CursorFactory factory){
//
        CenterDatabase cd = new CenterDatabase(context, null);
        String creater_id = cd.getUID();
        cd.close();
        String id = creater_id;
        mSQLiteOpenHelper=new N_mSQLiteOpenHelper(context,"db_"+id,factory,1);
    }
    public SQLiteDatabase getDataBase(){
        return mSQLiteOpenHelper.getWritableDatabase();
    }
}
