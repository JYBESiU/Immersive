package com.example.thekaist;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.Toast.LENGTH_LONG;

public class FrontActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = MainActivity.BASE_URL;

    public static Socket mSocket;

    private Context activity = this;

    public static String name;
    public static String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        setContentView(R.layout.activity_front);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");


        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        init();
    }

    private void init() {
        try {
            mSocket = IO.socket(BASE_URL);
            Log.d("SOCKET info", "Connection success : " + mSocket.toString());
            Log.d("SOCKET", "Connection success : " + mSocket.id());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT, waitBattle);
        mSocket.on("challengeCome", challengeCome);
        mSocket.on("startGame", startGame);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    public static Emitter.Listener waitBattle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("waitBattle", id);
        }
    };

    public Emitter.Listener challengeCome = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                Log.d("TTTTTTA", "challengeCome");
                String ask = args[0].toString();
                String accept = args[1].toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Challenge from " + ask);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSocket.emit("acceptGame", ask, accept);
//
//                        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//                        intent.putExtra("ask", args[0].toString());
//                        intent.putExtra("accept", args[1].toString());
//
//                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "NO Click", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    };

    public Emitter.Listener startGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            intent.putExtra("ask", args[0].toString());
            intent.putExtra("accept", args[1].toString());

            JSONArray jsonArray1 = (JSONArray) args[2];
            ArrayList<Integer> list1 = new ArrayList<Integer>();
            for (int i = 0; i < jsonArray1.length(); i++) {
                try {
                    list1.add(jsonArray1.getInt(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray jsonArray2 = (JSONArray) args[3];
            ArrayList<Integer> list2 = new ArrayList<Integer>();
            for (int i = 0; i < jsonArray2.length(); i++) {
                try {
                    list2.add(jsonArray1.getInt(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            intent.putExtra("cards_order", list1);
            intent.putExtra("nums_order", list2);

            startActivity(intent);
        }
    };

}