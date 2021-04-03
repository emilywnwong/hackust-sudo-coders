package com.example.testmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

import java.io.BufferedWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class requestAPI extends AsyncTask<String, String, String> {
    private final static String TAG = "HTTPURLCONNECTION test";

    @Override
    protected String doInBackground(String... params) { try {
        String NewsData;
        //define the url we have to connect with
        URL url = new URL(params[0]);
        Log.d(TAG,params[0]);
        //make connect with url and send request
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //waiting for 7000ms for response
        urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

        try {
            //getting the response data
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //convert the stream to string
            NewsData = ConvertInputToStringNoChange(in);
            //send to display data
            publishProgress(NewsData);
            Log.d(TAG,NewsData);
        } finally {
            //end connection
            Log.d(TAG,"failed");
            urlConnection.disconnect();
        }

    }catch (Exception ex){}
        return null;}


    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"onPostExecute");
    }
    // this method convert any stream to string
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {

                linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }

}

