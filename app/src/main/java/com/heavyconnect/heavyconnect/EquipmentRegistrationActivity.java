package com.heavyconnect.heavyconnect;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.geolocation.GPSTracker;
import com.heavyconnect.heavyconnect.geolocation.OnLocationChangedListener;
import com.heavyconnect.heavyconnect.rest.EquipmentDetailsResult;
import com.heavyconnect.heavyconnect.resttasks.EquipmentGetDetailsTask;
import com.heavyconnect.heavyconnect.resttasks.EquipmentRegistrationTask;
import com.heavyconnect.heavyconnect.resttasks.EquipmentSaveChangesTask;
import com.heavyconnect.heavyconnect.resttasks.RemoveEquipmentTask;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

/**
 * This class represents the Equipment Registration screen.
 */
public class EquipmentRegistrationActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback, OnLocationChangedListener {

    public static int ADD_EQUIPMENT_REQUEST_CODE = 5322;
    public static int EDIT_EQUIPMENT_REQUEST_CODE = 5323;

    public static String ADD_EQUIPMENT_RESULT_KEY = "newEquip";
    public static String UPDATE_EQUIPMENT_RESULT_KEY = "upEquip";

    public static String MODE_KEY = "mode";
    public static String EQUIP_ID_KEY = "id";
    public static int EQUIPMENT_REGISTRATION_MODE = 1;
    public static int EQUIPMENT_DETAILS_MODE = 2;

    private EditText mNameEt;
    private EditText mEquipModelEt;
    private EditText mAssetNumberEt;
    private EditText mEngineHoursEt;

    private RadioGroup mStatusRg;
    private RadioButton mOkRd;
    private RadioButton mServiceRd;
    private RadioButton mBrokenRd;

    private Button mClearBt;
    private Button mAddBt;

    private Button mRemoveBt;
    private Button mMapBt;
    private Button mSaveBt;

    private ProgressDialog mProgress;
    private User mUser;

    private Location mLocation;
    private Equipment mEquip = new Equipment();
    private boolean isSendingEquip = false;
    private GPSTracker mGPSTracker;

    private Dialog mRemoveDialog;

