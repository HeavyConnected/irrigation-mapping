package com.heavyconnect.heavyconnect.resttasks;

/**
 * Created by andremenezes on 8/19/15.
 */
public interface TaskCallback {
    void error(int code);
    void done(Object result);
}
