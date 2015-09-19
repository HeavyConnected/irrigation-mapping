package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.heavyconnect.heavyconnect.adapters.EquipmentListAdapter;
import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.rest.EquipmentListResult;
import com.heavyconnect.heavyconnect.resttasks.EquipmentListTask;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

import java.util.ArrayList;

/**
 * This class represents the Equipment List screen.
 */
public class EquipmentListActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback{

    private ListView mListView;
    private EquipmentListAdapter mAdapter;
    private ArrayList<Equipment> mEquips = new ArrayList<Equipment>();

    private Manager mManager;
    private ProgressDialog mProgress;

    private Button mAddEquip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        if(!StorageUtils.getIsLoggedIn(this) || (mManager = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.equip_list_user_isnt_logged_in), Toast.LENGTH_LONG).show();
            finish();
        }

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.equip_list_loading));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        mAddEquip = (Button) findViewById(R.id.equip_list_add);
        mAddEquip.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.equip_list_view);
        loadEquipments();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method loads the equipment list.
     */
    private void loadEquipments(){
        mEquips.clear();

        if(mProgress != null &&  !mProgress.isShowing())
            mProgress.show();

        new EquipmentListTask(this).execute(mManager);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.equip_list_add:startActivityForResult(new Intent(this, EquipmentRegistrationActivity.class), EquipmentRegistrationActivity.ADD_EQUIPMENT_REQUEST_CODE);

                break;
        }
    }


    @Override
    public void onTaskFailed(int code) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, getString(R.string.equip_list_load_failure), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(!(result instanceof EquipmentListResult)) {
            onTaskFailed(100);
            return;
        }

        mEquips = ((EquipmentListResult) result).getUserEquips();

        mAdapter = new EquipmentListAdapter(this, mEquips);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int equipId = mAdapter.get(position).getId();
                Intent intent = new Intent(EquipmentListActivity.this, EquipmentRegistrationActivity.class);
                intent.putExtra(EquipmentRegistrationActivity.MODE_KEY, EquipmentRegistrationActivity.EQUIPMENT_DETAILS_MODE);
                intent.putExtra(EquipmentRegistrationActivity.EQUIP_ID_KEY, equipId);
                startActivityForResult(intent, EquipmentRegistrationActivity.EDIT_EQUIPMENT_REQUEST_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
            loadEquipments();
//
//        if(requestCode == EquipmentRegistrationActivity.ADD_EQUIPMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
//            Bundle extras = data.getExtras();
//            Equipment newEquip;
//            if(extras != null){
//                newEquip = new Gson().fromJson((String) extras.get(EquipmentRegistrationActivity.ADD_EQUIPMENT_RESULT_KEY), Equipment.class);
//                if(mAdapter != null)
//                    mAdapter.add(newEquip);
//            }
//        }
//
//        if(requestCode == EquipmentRegistrationActivity.EDIT_EQUIPMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
//            Bundle extras = data.getExtras();
//            Equipment newEquip;
//            if(extras != null){
//                newEquip = new Gson().fromJson((String) extras.get(EquipmentRegistrationActivity.ADD_EQUIPMENT_RESULT_KEY), Equipment.class);
//                if(mAdapter != null)
//                    mAdapter.update(newEquip);
//            }else{
//                mAdapter.remove()
//            }
//        }
    }
}
