package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO encargado de la gestión de la relación entre listas y películas.
 * Permite agregar o quitar películas de listas de usuario.
 *
 * Patrón Singleton: se asegura una única instancia.
 *
 * @author Kevin Mejías
 */
public class ListaPeliculaDao {

    /** Instancia única del DAO (Singleton) */
    private static ListaPeliculaDao instance;

    /** Constructor vacío */
    public ListaPeliculaDao() {}

    /**
     * Devuelve la instancia única del DAO.
     *
     * @return instancia de {@code ListaPeliculaDao}
     *
     * @author Kevin Mejías
     */
    public static ListaPeliculaDao getInstance() {
        if (instance == null) {
            instance = new ListaPeliculaDao();
        }
        return instance;
    }

    /**
     * Agrega una película a una lista existente usando el procedimiento
     * almacenado {@code agregar_pelicula_a_lista}.
     *
     * @param lista Lista a la que se agregará la película
     * @param pelicula Película a agregar
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    public void agregarPelicula(Lista lista, Pelicula pelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL agregar_pelicula_a_lista(?,?)}")) {
            cs.setInt(1, lista.getIdLista());       // ID de la lista
            cs.setInt(2, pelicula.getIdPelicula()); // ID de la película
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Quita una película de una lista existente usando el procedimiento
     * almacenado {@code quitar_pelicula_de_lista}.
     *
     * @param lista Lista de la que se eliminará la película
     * @param pelicula Película a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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
