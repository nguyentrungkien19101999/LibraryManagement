package com.example.ictulib.fragment;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ictulib.BookHelper;
import com.example.ictulib.R;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BorrowFragment extends Fragment {
    BookHelper bookHelper;
    Adapter_ListSpinner_Borrow adapterListBook;

    private EditText masv, nameStudent, date, count;
    private Button save;
    private Spinner spinner_MaSach;
    private String id_sach;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow, container, false);

        masv = view.findViewById(R.id.idsinhvien);
        nameStudent = view.findViewById(R.id.name_studens);
        spinner_MaSach = view.findViewById(R.id.spinner);
        date = view.findViewById(R.id.date);
        save = view.findViewById(R.id.btnsave);
        count = view.findViewById(R.id.count);


        //create database
        bookHelper = new BookHelper(getActivity(), "books.db", null, 1);

        //Create table
        bookHelper.QueryData("CREATE TABLE IF NOT EXISTS MuonSach(Id integer primary key autoincrement," +
                " MaSV varchar(50)," +
                " MaSach varchar(50),"+
                " TenSV varchar(100)," +
                " SoLuong int," +
                " NgayMuon varchar(20)," +
                " NgayTra varchar(20)," +
                " CONSTRAINT k1 FOREIGN KEY (MaSach)REFERENCES Sach(MaSach))");

        bookHelper.QueryData(" CREATE TABLE IF NOT EXISTS Sach(MaSach varchar(50) primary key," +
                " TenSach varchar(100)," +
                " SoLuong int,"+
                " MaKe int," +
                " CONSTRAINT k2 FOREIGN KEY (MaKe)REFERENCES KeSach(MaKe))");

        bookHelper.QueryData(" CREATE TABLE IF NOT EXISTS KeSach(MaKe integer primary key autoincrement," +
                " TenKe varchar(100))");

        ArrayList<ListBooks> arrayMaSach = new ArrayList<ListBooks>();
        arrayMaSach.add(new ListBooks("-- Chọn mã sách",""));

        Cursor cursor = bookHelper.GetData("SELECT MaSach, TenSach FROM Sach");
        while (cursor.moveToNext()){
            String masach = cursor.getString(0);
            String tensach = cursor.getString(1);

            arrayMaSach.add(new ListBooks(masach,tensach));
        }


        adapterListBook = new Adapter_ListSpinner_Borrow(getActivity(), R.layout.fragment_borrow, arrayMaSach);
        spinner_MaSach.setAdapter(adapterListBook);

        spinner_MaSach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_sach = adapterListBook.getItem(i).getMaSach();
                 //Toast.makeText(getActivity(), id_sach+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook(id_sach);
            }
        });

        return view;
    }

    private void addBook(String id_sach) {
        String strMaSV = masv.getText().toString().trim();
        String strTenSV = nameStudent.getText().toString().trim();
        //int strsoluong = Integer.parseInt(String.valueOf(count));
        String strmuon = date.getText().toString().trim();
        Pattern pattern = Pattern.compile("\\d*");
        Matcher matcher = pattern.matcher(count.getText().toString());

        if (TextUtils.isEmpty(strMaSV)) {
            Toast.makeText(getActivity(), "Vui lòng nhập mã sinh viên!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strTenSV)) {
            Toast.makeText(getActivity(), "Vui lòng nhập tên sinh viên!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(count.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng!", Toast.LENGTH_LONG).show();
        } else if ((Integer.parseInt(String.valueOf(count.getText()))>5)) {
            Toast.makeText(getActivity(), "Mỗi sinh viên mượn tối đa 5 quyển sách!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strmuon)) {
            Toast.makeText(getActivity(), "Vui lòng thêm ngày mượn!", Toast.LENGTH_LONG).show();

        } else if (matcher.matches()) {
            try {
                bookHelper.QueryData("INSERT INTO MuonSach VALUES (null,'" +
                        strMaSV + "','" +
                        id_sach + "','" +
                        strTenSV+ "'," +
                        count.getText().toString().trim() + ",'" +
                        strmuon + "',null)");
                Toast.makeText(getActivity(), "Thêm dữ liệu người mượn thành công!", Toast.LENGTH_SHORT).show();

                int soluongmuon = Integer.parseInt(count.getText().toString().trim());
                Cursor cursor = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+id_sach+"'");
                int soluong = 0;
                while (cursor.moveToNext()){
                    soluong = cursor.getInt(0);
                }

                soluong = soluong - soluongmuon;
                //Toast.makeText(getActivity(), soluong+"", Toast.LENGTH_LONG).show();
                bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluong+" WHERE MaSach ='"+id_sach+"'");

                masv.setText(null);
                nameStudent.setText(null);
                date.setText(null);
                count.setText(null);
            }catch (Exception e){Toast.makeText(getActivity(), "Không tồn tại mã sách trong kho! Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();}

        } else {
            Toast.makeText(getActivity(), "Số lượng nhập sai định dạng! Vui lòng nhập lại!", Toast.LENGTH_LONG).show();
        }
    }
}

    /*public void hideSotfkeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }*/


