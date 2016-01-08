package com.heavyconnect.heavyconnect.entities;

/**
 * Created by anitagarcia on 1/6/16.
 */
public class PipeModel {
    // Under construction

    private String mLength;
    private String mDepth;
    private String mRow;
    private String mCoordinates;
    private String mFieldId;

    public PipeModel(){

    }
    public PipeModel(String length, String depth, String row, String coordinates, String fieldId){
        mLength = length;
        mDepth = depth;
        mRow = row;
        mCoordinates = coordinates;
        mFieldId = fieldId;
    }
    public String getLength(){return mLength;}
    public String getDepth() {return mDepth;}
    public String getRow() {return mRow;}
    public String getCoordinates() {return mCoordinates;}
    public String getFieldId() {return mFieldId;}

    public void setLength(String length) {
        mLength = length;
    }
    public void setDepth(String depth) {
        mDepth = depth;
    }
    public void setRow(String row) {
        mRow = row;
    }

    public void setCoordinates(String coordinates) {
        mCoordinates = coordinates;
    }

    public void setFieldId(String fieldId){
        mFieldId = fieldId;
    }
};
