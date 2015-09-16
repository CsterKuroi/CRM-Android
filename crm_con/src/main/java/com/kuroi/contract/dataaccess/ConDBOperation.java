package com.kuroi.contract.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuroi.contract.model.Contract;

import java.util.ArrayList;
import java.util.List;

public class ConDBOperation {
	private ConDBContract database = null;
	public ConDBOperation(Context context){
		database = new ConDBContract(context);
	}
	public boolean save(Contract contract){
		SQLiteDatabase db = database.getWritableDatabase();
		if(contract != null){
			ContentValues value = new ContentValues();
			value.put("_id", contract.getId());
			value.put("number", contract.getNumber());
			value.put("name", contract.getName());
			value.put("type", contract.getType());
			value.put("customer", contract.getCustomer());
			value.put("date", contract.getDate());
			value.put("dateStart", contract.getDateStart());
			value.put("dateEnd", contract.getDateEnd());
			value.put("money", contract.getMoney());
			value.put("discount", contract.getDiscount());
			value.put("principal", contract.getPrincipal());
			value.put("ourSigner", contract.getOurSigner());
			value.put("cusSigner", contract.getCusSigner());
			value.put("remark", contract.getRemark());
			value.put("img", contract.getImg());
			db.insertOrThrow("contract", null, value);
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
		String sql="select * from contract where name like ? or number like ?";
		switch (sort) {
			case 0:
				sql = "select * from contract where name like ? or number like ?";
				break;
			case 1:
				sql = "select * from contract where name like ? or number like ? order by date asc";
				break;
			case 2:
				sql = "select * from contract where name like ? or number like ? order by cast(money as decimal(9,2)) asc";
		}
		String[] params = new String[]{"%"+queryName+"%", "%"+queryName+"%"};
		Cursor cursor = db.rawQuery(sql, params);
		list = new ArrayList();
		while(cursor.moveToNext()){
			Contract contract = new Contract();
			contract.setId(cursor.getInt(0));
			contract.setNumber(cursor.getString(1));
			contract.setName(cursor.getString(2));
			contract.setType(cursor.getString(3));
			contract.setCustomer(cursor.getString(4));
			contract.setDate(cursor.getString(5));
			contract.setDateStart(cursor.getString(6));
			contract.setDateEnd(cursor.getString(7));
			contract.setMoney(cursor.getString(8));
			contract.setDiscount(cursor.getString(9));
			contract.setPrincipal(cursor.getString(10));
			contract.setOurSigner(cursor.getString(11));
			contract.setCusSigner(cursor.getString(12));
			contract.setRemark(cursor.getString(13));
			contract.setImg(cursor.getString(14));
			list.add(contract);
		}
		cursor.close();
		db.close();
		return list;
	}
	public List getAll(int sort){
		List list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql="select * from contract";
		switch (sort) {
			case 0:
				sql = "select * from contract";
				break;
			case 1:
				sql = "select * from contract order by date asc";
				break;
			case 2:
				sql = "select * from contract order by cast(money as decimal(9,2)) asc";
		}
		Cursor cursor = db.rawQuery(sql, null);
		
		list = new ArrayList();
		while(cursor.moveToNext()){
			Contract contract = new Contract();
			contract.setId(cursor.getInt(0));
			contract.setNumber(cursor.getString(1));
			contract.setName(cursor.getString(2));
			contract.setType(cursor.getString(3));
			contract.setCustomer(cursor.getString(4));
			contract.setDate(cursor.getString(5));
			contract.setDateStart(cursor.getString(6));
			contract.setDateEnd(cursor.getString(7));
			contract.setMoney(cursor.getString(8));
			contract.setDiscount(cursor.getString(9));
			contract.setPrincipal(cursor.getString(10));
			contract.setOurSigner(cursor.getString(11));
			contract.setCusSigner(cursor.getString(12));
			contract.setRemark(cursor.getString(13));
			contract.setImg(cursor.getString(14));
			list.add(contract);
		}
		cursor.close();
		db.close();
		return list;
	}
	public Contract getById(int id){
		Contract contract = null;
		if(id > 0){
			SQLiteDatabase db = database.getReadableDatabase();
			String sql = "select * from contract where _id=?";
			String[] params = new String[] {String.valueOf(id)};
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToNext()){
				contract = new Contract();
				contract.setId(cursor.getInt(0));
				contract.setNumber(cursor.getString(1));
				contract.setName(cursor.getString(2));
				contract.setType(cursor.getString(3));
				contract.setCustomer(cursor.getString(4));
				contract.setDate(cursor.getString(5));
				contract.setDateStart(cursor.getString(6));
				contract.setDateEnd(cursor.getString(7));
				contract.setMoney(cursor.getString(8));
				contract.setDiscount(cursor.getString(9));
				contract.setPrincipal(cursor.getString(10));
				contract.setOurSigner(cursor.getString(11));
				contract.setCusSigner(cursor.getString(12));
				contract.setRemark(cursor.getString(13));
				contract.setImg(cursor.getString(14));
			}
			cursor.close();
			db.close();
		}
		return contract;
	}
	public boolean update(Contract contract){
		if(contract != null){
			SQLiteDatabase db = database.getWritableDatabase();
//			String sql = "update contract set number = ?, name = ?, " +
//			             "phone = ?, customer = ?, address = ?, date = ?, " +
//					     "relationship = ?, remark = ? where _id = ?";
//			Object[] params = new Object[]{contract.getNumber(),contract.getName(),contract.getPhone(),
//					                       contract.getCustomer(),contract.getAddress(),contract.getdate(),
//					                       contract.getRelationship(),contract.getRemark(),contract.getId()};
//			db.execSQL(sql, params);
			ContentValues value = new ContentValues();
			value.put("number", contract.getNumber());
			value.put("name", contract.getName());
			value.put("type", contract.getType());
			value.put("customer", contract.getCustomer());
			value.put("date", contract.getDate());
			value.put("dateStart", contract.getDateStart());
			value.put("dateEnd", contract.getDateEnd());
			value.put("money", contract.getMoney());
			value.put("discount", contract.getDiscount());
			value.put("principal", contract.getPrincipal());
			value.put("ourSigner", contract.getOurSigner());
			value.put("cusSigner", contract.getCusSigner());
			value.put("remark", contract.getRemark());
			value.put("img", contract.getImg());
			db.update("contract", value, "_id=?", new String[]{String.valueOf(contract.getId())});
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
			String sql = "delete from contract where _id = ?";
			Object[] params = new Object[]{String.valueOf(id)};
			db.execSQL(sql, params);
			db.close();
		}
	}
	public void delete(){
			SQLiteDatabase db = database.getWritableDatabase();
			String sql = "delete from contract";
			db.execSQL(sql);
			db.close();
	}
	public Long getCount() {
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*)from contract",null);
		cursor.moveToFirst();
		Long count = cursor.getLong(0);
		cursor.close();
		db.close();
		return count;
	}

	public int getMax() {
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ifnull(max(_id),0) from contract",null);
		cursor.moveToFirst();
		int max=cursor.getInt(0);
		cursor.close();
		db.close();
		return max;
	}
}


