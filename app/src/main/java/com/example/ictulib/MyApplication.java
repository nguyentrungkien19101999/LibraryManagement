package com.example.ictulib;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public DatabaseReference getBookShelf() {
        return FirebaseDatabase.getInstance().getReference("/bookshelf");
    }

    public DatabaseReference getBooks() {
        return FirebaseDatabase.getInstance().getReference("/books");
    }

    public DatabaseReference getTagBorrow() {
        return FirebaseDatabase.getInstance().getReference("/tagborrow");
    }
}
