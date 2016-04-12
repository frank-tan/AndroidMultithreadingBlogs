package com.franktan.multithreadingblogs;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Frank Tan on 3/04/2016.
 */
public class CustomHandlerThread extends HandlerThread {

    CustomHandler mHandler;
    // use weak reference to avoid activity being leaked
    private WeakReference<UiThreadCallback> mUiThreadCallback;

    public CustomHandlerThread(String name){
        super(name, android.os.Process.THREAD_PRIORITY_BACKGROUND);
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

    public void postRunnable(Runnable runnable){
        if(mHandler != null) {
            mHandler.post(runnable);
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
                        if(!Thread.interrupted() && mUiThreadCallback != null && mUiThreadCallback.get() != null){
                            Bundle bundle = new Bundle();
                            bundle.putString(Util.MESSAGE_TAG, "Thread " + String.valueOf(Thread.currentThread().getId()) + " completed");
                            Message message = new Message();
                            message.what = 1;
                            message.setData(bundle);
                            mUiThreadCallback.get().publishToUiThread(message);
                        }
                    } catch (InterruptedException e){
                        Log.e(Util.LOG_TAG,"HandlerThread interrupted");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
