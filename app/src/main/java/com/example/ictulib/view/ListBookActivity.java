package com.example.ictulib.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ictulib.BookHelper;
import com.example.ictulib.MyApplication;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_Sach;
import com.example.ictulib.model.Book;
import com.example.ictulib.model.Storage;
import com.example.ictulib.my_interface.IClickitemSach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ListBookActivity extends AppCompatActivity {
    private List<Book> mListBook;
    private String id_Ke, name_Ke;
    private int soluong_sach;
    private RecyclerView rcvSach;
    private Adapter_Sach mBookAdapter;
    private FloatingActionButton btnFloating;
    private TextView tvEmptyList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);
        rcvSach = findViewById(R.id.rcv_sach);
        btnFloating = findViewById(R.id.fab2);
        tvEmptyList = findViewById(R.id.tvListEmpty);

        mListBook = new ArrayList<>();

        mBookAdapter = new Adapter_Sach(getApplication(), mListBook, new IClickitemSach() {
            @Override
            public void onClickItemSach(Book sach) {
                DialogSuaXoa(sach.getId(), sach.getName(), sach.getSoluong());
                //Toast.makeText(getApplication(), "Click item Sach...", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        id_Ke = (String) bundle.get("id_kesach");
        name_Ke = (String) bundle.get("name_kesach");
        soluong_sach = (int) bundle.get("soluong_sach");

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(String.valueOf(name_Ke));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvSach.setLayoutManager(linearLayoutManager);

        kiemTraRong();
        select();

        //Click btnFloating
        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog();
                //Toast.makeText(getApplication(), id+"", Toast.LENGTH_SHORT).show();
            }
        });

        rcvSach.setAdapter(mBookAdapter);
    }

    //Dialog Them Sach
    private void Dialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_themsach);

        dialog.setCancelable(false);

        EditText edtMaSach = dialog.findViewById(R.id.edt_masach);
        EditText edtTenSach = dialog.findViewById(R.id.edt_tensach);
        EditText edtSoLuong = dialog.findViewById(R.id.edt_soluong);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnThem = dialog.findViewById(R.id.btn_them);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maSach = edtMaSach.getText().toString().trim();
                String tenSach = edtTenSach.getText().toString().trim();
                int soLuong = Integer.parseInt(edtSoLuong.getText().toString().trim());

                if (TextUtils.isEmpty(maSach)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập mã sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tenSach)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập tên sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtSoLuong.getText().toString().trim())) {
                    Toast.makeText(getApplication(), "Vui lòng nhập số lượng sách!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Book book = new Book();
                        book.setId(maSach);
                        book.setName(tenSach);
                        book.setSoluong(soLuong);
                        book.setIDBookShelf(id_Ke);

                        MyApplication.get(getApplication()).getBooks().child(maSach).setValue(book, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                soluong_sach = soluong_sach + soLuong;
                                Toast.makeText(getApplication(),
                                        "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                                if (error != null) {
                                    Toast.makeText(getApplication(),
                                            "Thêm sách thất bại!", Toast.LENGTH_SHORT).show();
                                    Log.d("AddBook", "++++ " + error);
                                }
                            }
                        });

                        int soluong = soluong_sach + soLuong;
                        changeSoluong(soluong);

                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Sách đã tồn tại hoặc sai định dạng số lượng sách! Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Select Sach
    private void select() {
        if (getApplication() == null) {
            return;
        }

        MyApplication.get(getApplication()).getBooks().orderByChild("idbookShelf").equalTo(id_Ke).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || mListBook == null || mBookAdapter == null) {
                    return;
                }
                mListBook.add(0, book);
                mBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || mListBook == null || mBookAdapter == null) {
                    return;
                }
                for (int i = 0; i < mListBook.size(); i++) {
                    if (book.getId().equalsIgnoreCase(mListBook.get(i).getId())) {
                        mListBook.set(i, book);
                        break;
                    }
                }
                mBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                if (book == null || mListBook == null || mBookAdapter == null) {
                    return;
                }
                for (Book roomDelete : mListBook) {
                    if (book.getId().equalsIgnoreCase(roomDelete.getId())) {
                        mListBook.remove(roomDelete);
                        break;
                    }
                }
                mBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplication(), "R.string.msg_error_not_connect_to_internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Kiem tra KeSach Rỗng
    private void kiemTraRong() {
        if (getApplication() != null){
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    //Dialog Sửa, xóa sách
    private void DialogSuaXoa(String idBook, String name, int soluong) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_xoa_sach);

        TextView tvNameBook = dialog.findViewById(R.id.tv_namesach);
        Button btnXoa = dialog.findViewById(R.id.btn_xoa);
        Button btnSua = dialog.findViewById(R.id.btn_sua);

        tvNameBook.setText(name);

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder delete = new AlertDialog.Builder(ListBookActivity.this);
                delete.setTitle("Xóa sách!");
                delete.setMessage("Bạn muốn xóa sách đã chọn?");
                delete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyApplication.get(getApplication()).getBooks().child(idBook).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getApplication(), "Xóa sách thành công!", Toast.LENGTH_LONG).show();

                                soluong_sach = soluong_sach - soluong;
                                changeSoluong(soluong_sach);
                            }
                        });
                        dialog.dismiss();
                    }
                });
                delete.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                delete.show();
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSuaSach(idBook, name, soluong);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DialogSuaSach(String idBook, String tensach, int soluong) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_sach);

        dialog.setCancelable(false);

        EditText edt_tensach = dialog.findViewById(R.id.edt_tensach);
        EditText edt_masach = dialog.findViewById(R.id.edt_masach);
        EditText edt_soluong = dialog.findViewById(R.id.edt_soluong);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnThem = dialog.findViewById(R.id.btn_them);

        edt_masach.setText(idBook);
        edt_tensach.setText(tensach);
        edt_soluong.setText(String.valueOf(soluong));

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tensach1 = edt_tensach.getText().toString().trim();
                int soluong1 = Integer.parseInt(edt_soluong.getText().toString().trim());

                if (TextUtils.isEmpty(tensach1)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập tên sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edt_soluong.getText().toString().trim())) {
                    Toast.makeText(getApplication(), "Vui lòng nhập số lượng sách!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Book book = new Book();
                        book.setId(idBook);
                        book.setName(tensach1);
                        book.setSoluong(soluong1);
                        book.setIDBookShelf(id_Ke);

                        MyApplication.get(getApplication()).getBooks().child(idBook).setValue(book, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getApplication(),
                                        "Sửa thông tin kệ sách thành công!", Toast.LENGTH_LONG).show();
                            }
                        });

                        if (soluong1 > soluong){
                            soluong_sach = soluong_sach + (soluong1 - soluong);
                            changeSoluong(soluong_sach);
                        } else if (soluong1 < soluong){
                            soluong_sach = soluong_sach - (soluong - soluong1);
                            changeSoluong(soluong_sach);
                        }

                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Sách đã tồn tại hoặc số lượng nhập sai định dạng! Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void changeSoluong(int soluong){
        Storage storage = new Storage();
        storage.setId(id_Ke);
        storage.setName(name_Ke);
        storage.setSoluong(soluong);
        MyApplication.get(getApplication()).getBookShelf().child(id_Ke).setValue(storage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d("UpDateSoLuong", "Update Successfull");
            }
        });
    }
}