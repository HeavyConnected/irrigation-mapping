package com.heavyconnect.heavyconnect.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.heavyconnect.heavyconnect.database.IrrigationContract;
import com.heavyconnect.heavyconnect.database.IrrigationDbHelper;
import com.heavyconnect.heavyconnect.entities.FieldModel;
import com.heavyconnect.heavyconnect.entities.PipeModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    public List<FieldModel> getAllFields(){
        List<FieldModel> list = new ArrayList<FieldModel>();
        FieldModel field = new FieldModel();

        mIrrigationDatabase = mIrrigationDbHelper.getReadableDatabase();
        // Iterate through database
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

        // Query each field name in database
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            field = new FieldModel();
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry._ID)
            );

            String fieldName = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME)
            );
            String centerCoordinate = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES)
            );
            String coordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES)
            );
            // Insert fieldName, centerCoordinate, and coordinate into FieldModel
            if (fieldName != null) {
                //TODO: Clean up coordinate string (extract alpha and special characters except commas and '-')
                field.setFieldName(fieldName);
                field.setCenterCoorfinates(centerCoordinate);
                field.setCoordinates(coordinates);
            }

           // Populate field model
            field.setCenterCoorfinates(centerCoordinate);
            field.setFieldName(fieldName);
            field.setCoordinates(coordinates);

            // Add field model to list
            list.add(field);

            cursor.moveToNext();
        }

        for(FieldModel x : list)
        {
            Log.d("DATABASEHELPER", "Field Name" + x.getFieldName());
            Log.d("DATABASEHELPER", "Field Center Coordinates" + x.getCenterCoorfinates());
            Log.d("DATABASEHELPER", "Field Coordinates " + x.getCoordinates());
        }
        return list;

    }
    public List<PipeModel> getAllPipes(){
        List<PipeModel> list = new ArrayList<PipeModel>();
        PipeModel pipe = new PipeModel();
        mIrrigationDatabase = mIrrigationDbHelper.getReadableDatabase();
        // Iterate through database
        // Define a projection that specifies which columns from the database
        // you wil actually use after this query.
        String[] projection = {
                IrrigationContract.PipelineInfoEntry._ID,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES,
        };

        String sortOrder =
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES + " DESC";

        Cursor cursor = mIrrigationDatabase.query(
                IrrigationContract.PipelineInfoEntry.TABLE_NAME, // table to query
                projection,                                    // columns returned
                null,                                          // columns for WHERE clause
                null,                                          // values for WHERE clause
                null,                                          // row grouping
                null,                                          // filter by group rows
                sortOrder                                      // the sort order
        );

        // Query each field name in database
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            pipe = new PipeModel();
            // length, depth, row, coordinates, field id
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry._ID)
            );

            String centerCoordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES)
            );

            String coordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES)
            );

            String depth = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH)
            );
            String length = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH)
            );
            String row = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW)
            );

            // Populate pipe model
            pipe.setFieldId(centerCoordinates);
            pipe.setCoordinates(coordinates);
            pipe.setDepth(depth);
            pipe.setLength(length);
            pipe.setRow(row);

            // Insert pipe model into list
            list.add(pipe);

            cursor.moveToNext();
        }
        for(PipeModel x : list)
        {
            Log.d("DATABASEHELPER", "Pipe Length" + x.getLength());
            Log.d("DATABASEHELPER", "Pipe Depth" + x.getDepth());
            Log.d("DATABASEHELPER", "Pipe Row " + x.getRow());
            Log.d("DATABASEHELPER", "Pipe Coordinates " + x.getCoordinates());
            Log.d("DATABASEHELPER", "Pipe Center Coordinates" + x.getFieldId());
        }
        return list;
    }
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


    private void remove(FieldModel fieldModel) {
        // Remove by the primary key
        // Define 'where' part of the query
        String selection = IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME + " LIKE ? ";
        // Specify arguments in placeholder order
        String[] selectionArgs = {fieldModel.getCenterCoorfinates()};
        // Issue SQL statement
        mIrrigationDatabase.delete(IrrigationContract.FieldEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void put(PipeModel pipeModel)
    {
        mIrrigationDatabase = mIrrigationDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES,
                pipeModel.getFieldId());
        values.put(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES,
                pipeModel.getCoordinates());
        values.put(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH,
                pipeModel.getDepth());
        values.put(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH,
                pipeModel.getLength());
        values.put(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW,
                pipeModel.getRow());


        /* First values inserted for testing
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES, "36.641879,-121.658534");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES, "36.647218,-121.662750,36.634828,-121.667678,36.640901,-121.644362");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME,"F1"); */

        // Insert the new row returning the primary key value of the new row
        long newRowId;
        newRowId = mIrrigationDatabase.insert(
                IrrigationContract.PipelineInfoEntry.TABLE_NAME,
                null, values);

        /* -- Logs data <begin>  --*/
        mIrrigationDatabase = mIrrigationDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you wil actually use after this query.
        String[] projection = {
                IrrigationContract.PipelineInfoEntry._ID,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH,
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW,
        };

        String sortOrder =
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES + " DESC";

        Cursor cursor = mIrrigationDatabase.query(
                IrrigationContract.PipelineInfoEntry.TABLE_NAME, // table to query
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
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry._ID)
            );

            String centerCoordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES)
            );
            String coordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES)
            );

            String depth = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH)
            );
            String length = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH)
            );
            String row = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW)
            );

            Log.d("Database Values", "ID: " + itemId +
                            "\nField Center: " + centerCoordinates +
                            "\nCoordinates: " + coordinates +
                            "\nDepth: " + depth +
                             "\nLepth: " + length +
                            "\nRow: " + row
            );

            cursor.moveToNext();
        }
         /* -- Logs data <end> --*/
    }

    private void remove(PipeModel pipeModel) {
        // Remove by the primary key
        // Define 'where' part of the query
        String selection = IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES + " LIKE ? ";
        // Specify arguments in placeholder order
        String[] selectionArgs = {pipeModel.getFieldId()};
        // Issue SQL statement
        mIrrigationDatabase.delete(IrrigationContract.PipelineInfoEntry.TABLE_NAME, selection, selectionArgs);
    }
}
