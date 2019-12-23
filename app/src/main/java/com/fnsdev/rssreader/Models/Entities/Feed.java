package com.fnsdev.rssreader.Models.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "feed", indices = {@Index(value = "id", unique = true)})
public class Feed {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "media_url")
    public String mediaUrl;

    @ColumnInfo(name = "pub_date")
    public Date pubDate;

    @ColumnInfo(name = "feed_url")
    public String feedUrl;
}
