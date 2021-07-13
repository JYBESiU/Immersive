package com.example.thekaist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thekaist.Adapter.CardsAdapter;
import com.example.thekaist.ui.setting.SettingFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AppCompatActivity {
    public static String ask;
    public static String accept;
    public static String room;
    public static int ask_scr = 0;
    public static int accept_scr = 0;

    private TextView target, ply1, ply1scr, ply2, ply2scr, ask_pass, accept_pass;
    private RecyclerView recyclerView;
    private CardsAdapter cardsAdapter;
    private ImageButton buzzer, pass;
    private ImageView answer;

    public static Socket mSocket;

    private Context activity = this;
    private Gson gson = new Gson();

    Socket hSocket;
    String name;
    String id;
    String whosTurn;

    public boolean passFlag;

    private String BASE_URL = MainActivity.BASE_URL;

    static ArrayList<Drawable> cards_list = new ArrayList<Drawable>();
    static ArrayList<Drawable> nums_list = new ArrayList<Drawable>();
    static ArrayList<Drawable> image_list = new ArrayList<Drawable>();

    ArrayList<Integer> cards_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    ArrayList<Integer> nums_order = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));;
    ArrayList<Integer> selects = new ArrayList<Integer>();

    int targetNum ;
    String targetString;
    boolean card_clickable = false;

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
        ask_pass = findViewById(R.id.ask_pass);
        accept_pass = findViewById(R.id.accept_pass);

        ply1.setText(ask);
        ply2.setText(accept);

        buzzer = findViewById(R.id.buzzer);
        pass = findViewById(R.id.pass);

        answer = findViewById(R.id.answer);
        answer.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.show_cards);
        recyclerView.setClickable(false);
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
                runOnUiThread(() -> {
                    if (!card_clickable) return;
                    if (selects.contains(nums_order.get(position))) {
                        Toast.makeText(getApplicationContext(), "Don't click same card!", Toast.LENGTH_SHORT).show();
                    } else {
                        selects.add(nums_order.get(position));
                        hSocket.emit("click", room, position, selects.size(), id);
                    }
                });

            }
        });
        recyclerView.setAdapter(cardsAdapter);

        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buzzer.setBackgroundColor(getResources().getColor(R.color.buzzerred));
                whosTurn = id;
                hSocket.emit("startTurn", room, id);
                card_clickable = true;
                buzzer.setEnabled(false);
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass.setBackgroundColor(getResources().getColor(R.color.passon));
                pass.setEnabled(false);
                card_clickable = false;
                buzzer.setEnabled(false);
                hSocket.emit("passTurn", room, id, ask, accept, ask_scr, accept_scr);
            }
        });
        hSocket = FrontActivity.mSocket;

        hSocket.on("opponentClick", opponentClick);
        hSocket.on("opponentTurn", opponentTurn);
        hSocket.on("opponentPass", opponentPass);

        hSocket.on("startShow", startShow);
        hSocket.on("stopShow", stopShow);

        hSocket.on("startRound", startRound);
        hSocket.on("correct", correct);
        hSocket.on("wrong", wrong);

        hSocket.on("win", win);
        hSocket.on("lose", lose);

        hSocket.on("yourRejected", yourRejected);
    }

    @Override
    public void onBackPressed() {
        hSocket.emit("leave", ask, accept, id);
        super.onBackPressed();
    }

    public Emitter.Listener yourRejected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "거절당했습니다...", Toast.LENGTH_SHORT).show();
            });
        }
    };

    public Emitter.Listener opponentClick = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           runOnUiThread(() -> {
               int pos = (int) args[0];
               image_list.set(pos, nums_list.get(nums_order.get(pos)));
               cardsAdapter.notifyItemChanged(pos);

               if ((int) args[1] == 3 && args[2].toString().equals(id)) {
                    TimerTask task = new TimerTask() {
                        public void run () {
                            if (passFlag) {
                                hSocket.emit("endRound", room, ask, accept, ask_scr, accept_scr);
                            } else {
                                hSocket.emit("endTurn", selects.get(0), selects.get(1), selects.get(2), targetString, targetNum, id, ask, accept, ask_scr, accept_scr);
                                selects = new ArrayList<Integer>();
                            }

                        }
                    };

                    Timer timer = new Timer();
                    timer.schedule(task, 1000);
               }
           });
        }
    };

    public Emitter.Listener opponentTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                buzzer.setBackgroundColor(getResources().getColor(R.color.buzzerno));
                buzzer.setEnabled(false);
                buzzer.setImageResource(R.drawable.ic_baseline_not_interested_24);
                whosTurn = args[0].toString();
                card_clickable = false;
            });
        }
    };

    public Emitter.Listener opponentPass = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                String id = args[0].toString();
                String ask = args[1].toString();
                String accept = args[2].toString();

                View view = getLayoutInflater().inflate(R.layout.pass_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                AlertDialog ad = builder.setView(view).create();

                Button yes_button = view.findViewById(R.id.yes);
                Button no_button = view.findViewById(R.id.no);

                yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hSocket.emit("passTurn", room, id, ask, accept, ask_scr, accept_scr);
                        ad.dismiss();
                    }
                });

                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSocket.emit("challengeReject", ask, accept);
                        buzzer.setBackgroundColor(getResources().getColor(R.color.buzzerred));
                        whosTurn = id;
                        hSocket.emit("startTurn", room, id);
                        card_clickable = true;
                        buzzer.setEnabled(false);

                        passFlag = true;
                        ad.dismiss();
                    }
                });

                ad.show();
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
                target.setText("Show for 10 seconds");
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

                target.setTextSize(35);
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
                target.setTextSize(40);
                target.setText(Integer.toString(targetNum));

                ask_scr = (int) args[2];
                accept_scr = (int) args[3];

                answer.setVisibility(View.INVISIBLE);

                ask_pass.setText("");
                accept_pass.setText("");

                buzzer.setEnabled(true);
                buzzer.setBackgroundColor(getResources().getColor(R.color.buzzertrans));
                buzzer.setImageResource(R.drawable.push_img);

                pass.setEnabled(true);
                pass.setBackgroundColor(getResources().getColor(R.color.passtrans));
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

                answer.setImageResource(R.drawable.correct_imgg);
                answer.setVisibility(View.VISIBLE);

                ask_scr = (int) args[0];
                accept_scr = (int) args[1];
                ply1scr.setText(Integer.toString(ask_scr));
                ply2scr.setText(Integer.toString(accept_scr));

                TimerTask task = new TimerTask() {
                    public void run () {
                        hSocket.emit("endRound", room, ask, accept, ask_scr, accept_scr);
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 300);


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

                answer.setImageResource(R.drawable.wrong_imgg);
                answer.setVisibility(View.VISIBLE);

                TimerTask task = new TimerTask() {
                    public void run () {
                        answer.setVisibility(View.INVISIBLE);
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 400);

                if (!whosTurn.equals(id))
                    buzzer.setEnabled(true);
                buzzer.setBackgroundColor(getResources().getColor(R.color.buzzertrans));
                buzzer.setImageResource(R.drawable.push_img);
                card_clickable = false;
            });
        }
    };


    public Emitter.Listener win = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {

            });
        }
    };

    public Emitter.Listener lose = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {

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
