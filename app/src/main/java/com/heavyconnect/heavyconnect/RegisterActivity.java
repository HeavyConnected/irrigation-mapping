package com.heavyconnect.heavyconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heavyconnect.heavyconnect.entities.User;
import com.heavyconnect.heavyconnect.resttasks.TaskCallback;
import com.heavyconnect.heavyconnect.resttasks.RegisterTask;

/**
 * Created by andremenezes on 8/4/15.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback {

    Button buttonRegister;
    EditText etName, etUsername, etPassword;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage("Creating a new user...");
        mProgress.setIndeterminate(true);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonRegister:
                //Get the variables values
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(name, username, password);

                registerUser(user);

                break;
        }
    }

    private void registerUser(User user){
        if(mProgress != null &&  !mProgress.isShowing())
            mProgress.show();

        new RegisterTask(this).execute(user);
    }

    @Override
    public void error(int code) {
        Toast.makeText(this, "User registration failed!", Toast.LENGTH_LONG).show();
        if(mProgress != null &&  mProgress.isShowing())
            mProgress.dismiss();
    }

    @Override
    public void done(Object result) {
        Toast.makeText(this, "User registration done!", Toast.LENGTH_LONG).show();
        if(mProgress != null &&  mProgress.isShowing())
            mProgress.dismiss();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
