package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.StudySessionResponse;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private LinearLayout layoutEstudioHoy;
    private LinearLayout layoutTareasHoy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        layoutEstudioHoy = view.findViewById(R.id.layoutEstudioHoy);
        layoutTareasHoy = view.findViewById(R.id.layoutTareasHoy);

        cargarSesionesHoy();

        return view;
    }

    private void cargarSesionesHoy() {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "Token null", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getSessions("Bearer " + token)
                .enqueue(new Callback<List<StudySessionResponse>>() {

                    @Override
                    public void onResponse(Call<List<StudySessionResponse>> call,
                                           Response<List<StudySessionResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            layoutEstudioHoy.removeAllViews();
                            layoutTareasHoy.removeAllViews();

                            Calendar calHoy = Calendar.getInstance();
                            calHoy.set(Calendar.HOUR_OF_DAY, 0);
                            calHoy.set(Calendar.MINUTE, 0);
                            calHoy.set(Calendar.SECOND, 0);
                            calHoy.set(Calendar.MILLISECOND, 0);

                            for (StudySessionResponse s : response.body()) {

                                try {

                                    String fechaStr = s.getFecha();

                                    if (fechaStr.length() > 10) {
                                        fechaStr = fechaStr.substring(0, 10);
                                    }

                                    String[] partes = fechaStr.split("-");

                                    int year = Integer.parseInt(partes[0]);
                                    int month = Integer.parseInt(partes[1]) - 1;
                                    int day = Integer.parseInt(partes[2]);

                                    Calendar calSesion = Calendar.getInstance();
                                    calSesion.set(year, month, day, 0, 0, 0);
                                    calSesion.set(Calendar.MILLISECOND, 0);

                                    if (calSesion.equals(calHoy)) {

                                        // 📌 SEPARACIÓN CORRECTA
                                        if (s.getExamenId() != null) {
                                            añadirSesionExamen(s);
                                        } else if (s.getTareaId() != null) {
                                            añadirSesionTarea(s);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Toast.makeText(getContext(),
                                    "Error: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<StudySessionResponse>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Error conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void añadirSesionTarea(StudySessionResponse s) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_check, layoutTareasHoy, false);

        CheckBox check = item.findViewById(R.id.check);
        TextView tv = item.findViewById(R.id.tvTexto);

        tv.setText(s.getAsignatura() + " (" + s.getDuracionHoras() + "h)");

        check.setChecked(s.isCheck());

        // 🔥 BLOQUEO SI YA ESTÁ HECHA
        if (s.isCheck()) {
            check.setEnabled(false);
            check.setClickable(false);
        }

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !s.isCheck()) {
                completarSesion(s.getId());
                check.setEnabled(false); // 🔒 bloqueo inmediato
            }
        });

        layoutTareasHoy.addView(item);
    }

    private void añadirSesionExamen(StudySessionResponse s) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_check, layoutEstudioHoy, false);

        CheckBox check = item.findViewById(R.id.check);
        TextView tv = item.findViewById(R.id.tvTexto);

        tv.setText(s.getAsignatura() + " (" + s.getDuracionHoras() + "h)");

        check.setChecked(s.isCheck());

        // 🔥 BLOQUEO SI YA ESTÁ HECHA
        if (s.isCheck()) {
            check.setEnabled(false);
            check.setClickable(false);
        }

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !s.isCheck()) {
                completarSesion(s.getId());
                check.setEnabled(false); // 🔒 bloqueo inmediato
            }
        });

        layoutEstudioHoy.addView(item);
    }

    private void completarSesion(Long id) {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);
        if (token == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.completarSesion("Bearer " + token, id)
                .enqueue(new Callback<StudySessionResponse>() {

                    @Override
                    public void onResponse(Call<StudySessionResponse> call,
                                           Response<StudySessionResponse> response) {

                        if (response.isSuccessful()) {
                            cargarSesionesHoy();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al completar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StudySessionResponse> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Error conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}