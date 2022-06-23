package com.example.ictulib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ictulib.R;
import com.example.ictulib.model.Book;
import com.example.ictulib.model.ListBooks;

import java.util.List;

public class Adapter_ListSpinner_Borrow extends ArrayAdapter<Book> {

    public Adapter_ListSpinner_Borrow(@NonNull Context context, int resource, @NonNull List<Book> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected, parent,false);
        TextView tv_selected = convertView.findViewById(R.id.tv_selected);

        Book listBooks = this.getItem(position);
        if (listBooks != null){
            tv_selected.setText(listBooks.getId()+" -- "+listBooks.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listbook, parent,false);
        TextView textView = convertView.findViewById(R.id.tv_category);

        Book listBooks = this.getItem(position);
        if (listBooks != null){
            textView.setText(listBooks.getId()+" -- "+listBooks.getName());
        }

        return convertView;
    }
}
