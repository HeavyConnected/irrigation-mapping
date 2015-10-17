package com.heavyconnect.heavyconnect.rest;

import com.google.gson.annotations.SerializedName;
import com.heavyconnect.heavyconnect.entities.IrrigationFields;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationFieldsDetailsResult extends IrrigationFields {

    public static final int OK = 0;
    private String fieldName;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("pipe_length")
    private double pipeLength;

    @SerializedName("pipe_depth")
    private double pipeDepth;
    private boolean success = false;
    private int code = OK;

    // get result status

    public int getStatus() {return code;}

    public IrrigationFields getData() {return this;}
}
