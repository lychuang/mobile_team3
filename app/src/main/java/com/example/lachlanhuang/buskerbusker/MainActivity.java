package com.example.lachlanhuang.buskerbusker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.support.v4.app.Fragment;

import com.example.lachlanhuang.buskerbusker.database.User;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;

    private MapFragment mapFragment;
    private FeedFragment feedFragment;
    private AccountFragment accountFragment;

    private User mUser;

    private Bundle args;


    public final static int REQUEST_LOCATION_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        mapFragment = new MapFragment();
        feedFragment = new FeedFragment();
        accountFragment = new AccountFragment();

        Bundle extras = getIntent().getExtras();
        mUser = (User)extras.getSerializable("USER_CLASS");

        String s = String.format("USER = %s", mUser.getId());
        Log.d("myTag", s);


        args = new Bundle();
        args.putString("USER_ID", mUser.getId());
        mapFragment.setArguments(args);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkLocationPermission();
        }

        if (savedInstanceState == null) {
            setFragment(mapFragment);
        }


        // when navigation item is selected
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_map :
                        setFragment(mapFragment);
                        return true;

                    case R.id.nav_feed :
                        setFragment(feedFragment);
                        return true;

                    case R.id.nav_account :
                        setFragment(accountFragment);
                        return true;

                    default:
                        return false;

                }
            }
        });


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }



    //check whether the client is connected
    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }
            return false;

        }

        return true;

    }

}
