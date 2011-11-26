package com.DGSD.TweeterTweeter.Fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 26/11/11 6:03 PM
 * Description :
 */
public class NewTweetFragment extends DialogFragment {
    private static final String TAG = NewTweetFragment.class.getSimpleName();

    public static NewTweetFragment newInstance() {
        final NewTweetFragment f = new NewTweetFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MenuRes.IMG, 0, "Images")
                .setIcon(R.drawable.ic_menu_camera)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MenuRes.LOCATION, 0, "Location")
                .setIcon(R.drawable.ic_menu_mylocation)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MenuRes.CONTACTS, 0, "Add Contact")
                .setIcon(R.drawable.ic_menu_add_contact)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MenuRes.SHORTEN_URL, 0, "Shorten Urls")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(0, MenuRes.SUBMIT, 0, "Send")
                .setIcon(R.drawable.ic_menu_send)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    private static class MenuRes {
        public static final int SUBMIT = 0;

        public static final int LOCATION = 1;

        public static final int IMG = 2;

        public static final int SHORTEN_URL = 2;

        public static final int CONTACTS = 2;

    }
}
