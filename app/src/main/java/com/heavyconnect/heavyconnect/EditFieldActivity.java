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
import java.util.HashMap;


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
    private HashMap<String, String[]> mHashMap = new HashMap<String, String[]>(); // Saves all the attributes for each line
    private String[] mLineAttributes;

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

        // Allow map to be clicked
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
        for(int i = 0; i < mPolylines.size(); i++) {
            // If the marker that they clicked on already belongs to a line
            if (mPolylines.get(i).getPoints().contains(marker.getPosition())) {
                Marker firstMarker = marker;
                firstMarker.setPosition(mPolylines.get(i).getPoints().get(0));
                showTextBox(firstMarker); // Show the text box and allow edit
            }
        }



        if(!mLinePoints.isEmpty()) { // If they haven't closed a line and the line that they click on is the first
            if (mLinePoints.get(0).equals(marker.getPosition())) {
                countPolylinePoints(); // Create line and close it
                addInfoBox(marker); // Create an info box for that line

                // Reset attributes to allow drawing new lines
                mLinePoints.clear();
                mMarkerClicked = false;
            }
        }
        return false;
    }

    public void countPolylinePoints() {
        mMarkerClicked = true;
        mPolylineOptions = new PolylineOptions();

        mPolylineOptions.addAll(mLinePoints); // Add all points of line segment
        mPolylineOptions.color(Color.RED);
        mPolylines.add(mFieldMap.addPolyline(mPolylineOptions)); // Add a line to the map

    }

    public void addInfoBox(Marker marker) {
        mLineAttributes = new String[3];

        Marker tempMarker = mFieldMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .title("Field Name")
                .snippet("Row #: Length: Depth: "));
        tempMarker.showInfoWindow();

        mHashMap.put(marker.getPosition().toString(), mLineAttributes);
    }

    public void showTextBox(Marker marker) {
        String[] lineAttributes = mHashMap.get(marker.getPosition().toString());

        Marker tempMarker = mFieldMap.addMarker(new MarkerOptions()
        .position(marker.getPosition())
        .title("This Line")
        .snippet("Row #: " + lineAttributes[0] + "Length: " + lineAttributes[1] + "Depth: " + lineAttributes[2]));
        tempMarker.showInfoWindow();
    }


}
