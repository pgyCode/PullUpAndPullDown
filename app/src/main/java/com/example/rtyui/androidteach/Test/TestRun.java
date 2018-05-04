package com.example.rtyui.androidteach.Test;

/**
 * Created by rtyui on 2018/4/30.
 */

public class TestRun implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
