package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Administrador;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDao {

    public Administrador login(String nombreUsuario, String contrasena) throws SQLException {
        Administrador admin = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "SELECT id_admin, usuario, contrasena " +
                "FROM administrador WHERE usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passBD = rs.getString("contrasena");
                    System.out.println("Contraseña en BD: " + passBD);
                    System.out.println("Contraseña introducida: " + contrasena);

                    if (passBD != null && passBD.trim().equals(contrasena.trim())) {
                        admin = new Administrador(
                                rs.getInt("id_admin"),
                                rs.getString("usuario"),
                                passBD
                        );
                        admin.setIdAdmin(rs.getInt("id_admin"));
                    }
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }
        return admin;
    }

}
