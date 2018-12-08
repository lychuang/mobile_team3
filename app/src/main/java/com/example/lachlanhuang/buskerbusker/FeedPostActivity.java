package com.example.lachlanhuang.buskerbusker;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class FeedPostActivity extends AppCompatActivity {

    // images downloaded from the web for the sake of V I S U A L S
    // rando european places
    private String[] images = {
            "https://images.unsplash.com/photo-1543996991-8e851c2dc841?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1651&q=80",
            "https://images.unsplash.com/photo-1482042519045-a51ea0cfb61d?ixlib=rb-1.2.1&auto=format&fit=crop&w=1650&q=80",
            "https://images.unsplash.com/photo-1453747063559-36695c8771bd?ixlib=rb-1.2.1&auto=format&fit=crop&w=1650&q=80"

    };

    ViewPager viewPager;
    DotsIndicator dotsIndicator;
    private  int dotsCount;



    // TODO: Create page for when you click on each post
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);


        PhotoViewPagerAdapter photoViewPagerAdapter = new PhotoViewPagerAdapter(FeedPostActivity.this, images);

        viewPager.setAdapter(photoViewPagerAdapter);
        dotsIndicator.setViewPager(viewPager);


        dotsCount = photoViewPagerAdapter.getCount();


//
    }
}
