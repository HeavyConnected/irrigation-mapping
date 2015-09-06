package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.rest.EquipmentListResult;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;

/**
 * This class represents the equipment list task.
 */
public class EquipmentListTask extends AsyncTask<User, Void, EquipmentListResult> {

    private TaskCallback callback;

    public EquipmentListTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected EquipmentListResult doInBackground(User... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        User user = params[0];

        try {
            return retrofitClient.client.fetchUserEquips(user.getToken());
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
