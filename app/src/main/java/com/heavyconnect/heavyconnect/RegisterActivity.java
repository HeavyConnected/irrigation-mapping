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
 * This class represents the Register screen.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TaskCallback {

    private Button mRegisterBt;
    private EditText mNameEt, mUsernameEt, mPasswordEt, mEmailEt;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameEt = (EditText) findViewById(R.id.register_name);
        mUsernameEt = (EditText) findViewById(R.id.register_username);
        mPasswordEt = (EditText) findViewById(R.id.register_password);
        mEmailEt = (EditText) findViewById(R.id.register_email);

        mRegisterBt = (Button) findViewById(R.id.register_bt);
        mRegisterBt.setOnClickListener(this);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle(null);
        mProgress.setMessage(getString(R.string.register_signing_up));
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register_bt:
                //Get the variables values
                String name = mNameEt.getText().toString();
                String username = mUsernameEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                String email = mEmailEt.getText().toString();

                if(name.length() < 3){
                    Toast.makeText(this, getString(R.string.register_invalid_name), Toast.LENGTH_LONG).show();
                    return;
                }

                if(username.length() < 3){
                    Toast.makeText(this, getString(R.string.register_short_username), Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length() < 4){
                    Toast.makeText(this, getString(R.string.register_short_password), Toast.LENGTH_LONG).show();
                    return;
                }

                User user = new User(name, username, password, email);
                registerUser(user);

                break;
        }
    }

    /**
     * This method creates a new user in backend.
     * @param user - User to create.
     */
    private void registerUser(User user){
        if(mProgress != null &&  !mProgress.isShowing())
            mProgress.show();

        new RegisterTask(this).execute(user);
    }

    @Override
    public void onTaskFailed(int errorCode) {

        if(mProgress != null &&  mProgress.isShowing())
            mProgress.dismiss();

        String message;
        switch(errorCode){
            case 1:
                message = getString(R.string.register_invalid_method);
                break;
            case 2:
                message = getString(R.string.register_invalid_data);
                break;
            case 3:
                message = getString(R.string.register_invalid_username);
                break;
            case 4:
                message = getString(R.string.register_invalid_name);
                break;
            case 5:
                message = getString(R.string.register_invalid_password);
                break;
            case 6:
                message = getString(R.string.register_user_already_exists);
                break;
            default:
                message = getString(R.string.register_registration_failed);
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCompleted(Object result) {

        if(mProgress != null &&  mProgress.isShowing())
            mProgress.dismiss();

        Toast.makeText(this, getString(R.string.register_registration_success), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
