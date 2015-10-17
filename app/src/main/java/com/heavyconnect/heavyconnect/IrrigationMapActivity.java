package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.PlaceProvider;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

import android.support.v4.app.LoaderManager.LoaderCallbacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class IrrigationMapActivity extends AppCompatActivity implements TaskCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, LoaderCallbacks<Cursor> {
    private static final String PREFERENCES_KEY = "com.heavyconnect.heavyconnect.irrigation"; // Name of SharedPreferences file we will write to and read from
    private static final String USER_LEARNED_DRAWER_KEY = "has_drawer_opened"; // Key that maps to value mUserLearnedDrawer in SharedPreferences
    private Boolean mUserLearnedDrawer;

    private GoogleMap mGoogleMap;

    private String[] mFieldLocations; // Drawer ListView contents
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ProgressDialog mProgress;
    private Manager mManager;

    // Map tracing
    private GoogleMap mIrrigationMap;
    private SupportMapFragment mIrrigationMapFragment;
    private ArrayList<LatLng> mArrayPoints;
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

        if(getIntent().getAction() != null)
            handleIntent(getIntent());

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
                    mapMarkerDisable();
                    Log.d("IrrigationMapActivity", "Disable polygons not working");
                    mActionEditButton.setImageResource(R.drawable.green_pin);

                }
                countButtonClicks++;

            }
        });

    }

    public void onSearch(String query) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide keyboard
        List<Address> addressList = new ArrayList<>();
        mGoogleMap.clear();

        if(query != null || query != "") {
            Geocoder geocoder = new Geocoder(this);
            Location myLocation = mGoogleMap.getMyLocation(); // Get our current location
            String myPostalCode = "";
            String myAdminArea = "";
            try {
                Address myAddress = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1).get(0);
                myPostalCode = myAddress.getPostalCode(); // Get current postal code for search
                myAdminArea = myAddress.getAdminArea(); // Get current state for search
                Log.d("Postal Code", myPostalCode);
                Log.d("Admin Area", myAdminArea);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(myLocation != null)
                    addressList = geocoder.getFromLocationName(myPostalCode + " " + query + " " + myAdminArea, 1);
                else
                    addressList = geocoder.getFromLocationName(query, 1);
                //addressList = geocoder.getFromLocationName()
                //geocoder.getFrom

                // Sets marker to location found and zooms in
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(query));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                // Zoom in, animating the camera.
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 4000, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if(searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("UserQuery", query);
                onSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        Loader<Cursor> cLoader = null;

        Log.d("arg0", "arg0 = " + arg0);

        if(arg0==0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showLocations(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This edit button appears after user creates a polygon
        mEditScreenButton = (Button) findViewById(R.id.edit_screen_button);
        mEditScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("arraypoints", mArrayPoints);
                Intent intent = new Intent(IrrigationMapActivity.this, EditFieldActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /*
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        showLocations(c);
    } */

    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        mGoogleMap.clear();
        if(c != null) {
            while (c.moveToNext()) {
                markerOptions = new MarkerOptions();
                position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
                markerOptions.position(position);
                markerOptions.title(c.getString(0));
                mGoogleMap.addMarker(markerOptions);
            }
        }
        if(position != null){
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            mGoogleMap.animateCamera(cameraPosition);
        }
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

    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)){
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        } else if(intent.getAction().equals(Intent.ACTION_VIEW)) {
            getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
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
        // Instantiate map fragment
        mIrrigationMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.irrigation_map);
        mIrrigationMap = mIrrigationMapFragment.getMap();

        // Map functionality listeners and settings
        mIrrigationMap.setMyLocationEnabled(true);
        mIrrigationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Search Map Stuff
        mGoogleMap = mIrrigationMapFragment.getMap();
    }

    /*
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This edit button appears after user creates a polygon
        mEditScreenButton = (Button) findViewById(R.id.edit_screen_button);
        mEditScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("arraypoints", mArrayPoints);
                Intent intent = new Intent(IrrigationMapActivity.this, EditFieldActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    } */
}
