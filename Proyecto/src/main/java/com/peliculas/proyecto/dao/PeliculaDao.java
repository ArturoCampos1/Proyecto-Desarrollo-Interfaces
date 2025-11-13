package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.*;
import java.util.ArrayList;

public class PeliculaDao {

    private static PeliculaDao instance;

    private PeliculaDao() {}

    public static PeliculaDao getInstance() {
        if (instance == null) {
            instance = new PeliculaDao();
        }
        return instance;
    }

    // Crear película
    public void crear(Pelicula p) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_pelicula(?,?,?,?,?)}")) {
            cs.setString(1, p.getTitulo());
            cs.setString(2, p.getAnioSalida());
            cs.setString(3, p.getDirector());
            cs.setString(4, p.getResumen());
            cs.setString(5, p.getGenero().name()); // suponiendo que Genero es enum

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // Modificar película (sin tocar valoracion)
    public void modificar(Pelicula p) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_pelicula(?,?,?,?,?,?)}")) {
            cs.setInt(1, p.getIdPelicula());
            cs.setString(2, p.getTitulo());
            cs.setString(3, p.getAnioSalida());
            cs.setString(4, p.getDirector());
            cs.setString(5, p.getResumen());
            cs.setString(6, p.getGenero().name());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // Eliminar película de la lista
    public void eliminar(int idPelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_pelicula(?)}")) {
            cs.setInt(1, idPelicula);
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

}