    private int mMode = EQUIPMENT_REGISTRATION_MODE;
    private int mEquipId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_registration_form);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        if(!StorageUtils.getIsLoggedIn(this) || (mUser = StorageUtils.getUserData(this)) == null){
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, getString(R.string.equip_reg_user_isnt_logged_in), Toast.LENGTH_LONG).show();
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mMode = extras.getInt(MODE_KEY, EQUIPMENT_REGISTRATION_MODE);
            mEquipId = extras.getInt(EQUIP_ID_KEY, -1);
        }

        mNameEt = (EditText) findViewById(R.id.equip_reg_name);
        mEquipModelEt = (EditText) findViewById(R.id.equip_reg_model_number);
        mAssetNumberEt = (EditText) findViewById(R.id.equip_reg_asset_number);
        mEngineHoursEt = (EditText) findViewById(R.id.equip_reg_engine_hours);

        mStatusRg = (RadioGroup) findViewById(R.id.equip_reg_status);
        mOkRd = (RadioButton) findViewById(R.id.equip_reg_status_ok_radio);
        mServiceRd = (RadioButton) findViewById(R.id.equip_reg_status_service_radio);
        mBrokenRd = (RadioButton) findViewById(R.id.equip_reg_status_broken_radio);

        mClearBt = (Button) findViewById(R.id.equip_reg_clear);
        mClearBt.setOnClickListener(this);

        mAddBt = (Button) findViewById(R.id.equip_reg_add);
        mAddBt.setOnClickListener(this);

        mRemoveBt = (Button) findViewById(R.id.equip_reg_remove);
        mRemoveBt.setOnClickListener(this);

        mMapBt = (Button) findViewById(R.id.equip_reg_map);
        mMapBt.setOnClickListener(this);

        mSaveBt = (Button) findViewById(R.id.equip_reg_save);
        mSaveBt.setOnClickListener(this);

        // Remove dialog - BEGIN
        mRemoveDialog = new Dialog(this);
        mRemoveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRemoveDialog.setContentView(R.layout.dialog_ask);
        ((TextView) mRemoveDialog.findViewById(R.id.dialog_ask_message)).setText(getString(R.string.equip_reg_remove_confirmation));
        Button yesBt = (Button) mRemoveDialog.findViewById(R.id.dialog_ask_ok);
        yesBt.setText(getString(R.string.equip_reg_remove_yes));
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEquip();
                if(mRemoveDialog != null && mRemoveDialog.isShowing())
                    mRemoveDialog.dismiss();
            }
        });

        Button noBt = (Button) mRemoveDialog.findViewById(R.id.dialog_ask_cancel);
        noBt.setText(getString(R.string.equip_reg_remove_no));
        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRemoveDialog != null && mRemoveDialog.isShowing())
                    mRemoveDialog.dismiss();
            }
        });
        // Remove dialog - END


        mGPSTracker = new GPSTracker(this);
        mGPSTracker.setOnLocationChangedListener(this);

        if(mMode == EQUIPMENT_REGISTRATION_MODE) {
            findViewById(R.id.equip_reg_new_equip_bts).setVisibility(View.VISIBLE);
            findViewById(R.id.equip_reg_edit_equip_bts).setVisibility(View.GONE);
        }else{
            if(mEquipId == -1) {
                Toast.makeText(this, getString(R.string.equip_reg_invalid_equip_id), Toast.LENGTH_LONG).show();
                finish();
            }

            findViewById(R.id.equip_reg_new_equip_bts).setVisibility(View.GONE);
            findViewById(R.id.equip_reg_edit_equip_bts).setVisibility(View.VISIBLE);

            mProgress.setMessage(getString(R.string.equip_reg_loading));
            if(mProgress != null && !mProgress.isShowing())
                mProgress.show();

            new EquipmentGetDetailsTask(this).execute(mUser.getToken(), mEquipId);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.equip_reg_clear:
                clearFields();
                break;
            case R.id.equip_reg_add:
                saveEquip();
                break;
            case R.id.equip_reg_remove:
                mRemoveDialog.show();
                break;
            case R.id.equip_reg_map:
                //TODO: Implement map.
                break;
            case R.id.equip_reg_save:
                saveEquip();
                break;
        }

    }

    /**
     * This method verifies the information and tries to register the new equipment in backend.
     */
    private void saveEquip(){
        String name = mNameEt.getText().toString();
        String model = mEquipModelEt.getText().toString();
        String asset = mAssetNumberEt.getText().toString();
        String hours = mEngineHoursEt.getText().toString();

        int status;
        switch(mStatusRg.getCheckedRadioButtonId()){
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

        if(model.length() < 1){
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

        mEquip.setId(mEquipId);
        mEquip.setName(name);
        mEquip.setStatus(status);
        mEquip.setModelNumber(Integer.parseInt(model));
        mEquip.setAssetNumber(Integer.parseInt(asset));
        mEquip.setEngineHours(Integer.parseInt(hours));


        isSendingEquip = true;
        if (mLocation != null) {
            mProgress.setMessage(getString(R.string.equip_reg_sending));
            if (mMode == EQUIPMENT_REGISTRATION_MODE) {
                new EquipmentRegistrationTask(this).execute(mUser.getToken(), mEquip);
            } else {
                new EquipmentSaveChangesTask(this).execute(mUser.getToken(), mEquip);
            }
        }else {
            mProgress.setMessage(getString(R.string.equip_reg_getting_location));
        }

        if(mProgress != null && !mProgress.isShowing())
            mProgress.show();
    }

    /**
     * Removes equipment.
     */
    private void removeEquip(){


        if(mProgress != null && !mProgress.isShowing())
            mProgress.show();

        new RemoveEquipmentTask(new TaskCallback() {
            @Override
            public void onTaskFailed(int errorCode) {

                if(mProgress != null && mProgress.isShowing())
                    mProgress.dismiss();

                Toast.makeText(EquipmentRegistrationActivity.this, getString(R.string.equip_reg_failed_when_removing), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskCompleted(Object result) {
                if(mProgress != null && mProgress.isShowing())
                    mProgress.dismiss();

                Toast.makeText(EquipmentRegistrationActivity.this, getString(R.string.equip_reg_successfully_removed), Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }).execute(mUser.getToken(), mEquip.getId());
    }

    /**
     * This method clears all fields.
     */
    private void clearFields(){
        mNameEt.setText("");
        mEquipModelEt.setText("");
        mAssetNumberEt.setText("");
        mEngineHoursEt.setText("");
    }


    @Override
    public void onTaskFailed(int code) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(mMode == EQUIPMENT_REGISTRATION_MODE)
            Toast.makeText(this, getString(R.string.equip_reg_registration_failure), Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(this, getString(R.string.equip_reg_loading_failure), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(result instanceof EquipmentDetailsResult) {
            EquipmentDetailsResult rs = (EquipmentDetailsResult) result;
            if(mMode == EQUIPMENT_REGISTRATION_MODE)
                Toast.makeText(this, getString(R.string.equip_reg_registration_success), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, getString(R.string.equip_reg_successfully_saved), Toast.LENGTH_LONG).show();

            Intent intent = getIntent();
            intent.putExtra(mMode == EQUIPMENT_REGISTRATION_MODE? ADD_EQUIPMENT_RESULT_KEY : UPDATE_EQUIPMENT_RESULT_KEY,
                    new Gson().toJson(rs.getData()));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        if(result instanceof Equipment){
            mEquip = (Equipment) result;
            mNameEt.setText(mEquip.getName());
            mEquipModelEt.setText(Integer.toString(mEquip.getModelNumber()));
            mAssetNumberEt.setText(Integer.toString(mEquip.getAssetNumber()));
            mEngineHoursEt.setText(Integer.toString(mEquip.getEngineHours()));

            switch(mEquip.getStatus()){
                case Equipment.STATUS_OK:
                    mOkRd.setChecked(true);
                    break;
                case Equipment.STATUS_SERVICE:
                    mServiceRd.setChecked(true);
                    break;
                case Equipment.STATUS_BROKEN:
                    mBrokenRd.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mEquip.setLatitude(mLocation.getLatitude());
        mEquip.setLongitude(mLocation.getLongitude());

        if(isSendingEquip){
            mProgress.setMessage(getString(R.string.equip_reg_sending));
            if(mMode == EQUIPMENT_REGISTRATION_MODE) {
                new EquipmentRegistrationTask(this).execute(mUser.getToken(), mEquip);
            }else{
                new EquipmentSaveChangesTask(this).execute(mUser.getToken(), mEquip);
            }
        }
    }
}
