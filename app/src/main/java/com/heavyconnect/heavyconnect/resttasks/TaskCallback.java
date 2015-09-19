package com.heavyconnect.heavyconnect.resttasks;

/**
 * This class represents all rest tasks callbacks.
 */
public interface TaskCallback {

    /**
     * This method is called when a task fails.
     * @param errorCode - Error code.
     */
    void onTaskFailed(int errorCode);

    /**
     * This method is called when a task was successfully completed.
     * @param result - Task result.
     */
    void onTaskCompleted(Object result);
}
