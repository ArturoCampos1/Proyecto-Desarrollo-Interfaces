package com.peliculas.proyecto.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PeliculasDisponibles {

    private int idListaPeliculasDisponibles;
    private ArrayList<Pelicula> peliculas;

}
