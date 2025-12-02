package com.peliculas.proyecto.dto;

import lombok.Data;

@Data
public class Alquiler {

    private int idUsuario;
    private int idPelicula;
    private String fechaAlquiler;
    private String fechaDevolucion;

    public Alquiler(int idUsuario, int idPelicula, String fechaAlquiler) {
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaDevolucion = "";
    }

    public Alquiler() {
    }
}

