package com.fnsdev.rssreader.Views.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fnsdev.rssreader.Models.Entities.Feed;
import com.fnsdev.rssreader.R;
import com.fnsdev.rssreader.Services.NetworkIntentService;
import com.fnsdev.rssreader.Services.RssParserService;
import com.fnsdev.rssreader.ViewModels.FeedViewModel;
import com.fnsdev.rssreader.Views.Adapters.RssListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RssListViewFragment extends Fragment {
    private SearchView searchView;
    private TextView networkTextView;
    private ProgressBar progressBar;

    private static FeedViewModel feedViewModel;
    private GetRssFeedTask task;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rss_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        networkTextView = view.findViewById(R.id.internet);
        searchView = view.findViewById(R.id.search);
        searchView.setQuery("https://www.wired.com/feed/category/gear/latest/rss", false);

        progressBar = view.findViewById(R.id.progressBar);

        Handler handler = new Handler((Message msg) -> {
            Bundle data = msg.getData();
            boolean isConnected = data.getBoolean("isConnected");
            if(isConnected) {
                networkTextView.setText("Connected");
            }
            else {
                networkTextView.setText("No Internet");
            }
            return true;
        });

        Intent intent = new Intent(getActivity(), NetworkIntentService.class);
        intent.putExtra("messenger", new Messenger(handler));
        getActivity().startService(intent);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RssListViewAdapter adapter = new RssListViewAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        feedViewModel = new ViewModelProvider(getActivity()).get(FeedViewModel.class);
        feedViewModel.isLoading().observe(getViewLifecycleOwner(), (Boolean isLoading) -> {
                if(isLoading) {
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        );

        feedViewModel.getAll().observe(getViewLifecycleOwner(), (List<Feed> feed) ->
                adapter.setFeed(feed)
        );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isNetworkAvailable(getContext())) {
                    task = new GetRssFeedTask(getContext());
                    task.execute(query);
                }
                else {
                    List<Feed> feeds = feedViewModel.selectAll(query);
                    for(Feed f : feeds) {
                        System.out.println("TITLE = " + f.title);
                    }
                    feedViewModel.setAllFeed(feedViewModel.selectAll(query));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(task != null) {
            task.cancel(false);
        }
        Intent service = new Intent(getActivity(), NetworkIntentService.class);
        getActivity().stopService(service);
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private static class GetRssFeedTask extends AsyncTask<String, Void, Exception> {
        private List<Feed> allFeed = new ArrayList<>();
        private String feedUrl;
        private Context context;

        public GetRssFeedTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            feedViewModel.setIsLoading(true);
        }

        @Override
        protected Exception doInBackground(String... params) {
            feedUrl = params[0];
            return RssParserService.ParseRss(allFeed, feedUrl);
        }

        @Override
        protected void onPostExecute(Exception exception) {
            super.onPostExecute(exception);

            if (exception != null) {
                feedViewModel.setIsLoading(false);
                Toast toast = Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            feedViewModel.setAllFeed(allFeed);
            feedViewModel.deleteAll(feedUrl);

            int i = 1;
            for(Feed feed: allFeed) {
                feedViewModel.saveFeed(feed);
                // System.out.println("Saved!!! " + feed.title);
                if(i == RssParserService.MAX_CACHED_FEED) {
                    break;
                }
                i++;
            }

            feedViewModel.setIsLoading(false);
        }
    }
}
