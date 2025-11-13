package com.peliculas.proyecto.dto;

import lombok.Data;

@Data
public class Pelicula {

    private int idPelicula;
    private String titulo;
    private String anioSalida;
    private String director;
    private String resumen;
    private Genero genero;
    private String pathBanner;
    private double valoracion;

    public Pelicula(int idPelicula, String titulo, String anioSalida, String director, String resumen, Genero genero, double valoracion) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.anioSalida = anioSalida;
        this.director = director;
        this.resumen = resumen;
        this.genero = genero;
        this.valoracion = valoracion;
    }

    public Pelicula() {
    }
}
