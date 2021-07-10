package com.example.thekaist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    String ask;
    String accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        ask = intent.getExtras().getString("ask");
        accept = intent.getExtras().getString("accept");

        TextView text_ask = findViewById(R.id.text_ask);
        TextView text_accept = findViewById(R.id.text_accept);

        text_ask.setText(ask);
        text_accept.setText(accept);
    }
}
