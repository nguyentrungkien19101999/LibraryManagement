package com.example.ictulib.fragment;

public class Storage {
    private int id;
    private String soluong;
    private String name;

    public Storage(int id, String soluong, String name) {
        this.id = id;
        this.soluong = soluong;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSoluong() {
        return soluong;
    }

    public void setSoluong(String soluong) {
        this.soluong = soluong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
