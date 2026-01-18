package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Administrador;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO encargado de la gestión de administradores.
 * Permite autenticar administradores y controlar
 * los errores producidos durante el inicio de sesión.
 *
 * @author Arturo Campos y Iker Sillero
 */
public class AdministradorDao {

    /**
     * Almacena el último error producido durante el proceso de login.
     * Puede indicar error de usuario, contraseña o ambos.
     */
    private String ultimoErrorLogin;

    /**
     * Realiza el inicio de sesión de un administrador.
     * Comprueba si el usuario existe, si la contraseña es correcta
     * y establece el tipo de error en caso de fallo.
     *
     * @param nombreUsuario Nombre de usuario introducido
     * @param contrasena Contraseña introducida
     * @return Administrador autenticado o null si el login falla
     * @throws SQLException Si ocurre un error de acceso a la base de datos
     *
     * @author Arturo Campos
     */
    public Administrador login(String nombreUsuario, String contrasena) throws SQLException {
        Administrador admin = null;
        ultimoErrorLogin = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "SELECT id_admin, usuario, contrasena FROM administrador WHERE usuario = ?";

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

                admin = new Administrador(
                        rs.getInt("id_admin"),
                        rs.getString("usuario"),
                        passBD
                );
                admin.setIdAdmin(rs.getInt("id_admin"));
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return admin;
    }

    /**
     * Comprueba si la contraseña introducida coincide
     * con la de algún administrador registrado.
     *
     * @param con Conexión activa a la base de datos
     * @param contrasena Contraseña introducida
     * @return true si coincide con alguna contraseña existente, false en caso contrario
     * @throws SQLException Si ocurre un error en la consulta
     *
     * @author Iker Sillero
     */
    private boolean contrasenaCoincideConAlguna(Connection con, String contrasena) throws SQLException {
        String sql = "SELECT 1 FROM administrador WHERE TRIM(contrasena) = TRIM(?) LIMIT 1";

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
     * @return Tipo de error ("usuario", "contrasena", "ambos") o null si no hubo error
     *
     * @author Iker Sillero
     */
    public String getUltimoErrorLogin() {
        return ultimoErrorLogin;
    }
}
