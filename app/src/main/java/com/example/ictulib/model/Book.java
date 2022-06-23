package com.example.ictulib.model;

public class Book {
   private String id;
   private String name;
   private int soluong;
   private String idbookShelf;

   public Book() {
   }

   public Book(String id, String name, int soluong, String idbookShelf) {
      this.id = id;
      this.name = name;
      this.soluong = soluong;
      this.idbookShelf = idbookShelf;
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

   public int getSoluong() {
      return soluong;
   }

   public void setSoluong(int soluong) {
      this.soluong = soluong;
   }

   public String getIDBookShelf() {
      return idbookShelf;
   }

   public void setIDBookShelf(String idbookShelf) {
      this.idbookShelf = idbookShelf;
   }
}
