package com.heavyconnect.heavyconnect.rest;

import com.google.gson.annotations.SerializedName;
import com.heavyconnect.heavyconnect.entities.Manager;

/**
 * This class represents the RegisterResult.
 */
public class LoginResult {
    public static final int OK = 0;
    public static final int INVALID_REQUEST = 1;
    public static final int INVALID_INFO = 2;
    public static final int INVALID_USERNAME_OR_PASSWORD = 3;
    public static final int NEED_TO_ACTIVATE = 4;

    private boolean success = false;
    private String username;
    private String firstName;
    private String lastName;

    @SerializedName("api-token")
    private String token;
    private int code = OK;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public int getStatus() {
        return code;
    }

    public Manager getUser() {
        if (code != OK)
            return null;

        Manager result = new Manager();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setUsername(username);
        result.setToken(token);

        return result;
    }
}
