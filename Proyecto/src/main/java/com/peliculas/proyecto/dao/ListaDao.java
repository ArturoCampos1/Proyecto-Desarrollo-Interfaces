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

public class ListaDao implements CRUD<Lista> {

    private static ListaDao instance;

    public ListaDao() {}

    public static ListaDao getInstance() {
        if (instance == null) {
            instance = new ListaDao();
        }
        return instance;
    }

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

                // Crea usuario solo con ID para referenciar al usuario dueño de la lista
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                l.setUsuario(u);

                // Inicializamos lista vacía de películas; se pueden cargar después
                l.setPeliculas(PeliculaDao.getInstance().obtenerPeliculasDeLista(l.getIdLista()));

                listas.add(l);
            }

            return listas;
        } finally {
            Conexion.cerrarConexion();
        }
    }

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

                    l.setPeliculas(
                            PeliculaDao.getInstance().obtenerPeliculasDeLista(l.getIdLista())
                    );

                    return l;
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return null; // no encontrada
    }

}