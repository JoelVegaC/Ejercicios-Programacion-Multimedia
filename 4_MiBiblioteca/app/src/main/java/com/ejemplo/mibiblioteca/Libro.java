package com.ejemplo.mibiblioteca;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// Room crea una tabla "libros" con estos datos.
@Entity(tableName = "libros")
public class Libro {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String titulo = "";

    @NonNull
    public String autor = "";

    public int paginas;
    public boolean leido;

    // Room usa este constructor vacío.
    public Libro() {
    }

    @Ignore
    public Libro(@NonNull String titulo, @NonNull String autor, int paginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.paginas = paginas;
        this.leido = false;
    }
}
