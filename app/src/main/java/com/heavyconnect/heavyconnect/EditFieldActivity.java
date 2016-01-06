package com.heavyconnect.heavyconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.ListIterator;


/**
 * Created by naelinaquino on 10/3/15.
 */
public class EditFieldActivity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mFieldMap;
    private SupportMapFragment mFieldMapFragment;
    private ArrayList<LatLng> mArrayPoints = null; // Points that plot out field
    private ArrayList<LatLng> mLinePoints = null; // Points that plot out line segments
    private PolylineOptions mPolylineOptions; // Saves the points for one line segment
    private Boolean mMarkerClicked = false; // Determines when to close the line segment
    private ArrayList<Polyline> mPolylines; // Save all the line segments in this ArrayList
    private ArrayList<Marker> mMarkers; // Saves all markers in this ArrayList
    private boolean isRedrawn;
    private ArrayList<LatLng> mSavedPoints;
    private HashMap<String, String[]> mHashMap = new HashMap<String, String[]>(); // Saves all the attributes for each line
    private String[] mLineAttributes;

    // Toolbar buttons
    private Button mDoneButton;
    private Button mDeleteButton;
    private Button mCancelButton;
    private boolean mDeleteFlag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);

        mArrayPoints = getIntent().getParcelableArrayListExtra("arraypoints");
        Log.v("EditFieldActivity", mArrayPoints.toString());
        mSavedPoints = getIntent().getParcelableArrayListExtra("savedpoints");
        isRedrawn = getIntent().getBooleanExtra("isredrawn", false);

        mapSetup();
        toolBarSetup();
    }

    public void toolBarSetup() {
        mDoneButton = (Button) findViewById(R.id.button_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepForDatabase();
            }
        });

        mDeleteButton = (Button) findViewById(R.id.button_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteFlag = true;
                mDeleteButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.VISIBLE);
            }
        });

        mCancelButton = (Button) findViewById(R.id.button_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteFlag = false;

                mCancelButton.setVisibility(View.GONE);
                mDeleteButton.setVisibility(View.VISIBLE);
            }
        });


    }

    public void mapSetup() {
        // Create ArrayList of line points
        mLinePoints = new ArrayList<LatLng>();
        // Instantiate ArrayList of Polyline objects
        mPolylines = new ArrayList<>();
        // Instantiate ArrayList of Marker objects
        mMarkers = new ArrayList<>();

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
        LatLng center;
        if (isRedrawn) {
            mPolygonOptions.addAll(mSavedPoints);
            center = findCenter(mSavedPoints);
        } else {
            mPolygonOptions.addAll(mArrayPoints);
            center = findCenter(mSavedPoints);
        }
        mPolygonOptions.addAll(mArrayPoints);
        mPolygonOptions.strokeColor(Color.BLUE);
        mPolygonOptions.strokeWidth(7);
        mFieldMap.addPolygon(mPolygonOptions);

        //mFieldMap.addMarker(new MarkerOptions().position(center));
        //mFieldMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
        //mFieldMap.animateCamera(CameraUpdateFactory.zoomIn());
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (mDeleteFlag)
            return;

        if (mMarkerClicked == false) {
            // TODO: If they click on the "closing marker", do not add another marker
            Marker tempMarker = mFieldMap.addMarker(new MarkerOptions().position(latLng));
            mMarkers.add(tempMarker); // Add into our archive of markers
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
        // If user clicked the delete button, proceed to delete line
        // associated with clicked marker
        if (mDeleteFlag) {
            marker.remove(); // Deletes marker user clicks on
            deleteMarker(marker);

        } else {
            for (int i = 0; i < mPolylines.size(); i++) {
                // If the marker that they clicked on already belongs to a line
                if (mPolylines.get(i).getPoints().contains(marker.getPosition())) {
                    Marker firstMarker = marker;
                    firstMarker.setPosition(mPolylines.get(i).getPoints().get(0));
                    showTextBox(firstMarker); // Show the text box and allow edit
                }
            }

            if (!mLinePoints.isEmpty()) { // If they haven't closed a line and the line that they click on is the first
                if (mLinePoints.get(0).equals(marker.getPosition())) {
                    // TODO: Put this check mark after they enter attributes
                    //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_check));
                    countPolylinePoints(); // Create line and close it
                    addInfoBox(marker); // Create an info box for that line
                    // Reset attributes to allow drawing new lines
                    mLinePoints.clear();
                    mMarkerClicked = false;
                }
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

        for (int i = 0; i < mPolylines.size(); i++) {
            Log.i("prepForDatabase", "i = " + i + " " + mPolylines.get(i).getPoints().toString());
        }
    }

    public LatLng findCenter(ArrayList<LatLng> points) {
        double avgX = 0, avgY = 0, avgZ = 0;
        for (int i = 0; i < points.size(); i++) {
            double lat, lon;
            lat = points.get(i).latitude;
            lon = points.get(i).longitude;


            lat = lat * (Math.PI / 180); // convert deg to rad
            lon = lon * (Math.PI / 180);

            avgX += Math.cos(lat) * Math.cos(lon); // convert the avg
            avgY += Math.cos(lat) * Math.sin(lon);
            avgZ += Math.sin(lat);
        }
        avgX /= points.size();
        avgY /= points.size();
        avgZ /= points.size();

        double lon = Math.atan2(avgY, avgX);
        double hyp = Math.sqrt(avgX * avgX + avgY * avgY);
        double lat = Math.atan2(avgZ, hyp);
        lon = lon * (180 / Math.PI);
        lat = lat * (180 / Math.PI);

        LatLng newLatLon = new LatLng(lat, lon);
        return newLatLon;
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

    public void prepForDatabase() {
        // TODO: Check if mPolylines is null

        // TODO: DATABASE PEOPLE: save these string representations into the database
        Log.i("prepForDatabase", mArrayPoints.toString());

        for (int i = 0; i < mPolylines.size(); i++) {
            Log.i("prepForDatabase", "i = " + i + " " + mPolylines.get(i).getPoints().toString());
        }

    }

    public void deleteMarker(Marker marker) {
        Log.d("deleteMarker", "mDeleteFlag == true");

        for (int i = 0; i < mPolylines.size(); i++) {
            if (mPolylines.get(i).getPoints().contains(marker.getPosition())) { // If the marker they clicked is in a line
                // Delete all markers associated with this line
                ListIterator<LatLng> iterator = mPolylines.get(i).getPoints().listIterator();

                int j = 0;
                while(j < mPolylines.get(i).getPoints().size()) {
                    int k = 0;
                    int markerSize = mMarkers.size();
                    while (k < markerSize) {
                        if (mMarkers.get(k).getPosition().equals(mPolylines.get(i).getPoints().get(j))) {
                            Log.d("deleteMarker", "mMarkers size: " + mMarkers.size() + " j= " + j + "myMarkers.size= " + markerSize);
                            mMarkers.get(k).remove();
                            markerSize--;
                        } else {
                            k++;
                        }
                    }
                    j++;
                }
                // Removes points from Polyline
                mPolylines.get(i).getPoints().clear();
                // Delete polyline associated with user marker click
                mPolylines.get(i).remove();
                Log.d("deleteMarker", " " + mPolylines.get(i).getPoints().isEmpty());
            }
        }

    }
}
