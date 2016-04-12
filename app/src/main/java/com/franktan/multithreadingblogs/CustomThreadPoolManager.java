package com.franktan.multithreadingblogs;

import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tan on 11/04/2016.
 */
public class CustomThreadPoolManager {

    private static CustomThreadPoolManager sInstance = null;
    private static final int THREAD_POOL_SIZE = 3;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService mExecutorService;
    private final BlockingQueue<Runnable> mTaskQueue;

    private WeakReference<UiThreadCallback> uiThreadCallbackWeakReference;

    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new CustomThreadPoolManager();
    }

    // Made constructor private to avoid the class being initiated from outside
    private CustomThreadPoolManager() {
        // initialize a queue for the thread pool. New tasks will be added to this queue
        mTaskQueue = new LinkedBlockingQueue<Runnable>();

        /*
            TODO: You can choose between a fixed sized thread pool and a dynamic pool
            TODO: Comment one and uncomment another to see the difference.
         */
        //mExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new BackgroundThreadFactory());
        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mTaskQueue, new BackgroundThreadFactory());
    }

    public static CustomThreadPoolManager getsInstance() {
        return sInstance;
    }

    // Add a callable to the queue, which will be executed by available thread in the pool
    public void addCallable(Callable callable){
        Future future = mExecutorService.submit(callable);
    }

    // TODO: 12/04/2016
    public void clearQueue() {
        mTaskQueue.clear();
    }

    // Keep a weak reference to the UI thread, so we can send messages to the UI thread
    public void setUiThreadCallback(UiThreadCallback uiThreadCallback) {
        this.uiThreadCallbackWeakReference = new WeakReference<UiThreadCallback>(uiThreadCallback);
    }

    // Pass the message to the UI thread
    public void passMessageToUiThread(Message message){
        if(uiThreadCallbackWeakReference != null && uiThreadCallbackWeakReference.get() != null) {
            uiThreadCallbackWeakReference.get().publishToUiThread(message);
        }
    }

    /* A ThreadFactory implementation which create new threads for the thread pool.
       The threads created is set to background priority, so it does not compete with the UI thread.
     */
    private static class BackgroundThreadFactory implements ThreadFactory {
        private static int sTag = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(Util.LOG_TAG, thread.getName() + " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }
}
