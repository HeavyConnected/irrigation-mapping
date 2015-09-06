package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.geolocation.GPSTracker;
import com.heavyconnect.heavyconnect.geolocation.OnLocationChangedListener;
import com.heavyconnect.heavyconnect.rest.AddEquipmentResult;
import com.heavyconnect.heavyconnect.resttasks.AddEquipmentTask;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

/**
 * This class represents the Equipment Registration screen.
 */
public class EquipmentRegistrationActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback, OnLocationChangedListener {

    public static int ADD_EQUIPMENT_REQUEST_CODE = 5322;
    public static String ADD_EQUIPMENT_RESULT_KEY = "equip";

    private EditText mName;
    private EditText mEquipModel;
    private EditText mAssetNumber;
    private EditText mEngineHours;

    private RadioGroup mStatus;
    private Button mClear;
    private Button mAdd;

    private ProgressDialog mProgress;
    private User mUser;

    private Location mLocation;
    private Equipment mEquip = new Equipment();
    private boolean isSendingEquip = false;
    private GPSTracker mGPSTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.equip_reg_sending));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        if(!StorageUtils.getIsLoggedIn(this) || (mUser = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.equip_reg_user_isnt_logged_in), Toast.LENGTH_LONG).show();
            finish();
        }

        mGPSTracker = new GPSTracker(this);
        mGPSTracker.setOnLocationChangedListener(this);

        mName = (EditText) findViewById(R.id.equip_reg_name);
        mEquipModel = (EditText) findViewById(R.id.equip_reg_model_number);
        mAssetNumber = (EditText) findViewById(R.id.equip_reg_asset_number);
        mEngineHours = (EditText) findViewById(R.id.equip_reg_engine_hours);

        mStatus = (RadioGroup) findViewById(R.id.equip_reg_status);

        mClear = (Button) findViewById(R.id.equip_reg_clear);
        mClear.setOnClickListener(this);

        mAdd = (Button) findViewById(R.id.equip_reg_add);
        mAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.equip_reg_clear:
                clearFields();
                break;
            case R.id.equip_reg_add:
                addEquip();
                break;
        }

    }

    /**
     * This method verifies the information and tries to register the new equipment in backend.
     */
    private void addEquip(){
        String name = mName.getText().toString();
        String model = mEquipModel.getText().toString();
        String asset = mAssetNumber.getText().toString();
        String hours = mEngineHours.getText().toString();

        int status;
        switch(mStatus.getCheckedRadioButtonId()){
            default:
            case R.id.equip_reg_status_ok_radio:
                status = Equipment.STATUS_OK;
                break;
            case R.id.equip_reg_status_service_radio:
                status = Equipment.STATUS_SERVICE;
                break;
            case R.id.equip_reg_status_broken_radio:
                status = Equipment.STATUS_BROKEN;
                break;
        }

        if(name.length() < 3) {
            Toast.makeText(this, getString(R.string.equip_reg_invalid_name), Toast.LENGTH_LONG).show();
            return;
        }

        if((model.length() < 1) || !model.matches("^\\d{1,}$")){
            Toast.makeText(this, getString(R.string.equip_reg_invalid_model_number), Toast.LENGTH_LONG).show();
            return;
        }

        if((asset.length() < 1) || !asset.matches("^\\d{1,}$")){
            Toast.makeText(this, getString(R.string.equip_reg_invalid_asset_number), Toast.LENGTH_LONG).show();
            return;
        }

        if((hours.length() < 1) || !hours.matches("^\\d{1,}$")){
            Toast.makeText(this, getString(R.string.equip_reg_invalid_engine_hours_value), Toast.LENGTH_LONG).show();
            return;
        }

        mEquip.setName(name);
        mEquip.setStatus(status);
        mEquip.setModelNumber(Integer.parseInt(model));
        mEquip.setAssetNumber(Integer.parseInt(asset));
        mEquip.setEngineHours(Integer.parseInt(hours));

        if(mProgress != null && !mProgress.isShowing())
            mProgress.show();

        isSendingEquip = true;
        if(mLocation != null)
            new AddEquipmentTask(this).execute(mUser.getToken(), mEquip);
    }

    /**
     * This method clears all fields.
     */
    private void clearFields(){
        mName.setText("");
        mEquipModel.setText("");
        mAssetNumber.setText("");
        mEngineHours.setText("");
    }


    @Override
    public void onTaskFailed(int code) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, getString(R.string.equip_reg_registration_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(!(result instanceof AddEquipmentResult)) {
            onTaskFailed(100);
            return;
        }

        AddEquipmentResult rs = (AddEquipmentResult) result;
        Toast.makeText(this, getString(R.string.equip_reg_registration_success), Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        intent.putExtra(ADD_EQUIPMENT_RESULT_KEY, new Gson().toJson(rs.getData()));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mEquip.setLatitude(mLocation.getLatitude());
        mEquip.setLongitude(mLocation.getLongitude());
        if(isSendingEquip){
            new AddEquipmentTask(this).execute(mUser.getToken(), mEquip);
        }
    }
}
