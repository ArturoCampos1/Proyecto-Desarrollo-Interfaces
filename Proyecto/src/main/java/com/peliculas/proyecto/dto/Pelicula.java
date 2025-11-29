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

    // Constructor completo con pathBanner y disponible
    public Pelicula(int idPelicula, String titulo, String anioSalida, String director, String resumen,
                    String genero, String pathBanner, double valoracion, int disponible) {
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

    // Constructor simplificado sin pathBanner ni disponible
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

    // Constructor vacío
    public Pelicula() {
        this.disponible = 0;
    }

    @Override
    public String toString() {
        return titulo;
    }

}