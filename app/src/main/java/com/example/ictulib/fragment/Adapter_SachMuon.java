package com.example.ictulib.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictulib.R;
import com.example.ictulib.my_interface.IClickitemMuonSach;

import java.util.List;

public class Adapter_SachMuon extends RecyclerView.Adapter<Adapter_SachMuon.MuonViewHolder>{
    private List<Books> mlistMuon;
    private IClickitemMuonSach iClickitemMuonSach;

    public Adapter_SachMuon(List<Books> mlistMuon, IClickitemMuonSach iClickitemMuonSach) {
        this.mlistMuon = mlistMuon;
        this.iClickitemMuonSach = iClickitemMuonSach;
    }

    public void setData(List<Books> list){
        this.mlistMuon = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MuonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlistview_returnbook, parent, false);
        return new MuonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuonViewHolder holder, int position) {
        Books books = mlistMuon.get(position);
        if (books == null){
            return;
        }
        holder.maSV.setText(books.getMaSV());
        holder.maSach.setText(books.getMaSach());
        holder.tenSV.setText(books.getNamestudent());
        holder.soLuong.setText(books.getSoLuong());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickitemMuonSach.onClickItemMuonSach(books);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mlistMuon != null){
            return mlistMuon.size();
        }
        return 0;
    }

    public class MuonViewHolder extends RecyclerView.ViewHolder{
        private TextView maSV, maSach, tenSV, soLuong;
        private RelativeLayout relativeLayout;

        public MuonViewHolder(@NonNull View itemView) {
            super(itemView);

            maSV = itemView.findViewById(R.id.tv_idsv);
            tenSV = itemView.findViewById(R.id.tv_namesv);
            maSach = itemView.findViewById(R.id.tv_masach1);
            soLuong = itemView.findViewById(R.id.tv_soluong);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

}