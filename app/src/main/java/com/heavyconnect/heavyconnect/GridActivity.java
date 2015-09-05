package com.heavyconnect.heavyconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class GridActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mEquips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        mEquips = (LinearLayout) findViewById(R.id.grid_equip);
        mEquips.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.grid_equip:
                startActivity(new Intent(this, EquipmentListActivity.class));
                break;
        }
    }
}
