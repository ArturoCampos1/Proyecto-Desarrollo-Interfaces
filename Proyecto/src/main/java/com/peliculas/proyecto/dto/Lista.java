package com.peliculas.proyecto.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Lista {

    private int idLista;
    private ArrayList<Pelicula> peliculas;
    private Usuario usuario;

    public Lista(int idLista, ArrayList<Pelicula> peliculas, Usuario usuario) {
        this.idLista = idLista;
        this.peliculas = peliculas;
        this.usuario = usuario;
    }

    public Lista() {
    }
}
