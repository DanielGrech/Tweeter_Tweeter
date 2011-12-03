package com.DGSD.TweeterTweeter.UI;

import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 25/11/11 11:49 AM
 * Description : Dataholder for status list items
 */
public class StatusDataHolder {
    private static final String TAG = StatusDataHolder.class.getSimpleName();

    public long id;
    public String user;
    public String text;
    public String img;
    public WebImageView webimageview;

    public StatusDataHolder(long i, String u, String t, String im, WebImageView wiv) {
        id = i;
        user = u;
        text = t;
        img = im;
        webimageview = wiv;
    }
}
