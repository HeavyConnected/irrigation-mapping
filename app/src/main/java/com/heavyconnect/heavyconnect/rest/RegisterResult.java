package com.heavyconnect.heavyconnect.rest;

/**
 * RegisterResult class.
 */
public class RegisterResult {
    public static final int OK = 0;
    public static final int INVALID_REQUEST = 1;
    public static final int INVALID_INFO = 2;
    public static final int USER_ALREADY_EXISTS = 3;

    public String success;
    public String description;
    public int code = 0;

    /**
     * Get result status.
     *
     * @return Result code.
     */
    public int getStatus() {
        return code;
    }
}
