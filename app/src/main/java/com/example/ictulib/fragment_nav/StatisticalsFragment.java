package com.example.ictulib.fragment_nav;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ictulib.BookHelper;
import com.example.ictulib.R;
import com.example.ictulib.model.StatisticBorrow;
import com.skydoves.progressview.ProgressView;

import java.util.ArrayList;

public class StatisticalsFragment extends Fragment {
    BookHelper bookHelper;
    ArrayList<StatisticBorrow> mangThongKe;
    private TextView tvProgress1;
    private TextView tvProgress2;
    private TextView tvProgress3;
    private TextView tvProgress4;

    private ProgressView progressView1;
    private ProgressView progressView2;
    private ProgressView progressView3;
    private ProgressView progressView4;

    private TextView tv_SumSach;
    private TextView tv_SumKe;
    private TextView tv_sumMuon;

    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statisticals, container, false);

        tvProgress1 = view.findViewById(R.id.tv_progress_1);
        tvProgress2 = view.findViewById(R.id.tv_progress_2);
        tvProgress3 = view.findViewById(R.id.tv_progress_3);
        tvProgress4 = view.findViewById(R.id.tv_progress_4);

        progressView1 = view.findViewById(R.id.progress_1);
        progressView2 = view.findViewById(R.id.progress_2);
        progressView3 = view.findViewById(R.id.progress_3);
        progressView4 = view.findViewById(R.id.progress_4);

        tv_SumSach = view.findViewById(R.id.tv_sumSach);
        tv_SumKe = view.findViewById(R.id.tv_sumKe);
        tv_sumMuon = view.findViewById(R.id.tv_sumMuon);

        spinner = view.findViewById(R.id.spinner2);

        //create database
        bookHelper = new BookHelper(getActivity(), "books.db", null, 1);

        mangThongKe = new ArrayList<StatisticBorrow>();

        tinhTong();

        Cursor cursor = bookHelper.GetData("SELECT strftime('%m', NgayMuon), sum(SoLuong) FROM MuonSach GROUP by strftime('%m', NgayMuon)");
        while (cursor.moveToNext()){
            String thang = cursor.getString(0);
            int count = cursor.getInt(1);
            mangThongKe.add(new StatisticBorrow(thang,count));
        }

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("4 tháng qua");
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int thang7 = 0;
                int thang8 = 0;
                int thang9 = 0;
                int thang10 = 0;
                for (int i1 = 0; i1 < mangThongKe.size(); i1++) {
                    if (mangThongKe.get(i1).getThang().compareTo("07") == 0) {
                        thang7 = mangThongKe.get(i1).getCount();

                    } else

                        if (mangThongKe.get(i1).getThang().compareTo("08") == 0) {
                        thang8 = mangThongKe.get(i1).getCount();

                    } else

                        if (mangThongKe.get(i1).getThang().compareTo("09") == 0) {
                        thang9 = mangThongKe.get(i1).getCount();

                    } else

                        if (mangThongKe.get(i1).getThang().compareTo("10") == 0) {
                        thang10 = mangThongKe.get(i1).getCount();

                    }
                }
                progressChart(thang7, thang8, thang9, thang10);
                //Toast.makeText(getActivity(), mangThongKe.size()+"", Toast.LENGTH_LONG).show();
                //Toast.makeText(getActivity(), thang7+" "+thang8+" "+thang9+" "+thang10,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Toast.makeText(getActivity(), mangThongKe.get(0).getCount()+" "+mangThongKe.get(1).getCount()+" "+mangThongKe.get(2).getCount()+" ",Toast.LENGTH_LONG).show();

        return view;
    }

    private void progressChart(int progress1, int progress2, int progress3, int progress4){
        tvProgress1.setText(progress1+"");
        tvProgress2.setText(progress2+"");
        tvProgress3.setText(progress3+"");
        tvProgress4.setText(progress4+"");

        progressView1.setProgress(progress1);
        progressView2.setProgress(progress2);
        progressView3.setProgress(progress3);
        progressView4.setProgress(progress4);
    }

    private void tinhTong(){
        int soLuongSach = 0;
        int soLuongKe = 0;
        int sum1 = 0;
        int sum2 = 0;
        int sum3 = 0;

        //Tinh tong so luong sach
        Cursor sumSach = bookHelper.GetData("SELECT SUM(SoLuong) FROM Sach");
        Cursor sumsachDangMuon = bookHelper.GetData("SELECT SUM(SoLuong) FROM MuonSach WHERE NgayTra is NULL");

        while (sumSach.moveToNext()){
            sum1 = sumSach.getInt(0);
        }
        while (sumsachDangMuon.moveToNext()){
            sum2 = sumsachDangMuon.getInt(0);
        }

        soLuongSach = sum1 + sum2;

        tv_SumSach.setText(soLuongSach+"");

        //Tinh tong so luong ke sach
        Cursor sumKe = bookHelper.GetData("SELECT count(MaKe) FROM KeSach");
        while (sumKe.moveToNext()){
            sum3 = sumKe.getInt(0);
        }

        tv_SumKe.setText(sum3+"");

        //Tinh tong sach dang cho muon
        tv_sumMuon.setText(sum2+"");
    }
}
