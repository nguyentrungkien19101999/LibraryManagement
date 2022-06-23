package com.example.ictulib.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ictulib.R;
import com.example.ictulib.fragment_nav.AboutFragment;
import com.example.ictulib.fragment_nav.HomeFragment;
import com.example.ictulib.fragment_nav.StatisticalsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvname, tvemail;
    private NavigationView mnavigationView;

    private DrawerLayout mDrawerlayout;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_STATISTICALS = 1;
    private static final int FRAGMENT_ABOUT = 2;
    private static final int FRAGMENT_LOGOUT = 3;

    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerlayout =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerlayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();


        mnavigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        mnavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        showUserinfo();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.nav_home){
            if (mCurrentFragment != FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }

        }else if(id==R.id.nav_statictical){
            if (mCurrentFragment != FRAGMENT_STATISTICALS){
                replaceFragment(new StatisticalsFragment());
                mCurrentFragment = FRAGMENT_STATISTICALS;
            }

        }else if(id==R.id.nav_about){
            if (mCurrentFragment != FRAGMENT_ABOUT){
                replaceFragment(new AboutFragment());
                mCurrentFragment = FRAGMENT_ABOUT;
            }
        }else if(id==R.id.nav_logout){
            if (mCurrentFragment != FRAGMENT_LOGOUT){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        mDrawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ấn QUAY LẠI một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,fragment);
        transaction.commit();
    }

    private void initUi(){
        mnavigationView = findViewById(R.id.navigation_view);
        tvname = mnavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvemail = mnavigationView.getHeaderView(0).findViewById(R.id.tv_email);
    }
    private void showUserinfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();

        if(name == null){
            tvname.setVisibility((View.GONE));
        }else {
            tvemail.setVisibility(View.VISIBLE);
            tvname.setText(name);
        }

        tvname.setText(name);
        tvemail.setText(email);
    }
}