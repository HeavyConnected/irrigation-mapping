package com.heavyconnect.heavyconnect.rest;

import com.heavyconnect.heavyconnect.User;

/**
 * RegisterResult class.
 */
public class LoginResult {
    public String error;
    public static final String OK = "ok";
    private User data;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public String getStatus() {
        if (this.error != null) {
            return error;
        } else {
            return OK;
        }
    }

    public User getUser() {
        return data;
    }
}
