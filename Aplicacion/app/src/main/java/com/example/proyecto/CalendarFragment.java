package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.BusyHoursRequest;
import com.example.proyecto.model.BusyHoursResponse;
import com.example.proyecto.model.CalendarEventResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private LinearLayout layoutStudy, layoutBusy, layoutExam, layoutTask;

    private String fechaSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        layoutStudy = view.findViewById(R.id.layoutStudy);
        layoutBusy = view.findViewById(R.id.layoutBusy);
        layoutExam = view.findViewById(R.id.layoutExam);
        layoutTask = view.findViewById(R.id.layoutTask);

        Button btnAdd = view.findViewById(R.id.btnAddBusy);

        fechaSeleccionada = hoy();

        calendarView.setOnDateChangeListener((v, year, month, day) -> {

            fechaSeleccionada = year + "-" +
                    String.format("%02d", month + 1) + "-" +
                    String.format("%02d", day);

            cargarEventos();
        });

        btnAdd.setOnClickListener(v -> mostrarDialog());

        cargarEventos();

        return view;
    }

    private void cargarEventos() {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);
        if (token == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getCalendar("Bearer " + token)
                .enqueue(new Callback<List<CalendarEventResponse>>() {

                    @Override
                    public void onResponse(Call<List<CalendarEventResponse>> call,
                                           Response<List<CalendarEventResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            layoutStudy.removeAllViews();
                            layoutBusy.removeAllViews();
                            layoutExam.removeAllViews();
                            layoutTask.removeAllViews();

                            for (CalendarEventResponse e : response.body()) {

                                if (!fechaSeleccionada.equals(e.getFecha())) continue;

                                switch (e.getTipo()) {

                                    case "STUDY":
                                        añadirCard(layoutStudy,
                                                e.getAsignatura(),
                                                e.getDuracionHoras() + " horas");
                                        break;

                                    case "BUSY":
                                        añadirCard(layoutBusy,
                                                e.getTitulo(),
                                                e.getDuracionHoras() + " horas");
                                        break;

                                    case "EXAMEN":
                                        añadirCard(layoutExam,
                                                e.getAsignatura(),
                                                "Examen");
                                        break;

                                    case "TAREA":
                                        añadirCard(layoutTask,
                                                e.getAsignatura(),
                                                "Entrega");
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CalendarEventResponse>> call, Throwable t) {}
                });
    }

    private void añadirCard(LinearLayout layout, String titulo, String sub) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_evento, layout, false);

        TextView tvTitulo = item.findViewById(R.id.tvTitulo);
        TextView tvSub = item.findViewById(R.id.tvSub);

        tvTitulo.setText(titulo);
        tvSub.setText(sub);

        layout.addView(item);
    }

    private void mostrarDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_busy, null);

        EditText etTitulo = dialogView.findViewById(R.id.etTitulo);
        EditText etHoras = dialogView.findViewById(R.id.etHoras);

        new AlertDialog.Builder(requireContext())
                .setTitle("Horas ocupadas")
                .setView(dialogView)
                .setPositiveButton("Crear", (d, w) -> {

                    crearBusy(
                            etTitulo.getText().toString(),
                            Integer.parseInt(etHoras.getText().toString())
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearBusy(String titulo, int horas) {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);
        if (token == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        BusyHoursRequest request =
                new BusyHoursRequest(titulo, fechaSeleccionada, horas);

        api.createBusy("Bearer " + token, request)
                .enqueue(new Callback<BusyHoursResponse>() {

                    @Override
                    public void onResponse(Call<BusyHoursResponse> call,
                                           Response<BusyHoursResponse> response) {

                        cargarEventos();
                    }

                    @Override
                    public void onFailure(Call<BusyHoursResponse> call, Throwable t) {}
                });
    }

    private String hoy() {
        return new java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());
    }
}