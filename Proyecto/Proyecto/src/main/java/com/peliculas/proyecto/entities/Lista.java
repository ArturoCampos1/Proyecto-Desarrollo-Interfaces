package com.peliculas.proyecto.entities;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Lista {

    private ArrayList<Pelicula> peliculas;
    private Usuario usuario;

}
