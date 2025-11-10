package com.peliculas.proyecto.dto;

import lombok.Data;

@Data
public class ListaPelicula {

    private int idLista;
    private int idPelicula;

    public ListaPelicula(int idLista, int idPelicula) {
        this.idLista = idLista;
        this.idPelicula = idPelicula;
    }

    public ListaPelicula() {
    }
}