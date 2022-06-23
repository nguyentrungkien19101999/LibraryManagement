package com.example.ictulib.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ictulib.BookHelper;
import com.example.ictulib.view.ListBookActivity;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_Storage;
import com.example.ictulib.model.Storage;
import com.example.ictulib.my_interface.IClickitemKeSach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookstoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    BookHelper bookHelper;
    ArrayList<Storage> mangStorage;

    private FloatingActionButton btnFloating;
    private RecyclerView rcvStorage;
    private Adapter_Storage mAdapterStorage;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookstore,container, false);

        rcvStorage = view.findViewById(R.id.rcv_kesach);
        btnFloating = view.findViewById(R.id.fab);
        swipeRefreshLayout = view.findViewById(R.id.srllayout);


        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rcvStorage.setLayoutManager(gridLayoutManager);

        mangStorage = new ArrayList<Storage>();

        bookHelper = new BookHelper(getActivity(), "books.db", null, 1);

        mAdapterStorage = new Adapter_Storage(mangStorage, new IClickitemKeSach() {
            @Override
            public void onClickItemStorage(Storage storage) {
                Intent intent = new Intent(getActivity(), ListBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id_kesach",storage.getId());
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(getActivity(), storage.getId()+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClicklongItemStorage(Storage storage) {
                DialogLongClick(storage.getName(),storage.getId());
                //Toast.makeText(getActivity(), "LongClick...", Toast.LENGTH_SHORT).show();
            }
        });

        select();

        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogKeSach();
                //Toast.makeText(getActivity(), "heloo", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //Dialog Click Floattingbtn
    private void DialogKeSach(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_kesach);

        dialog.setCancelable(false);

        EditText edtTenKe = dialog.findViewById(R.id.edt_kesach);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnThem = dialog.findViewById(R.id.btn_them);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenKe = edtTenKe.getText().toString().trim();
                if (TextUtils.isEmpty(tenKe)){
                    Toast.makeText(getActivity(), "Vui lòng nhập tên kệ sách!", Toast.LENGTH_SHORT).show();
                }else {
                    bookHelper.QueryData("INSERT INTO KeSach VALUES(null, '"+tenKe+"')");
                    Toast.makeText(getActivity(), "Thêm kệ sách thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    select();
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

    //Dialog LongClick
    private void DialogLongClick(String name, int id){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_xoa_ke);

        TextView tvNameKe = dialog.findViewById(R.id.tv_nameke);
        Button btnXoa = dialog.findViewById(R.id.btn_xoa);
        Button btnSua = dialog.findViewById(R.id.btn_sua);

        tvNameKe.setText(name);

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder delete = new AlertDialog.Builder(getActivity());
                delete.setTitle("Xóa kệ sách!");
                delete.setMessage("Bạn muốn xóa kệ sách đã chọn?");
                delete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookHelper.QueryData("DELETE FROM KeSach WHERE MaKe =" + id);
                        bookHelper.QueryData("DELETE FROM Sach WHERE MaKe =" + id);

                        Toast.makeText(getActivity(),"Xóa dữ liệu thành công!",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        select();
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
                DialogSuaTenKe(name, id);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Dialog Sua Ten Ke
    private void DialogSuaTenKe(String name, int id){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sua_ke);

        EditText edtTenKe = dialog.findViewById(R.id.edt_kesach);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnThem = dialog.findViewById(R.id.btn_them);
        edtTenKe.setText(name);

        dialog.setCancelable(false);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenKe = edtTenKe.getText().toString().trim();
                if (TextUtils.isEmpty(tenKe)) {
                    Toast.makeText(getActivity(), "Vui lòng nhập tên kệ sách!", Toast.LENGTH_SHORT).show();
                } else {
                    bookHelper.QueryData("UPDATE KeSach SET TenKe = '" + tenKe + "' WHERE MaKe = " + id);
                    Toast.makeText(getActivity(), "Sửa tên kệ sách thành công", Toast.LENGTH_SHORT).show();
                    select();
                    dialog.dismiss();
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

    //Select KeSach
    private void select(){
        mangStorage.clear();
        Cursor cursor = bookHelper.GetData("SELECT KeSach.MaKe, KeSach.TenKe, sum(SoLuong) FROM KeSach,Sach WHERE KeSach.MaKe = Sach.MaKe GROUP by KeSach.MaKe, KeSach.TenKe");
        Cursor cursor1 = bookHelper.GetData("select MaKe,TenKe from KeSach WHERE MaKe not in(SELECT MaKe from Sach GROUP by MaKe)");
        while (cursor.moveToNext()) {
            int MaKe = cursor.getInt(0);
            String TenKe = cursor.getString(1);
            String soluong = cursor.getString(2);

            mangStorage.add(new Storage(MaKe, soluong,TenKe));
        }

        while (cursor1.moveToNext()) {
            int MaKe1 = cursor1.getInt(0);
            String TenKe1 = cursor1.getString(1);
            int soluong1 = 0;

            mangStorage.add(new Storage(MaKe1, String.valueOf(soluong1),TenKe1));
        }
        //Toast.makeText(getActivity(), mangStorage+"", Toast.LENGTH_SHORT).show();
        mAdapterStorage.setData(mangStorage);
        rcvStorage.setAdapter(mAdapterStorage);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                select();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2500);
    }
}
