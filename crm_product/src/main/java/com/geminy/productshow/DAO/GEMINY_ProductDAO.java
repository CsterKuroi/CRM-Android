package com.geminy.productshow.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geminy.productshow.Model.GEMINY_Tb_product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hatsune Miku on 2015/8/1.
 */
public class GEMINY_ProductDAO {
    private GEMINY_DBOpenHelper helper;
    private SQLiteDatabase db;

    public GEMINY_ProductDAO(Context context){
        helper=new GEMINY_DBOpenHelper(context);
    }

    public void add(GEMINY_Tb_product tb_product){
        db=helper.getWritableDatabase();
        db.execSQL("insert into tb_product (_id,name,type,args,character,detail,pic,star,related,num,remark,tempRemark,time) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{
                tb_product.getId(),
                tb_product.getName(),
                tb_product.getType(),
                tb_product.getArgs(),
                tb_product.getCharacter(),
                tb_product.getDetail(),
                tb_product.getPicture(),
                tb_product.getStar(),
                tb_product.getRelated(),
                tb_product.getNum(),
                tb_product.getRemark(),
                tb_product.getTempRemark(),
                tb_product.getTime()
        });
    }
    public void update(GEMINY_Tb_product tb_product){
        db=helper.getWritableDatabase();
        db.execSQL("update tb_product set name=?,type=?,args=?,character=?,detail=?,pic=?,star=?,related=?,num=?,remark=?,tempRemark=?,time=? where _id=?",new Object[]{
                tb_product.getName(),
                tb_product.getType(),
                tb_product.getArgs(),
                tb_product.getCharacter(),
                tb_product.getDetail(),
                tb_product.getPicture(),
                tb_product.getStar(),
                tb_product.getRelated(),
                tb_product.getNum(),
                tb_product.getRemark(),
                tb_product.getTempRemark(),
                tb_product.getTime(),
                tb_product.getId()
        });
    }
    public void deleteByName(String name){
        db=helper.getWritableDatabase();
        db.execSQL("delete from tb_product where name in "+"(?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{name});
    }
    public boolean isDataExist(){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from tb_product",null);
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if(count>0)
            return true;
        else
            return false;
    }

    public GEMINY_Tb_product getFirstData(){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product",null);
        if(cursor.moveToFirst()){
            return new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            );
        } else {
            return null;
        }
    }
    public GEMINY_Tb_product selectById(int id){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product where _id=?",new String[]{String.valueOf(id)});
        if (cursor.moveToNext()){
            return new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            );
        } else {
            return null;
        }
    }
    public GEMINY_Tb_product getByName(String name){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product where name=?",new String[]{name});
        if(cursor.moveToNext()){
            return new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            );
        } else {
            return null;
        }
    }

    public List<GEMINY_Tb_product> getByType(String type){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product where type=?",new String[]{type});
        List<GEMINY_Tb_product> productList=new ArrayList<GEMINY_Tb_product>();
        while (cursor.moveToNext()){
            productList.add(new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            ));
        }
        return productList;
    }

    public List<GEMINY_Tb_product> getByStar(int star){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product where star=?",new String[]{String.valueOf(star)});
        List<GEMINY_Tb_product> productList=new ArrayList<GEMINY_Tb_product>();
        while (cursor.moveToNext()){
            productList.add(new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            ));
        }
        return productList;
    }

    public List<GEMINY_Tb_product> selectByNameLike(String nameLike){
        db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tb_product where name like '%"+nameLike+"%'",null);
        List<GEMINY_Tb_product> productList=new ArrayList<GEMINY_Tb_product>();
        while (cursor.moveToNext()){
            productList.add(new GEMINY_Tb_product(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("args")),
                    cursor.getString(cursor.getColumnIndex("character")),
                    cursor.getString(cursor.getColumnIndex("detail")),
                    cursor.getString(cursor.getColumnIndex("pic")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("related")),
                    cursor.getInt(cursor.getColumnIndex("num")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("tempRemark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            ));
        }
        return productList;
    }
}
