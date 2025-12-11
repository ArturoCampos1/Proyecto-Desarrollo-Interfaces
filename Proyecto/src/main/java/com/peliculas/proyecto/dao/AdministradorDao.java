package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Administrador;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDao {

    private String ultimoErrorLogin;

    public Administrador login(String nombreUsuario, String contrasena) throws SQLException {
        Administrador admin = null;
        ultimoErrorLogin = null; // limpiar error anterior

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        // 1) Buscar por usuario
        String sql = "SELECT id_admin, usuario, contrasena FROM administrador WHERE usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    // Usuario no existe → miramos si la contraseña coincide con alguna
                    if (contrasenaCoincideConAlguna(con, contrasena)) {
                        ultimoErrorLogin = "usuario";      // usuario mal, contraseña válida para otro
                    } else {
                        ultimoErrorLogin = "ambos";        // usuario mal y contraseña no coincide con ninguna
                    }
                    return null;
                }

                String passBD = rs.getString("contrasena");

                // Usuario existe pero contraseña incorrecta
                if (!passBD.trim().equals(contrasena.trim())) {
                    ultimoErrorLogin = "contrasena";
                    return null;
                }

                // Login correcto
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

    // Comprueba si la contraseña introducida coincide con la de algún admin existente
    private boolean contrasenaCoincideConAlguna(Connection con, String contrasena) throws SQLException {
        String sql = "SELECT 1 FROM administrador WHERE TRIM(contrasena) = TRIM(?) LIMIT 1";

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
