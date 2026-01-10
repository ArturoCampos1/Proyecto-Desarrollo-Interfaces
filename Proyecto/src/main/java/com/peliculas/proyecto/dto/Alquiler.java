package com.peliculas.proyecto.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Alquiler {

    private int idUsuario;
    private int idPelicula;
    private Timestamp fechaAlquiler;
    private Timestamp fechaDevolucion;

    public Alquiler(int idUsuario, int idPelicula, Timestamp fechaAlquiler) {
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaDevolucion = null;
    }

    public Alquiler() {
    }
}

