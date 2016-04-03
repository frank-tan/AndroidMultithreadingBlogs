package com.franktan.multithreadingblogs;

/**
 * Created by Frank Tan on 3/04/2016.
 */
public interface UiThreadCallback {
    void publishToUiThread(int message);
}
