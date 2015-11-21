package com.heavyconnect.heavyconnect.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by juice on 10/24/15.
 */
public class IrrigationProvider extends ContentProvider {

    private static final UriMatcher sUriMather = buildUriMather();
    private IrrigationDbHelper mOpenHelper;

    static final int FIELD_NAME_WITH_COORDINATES = 1;

    private static final SQLiteQueryBuilder sIrrigationQueryBuilder;

    static {
        sIrrigationQueryBuilder = new SQLiteQueryBuilder();
    }

  /*
   TODO: Query Selection
    private static final String sFieldName =
            IrrigationContract.IrrigationEntry.TABLE_NAME + "." +
                    IrrigationContract.IrrigationEntry.COLUMN_NAME_FIELD_NAME + " = ?";
    private  static final String sCenterCoordinates =
            IrrigationContract.IrrigationEntry.TABLE_NAME + "." +
                    IrrigationContract.IrrigationEntry.COLUMN_NAME_CENTER_COORDINATES + " = ?";
    private static final String sCoordinates =
            IrrigationContract.IrrigationEntry.TABLE_NAME + "." +
                    IrrigationContract.IrrigationEntry.COLUMN_NAME_COORDINATES + " = ?";
   */

    private static UriMatcher buildUriMather() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = IrrigationContract.CONTENT_AUTHORITY;
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch(sUriMather.match(uri))
        {
            case FIELD_NAME_WITH_COORDINATES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        IrrigationContract.IrrigationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                break;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMather.match(uri);
        Uri returnUri = null;

        switch(match)
        {
            case FIELD_NAME_WITH_COORDINATES: {
                long _id = db.insert(IrrigationContract.IrrigationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = IrrigationContract.IrrigationEntry.buildFieldUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
