package com.example.ictulib.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictulib.R;
import com.example.ictulib.model.Sach;
import com.example.ictulib.my_interface.IClickitemSach;

import java.util.List;


public class Adapter_Sach extends RecyclerView.Adapter<Adapter_Sach.SachViewHolder>{
    private List<Sach> mListSach;
    private IClickitemSach iClickitemSach;

    public Adapter_Sach(List<Sach> mListSach, IClickitemSach iClickitemSach) {
        this.mListSach = mListSach;
        this.iClickitemSach = iClickitemSach;
    }

    public void setData(List<Sach> list){
        this.mListSach = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sach, parent, false);
        return new SachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SachViewHolder holder, int position) {
        Sach sach = mListSach.get(position);
        if (sach == null){
            return;
        }
        holder.tvId.setText(sach.getId());
        holder.tvName.setText(sach.getName());
        holder.tvSoluong.setText(sach.getSoluong());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickitemSach.onClickItemSach(sach);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListSach != null){
            return mListSach.size();
        }
        return 0;
    }

    public class SachViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvId;
        private TextView tvSoluong;
        private RelativeLayout layoutItem;

        public SachViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.name);
            tvId = itemView.findViewById(R.id.tv_masach);
            tvSoluong = itemView.findViewById(R.id.tv_soluong);
            layoutItem = itemView.findViewById(R.id.linear);
        }
    }

}