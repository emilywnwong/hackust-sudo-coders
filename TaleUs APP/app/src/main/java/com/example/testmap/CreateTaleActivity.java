package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CreateTaleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String privacy;
    private String timelimit;

    MediaRecorder mediaRecorder;

    public static String filename = "record.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filename;


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
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_tale);
        Chronometer timer = (Chronometer) findViewById(R.id.record_timer);

        ImageView back_btn = (ImageView) findViewById(R.id.record_back);
        ImageView record_btn = (ImageView) findViewById(R.id.record_record);
        ImageView pause_btn = (ImageView) findViewById(R.id.record_pause);
        ImageView done_btn = (ImageView) findViewById(R.id.record_done);
        ImageView cancel_btn = (ImageView) findViewById(R.id.record_cancel);

        ImageView public_active = (ImageView) findViewById(R.id.record_public_active);
        ImageView public_inactive = (ImageView) findViewById(R.id.record_public_inactive);
        ImageView friend_active = (ImageView) findViewById(R.id.record_friend_active);
        ImageView friend_inactive = (ImageView) findViewById(R.id.record_friend_inactive);
        ImageView next_btn = (ImageView) findViewById(R.id.record_next);
        ImageView who_can_view = (ImageView) findViewById(R.id.who_can);

        ImageView _48h_btn = (ImageView) findViewById(R.id.record_48h_active);
        ImageView _72h_btn = (ImageView) findViewById(R.id.record_72h_inactive);
        ImageView _1w_btn = (ImageView) findViewById(R.id.record_1w_inactive);
        ImageView _forever_btn = (ImageView) findViewById(R.id.record_forever_inactive);
        ImageView post_btn = (ImageView) findViewById(R.id.record_post);
        ImageView duration = (ImageView) findViewById(R.id.record_duration);


        TextView title = (TextView) findViewById(R.id.record_title);
        title.setTypeface(null, Typeface.BOLD);


//        Spinner privacy_spinner = (Spinner) findViewById(R.id.record_privacy);
//        ArrayAdapter<CharSequence> privacy_adapter = ArrayAdapter.createFromResource(this,
//                R.array.talePrivacy_array, android.R.layout.simple_spinner_item);
//        privacy_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        privacy_spinner.setAdapter(privacy_adapter);
//        privacy_spinner.setOnItemSelectedListener(this);
//
//
//        Spinner timelimit_spinner = (Spinner) findViewById(R.id.record_timelimit);
//        ArrayAdapter<CharSequence> timelimit_adapter = ArrayAdapter.createFromResource(this,
//                R.array.taleTimeLimit_array, android.R.layout.simple_spinner_item);
//        timelimit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        timelimit_spinner.setAdapter(timelimit_adapter);
//        timelimit_spinner.setOnItemSelectedListener(this);


        int file_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If we don't have permissions, ask user for permissions
        if (file_permission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            int REQUEST_EXTERNAL_STORAGE = 1;

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        int mic_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        // If we don't have permissions, ask user for permissions
        if (mic_permission != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_RECORD_AUDIO = 1;

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        }


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(file);

                    record_btn.setVisibility(View.GONE);
                    pause_btn.setVisibility(View.VISIBLE);
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause_btn.setVisibility(View.GONE);
                record_btn.setVisibility(View.VISIBLE);
                mediaRecorder.stop();
                mediaRecorder.release();
                timer.stop();
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                try {
//                    mediaPlayer.setDataSource(file);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                cancel_btn.setVisibility(View.GONE);
                done_btn.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);

                public_active.setVisibility(View.VISIBLE);
                friend_inactive.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
                who_can_view.setVisibility(View.VISIBLE);
            }
        });

        public_inactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                public_inactive.setVisibility(View.GONE);
                friend_active.setVisibility(View.GONE);

                public_active.setVisibility(View.VISIBLE);
                friend_inactive.setVisibility(View.VISIBLE);
            }
        });

        friend_inactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                public_active.setVisibility(View.GONE);
                friend_inactive.setVisibility(View.GONE);

                public_inactive.setVisibility(View.VISIBLE);
                friend_active.setVisibility(View.VISIBLE);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                public_active.setVisibility(View.GONE);
                friend_inactive.setVisibility(View.GONE);
                public_inactive.setVisibility(View.GONE);
                friend_active.setVisibility(View.GONE);
                who_can_view.setVisibility(View.GONE);
                next_btn.setVisibility(View.GONE);

                duration.setVisibility(View.VISIBLE);
                _48h_btn.setVisibility(View.VISIBLE);
                _72h_btn.setVisibility(View.VISIBLE);
                _1w_btn.setVisibility(View.VISIBLE);
                _forever_btn.setVisibility(View.VISIBLE);
                post_btn.setVisibility(View.VISIBLE);
            }
        });

        _48h_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _48h_btn.setImageResource(R.drawable.ic_48h_active);
                _72h_btn.setImageResource(R.drawable.ic_72h_inactive);
                _1w_btn.setImageResource(R.drawable.ic_1w_inactive);
                _forever_btn.setImageResource(R.drawable.ic_forever_inactive);

            }
        });

        _72h_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _48h_btn.setImageResource(R.drawable.ic_48h_inactive);
                _72h_btn.setImageResource(R.drawable.ic_72h_active);
                _1w_btn.setImageResource(R.drawable.ic_1w_inactive);
                _forever_btn.setImageResource(R.drawable.ic_forever_inactive);

            }
        });

        _1w_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _48h_btn.setImageResource(R.drawable.ic_48h_inactive);
                _72h_btn.setImageResource(R.drawable.ic_72h_inactive);
                _1w_btn.setImageResource(R.drawable.ic_1w_active);
                _forever_btn.setImageResource(R.drawable.ic_forever_inactive);

            }
        });

        _forever_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _48h_btn.setImageResource(R.drawable.ic_48h_inactive);
                _72h_btn.setImageResource(R.drawable.ic_72h_inactive);
                _1w_btn.setImageResource(R.drawable.ic_1w_inactive);
                _forever_btn.setImageResource(R.drawable.ic_forever_active);
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_text = title.getText().toString();
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "record.3gp");
                    FileInputStream fileInputStreamReader = new FileInputStream(file);
                    byte[] bytes = new byte[(int)file.length()];
                    fileInputStreamReader.read(bytes);
                    String encodedfile = Base64.encodeToString(bytes, Base64.DEFAULT);


                    String baseurl = "http://35.194.218.135:5000/createTale";
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("userId", LoginActivity.userId);
                    map.put("b64", encodedfile);
                    map.put("privacy", "0");
                    map.put("timelimit", "48");
                    map.put("title", title_text);
                    map.put("lat", String.valueOf(MapsActivity.lat));
                    map.put("lon", String.valueOf(MapsActivity.lon));

                    new Thread(new Runnable(){
                        public void run(){
                            try {
                                JSONObject jsonObject = postRequest(baseurl, map);
                                System.out.println(jsonObject);
                                String createSuccess = jsonObject.get("error").toString();
                                if (createSuccess.equals("false")) {

                                    CreateTaleActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(CreateTaleActivity.this, "Posted!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    openMapsActivity();

                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            private void openMapsActivity() {
                Intent intent = new Intent(CreateTaleActivity.this, MapsActivity.class);
                startActivity(intent);
            }

        });


    }




    public void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}