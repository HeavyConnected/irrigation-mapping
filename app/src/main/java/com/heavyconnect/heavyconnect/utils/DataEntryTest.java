package com.heavyconnect.heavyconnect.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.heavyconnect.heavyconnect.R;
import com.heavyconnect.heavyconnect.database.IrrigationContract;
import com.heavyconnect.heavyconnect.database.IrrigationDbHelper;

public class DataEntryTest extends AppCompatActivity {

    EditText mFieldName;
    EditText mCenterCoords;
    EditText mCoords;
    EditText mDeleteText;

    String fieldName;
    String centerCoordinate;
    String coordinates;
    String deleteText;

    Button mSubmitButton;

    Button mDeleteButton;

    // Database helper
    private IrrigationDbHelper mIrrigationDbHelper;
    private SQLiteDatabase mIrrigationDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIrrigationDbHelper = new IrrigationDbHelper(this);
        // Gets the data repo. in write mode
        mIrrigationDatabase = mIrrigationDbHelper.getWritableDatabase();

        mFieldName = (EditText) findViewById(R.id.field_name);
        mFieldName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fieldName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCenterCoords = (EditText) findViewById(R.id.center_coordiante);
        mCenterCoords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                centerCoordinate = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCoords = (EditText) findViewById(R.id.coordinates);
        mCoords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                coordinates = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDeleteText = (EditText) findViewById(R.id.delete_query);
        mDeleteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleteText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSubmitButton = (Button) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        mDeleteButton = (Button) findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

    }

    private void insertData() {


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES, centerCoordinate);
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES, coordinates);
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME, fieldName);

        /* First values inserted for testing
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES, "36.641879,-121.658534");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES, "36.647218,-121.662750,36.634828,-121.667678,36.640901,-121.644362");
        values.put(IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME,"F1"); */

        // Insert the new row returning the primary key value of the new row
        long newRowId;
        newRowId = mIrrigationDatabase.insert(
                IrrigationContract.IrrigationEntry.TABLE_NAME,
                null, values);

        mIrrigationDatabase = mIrrigationDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you wil actually use after this query.
        String[] projection = {
                IrrigationContract.IrrigationEntry._ID,
                IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES,
                IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES,
                IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME,
        };

        String sortOrder =
                IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME + " DESC";

        Cursor cursor = mIrrigationDatabase.query(
                IrrigationContract.IrrigationEntry.TABLE_NAME, // table to query
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
                    cursor.getColumnIndexOrThrow(IrrigationContract.IrrigationEntry._ID)
            );

            String fieldName = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME)
            );

            String centerCoordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES)
            );

            String coordinates = cursor.getString(
                    cursor.getColumnIndexOrThrow(IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES)
            );

            Log.d("Database Values", "ID: " + itemId +
                            "\nField Name: " + fieldName +
                            "\nCenter Coordinates: " + centerCoordinates +
                            "\nCoordinates: " + coordinates
            );

            cursor.moveToNext();
        }
    }

    private void deleteData() {
        // Define 'where' part of the query
        String selection = IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME + " LIKE ? ";
        // Specify arguments in placeholder order
        String[] selectionArgs = {deleteText};
        // Issue SQL statement
        mIrrigationDatabase.delete(IrrigationContract.IrrigationEntry.TABLE_NAME, selection, selectionArgs);

    }

}
