package com.franktan.multithreadingblogs;

import android.os.Message;

/**
 * Created by Frank Tan on 3/04/2016.
 */
public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
