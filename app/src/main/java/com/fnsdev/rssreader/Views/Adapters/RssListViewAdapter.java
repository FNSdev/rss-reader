package com.fnsdev.rssreader.Views.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fnsdev.rssreader.Models.Entities.Feed;
import com.fnsdev.rssreader.R;
import com.fnsdev.rssreader.ViewModels.WebViewViewModel;
import com.fnsdev.rssreader.Views.Activities.MainActivity;
import com.fnsdev.rssreader.Views.Fragments.WebViewFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RssListViewAdapter extends RecyclerView.Adapter<RssListViewAdapter.RssViewHolder>{
    class RssViewHolder extends RecyclerView.ViewHolder {
        private final TextView feedTitleTextView;
        private final TextView feedPubDateTextView;
        private final TextView feedDescriptionTextView;
        private final CardView cardView;
        private final ImageView imageView;

        private RssViewHolder(View itemView) {
            super(itemView);
            feedTitleTextView = itemView.findViewById(R.id.feedTitle);
            feedPubDateTextView = itemView.findViewById(R.id.feedPubDate);
            feedDescriptionTextView = itemView.findViewById(R.id.feedDescription);
            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.thumbnail);
        }
    }

    private final LayoutInflater inflater;
    private List<Feed> feed;
    private Context context;


    public RssListViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public @NonNull RssViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_feed_item, parent, false);
        return new RssViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RssViewHolder holder, int position) {
        if(feed != null) {
            Feed current = feed.get(position);
            holder.feedTitleTextView.setText(current.title);
            holder.feedPubDateTextView.setText(current.pubDate.toString());
            holder.feedDescriptionTextView.setText(current.description);
            holder.cardView.setOnClickListener((View view) -> {
                WebViewViewModel feedViewModel = new ViewModelProvider((MainActivity) context).get(WebViewViewModel.class);
                feedViewModel.setLink(current.link);

                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                if(fragmentManager.findFragmentByTag("WebViewFragment") == null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.addToBackStack(null);
                    WebViewFragment fragment = new WebViewFragment();
                    transaction.replace(R.id.frameLayout, fragment, "WebViewFragment");
                    transaction.commit();
                }
            });
            if (current.mediaUrl == null) {
                holder.imageView.setVisibility(View.GONE);
            }
            else {
                Picasso.get()
                        .load(current.mediaUrl)
                        .resize(750, 750)
                        .centerCrop()
                        .into(holder.imageView);
            }
        }
    }


    public void setFeed(List<Feed> feed){
        this.feed = feed;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (feed != null)
            return feed.size();
        else return 0;
    }
}
