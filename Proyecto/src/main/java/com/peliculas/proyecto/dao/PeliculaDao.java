package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Genero;
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

    public ArrayList<Pelicula> obtenerPeliculas() throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        ArrayList<Pelicula> lista = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas()}")) {
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setAnioSalida(rs.getString("anio_salida"));
                p.setDirector(rs.getString("director"));
                p.setResumen(rs.getString("resumen"));
                p.setGenero(Genero.valueOf(rs.getString("genero")));

                lista.add(p);
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    public ArrayList<Pelicula> obtenerPeliculasDeLista(int idLista) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        ArrayList<Pelicula> lista = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_de_lista(?)}")) {
            cs.setInt(1, idLista);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setAnioSalida(rs.getString("anio_salida"));
                p.setDirector(rs.getString("director"));
                p.setResumen(rs.getString("resumen"));
                p.setGenero(Genero.valueOf(rs.getString("genero")));

                lista.add(p);
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }
}
