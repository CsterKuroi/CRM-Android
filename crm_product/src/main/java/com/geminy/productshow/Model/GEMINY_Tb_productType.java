package com.geminy.productshow.Model;

/**
 * Created by Hatsune Miku on 2015/8/17.
 */
public class GEMINY_Tb_productType {
    private int id;
    private String typeName;
    private String subTypeName;

    public GEMINY_Tb_productType(){
        super();
    }


    public GEMINY_Tb_productType(int id,String typeName,String subTypeName){
        super();
        this.id=id;
        this.typeName=typeName;
        this.subTypeName=subTypeName;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getTypeName(){
        return typeName;
    }
    public void setTypeName(String typeName){
        this.typeName=typeName;
    }
    public String getSubTypeName(){
        return subTypeName;
    }
    public void setSubTypeName(String subTypeName){
        this.subTypeName=subTypeName;
    }

}
