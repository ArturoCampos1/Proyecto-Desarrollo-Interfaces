package com.peliculas.proyecto.dto;

import lombok.Data;

import java.util.List;

@Data
public class PeliculasDisponibles {

    private int idListaPeliculasDisponibles;
    private List<Pelicula> peliculas;
}
