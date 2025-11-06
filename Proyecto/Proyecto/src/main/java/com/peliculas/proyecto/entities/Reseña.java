package com.peliculas.proyecto.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reseña {

    private int idReseña;
    private double valoracion;
    private String texto;
    private LocalDateTime fecha;
    private Usuario usuario;
    private Pelicula pelicula;

}
