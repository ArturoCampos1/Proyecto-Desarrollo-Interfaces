package com.peliculas.proyecto.entities;

import lombok.Data;

@Data
public class Reseña {

    private int idReseña;
    private double valoracion;
    private String texto;
    private Usuario usuario;
    private Pelicula pelicula;


}
