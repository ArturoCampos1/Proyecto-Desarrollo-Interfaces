package com.peliculas.proyecto.dto;

import lombok.Data;

@Data
public class Administrador {

    private int idAdmin;
    private String usuario;
    private String contrasena;

    public Administrador(int idAdmin, String usuario, String contrasena) {
        this.idAdmin = idAdmin;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public Administrador() {
    }
}
