package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {
    public static String userId;
    public static String username;
    private EditText username_editText, password_editText;
    private ImageView LoginButton, RegisterButton;

    // Adrian: For GET request
    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        String jsonString = null;

        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        for (int i = 0;  i < 10 ; i++){
            try{
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            jsonString = sb.toString();
            break;
            }
            catch (ProtocolException e){ }
        }

        return new JSONObject(jsonString);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_editText = findViewById(R.id.username_text);
        password_editText = findViewById(R.id.password_text);
        LoginButton = (ImageView) findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               String username = username_editText.getText().toString();
                                               String password = password_editText.getText().toString();
                                               String url = "http://35.194.218.135:5000/login?account="+ username+"&password="+password;
                                               new Thread(new Runnable(){
                                                   public void run(){
                                                       try {
                                                           JSONObject jsonObject = getJSONObjectFromURL(url);
                                                           System.out.println(jsonObject);
                                                           String userId = jsonObject.get("userId").toString();
                                                           if (userId!= "null") {
                                                               LoginActivity.userId = userId;
                                                               LoginActivity.username = username;
                                                               System.out.println(userId);
                                                               openMapsActivity();
                                                           }
                                                       } catch (IOException e) {
                                                           e.printStackTrace();
                                                       } catch (JSONException e) {
                                                           e.printStackTrace();
                                                       }
                                                   }
                                               }).start();
                                           }
                                       });

        RegisterButton = (ImageView) findViewById(R.id.RegisterButton);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                openRegisterActivity();
                                            }
                                        });


    }



    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}