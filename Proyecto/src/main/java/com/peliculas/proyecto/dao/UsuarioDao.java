package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDao {

    private static UsuarioDao instance;

    private UsuarioDao() {}

    public static UsuarioDao getInstance() {
        if (instance == null) {
            instance = new UsuarioDao();
        }
        return instance;
    }

    // Crear usuario vía procedimiento almacenado
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

    // Buscar usuario por nombre
    public Usuario buscarPorNombre(String nombreUsuario) throws SQLException {
        Usuario user = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL buscar_usuario_por_nombre(?)}")) {
            cs.setString(1, nombreUsuario);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario(
                            rs.getString("nombre_usuario"),
                            rs.getString("correo"),
                            rs.getString("numTelef"),
                            rs.getString("contrasena")
                    );
                    user.setIdUsuario(rs.getInt("id_usuario"));
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return user;
    }

    // Modificar usuario
    public void modificar(Usuario u) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_usuario(?,?,?,?,?)}")) {
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

    // Eliminar usuario
    public void eliminar(int idUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_usuario(?)}")) {
            cs.setInt(1, idUsuario);
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // Login
    public Usuario login(String nombreUsuario, String contrasena) throws SQLException {
        Usuario user = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "SELECT id_usuario, nombre_usuario, correo, num_tel, contrasena " +
                "FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passBD = rs.getString("contrasena");
                    System.out.println("Contraseña en BD: " + passBD);
                    System.out.println("Contraseña introducida: " + contrasena);

                    if (passBD != null && passBD.trim().equals(contrasena.trim())) {
                        user = new Usuario(
                                rs.getString("nombre_usuario"),
                                rs.getString("correo"),
                                rs.getString("num_tel"),
                                passBD
                        );
                        user.setIdUsuario(rs.getInt("id_usuario"));
                    }
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }
        return user;
    }

    // Insert directo (sin SP)
    public void insert(Usuario usuario) throws SQLException {
        Conexion.abrirConexion();
        Connection conn = Conexion.conexion;

        String sql = "INSERT INTO usuario (nombre_usuario, correo, num_tel, contrasena) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getNumTelef());
            stmt.setString(4, usuario.getContrasena());
            stmt.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    //Consultar TODOS los usuarios registrados
    public ArrayList<Usuario> consultarUsuarios() throws SQLException{
        ArrayList<Usuario> usuarios = new ArrayList<>();

        Conexion.abrirConexion();
        Connection conn = Conexion.conexion;

        String sql = "{ CALL obtener_todos_usuarios() }";

        try {

            CallableStatement cs = conn.prepareCall(sql);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nombre = rs.getString("nombre_usuario");
                String correo = rs.getString("correo");
                String telefono = rs.getString("num_tel");
                String contrasena = rs.getString("contrasena");

                Usuario u = new Usuario(nombre, correo, telefono, contrasena);
                usuarios.add(u);
            }
            Conexion.cerrarConexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

}
