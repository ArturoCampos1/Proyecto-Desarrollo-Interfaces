package com.peliculas.proyecto.dto;

import lombok.Data;

@Data
public class Pelicula {

    private int idPelicula;
    private String titulo;
    private String anioSalida;
    private String director;
    private String resumen;
    private String genero;
    private String pathBanner;
    private double valoracion;
    private int disponible;

    public Pelicula(int idPelicula, String titulo, String anioSalida, String director, String resumen, String genero, String pathBanner, double valoracion, int disponible) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.anioSalida = anioSalida;
        this.director = director;
        this.resumen = resumen;
        this.genero = genero;
        this.pathBanner = pathBanner;
        this.valoracion = valoracion;
        this.disponible = disponible;
    }

    public Pelicula(int idPelicula, String titulo, String anioSalida, String director, String resumen, String genero, double valoracion) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.anioSalida = anioSalida;
        this.director = director;
        this.resumen = resumen;
        this.genero = genero;
        this.valoracion = valoracion;
        this.disponible = 0;
    }

    public Pelicula(int idPelicula, String titulo, String anioSalida, String director, String resumen, String genero, double valoracion, int disponible) {
        this(idPelicula, titulo, anioSalida, director, resumen, genero, valoracion);
        this.disponible = disponible;
    }

    public Pelicula() {
        this.disponible = 0;
    }
}