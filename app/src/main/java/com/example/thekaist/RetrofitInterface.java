package com.example.thekaist;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);

//    @GET("/battles")
//    Call<String> getBattle (@Body HashMap<String, String> map);

    @HTTP(method = "get", path = "/battles", hasBody = true)
    Call<String> getBattle (@Body HashMap<String, String> map);

    @HTTP(method = "post", path = "/battles", hasBody = true)
    Call<Void> postBattle(@Body HashMap<String, String> map);
}
