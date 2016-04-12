package com.franktan.multithreadingblogs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by Frank Tan on 3/04/2016.
 * The launching activity for the app
 */
public class MainActivity extends AppCompatActivity implements UiThreadCallback {
    // A worker thread which has the same lifecycle with the activity
    // It is created and started in Activity onStart and stopped in Activity onStop
    private CustomHandlerThread mHandlerThread;

    // The handler for the UI thread. Used for handling messages from worker threads.
    private UiHandler mUiHandler;

    // A text view to show messages sent from work threads
    private TextView mDisplayTextView;

    // A thread pool manager
    // It is a static singleton instance by design and will survive activity lifecycle
    private CustomThreadPoolManager mCustomThreadPoolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayTextView = (TextView)findViewById(R.id.display);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize the handler for UI thread to handle message from worker threads
        mUiHandler = new UiHandler(Looper.getMainLooper(), mDisplayTextView);

        // create and start a new HandlerThread worker thread
        mHandlerThread = new CustomHandlerThread("HandlerThread");
        mHandlerThread.setUiThreadCallback(this);
        mHandlerThread.start();

        // get the thread pool manager instance
        mCustomThreadPoolManager = CustomThreadPoolManager.getsInstance();
        // CustomThreadPoolManager stores activity as a weak reference. No need to unregister.
        mCustomThreadPoolManager.setUiThreadCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // clear the message queue of HandlerThread worker thread and stop the current task
        if(mHandlerThread != null){
            mHandlerThread.quit();
            mHandlerThread.interrupt();
        }
    }

    // onClick handler for Send Runnable button
    public void sendRunnableToHandlerThread(View view){
        // send a runnable to run on the HandlerThread
        CustomRunnable runnable = new CustomRunnable();
        runnable.setUiThreadCallback(this);
        mHandlerThread.postRunnable(runnable);
    }

    // onClick handler for Send Message button
    public void sendMessageToHandlerThread(View view){
        // add a message to worker thread's message queue
        mHandlerThread.addMessage(1);
    }

    // onClick handler for Send 4 Tasks button
    public void send4tasksToThreadPool(View view) {
        for(int i = 0; i < 4; i++) {
            CustomCallable callable = new CustomCallable();
            callable.setCustomThreadPoolManager(mCustomThreadPoolManager);
            mCustomThreadPoolManager.addCallable(callable);
        }
    }

    // onClick handler for Send 8 Tasks button
    public void send8TasksToThreadPool(View view) {
        for(int i = 0; i < 8; i++) {
            CustomCallable callable = new CustomCallable();
            callable.setCustomThreadPoolManager(mCustomThreadPoolManager);
            mCustomThreadPoolManager.addCallable(callable);
        }
    }

    // onClick handler for Stop All Thread button
    public void cancelAllTasksInThreadPool(View view) {
        mCustomThreadPoolManager.cancelAllTasks();
    }

    // onClick handler for Clear Messages button
    public void clearDisplay(View view) {
        mDisplayTextView.setText("");
    }

    // Send message from worker thread to the UI thread
    @Override
    public void publishToUiThread(Message message) {
        // add the message from worker thread to UI thread's message queue
        if(mUiHandler != null){
            mUiHandler.sendMessage(message);
        }
    }

    // UI handler class, declared as static so it doesn't have implicit
    // reference to activity context. This helps to avoid memory leak.
    private static class UiHandler extends Handler {
        private WeakReference<TextView> mWeakRefDisplay;

        public UiHandler(Looper looper, TextView display) {
            super(looper);
            this.mWeakRefDisplay = new WeakReference<TextView>(display);
        }

        // This method will run on UI thread
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                // Our communication protocol for passing a string to the UI thread
                case Util.MESSAGE_ID:
                    Bundle bundle = msg.getData();
                    String messsageText = bundle.getString(Util.MESSAGE_BODY, Util.EMPTY_MESSAGE);
                    if(mWeakRefDisplay != null && mWeakRefDisplay.get() != null)
                        mWeakRefDisplay.get().append(Util.getReadableTime() + " " + messsageText + "\n");
                    break;
                default:
                    break;
            }
        }
    }
}
