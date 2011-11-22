package com.DGSD.TweeterTweeter.Fragment;

public interface UpdateableFragment {

    public void startRefresh();

    public void onRefreshData();

    public void onRefreshNoData();

    public void onRefreshError();
}