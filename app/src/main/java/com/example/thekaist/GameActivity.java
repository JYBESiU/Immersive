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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thekaist.Adapter.CardsAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {
    String ask;
    String accept;

    private TextView target, ply1, ply1scr, ply2, ply2scr;
    private RecyclerView recyclerView;
    private CardsAdapter cardsAdapter;
    private ImageButton buzzer, pass;

    public static Socket mSocket;

    private Context activity = this;
    private Gson gson = new Gson();
    private String roomname;


    Socket hSocket;
    String id;

    private String BASE_URL = MainActivity.BASE_URL;

    ArrayList<Integer> cards_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    ArrayList<Integer> nums_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));;
    ArrayList<Integer> selects = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        ask = intent.getExtras().getString("ask");
        accept = intent.getExtras().getString("accept");

        target = findViewById(R.id.target);
        ply1 = findViewById(R.id.ply1);
        ply1scr = findViewById(R.id.ply1score);
        ply2 = findViewById(R.id.ply2);
        ply2scr = findViewById(R.id.ply2score);

        ply1.setText(ask);
        ply2.setText(accept);

        buzzer = findViewById(R.id.buzzer);
        pass = findViewById(R.id.pass);

        recyclerView = findViewById(R.id.show_cards);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        cardsAdapter = new CardsAdapter(this, cards_order, nums_order);
        cardsAdapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selects.add(position);
                if (selects.size() == 3) {
                    hSocket.emit("turnOver", selects);
                    selects = new ArrayList<Integer>();
                }
            }
        });
        recyclerView.setAdapter(cardsAdapter);

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
            cards_order = (ArrayList<Integer>) args[1];
            nums_order = (ArrayList<Integer>) args[2];
            Log.d("room", ""+roomname);
            Log.d("card", ""+cards_order);
            Log.d("num", ""+nums_order);
        }
    };
}
