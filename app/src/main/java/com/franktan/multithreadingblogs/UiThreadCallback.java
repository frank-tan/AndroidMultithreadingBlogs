package com.franktan.multithreadingblogs;

import android.os.Message;

/**
 * Created by Frank Tan on 3/04/2016.
 *
 * An interface for worker threads to send messages to the UI thread.
 * MainActivity implemented this Interface in this app.
 */
public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
