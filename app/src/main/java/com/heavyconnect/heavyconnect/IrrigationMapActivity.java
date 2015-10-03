package com.heavyconnect.heavyconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

import java.util.ArrayList;


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
    private ArrayList<LatLng> mArrayPoints = new ArrayList<LatLng>();
    private boolean mMarkerClicked = false;
    private PolygonOptions mPolygonOptions;

    private Button mEditScreenButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);

        mEditScreenButton = (Button) findViewById(R.id.edit_screen_button);

        if(!StorageUtils.getIsLoggedIn(this) || (mManager = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.map_loading), Toast.LENGTH_LONG).show();
            finish();
        }

        MapsInitializer.initialize(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*
        mIrrigationMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.irrigation_map_container, mIrrigationMapFragment).commit();
        mIrrigationMapFragment.getMapAsync(this); */

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.equip_list_loading));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        // Initialize map
        mapSetup();

        mEditScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Find out how to pass ArrayList of LatLng to next Activity
                /*
                Bundle bundle = new Bundle();
                bundle.putSerializable("arraypoints", mArrayPoints);
                Intent intent = new Intent(getBaseContext(), EditFieldActivity.class);
                intent.putExtra("arraypoints", mArrayPoints);
                */
            }
        });

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
        if(mArrayPoints.get(0).equals(marker.getPosition())) {
            countPolygonPoints();
            // TODO: if (Drawer is !null) mEditScreenButton.setVisibility(View.GONE);
            mEditScreenButton.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public void countPolygonPoints() {
        if(mArrayPoints.size() >= 3) {
            mMarkerClicked = true;
            mPolygonOptions = new PolygonOptions();
            mPolygonOptions.addAll(mArrayPoints);
            mPolygonOptions.strokeColor(Color.BLUE);
            mPolygonOptions.strokeWidth(7);
            mPolygonOptions.fillColor(Color.YELLOW);
            mIrrigationMap.addPolygon(mPolygonOptions);
        }
    }

    public void mapSetup() {
        // Create ArrayList of coordinates
        mArrayPoints = new ArrayList<LatLng>();

        // Instantiate map fragment
        mIrrigationMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.irrigation_map);
        mIrrigationMap = mIrrigationMapFragment.getMap();

        // TODO: Enable this upon floating action button click
        // Map functionality listeners and settings
        mIrrigationMap.setMyLocationEnabled(true);
        mIrrigationMap.setOnMapClickListener(this);
        mIrrigationMap.setOnMapLongClickListener(this);
        mIrrigationMap.setOnMarkerClickListener(this);
        mIrrigationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

}
