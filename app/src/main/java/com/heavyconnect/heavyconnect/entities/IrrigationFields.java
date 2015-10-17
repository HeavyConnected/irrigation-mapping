package com.heavyconnect.heavyconnect.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationFields implements Serializable {


    private int id;
    private int manager;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("pipe_length")
    private double pipeLength;

    @SerializedName("pipe_depth")
    private double pipeDepth;

    @SerializedName("pipe_row")
    private int pipeRow;

    public IrrigationFields() {}

    public IrrigationFields(String fieldName, double longitude, double latitude,
                            double pipeLength, double pipeDepth, int pipeRow) {
        this.fieldName = fieldName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pipeLength = pipeLength;
        this.pipeDepth = pipeDepth;
        this.pipeRow = pipeRow;
    }

    // setters

    public void setId(int Id) {this.id = Id;}

    public void setFieldName(String fieldName) {this.fieldName = fieldName;}

    public void setLongitude(double longitude) {this.longitude = longitude;}

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public void setPipeLength(double pipeLength) {this.pipeLength = pipeLength;}

    public void setPipeDepth(double pipeDepth) {this.pipeDepth = pipeDepth;}

    public void setPipeRow(int pipeRow) {this.pipeRow = pipeRow;}

    public void setManager(int manager) {this.manager = manager;}

    // getters

    public int getId() {return id;}

    public String getFieldName() {return fieldName;}

    public double getLongitude() {return longitude;}

    public double getLatitude() {return  latitude;}

    public double getPipeLength() {return pipeLength;}

    public double getPipeDepth() {return pipeDepth;}

    public int getPipeRow() {return pipeRow;}

    public int getManager() {return manager;}

}
