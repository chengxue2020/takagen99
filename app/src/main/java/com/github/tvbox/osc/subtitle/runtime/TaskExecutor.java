package com.github.tvbox.osc.subtitle.runtime;

/**
 * @author AveryZhong.
 */

public abstract class TaskExecutor {

    public abstract void executeOnDeskIO(Runnable task);

    public void executeOnMainThread(Runnable task) {
        if (isMainThread()) {
            task.run();
        } else {
            postToMainThread(task);
        }
    }

    public abstract void postToMainThread(Runnable task);

    public abstract boolean isMainThread();

}