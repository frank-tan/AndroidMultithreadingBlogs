package com.franktan.multithreadingblogs;

import java.lang.ref.WeakReference;

/**
 * Created by tan on 11/04/2016.
 */
public class CustomRunnable implements Runnable {
    private WeakReference<UiThreadCallback> uiThreadCallbackWeakReference;

    @Override
    public void run() {
        try {
            if (Thread.interrupted()) throw new InterruptedException();
            Thread.sleep(1000);
            if (Thread.interrupted()) throw new InterruptedException();
            Thread.sleep(1000);
            if (Thread.interrupted()) throw new InterruptedException();
            Thread.sleep(1000);
            if (Thread.interrupted()) throw new InterruptedException();
            Thread.sleep(1000);
            if (Thread.interrupted()) throw new InterruptedException();

            if(uiThreadCallbackWeakReference != null && uiThreadCallbackWeakReference.get() != null) {
                uiThreadCallbackWeakReference.get().publishToUiThread(3);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setUiThreadCallback(UiThreadCallback uiThreadCallback) {
        this.uiThreadCallbackWeakReference = new WeakReference<UiThreadCallback>(uiThreadCallback);
    }
}
