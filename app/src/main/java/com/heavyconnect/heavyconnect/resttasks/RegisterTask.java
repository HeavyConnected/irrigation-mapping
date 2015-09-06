package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.RegisterResult;

/**
 * This class represents the register task.
 */
public class RegisterTask extends AsyncTask<User, Void, RegisterResult> {

    private TaskCallback callback;

    public RegisterTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected RegisterResult doInBackground(User... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        User user = params[0];

        try {
            return retrofitClient.client.createUser(user.getName(), user.getUsername(), user.getPassword());
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
