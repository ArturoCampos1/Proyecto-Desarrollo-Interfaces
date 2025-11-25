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
    private ArrayList<Pelicula> peliculasAlquiladas = new ArrayList<>();

    // Único constructor: obliga a pasar todos los datos
    public Usuario(String nombreUsuario, String correo, String numTelef, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.numTelef = numTelef;
        this.contrasena = contrasena;
    }

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public Usuario() {

    }

    public String getTelef() {
        return numTelef;
    }
}
