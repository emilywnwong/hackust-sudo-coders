package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends AppCompatActivity {
    private EditText email_editText,username_editText, password_editText;
    private Button button;

    private String url = "http://35.194.218.135:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_editText = findViewById(R.id.email_text);
        username_editText = findViewById(R.id.username_text);
        password_editText = findViewById(R.id.password_text);

        button = (Button) findViewById(R.id.gtmbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_editText.getText().toString();
                String username = username_editText.getText().toString();
                String password = password_editText.getText().toString();
                new requestAPI().execute();
//                createAccount();
            }
        });
    }

//    public void logon()
//    {
//
//    }

    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
//    public void createAccount() {
//
//    }
}