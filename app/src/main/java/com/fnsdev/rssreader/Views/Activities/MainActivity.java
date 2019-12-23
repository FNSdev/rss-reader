package com.fnsdev.rssreader.Views.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.fnsdev.rssreader.R;
import com.fnsdev.rssreader.Views.Fragments.RssListViewFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("RssListViewFragment") == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            RssListViewFragment fragment = new RssListViewFragment();
            transaction.replace(R.id.frameLayout, fragment, "RssListViewFragment");
            transaction.commit();
        }

        setContentView(R.layout.activity_main);
    }
}
