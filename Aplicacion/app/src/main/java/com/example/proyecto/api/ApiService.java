package com.example.proyecto.api;

import com.example.proyecto.model.AuthResponse;
import com.example.proyecto.model.ChatResponse;
import com.example.proyecto.model.CreateChatRequest;
import com.example.proyecto.model.JoinChatRequest;
import com.example.proyecto.model.LoginRequest;
import com.example.proyecto.model.MessageResponse;
import com.example.proyecto.model.SendMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body LoginRequest request);

    @POST("chats")
    Call<ChatResponse> createChat(@Header("Authorization") String token,
                                  @Body CreateChatRequest request);

    @POST("chats/join")
    Call<ChatResponse> joinChat(@Header("Authorization") String token,
                                @Body JoinChatRequest request);

    @GET("chats")
    Call<List<ChatResponse>> getChats(@Header("Authorization") String token);

    @GET("chats/{chatId}/messages")
    Call<List<MessageResponse>> getMessages(@Header("Authorization") String token,
                                            @Path("chatId") Long chatId);

    @POST("chats/{chatId}/messages")
    Call<MessageResponse> sendMessage(@Header("Authorization") String token,
                                      @Path("chatId") Long chatId,
                                      @Body SendMessageRequest request);
}