package com.heavyconnect.heavyconnect.rest;

import com.google.gson.annotations.SerializedName;
import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.entities.User;

/**
 * RegisterResult class.
 */
public class EquipmentDetailsResult extends Equipment {
    public static final int OK = 0;

    private boolean success = false;
    private int code = OK;

    /**
     * Get result status.
     *
     * @return the text.
     */
    public int getStatus() {
        return code;
    }


    public Equipment getData(){
        return this;
    }
}
