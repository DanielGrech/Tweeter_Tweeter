package com.DGSD.TweeterTweeter.UI;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.DGSD.TweeterTweeter.Utils.UriList;
import com.DGSD.TweeterTweeter.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Daniel Grech
 * Date: 4/12/11 2:14 PM
 * Description :
 */
public class GalleryAdapter extends BaseAdapter {
    private static final String TAG = GalleryAdapter.class.getSimpleName();

    private Activity mActivity;

    private UriList mUris;

    public GalleryAdapter(Activity a) {
        mActivity = a;

        mUris = new UriList();
    }

    public int getCount() {
        return mUris == null ? 0 : mUris.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void addToAdapter(Uri uri) {
        mUris.add(uri);
        this.notifyDataSetChanged();
    }

    public void addAll(Collection<Uri> uris) {
        if(uris != null) {
            mUris.addAll(uris);
        }
    }

    public UriList getUris() {
        return mUris;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mActivity);
            imageView.setLayoutParams(new Gallery.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            imageView = (ImageView) convertView;
        }

        try {
            //imageView.setImageURI(mUris.get(position));
            imageView.setImageBitmap(Utils.decodeFile(10, new File(Utils.getPath(mActivity, mUris.get(position)))));
            imageView.setTag(mUris.get(position).toString());
        } catch (IOException e) {
            Log.e(TAG, "Error in getView()", e);
        }

        return imageView;
    }
}