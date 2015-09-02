package com.heavyconnect.heavyconnect;

import android.app.AlertDialog;
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
 * Created by andremenezes on 8/4/15.
 */

//LoginActivity page
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback{

    //Start variables
    Button buttonLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set variables
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);

        //Set listener
        buttonLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);


        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage("Signing in...");
        mProgress.setIndeterminate(true);

    }

    @Override
    public void onClick(View v) {
        //Get the listener and execute the function
        switch(v.getId()){
            case R.id.buttonLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                User user =  new User(username, password);

                authenticate(user);

                break;

            case R.id.tvRegisterLink:
                startActivity(new Intent(this, RegisterActivity.class ));

                break;
        }
    }

    private void authenticate(User user){
        if(mProgress != null && !mProgress.isShowing())
            mProgress.show();

        new LoginTask(this).execute(user);
    }

    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this );
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser){
        StorageUtils.storeUserData(this, returnedUser);
        StorageUtils.putIsLoggedIn(this, true);

        startActivity(new Intent(this, GridActivity.class));
    }

    @Override
    public void error(int code) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, "Invalid user or password. Try again...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void done(Object result) {
        if(mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if(!(result instanceof LoginResult))
            error(100);

        LoginResult loginResult = (LoginResult) result;
        User user = loginResult.getUser();
        logUserIn(user);
        Toast.makeText(this, "Welcome back, " +  user.getName() +  "!", Toast.LENGTH_LONG).show();

    }
}
