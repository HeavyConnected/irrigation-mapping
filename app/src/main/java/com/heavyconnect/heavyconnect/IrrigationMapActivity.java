package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

/**
 * Created by anon on 9/19/15.
 */
public class IrrigationMapActivity extends Activity {
    private String[] mFieldLocations;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);
        mFieldLocations = getResources().getStringArray(R.array.field_locations_array);
    }
}
