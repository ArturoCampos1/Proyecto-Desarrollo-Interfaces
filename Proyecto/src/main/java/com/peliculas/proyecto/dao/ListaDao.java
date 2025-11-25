package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;

public class ListaDao {

    private static ListaDao instance;

    private ListaDao() {}

    public static ListaDao getInstance() {
        if (instance == null) {
            instance = new ListaDao();
        }
        return instance;
    }

    // ✅ Crear lista SIN procedimientos
    public void crear(Lista lista) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "INSERT INTO listas (nombre_lista, id_usuario) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lista.getNombreLista());
            ps.setInt(2, lista.getUsuario().getIdUsuario());
            ps.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // ✅ Eliminar lista SIN procedimientos
    public void eliminar(Lista lista) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "DELETE FROM listas WHERE id_lista = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lista.getIdLista());
            ps.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // ✅ Obtener listas del usuario SIN procedimientos
    public ArrayList<Lista> obtenerPorNombreUsuario(String nombreUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        ArrayList<Lista> listas = new ArrayList<>();

        String sql = """
            SELECT l.id_lista, l.nombre_lista, u.id_usuario
            FROM listas l
            JOIN usuarios u ON l.id_usuario = u.id_usuario
            WHERE u.nombre_usuario = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Lista l = new Lista();
                l.setIdLista(rs.getInt("id_lista"));
                l.setNombreLista(rs.getString("nombre_lista"));

                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(nombreUsuario);

                l.setUsuario(u);

                listas.add(l);
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return listas;
    }
}
