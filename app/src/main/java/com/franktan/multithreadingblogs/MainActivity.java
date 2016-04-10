package com.franktan.multithreadingblogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements UiThreadCallback {
    private CustomHandlerThread mHandlerThread;
    private UiHandler mUiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // handler for UI thread to receive message from worker thread
        mUiHandler = new UiHandler(Looper.getMainLooper(),this);

        // create and start a new worker thread
        mHandlerThread = new CustomHandlerThread("HandlerThread");
        mHandlerThread.setUiThreadCallback(this);
        mHandlerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clear the message queue of worker thread and stop the current task
        if(mHandlerThread != null){
            mHandlerThread.quit();
            mHandlerThread.interrupt();
        }
    }

    // onClick handler for SEND MESSAGE 1 button
    public void sendMsg1Clicked(View view){
        // add a message to worker thread's message queue
        mHandlerThread.addMessage(1);
    }

    // onClick handler for SEND MESSAGE 2 button
    public void sendMsg2Clicked(View view){
        // add a message to worker thread's message queue
        mHandlerThread.addMessage(2);
    }

    // receive message from worker thread
    @Override
    public void publishToUiThread(int message) {
        // add the message from worker thread to UI thread's message queue
        if(mUiHandler != null){
            mUiHandler.sendEmptyMessage(message);
        }
    }

    // UI handler class, declared as static so it doesn't have implicit
    // reference to activity context. This is to avoid memory leak.
    private static class UiHandler extends Handler {
        private WeakReference<Context> mWeakRefContext;

        public UiHandler(Looper looper, Context context) {
            super(looper);
            this.mWeakRefContext = new WeakReference<Context>(context);
        }

        // This method will run on UI thread
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        Toast.makeText(mWeakRefContext.get(),"Message 1 has been processed",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        Toast.makeText(mWeakRefContext.get(),"Message 2 has been processed",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
