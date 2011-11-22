package com.DGSD.TweeterTweeter.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.R;
import twitter4j.*;

import java.util.ArrayList;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 9:56 AM
 * Description : Shows the current daily trends while the user signs in
 *
 * TODO: Need to handle item clicks..
 */
public class LoginFragment extends DialogFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private ListView mListView;

    private TrendsLoader mLoader;

    private FragmentActivity mActivity;

    public static LoginFragment newInstance() {
        LoginFragment f = new LoginFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mActivity = (FragmentActivity) getActivity();

        mLoader = new TrendsLoader(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mListView = (ListView) v.findViewById(R.id.trends_list);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mLoader == null) {
            Log.v(TAG, "onActivityCreated(): Loader was null. Getting a new one");

            mLoader = new TrendsLoader(mActivity);

        } else {
            Log.v(TAG, "onActivityCreated(): Loader was not null. Restoring trends");
        }

        //Restore any previous trends
        if(savedInstanceState != null) {
            mLoader.restoreTrends(savedInstanceState.getStringArray(TrendsLoader.TRENDS));
        }

        mLoader.startLoading();
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        if(mLoader.getTrends() != null) {
            out.putStringArray(TrendsLoader.TRENDS, mLoader.getTrends());
        }
    }

    private class TrendsLoader extends AsyncTaskLoader<String[]> {
        private String[] mResults;

        private Twitter mTwitter;

        public static final String TRENDS = "_trends";

        public TrendsLoader(Context c) {
            super(c);

            mTwitter = new TwitterFactory().getInstance();
        }

        @Override
        public String[] loadInBackground() {
            Log.d(TAG, "loadInBackground()");

            try {
                Trend[] t = mTwitter.getDailyTrends().get(0).getTrends();

                mResults = new String[t.length];
                for(int i = 0, size = t.length; i < size; i++) {
                    mResults[i] = t[i].getName();
                }

                return mResults;
            } catch(TwitterException e) {
                Log.e(TAG, "Error loading trends", e);
                return null;
            } catch(Exception e) {
                Log.e(TAG, "Unknown exception", e);
                return null;
            }
        }

        @Override
        public void deliverResult(String[] trends) {
            Log.d(TAG, "deliverResults()");

            //Hide the progress bar
            mActivity.setProgressBarIndeterminateVisibility(Boolean.FALSE);

            if(isStarted() && trends != null) {
                mResults = trends;

                mListView.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, trends));

                super.deliverResult(trends);

            } else {
                Log.e(TAG, "Loader not started");
            }
        }

        @Override
        protected void onStopLoading() {
            //Attempt to cancel the load
            cancelLoad();
        }

        @Override
        protected void onStartLoading() {
            Log.v(TAG, "onStartLoading()");

            //Show the progress bar
            mActivity.setProgressBarIndeterminateVisibility(Boolean.TRUE);

            if(mResults != null) {
                //We have results, lets use them!
                Log.v(TAG, "Using old results");
                deliverResult(mResults);
                return;
            } else {
                Log.v(TAG, "Getting new results");

                //Start the load
                forceLoad();
            }

        }

        @Override
        protected void onReset() {
            super.onReset();

            mResults = null;
        }

        public String[] getTrends() {
            return mResults;
        }

        public void restoreTrends(String[] t) {
            mResults = t;
        }
    }

}
