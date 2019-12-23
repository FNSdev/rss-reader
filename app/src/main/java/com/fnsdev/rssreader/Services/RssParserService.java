package com.fnsdev.rssreader.Services;

import com.fnsdev.rssreader.Models.Entities.Feed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RssParserService {
    public static final int MAX_CACHED_FEED = 3;

    private static InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static Exception ParseRss(List<Feed> allFeed, String feedUrl) {
        Exception exception = null;
        try {
            URL url = new URL(feedUrl);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(getInputStream(url), "UTF_8");

            boolean insideItem = false;
            int eventType = parser.getEventType();
            String title = null, description = null, link = null, pubDate = null, mediaUrl = null;

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    }
                    else if (parser.getName().equalsIgnoreCase("title") && insideItem) {
                        title = parser.nextText();
                    }
                    else if (parser.getName().equalsIgnoreCase("link") && insideItem) {
                        link = parser.nextText();
                    }
                    else if (parser.getName().equalsIgnoreCase("pubdate") && insideItem) {
                        pubDate = parser.nextText();
                    }
                    else if (parser.getName().equalsIgnoreCase("description") && insideItem) {
                        description = parser.nextText();
                    }
                    else if (parser.getName().equalsIgnoreCase("media:thumbnail") && insideItem) {
                        mediaUrl = parser.getAttributeValue(null, "url");
                    }
                }
                else if (eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    Feed feed = new Feed();
                    feed.title = title;
                    feed.description = description;
                    feed.link = link;
                    feed.feedUrl = feedUrl;
                    feed.mediaUrl = mediaUrl;
                    try {
                        feed.pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH).parse(pubDate);
                    }
                    catch (ParseException e) {
                        exception = e;
                    }
                    allFeed.add(feed);
                }

                eventType = parser.next();
            }

        }
        catch (XmlPullParserException | IOException e) {
            exception = e;
        }
        catch (Exception e) {
            exception = e;
        }

        return exception;
    }
}
