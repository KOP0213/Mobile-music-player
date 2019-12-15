package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView songListView;
    String [] songItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songListView = (ListView) findViewById(R.id.songListView); // List s jednotlivymi pisnickami

        permission();
    }


    public void permission(){
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() { // Vyuziti permissionu na cteni z externi pameti
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) { // Pokud je permission povolen, volam display metodu, ktera mi zobrazuje jednotlive pisnicky v seznamu z externi pameti
            displaySongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
            }
        }) .check();
    }

    public ArrayList<File> searchSong(File song){
        ArrayList<File> songArrayList = new ArrayList<>();

        File[] songFiles = song.listFiles();

        for(int i=0; i < songFiles.length; i++){
            File singleFile = songFiles[i];
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                songArrayList.addAll(searchSong(singleFile));
            }
            else if(singleFile.getName().endsWith(".mp3")){
                songArrayList.add(singleFile);
            }
        }
        return songArrayList;
    }

    public void displaySongs(){
        final ArrayList<File> songList = searchSong(Environment.getExternalStorageDirectory());

        songItems = new String[songList.size()];

        for(int i=0; i<songList.size(); i++){
            songItems[i] = songList.get(i).getName().replace(".mp3",""); // Odstranuju priponu souboru
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, songItems); // Pouziti ArrayAdapteru ktery pouziva pole jako datasource
        songListView.setAdapter(myAdapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameOfSong = songListView.getItemAtPosition(position).toString();
                startActivity(new Intent(getApplicationContext(), MusicPlayerActivity.class)
                .putExtra("songs", songList).putExtra("songname",nameOfSong).putExtra("position", position));
            }
        });
    }
}
