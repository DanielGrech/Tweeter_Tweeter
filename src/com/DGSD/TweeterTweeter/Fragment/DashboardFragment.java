package com.DGSD.TweeterTweeter.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.R;
import org.w3c.dom.Text;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 7:30 PM
 * Description :
 */
public class DashboardFragment extends BaseFragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    public static DashboardFragment newInstance() {
        DashboardFragment f = new DashboardFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        TextView tv = new TextView(getActivity());
        tv.setText("Hello, World!");

        return tv;
    }
}
