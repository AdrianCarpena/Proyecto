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
import com.example.proyecto.model.StudySessionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import java.util.Calendar;

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
                                        añadirCard(layoutStudy, e, e.getAsignatura(),
                                                e.getDuracionHoras() + " horas", getString(R.string.mover));
                                        break;

                                    case "BUSY":
                                        añadirCard(layoutBusy, e, e.getTitulo(),
                                                e.getDuracionHoras() + " horas", getString(R.string.borrar));
                                        break;

                                    case "EXAMEN":
                                        añadirCard(layoutExam, e, e.getAsignatura(),
                                                "Examen", getString(R.string.borrar));
                                        break;

                                    case "TAREA":
                                        añadirCard(layoutTask, e, e.getAsignatura(),
                                                "Entrega", getString(R.string.borrar));
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CalendarEventResponse>> call, Throwable t) {}
                });
    }

    private void añadirCard(LinearLayout layout, CalendarEventResponse evento,
                            String titulo, String sub, String textoBoton) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_evento, layout, false);

        TextView tvTitulo = item.findViewById(R.id.tvTitulo);
        TextView tvSub = item.findViewById(R.id.tvSub);
        Button btnAccion = item.findViewById(R.id.btnAccionEvento);

        tvTitulo.setText(titulo);
        tvSub.setText(sub);
        btnAccion.setText(textoBoton);

        btnAccion.setOnClickListener(v -> {

            if ("STUDY".equals(evento.getTipo())) {
                mostrarDatePickerMoverSesion(evento.getId());
            } else {
                confirmarBorrado(evento);
            }
        });

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

    private String getToken() {
        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        if (token == null) return null;

        return "Bearer " + token;
    }

    private ApiService getApi() {
        return RetrofitClient.getClient().create(ApiService.class);
    }

    private void confirmarBorrado(CalendarEventResponse evento) {

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.confirmar_borrado))
                .setMessage(getString(R.string.Seguro_que_quieres_borrar_este_elemento))
                .setPositiveButton(getString(R.string.borrar), (dialog, which) -> borrarEvento(evento))
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }

    private void borrarEvento(CalendarEventResponse evento) {

        String token = getToken();
        if (token == null) return;

        ApiService api = getApi();

        Call<Void> call;

        switch (evento.getTipo()) {

            case "EXAMEN":
                call = api.deleteExam(token, evento.getId());
                break;

            case "TAREA":
                call = api.deleteTask(token, evento.getId());
                break;

            case "BUSY":
                call = api.deleteBusy(token, evento.getId());
                break;

            default:
                Toast.makeText(getContext(), "No se puede borrar este elemento", Toast.LENGTH_SHORT).show();
                return;
        }

        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Elemento borrado", Toast.LENGTH_SHORT).show();
                    cargarEventos();
                } else {
                    Toast.makeText(getContext(), "No se pudo borrar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatePickerMoverSesion(Long sessionId) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {

                    String nuevaFecha = year + "-" +
                            String.format("%02d", month + 1) + "-" +
                            String.format("%02d", dayOfMonth);

                    moverSesion(sessionId, nuevaFecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());

        datePicker.show();
    }

    private void moverSesion(Long sessionId, String nuevaFecha) {

        String token = getToken();
        if (token == null) return;

        ApiService api = getApi();

        api.moveSession(token, sessionId, nuevaFecha)
                .enqueue(new Callback<StudySessionResponse>() {

                    @Override
                    public void onResponse(Call<StudySessionResponse> call,
                                           Response<StudySessionResponse> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Sesión movida", Toast.LENGTH_SHORT).show();

                            fechaSeleccionada = nuevaFecha;
                            cargarEventos();
                        } else {
                            Toast.makeText(getContext(),
                                    "No se puede mover a esa fecha",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StudySessionResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}