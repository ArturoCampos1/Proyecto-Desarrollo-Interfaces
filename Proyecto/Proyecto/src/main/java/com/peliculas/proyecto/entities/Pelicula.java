package com.peliculas.proyecto.entities;

import lombok.Data;

@Data
public class Pelicula {

    private int idPelicula;
    private String titulo;
    private String anioSalida;
    private String director;
    private String resumen;
    private Genero genero;
    private boolean disponible;
    private double valoracion;

}
