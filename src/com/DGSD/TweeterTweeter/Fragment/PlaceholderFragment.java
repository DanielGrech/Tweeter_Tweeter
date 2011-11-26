package com.DGSD.TweeterTweeter.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 26/11/11 10:20 PM
 * Description :
 */
public class PlaceholderFragment extends BaseFragment {
    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        TextView tv = new TextView(getActivity());
        tv.setText("Hello, World!");

        return tv;
    }
}
