package com.example.proyecto.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //private static final String BASE_URL = "http://10.53.238.167:8080/";
    private static final String BASE_URL = "http://10.0.2.2:8080/";


    public static final String WS_URL = "ws://10.0.2.2:8080/ws-chat";
    //public static final String WS_URL = "ws://10.53.238.167:8080/ws-chat";


    private static Retrofit retrofit;

    public static Retrofit getClient(){

        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}