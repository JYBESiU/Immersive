package com.example.thekaist.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekaist.Battle;
import com.example.thekaist.FrontActivity;
import com.example.thekaist.R;
import com.example.thekaist.RetrofitInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kotlinx.coroutines.channels.ValueOrClosed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;

import static android.widget.Toast.LENGTH_LONG;

public class HomeFragment extends Fragment {
    EditText editText;
    Button button;
    private Gson gson = new Gson();

    FrontActivity frontActivity = (FrontActivity)getActivity();
    Socket hSocket;
    String name;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.249.18.171:443";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        editText = root.findViewById(R.id.accept_id);
        button = root.findViewById(R.id.ask_button);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

//        try {
//            hSocket = IO.socket(BASE_URL);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        hSocket.connect();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hSocket.emit("battle", gson.toJson(new Battle(name, editText.getText().toString())));
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        hSocket = frontActivity.mSocket;
        name = frontActivity.name;

        super.onStart();
    }

    public static Emitter.Listener battle = new Emitter.Listener() {
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