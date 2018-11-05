package com.example.lachlanhuang.buskerbusker;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.support.v4.app.Fragment;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;

    private MapsActivity mapFragment;
    private FeedFragment feedFragment;
    private AccountFragment accountFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        mapFragment = new MapsActivity();
        feedFragment = new FeedFragment();
        accountFragment = new AccountFragment();

        // FragmentActivity not the same as Fragment, error with MapsActivity
        // Just use BottomNav with Activities?
        /*mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_map :
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(mapFragment);
                        return true;

                    case R.id.nav_feed :
                        mainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(feedFragment);
                        return true;

                    case R.id.nav_account :
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(accountFragment);
                        return true;

                    default:
                        return false;

                }
            }
        });*/


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
