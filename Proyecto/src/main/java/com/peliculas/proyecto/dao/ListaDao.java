package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO encargado de la gestión de las listas de usuario.
 * Permite crear, eliminar, modificar y consultar listas de películas.
 * Implementa la interfaz {@code CRUD<Lista>} para operaciones básicas.
 *
 * Patrón Singleton: se asegura una única instancia.
 *
 * @author Kevin Mejías
 */
public class ListaDao implements CRUD<Lista> {

    /** Instancia única del DAO (Singleton) */
    private static ListaDao instance;

    /** Constructor vacío */
    public ListaDao() {}

    /**
     * Devuelve la instancia única del DAO.
     *
     * @return instancia de {@code ListaDao}
     *
     * @author Kevin Mejías
     */
    public static ListaDao getInstance() {
        if (instance == null) {
            instance = new ListaDao();
        }
        return instance;
    }

    /**
     * Crea una nueva lista en la base de datos mediante
     * el procedimiento almacenado {@code crear_lista}.
     *
     * @param l Lista a crear
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    @Override
    public void crear(Lista l) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_lista(?,?)}")) {
            cs.setInt(1, l.getUsuario().getIdUsuario());
            cs.setString(2, l.getNombreLista());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Obtiene todas las listas de un usuario por su nombre de usuario
     * usando el procedimiento almacenado {@code obtener_listas_por_nombre_usuario}.
     * Cada lista incluye la información básica y las películas asociadas.
     *
     * @param nombreUsuario Nombre del usuario
     * @return Lista de objetos {@code Lista} del usuario
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    public ArrayList<Lista> obtenerPorNombreUsuario(String nombreUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_listas_por_nombre_usuario(?)}")) {
            cs.setString(1, nombreUsuario);
            ResultSet rs = cs.executeQuery();

            ArrayList<Lista> listas = new ArrayList<>();

            while (rs.next()) {
                Lista l = new Lista();
                l.setIdLista(rs.getInt("id_lista"));
                l.setNombreLista(rs.getString("nombre_lista"));

                // Usuario propietario de la lista
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                l.setUsuario(u);

                // Inicializamos lista de películas asociadas
                l.setPeliculas(PeliculaDao.getInstance().obtenerPeliculasDeLista(l.getIdLista()));

                listas.add(l);
            }

            return listas;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Modifica el nombre de una lista existente usando
     * el procedimiento almacenado {@code modificar_lista}.
     *
     * @param l Lista a modificar
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    @Override
    public void modificar(Lista l) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_lista(?,?)}")) {
            cs.setInt(1, l.getIdLista());
            cs.setString(2, l.getNombreLista());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Elimina una lista existente usando
     * el procedimiento almacenado {@code eliminar_lista}.
     *
     * @param l Lista a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    @Override
    public void eliminar(Lista l) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_lista(?)}")) {
            cs.setInt(1, l.getIdLista());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Busca una lista por el nombre del usuario y el nombre de la lista.
     *
     * @param idUsuario ID del usuario propietario
     * @param nombreLista Nombre de la lista
     * @return Objeto {@code Lista} encontrado, o {@code null} si no existe
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Arturo Campos
     */
    public Lista encontrarPorNombre(int idUsuario, String nombreLista) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "SELECT id_lista, id_usuario, nombre_lista " +
                "FROM lista " +
                "WHERE id_usuario = ? AND nombre_lista = ? " +
                "LIMIT 1";

        try (var ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, nombreLista);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lista l = new Lista();
                    l.setIdLista(rs.getInt("id_lista"));
                    l.setNombreLista(rs.getString("nombre_lista"));

                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    l.setUsuario(u);

                    l.setPeliculas(PeliculaDao.getInstance().obtenerPeliculasDeLista(l.getIdLista()));

                    return l;
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return null; // Lista no encontrada
    }
}