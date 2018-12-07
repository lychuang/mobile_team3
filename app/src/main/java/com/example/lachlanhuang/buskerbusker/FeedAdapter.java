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

public class FeedAdapter extends RecyclerView.Adapter {

    private ArrayList dataList;


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
            mUserName = (TextView) itemView.findViewById(R.id.item_username);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mLocation = (TextView) itemView.findViewById(R.id.item_location);




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
            int imageID = res.getIdentifier("stock", "drawable", "com.example.lachlanhuang.buskerbusker");


            mUserName.setText(DummyData.userNames[position]);
            mImageView.setImageResource(imageID);

        }

        public void onClick(View view) {

        }

    }
}
