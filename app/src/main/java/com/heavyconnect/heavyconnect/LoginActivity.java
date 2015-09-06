package com.heavyconnect.heavyconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.rest.LoginResult;
import com.heavyconnect.heavyconnect.resttasks.LoginTask;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.utils.StorageUtils;

/**
 * This class represents the Login screen.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback{

    //Start variables
    Button mLoginBt;
    EditText mUsernameEt, mPasswordEt;
    TextView mSignUpTv;
    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set variables
        mUsernameEt = (EditText) findViewById(R.id.login_username);
        mPasswordEt = (EditText) findViewById(R.id.login_password);
        mLoginBt = (Button) findViewById(R.id.login_sign_in);
        mSignUpTv = (TextView) findViewById(R.id.login_sign_up);

        //Set listener
        mLoginBt.setOnClickListener(this);
        mSignUpTv.setOnClickListener(this);


        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.login_signing_in));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        // Someone is already logged in?
        if(StorageUtils.getIsLoggedIn(this)){
            User user = StorageUtils.getUserData(this);
            if(user == null){
                StorageUtils.clearPrefs(this);
                StorageUtils.putIsLoggedIn(this, false);
            }else{
                navigateToGrid(user);
            }
        }

    }

    @Override
    public void onClick(View v) {
        //Get the listener and execute the function
        switch(v.getId()){
            case R.id.login_sign_in:
                authenticate();
                break;

            case R.id.login_sign_up:
                startActivity(new Intent(this, RegisterActivity.class ));
                break;
        }
    }

    /**
     * This method verifies the user info and tries to log in.
     */
    private void authenticate(){
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();

        if(username.length() < 3){
            Toast.makeText(this, getString(R.string.login_short_username), Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() < 3){
            Toast.makeText(this, getString(R.string.login_short_password), Toast.LENGTH_LONG).show();
            return;
        }

        User user =  new User(username, password);

        if(mProgress != null && !mProgress.isShowing())
            mProgress.show();

        new LoginTask(this).execute(user);
    }


    /**
     * This method stores locally some user information and navigates to GridActivity.
     * @param returnedUser - Login result user.
     */
    private void navigateToGrid(User returnedUser){
        StorageUtils.storeUserData(this, returnedUser);
        StorageUtils.putIsLoggedIn(this, true);

        Toast.makeText(this, getString(R.string.login_welcome_back) + ", " +  returnedUser.getName() +  "!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, GridActivity.class));
        finish();
    }

    @Override
    public void onTaskFailed(int code) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        String message;
        switch(code){
            case 1:
                message = getString(R.string.login_invalid_method);
                break;
            case 2:
                message = getString(R.string.login_invalid_data);
                break;
            case 3:
                message = getString(R.string.login_invalid_username_or_password);
                break;
            case 4:
                message = getString(R.string.login_user_no_longer_active);
                break;
            case 5:
                message = getString(R.string.login_invalid_password);
                break;
            case 6:
                message = getString(R.string.login_invalid_username);
                break;
            default:
                message = getString(R.string.login_sign_in_failure);
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCompleted(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(!(result instanceof LoginResult)) {
            onTaskFailed(100);
            return;
        }

        LoginResult loginResult = (LoginResult) result;
        User user = loginResult.getUser();
        if(user == null) {
            onTaskFailed(101);
            return;
        }

        navigateToGrid(user);
    }
}
