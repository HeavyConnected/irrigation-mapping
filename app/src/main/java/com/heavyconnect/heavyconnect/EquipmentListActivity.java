package com.heavyconnect.heavyconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.heavyconnect.heavyconnect.adapters.EquipmentListAdapter;
import com.heavyconnect.heavyconnect.entities.Equipment;

import java.util.ArrayList;
import java.util.Random;

public class EquipmentListActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mListView;
    private EquipmentListAdapter mAdapter;
    private ArrayList<Equipment> mEquips = new ArrayList<Equipment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        mListView = (ListView) findViewById(R.id.equip_list_view);

        Random rd = new Random();

        mEquips.clear();
        for(int i = 0; i < 10; i++)
            mEquips.add(new Equipment("Tractor #" + i, rd.nextInt(3)));

        mAdapter = new EquipmentListAdapter(this, mEquips);
        mListView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_equip_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.equip_add:
                startActivity(new Intent(this, EquipmentRegisterActivity.class));
                break;
        }
    }
}
