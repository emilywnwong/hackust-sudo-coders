package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private TextView username_TextView, tales_count_TextView, follower_count_TextView, following_count_TextView;
    private ImageView icon_ImageView;

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
        setContentView(R.layout.activity_profile);

        username_TextView = (TextView) findViewById(R.id.username_text);
        tales_count_TextView = (TextView) findViewById(R.id.tales_text);
        follower_count_TextView = (TextView) findViewById(R.id.follower_text);
        following_count_TextView = (TextView) findViewById(R.id.following_text);
        icon_ImageView = (ImageView) findViewById(R.id.icon_view);

        // need to get userId & username after login/ register
        String userId = "1";
        String username = "@adrian";
        String url = "http://35.194.218.135:5000/getAccountSummary?userId="+ userId;

        username_TextView.setText(username);

        new Thread(new Runnable(){
            public void run(){
                try {
                    JSONObject jsonObject = getJSONObjectFromURL(url);
                    System.out.println(jsonObject);
                    String talesCount = jsonObject.get("talesCount").toString();
                    if (talesCount!= "null") {
                        System.out.println(talesCount);
                        for (int i=0;i<10;i++) {
                            try{
                            tales_count_TextView.setText("Tales: " + talesCount);
                            break;
                            } catch (Exception e) {}
                        }
                    }
                    String followerCount = jsonObject.get("followerCount").toString();
                    if (followerCount!= "null") {
                        System.out.println(followerCount);
                        for (int i=0;i<10;i++) {
                            try{
                                follower_count_TextView.setText("Follower: "+followerCount);
                                break;
                            } catch (Exception e) {}
                        }
                    }
                    String followingCount = jsonObject.get("followingCount").toString();
                    if (followingCount!= "null") {
                        System.out.println(followingCount);
                        for (int i=0;i<10;i++) {
                            try{
                                following_count_TextView.setText("Following: "+followingCount);
                                break;
                            } catch (Exception e) {}
                        }
                    }
                    String iconString = jsonObject.get("icon").toString();
                    if (iconString!= "null") {
                        System.out.println(iconString);
                        byte[] iconBytes = Base64.decode(iconString, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
                        for (int i=0;i<10;i++) {
                            try{
                                icon_ImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, icon_ImageView.getWidth(), icon_ImageView.getHeight(), false));
                                break;
                            } catch (Exception e) {}
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}