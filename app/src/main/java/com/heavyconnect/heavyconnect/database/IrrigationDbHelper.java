package com.heavyconnect.heavyconnect.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by juice on 10/24/15.
 */
public class IrrigationDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "irrigation_locations.db";

    public IrrigationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // TODO: ADD PIPE TABLE HERE
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("IRRIGATIONDATABASEHELPE", "Test");
        // Creates Field Table
        final String CREATE_SQL_FIELD_TABLE = "CREATE TABLE " + IrrigationContract.FieldEntry.TABLE_NAME +
                " (" + IrrigationContract.FieldEntry._ID  + " INTEGER, " +
                IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME + " VARCHAR(10), " +
                IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES + " VARCHAR(10) PRIMARY KEY, " +
                IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES + " VARCHAR(10)" +
                ");";


        // length, depth, row, mPipeCoordinates, mCenterCoordinate
        // Create Pipe Table
        final String CREATE_SQL_PIPE_TABLE = "CREATE TABLE " + IrrigationContract.PipelineInfoEntry.TABLE_NAME +
                " (" + IrrigationContract.PipelineInfoEntry._ID  + " INTEGER PRIMARY KEY, " +
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_LENGTH + " VARCHAR(10), " +
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_DEPTH + " VARCHAR(10), " +
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_ROW + " VARCHAR(10), " +
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_COORDINATES + " VARCHAR(10), " +
                IrrigationContract.PipelineInfoEntry.COLUMN_NAME_FIELD_CENTER_COORDINATES + " VARCHAR(10)" +
                ");";

        db.execSQL(CREATE_SQL_FIELD_TABLE);
        db.execSQL(CREATE_SQL_PIPE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS " + IrrigationContract.FieldEntry.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + IrrigationContract.PipelineInfoEntry.TABLE_NAME);
        onCreate(db);
    }
}
