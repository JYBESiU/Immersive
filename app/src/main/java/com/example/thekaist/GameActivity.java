package com.example.thekaist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.example.thekaist.ui.setting.SettingFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {
    public static String ask;
    public static String accept;
    public static String room;
    public static int ask_scr = 0;
    public static int accept_scr = 0;

    private TextView target, ply1, ply1scr, ply2, ply2scr;
    private RecyclerView recyclerView;
    private CardsAdapter cardsAdapter;
    private ImageButton buzzer, pass;

    public static Socket mSocket;

    private Context activity = this;
    private Gson gson = new Gson();

    Socket hSocket;
    String name;
    String id;

    private String BASE_URL = MainActivity.BASE_URL;

    static ArrayList<Drawable> cards_list = new ArrayList<Drawable>();
    static ArrayList<Drawable> nums_list = new ArrayList<Drawable>();
    static ArrayList<Drawable> image_list = new ArrayList<Drawable>();

    ArrayList<Integer> cards_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    ArrayList<Integer> nums_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));;
    ArrayList<Integer> selects = new ArrayList<Integer>();

    int targetNum ;
    String targetString;

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
        room = ask + "&" + accept;
        cards_order = intent.getExtras().getIntegerArrayList("cards_order");
        nums_order = intent.getExtras().getIntegerArrayList("nums_order");

        makeImageList();

        name = SettingFragment.name;
        id = FrontActivity.id;

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
        cardsAdapter = new CardsAdapter(this, image_list);
        cardsAdapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                hSocket.emit("click", room, position);
                selects.add(nums_order.get(position));
                // 같은 숫자 중복 금지 추가
                if (selects.size() == 3) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 누른 사람 3번째 클릭에 안보이는 것 수정
                    hSocket.emit("turnOver", selects.get(0), selects.get(1), selects.get(2), targetString, targetNum, id, ask, accept, ask_scr, accept_scr);
                    selects = new ArrayList<Integer>();
                }
            }
        });
        recyclerView.setAdapter(cardsAdapter);

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
        hSocket = FrontActivity.mSocket;

        hSocket.on("randomCard", randomCard);

        hSocket.on("opponentClick", opponentClick);
        hSocket.on("startShow", startShow);
        hSocket.on("stopShow", stopShow);
        hSocket.on("startRound", startRound);
        hSocket.on("correct", correct);
        hSocket.on("wrong", wrong);
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

    public Emitter.Listener randomCard = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();
            Log.d("data", ""+data);
        }
    };

    public Emitter.Listener opponentClick = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           runOnUiThread(() -> {
               int pos = (int) args[0];
               image_list.set(pos, nums_list.get(nums_order.get(pos)));
               cardsAdapter.notifyItemChanged(pos);
           });
        }
    };

    public Emitter.Listener startShow = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                image_list.clear();
                for (int i = 0; i < 16; i++) {
                    image_list.add(nums_list.get(nums_order.get(i)));
                }
                cardsAdapter.notifyDataSetChanged();

                target.setTextSize(20);
                target.setText("Show for 5 seconds");
            });
        }
    };

    public Emitter.Listener stopShow = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                image_list.clear();
                for (int i = 0; i < 16; i++) {
                    image_list.add(cards_list.get(cards_order.get(i)));
                }
                cardsAdapter.notifyDataSetChanged();

                target.setTextSize(40);
                target.setText("Game Start");
            });
        }
    };

    public Emitter.Listener startRound = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                target.setText(" ");
                targetString = args[0].toString();
                targetNum = Integer.parseInt(args[1].toString());
                target.setText(Integer.toString(targetNum));
            });
        }
    };

    public Emitter.Listener correct = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                image_list.clear();
                for (int i = 0; i < 16; i++) {
                    image_list.add(cards_list.get(cards_order.get(i)));
                }
                cardsAdapter.notifyDataSetChanged();

//                hSocket.emit("endRound");
            });
        }
    };

    public Emitter.Listener wrong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                image_list.clear();
                for (int i = 0; i < 16; i++) {
                    image_list.add(cards_list.get(cards_order.get(i)));
                }
                cardsAdapter.notifyDataSetChanged();


            });
        }
    };

    public void makeImageList() {
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card1));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card2));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card3));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card4));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card5));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card6));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card7));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card8));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card9));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card10));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card11));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card12));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card13));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card14));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card15));
        this.cards_list.add(this.getResources().getDrawable(R.drawable.card16));

        this.nums_list.add(this.getResources().getDrawable(R.drawable.num1));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num2));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num3));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num4));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num5));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num6));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num7));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num8));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num9));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num10));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num11));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num12));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num13));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num14));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num15));
        this.nums_list.add(this.getResources().getDrawable(R.drawable.num16));

        for (int i = 0; i < 16; i++) {
            this.image_list.add(cards_list.get(cards_order.get(i)));
        }
    }
}
