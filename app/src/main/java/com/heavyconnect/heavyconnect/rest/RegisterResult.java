package com.heavyconnect.heavyconnect.rest;

/**
 * RegisterResult class.
 */
public class RegisterResult {
    public static final String OK = "ok";
    public static final String ERROR = "error";
    public String description;
    public String error;
    public String errors;
    private Data data;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public String getStatus() {
        if (this.error != null) {
            return ERROR;
        } else if (this.errors != null) {
            return ERROR;
        } else {
            return OK;
        }
    }

    /**
     * Get terms text.
     *
     * @return the breeds array.
     */
    public String getTermsText() {
        String result = description;
        if(result != null)
            return result;

        if(data != null)
            return data.description;

        return "";
    }

    /**
     * Data class
     */
    private class Data{
        private String description;
    }
}
