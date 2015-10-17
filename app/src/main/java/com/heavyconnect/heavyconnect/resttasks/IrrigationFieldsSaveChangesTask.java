package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;
import android.util.Log;

import com.heavyconnect.heavyconnect.entities.IrrigationFields;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.IrrigationFieldsDetailsResult;
import com.heavyconnect.heavyconnect.rest.IrrigationListResult;
import com.heavyconnect.heavyconnect.utils.Constants;

/**
 * Created by anon on 10/11/15.
 */
public class IrrigationFieldsSaveChangesTask extends AsyncTask<Object, Void, IrrigationFieldsDetailsResult> {

    private TaskCallback callback;

    public IrrigationFieldsSaveChangesTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected IrrigationFieldsDetailsResult doInBackground(Object... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();

        if(!(params[0] instanceof String)) {
            Log.w(Constants.DEBUG_TAG, "IrrigationFieldsRegistrationTask: The first parameter must be the token.");
            return null;
        }

        if(!(params[1] instanceof IrrigationFields)) {
            Log.w(Constants.DEBUG_TAG, "IrrigationFieldsRegistrationTask: The second parameter must be the Field.");
            return null;
        }

        String token = (String) params[0];
        IrrigationFields field = (IrrigationFields) params[1];

        try {
            return retrofitClient.client.saveIrrigationFieldChanges(token, field.getFieldName(),
                    field.getPipeLength(), field.getPipeDepth(), field.getPipeRow(), field.getLongitude(),
                    field.getLatitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(IrrigationFieldsDetailsResult result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != IrrigationListResult.OK)
            callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
