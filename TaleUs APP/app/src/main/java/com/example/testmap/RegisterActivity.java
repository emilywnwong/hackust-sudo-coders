package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.OutputStream;
import java.io.DataOutputStream;

//import java.io.InputStreamReader;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText email_editText,username_editText, password_editText;
    private Button button;

    // Adrian: For GET request
    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        String jsonString = sb.toString();
        return new JSONObject(jsonString);
    }

    //Emily: for POST request
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public JSONObject postRequest(String urlString, HashMap<String, String> postDataParams) throws IOException, JSONException {
        OutputStream out = null;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();

        conn.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        String jsonString = sb.toString();
        return new JSONObject(jsonString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_editText = findViewById(R.id.email_text);
        username_editText = findViewById(R.id.username_text);
        password_editText = findViewById(R.id.password_text);

        button = (Button) findViewById(R.id.createButton);
        button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String email = email_editText.getText().toString();
                                        String username = username_editText.getText().toString();
                                        String password = password_editText.getText().toString();
                                        String icon = "---";
                                        String baseurl = "http://35.194.218.135:5000/createAccount?";
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("account",username);
                                        map.put("password", password);
                                        map.put("email", email);
                                        map.put("icon", icon);

                                        new Thread(new Runnable(){
                                            public void run(){
                                                try {

                                                    JSONObject jsonObject = postRequest(baseurl, map);

                                                    String createSuccess = jsonObject.get("error").toString();
                                                    System.out.println("createAccount "+username+" status - "+createSuccess);
                                                    if (createSuccess== "true") { openMapsActivity(); }
                                                    else{
                                                    //else : may have an account already -> login
                                                    String url = "http://35.194.218.135:5000/login?account="+ username+"&password="+password;
                                                    new Thread(new Runnable(){
                                                        public void run(){
                                                            try {
                                                                JSONObject jsonObject = getJSONObjectFromURL(url);
                                                                System.out.println(jsonObject);
                                                                String userId = jsonObject.get("userId").toString();
                                                                if (userId!= "null") {
                                                                    System.out.println(userId);
                                                                    openMapsActivity();
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();}
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                });
    }



    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}