package com.geminy.productshow.Model;

/**
 * Created by Hatsune Miku on 2015/7/30.
 */
public class GEMINY_Tb_product {
    private int _id;
    private String name;
    private String args;
    private String character;
    private String picture;
    private String type;
    private String detail;
    private int star;
    private String related;
    private int num;
    private String remark;
    private String tempRemark;
    private String time;

    public GEMINY_Tb_product(){
        this._id=0;
        this.name="";
        this.args="";
        this.character=";";
        this.picture=";";
        this.type="";
        this.star=0;
        this.related=";";
        this.num=0;
        this.remark="";
        this.tempRemark="";
        this.time="0";

    }
    public GEMINY_Tb_product(int _id, String name, String type, String args, String character, String detail, String picture, int star, String related,int num,String remark,String tempRemark,String time){
        super();
        this._id=_id;
        this.name=name;
        this.type=type;
        this.args=args;
        this.character=character;
        this.detail=detail;
        this.picture=picture;
        this.star=star;
        this.related=related;
        this.num=num;
        this.remark=remark;
        this.tempRemark=tempRemark;
        this.time=time;
    }

    public int getId(){
        return _id;
    }
    public void setId(int id){
        this._id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getArgs(){
        return args;
    }
    public void setArgs(String args){
        this.args=args;
    }
    public String getCharacter(){
        return character;
    }
    public void setCharacter(String character){
        this.character=character;
    }
    public String getPicture(){
        return picture;
    }
    public void setPicture(String picture){
        this.picture=this.picture+picture+";";
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail=detail;
    }
    public int getStar(){
        return star;
    }
    public void setStar(int star){
        this.star=star;
    }
    public String getRelated(){
        return related;
    }
    public void setRelated(String related){
        this.related=related;
    }
    public int getNum(){
        return num;
    }
    public void setNum(int num){
        this.num=num;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public String getTempRemark(){
        return tempRemark;
    }
    public void setTempRemark(String tempRemark){
        this.tempRemark=tempRemark;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time=time;
    }

}
