package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class talePlayer extends AppCompatActivity {

    private static String audioString;
    private TextView player_username_TextView, player_title_TextView;
    private ImageView player_icon_ImageView, player_down_ImageView, player_translate_ImageView;
    private String taleId, userId, title, lat, lon, datetime;

    TextView playerPosition, playerDuration;
    SeekBar seekBar;
    ImageView player_play, player_pause;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;


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
        setContentView(R.layout.activity_tale_player);

        playerPosition = findViewById(R.id.playerPosition);
        playerDuration = findViewById(R.id.playerDuration);
        seekBar = findViewById(R.id.player_seekBar);
        player_play = findViewById(R.id.player_play);
        player_pause = findViewById(R.id.player_pause);
        player_down_ImageView = findViewById(R.id.player_down);
        player_translate_ImageView = findViewById(R.id.player_translate);

        String[] parts = getIntent().getStringExtra("taleId").split("@@@");
        taleId = parts[0];
        userId = parts[1];
        title = parts[2];
        lat = parts[3];
        lon = parts[4];
        datetime = parts[5];

        System.out.println(getIntent().getStringExtra("taleId"));

        player_icon_ImageView = (ImageView) findViewById(R.id.player_icon);
        player_title_TextView = (TextView) findViewById(R.id.player_title);
        player_username_TextView = (TextView) findViewById(R.id.player_username);

        player_title_TextView.setTypeface(null, Typeface.BOLD);

        String tale_url = "http://35.194.218.135:5000/viewTale?taleId="+ taleId;
        new Thread(new Runnable(){
            public void run(){
                try {
                    JSONObject jsonObject = getJSONObjectFromURL(tale_url);
                    talePlayer.audioString = jsonObject.get("b64").toString();
                } catch (IOException | JSONException e) {}
            }
        }).start();

        System.out.println(this.audioString);
//        byte[] data = Base64.decode(this.audioString, Base64.DEFAULT);
        mediaPlayer = MediaPlayer.create(this, R.raw.music2);
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        int duration = mediaPlayer.getDuration();
        String sDuration = convertFormat(duration);
        playerDuration.setText(sDuration);

        player_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                player_play.setVisibility(View.GONE);
                player_pause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable, 0);
            }
        });

        player_pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                player_pause.setVisibility(View.GONE);
                player_play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable, 0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player_pause.setVisibility(View.GONE);
                player_play.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });

        if (!title.equals("")) {
            player_title_TextView.setText(title);
        }

        String url = "http://35.194.218.135:5000/getAccountSummary?userId="+ userId;
        new Thread(new Runnable(){
            public void run(){
                try {
                    JSONObject jsonObject = getJSONObjectFromURL(url);
                    String iconString = jsonObject.get("icon").toString();
                    if (iconString!= "null") {
                        System.out.println(iconString);
                        byte[] iconBytes = Base64.decode(iconString, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
                        for (int i=0;i<10;i++) {
                            try{
                                player_icon_ImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, player_icon_ImageView.getWidth(), player_icon_ImageView.getHeight(), false));
                                break;
                            } catch (Exception e) {}
                        }
                    }

                    String account = jsonObject.get("account").toString();
                    if (account!= "null") {
                        System.out.println(account);
                        for (int i=0;i<10;i++) {
                            try{
                                player_username_TextView.setText(account);
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

        player_down_ImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(talePlayer.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        player_translate_ImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(talePlayer.this, "Translate will available soon!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(talePlayer.this, MapsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    @Override
    public void onPause() {
        super.onPause();
        player_pause.setVisibility(View.GONE);
        player_play.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
        handler.removeCallbacks(runnable, 0);
    }

}