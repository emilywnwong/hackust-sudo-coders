package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;

import org.json.*;

import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, mapBottomSheet.BottomSheetListener {

    public static double lat, lon;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private FloatingActionButton CreateTaleButton, ProfileButton;
    private Marker myMarker;
    private TextView mTextView;
    private List<String> markers_list = new ArrayList<String>();

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

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        jsonString = sb.toString();

        return new JSONObject(jsonString);
    }


    final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                MapsActivity.lat = location.getLatitude();
                MapsActivity.lon = location.getLongitude();

                final LatLng CURRENT = new LatLng(location.getLatitude(), location.getLongitude());
                System.out.println(CURRENT);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT, 19));
//                mMap.clear();

                try {
                    JSONObject jsonObject = getJSONObjectFromURL("http://35.194.218.135:5000/dbStats");
                    JSONArray getArray = jsonObject.getJSONArray("tales");

                    for (int i = 0; i < getArray.length(); i++) {

                        JSONObject object = getArray.getJSONObject(i);

                        double lat = Double.parseDouble(object.get("lat").toString());
                        double lon = Double.parseDouble(object.get("lon").toString());
                        boolean retval = markers_list.contains(lat+"@"+lon);

//                        System.out.println(markers_list);

                        if (retval == false) {
                            markers_list.add(lat + "@" + lon);
                            String taleId = object.get("taleId").toString() + "@@@"
                                    + object.get("userId").toString() + "@@@"
                                    + object.get("title").toString() + "@@@"
                                    + object.get("lat").toString() + "@@@"
                                    + object.get("lon").toString() + "@@@"
                                    + object.get("timedate").toString();
//                            System.out.println(taleId);

                            final LatLng MARKER = new LatLng(lat, lon);

                            Marker marker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(MARKER)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.inactive5)));
                            marker.setTag(taleId);


                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                public boolean onMarkerClick(Marker marker) {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active5));
//                                stopLocationUpdates();
                                    mapBottomSheet bottomSheet = new mapBottomSheet((String) marker.getTag());
                                    bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                                    return true;
                                }
                            });

                        }
                    }
                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
                }

            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        CreateTaleButton = findViewById(R.id.createTale);
        CreateTaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTaleActivity();
            }
        });
        ProfileButton = findViewById(R.id.profile);
        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Adrian: Refresh user location
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    // Adrian: Do when map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Adrian: check location permission & keep updating location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            checkSettingAndStartLocationUpdates();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void checkSettingAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Adrian: Setting of device are satisfy and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void openCreateTaleActivity() {
        Intent intent = new Intent(this, CreateTaleActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void openArActivity() {
        Intent intent = new Intent(this, arActivity.class);
        startActivity(intent);
    }

    public void onButtonClicked(String text) {
        mTextView.setText(text);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
