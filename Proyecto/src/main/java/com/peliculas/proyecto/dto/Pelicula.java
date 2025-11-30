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

    // Constructor para crear nuevas películas
    public Pelicula(String titulo, String anioSalida, String director,
                    String resumen, String genero,
                    String pathBanner, double valoracion) {
        this.titulo = titulo;
        this.anioSalida = anioSalida;
        this.director = director;
        this.resumen = resumen;
        this.genero = genero;
        this.pathBanner = pathBanner;
        this.valoracion = valoracion;
        this.disponible = 1;
    }

    // Constructor completo (por si lo necesitas al leer de BD)
    public Pelicula(int idPelicula, String titulo, String anioSalida, String director,
                    String resumen, String genero,
                    String pathBanner, double valoracion, int disponible) {
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

    // Constructor vacío
    public Pelicula() {
        this.disponible = 1;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
