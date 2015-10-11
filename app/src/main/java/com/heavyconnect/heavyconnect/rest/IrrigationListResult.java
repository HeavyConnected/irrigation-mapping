package com.heavyconnect.heavyconnect.rest;

import com.heavyconnect.heavyconnect.entities.IrrigationFields;

import java.util.ArrayList;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationListResult {

    // copied from EquipmentListResult

    public static final int OK = 0;
    public static final int INVALID_REQUEST = 1;
    public static final int INVALID_INFO = 2;
    public static final int INVALID_USERNAME_OR_PASSWORD = 3;
    public static final int NEED_TO_ACTIVATE = 4;

    private boolean success = false;
    private int count;
    private IrrigationFields[] results;

    private int code = OK;

    // Gets result status

    public int getStatus() {return code;}

    public ArrayList<IrrigationFields> getUserFields() {
        if(code != OK || results == null) {
            return null;
        }

        ArrayList<IrrigationFields> result = new ArrayList<>();
        for(int i = 0; i < results.length; i++)
            result.add(results[i]);

        return result;
    }
}
