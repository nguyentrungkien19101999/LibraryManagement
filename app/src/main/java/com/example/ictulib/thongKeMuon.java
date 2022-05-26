package com.example.ictulib;

public class thongKeMuon {
    private String thang;
    private int count;

    public thongKeMuon(String thang, int count) {
        this.thang = thang;
        this.count = count;
    }

    public String getThang() {
        return thang;
    }

    public void setThang(String thang) {
        this.thang = thang;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
