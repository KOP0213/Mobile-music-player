package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = findViewById(R.id.songListView); // List s jednotlivymi pisnickami

        permission();
    }


    public void permission(){
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() { // Vyuziti permissionu na cteni z externi pameti
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }) .check();
    }
}
