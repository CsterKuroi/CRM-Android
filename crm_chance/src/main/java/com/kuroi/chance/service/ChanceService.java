package com.kuroi.chance.service;

import android.content.Context;

import com.kuroi.chance.dataaccess.ChanceDBOperation;
import com.kuroi.chance.model.Chance;

import java.util.List;

public class ChanceService {
    private ChanceDBOperation dao = null;
    public ChanceService(Context context) {
        dao = new ChanceDBOperation(context);
    }
    public boolean save(Chance chance) {
        boolean flag = dao.save(chance);
        return flag;
    }
    public List getByName(String queryName, int sort) {
        List list = dao.getByName(queryName, sort);
        return list;
    }
    public Chance getById(int id) {
        Chance chance = dao.getById(id);
        return chance;
    }
    public boolean update(Chance chance) {
        boolean flag = dao.update(chance);
        return flag;
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public void delete() {
        dao.delete();
    }
    public Long getCount() {
        return dao.getCount();
    }
    public int getMax() {
        return dao.getMax();
    }
}
