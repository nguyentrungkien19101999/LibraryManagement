package com.example.ictulib.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.ictulib.MyApplication;
import com.example.ictulib.constant.GlobalFuntion;
import com.example.ictulib.view.ListBookActivity;
import com.example.ictulib.R;
import com.example.ictulib.adapter.Adapter_Storage;
import com.example.ictulib.model.Storage;
import com.example.ictulib.my_interface.IClickitemKeSach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class BookstoreFragment extends Fragment {

    private List<Storage> mListStorage;
    private FloatingActionButton btnFloating;
    private RecyclerView rcvStorage;
    private Adapter_Storage mAdapterStorage;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bookstore, container, false);

        initView();
        select();

        return mView;
    }

    private void initView() {
        if (mView == null) {
            return;
        }
        rcvStorage = mView.findViewById(R.id.rcv_kesach);
        btnFloating = mView.findViewById(R.id.fab);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rcvStorage.setLayoutManager(gridLayoutManager);

        mListStorage = new ArrayList<>();

        mAdapterStorage = new Adapter_Storage(getActivity(), mListStorage, new IClickitemKeSach() {
            @Override
            public void onClickItemStorage(Storage storage) {
                Intent intent = new Intent(getActivity(), ListBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id_kesach", storage.getId());
                bundle.putString("name_kesach", storage.getName());
                bundle.putInt("soluong_sach", storage.getSoluong());
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(getActivity(), storage.getId()+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClicklongItemStorage(Storage storage) {
                DialogLongClick(storage.getName(), storage.getId());
                //Toast.makeText(getActivity(), "LongClick...", Toast.LENGTH_SHORT).show();
            }
        });

        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogKeSach();
                //Toast.makeText(getActivity(), "heloo", Toast.LENGTH_SHORT).show();
            }
        });

        rcvStorage.setAdapter(mAdapterStorage);
    }

    //Dialog Click Floattingbtn
    private void DialogKeSach() {
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
                if (TextUtils.isEmpty(tenKe)) {
                    Toast.makeText(getActivity(), "Vui lòng nhập tên kệ sách!", Toast.LENGTH_SHORT).show();
                } else {
                    long categoryId = GlobalFuntion.getId();
                    Storage storage = new Storage();
                    storage.setName(tenKe);
                    storage.setId(String.valueOf(categoryId));

                    MyApplication.get(getActivity()).getBookShelf().child(String.valueOf(categoryId)).setValue(storage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(),
                                    "Thêm kệ sách thành công!", Toast.LENGTH_SHORT).show();
                            GlobalFuntion.hideSoftKeyboard(getActivity());
                            if (error != null) {
                                Toast.makeText(getActivity(),
                                        "Thêm kệ sách thất bại!", Toast.LENGTH_SHORT).show();
                                Log.d("AddBookShelf", "++++ " + error);
                            }
                        }
                    });
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

    //Dialog LongClick
    private void DialogLongClick(String name, String id) {
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
                        MyApplication.get(getActivity()).getBookShelf().child(id).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getActivity(), "Xóa kệ sách thành công!", Toast.LENGTH_LONG).show();
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
                DialogSuaTenKe(name, id);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Dialog Sua Ten Ke
    private void DialogSuaTenKe(String name, String id) {
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
                    Storage storage = new Storage();
                    storage.setName(tenKe);
                    storage.setId(id);

                    MyApplication.get(getActivity()).getBookShelf().child(id).setValue(storage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(),
                                    "Sửa thông tin kệ sách thành công!", Toast.LENGTH_LONG).show();
                        }
                    });
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
    private void select() {
        if (getActivity() == null) {
            return;
        }

        MyApplication.get(getActivity()).getBookShelf().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Storage storage = snapshot.getValue(Storage.class);
                if (storage == null || mListStorage == null || mAdapterStorage == null) {
                    return;
                }
                mListStorage.add(0, storage);
                mAdapterStorage.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Storage storage = snapshot.getValue(Storage.class);
                if (storage == null || mListStorage == null || mAdapterStorage == null) {
                    return;
                }
                for (int i = 0; i < mListStorage.size(); i++) {
                    if (storage.getId().equalsIgnoreCase(mListStorage.get(i).getId())) {
                        mListStorage.set(i, storage);
                        break;
                    }
                }
                mAdapterStorage.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Storage storage = snapshot.getValue(Storage.class);
                if (storage == null || mListStorage == null || mAdapterStorage == null) {
                    return;
                }
                for (Storage roomDelete : mListStorage) {
                    if (storage.getId().equalsIgnoreCase(roomDelete.getId())) {
                        mListStorage.remove(roomDelete);
                        break;
                    }
                }
                mAdapterStorage.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "R.string.msg_error_not_connect_to_internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapterStorage != null) {
            mAdapterStorage.release();
        }
    }
}
