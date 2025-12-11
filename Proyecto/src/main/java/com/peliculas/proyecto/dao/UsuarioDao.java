package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;

import static com.peliculas.proyecto.conexion.Conexion.conexion;

public class UsuarioDao {

    private static UsuarioDao instance;
    private String ultimoErrorLogin;

    public UsuarioDao() {}

    public static UsuarioDao getInstance() {
        if (instance == null) {
            instance = new UsuarioDao();
        }
        return instance;
    }

    // Crear usuario vía procedimiento almacenado
    public void crear(Usuario u) throws SQLException {
        Conexion.abrirConexion();
        Connection con = conexion;

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
        Connection con = conexion;

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

    // Modificar usuario vía SP (requiere contraseña siempre)
    public void modificar(Usuario u) throws SQLException {
        Conexion.abrirConexion();
        Connection con = conexion;

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
        Connection con = conexion;

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
        ultimoErrorLogin = null;

        Conexion.abrirConexion();
        Connection con = conexion;

        // 1) Buscar por usuario
        String sql = "SELECT id_usuario, nombre_usuario, correo, num_tel, contrasena " +
                "FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                // Usuario NO existe
                if (!rs.next()) {

                    // Comprobamos si la contraseña coincide con alguna válida
                    if (contrasenaCoincideConAlguna(con, contrasena)) {
                        ultimoErrorLogin = "usuario"; // usuario mal, contraseña válida para otro
                    } else {
                        ultimoErrorLogin = "ambos";   // usuario mal y contraseña mal
                    }

                    return null;
                }

                // Usuario existe → comprobamos contraseña
                String passBD = rs.getString("contrasena");

                if (!passBD.trim().equals(contrasena.trim())) {
                    ultimoErrorLogin = "contrasena";
                    return null;
                }

                // Login correcto
                user = new Usuario(
                        rs.getString("nombre_usuario"),
                        rs.getString("correo"),
                        rs.getString("num_tel"),
                        passBD
                );
                user.setIdUsuario(rs.getInt("id_usuario"));
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return user;
    }

    // Consultar TODOS los usuarios registrados
    public ArrayList<Usuario> consultarUsuarios() throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        Conexion.abrirConexion();
        Connection conn = conexion;

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
                u.setIdUsuario(id);
                usuarios.add(u);
            }

            Conexion.cerrarConexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

    public boolean existeUsuario(String nombreUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection conn = conexion;

        String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nombreUsuario);

        ResultSet rs = stmt.executeQuery();
        boolean existe = false;

        if (rs.next()) {
            existe = rs.getInt(1) > 0;
        }

        Conexion.cerrarConexion();
        return existe;
    }

    // MÉTODO NUEVO: Actualizar usuario (para vistaPerfilUsuario)
    public boolean actualizarUsuario(Usuario u) {
        boolean actualizado = false;

        try {
            Conexion.abrirConexion();
            Connection con = conexion;

            String sql = "UPDATE usuario SET nombre_usuario = ?, correo = ?, num_tel = ?, contrasena = ? WHERE id_usuario = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, u.getNombreUsuario());
                ps.setString(2, u.getCorreo());
                ps.setString(3, u.getNumTelef());
                ps.setString(4, u.getContrasena());
                ps.setInt(5, u.getIdUsuario());

                actualizado = ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Conexion.cerrarConexion();
        }

        return actualizado;
    }

    public ArrayList<Usuario> obtenerUsuariosExcepto(String nombreActual) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_usuarios_excepto(?)}")) {
            cs.setString(1, nombreActual);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                usuarios.add(u);
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return usuarios;
    }

    public boolean existeCorreo(String correo) throws SQLException {
        Conexion.abrirConexion();
        Connection conn = conexion;

        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, correo);

        ResultSet rs = stmt.executeQuery();
        boolean existe = false;

        if (rs.next()) {
            existe = rs.getInt(1) > 0;
        }

        Conexion.cerrarConexion();
        return existe;
    }

    public boolean existeTelefono(String telefono) throws SQLException {
        Conexion.abrirConexion();
        Connection conn = conexion;

        String sql = "SELECT COUNT(*) FROM usuario WHERE num_tel = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, telefono);

        ResultSet rs = stmt.executeQuery();
        boolean existe = false;

        if (rs.next()) {
            existe = rs.getInt(1) > 0;
        }

        Conexion.cerrarConexion();
        return existe;
    }

    // Comprueba si la contraseña coincide con la de algún usuario existente
    private boolean contrasenaCoincideConAlguna(Connection con, String contrasena) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE TRIM(contrasena) = TRIM(?) LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String getUltimoErrorLogin() {
        return ultimoErrorLogin;
    }
}
