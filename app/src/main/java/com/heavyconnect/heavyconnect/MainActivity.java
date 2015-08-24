package com.heavyconnect.heavyconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by andremenezes on 8/4/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogout;
    EditText etName, etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true){
            displayUserDetails();
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private boolean authenticate(){
        return StorageUtils.getIsLoggedIn(this);
    }

    private void displayUserDetails(){
        User user = StorageUtils.getUserData(this);
        etUsername.setText(user.username);
        etName.setText(user.name);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonLogout:
                StorageUtils.clearPrefs(this);
                startActivity(new Intent(this, LoginActivity.class));

                break;
        }
    }
}
