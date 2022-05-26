package com.example.ictulib.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictulib.R;
import com.example.ictulib.my_interface.IClickitemKeSach;

import java.util.List;

public class Adapter_Storage extends RecyclerView.Adapter<Adapter_Storage.StorageViewHolder>{


    private List<Storage> mListStorage;
    private IClickitemKeSach iClickitemKeSach;

    public Adapter_Storage(List<Storage> mListStorage, IClickitemKeSach listener) {
        this.mListStorage = mListStorage;
        this.iClickitemKeSach = listener;
    }

    public void setData(List<Storage> list){
        this.mListStorage = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kesach, parent, false );
        return new StorageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position) {
        final Storage storage = mListStorage.get(position);
        if (storage == null){
            return;
        }

        holder.tv_soluong.setText(storage.getSoluong()+" Sách");
        holder.tvName.setText(storage.getName());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickitemKeSach.onClickItemStorage(storage);
            }
        });

        holder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iClickitemKeSach.onClicklongItemStorage(storage);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mListStorage != null){
            return mListStorage.size();
        }
        return 0;
    }

    public class StorageViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_soluong;
        private TextView tvName;
        private RelativeLayout layoutItem;

        public StorageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tv_soluong = itemView.findViewById(R.id.tv_soluong);
            layoutItem = itemView.findViewById(R.id.linear);
        }
    }

}
