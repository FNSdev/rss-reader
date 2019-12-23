package com.fnsdev.rssreader.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fnsdev.rssreader.Models.Entities.Feed;
import com.fnsdev.rssreader.Models.Repositories.FeedRepository;

import java.util.List;

public class FeedViewModel extends AndroidViewModel {
    private FeedRepository feedRepository;
    private MutableLiveData<List<Feed>> allFeed = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public FeedViewModel (Application application) {
        super(application);
        feedRepository = new FeedRepository(application);
        isLoading.setValue(false);
    }

    public LiveData<List<Feed>> getAll() { return allFeed; }
    public List<Feed> selectAll(String feedUrl) { return feedRepository.selectAll(feedUrl); }
    public void setAllFeed(List<Feed> feed) { allFeed.setValue(feed); }

    public LiveData<Boolean> isLoading() { return isLoading; }
    public void setIsLoading(Boolean val) { isLoading.setValue(val); }

    public void saveFeed(Feed feed) {
        feedRepository.insert(feed);
    }

    public void deleteAll(String feedUrl) {
        feedRepository.deleteAll(feedUrl);
    }
}
