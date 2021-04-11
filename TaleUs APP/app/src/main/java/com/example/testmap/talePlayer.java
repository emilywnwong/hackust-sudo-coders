package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class talePlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_player);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(talePlayer.this, MapsActivity.class);
        startActivity(intent);
    }
}