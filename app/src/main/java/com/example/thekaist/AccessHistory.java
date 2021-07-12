package com.example.thekaist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.thekaist.Adapter.HistoryAdapter;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccessHistory extends AppCompatActivity {

    private TextView title;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = MainActivity.BASE_URL;
    private String id;
    private ArrayList<UserInfo> histlist=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_history);

        title = findViewById(R.id.History);
        recyclerView = findViewById(R.id.show_history);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        //배틀에서 가져와야함 레트로핏 생성예정

        historyAdapter = new HistoryAdapter(getApplicationContext(), histlist);
        recyclerView.setAdapter(historyAdapter);


    }
}