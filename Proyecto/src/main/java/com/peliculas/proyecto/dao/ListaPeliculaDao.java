package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ListaPeliculaDao {

    private static ListaPeliculaDao instance;

    private ListaPeliculaDao() {}

    public static ListaPeliculaDao getInstance() {
        if (instance == null) {
            instance = new ListaPeliculaDao();
        }
        return instance;
    }

    public void agregarPelicula(Lista lista, Pelicula pelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL agregar_pelicula_a_lista(?,?)}")) {
            cs.setInt(1, lista.getIdLista());       // ID de la lista
            cs.setInt(2, pelicula.getIdPelicula()); // ID de la película recién insertada
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }


    public void quitarPelicula(Lista lista, Pelicula pelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL quitar_pelicula_de_lista(?,?)}")) {
            cs.setInt(1, lista.getIdLista());
            cs.setInt(2, pelicula.getIdPelicula());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }
}