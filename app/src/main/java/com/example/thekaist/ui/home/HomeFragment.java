package com.example.thekaist.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekaist.Adapter.OnlineAdapter;
import com.example.thekaist.FrontActivity;
import com.example.thekaist.LoginResult;
import com.example.thekaist.MainActivity;
import com.example.thekaist.R;
import com.example.thekaist.RetrofitInterface;
import com.example.thekaist.UserInfo;
import com.example.thekaist.online_player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private Button req;
    private ArrayList<online_player> onlist = new ArrayList<>();
    private SwipeRefreshLayout swipe;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.249.18.152:443";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        swipe = root.findViewById(R.id.swipe_home);

        recyclerView = root.findViewById(R.id.show_online);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView.LayoutManager layoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(layoutManager);


        getOnline();
        if(onlist!=null){
            OnlineAdapter adapter = new OnlineAdapter(getContext(), onlist);
            Log.d("look", ""+onlist.size());
            recyclerView.setAdapter(adapter);
        }


        req = root.findViewById(R.id.request);


        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRequest();
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onlist.clear();
                getOnline();

                OnlineAdapter adapter = new OnlineAdapter(getContext(), onlist);

                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                RecyclerView.LayoutManager layoutManager = linearLayoutManager;
                recyclerView.setLayoutManager(layoutManager);

                swipe.setRefreshing(false);
            }
        });

        return root;
    }

    private void connectRequest() {
    }

    private void getOnline() {
        Call<List<UserInfo>> call = retrofitInterface.executeOnline();//로그인리절트 클래스 부르는데저 map넣어서 함
        //final ArrayList<online_player> list = new ArrayList<>();

        onlist = new ArrayList<>();
        call.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                if (response.code() == 200) {

                    List<UserInfo> resultList = response.body();
                    Log.d("look", ""+resultList.get(0).getName()+" and "+resultList.size());

                    for(int i=0;i<resultList.size();i++) {
                        UserInfo result = resultList.get(i);
                        online_player item = new online_player(result.getName(), result.getId());

                        onlist.add(item);
                        Log.d("look", ""+onlist.size()+" and "+item.getId());

                    }

                    OnlineAdapter adapter = new OnlineAdapter(getContext(), onlist);
                    Log.d("look", ""+onlist.size());
                    recyclerView.setAdapter(adapter);


                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Wrong Credential in homes",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {

                Toast.makeText(getContext(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }


        });

    }
}