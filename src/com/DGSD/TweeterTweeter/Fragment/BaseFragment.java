package com.DGSD.TweeterTweeter.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.DGSD.TweeterTweeter.Receiver.PortableReceiver;
import com.DGSD.TweeterTweeter.Service.DownloadService;
import com.DGSD.TweeterTweeter.UI.QuickPopup;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 5:49 PM
 * Description: Holds common properties for all fragments
 */
public abstract class BaseFragment extends DialogFragment implements UpdateableFragment{
    private static final String TAG = BaseFragment.class.getSimpleName();

    protected OnRefreshListener mRefreshListener;

    private static final String BROADCAST_RECEIVE_DATA = "com.DGSD.TweeterTweeter.RECEIVE_DATA";

    protected PortableReceiver mReceiver;

    protected FragmentActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FragmentActivity) getActivity();

        mReceiver = new PortableReceiver();

        mReceiver.setReceiver(new PortableReceiver.Receiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(DownloadService.Result.DATA)) {
                    Log.v(TAG, "Data Received");
                    onRefreshData();
                    if(mRefreshListener != null) {
                        mRefreshListener.onRefreshData();
                    } else {
                        Log.v(TAG, "Refresh Listener was null");
                    }
                }
                else if(intent.getAction().equals(DownloadService.Result.NO_DATA)) {
                    Log.v(TAG, "No Data Received");
                    onRefreshNoData();
                    if(mRefreshListener != null) {
                        mRefreshListener.onRefreshNoData();
                    } else {
                        Log.v(TAG, "Refresh Listener was null");
                    }
                }
                else if(intent.getAction().equals(DownloadService.Result.ERROR)) {
                    Log.v(TAG, "Error Received");
                    onRefreshError();
                    if(mRefreshListener != null) {
                        mRefreshListener.onRefreshError();
                    } else {
                        Log.v(TAG, "Refresh Listener was null");
                    }
                }
                else {
                    Log.w(TAG, "Received Mystery Intent: " + intent.getAction());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");

        // Register the receiver
        getActivity().registerReceiver(mReceiver, new IntentFilter(DownloadService.Result.DATA),
                BROADCAST_RECEIVE_DATA, null);

        getActivity().registerReceiver(mReceiver, new IntentFilter(DownloadService.Result.NO_DATA),
                BROADCAST_RECEIVE_DATA, null);

        getActivity().registerReceiver(mReceiver, new IntentFilter(DownloadService.Result.ERROR),
                BROADCAST_RECEIVE_DATA, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
        getActivity().unregisterReceiver(mReceiver);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    @Override
    public void startRefresh() {
        //Default implementation
        Log.w(TAG, "onStartRefresh() was not overridden");
    }

    @Override
    public void onRefreshData() {
        //Default implementation
        Log.w(TAG, "onRefreshData() was not overridden");
    }

    @Override
    public void onRefreshNoData() {
        //Default implementation
        Log.w(TAG, "onRefreshNoData() was not overridden");
    }

    @Override
    public void onRefreshError() {
        //Default implementation
        Log.w(TAG, "onRefreshError() was not overridden");
    }

    public interface OnRefreshListener {
        public void onStartRefresh();
        public void onRefreshData();
        public void onRefreshNoData();
        public void onRefreshError();
    }

    public static class ActionModeItemId {
        public static final int RETWEET = 0;
        public static final int REPLY = 1;
        public static final int FAVOURITE = 2;
        public static final int SHARE = 3;

    }
}
