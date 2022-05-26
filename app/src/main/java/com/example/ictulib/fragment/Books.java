package com.example.ictulib.fragment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class Books {
    private int id;
    private String MaSV;
    private String namestudent;
    private String maSach;
    private String SoLuong;

    public Books(int id, String MaSV, String namestudent, String maSach, String SoLuong) {
        this.id = id;
        this.MaSV = MaSV;
        this.namestudent = namestudent;
        this.maSach = maSach;
        this.SoLuong = SoLuong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaSV() {
        return MaSV;
    }

    public void setMaSV(String MaSV) { this.MaSV = MaSV;}

    public String getNamestudent() {
        return namestudent;
    }

    public void setNamestudent(String namestudent) {
        this.namestudent = namestudent;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(String soLuong) {
        SoLuong = soLuong;
    }
}
