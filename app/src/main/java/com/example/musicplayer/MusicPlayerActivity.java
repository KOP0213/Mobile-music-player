package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    public static final int SWIPE_TRESHOLD = 100;
    public static final int SWIPE_VELOCITY_TRESHOLD = 100;
    TextView songNameTextView;
    Button nextButton, previousButton, pauseButton;
    SeekBar songDurationBar;
    ImageView playIcon;

    Animation rotateAnimation;

    static MediaPlayer mediaPlayer;
    int pos;
    long animationDuration = 1000;
    String songName, actualSongName;

    ArrayList<File> songList;
    Thread updateDuration; // Vyuziti threadu pri praci s zobrazovanim prubehu pisne
    private GestureDetector gestureDetector;

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
            case R.id.item1: // Email item v menu - zaslani emailu s nazvem aktualni pisne
                Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();

                songDurationBar.setMax(mediaPlayer.getDuration());

                if(mediaPlayer.isPlaying()){
                    pauseButton.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    pauseButton.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
                // volani nove aktivity pro vypis emailu
                Intent mIntent = new Intent(this, emailActivity.class);
                startActivity(mIntent.putExtra("SONG_NAME_ID", actualSongName));
                break;

            case R.id.item2:

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
        playIcon = findViewById(R.id.playIcon);
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
                fadeAnimation();
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
                rotateAnimationRight();
                fadeNextAnimation();
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
                rotateAnimationLeft();
                fadePreviousAnimation();
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

        gestureDetector = new GestureDetector(this);
    }


    public void animation(View view){
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(playIcon, "rotation", 0f, 360f);
        rotateAnimation.setDuration(animationDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.start();
    }

    private void rotateAnimationLeft(){
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.rotateleft);
        playIcon.startAnimation(rotateAnimation);
    }

    private void rotateAnimationRight(){
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.rotateright);
        playIcon.startAnimation(rotateAnimation);
    }

    private void fadeAnimation(){
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.fade);
        pauseButton.startAnimation(rotateAnimation);
    }

    private void fadeNextAnimation(){
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.fade);
        nextButton.startAnimation(rotateAnimation);
    }

    private void fadePreviousAnimation(){
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.fade);
        previousButton.startAnimation(rotateAnimation);
    }


    /*
    Metody na gesta
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getY();

        // ktery swipe byl vetsi - odkud kam provadim swipe
        if(Math.abs(diffX) > Math.abs(diffY)){
            // doleva nebo doprava
            if(Math.abs(diffX) > SWIPE_TRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_TRESHOLD) {
                if(diffX > 0){
                    onSwipeRgiht();
                }
                else{
                    onSwipeLeft();
                }
                result = true;
            }
        }
        else {
            // nahoru dolu
        }

        return result;
    }

    private void onSwipeLeft() {
        Toast.makeText(this, "Swipe left", Toast.LENGTH_SHORT).show();

        rotateAnimationRight();
        fadeNextAnimation();
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

    private void onSwipeRgiht() {
        Toast.makeText(this, "Swipe right", Toast.LENGTH_SHORT).show();

        rotateAnimationLeft();
        fadePreviousAnimation();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
