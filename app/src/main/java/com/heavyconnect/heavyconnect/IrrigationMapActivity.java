package com.heavyconnect.heavyconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.Constants;
import com.heavyconnect.heavyconnect.utils.StorageUtils;


/**
 * Created by anon on 9/19/15.
 */
public class IrrigationMapActivity extends AppCompatActivity implements OnMapReadyCallback, ViewTreeObserver.OnGlobalLayoutListener, TaskCallback, GoogleMap.OnCameraChangeListener {
    private String[] mFieldLocations;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private SupportMapFragment mIrrigationMapFragment;

    private FrameLayout mIrrigationMapContainer;

    private GoogleMap mIrrigationMap;

    private ProgressDialog mProgress;
    private Manager mManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_map);

        if(!StorageUtils.getIsLoggedIn(this) || (mManager = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.map_loading), Toast.LENGTH_LONG).show();
            finish();
        }

        MapsInitializer.initialize(this);

        mIrrigationMapContainer = (FrameLayout) findViewById(R.id.irrigation_map_container);

        mIrrigationMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.irrigation_map_container, mIrrigationMapFragment).commit();
        mIrrigationMapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        //mProgress.setMessage(getString(R.string.equip_list_loading));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

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
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        if (position.zoom > Constants.MAP_DEFAULT_MARKER_SMALL_ZOOM)
            mIrrigationMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.MAP_DEFAULT_MARKER_SMALL_ZOOM));
    }

    @Override
    public void onGlobalLayout() {
        mIrrigationMapContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onMapReady(GoogleMap irrigationMap) {
        mIrrigationMap = irrigationMap;
        mIrrigationMap.setOnCameraChangeListener(this);

        ViewTreeObserver vto = mIrrigationMapContainer.getViewTreeObserver();
        if(vto.isAlive()) {
            vto.addOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onTaskFailed(int errorCode) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, getString(R.string.map_load_failure), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }
}
