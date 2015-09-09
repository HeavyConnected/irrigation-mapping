package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.Manager;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.RegisterResult;

/**
 * This class represents the register task.
 */
public class RegisterTask extends AsyncTask<Manager, Void, RegisterResult> {

    private TaskCallback callback;

    public RegisterTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected RegisterResult doInBackground(Manager... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        Manager manager = params[0];

        try {
            return retrofitClient.client.createUser(manager.getFirstName(), manager.getLastName(), manager.getUsername(), manager.getPassword(), manager.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(RegisterResult result) {
         if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != RegisterResult.OK)
            callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
