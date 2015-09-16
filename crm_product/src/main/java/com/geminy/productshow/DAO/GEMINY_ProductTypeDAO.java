package com.geminy.productshow.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.geminy.productshow.Model.GEMINY_Tb_productType;

/**
 * Created by Hatsune Miku on 2015/8/17.
 */
public class GEMINY_ProductTypeDAO {
    private GEMINY_DBOpenHelper helper;
    private SQLiteDatabase db;

    public GEMINY_ProductTypeDAO(Context context){
        helper=new GEMINY_DBOpenHelper(context);
    }
    public void add(GEMINY_Tb_productType tb_productType){
        db=helper.getWritableDatabase();
        db.execSQL("insert into tb_productType (id,typeName,subTypeName) values (?,?,?)",new Object[]{
                tb_productType.getId(),
                tb_productType.getTypeName(),
                tb_productType.getSubTypeName()
        });
    }
    public void update(GEMINY_Tb_productType tb_productType){
        db=helper.getWritableDatabase();
        db.execSQL("update tb_productType set typeName=?,subTypeName=? where id=?",new Object[]{
                tb_productType.getTypeName(),
                tb_productType.getSubTypeName(),
                tb_productType.getId()
        });
    }
    public void deleteByTypeName(String typeName){
        db=helper.getWritableDatabase();
        db.execSQL("delete from tb_productType where typeName in"+"(?,?,?)",new String[]{typeName});
    }
}
