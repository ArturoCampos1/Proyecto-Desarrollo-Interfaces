package com.peliculas.proyecto.dto;

import lombok.Data;
import java.util.ArrayList;

@Data
public class Lista {

    private int idLista;
    private String nombreLista;
    private ArrayList<Pelicula> peliculas;
    private Usuario usuario;

    public Lista(int idLista, String nombreLista, ArrayList<Pelicula> peliculas, Usuario usuario) {
        this.idLista = idLista;
        this.nombreLista = nombreLista;
        this.peliculas = peliculas;
        this.usuario = usuario;
    }

    public Lista(String nombreLista, Usuario usuario) {
        this.nombreLista = nombreLista;
        this.usuario = usuario;
    }

    public Lista() {
        this.peliculas = new ArrayList<>();
    }
}