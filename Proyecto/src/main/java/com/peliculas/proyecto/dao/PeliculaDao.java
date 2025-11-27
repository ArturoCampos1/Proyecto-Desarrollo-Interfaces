package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Genero;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Pelicula> buscarPorNombre(String nombre) throws SQLException {
        List<Pelicula> lista = new ArrayList<>();

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL buscar_peliculas_por_nombre(?)}")) {

            cs.setString(1, nombre);

            try (ResultSet rs = cs.executeQuery()) {

                while (rs.next()) {
                    Pelicula p = new Pelicula();
                    p.setIdPelicula(rs.getInt("id_pelicula"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setAnioSalida(rs.getString("anio_salida"));
                    p.setDirector(rs.getString("director"));
                    p.setResumen(rs.getString("resumen"));
                    p.setGenero(Genero.valueOf(rs.getString("genero"))); // si existe
                    lista.add(p);
                }
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
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
