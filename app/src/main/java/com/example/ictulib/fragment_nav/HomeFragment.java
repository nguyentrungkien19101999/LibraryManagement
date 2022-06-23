package com.example.ictulib.fragment_nav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ictulib.R;
import com.example.ictulib.adapter.MyViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeFragment extends Fragment {

    private ViewPager2 mViewPager;
    public BottomNavigationView mBottomNavigationView;

    private View mView;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initUi();
        return mView;
    }

    private void initUi() {
        mViewPager = mView.findViewById(R.id.view_pager);
        mBottomNavigationView = mView.findViewById(R.id.bottom_navigation);

        myViewPagerAdapter = new MyViewPagerAdapter(this);
        mViewPager.setAdapter(myViewPagerAdapter);


        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.tab_borrow).setChecked(true);
                        break;

                    case 1:

                        mBottomNavigationView.getMenu().findItem(R.id.tab_returnbook).setChecked(true);
                        break;

                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.tab_history).setChecked(true);
                        break;

                    case 3:
                        mBottomNavigationView.getMenu().findItem(R.id.tab_storage).setChecked(true);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab_borrow:
                        mViewPager.setCurrentItem(0);
                        break;

                    case R.id.tab_returnbook:
                        mViewPager.setCurrentItem(1);
                        break;

                    case R.id.tab_history:
                        mViewPager.setCurrentItem(2);
                        break;

                    case R.id.tab_storage:
                        mViewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
    }
    /*private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.listView,fragment);
        transaction.commit();
    }*/
}
