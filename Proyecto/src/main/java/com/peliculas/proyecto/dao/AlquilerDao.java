package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Alquiler;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO encargado de la gestión de los alquileres.
 * Permite crear, eliminar, modificar y consultar alquileres
 * y obtener las películas alquiladas por un usuario.
 * Implementa la interfaz CRUD para operaciones básicas.
 *
 * Utiliza el patrón Singleton para asegurar que solo haya
 * una instancia de este DAO en toda la aplicación.
 *
 * @author Kevin
 */
public class AlquilerDao implements CRUD<Alquiler> {

    /** Instancia única del DAO (Singleton) */
    private static AlquilerDao instance;

    /** Constructor vacío */
    public AlquilerDao() {}

    /**
     * Devuelve la instancia única del DAO (Singleton).
     *
     * @return instancia única de AlquilerDao
     */
    public static AlquilerDao getInstance() {
        if (instance == null) {
            instance = new AlquilerDao();
        }
        return instance;
    }

    /**
     * Crea un nuevo alquiler en la base de datos utilizando
     * el procedimiento almacenado {@code crear_alquiler}.
     *
     * @param a Alquiler a crear
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    @Override
    public void crear(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_alquiler(?,?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());
            cs.setTimestamp(3, a.getFechaAlquiler());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Obtiene todos los alquileres de un usuario específico
     * mediante el procedimiento almacenado
     * {@code obtener_alquileres_por_usuario}.
     *
     * @param idUsuario ID del usuario
     * @return Lista de alquileres del usuario
     * @throws SQLException Si ocurre un error en la base de datos
     * @author Kevin Mejías
     */
    public ArrayList<Alquiler> obtenerPorUsuario(int idUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_alquileres_por_usuario(?)}")) {
            cs.setInt(1, idUsuario);
            ResultSet rs = cs.executeQuery();

            ArrayList<Alquiler> lista = new ArrayList<>();
            while (rs.next()) {
                Alquiler a = new Alquiler();
                a.setIdUsuario(rs.getInt("id_usuario"));
                a.setIdPelicula(rs.getInt("id_pelicula"));
                a.setFechaAlquiler(rs.getTimestamp("fecha_alquiler"));
                a.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                lista.add(a);
            }
            return lista;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Obtiene todos los alquileres existentes en la base de datos.
     *
     * @return Lista de todos los alquileres
     * @throws SQLException Si ocurre un error en la base de datos
     * @author Kevin Mejías
     */
    public ArrayList<Alquiler> obtenerTodos() throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("SELECT * FROM alquiler")) {
            ResultSet rs = cs.executeQuery();

            ArrayList<Alquiler> lista = new ArrayList<>();
            while (rs.next()) {
                Alquiler a = new Alquiler();
                a.setIdUsuario(rs.getInt("id_usuario"));
                a.setIdPelicula(rs.getInt("id_pelicula"));
                a.setFechaAlquiler(rs.getTimestamp("fecha_alquiler"));
                a.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                lista.add(a);
            }
            return lista;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Modifica un alquiler existente usando el procedimiento
     * almacenado {@code modificar_alquiler}.
     * Nota: actualmente no se utiliza.
     *
     * @param a Alquiler a modificar
     * @throws SQLException Si ocurre un error en la base de datos
     * @author Kevin Mejías
     */
    @Override
    public void modificar(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_alquiler(?,?,?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());
            // cs.setInt(3, a.getPrecio());
            // cs.setTimestamp(4, new Timestamp(a.getFechaDevolucion().getTime()));
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Elimina un alquiler usando el procedimiento almacenado
     * {@code eliminar_alquiler}.
     *
     * @param a Alquiler a eliminar
     * @throws SQLException Si ocurre un error en la base de datos
     * @author Kevin Mejías
     */
    @Override
    public void eliminar(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_alquiler(?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Obtiene la lista de películas actualmente alquiladas
     * por un usuario específico mediante el procedimiento
     * almacenado {@code obtener_peliculas_alquiladas_por_usuario}.
     *
     * @param idUsuario ID del usuario
     * @return Lista de películas alquiladas por el usuario
     * @throws SQLException Si ocurre un error en la base de datos
     * @author Kevin Mejías
     */
    public ArrayList<Pelicula> obtenerPeliculasAlquiladasPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Pelicula> lista = new ArrayList<>();
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_alquiladas_por_usuario(?)}")) {
            cs.setInt(1, idUsuario);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setDirector(rs.getString("director"));
                p.setAnioSalida(rs.getString("anio_salida"));
                p.setGenero(rs.getString("genero"));
                p.setResumen(rs.getString("resumen"));
                p.setValoracion(rs.getDouble("valoracion"));
                p.setDisponible(rs.getInt("disponible"));
                p.setPathBanner(rs.getString("url_photo"));
                lista.add(p);
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }
}