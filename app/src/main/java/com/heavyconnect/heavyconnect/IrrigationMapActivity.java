package com.heavyconnect.heavyconnect;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.resttasks.LoginTask;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;
import java.lang.Math;

import java.util.ArrayList;
import java.util.HashMap;


public class IrrigationMapActivity extends AppCompatActivity implements TaskCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener  {
    private static final String PREFERENCES_KEY = "com.heavyconnect.heavyconnect.irrigation"; // Name of SharedPreferences file we will write to and read from
    private static final String USER_LEARNED_DRAWER_KEY = "has_drawer_opened"; // Key that maps to value mUserLearnedDrawer in SharedPreferences
    private Boolean mUserLearnedDrawer;

    private String[] mFieldLocations; // Drawer ListView contents
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ProgressDialog mProgress;
    private Manager mManager;

    // Map tracing
    private GoogleMap mIrrigationMap;
    private SupportMapFragment mIrrigationMapFragment;
    private ArrayList<LatLng> mArrayPoints;
    private ArrayList<LatLng> mFieldWindowLocations; // holds center location for each field
    private HashMap<LatLng, ArrayList<LatLng>> mSavedfieldLocaions; // holds all cleared field locations
    private ArrayList<LatLng> mSavedArrayPoints;
    private LatLng current;
    private boolean isRedrawn = false;
    private boolean mMarkerClicked = false;
    private PolygonOptions mPolygonOptions;
    private int countButtonClicks = 0;

