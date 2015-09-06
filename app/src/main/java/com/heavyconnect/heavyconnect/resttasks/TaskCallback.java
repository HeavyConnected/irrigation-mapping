package com.heavyconnect.heavyconnect.resttasks;

/**
 * Created by andremenezes on 8/19/15.
 */
public interface TaskCallback {

    void onTaskFailed(int errorCode);
    void onTaskCompleted(Object result);
}
