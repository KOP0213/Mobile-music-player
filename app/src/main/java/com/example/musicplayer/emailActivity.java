package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
Aktivita pro vypisovani emailu - adresát, predmet a obsah zpravy
 */
public class emailActivity extends AppCompatActivity {
    private EditText myEditTextTo;
    private EditText myEditTextSubject;
    private EditText myEditTextMessage;
    String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        myEditTextMessage = findViewById(R.id.edit_text_message);
        myEditTextTo = findViewById(R.id.edit_text_to);
        myEditTextSubject = findViewById(R.id.edit_text_subject);
        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        songName = getIntent().getStringExtra("SONG_NAME_ID");
        myEditTextMessage.append("Hey! Do you know this song: ");
        myEditTextMessage.append(songName);
        myEditTextMessage.append(" ?\n" + "Check it out!");

        myEditTextSubject.append("Check out this song!");
    }

    private void sendMail() {
        String recipientList = myEditTextTo.getText().toString();
        String[] recipients = recipientList.split(",");

        String subject = myEditTextSubject.getText().toString();
        String message = myEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        // otevira pouze email klienty
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}
