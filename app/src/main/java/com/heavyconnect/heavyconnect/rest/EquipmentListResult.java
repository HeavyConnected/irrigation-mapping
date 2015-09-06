package com.heavyconnect.heavyconnect.rest;

import com.heavyconnect.heavyconnect.entities.Equipment;

import java.util.ArrayList;

/**
 * RegisterResult class.
 */
public class EquipmentListResult {
    public static final int OK = 0;
    public static final int INVALID_REQUEST = 1;
    public static final int INVALID_INFO = 2;
    public static final int INVALID_USERNAME_OR_PASSWORD = 3;
    public static final int NEED_TO_ACTIVATE = 4;

    private boolean success = false;
    private int count;
    private Equipment[] results;

    private int code = OK;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public int getStatus() {
        return code;
    }

    public ArrayList<Equipment> getUserEquips() {
        if (code != OK || results == null)
            return null;

        ArrayList<Equipment> result = new ArrayList<Equipment>();
        for(int i = 0; i < results.length; i++)
            result.add(results[i]);

        return result;
    }
}
