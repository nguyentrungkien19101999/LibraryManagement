package com.example.ictulib.fragment;

public class ListBooks {
    private String maSach;
    private String tenSach;

    public ListBooks(String maSach, String tenSach) {
        this.maSach = maSach;
        this.tenSach = tenSach;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
}
