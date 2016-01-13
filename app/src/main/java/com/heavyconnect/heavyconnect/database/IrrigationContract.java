package com.heavyconnect.heavyconnect.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by juice on 10/24/15.
 */
public final class IrrigationContract {
    public static final String CONTENT_AUTHORITY = "com.heavyconnect.heavyconnect";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LOCATION = "location";


    public IrrigationContract(){}

    // Database Table
    public static final class FieldEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String TABLE_NAME = "field_locations";
        public static final String COLUMN_NAME_FIELD_NAME = "field_name";
        public static final String COLUMN_NAME_CENTER_COORDINATES = "center_coordinates";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";

        public static Uri buildFieldUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PipelineInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "pipe";
      //  public static final String COLUMN_NAME_PIPE_ID = "pipe_id";
        public static final String COLUMN_NAME_LENGTH = "lenght";
        public static final String COLUMN_NAME_DEPTH = "depth";
        public static final String COLUMN_NAME_ROW = "row";
        public static final String COLUMN_NAME_COORDINATES = "pipe_coordinates";
        public static final String COLUMN_NAME_FIELD_CENTER_COORDINATES = "field_center_coordinates";
    }
}
