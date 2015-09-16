package com.kuroi.chance.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuroi.chance.model.Chance;

import java.util.ArrayList;
import java.util.List;

public class ChanceDBOperation {
	private ChanceDBChance database = null;
	public ChanceDBOperation(Context context){
		database = new ChanceDBChance(context);
	}
	public boolean save(Chance chance){
		SQLiteDatabase db = database.getWritableDatabase();
		if(chance != null){
			ContentValues value = new ContentValues();
			value.put("_id", chance.getId());
			value.put("number", chance.getNumber());
			value.put("name", chance.getName());
			value.put("type", chance.getType());
			value.put("customer", chance.getCustomer());
			value.put("date", chance.getDate());
			value.put("dateStart", chance.getDateStart());
			value.put("dateEnd", chance.getDateEnd());
			value.put("money", chance.getMoney());
			value.put("discount", chance.getDiscount());
			value.put("principal", chance.getPrincipal());
			value.put("ourSigner", chance.getOurSigner());
			value.put("cusSigner", chance.getCusSigner());
			value.put("remark", chance.getRemark());
			value.put("img", chance.getImg());
			db.insertOrThrow("chance", null, value);
			db.close();
			return true;
		}
		else{
			return false;
		}
	}
	public List getByName(String queryName,int sort){
		if(queryName == null || queryName.equals("")){
			return getAll(sort);
		}	
		List list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql="select * from chance where name like ? or _id like ?";
		switch (sort) {
			case 0:
				sql = "select * from chance where name like ? or _id like ?";
				break;
			case 1:
				sql = "select * from chance where name like ? or _id like ? order by date asc";
				break;
			case 2:
				sql = "select * from chance where name like ? or _id like ? order by cast(money as decimal(9,2)) asc";
		}
		String[] params = new String[]{"%"+queryName+"%", "%"+queryName+"%"};
		Cursor cursor = db.rawQuery(sql, params);
		list = new ArrayList();
		while(cursor.moveToNext()){
			Chance chance = new Chance();
			chance.setId(cursor.getInt(0));
			chance.setNumber(cursor.getString(1));
			chance.setName(cursor.getString(2));
			chance.setType(cursor.getString(3));
			chance.setCustomer(cursor.getString(4));
			chance.setDate(cursor.getString(5));
			chance.setDateStart(cursor.getString(6));
			chance.setDateEnd(cursor.getString(7));
			chance.setMoney(cursor.getString(8));
			chance.setDiscount(cursor.getString(9));
			chance.setPrincipal(cursor.getString(10));
			chance.setOurSigner(cursor.getString(11));
			chance.setCusSigner(cursor.getString(12));
			chance.setRemark(cursor.getString(13));
			chance.setImg(cursor.getString(14));
			list.add(chance);
		}
		cursor.close();
		db.close();
		return list;
	}
	public List getAll(int sort){
		List list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql="select * from chance";
		switch (sort) {
			case 0:
				sql = "select * from chance";
				break;
			case 1:
				sql = "select * from chance order by date asc";
				break;
			case 2:
				sql = "select * from chance order by cast(money as decimal(9,2)) asc";
		}
		Cursor cursor = db.rawQuery(sql, null);
		
		list = new ArrayList();
		while(cursor.moveToNext()){
			Chance chance = new Chance();
			chance.setId(cursor.getInt(0));
			chance.setNumber(cursor.getString(1));
			chance.setName(cursor.getString(2));
			chance.setType(cursor.getString(3));
			chance.setCustomer(cursor.getString(4));
			chance.setDate(cursor.getString(5));
			chance.setDateStart(cursor.getString(6));
			chance.setDateEnd(cursor.getString(7));
			chance.setMoney(cursor.getString(8));
			chance.setDiscount(cursor.getString(9));
			chance.setPrincipal(cursor.getString(10));
			chance.setOurSigner(cursor.getString(11));
			chance.setCusSigner(cursor.getString(12));
			chance.setRemark(cursor.getString(13));
			chance.setImg(cursor.getString(14));
			list.add(chance);
		}
		cursor.close();
		db.close();
		return list;
	}
	public Chance getById(int id){
		Chance chance = null;
		if(id > 0){
			SQLiteDatabase db = database.getReadableDatabase();
			String sql = "select * from chance where _id=?";
			String[] params = new String[] {String.valueOf(id)};
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToNext()){
				chance = new Chance();
				chance.setId(cursor.getInt(0));
				chance.setNumber(cursor.getString(1));
				chance.setName(cursor.getString(2));
				chance.setType(cursor.getString(3));
				chance.setCustomer(cursor.getString(4));
				chance.setDate(cursor.getString(5));
				chance.setDateStart(cursor.getString(6));
				chance.setDateEnd(cursor.getString(7));
				chance.setMoney(cursor.getString(8));
				chance.setDiscount(cursor.getString(9));
				chance.setPrincipal(cursor.getString(10));
				chance.setOurSigner(cursor.getString(11));
				chance.setCusSigner(cursor.getString(12));
				chance.setRemark(cursor.getString(13));
				chance.setImg(cursor.getString(14));
			}
			cursor.close();
			db.close();
		}
		return chance;
	}
	public boolean update(Chance chance){
		if(chance != null){
			SQLiteDatabase db = database.getWritableDatabase();
//			String sql = "update chance set number = ?, name = ?, " +
//			             "phone = ?, customer = ?, address = ?, date = ?, " +
//					     "relationship = ?, remark = ? where _id = ?";
//			Object[] params = new Object[]{chance.getNumber(),chance.getName(),chance.getPhone(),
//					                       chance.getCustomer(),chance.getAddress(),chance.getdate(),
//					                       chance.getRelationship(),chance.getRemark(),chance.getId()};
//			db.execSQL(sql, params);
			ContentValues value = new ContentValues();
			value.put("number", chance.getNumber());
			value.put("name", chance.getName());
			value.put("type", chance.getType());
			value.put("customer", chance.getCustomer());
			value.put("date", chance.getDate());
			value.put("dateStart", chance.getDateStart());
			value.put("dateEnd", chance.getDateEnd());
			value.put("money", chance.getMoney());
			value.put("discount", chance.getDiscount());
			value.put("principal", chance.getPrincipal());
			value.put("ourSigner", chance.getOurSigner());
			value.put("cusSigner", chance.getCusSigner());
			value.put("remark", chance.getRemark());
			value.put("img", chance.getImg());
			db.update("chance", value, "_id=?", new String[]{String.valueOf(chance.getId())});
			db.close();
			return true;
		}
		else{
			return false;
		}
	}
	public void delete(int id){
		if(id > 0){
			SQLiteDatabase db = database.getWritableDatabase();
			String sql = "delete from chance where _id = ?";
			Object[] params = new Object[]{String.valueOf(id)};
			db.execSQL(sql, params);
			db.close();
		}
	}

	public void delete(){
		SQLiteDatabase db = database.getWritableDatabase();
		String sql = "delete from chance";
		db.execSQL(sql);
		db.close();
	}

	public Long getCount() {
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*)from chance",null);
		cursor.moveToFirst();
		Long count = cursor.getLong(0);
		db.close();
		return count;
	}
	public int getMax() {
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ifnull(max(_id),0) from chance",null);
		cursor.moveToFirst();
		int max=cursor.getInt(0);
		cursor.close();
		db.close();
		return max;
	}

}


