package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    private static UsuarioDao instance;

    private UsuarioDao() {}

    public static UsuarioDao getInstance() {
        if (instance == null) {
            instance = new UsuarioDao();
        }
        return instance;
    }

    public void crear(Usuario u) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_usuario(?,?,?,?)}")) {

            cs.setString(1, u.getNombreUsuario());
            cs.setString(2, u.getCorreo());
            cs.setString(3, u.getNumTelef());
            cs.setString(4, u.getContrasena());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    public Usuario buscarPorNombre(String nombreUsuario) throws SQLException {

        Usuario user = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL buscar_usuario_por_nombre(?)}")) {

            cs.setString(1, nombreUsuario);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombreUsuario(rs.getString("nombre_usuario"));
                user.setCorreo(rs.getString("correo"));
                user.setNumTelef(rs.getString("numTelef"));
                user.setContrasena(rs.getString("contrasena"));
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return user;
    }

    public void modificar(Usuario u) throws SQLException {

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs =
                     con.prepareCall("{CALL modificar_usuario(?,?,?,?,?)}"))
        {
            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombreUsuario());
            cs.setString(3, u.getCorreo());
            cs.setString(4, u.getNumTelef());
            cs.setString(5, u.getContrasena());
            cs.executeUpdate();

        } finally {
            Conexion.cerrarConexion();
        }
    }

    public void eliminar(int idUsuario) throws SQLException {

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs =
                     con.prepareCall("{CALL eliminar_usuario(?)}"))
        {
            cs.setInt(1, idUsuario);
            cs.executeUpdate();

        } finally {
            Conexion.cerrarConexion();
        }
    }
}