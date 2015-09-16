package com.kuroi.contract.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConDBContract extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contract.db";
    private static final int DATABASE_VERSION = 1;
    private static String sql =
            "create table contract ("
                    + "_id integer primary key , "
                    + "number text, "
                    + "name text, "
                    + "type text, "
                    + "customer text, "
                    + "date text, "
                    + "dateStart text, "
                    + "dateEnd text, "
                    + "money text, "
                    + "discount text, "
                    + "principal text, "
                    + "ourSigner text, "
                    + "cusSigner text, "
                    + "remark text, "
                    + "img text"
                    + "img2 text"
                    + "img3 text)";

    public ConDBContract(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {db.execSQL(sql);}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
