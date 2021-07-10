package com.example.thekaist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class GameActivity extends AppCompatActivity {
    String ask;
    String accept;

    private TextView target, ply1, ply1scr, ply2, ply2scr;
    private RecyclerView recyclerView;
    //어댑터 필요
    private ImageButton buzzer, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        ask = intent.getExtras().getString("ask");
        accept = intent.getExtras().getString("accept");

        target = findViewById(R.id.target);
        ply1 = findViewById(R.id.ply1);
        ply1scr = findViewById(R.id.ply1score);
        ply2 = findViewById(R.id.ply2);
        ply2scr = findViewById(R.id.ply2score);

        recyclerView = findViewById(R.id.show_cards);
        buzzer = findViewById(R.id.buzzer);
        pass = findViewById(R.id.pass);

        ply1.setText(ask);
        ply2.setText(accept);

        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sayPass();
            }
        });





    }

    private void sayPass() {
    }

    private void speak() {
    }
}
