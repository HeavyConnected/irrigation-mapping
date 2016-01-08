package com.heavyconnect.heavyconnect.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.heavyconnect.heavyconnect.database.IrrigationContract;
import com.heavyconnect.heavyconnect.database.IrrigationDbHelper;
import com.heavyconnect.heavyconnect.entities.FieldModel;

/**
 * Created by anitagarcia on 11/21/15.
 */
public final class DataBaseHelper {
    // Database helper
    private IrrigationDbHelper mIrrigationDbHelper;
    // Database containing both the pipe and field tables
    private SQLiteDatabase mIrrigationDatabase;

    // Application context
    Context mContext;

    // Constructor
    public DataBaseHelper(Context context){
        mContext = context;
        mIrrigationDbHelper = new IrrigationDbHelper(context);
    }

    // Methods
    public void put(FieldModel fieldModel)
    {
        mIrrigationDatabase = mIrrigationDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES,
                fieldModel.getCenterCoorfinates());
        values.put(IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES,
                fieldModel.getCoordinates());
        Log.d("DataBaseHelper", fieldModel.getFieldName());
        values.put(IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME,
                fieldModel.getFieldName());

        /* First values inserted for testing
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES, "36.641879,-121.658534");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES, "36.647218,-121.662750,36.634828,-121.667678,36.640901,-121.644362");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME,"F1"); */

        // Insert the new row returning the primary key value of the new row
        long newRowId;
        newRowId = mIrrigationDatabase.insert(
                IrrigationContract.FieldEntry.TABLE_NAME,
                null, values);

        /* -- Logs data <begin>  --*/
        mIrrigationDatabase = mIrrigationDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you wil actually use after this query.
        String[] projection = {
                IrrigationContract.FieldEntry._ID,
                IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES,
                IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES,
                IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME,
        };

        String sortOrder =
                IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME + " DESC";

        Cursor cursor = mIrrigationDatabase.query(
                IrrigationContract.FieldEntry.TABLE_NAME, // table to query
                projection,                                    // columns returned
                null,                                          // columns for WHERE clause
                null,                                          // values for WHERE clause
                null,                                          // row grouping
                null,                                          // filter by group rows
                sortOrder                                      // the sort order
        );

        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry._ID)
            );

            String fieldName = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME)
            );
            String centerCoordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES)
            );

            String coordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES)
            );

            Log.d("Database Values", "ID: " + itemId +
                            "\nField Name: " + fieldName +
                            "\nCenter Coordinates: " + centerCoordinates +
                            "\nCoordinates: " + coordinates
            );

            cursor.moveToNext();
        }
         /* -- Logs data <end> --*/
    }
    // TODO: Overload put and remove for Pipe Model

    private void remove(FieldModel fieldModel) {
        // Remove by the primary key
        // Define 'where' part of the query
        String selection = IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME + " LIKE ? ";
        // Specify arguments in placeholder order
        String[] selectionArgs = {fieldModel.getCenterCoorfinates()};
        // Issue SQL statement
        mIrrigationDatabase.delete(IrrigationContract.FieldEntry.TABLE_NAME, selection, selectionArgs);
    }

}
