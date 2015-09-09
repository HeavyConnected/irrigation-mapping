package com.heavyconnect.heavyconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

/**
 * This class represents the Grid screen.
 */
public class GridActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mEquips;
    private LinearLayout mMap;
    private LinearLayout mSettings;
    private LinearLayout mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        if(!StorageUtils.getIsLoggedIn(this)){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.grid_user_isnt_logged_in), Toast.LENGTH_LONG).show();
            finish();
        }

        mEquips = (LinearLayout) findViewById(R.id.grid_equip);
        mEquips.setOnClickListener(this);

        mMap = (LinearLayout) findViewById(R.id.grid_map);
        mMap.setOnClickListener(this);

        mSettings = (LinearLayout) findViewById(R.id.grid_settings);
        mSettings.setOnClickListener(this);

        mExit = (LinearLayout) findViewById(R.id.grid_exit);
        mExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.grid_equip: // Equipments
                startActivity(new Intent(this, EquipmentListActivity.class));
                break;
            case R.id.grid_map:
                startActivity(new Intent(this, MapActivity.class));
                break;
            case R.id.grid_exit: // Logout
                logout();
                break;
        }
    }

    /**
     * This method logs out the user.
     */
    private void logout(){
        StorageUtils.clearPrefs(this);
        StorageUtils.putIsLoggedIn(this, false);
        Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
