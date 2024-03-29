package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import com.heavyconnect.heavyconnect.adapters.FieldListAdapter;
import com.heavyconnect.heavyconnect.database.IrrigationContract;
import com.heavyconnect.heavyconnect.database.IrrigationDbHelper;
import com.heavyconnect.heavyconnect.entities.FieldModel;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.DataBaseHelper;
import com.heavyconnect.heavyconnect.utils.FieldNameDialogFragment;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IrrigationMapActivity extends AppCompatActivity implements TaskCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener/*, LoaderCallbacks<Cursor> */{
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
    private ArrayList<LatLng> mFieldWindowLocations; // holds center location for each field
    public HashMap<LatLng, ArrayList<LatLng>> mSavedfieldLocaions; // holds all cleared field locations
    private ArrayList<LatLng> mSavedArrayPoints;
    private LatLng current;
    private boolean isRedrawn = false;
    private boolean mMarkerClicked = false;
    private PolygonOptions mPolygonOptions;
    private int countButtonClicks = 0;

    private ArrayList<LatLng> temp;

    private static Button mEditScreenButton;

    // Database members
    private Button mDataEntry;
    private SQLiteDatabase mIrrigationDataBase; // Field Location
    private IrrigationDbHelper mIrrigationDbHelper;

    // Values being input into database
    private static String mFieldName;
    // These values have been converted from ints
    // over to strings in order to be stored into db.
    String dbCenterCoordinate = "";
    String dbCoordinates = "";


    // Database models
    private static FieldModel mFieldModel;
    // TODO: Create a pipeModel

    //Database
    private static DataBaseHelper mDatabaseHelper;

    // Holds field model to be populated onto naviagtion drawer list
    List<FieldModel> mDrawerFieldInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);
        mDatabaseHelper = new DataBaseHelper(this);

        mIrrigationDbHelper = new IrrigationDbHelper(this);
        mDatabaseHelper.getAllFields();
        mDatabaseHelper.getAllPipes();

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

        temp = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Initialize map
        MapsInitializer.initialize(this);
        mapSetup();
