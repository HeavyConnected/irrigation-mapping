package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.IrrigationListResult;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationFieldsListTask extends AsyncTask<Manager, Void, IrrigationListResult> {

    private TaskCallback callback;

    public IrrigationFieldsListTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected IrrigationListResult doInBackground(Manager... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        Manager manager = params[0];

        try {
            return retrofitClient.client.fetchUserIrrigationFields(manager.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(IrrigationListResult result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != IrrigationListResult.OK)
            callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
