package com.heavyconnect.heavyconnect.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by juice on 10/24/15.
 */
public class IrrigationDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "irrigation_locations.db";

    public IrrigationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_SQL_FIELD_TABLE = "CREATE TABLE " + IrrigationContract.FieldEntry.TABLE_NAME +
                " (" + IrrigationContract.FieldEntry._ID  + " INTEGER, " +
                IrrigationContract.FieldEntry.COLUMN_NAME_FIELD_NAME + " VARCHAR(10), " +
                IrrigationContract.FieldEntry.COLUMN_NAME_CENTER_COORDINATES + " VARCHAR(10) PRIMARY KEY, " +
                IrrigationContract.FieldEntry.COLUMN_NAME_COORDINATES + " VARCHAR(10)" +
                ");";

        db.execSQL(CREATE_SQL_FIELD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IrrigationContract.FieldEntry.TABLE_NAME);
        onCreate(db);
    }
}
