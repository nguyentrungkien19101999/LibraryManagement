package com.example.ictulib.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ictulib.BookHelper;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_Sach;
import com.example.ictulib.model.Sach;
import com.example.ictulib.my_interface.IClickitemSach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListBookActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    BookHelper bookHelper;
    ArrayList<Sach> mangSach;
    private int id_Ke;

    private RecyclerView rcvSach;
    private Adapter_Sach madapterSach;
    private FloatingActionButton btnFloating;
    private SwipeRefreshLayout swipeRefreshLayout;


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
        swipeRefreshLayout = findViewById(R.id.srllayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);

        bookHelper = new BookHelper(this, "books.db", null, 1);
        madapterSach = new Adapter_Sach(mangSach, new IClickitemSach() {
            @Override
            public void onClickItemSach(Sach sach) {
                DialogSuaXoa(sach.getId(), sach.getName(), sach.getSoluong());
                //Toast.makeText(getApplication(), "Click item Sach...", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        id_Ke = (int) bundle.get("id_kesach");

        Cursor cursor1 = bookHelper.GetData("SELECT TenKe FROM KeSach WHERE MaKe =" + id_Ke);
        String TenKe = null;

        while (cursor1.moveToNext()) {
            TenKe = cursor1.getString(0);
        }

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(String.valueOf(TenKe));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvSach.setLayoutManager(linearLayoutManager);

        mangSach = new ArrayList<Sach>();

        kiemTraRong(id_Ke);
        select(id_Ke);

        //Click btnFloating
        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog(id_Ke);
                //Toast.makeText(getApplication(), id+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Dialog Them Sach
    private void Dialog(int id) {
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
                String soLuong = edtSoLuong.getText().toString().trim();

                if (TextUtils.isEmpty(maSach)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập mã sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tenSach)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập tên sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(soLuong)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập số lượng sách!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        bookHelper.QueryData("INSERT INTO Sach VALUES('" + maSach + "','" + tenSach + "'," + soLuong + "," + id + ")");
                        Toast.makeText(getApplication(), "Thêm sách thành công", Toast.LENGTH_SHORT).show();
                        select(id);

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
    private void select(int id) {
        mangSach.clear();
        Cursor cursor = bookHelper.GetData("SELECT MaSach, TenSach, SoLuong FROM Sach WHERE MaKe =" + id);
        while (cursor.moveToNext()) {
            String MaSach = cursor.getString(0);
            String TenSach = cursor.getString(1);
            String SoLuong = cursor.getString(2);

            mangSach.add(new Sach(MaSach, TenSach, SoLuong));
        }
        //Toast.makeText(this, mangSach+"", Toast.LENGTH_SHORT).show();
        madapterSach.setData(mangSach);
        rcvSach.setAdapter(madapterSach);
    }

    //Kiem tra KeSach Rỗng
    private void kiemTraRong(int id) {
        String TenKe2 = null;
        Cursor cursor2 = bookHelper.GetData("SELECT MaKe FROM Sach WHERE MaKe =" + id);
        while (cursor2.moveToNext()) {
            TenKe2 = cursor2.getString(0);
        }
        if (TenKe2 == null) {
            Toast.makeText(this, "Kệ sách trống.\nChọn dấu + bên cạnh để thêm sách vào kệ.", Toast.LENGTH_LONG).show();
        }
    }

    //Dialog Sửa, xóa sách
    private void DialogSuaXoa(String masach, String name, String soluong) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_xoa_sach);

        TextView tvNameKe = dialog.findViewById(R.id.tv_namesach);
        Button btnXoa = dialog.findViewById(R.id.btn_xoa);
        Button btnSua = dialog.findViewById(R.id.btn_sua);

        tvNameKe.setText(name);

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder delete = new AlertDialog.Builder(ListBookActivity.this);
                delete.setTitle("Xóa sách!");
                delete.setMessage("Bạn muốn xóa sách đã chọn?");
                delete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookHelper.QueryData("DELETE FROM Sach WHERE MaSach ='" + masach + "'");

                        Toast.makeText(getApplication(), "Xóa dữ liệu thành công!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        select(id_Ke);
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
                DialogSuaSach(masach, name, soluong);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DialogSuaSach(String masach, String tensach, String soluong) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_sach);

        dialog.setCancelable(false);

        EditText edt_tensach = dialog.findViewById(R.id.edt_tensach);
        EditText edt_masach = dialog.findViewById(R.id.edt_masach);
        EditText edt_soluong = dialog.findViewById(R.id.edt_soluong);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnThem = dialog.findViewById(R.id.btn_them);

        edt_masach.setText(masach);
        edt_tensach.setText(tensach);
        edt_soluong.setText(soluong);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String masach1 = edt_masach.getText().toString().trim();
                String tensach1 = edt_tensach.getText().toString().trim();
                String soluong1 = edt_soluong.getText().toString().trim();

                if (TextUtils.isEmpty(masach1)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập mã sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tensach1)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập tên sách!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(soluong1)) {
                    Toast.makeText(getApplication(), "Vui lòng nhập số lượng sách!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        bookHelper.QueryData("UPDATE Sach SET MaSach = '" + masach1 + "',TenSach = '" + tensach1 + "',SoLuong = " + soluong1 + ", MaKe = " + id_Ke + " WHERE MaSach = '" + masach + "'");
                        Toast.makeText(getApplication(), "Sửa thông tin sách thành công", Toast.LENGTH_SHORT).show();
                        select(id_Ke);
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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                select(id_Ke);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2500);
    }
}