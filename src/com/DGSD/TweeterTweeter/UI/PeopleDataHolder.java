package com.DGSD.TweeterTweeter.UI;

import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 25/11/11 11:49 AM
 * Description : Dataholder for status list items
 */
public class PeopleDataHolder {
    private static final String TAG = PeopleDataHolder.class.getSimpleName();

    public long id;
    public String name;
    public String img;
    public WebImageView webimageview;

    public PeopleDataHolder(long i, String n, String im, WebImageView wiv) {
        id = i;
        name = n;
        img = im;
        webimageview = wiv;
    }
}
