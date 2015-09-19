package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.rest.EquipmentListResult;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;

/**
 * This class represents the equipment list task.
 */
public class EquipmentListTask extends AsyncTask<Manager, Void, EquipmentListResult> {

    private TaskCallback callback;

    public EquipmentListTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected EquipmentListResult doInBackground(Manager... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        Manager manager = params[0];

        try {
            return retrofitClient.client.fetchUserEquips(manager.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(EquipmentListResult result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != EquipmentListResult.OK)
           callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