    private Button mEditScreenButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);

        if(!StorageUtils.getIsLoggedIn(this) || (mManager = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.map_loading), Toast.LENGTH_LONG).show();
            finish();
        }

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.equip_list_loading));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Initialize map
        MapsInitializer.initialize(this);
        mapSetup();

        mFieldLocations = getResources().getStringArray(R.array.field_locations_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set Adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mFieldLocations));
        // Set onClick listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Hello HappyTown", Toast.LENGTH_SHORT).show();
            }
        });

        mUserLearnedDrawer = readFromPreferences(this, USER_LEARNED_DRAWER_KEY, false);
        checkHasOpened(); // Checks if the user has previously accessed this screen. Opens the drawer the first time this screen is accessed.


        // FloatingActionButton to allow field tracing
        final FloatingActionButton mActionEditButton = (FloatingActionButton) findViewById(R.id.floating_action);
        mActionEditButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                //make the field clickable to edit and change

                Log.d("IrrigationMapActivity", "floating Action Button pushed");
                //if the countButtonClicks is even the enable polygon map is created
                if (countButtonClicks % 2 == 0) {
                    mActionEditButton.setImageResource(R.drawable.red_pin);
                    mapMarkerEnable();
                    Log.d("IrrigationMapActivity", "Enable polygons");


                }
                //This clears all polygons created
                else {
                    ArrayList<LatLng> temp = new ArrayList<LatLng>();
                    temp.addAll(mArrayPoints);
                    mSavedfieldLocaions.put(current, temp);
                    mapMarkerDisable();
                    Log.d("IrrigationMapActivity", "Disable polygons not working");
                    mActionEditButton.setImageResource(R.drawable.green_pin);
                    // since not editing fields, show markers on each owned field
                    for(int i = 0; i < mFieldWindowLocations.size(); i++)
                    {
                        Marker tempMarker = mIrrigationMap.addMarker(new MarkerOptions()
                                .position(mFieldWindowLocations.get(i))
                                .title("Field: " + Integer.toString(i))
                                .snippet("Pipe Depth: 32 inches"));
                        tempMarker.showInfoWindow();
                    }



                }
                countButtonClicks++;

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTaskFailed(int errorCode) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, getString(R.string.irrigation_map_load_failure), Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    private void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putBoolean(preferenceName, preferenceValue);
        preferencesEditor.apply();
    }

    private boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(preferenceName, false);
    }

    public void checkHasOpened() {
        if(!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            saveToPreferences(this, USER_LEARNED_DRAWER_KEY, true);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mMarkerClicked == false) {
            mIrrigationMap.addMarker(new MarkerOptions().position(latLng));
            mArrayPoints.add(latLng);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mIrrigationMap.clear();
        mArrayPoints.clear();
        mMarkerClicked = false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if((countButtonClicks % 2 != 0) && (mSavedfieldLocaions.get(marker.getPosition()) != null))
        {
            ArrayList<LatLng> temp = mSavedfieldLocaions.get(marker.getPosition());
            redrawPolygonPoints(temp);
            current = marker.getPosition();
            isRedrawn = true;
        }

        else if(mArrayPoints.get(0).equals(marker.getPosition())) {
            countPolygonPoints();
            // TODO: if (Drawer is !null) mEditScreenButton.setVisibility(View.GONE);
            mEditScreenButton.setVisibility(View.VISIBLE);

            double avgX = 0, avgY = 0, avgZ = 0;
            for(int i = 0; i < mArrayPoints.size(); i++){
                double lat, lon;
                lat = mArrayPoints.get(i).latitude;
                lon = mArrayPoints.get(i).longitude;


                lat = lat * (Math.PI / 180); // convert deg to rad
                lon = lon * (Math.PI / 180);

                avgX += Math.cos(lat) * Math.cos(lon); // convert the avg
                avgY += Math.cos(lat) * Math.sin(lon);
                avgZ += Math.sin(lat);
            }
            avgX /= mArrayPoints.size();
            avgY /= mArrayPoints.size();
            avgZ /= mArrayPoints.size();

            double lon = Math.atan2(avgY, avgX);
            double hyp = Math.sqrt(avgX * avgX + avgY * avgY);
            double lat = Math.atan2(avgZ, hyp);
            lon = lon * (180 / Math.PI);
            lat = lat * (180 / Math.PI);

            LatLng newLatLon = new LatLng(lat, lon);
            mFieldWindowLocations.add(newLatLon);
            current = newLatLon;
        }
        /*
        if(mMarkerClicked == true) {

            double avgX = 0, avgY = 0, avgZ = 0;
            for(int i = 0; i < mArrayPoints.size(); i++){
                double lat, lon;
                lat = mArrayPoints.get(i).latitude;
                lon = mArrayPoints.get(i).longitude;


                lat = lat * (Math.PI / 180); // convert deg to rad
                lon = lon * (Math.PI / 180);

                avgX += Math.cos(lat) * Math.cos(lon); // convert the avg
                avgY += Math.cos(lat) * Math.sin(lon);
                avgZ += Math.sin(lat);
            }
            avgX /= mArrayPoints.size();
            avgY /= mArrayPoints.size();
            avgZ /= mArrayPoints.size();

            double lon = Math.atan2(avgY, avgX);
            double hyp = Math.sqrt(avgX * avgX + avgY * avgY);
            double lat = Math.atan2(avgZ, hyp);
            lon = lon * (180 / Math.PI);
            lat = lat * (180 / Math.PI);

            LatLng newLatLon = new LatLng(lat, lon);
            mFieldWindowLocations.add(newLatLon);
            current = newLatLon;


            /*
            Marker tempMarker = mIrrigationMap.addMarker(new MarkerOptions()
                    .position(newLatLon)
                    .title("Field: A113")
                    .snippet("Pipe Depth: 32 inches"));
            tempMarker.showInfoWindow();
            */

        //}


        return false;
    }

    public void redrawPolygonPoints(ArrayList<LatLng> coordinates) {
        mSavedArrayPoints = coordinates;
        isRedrawn = true;
        mPolygonOptions = new PolygonOptions();
        mPolygonOptions.addAll(coordinates);
        mPolygonOptions.strokeColor(Color.BLUE);
        mPolygonOptions.strokeWidth(7);
        mPolygonOptions.fillColor(Color.YELLOW);
        mIrrigationMap.addPolygon(mPolygonOptions);
    }

    public void countPolygonPoints() {
        if(mArrayPoints.size() >= 3) {
            mMarkerClicked = true;
            mPolygonOptions = new PolygonOptions();
            mPolygonOptions.addAll(mArrayPoints);
            mPolygonOptions.strokeColor(Color.BLUE);
            mPolygonOptions.strokeWidth(7);
            mPolygonOptions.fillColor(Color.YELLOW);
            mIrrigationMap.addPolygon(mPolygonOptions); // Draw full polygon on map
        }
    }

    public void mapMarkerEnable() {
        // Map functionality listeners and settings
        mIrrigationMap.setOnMapClickListener(this);
        mIrrigationMap.setOnMapLongClickListener(this);
        mIrrigationMap.setOnMarkerClickListener(this);
    }

    public void mapMarkerDisable() {
        //this will clear all markers and polygons
        mIrrigationMap.setOnMapClickListener(null);
        mIrrigationMap.setOnMapLongClickListener(null);
        mIrrigationMap.setOnMarkerClickListener(null);
        mIrrigationMap.clear();
        mArrayPoints.clear();
        mMarkerClicked = false;
    }

    //this sets up the map after the edit button is clicked
    public void mapSetup() {
        // Instantiate ArrayList of points
        mArrayPoints = new ArrayList<LatLng>();
        mFieldWindowLocations = new ArrayList<LatLng>();
        mSavedfieldLocaions = new HashMap<LatLng, ArrayList<LatLng>>();
        mSavedArrayPoints = new ArrayList<LatLng>();
        current = new LatLng(0,0);
        // Instantiate map fragment
        mIrrigationMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.irrigation_map);
        mIrrigationMap = mIrrigationMapFragment.getMap();

        // Map functionality listeners and settings
        mIrrigationMap.setMyLocationEnabled(true);
        mIrrigationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // This edit button appears after user creates a polygon
        mEditScreenButton = (Button) findViewById(R.id.edit_screen_button);
        mEditScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countButtonClicks % 2 == 0)
                    return;
                Bundle bundle = new Bundle();
                Log.v("mArrayPoints", Integer.toString(mArrayPoints.size()));
                Log.v("mSavedArrayPoints", Integer.toString(mSavedArrayPoints.size()));
                bundle.putParcelableArrayList("arraypoints", mArrayPoints);
                bundle.putParcelableArrayList("savedpoints", mSavedArrayPoints);
                bundle.putBoolean("isredrawn", isRedrawn);
                Intent intent = new Intent(IrrigationMapActivity.this, EditFieldActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
