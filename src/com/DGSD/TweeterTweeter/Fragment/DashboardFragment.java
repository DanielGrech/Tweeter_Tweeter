package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.DGSD.TweeterTweeter.Activity.DashboardChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.FavouritesChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.MainChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.PeopleChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.Phone.NewTweetActivity;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 7:30 PM
 * Description :
 */
public class DashboardFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = DashboardFragment.class.getSimpleName();

    public static DashboardFragment newInstance() {
        DashboardFragment f = new DashboardFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        v.findViewById(R.id.btn_dashboard_timeline).setOnClickListener(this);

        v.findViewById(R.id.btn_dashboard_mentions).setOnClickListener(this);

        v.findViewById(R.id.btn_dashboard_dm).setOnClickListener(this);

        v.findViewById(R.id.btn_dashboard_favourites).setOnClickListener(this);

        v.findViewById(R.id.btn_dashboard_following).setOnClickListener(this);

        v.findViewById(R.id.btn_dashboard_trends).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_dashboard_timeline: {
                Intent i = new Intent(mActivity, MainChoiceActivity.class);
                i.putExtra(MainChoiceActivity.EXTRA.SHOW_SCREEN, MainChoiceActivity.EXTRA.HOME_TIMELINE);
                startActivity(i);
                break;
            }
            case R.id.btn_dashboard_mentions: {
                Intent i = new Intent(mActivity, MainChoiceActivity.class);
                i.putExtra(MainChoiceActivity.EXTRA.SHOW_SCREEN, MainChoiceActivity.EXTRA.MENTIONS);
                startActivity(i);
                break;
            }

            case R.id.btn_dashboard_dm: {
                Intent i = new Intent(mActivity, MainChoiceActivity.class);
                i.putExtra(MainChoiceActivity.EXTRA.SHOW_SCREEN, MainChoiceActivity.EXTRA.DM);
                startActivity(i);
                break;
            }

            case R.id.btn_dashboard_favourites: {
                Intent i = new Intent(mActivity, FavouritesChoiceActivity.class);
                startActivity(i);
                break;
            }

            case R.id.btn_dashboard_following: {
                Intent i = new Intent(mActivity, PeopleChoiceActivity.class);
                startActivity(i);
                break;
            }

            case R.id.btn_dashboard_trends: {

                break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater) {
        menu.add("New Tweet")
                .setIcon(R.drawable.ic_menu_compose)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("About")
                .setIcon(R.drawable.ic_menu_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Search")
                .setIcon(R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MenuRes.ABOUT: {

                return true;
            }

            case MenuRes.NEW_TWEET: {
                Intent i = new Intent(getActivity(), NewTweetActivity.class);
                startActivity(i);
                return true;
            }

            case MenuRes.SEARCH: {

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private static class MenuRes {
        public static final int NEW_TWEET = 0;

        public static final int SEARCH = 1;

        public static final int ABOUT = 3;

    }
}
