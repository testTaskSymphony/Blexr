package com.blexr.entity;

import java.sql.Blob;

public class Image {

    private Integer id = null;
    private String md5 = null;
    private Blob file = null;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public Blob getFile() {
        return file;
    }
    public void setFile(Blob file) {
        this.file = file;
    }
    
}
