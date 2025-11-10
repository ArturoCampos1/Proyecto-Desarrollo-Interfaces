package com.peliculas.proyecto.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Alquiler {

    private int idUsuario;
    private int idPelicula;
    private Date fechaAlquiler;
    private Date fechaDevolucion;
    private int precio;

    public Alquiler(int idUsuario, int idPelicula, Date fechaAlquiler, Date fechaDevolucion, int precio) {
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaDevolucion = fechaDevolucion;
        this.precio = precio;
    }

    public Alquiler() {
    }
}

