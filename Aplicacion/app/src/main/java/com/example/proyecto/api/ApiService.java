package com.example.proyecto.api;

import com.example.proyecto.model.AuthResponse;
import com.example.proyecto.model.BusyHoursRequest;
import com.example.proyecto.model.BusyHoursResponse;
import com.example.proyecto.model.CalendarEventResponse;
import com.example.proyecto.model.ChatResponse;
import com.example.proyecto.model.CreateChatRequest;
import com.example.proyecto.model.ExamenRequest;
import com.example.proyecto.model.ExamenResponse;
import com.example.proyecto.model.JoinChatRequest;
import com.example.proyecto.model.LoginRequest;
import com.example.proyecto.model.LoginResponse;
import com.example.proyecto.model.MessageResponse;
import com.example.proyecto.model.SendMessageRequest;
import com.example.proyecto.model.StudySessionResponse;
import com.example.proyecto.model.TareaRequest;
import com.example.proyecto.model.TareaResponse;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Header;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body LoginRequest request);

    @GET("tasks")
    Call<List<TareaResponse>> getTasks(@Header("Authorization") String token);

    @POST("tasks")
    Call<TareaResponse> createTask(
            @Header("Authorization") String token,
            @Body TareaRequest request
    );

    @GET("exams")
    Call<List<ExamenResponse>> getExams(@Header("Authorization") String token);

    @POST("exams")
    Call<ExamenResponse> createExam(
            @Header("Authorization") String token,
            @Body ExamenRequest request
    );

    @GET("sessions")
    Call<List<StudySessionResponse>> getSessions(@Header("Authorization") String token);

    @PATCH("sessions/{id}/complete")
    Call<StudySessionResponse> completarSesion(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    @GET("calendar")
    Call<List<CalendarEventResponse>> getCalendar(
            @Header("Authorization") String token
    );

    @POST("busy-slots")
    Call<BusyHoursResponse> createBusy(
            @Header("Authorization") String token,
            @Body BusyHoursRequest request
    );
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