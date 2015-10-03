package com.heavyconnect.heavyconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

import java.util.ArrayList;

public class IrrigationMapActivity extends AppCompatActivity implements TaskCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener  {

    private String[] mFieldLocations;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ProgressDialog mProgress;
    private Manager mManager;

    // Draw polygon
    private GoogleMap mIrrigationMap;
    private ArrayList<LatLng> mArrayPoints = null;
    private boolean mMarkerClicked = false;
    Polyline polylineOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);

        if(!StorageUtils.getIsLoggedIn(this) || (mManager = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.map_loading), Toast.LENGTH_LONG).show();
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.equip_list_loading));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        mFieldLocations = getResources().getStringArray(R.array.field_locations_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Draw polygon
        mArrayPoints = new ArrayList<LatLng>();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.irrigation_map);
        mIrrigationMap = fm.getMap();

        // Display zoom map
        mIrrigationMap.setMyLocationEnabled(true);
        mIrrigationMap.setOnMapClickListener(this);
        mIrrigationMap.setOnMapLongClickListener(this);
        mIrrigationMap.setOnMarkerClickListener(this);
        mIrrigationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Set Adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mFieldLocations));
        // Set onClick listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Hello HappyTown", Toast.LENGTH_SHORT).show();
            }
        });


        //inflaion of the Floating Action button
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action);
        floatingActionButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("IrrigationMapActivity", "floating Action Button pushed");
            }
        });

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
        }
        return false;
    }

    public void countPolygonPoints() {
        if(mArrayPoints.size() >= 3) {
            mMarkerClicked = true;
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(mArrayPoints);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.YELLOW);
            com.google.android.gms.maps.model.Polygon polygon = mIrrigationMap.addPolygon(polygonOptions);
        }
    }

}
