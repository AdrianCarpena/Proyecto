package com.example.proyecto;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.TareaRequest;
import com.example.proyecto.model.TareaResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TareaFragment extends Fragment {

    private LinearLayout layoutHoy, layoutSemana;
    private FloatingActionButton fabCrear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tarea, container, false);

        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });

        layoutHoy = view.findViewById(R.id.layoutHoy);
        layoutSemana = view.findViewById(R.id.layoutSemana);
        fabCrear = view.findViewById(R.id.fabCrear);

        fabCrear.setOnClickListener(v -> mostrarDialogCrearTarea());

        cargarTareas();

        return view;
    }

    // =========================
    // DIALOG CREAR TAREA
    // =========================
    private void mostrarDialogCrearTarea() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_crear_tarea, null);

        EditText etAsignatura = dialogView.findViewById(R.id.etAsignatura);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);
        EditText etHoras = dialogView.findViewById(R.id.etHorasEstimadas);

        Spinner spPrioridad = dialogView.findViewById(R.id.spPrioridad);
        Spinner spDificultad = dialogView.findViewById(R.id.spDificultad);

        String[] prioridades = {"ALTA", "MEDIA", "BAJA"};
        String[] dificultades = {"DIFICIL", "MEDIA", "FACIL"};

        spPrioridad.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                prioridades
        ));

        spDificultad.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dificultades
        ));

        // DatePicker
        etFecha.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, day) -> {

                        String fecha = year + "-" +
                                String.format("%02d", month + 1) + "-" +
                                String.format("%02d", day);

                        etFecha.setText(fecha);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );

            // mínimo mañana
            dialog.getDatePicker()
                    .setMinDate(System.currentTimeMillis() + 86400000);

            dialog.show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Nueva tarea")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {

                    String horasText =
                            etHoras.getText().toString().trim();

                    if (etAsignatura.getText().toString().trim().isEmpty()
                            || etFecha.getText().toString().trim().isEmpty()
                            || horasText.isEmpty()) {

                        Toast.makeText(getContext(),
                                "Complete todos los campos",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Integer horas = Integer.parseInt(horasText);

                    crearTarea(
                            etAsignatura.getText().toString().trim(),
                            etFecha.getText().toString().trim(),
                            spPrioridad.getSelectedItem().toString(),
                            spDificultad.getSelectedItem().toString(),
                            horas
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =========================
    // CREATE TASK
    // =========================
    private void crearTarea(String asignatura,
                            String fecha,
                            String prioridad,
                            String dificultad,
                            Integer horasEstimadas) {

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(),
                    "Token inválido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api =
                RetrofitClient.getClient().create(ApiService.class);

        TareaRequest request = new TareaRequest(
                asignatura,
                fecha,
                prioridad,
                dificultad,
                horasEstimadas
        );

        api.createTask("Bearer " + token, request)
                .enqueue(new Callback<TareaResponse>() {

                    @Override
                    public void onResponse(Call<TareaResponse> call,
                                           Response<TareaResponse> response) {

                        Log.e("CREATE_TASK",
                                "CODE: " + response.code());

                        if (response.isSuccessful()) {

                            Toast.makeText(getContext(),
                                    "Tarea creada",
                                    Toast.LENGTH_SHORT).show();

                            cargarTareas();

                        } else {

                            try {
                                if (response.errorBody() != null) {

                                    Log.e("CREATE_TASK",
                                            response.errorBody().string());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TareaResponse> call,
                                          Throwable t) {

                        Toast.makeText(getContext(),
                                "Error conexión",
                                Toast.LENGTH_SHORT).show();

                        Log.e("CREATE_TASK",
                                t.getMessage());
                    }
                });
    }

    // =========================
    // LOAD TASKS
    // =========================
    private void cargarTareas() {

        SharedPreferences prefs =
                requireContext().getSharedPreferences(
                        "Sesion",
                        Context.MODE_PRIVATE
                );

        String token = prefs.getString("token", null);

        if (token == null) return;

        ApiService api =
                RetrofitClient.getClient().create(ApiService.class);

        api.getTasks("Bearer " + token)
                .enqueue(new Callback<List<TareaResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<TareaResponse>> call,
                            Response<List<TareaResponse>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            layoutHoy.removeAllViews();
                            layoutSemana.removeAllViews();

                            for (TareaResponse t : response.body()) {

                                if (esHoy(t.getFecha())) {
                                    añadirVista(layoutHoy, t);
                                } else if (esEstaSemana(t.getFecha())) {
                                    añadirVista(layoutSemana, t);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<TareaResponse>> call,
                            Throwable t) {

                        Log.e("LOAD_TASKS", t.getMessage());
                    }
                });
    }

    // =========================
    // ITEM UI
    // =========================
    private void añadirVista(
            LinearLayout layout,
            TareaResponse tarea) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_tarea, layout, false);

        TextView tvAsignatura =
                item.findViewById(R.id.tvAsignatura);

        TextView tvFecha =
                item.findViewById(R.id.tvFecha);

        TextView tvInfo =
                item.findViewById(R.id.tvInfo);

        tvAsignatura.setText(tarea.getAsignatura());

        tvFecha.setText(
                getString(R.string.fecha)
                        + ": "
                        + tarea.getFecha()
        );

        tvInfo.setText(
                "Horas: "
                        + tarea.getHorasEstimadas()
                        + "h | "
                        + getString(R.string.prioridad)
                        + ": "
                        + tarea.getPrioridad()
                        + " | "
                        + getString(R.string.dificultad)
                        + ": "
                        + tarea.getDificultad()
        );

        layout.addView(item);
    }

    private boolean esHoy(String fecha) {
        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            Date f = sdf.parse(fecha);

            Calendar hoy = Calendar.getInstance();
            Calendar tarea = Calendar.getInstance();

            tarea.setTime(f);

            return hoy.get(Calendar.YEAR)
                    == tarea.get(Calendar.YEAR)
                    && hoy.get(Calendar.DAY_OF_YEAR)
                    == tarea.get(Calendar.DAY_OF_YEAR);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean esEstaSemana(String fecha) {

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            Date f = sdf.parse(fecha);

            Calendar hoy = Calendar.getInstance();
            Calendar limite = Calendar.getInstance();

            limite.add(Calendar.DAY_OF_YEAR, 7);

            Calendar tarea = Calendar.getInstance();
            tarea.setTime(f);

            return tarea.after(hoy)
                    && tarea.before(limite);

        } catch (Exception e) {
            return false;
        }
    }
}