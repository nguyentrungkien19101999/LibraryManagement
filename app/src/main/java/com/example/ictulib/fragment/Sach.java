package com.example.ictulib.fragment;

public class Sach {
   private String id;
   private String name;
   private String soluong;

   public Sach(String id, String name, String soluong) {
      this.id = id;
      this.name = name;
      this.soluong = soluong;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSoluong() {
      return soluong;
   }

   public void setSoluong(String soluong) {
      this.soluong = soluong;
   }
}
