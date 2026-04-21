package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.ExamenRequest;
import com.example.proyecto.model.ExamenResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExamenFragment extends Fragment {

    private LinearLayout layoutHoy, layoutSemana;
    private FloatingActionButton fabCrear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_examen, container, false);

        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });

        layoutHoy = view.findViewById(R.id.layoutHoy);
        layoutSemana = view.findViewById(R.id.layoutSemana);
        fabCrear = view.findViewById(R.id.fabCrear);

        fabCrear.setOnClickListener(v -> mostrarDialogCrearExamen());

        cargarExamenes();

        return view;
    }

    private void mostrarDialogCrearExamen() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_crear_examen, null);

        EditText etAsignatura = dialogView.findViewById(R.id.etAsignatura);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);
        Spinner spPrioridad = dialogView.findViewById(R.id.spPrioridad);
        Spinner spDificultad = dialogView.findViewById(R.id.spDificultad);

        String[] prioridades = {"ALTA", "MEDIA", "BAJA"};
        String[] dificultades = {"DIFICIL", "MEDIA", "FACIL"};

        spPrioridad.setAdapter(new android.widget.ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                prioridades
        ));

        spDificultad.setAdapter(new android.widget.ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dificultades
        ));

        // DatePicker
        etFecha.setOnClickListener(v -> {

            java.util.Calendar c = java.util.Calendar.getInstance();

            android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(
                    getContext(),
                    (view, year, month, day) -> {
                        String fecha = year + "-" +
                                String.format("%02d", month + 1) + "-" +
                                String.format("%02d", day);
                        etFecha.setText(fecha);
                    },
                    c.get(java.util.Calendar.YEAR),
                    c.get(java.util.Calendar.MONTH),
                    c.get(java.util.Calendar.DAY_OF_MONTH)
            );

            // SOLO FUTURO
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() + 86400000);

            dialog.show();
        });

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Nuevo examen")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {

                    crearExamen(
                            etAsignatura.getText().toString(),
                            etFecha.getText().toString(),
                            spPrioridad.getSelectedItem().toString(),
                            spDificultad.getSelectedItem().toString()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearExamen(String asignatura, String fecha, String prioridad, String dificultad) {

        SharedPreferences prefs = getActivity()
                .getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        if (token == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        ExamenRequest request = new ExamenRequest(
                asignatura,
                fecha,
                prioridad,
                dificultad
        );

        api.createExam("Bearer " + token, request)
                .enqueue(new retrofit2.Callback<ExamenResponse>() {

                    @Override
                    public void onResponse(Call<ExamenResponse> call, Response<ExamenResponse> response) {

                        Toast.makeText(getContext(),
                                "CODE: " + response.code(),
                                Toast.LENGTH_SHORT).show();

                        Log.e("EXAM", "CODE: " + response.code());

                        if (response.errorBody() != null) {
                            try {
                                Log.e("EXAM", response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (response.isSuccessful()) {
                            Log.d("EXAM", "CREADO OK");
                            cargarExamenes();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExamenResponse> call, Throwable t) {
                        Log.e("EXAM", t.getMessage());
                    }
                });
    }

    private void cargarExamenes() {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(getContext(),
                    "Sesión inválida (token vacío)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        token = token.trim();

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getExams("Bearer " + token).enqueue(new Callback<List<ExamenResponse>>() {

            @Override
            public void onResponse(Call<List<ExamenResponse>> call,
                                   Response<List<ExamenResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    layoutHoy.removeAllViews();
                    layoutSemana.removeAllViews();

                    for (ExamenResponse e : response.body()) {

                        if (esHoy(e.getFecha())) {
                            añadirVista(layoutHoy, e);
                        } else if (esSemana(e.getFecha())) {
                            añadirVista(layoutSemana, e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ExamenResponse>> call, Throwable t) {
                Log.e("EXAMS", t.getMessage());
            }
        });
    }

    private void añadirVista(LinearLayout layout, ExamenResponse examen) {

        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_examen, layout, false);

        TextView tvAsignatura = item.findViewById(R.id.tvAsignatura);
        TextView tvFecha = item.findViewById(R.id.tvFecha);
        TextView tvInfo = item.findViewById(R.id.tvInfo);

        tvAsignatura.setText(examen.getAsignatura());
        tvFecha.setText(getString(R.string.fecha) + getString(R.string.dospuntos) + examen.getFecha());

        tvInfo.setText(
                getString(R.string.prioridad) + getString(R.string.dospuntos) + examen.getPrioridad() +
                        " | " +
                        getString(R.string.dificultad) + getString(R.string.dospuntos) + examen.getDificultad()
        );

        layout.addView(item);
    }


    private boolean esHoy(String fecha) {

        String hoy = new java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        return fecha.equals(hoy);
    }

    private boolean esSemana(String fecha) {

        try {
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

            java.util.Date fechaExamen = sdf.parse(fecha);

            java.util.Calendar hoy = java.util.Calendar.getInstance();
            hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
            hoy.set(java.util.Calendar.MINUTE, 0);
            hoy.set(java.util.Calendar.SECOND, 0);
            hoy.set(java.util.Calendar.MILLISECOND, 0);

            java.util.Calendar limite = java.util.Calendar.getInstance();
            limite.setTime(hoy.getTime());
            limite.add(java.util.Calendar.DAY_OF_YEAR, 7);

            java.util.Calendar examen = java.util.Calendar.getInstance();
            examen.setTime(fechaExamen);

            return examen.after(hoy) && examen.before(limite);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layoutHoy.removeAllViews();
        layoutSemana.removeAllViews();

        cargarExamenes();
    }


}