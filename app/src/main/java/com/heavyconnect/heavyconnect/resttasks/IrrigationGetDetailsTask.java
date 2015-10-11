package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;
import android.util.Log;

import com.heavyconnect.heavyconnect.entities.IrrigationFields;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.utils.Constants;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationGetDetailsTask extends AsyncTask<Object, Void, IrrigationFields> {

    private TaskCallback callback;

    public IrrigationGetDetailsTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected IrrigationFields doInBackground(Object... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();

        if(!(params[0] instanceof String)) {
            Log.w(Constants.DEBUG_TAG, "IrrigationFieldRegistrationTask: The first parameter must be the token.");
            return null;
        }

        if(!(params[1] instanceof Integer)) {
            Log.w(Constants.DEBUG_TAG, "IrrigationFieldRegistrationTask: The second parameter must be the IrrigationField id.");
            return null;
        }

        try {
            return retrofitClient.client.fetchIrrigationDetails((String) params[0], (Integer) params[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(IrrigationFields result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else
            callback.onTaskCompleted(result);
    }
}
