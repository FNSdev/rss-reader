package com.fnsdev.rssreader.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class WebViewViewModel extends AndroidViewModel {
    private String link;

    public WebViewViewModel (Application application) {
        super(application);
    }

    public String getLink() { return link; }
    public void setLink(String link) {
        this.link = link;
    }
}
