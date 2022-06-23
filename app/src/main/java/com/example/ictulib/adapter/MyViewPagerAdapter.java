package com.example.ictulib.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ictulib.fragment.BookstoreFragment;
import com.example.ictulib.fragment.BorrowFragment;
import com.example.ictulib.fragment.History_BorrowFragment;
import com.example.ictulib.fragment.ReturnBookFragment;
import com.example.ictulib.fragment_nav.HomeFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull HomeFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BorrowFragment();

            case 1:
                return new ReturnBookFragment();

            case 2:
                return new History_BorrowFragment();

            case 3:
                return new BookstoreFragment();

            default:
                return new BorrowFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
