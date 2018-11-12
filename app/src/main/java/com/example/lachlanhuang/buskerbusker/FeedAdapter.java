package com.example.lachlanhuang.buskerbusker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import static android.support.v4.content.ContextCompat.startActivity;

public class FeedAdapter extends RecyclerView.Adapter {

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

        public FeedViewHolder(final View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.item_username);

            // transition to post
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), FeedPost.class);
                    itemView.getContext().startActivity(intent);

                }
            });
        }

        public void bindView(int position) {
            mUserName.setText(DummyData.userNames[position]);
        }

        public void onClick(View view) {

        }

    }
}
