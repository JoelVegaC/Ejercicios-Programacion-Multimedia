package com.ejemplo.mibiblioteca;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ejemplo.mibiblioteca.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements LibroAdapter.OnLibroListener {

    private ActivityMainBinding binding;
    private LibroDao dao;
    private LibroAdapter adapter;

    // Usamos un hilo secundario para todas las operaciones de Room.
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        // Obtenemos la base de datos y el DAO.
        dao = AppDatabase.getInstance(this).libroDao();

        // Preparamos la lista.
        adapter = new LibroAdapter(this);
        binding.rvLibros.setLayoutManager(
                new LinearLayoutManager(this));
        binding.rvLibros.setAdapter(adapter);

        binding.btnAnadir.setOnClickListener(v -> anadirLibro());

        // Leemos los libros guardados al abrir la app.
        cargarLibros();
    }

    private void cargarLibros() {
        executor.execute(() -> {
            // Leemos en segundo plano.
            List<Libro> lista = dao.obtenerTodos();
            int leidos = dao.contarLeidos();

            // Volvemos al hilo principal para actualizar la pantalla.
            runOnUiThread(() -> mostrar(lista, leidos));
        });
    }

    private void mostrar(List<Libro> lista, int leidos) {
        adapter.setLibros(lista);

        binding.tvVacio.setVisibility(
                lista.isEmpty() ? View.VISIBLE : View.GONE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(
                    getString(
                            R.string.resumen_libros,
                            lista.size(),
                            leidos));
        }
    }

    private void anadirLibro() {
        String titulo = texto(binding.etTitulo.getText());
        String autor = texto(binding.etAutor.getText());
        String paginasTexto = texto(binding.etPaginas.getText());

        // Quitamos errores anteriores antes de validar otra vez.
        binding.tilTitulo.setError(null);
        binding.tilAutor.setError(null);
        binding.tilPaginas.setError(null);

        boolean correcto = true;

        if (titulo.isEmpty()) {
            binding.tilTitulo.setError(
                    getString(R.string.error_obligatorio));
            correcto = false;
        }

        if (autor.isEmpty()) {
            binding.tilAutor.setError(
                    getString(R.string.error_obligatorio));
            correcto = false;
        }

        int paginas = 0;

        try {
            paginas = Integer.parseInt(paginasTexto);
        } catch (NumberFormatException ignored) {
        }

        if (paginas <= 0) {
            binding.tilPaginas.setError(
                    getString(R.string.error_paginas));
            correcto = false;
        }

        if (!correcto) return;

        Libro nuevo = new Libro(titulo, autor, paginas);

        executor.execute(() -> {
            // Insertamos en segundo plano.
            dao.insertar(nuevo);

            runOnUiThread(() -> {
                binding.etTitulo.setText("");
                binding.etAutor.setText("");
                binding.etPaginas.setText("");
                binding.etTitulo.requestFocus();

                Toast.makeText(
                        this,
                        R.string.libro_guardado,
                        Toast.LENGTH_SHORT
                ).show();
            });

            cargarLibros();
        });
    }

    @Override
    public void onCambiarLeido(Libro libro, boolean leido) {
        libro.leido = leido;

        executor.execute(() -> {
            dao.actualizar(libro);
            cargarLibros();
        });
    }

    @Override
    public void onEliminar(Libro libro) {
        executor.execute(() -> {
            dao.eliminar(libro);
            cargarLibros();

            runOnUiThread(() ->
                    Snackbar.make(
                                    binding.getRoot(),
                                    R.string.libro_eliminado,
                                    Snackbar.LENGTH_LONG)
                            .setAction(R.string.deshacer, v ->
                                    executor.execute(() -> {
                                        dao.insertar(libro);
                                        cargarLibros();
                                    }))
                            .show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private String texto(CharSequence texto) {
        return texto == null
                ? ""
                : texto.toString().trim();
    }
}
