package com.heavyconnect.heavyconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * Created by naelinaquino on 10/3/15.
 */
public class EditFieldActivity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener{
    private GoogleMap mFieldMap;
    private SupportMapFragment mFieldMapFragment;
    private ArrayList<LatLng> mArrayPoints = null; // Points that plot out field
    private ArrayList<LatLng> mLinePoints = null; // Points that plot out line segments
    private PolylineOptions mPolylineOptions; // Saves the points for one line segment
    private Boolean mMarkerClicked = false; // Determines when to close the line segment
    private ArrayList<Polyline> mPolylines; // Save all the line segments in this ArrayList

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);

        mArrayPoints = getIntent().getParcelableArrayListExtra("arraypoints");

        mapSetup();
    }

    public void mapSetup() {
        // Create ArrayList of line points
        mLinePoints = new ArrayList<LatLng>();
        // Instantiate ArrayList of Polyline objects
        mPolylines = new ArrayList<>();

        // Instantiate map fragment
        mFieldMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.field_map);
        mFieldMap = mFieldMapFragment.getMap();

        // Map functionality listeners and settings
        mFieldMap.setMyLocationEnabled(true);
        mFieldMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mFieldMap.setOnMapClickListener(this);
        mFieldMap.setOnMapLongClickListener(this);
        mFieldMap.setOnMarkerClickListener(this);

        // Draw traced field on edit screen map
        PolygonOptions mPolygonOptions = new PolygonOptions();
        mPolygonOptions.addAll(mArrayPoints);
        mPolygonOptions.strokeColor(Color.BLUE);
        mPolygonOptions.strokeWidth(7);
        mFieldMap.addPolygon(mPolygonOptions);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(mMarkerClicked == false) {
            mFieldMap.addMarker(new MarkerOptions().position(latLng));
            mLinePoints.add(latLng);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mFieldMap.clear();
        mLinePoints.clear();
        mMarkerClicked = false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mLinePoints.get(0).equals(marker.getPosition())) {
            countPolylinePoints();
        }
        return false;
    }

    public void countPolylinePoints() {
        mMarkerClicked = true;
        mPolylineOptions = new PolylineOptions();

        mPolylineOptions.addAll(mLinePoints); // Add all points of line segment
        mPolylineOptions.color(Color.RED);
        Polyline polyline = mFieldMap.addPolyline(mPolylineOptions); // Add a line to the map

        mPolylines.add(polyline);
        // Reset attributes to allow drawing new lines
        mLinePoints.clear();
        mMarkerClicked = false;
    }
}
