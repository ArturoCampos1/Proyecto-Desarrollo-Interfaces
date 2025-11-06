package com.peliculas.proyecto.entities;

import lombok.Data;

import java.util.Date;

@Data
public class Alquiler {

    private int idUsuario;
    private int idPelicula;
    private Date fechaAlquiler;
    private Date fechaDevolucion;
    private int precio;

}

