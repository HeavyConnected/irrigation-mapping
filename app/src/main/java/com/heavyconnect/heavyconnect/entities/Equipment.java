package com.heavyconnect.heavyconnect.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by felipepx on 9/2/15.
 */
public class Equipment {

    public static final int STATUS_OK = 0;
    public static final int STATUS_SERVICE = 1;
    public static final int STATUS_BROKEN = 2;

    private String name;

    private int status;

    public Equipment(String name, int status){
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
