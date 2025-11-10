package com.peliculas.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Resena {

    private int idReseña;
    private double valoracion;
    private String texto;
    private LocalDateTime fecha;
    private Usuario usuario;
    private Pelicula pelicula;

    public Resena(int idReseña, double valoracion, String texto, LocalDateTime fecha, Usuario usuario, Pelicula pelicula) {
        this.idReseña = idReseña;
        this.valoracion = valoracion;
        this.texto = texto;
        this.fecha = fecha;
        this.usuario = usuario;
        this.pelicula = pelicula;
    }

    public Resena() {
    }
}
