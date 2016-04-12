package com.franktan.multithreadingblogs;

import android.os.Bundle;
import android.os.Message;

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
                Bundle bundle = new Bundle();
                bundle.putString(Util.MESSAGE_TAG, "Thread " + String.valueOf(Thread.currentThread().getId()) + " completed");
                Message message = new Message();
                message.what = 1;
                message.setData(bundle);
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
