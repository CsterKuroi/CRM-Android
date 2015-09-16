package com.kuroi.chance.model;

public class Chance {
    private int id;
    private String number = null;
    private String name = null;
    private String type = null;
    private String customer = null;
    private String date = null;
    private String dateStart = null;
    private String dateEnd = null;
    private String money = null;
    private String discount = null;
    private String principal = null;
    private String ourSigner = null;
    private String cusSigner = null;
    private String remark = null;
    private String img = null;

    public Chance() {
        id = 0;
        number = "";
        name = "";
        type = "";
        customer = "";
        date = "";
        dateStart = "";
        dateEnd = "";
        money = "";
        discount = "";
        principal = "";
        ourSigner = "";
        cusSigner = "";
        remark = "";
        img = "";
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getNumber() {
        return number;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public String getCustomer() {
        return customer;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDateStart() {
        return dateStart;
    }
    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }
    public String getDateEnd() {
        return dateEnd;
    }
    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
    public String getMoney() {
        return money;
    }
    public void setMoney(String money) {
        this.money = money;
    }
    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }
    public String getPrincipal() {
        return principal;
    }
    public void setPrincipal(String principal) {
        this.principal = principal;
    }
    public String getOurSigner() {
        return ourSigner;
    }
    public void setOurSigner(String ourSigner) {
        this.ourSigner = ourSigner;
    }
    public String getCusSigner() {
        return cusSigner;
    }
    public void setCusSigner(String cusSigner) {
        this.cusSigner = cusSigner;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
}
