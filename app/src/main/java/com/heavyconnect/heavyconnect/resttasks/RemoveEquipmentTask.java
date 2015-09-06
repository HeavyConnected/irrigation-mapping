package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;
import android.util.Log;

import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.utils.Constants;

/**
 * This class represents the equipment details task.
 */
public class RemoveEquipmentTask extends AsyncTask<Object, Void, Equipment> {

    private TaskCallback callback;

    public RemoveEquipmentTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected Equipment doInBackground(Object... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();

        if(!(params[0] instanceof String)) {
            Log.w(Constants.DEBUG_TAG, "EquipmentRegistrationTaskØ: The first parameter must be the token.");
            return null;
        }

        if(!(params[1] instanceof Integer)) {
            Log.w(Constants.DEBUG_TAG, "EquipmentRegistrationTask: The second parameter must be the Equipment id.");
            return null;
        }

        try {
            return retrofitClient.client.fetchEquipmentDetails((String) params[0], (Integer) params[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Equipment result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else
            callback.onTaskCompleted(result);
    }
}
