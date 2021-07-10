package com.example.thekaist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Retrofit;

public class FrontActivity extends AppCompatActivity {
    public static String name;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.249.18.171:443";

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        name = intent.getExtras().getString("ID");



//        retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        retrofitInterface = retrofit.create(RetrofitInterface.class);

        setContentView(R.layout.activity_front);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        init();

//        HashMap<String, String> map = new HashMap<>();//key도 스트링, 값도 스트링
//
//        map.put("accept", name);
//
//        Call<String> call = retrofitInterface.getBattle(map);
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.code() == 200) {
//                    String result = response.body();//응답의 내용. 이와같은 디비구조인게 loginresult.
//                    Toast.makeText(FrontActivity.this, result, LENGTH_LONG).show();
//                } else if (response.code() == 404) {
//                    Toast.makeText(FrontActivity.this, "404", LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(FrontActivity.this, "500 wrong", LENGTH_LONG).show();
//            }
//        });
    }

    private void init() {
        try {
            mSocket = IO.socket("http://192.249.18.171:443");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("serverMessage", onMessageReceived);
//            Log.d("SOCKET info", "Connection success : " + mSocket.toString());
//            Log.d("SOCKET", "Connection success : " + mSocket.id());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        mSocket.connect();
//        Log.d("SOCKET", "Connection success???? : " + mSocket.id());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("clientMessage", "hi");
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject receivedData = (JSONObject) args[0];
                Log.d("SOCKET", receivedData.getString("msg"));
                Log.d("SOCKET", receivedData.getString("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}