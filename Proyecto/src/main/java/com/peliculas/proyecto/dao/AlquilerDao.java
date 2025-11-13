package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Alquiler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDao implements CRUD<Alquiler> {

    private static AlquilerDao instance;

    private AlquilerDao() {}

    public static AlquilerDao getInstance() {
        if (instance == null) {
            instance = new AlquilerDao();
        }
        return instance;
    }

    @Override
    public void crear(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_alquiler(?,?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());
            cs.setInt(3, a.getPrecio());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    public List<Alquiler> obtenerPorUsuario(int idUsuario) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_alquileres_por_usuario(?)}")) {
            cs.setInt(1, idUsuario);
            ResultSet rs = cs.executeQuery();

            List<Alquiler> lista = new ArrayList<>();

            while (rs.next()) {
                Alquiler a = new Alquiler();
                a.setIdUsuario(rs.getInt("id_usuario"));
                a.setIdPelicula(rs.getInt("id_pelicula"));
                a.setPrecio(rs.getInt("precio"));

                Timestamp tsAlquiler = rs.getTimestamp("fecha_alquiler");
                if (tsAlquiler != null) {
                    a.setFechaAlquiler(new java.util.Date(tsAlquiler.getTime()));
                }

                Timestamp tsDevolucion = rs.getTimestamp("fecha_devolucion");
                if (tsDevolucion != null) {
                    a.setFechaDevolucion(new java.util.Date(tsDevolucion.getTime()));
                }

                lista.add(a);
            }

            return lista;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    @Override
    public void modificar(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_alquiler(?,?,?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());
            cs.setInt(3, a.getPrecio());
            cs.setTimestamp(4, new Timestamp(a.getFechaDevolucion().getTime()));

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    @Override
    public void eliminar(Alquiler a) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_alquiler(?,?)}")) {
            cs.setInt(1, a.getIdUsuario());
            cs.setInt(2, a.getIdPelicula());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }
}