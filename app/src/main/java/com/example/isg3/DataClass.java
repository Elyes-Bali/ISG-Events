package com.example.isg3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataClass {
    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    private List<String> postuledBy;
    private String key;
    private Date dataDate;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }
    public Date getDataDate() {
        return dataDate;
    }
    public List<String> getPostuledBy() {
        return postuledBy;
    }

    public void setPostuledBy(List<String> postuledBy) {
        this.postuledBy = postuledBy;
    }




    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }
    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, List<String> postuledBy) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.postuledBy = postuledBy;
    }
    public DataClass(){
    }
}
