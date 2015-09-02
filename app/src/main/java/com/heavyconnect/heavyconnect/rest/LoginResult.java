package com.heavyconnect.heavyconnect.rest;

import com.heavyconnect.heavyconnect.entities.User;

/**
 * RegisterResult class.
 */
public class LoginResult {
    public static final int OK = 0;

    private boolean success = false;
    private String username;
    private String name;
    private int code = OK;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public int getStatus() {
        return code;
    }

    public User getUser() {
        if (code != OK)
            return null;

        User result = new User();
        result.setName(name);
        result.setUsername(username);

        return result;
    }
}
