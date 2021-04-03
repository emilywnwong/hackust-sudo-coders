package com.example.testmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;

public class requestAPI extends AsyncTask<String, Void, String> {
    private final static String TAG = "HTTPURLCONNECTION test";
    private String parameter1;
    private String parameter2;
    private String parameter3;

    @Override
    protected String doInBackground(String... urls) {
        return POST(urls[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"onPostExecute");
    }
    private String POST(String APIUrl) {
        String result = "";
        HttpURLConnection connection;
        try {
            URL url = new URL(APIUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
//            connection.setRequestProperty("authentication", RegisterActivity.Authentication);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("parameter1=").append(URLEncoder.encode(parameter1, "UTF-8")).append("&");
            stringBuilder.append("parameter2=").append(URLEncoder.encode(parameter2, "UTF-8")).append("&");
            stringBuilder.append("parameter3=").append(URLEncoder.encode(parameter3, "UTF-8")).append("&");
            outputStream.writeBytes(stringBuilder.toString());
            outputStream.flush();
            outputStream.close();


            InputStream inputStream = connection.getInputStream();
            int status = connection.getResponseCode();
            Log.d(TAG, String.valueOf(status));
            if(inputStream != null){
                InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader in = new BufferedReader(reader);

                String line="";
                while ((line = in.readLine()) != null) {
                    result += (line+"\n");
                }
            } else{
                result = "Did not work!";
            }
            return  result;
        } catch (Exception e) {
            Log.d("ATask InputStream", e.getLocalizedMessage());
            e.printStackTrace();
            return result;
        }
    }
}