/*
        if(getIntent().getAction() != null)
            handleIntent(getIntent());*/

        mFieldLocations = getResources().getStringArray(R.array.field_locations_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

       /*
        // Set Adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mFieldLocations));
        // Set onClick listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Hello HappyTown", Toast.LENGTH_SHORT).show();
            }
        });*/

        // Populate Navigation Drawer List
        populateNavigationDrawerList();

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
                //This cleamSavedfieldrs all polygons created
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

        marker.hideInfoWindow();
        if((countButtonClicks % 2 != 0) && (mSavedfieldLocaions.get(marker.getPosition()) != null)) // if you are on edit and
        {                                                                                           // the marker is stored in msfl
            ArrayList<LatLng> temp = mSavedfieldLocaions.get(marker.getPosition());
            redrawPolygonPoints(temp);
            current = marker.getPosition();
            isRedrawn = true;
            return false;
        }

        else if(mArrayPoints.get(0).equals(marker.getPosition())) {
            mFieldModel = new FieldModel();
            countPolygonPoints();

            // TODO: if (Drawer is !null) mEditScreenButton.setVisibility(View.GONE);

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
            //countButtonClicks++;


            /* -- Store into field model to be stored into database  -- */
            // Convert center coordinate into string

            dbCenterCoordinate =  Double.toString(current.latitude) + "," + Double.toString(current.longitude);
            Log.d("LatLong" , "Center Coordinate: " + current);
            // Insert into field model
            mFieldModel.setCenterCoorfinates(dbCenterCoordinate);
            Log.d("LatLong", "(**Center Coordinate: " + dbCenterCoordinate);



            // Convert coordinates into string
            for(int i = 0; i < mArrayPoints.size(); i++) {
                // Preprocess coordinates to take in numbers and commas only

                dbCoordinates += Double.toString(mArrayPoints.get(i).latitude) + ",";
                dbCoordinates += Double.toString(mArrayPoints.get(i).longitude) + ",";
            }

            // Omit last comma
           dbCoordinates = dbCoordinates.substring(0, dbCoordinates.length() - 2);

            // Insert into field model
            mFieldModel.setCoordinates(dbCoordinates);

            displayDialog();

            return true;
        }
        return false;
    }

    private void displayDialog() {
        FieldNameDialogFragment nameDialogFragment = FieldNameDialogFragment.getInstance();
        nameDialogFragment.show(getFragmentManager(), "field_name_fragment");
    }

    public void redrawPolygonPoints(ArrayList<LatLng> coordinates) {
        mSavedArrayPoints = coordinates;
        Log.d("mSavedArrayPoints len", mSavedArrayPoints.toString());
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

        mIrrigationMap.setInfoWindowAdapter(new CustomInfoWindowAdapter()); // enables the use of the new infowindow

        // Search Map Stuff
        mGoogleMap = mIrrigationMapFragment.getMap();

        // This edit button appears after user creates a polygon
        mEditScreenButton = (Button) findViewById(R.id.edit_screen_button);
        mEditScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countButtonClicks % 2 == 0)
                    return;
                Bundle bundle = new Bundle();
                Log.d("mArrayPoints", Integer.toString(mArrayPoints.size()));
                Log.d("mSavedArrayPoints", Integer.toString(mSavedArrayPoints.size()));
                bundle.putParcelableArrayList("arraypoints", mArrayPoints);
                bundle.putParcelableArrayList("savedpoints", mSavedArrayPoints);
                bundle.putBoolean("isredrawn", isRedrawn);
                bundle.putString("center_coordinate", dbCenterCoordinate);
                Intent intent = new Intent(IrrigationMapActivity.this, EditFieldActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    // Populate Navigation Drawer List
    public void populateNavigationDrawerList() {
        // Field model to be put into mDrawerFieldInfo array list
        FieldModel localFieldModel;
        mDrawerFieldInfo = new ArrayList<>();

        mIrrigationDataBase = mIrrigationDbHelper.getReadableDatabase();
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

        Cursor cursor = mIrrigationDataBase.query(
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
            localFieldModel = new FieldModel();
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
            if(fieldName != null ){
                //TODO: Clean up coordinate string (extract alpha and special characters except commas and '-')
                localFieldModel.setFieldName(fieldName);
                localFieldModel.setCenterCoorfinates(centerCoordinate);
                localFieldModel.setCoordinates(coordinates);
            }

           /* Log.d("Database Values", "ID: " + itemId +
                            "\nField Name: " + fieldName
            );*/

            // Insert field model into array
            mDrawerFieldInfo.add(localFieldModel);

            cursor.moveToNext();
        }
        // Field List Adapter
        final FieldListAdapter fieldListAdapter = new FieldListAdapter(this, mDrawerFieldInfo);
        // Populate list view from adapter
        // Set Adapter for the list view
        mDrawerList.setAdapter(fieldListAdapter);
        // Set onClick listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mGoogleMap.clear();
                mArrayPoints.clear();
                String[] latlng = mDrawerFieldInfo.get(i).getCoordinates().split(",");
                Toast.makeText(getApplicationContext(), mDrawerFieldInfo.get(i).getCoordinates(), Toast.LENGTH_LONG).show();

                for(int j = 0; j < latlng.length - 1; j += 2)
                {
                    Log.i("IrrigationMapActivity", "lat: " + latlng[j]);
                    Log.i("IrrigationMapActivity", "long: " + latlng[j + 1]);
                    LatLng location = new LatLng(Double.parseDouble(latlng[j]), Double.parseDouble(latlng[j + 1]));
                    mArrayPoints.add(location);
                }

                redrawPolygonPoints(mArrayPoints);
            }
        });
    }

    public static void submitFieldEntry(String fieldName) {
        mFieldName = fieldName;
        mFieldModel.setFieldName(mFieldName);
        if(mFieldName != null)
            mEditScreenButton.setVisibility(View.VISIBLE);
        // Store field model into
        mDatabaseHelper.put(mFieldModel);

        Log.d("IrrigationMapActivity", "Field Name: " + mFieldModel.getFieldName());
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        CustomInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.field_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.field_name));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.depth));
            tvSnippet.setText(marker.getSnippet());
            TextView tvPipenum = ((TextView)myContentsView.findViewById(R.id.num_pipes));
            tvPipenum.setText("32 pipes");
            return myContentsView;

        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}
