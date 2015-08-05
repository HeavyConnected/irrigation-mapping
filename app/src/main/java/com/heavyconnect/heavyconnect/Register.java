package com.heavyconnect.heavyconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by andremenezes on 8/4/15.
 */
public class Register extends AppCompatActivity implements View.OnClickListener{

    Button buttonRegister;
    EditText etName, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonRegister:
                //Get the variables values
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                int password = Integer.parseInt(etPassword.getText().toString());

                User registeredData = new User(name, username, password);

                break;
        }
    }
}
