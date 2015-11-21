package com.heavyconnect.heavyconnect.entities;

/**
 * Created by juice on 10/31/15.
 */
public class FieldModel {

    private String mFieldName;
    private String mCenterCoorfinates;
    private String mCoordinates;

    public String getFieldName() {
        return mFieldName;
    }

    public String getCenterCoorfinates() {
        return mCenterCoorfinates;
    }

    public String getCoordinates() {
        return mCoordinates;
    }

    public void setFieldName(String mFieldName) {
        this.mFieldName = mFieldName;
    }

    public void setCenterCoorfinates(String mCenterCoorfinates) {
        this.mCenterCoorfinates = mCenterCoorfinates;
    }

    public void setCoordinates(String mCoordinates) {
        this.mCoordinates = mCoordinates;
    }
}
