package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView songNameTextView;
    Button nextButton, previousButton, pauseButton;
    SeekBar songDurationBar;

    static MediaPlayer mediaPlayer;
    int pos;
    //int colorPrimary = R.color.colorPrimary;
    String songName, actualSongName;

    ArrayList<File> songList;
    Thread updateDuration; // Vyuziti threadu pri praci s zobrazovanim prubehu pisne

    /*
    Tvorba menu v aktivite prehravace
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        songNameTextView = findViewById(R.id.songName);

        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        pauseButton = findViewById(R.id.pauseButton);

        songDurationBar = findViewById(R.id.durationBar);



        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateDuration = new Thread(){ // Vyuziti threadu pri praci s zobrazovanim prubehu pisne
            @Override
            public void run() {
                super.run();

                int totalDuration = mediaPlayer.getDuration();
                int currentDuration = 0;

                /*
                Zde zjistuji aktualni cas prehravani a aktualizuji jej - nastavuju do seek baru
                 */
                while(currentDuration<totalDuration){
                    try{
                        sleep(1500);
                        currentDuration = mediaPlayer.getCurrentPosition();
                        songDurationBar.setProgress(currentDuration);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        Intent inent = getIntent();
        Bundle bundle = inent.getExtras();

        songList =(ArrayList) bundle.getParcelableArrayList("songs");

        songName = songList.get(pos).getName();

        actualSongName = getIntent().getStringExtra("songname");

        songNameTextView.setText(actualSongName);
        songNameTextView.setSelected(true);
        pos = bundle.getInt("position", 0);

        Uri uri = Uri.parse(songList.get(pos).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        mediaPlayer.start();
        songDurationBar.setMax(mediaPlayer.getDuration());

        updateDuration.start(); // volam update na duration, startuju thread
        //songDurationBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songDurationBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY));

        songDurationBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
        //songDurationBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_IN));
        //songDurationBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        songDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        /*
        Nastaveni pause/play buttonu
         */
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songDurationBar.setMax(mediaPlayer.getDuration());

                if(mediaPlayer.isPlaying()){
                    pauseButton.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    pauseButton.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        /*
        Nastaveni next buttonu
        - nejprve se pisen zastavi, pote se posune pozice na dalsi pisen, pak se spusti nova
         */
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                pos = ((pos+1)%songList.size()); // zde se urcuje nova pozice

                Uri uri = Uri.parse(songList.get(pos).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                songName = songList.get(pos).getName();
                songNameTextView.setText(songName);
                songDurationBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
            }
        });

        /*
        Nastaveni previous buttonu
        - nejprve se pisen zastavi, pote se posune pozice na dalsi pisen, pak se spusti nova
         */
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();
                pos = ((pos-1)<0)?(songList.size()-1):(pos-1); // zde se urcuje nova pozice

                Uri uri = Uri.parse(songList.get(pos).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                songName = songList.get(pos).getName();
                songNameTextView.setText(songName);
                songDurationBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
            }
        });
    }


}
