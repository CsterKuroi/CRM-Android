package com.kuroi.contract.service;

import android.content.Context;

import com.kuroi.contract.dataaccess.ConDBOperation;
import com.kuroi.contract.model.Contract;

import java.util.List;

public class ConService {
    private ConDBOperation dao = null;
    public ConService(Context context) {
        dao = new ConDBOperation(context);
    }
    public boolean save(Contract contract) {
        boolean flag = dao.save(contract);
        return flag;
    }
    public List getByName(String queryName, int sort) {
        List list = dao.getByName(queryName, sort);
        return list;
    }
    public Contract getById(int id) {
        Contract contract = dao.getById(id);
        return contract;
    }
    public boolean update(Contract contract) {
        boolean flag = dao.update(contract);
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
