package com.example.rtyui.androidteach.PullRefreshPushLoadList;

/**
 * Created by rtyui on 2018/4/27.
 */

public interface PushLoadInterface {

    void beforeLoad_PushLoad();
    boolean load_PushLoad();
    void afterLoad_PushLoad(boolean result);
}
