package com.franktan.multithreadingblogs;

import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Created by frank.yitan on 12/04/2016.
 * CustomCallable is used for sending tasks to the thread pool. When a callable is submitted,
 * a Future object is returned, allowing the thread pool manager to stop the task.
 */
public class CustomCallable implements Callable {

    // Keep a weak reference to the CustomThreadPoolManager singleton object, so we can send a
    // message. Use of weak reference is not a must here because CustomThreadPoolManager lives
    // across the whole application lifecycle
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;

    @Override
    public Object call() throws Exception {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();

            // In real world project, you might do some blocking IO operation
            // In this example, I just let the thread sleep for 3 second
            Thread.sleep(3000);

            // After work is finished, send a message to CustomThreadPoolManager
            Message message = Util.createMessage(Util.MESSAGE_ID, "Thread " +
                    String.valueOf(Thread.currentThread().getId()) + " " +
                    String.valueOf(Thread.currentThread().getName()) + " completed");

            if(mCustomThreadPoolManagerWeakReference != null
                    && mCustomThreadPoolManagerWeakReference.get() != null) {

                mCustomThreadPoolManagerWeakReference.get().sendMessageToUiThread(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCustomThreadPoolManager(CustomThreadPoolManager customThreadPoolManager) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<CustomThreadPoolManager>(customThreadPoolManager);
    }
}
