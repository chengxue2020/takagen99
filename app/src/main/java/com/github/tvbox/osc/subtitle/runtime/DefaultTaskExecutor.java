package com.github.tvbox.osc.subtitle.runtime;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author AveryZhong.
 */

public class DefaultTaskExecutor extends TaskExecutor {

    @Nullable
    private Handler mMainHandler;
    private final Object mLock = new Object();
    private final ExecutorService mDeskIO = Executors.newFixedThreadPool(3);

    @Override
    public void executeOnDeskIO(final Runnable task) {
        mDeskIO.execute(task);
    }

    @Override
    public void postToMainThread(final Runnable task) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                mMainHandler = new Handler(Looper.getMainLooper());
            }
        }
        mMainHandler.post(task);
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
