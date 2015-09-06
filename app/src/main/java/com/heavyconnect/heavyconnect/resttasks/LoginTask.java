package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.LoginResult;

/**
 * This class represents the login task.
 */
public class LoginTask extends AsyncTask<User, Void, LoginResult> {

    private TaskCallback callback;

    public LoginTask(TaskCallback userCallback) {
        this.callback = userCallback;
    }

    @Override
    protected LoginResult doInBackground(User... params) {
        HttpRetrofitClient retrofitClient = new HttpRetrofitClient();
        User user = params[0];

        try {
            return retrofitClient.client.fetchUser(user.getUsername(), user.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(LoginResult result) {
        if(result == null)
            callback.onTaskFailed(-1);
        else if(result.getStatus() != LoginResult.OK)
           callback.onTaskFailed(result.getStatus());
        else
            callback.onTaskCompleted(result);
    }
}
