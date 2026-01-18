package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;

import static com.peliculas.proyecto.conexion.Conexion.conexion;

/**
 * DAO encargado de gestionar el acceso a datos de la entidad Usuario.
 * Contiene operaciones CRUD y lógica de autenticación.
 *
 * @author Iker Sillero, Kevin Mejías y Arturo Campos
 */
public class UsuarioDao {

    private static UsuarioDao instance;
    private String ultimoErrorLogin;

    /**
     * Constructor vacío del DAO.
     */
    public UsuarioDao() {}

    /**
     * Devuelve la instancia única del DAO (patrón Singleton).
     *
     * @return instancia de UsuarioDao
     */
    public static UsuarioDao getInstance() {
        if (instance == null) {
            instance = new UsuarioDao();
        }
        return instance;
    }

    /**
     * Crea un nuevo usuario en la base de datos mediante
     * un procedimiento almacenado.
     *
     * @param u Usuario a crear
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario Nombre del usuario a buscar
     * @return Usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Modifica un usuario existente mediante procedimiento almacenado.
     *
     * @param u Usuario con los datos actualizados
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Elimina un usuario de la base de datos por su ID.
     *
     * @param idUsuario ID del usuario a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Realiza el proceso de autenticación de un usuario.
     * Determina el motivo del fallo en caso de error.
     *
     * @param nombreUsuario Nombre de usuario
     * @param contrasena Contraseña introducida
     * @return Usuario autenticado o null si falla el login
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
    public Usuario login(String nombreUsuario, String contrasena) throws SQLException {
        Usuario user = null;
        ultimoErrorLogin = null;

        Conexion.abrirConexion();
        Connection con = conexion;

        String sql = "SELECT id_usuario, nombre_usuario, correo, num_tel, contrasena " +
                "FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    if (contrasenaCoincideConAlguna(con, contrasena)) {
                        ultimoErrorLogin = "usuario";
                    } else {
                        ultimoErrorLogin = "ambos";
                    }
                    return null;
                }

                String passBD = rs.getString("contrasena");

                if (!passBD.trim().equals(contrasena.trim())) {
                    ultimoErrorLogin = "contrasena";
                    return null;
                }

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

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Lista de usuarios
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Arturo Campos
     */
    public ArrayList<Usuario> consultarUsuarios() throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        Conexion.abrirConexion();
        Connection conn = conexion;

        String sql = "{ CALL obtener_todos_usuarios() }";

        try {
            CallableStatement cs = conn.prepareCall(sql);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getString("nombre_usuario"),
                        rs.getString("correo"),
                        rs.getString("num_tel"),
                        rs.getString("contrasena")
                );
                u.setIdUsuario(rs.getInt("id_usuario"));
                usuarios.add(u);
            }

            Conexion.cerrarConexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

    /**
     * Comprueba si existe un usuario con el nombre indicado.
     *
     * @param nombreUsuario Nombre de usuario
     * @return true si existe, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
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

    /**
     * Actualiza los datos de un usuario desde la vista de perfil.
     *
     * @param u Usuario con los datos modificados
     * @return true si se actualiza correctamente
     *
     * @author Iker Sillero
     */
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

    /**
     * Obtiene todos los usuarios excepto uno concreto.
     *
     * @param nombreActual Usuario a excluir
     * @return Lista de usuarios
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
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

    /**
     * Comprueba si un correo electrónico ya existe.
     *
     * @param correo Correo a comprobar
     * @return true si existe, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
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

    /**
     * Comprueba si un número de teléfono ya existe.
     *
     * @param telefono Número de teléfono
     * @return true si existe, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
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

    /**
     * Comprueba si una contraseña coincide con la de algún usuario.
     *
     * @param con Conexión a la base de datos
     * @param contrasena Contraseña a comprobar
     * @return true si coincide con alguna existente
     * @throws SQLException si ocurre un error en la base de datos
     *
     * @author Iker Sillero
     */
    private boolean contrasenaCoincideConAlguna(Connection con, String contrasena) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE TRIM(contrasena) = TRIM(?) LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Devuelve el último error producido durante el login.
     *
     * @return Código del último error de login
     */
    public String getUltimoErrorLogin() {
        return ultimoErrorLogin;
    }
}
