package com.example.proyecto.api;

import com.example.proyecto.model.AuthResponse;
import com.example.proyecto.model.LoginRequest;
import com.example.proyecto.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body LoginRequest request);
}