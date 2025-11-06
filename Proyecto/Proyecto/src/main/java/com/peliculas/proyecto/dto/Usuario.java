package com.peliculas.proyecto.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Usuario {

    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String numTelef;
    private String contrasena;
    private ArrayList<Pelicula> peliculasAlquiladas;

}
