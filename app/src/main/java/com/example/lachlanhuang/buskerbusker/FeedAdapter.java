package com.example.lachlanhuang.buskerbusker;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.lachlanhuang.buskerbusker.database.Post;
import com.example.lachlanhuang.buskerbusker.database.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FeedAdapter extends RecyclerView.Adapter {

    private ArrayList dataList;

    private static final String TAG = "FeedPostActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference postReference;
    private ValueEventListener postListener;
    private String postKey;

    private TextView buskerNameView;
    private TextView buskerLocationView;
    private TextView textDescView;

    public static String[] stockImages = new String[] {
            "https://images.unsplash.com/photo-1516280440614-37939bbacd81?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80",
            "https://images.unsplash.com/photo-1445965752525-ac2d3c195ffe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1867&q=80",
            "https://images.unsplash.com/photo-1485233557992-5c5b5d91eef1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2689&q=80",
            "https://images.unsplash.com/photo-1495584573439-311b2edeec75?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1866&q=80",
            "https://images.unsplash.com/photo-1493849749377-e4f82d0a8319?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1948&q=80",
            "https://images.unsplash.com/photo-1523772148557-7180f78e3511?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1867&q=80",
            "https://images.unsplash.com/photo-1454486837617-ce8e1ba5ebfe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1952&q=80",
            "https://images.unsplash.com/photo-1469488865564-c2de10f69f96?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80"
    };




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((FeedViewHolder) viewHolder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return DummyData.userNames.length;

    }

    private class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUserName;
        private ImageView mImageView;
        private TextView mLocation;

        public FeedViewHolder(final View itemView) {
            super(itemView);

// WHY WONT IT WORK
            // Get post key from intent
//            postKey = ref.getIntent().getStringExtra(EXTRA_POST_KEY);
//            postKey = ref.child("posts").push().getKey();
//            if (postKey == null) {
//                throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
//            }
//
//            // Initialize Database
//            postReference = FirebaseDatabase.getInstance().getReference()
//                    .child("posts").child(postKey);
//
//            buskerNameView = (TextView) itemView.findViewById(R.id.busker_name);
//            buskerLocationView = (TextView) itemView.findViewById(R.id.busker_location);
//            textDescView = (TextView) itemView.findViewById(R.id.post_description);

            mUserName = (TextView) itemView.findViewById(R.id.busker_name);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mLocation = (TextView) itemView.findViewById(R.id.busker_location);




            // transition to post
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), FeedPostActivity.class);
                    itemView.getContext().startActivity(intent);

                }
            });
        }

        public void bindView(int position) {

            /* temporary data for display
             * will load real data from database
             */
            Resources res = itemView.getContext().getResources();



            mUserName.setText(DummyData.userNames[position]);
//            mImageView.setImageResource(imageID);

            try {
                Picasso.get()
                        .load(stockImages[position])
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(mImageView);
            } catch (Exception ex) {
                // put something here later
            }

        }

        public void onClick(View view) {

        }



    }


}
