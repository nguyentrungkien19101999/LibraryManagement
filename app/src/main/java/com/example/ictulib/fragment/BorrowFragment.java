package com.example.ictulib.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.ictulib.MyApplication;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_ListSpinner_Borrow;
import com.example.ictulib.adapter.Adapter_Storage;
import com.example.ictulib.constant.GlobalFuntion;
import com.example.ictulib.model.Book;
import com.example.ictulib.model.ListBooks;
import com.example.ictulib.model.Storage;
import com.example.ictulib.model.TagBorrow;
import com.example.ictulib.view.ListBookActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BorrowFragment extends Fragment {
    private Adapter_ListSpinner_Borrow adapterListBook;
    private EditText masv, nameStudent, date, count;
    private Button save;
    private Spinner spinner_MaSach;
    private String id_sach, name_sach, id_Ke;
    private int soLuong;

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

        ArrayList<Book> arrayMaSach = new ArrayList<>();
        arrayMaSach.add(new Book("-- Chọn mã sách","",0,""));

        MyApplication.get(getActivity()).getBooks().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || arrayMaSach == null) {
                    return;
                }
                arrayMaSach.add(new Book(book.getId(), book.getName(), book.getSoluong(), ""));
                adapterListBook.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || arrayMaSach == null) {
                    return;
                }
                for (int i = 0; i < arrayMaSach.size(); i++) {
                    if (book.getId().equalsIgnoreCase(arrayMaSach.get(i).getId())) {
                        arrayMaSach.set(i, book);
                        break;
                    }
                }
                adapterListBook.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || arrayMaSach == null) {
                    return;
                }
                for (Book roomDelete : arrayMaSach) {
                    if (book.getId().equalsIgnoreCase(roomDelete.getId())) {
                        arrayMaSach.remove(roomDelete);
                        break;
                    }
                }
                adapterListBook.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterListBook = new Adapter_ListSpinner_Borrow(getActivity(), R.layout.fragment_borrow, arrayMaSach);
        spinner_MaSach.setAdapter(adapterListBook);

        spinner_MaSach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_sach = adapterListBook.getItem(i).getId();
                soLuong = adapterListBook.getItem(i).getSoluong();
                id_Ke = adapterListBook.getItem(i).getIDBookShelf();
                name_sach = adapterListBook.getItem(i).getName();
                 Toast.makeText(getActivity(), id_sach+" "+soLuong+" ++ "+id_Ke+" ++ "+name_sach+" ", Toast.LENGTH_LONG).show();
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
                TagBorrow tagBorrow = new TagBorrow();
                long categoryId = GlobalFuntion.getId();
                tagBorrow.setMaSV(strMaSV);
                tagBorrow.setId(String.valueOf(categoryId));
                tagBorrow.setTenSV(strTenSV);
                tagBorrow.setMaSach(id_sach);
                tagBorrow.setNgayMuon(strmuon);
                tagBorrow.setNgayTra("null");
                tagBorrow.setSoLuong(Integer.parseInt(count.getText().toString()));

                int soLuongInput = Integer.parseInt(count.getText().toString());

                if (soLuongInput > soLuong){
                    Toast.makeText(getActivity(),
                            "Kho chỉ còn "+ soLuong + " quyển!", Toast.LENGTH_SHORT).show();
                } else if(soLuongInput <= soLuong){
                    soLuong = soLuong - soLuongInput;
                    MyApplication.get(getActivity()).getTagBorrow().child(String.valueOf(categoryId)).setValue(tagBorrow, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(),
                                    "Thêm thẻ mượn thành công!", Toast.LENGTH_SHORT).show();
                            GlobalFuntion.hideSoftKeyboard(getActivity());
                            if (error != null) {
                                Toast.makeText(getActivity(),
                                        "Thêm thẻ mượn thất bại!", Toast.LENGTH_SHORT).show();
                                Log.d("AddTagBorrow", "++++ " + error);
                            }
                        }
                    });

                    changeSoluong(soLuong);

                    masv.setText(null);
                    nameStudent.setText(null);
                    date.setText(null);
                    count.setText(null);
                }
            }catch (Exception e){Toast.makeText(getActivity(), "Không tồn tại mã sách trong kho! Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();}
        } else {
            Toast.makeText(getActivity(), "Số lượng nhập sai định dạng! Vui lòng nhập lại!", Toast.LENGTH_LONG).show();
        }
    }

    private void changeSoluong(int soluong){
        Book book = new Book();
        book.setId(id_sach);
        book.setName(name_sach);
        book.setIDBookShelf(id_Ke);
        book.setSoluong(soluong);
        MyApplication.get(getActivity()).getBooks().child(id_sach).setValue(book, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d("UpDateSoLuong", "Update Successfull");
            }
        });
    }
}