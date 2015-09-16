package com.geminy.productshow.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hatsune Miku on 2015/8/1.
 */
public class GEMINY_DBOpenHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DBNAME = "product_show.db";

    public GEMINY_DBOpenHelper(Context context){
        super(context,DBNAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tb_product(_id integer primary key,name TEXT,type TEXT,args TEXT,character TEXT,detail TEXT,pic TEXT,star tinyinteger,related TEXT,num integer,remark TEXT,tempRemark TEXT,time TEXT)");
        //db.execSQL("create table tb_productType(id integer primary key,typeName TEXT,subTypeName TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
