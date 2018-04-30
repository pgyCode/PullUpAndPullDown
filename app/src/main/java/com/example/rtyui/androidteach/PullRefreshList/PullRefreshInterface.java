package com.example.rtyui.androidteach.PullRefreshList;

/**
 * Created by rtyui on 2018/4/27.
 */

public interface PullRefreshInterface {

    void beforeLoad_PullRefresh();
    boolean load_PullRefresh();
    void afterLoad_PullRefresh(boolean result);
}
