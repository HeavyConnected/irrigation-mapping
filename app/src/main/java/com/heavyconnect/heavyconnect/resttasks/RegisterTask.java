package com.heavyconnect.heavyconnect.resttasks;

import android.os.AsyncTask;

import com.heavyconnect.heavyconnect.User;
import com.heavyconnect.heavyconnect.rest.HttpRetrofitClient;
import com.heavyconnect.heavyconnect.rest.RegisterResult;

/**
 * Created by andremenezes on 8/24/15.
 */
public class RegisterTask extends AsyncTask<User, Void, RegisterResult> {

    TaskCallback callback;

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
        // TODO: Need to implement php results!
        // if(result == null)
        //    callback.error(10);
        //else
            callback.done(null);
    }
}
