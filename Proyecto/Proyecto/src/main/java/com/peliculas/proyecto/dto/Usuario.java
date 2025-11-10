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

    public Usuario(int idUsuario, String nombreUsuario, String correo, String numTelef, String contrasena, ArrayList<Pelicula> peliculasAlquiladas) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.numTelef = numTelef;
        this.contrasena = contrasena;
        this.peliculasAlquiladas = peliculasAlquiladas;
    }

    public Usuario() {
    }
}
