package com.DGSD.TweeterTweeter.Utils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

/**
 * Author: Daniel Grech
 * Date: 4/12/11 3:14 PM
 * Description :
 */
public class UriList extends LinkedList<Uri> implements Parcelable {
    private static final String TAG = UriList.class.getSimpleName();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this);
    }
}
