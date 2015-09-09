package com.heavyconnect.heavyconnect.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by felipepx on 9/2/15.
 */
public class Equipment {

    public static final int STATUS_OK = 1;
    public static final int STATUS_SERVICE = 2;
    public static final int STATUS_BROKEN = 3;

    private int id;
    private int manager;
    private String name;

    @SerializedName("model_number")
    private String modelNumber;

    @SerializedName("asset_number")
    private int assetNumber;

    private int status;
    private int hours;

    private double longitude;
    private double latitude;

    public Equipment(){}

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManager() {
        return manager;
    }

    public void setManager(int manager) {
        this.manager = manager;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public int getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(int assetNumber) {
        this.assetNumber = assetNumber;
    }

    public int getEngineHours() {
        return hours;
    }

    public void setEngineHours(int hours) {
        this.hours = hours;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
