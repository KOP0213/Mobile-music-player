package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView songName;
    Button nextButton, previousButton, pauseButton;
    SeekBar songDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        songName = findViewById(R.id.songName);

        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        pauseButton = findViewById(R.id.pauseButton);

        songDuration = findViewById(R.id.durationBar);

    }
}
