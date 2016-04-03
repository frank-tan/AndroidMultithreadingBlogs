package com.franktan.multithreadingblogs;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by Frank Tan on 3/04/2016.
 */
public class CustomHandlerThread extends HandlerThread {

    CustomHandler mHandler;
    // use weak reference to avoid activity being leaked
    private WeakReference<UiThreadCallback> mUiThreadCallback;

    public CustomHandlerThread(String name){
        super(name);
    }

    // Get a reference to worker thread's handler after looper is prepared
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new CustomHandler(getLooper());
    }

    // Used by UI thread to send message to worker thread's message queue
    public void addMessage(int message){
        if(mHandler != null) {
            mHandler.sendEmptyMessage(message);
        }
    }

    // The UiThreadCallback is used to send message to UI thread
    public void setUiThreadCallback(UiThreadCallback callback){
        this.mUiThreadCallback = new WeakReference<UiThreadCallback>(callback);
    }

    // Custom Handler. It pause the thread for some time and send a message back to UI Thread
    private class CustomHandler extends Handler {
        public CustomHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){}
                    if(mUiThreadCallback != null){
                        mUiThreadCallback.get().publishToUiThread(1);
                    }
                    break;
                case 2:
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e){}
                    if(mUiThreadCallback != null){
                        mUiThreadCallback.get().publishToUiThread(2);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
