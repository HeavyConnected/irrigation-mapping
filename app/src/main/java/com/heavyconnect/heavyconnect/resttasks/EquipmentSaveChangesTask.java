package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;
import android.util.Log;

import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.rest.AddEquipmentResult;
import com.heavyconnect.heavyconnect.rest.EquipmentListResult;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.utils.Constants;

/**
 * This class represents the equipment registration task.
 */
public class EquipmentSaveChangesTask extends AsyncTask<Object, Void, AddEquipmentResult> {

    private TaskCallback callback;

    public EquipmentSaveChangesTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected AddEquipmentResult doInBackground(Object... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();

        if(!(params[0] instanceof String)) {
            Log.w(Constants.DEBUG_TAG, "EquipmentRegistrationTaskØ: The first parameter must be the token.");
            return null;
        }

        if(!(params[1] instanceof Equipment)) {
            Log.w(Constants.DEBUG_TAG, "EquipmentRegistrationTask: The second parameter must be the Equip.");
            return null;
        }
        String token = (String) params[0];
        Equipment equip = (Equipment) params[1];

        try {
            return retrofitClient.client.createEquip(token, equip.getName(), equip.getModelNumber(),
                    equip.getAssetNumber(), equip.getStatus(), equip.getEngineHours(),
                    equip.getLatitude(), equip.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AddEquipmentResult result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != EquipmentListResult.OK)
           callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
