package com.example.thekaist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {
    String ask;
    String accept;

    private TextView target, ply1, ply1scr, ply2, ply2scr;
    private RecyclerView recyclerView;
    //어댑터 필요
    private ImageButton buzzer, pass;


    public static Socket mSocket;

    private Context activity = this;
    private Gson gson = new Gson();
    private String roomname;


    Socket hSocket;
    String id;

    private String BASE_URL = "http://192.249.18.152:443";



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

         hSocket = FrontActivity.mSocket;
         id = FrontActivity.id;

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


        hSocket.on("enterroom", enterroom);





    }

    @Override
    public void onBackPressed() {

        hSocket.emit("leave", ask, accept, id);
        super.onBackPressed();
    }

    private void sayPass() {
    }

    private void speak() {
    }

    public Emitter.Listener enterroom = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            roomname = args[0].toString();
            Log.d("room", ""+roomname);
        }
    };
}
