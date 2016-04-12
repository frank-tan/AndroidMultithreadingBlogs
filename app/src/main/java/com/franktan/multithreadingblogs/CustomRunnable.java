package com.franktan.multithreadingblogs;

import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by tan on 11/04/2016.
 * CustomRunnable run some lengthy blocking code on a worker thread and notify UI thread when the
 * work is done
 */
public class CustomRunnable implements Runnable {
    // Keep a weak reference to ui callback, so we can send a message to the UI thread
    // Use weak reference to avoid leaking activity object
    private WeakReference<UiThreadCallback> uiThreadCallbackWeakReference;

    @Override
    public void run() {
        try {
            // Before running some lengthy and blocking work, check if the thread has been interrupted
            if (Thread.interrupted()) throw new InterruptedException();

            // In real world project, you might do some blocking IO operation
            // In this example, I just let the thread sleep for 3 second
            Thread.sleep(3000);

            // After work is finished, send a message to UI thread
            if(uiThreadCallbackWeakReference != null && uiThreadCallbackWeakReference.get() != null) {
                Message message = Util.createMessage(1,
                        "Thread " + String.valueOf(Thread.currentThread().getId()) + " completed");
                uiThreadCallbackWeakReference.get().publishToUiThread(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setUiThreadCallback(UiThreadCallback uiThreadCallback) {
        this.uiThreadCallbackWeakReference = new WeakReference<UiThreadCallback>(uiThreadCallback);
    }
}
