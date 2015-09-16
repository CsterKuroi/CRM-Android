package com.melnykov.fab.sample.tools;

/**
 * Created by dell on 2015/8/28.
 */
public class sortName {


    private String data,name,createtime,updatetime;
    public sortName() {
    }

    public sortName(String data,String name, String createtime,String updatetime) {
        this.data = data;
        this.name = name;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }



}
