package com.example.ictulib.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ictulib.BookHelper;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_SachMuon;
import com.example.ictulib.model.Books;
import com.example.ictulib.my_interface.IClickitemMuonSach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ReturnBookFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<Books> mangMuonSach;
    BookHelper bookHelper;

    private Adapter_SachMuon adapterBook;
    private SwipeRefreshLayout mSrlLayout;
    private RecyclerView rcvMuonSach;
    private FloatingActionButton floatingActionButton;
    private EditText editText_MaSV;
    private EditText editText_TenSV;
    private EditText editText_MaSach;
    private EditText editText_SoLuong;
    private EditText editText_NgayMuon;
    private EditText editText_NgayTra;
    private Button btn_update;
    private Button btn_delete;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_return, container, false);

        rcvMuonSach = view.findViewById(R.id.rcv_muonsach);
        mSrlLayout = view.findViewById(R.id.srllayout);
        floatingActionButton = view.findViewById(R.id.fab);
        spinner = view.findViewById(R.id.spinner);

        mSrlLayout.setColorSchemeResources(R.color.orange, R.color.green);
        mSrlLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false);
        rcvMuonSach.setLayoutManager(linearLayoutManager);

        mangMuonSach = new ArrayList<Books>();

        //create database
        bookHelper = new BookHelper(getActivity(), "books.db", null, 1);

        adapterBook = new Adapter_SachMuon(mangMuonSach, new IClickitemMuonSach() {
            @Override
            public void onClickItemMuonSach(Books books) {
                DialogUpdateInfor(Gravity.CENTER, books.getId());
            }
        });

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

        bookHelper.QueryData("CREATE VIEW IF NOT EXISTS dangmuon_VIEW AS SELECT * FROM MuonSach WHERE NgayTra is NULL");

        ArrayList<String> arraySpinner = new ArrayList<String>();
        arraySpinner.add("Sách đang cho mượn");
        arraySpinner.add("Sách mượn quá hạn (2 tháng)");

        ArrayAdapter  spinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, arraySpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        select1();
                        break;
                    case 1:
                        quaHanMuon();
                        break;
                    default:
                        select1();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        select1();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSearch();
            }
        });
        return view;
    }

    private void select1() {
        mangMuonSach.clear();
        Cursor cursor = bookHelper.GetData("SELECT * FROM MuonSach WHERE NgayTra is null");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String MaSV = cursor.getString(1);
            String MaSach = cursor.getString(2);
            String TenSV = cursor.getString(3);
            String SoLuong = cursor.getString(4);

            mangMuonSach.add(new Books(id,MaSV, TenSV, MaSach, SoLuong));
        }
        if(mangMuonSach.size() ==0){
            Toast.makeText(getActivity(), "Chưa có sách mượn", Toast.LENGTH_LONG).show();
        }else {
            adapterBook.setData(mangMuonSach);
            rcvMuonSach.setAdapter(adapterBook);
        }
    }

    private void dialogSearch(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_muontra);

        dialog.setCancelable(false);

        EditText edtsearch = dialog.findViewById(R.id.edt_search);
        Button btnHuy = dialog.findViewById(R.id.btn_huy);
        Button btnSearch = dialog.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = edtsearch.getText().toString().trim();
                if (TextUtils.isEmpty(search)){
                    Toast.makeText(getActivity(), "Vui lòng nhập từ khóa!", Toast.LENGTH_SHORT).show();
                }else {
                    mangMuonSach.clear();
                    Cursor cursor = bookHelper.GetData("SELECT * FROM dangmuon_VIEW WHERE TenSV like '%"+search+"%' or MaSach like '%"+search+"%' or MaSV like '%"+search+"%' or NgayMuon like '%"+search+"%'");
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        String MaSV = cursor.getString(1);
                        String MaSach = cursor.getString(2);
                        String TenSV = cursor.getString(3);
                        String SoLuong = cursor.getString(4);

                        mangMuonSach.add(new Books(id,MaSV, TenSV, MaSach, SoLuong));
                    }

                    if (mangMuonSach.size() == 0){
                        Toast.makeText(getActivity(), "Không tồn tại đối tượng cần tìm!", Toast.LENGTH_LONG).show();
                    }else {
                        adapterBook.setData(mangMuonSach);
                        rcvMuonSach.setAdapter(adapterBook);

                        dialog.dismiss();
                    }
                    //Toast.makeText(getActivity(), mangMuonSach+" ", Toast.LENGTH_SHORT).show();
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

    private void DialogUpdateInfor(int gravity, int id){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_infor);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        editText_MaSV = dialog.findViewById(R.id.idsinhvien);
        editText_TenSV = dialog.findViewById(R.id.name_studens);
        editText_MaSach = dialog.findViewById(R.id.ma_book);
        editText_SoLuong = dialog.findViewById(R.id.count);
        editText_NgayMuon = dialog.findViewById(R.id.datemuon);
        editText_NgayTra = dialog.findViewById(R.id.datetra);
        btn_delete = dialog.findViewById(R.id.btndelete);
        btn_update = dialog.findViewById(R.id.btnupdate);

        Cursor cursor = bookHelper.GetData("SELECT * FROM MuonSach WHERE Id =" + id);
        //Toast.makeText(getApplication(), id +"", Toast.LENGTH_SHORT).show();

        while (cursor.moveToNext()) {
            editText_MaSV.setText(cursor.getString(1));
            editText_MaSach.setText(cursor.getString(2));
            editText_TenSV.setText(cursor.getString(3));
            editText_SoLuong.setText(cursor.getString(4));
            editText_NgayMuon.setText(cursor.getString(5));
            editText_NgayTra.setText(cursor.getString(6));
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder delete = new AlertDialog.Builder(getActivity());
                delete.setTitle("Thông báo!");
                delete.setMessage("Bạn muốn xóa dữ liệu?");
                delete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strMaSach = editText_MaSach.getText().toString().trim();
                        int count1 = Integer.parseInt(editText_SoLuong.getText().toString());
                        bookHelper.QueryData("DELETE FROM MuonSach WHERE Id =" + id);

                        Toast.makeText(getActivity(),"Xóa dữ liệu thành công!",Toast.LENGTH_LONG).show();
                        int soluongmuon = count1;

                        Cursor cursor = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+strMaSach+"'");
                        int soluong = 0;
                        while (cursor.moveToNext()){
                            soluong = cursor.getInt(0);
                        }

                        soluong = soluong + soluongmuon;
                        //Toast.makeText(getApplication(), soluong+"", Toast.LENGTH_LONG).show();
                        bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluong+" WHERE MaSach ='"+strMaSach+"'");
                        select1();
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
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDateData(id);
                select1();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void upDateData(int id) {
        String strMaSV = editText_MaSV.getText().toString().trim();
        String strTenSV = editText_TenSV.getText().toString().trim();
        String strMaSach = editText_MaSach.getText().toString().trim();
        //int strsoluong = Integer.parseInt(String.valueOf(count));
        String strmuon = editText_NgayMuon.getText().toString().trim();
        String strtra = editText_NgayTra.getText().toString().trim();

        if (TextUtils.isEmpty(strMaSV)) {
            Toast.makeText(getActivity(), "Vui lòng nhập mã sinh viên!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strTenSV)) {
            Toast.makeText(getActivity(), "Vui lòng nhập tên sinh viên!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strMaSach)) {
            Toast.makeText(getActivity(), "Vui lòng nhập tên sách!", Toast.LENGTH_LONG).show();
        }  else if (TextUtils.isEmpty(editText_SoLuong.getText())) {
            Toast.makeText(getActivity(), "Vui lòng nhập số lượng!", Toast.LENGTH_LONG).show();
        } else if ((Integer.parseInt(String.valueOf(editText_SoLuong.getText()))>5)) {
            Toast.makeText(getActivity(), "Mỗi sinh viên mượn tối đa 5 quyển sách!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strmuon)) {
            Toast.makeText(getActivity(), "Vui lòng thêm ngày mượn!", Toast.LENGTH_LONG).show();
        } else {
            //Thay doi du lieu khi chua tra sach
            if (TextUtils.isEmpty(strtra)) {
                Cursor maSach = bookHelper.GetData("SELECT MaSach FROM MuonSach WHERE Id ='"+id+"'");
                String idSach = null;
                while (maSach.moveToNext()){
                    idSach = maSach.getString(0);
                }
                //Toast.makeText(this, idSach.compareTo(strMaSV)+"", Toast.LENGTH_LONG).show();
                if (idSach.compareTo(strMaSach) == 0){
                    try {
                        int soluongSua = Integer.parseInt(editText_SoLuong.getText().toString());

                        //Lay so luong muon
                        Cursor cursor = bookHelper.GetData("SELECT SoLuong FROM MuonSach WHERE MaSach ='"+strMaSach+"'");

                        //Lay so luong trong kho
                        Cursor cursor1 = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+strMaSach+"'");

                        int soluongKho = 0;
                        int soluongMuon = 0;

                        while (cursor.moveToNext()){
                            soluongMuon = cursor.getInt(0);
                        }

                        while (cursor1.moveToNext()){
                            soluongKho = cursor1.getInt(0);
                        }

                        if (soluongSua == soluongMuon){
                            bookHelper.QueryData("UPDATE MuonSach SET " +
                                    "MaSV ='" + strMaSV + "', " +
                                    "TenSV ='" + strTenSV + "', " +
                                    "MaSach ='" + strMaSach + "'," +
                                    "SoLuong=" + soluongSua + ", " +
                                    "NgayMuon='" + strmuon + "', " +
                                    "NgayTra=null " +
                                    "WHERE Id =" + id + "");

                            Toast.makeText(getActivity(), "Thay đổi dữ liệu mượn thành công!", Toast.LENGTH_SHORT).show();

                        } else if (soluongSua > soluongMuon){
                            bookHelper.QueryData("UPDATE MuonSach SET " +
                                    "MaSV ='" + strMaSV + "', " +
                                    "TenSV ='" + strTenSV + "', " +
                                    "MaSach ='" + strMaSach + "'," +
                                    "SoLuong=" + soluongSua + ", " +
                                    "NgayMuon='" + strmuon + "', " +
                                    "NgayTra=null " +
                                    "WHERE Id =" + id + "");
                            Toast.makeText(getActivity(), "Thay đổi dữ liệu mượn thành công!", Toast.LENGTH_SHORT).show();

                            soluongSua = soluongSua - soluongMuon;
                            soluongKho = soluongKho - soluongSua;

                            //Toast.makeText(getApplication(), soluong+"", Toast.LENGTH_LONG).show();
                            bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluongKho+" WHERE MaSach ='"+strMaSach+"'");
                        } else if (soluongSua < soluongMuon){
                            bookHelper.QueryData("UPDATE MuonSach SET " +
                                    "MaSV ='" + strMaSV + "', " +
                                    "TenSV ='" + strTenSV + "', " +
                                    "MaSach ='" + strMaSach + "'," +
                                    "SoLuong=" + soluongSua + ", " +
                                    "NgayMuon='" + strmuon + "', " +
                                    "NgayTra=null " +
                                    "WHERE Id =" + id + "");
                            Toast.makeText(getActivity(), "Thay đổi dữ liệu mượn thành công!", Toast.LENGTH_SHORT).show();

                            soluongSua = soluongMuon - soluongSua;
                            soluongKho = soluongKho + soluongSua;

                            bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluongKho+" WHERE MaSach ='"+strMaSach+"'");
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Số lượng sai định dạng! Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                    }

                }else if (idSach.compareTo(strMaSach) != 0){
                    try {
                        int soluongSua = Integer.parseInt(editText_SoLuong.getText().toString());

                        //Lay so luong kho sach cu
                        Cursor cursor = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+idSach+"'");

                        //Lay so luong trong kho sach moi
                        Cursor cursor1 = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+strMaSach+"'");

                        //Lay so luong muon sach cu
                        Cursor cursor2 = bookHelper.GetData("SELECT SoLuong FROM MuonSach WHERE Id ='"+id+"'");

                        int soluongKhocu = 0;
                        int soluongKhomoi = 0;
                        int soluongMuonSachcu = 0;

                        while (cursor.moveToNext()){
                            soluongKhocu = cursor.getInt(0);
                        }

                        while (cursor1.moveToNext()){
                            soluongKhomoi = cursor1.getInt(0);
                        }

                        while (cursor2.moveToNext()){
                            soluongMuonSachcu = cursor2.getInt(0);
                        }

                        bookHelper.QueryData("UPDATE MuonSach SET " +
                                "MaSV ='" + strMaSV + "', " +
                                "TenSV ='" + strTenSV + "', " +
                                "MaSach ='" + strMaSach + "'," +
                                "SoLuong=" + soluongSua + ", " +
                                "NgayMuon='" + strmuon + "', " +
                                "NgayTra=null " +
                                "WHERE Id =" + id + "");
                        Toast.makeText(getActivity(), "Thay đổi dữ liệu mượn thành công!", Toast.LENGTH_SHORT).show();

                        soluongKhocu = soluongKhocu + soluongMuonSachcu;

                        bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluongKhocu+" WHERE MaSach ='"+idSach+"'");

                        soluongKhomoi = soluongKhomoi - soluongSua;
                        bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluongKhomoi+" WHERE MaSach ='"+strMaSach+"'");
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Số lượng sai định dạng! Vui lòng nhập lại!", Toast.LENGTH_SHORT).show(); }
                }
            } else {
                try {
                    int count1 = Integer.parseInt(editText_SoLuong.getText().toString());

                    bookHelper.QueryData("UPDATE MuonSach SET " +
                            "MaSV ='" + strMaSV + "', " +
                            "TenSV ='" + strTenSV + "', " +
                            "MaSach ='" + strMaSach + "'," +
                            "SoLuong=" + count1 + ", " +
                            "NgayMuon='" + strmuon + "', " +
                            "NgayTra='" + editText_NgayTra.getText().toString().trim() + "' " +
                            "WHERE Id =" + id + "");
                    Toast.makeText(getActivity(), "Thay đổi dữ liệu mượn thành công!", Toast.LENGTH_SHORT).show();

                    int soluongmuon = count1;
                    Cursor cursor = bookHelper.GetData("SELECT SoLuong FROM Sach WHERE MaSach ='"+strMaSach+"'");
                    int soluong = 0;
                    while (cursor.moveToNext()){
                        soluong = cursor.getInt(0);
                    }

                    soluong = soluong + soluongmuon;
                    //Toast.makeText(getApplication(), soluong+"", Toast.LENGTH_LONG).show();
                    bookHelper.QueryData("UPDATE Sach SET SoLuong ="+soluong+" WHERE MaSach ='"+strMaSach+"'");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Nhập số lượng sai định dạng! Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void quaHanMuon(){
        mangMuonSach.clear();
        Cursor cursor = bookHelper.GetData("SELECT * FROM MuonSach WHERE NgayTra is null \n" +
                "and (julianday('now') - julianday(NgayMuon))>=60");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String MaSV = cursor.getString(1);
            String MaSach = cursor.getString(2);
            String TenSV = cursor.getString(3);
            String SoLuong = cursor.getString(4);

            mangMuonSach.add(new Books(id,MaSV, TenSV, MaSach, SoLuong));
        }
        if(mangMuonSach.size() ==0){
            Toast.makeText(getActivity(), "Chưa có sách mượn quá hạn", Toast.LENGTH_LONG).show();
        }else {
            adapterBook.setData(mangMuonSach);
            rcvMuonSach.setAdapter(adapterBook);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                select1();
                mSrlLayout.setRefreshing(false);
            }
        }, 1200);
    }
}
