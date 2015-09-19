package com.heavyconnect.heavyconnect.rest;

/**
 * This class represents the RegisterResult.
 */
public class RegisterResult {
    public static final int OK = 0;
    public static final int INVALID_REQUEST = 1;
    public static final int INVALID_INFO = 2;
    public static final int USER_ALREADY_EXISTS = 3;

    private String success;
    private String description;
    private int code = OK;

    /**
     * Get result status.
     *
     * @return Result code.
     */
    public int getStatus() {
        return code;
    }
}
