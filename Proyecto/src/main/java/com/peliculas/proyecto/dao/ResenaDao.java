package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Resena;
import com.peliculas.proyecto.dto.Usuario;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ResenaDao implements CRUD<Resena> {

    private static ResenaDao instance;

    private ResenaDao() {}

    public static ResenaDao getInstance() {
        if (instance == null) {
            instance = new ResenaDao();
        }
        return instance;
    }

    @Override
    public void crear(Resena r) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL crear_resena(?,?,?,?)}")) {
            cs.setDouble(1, r.getValoracion());
            cs.setString(2, r.getTexto());
            cs.setInt(3, r.getUsuario().getIdUsuario());
            cs.setInt(4, r.getPelicula().getIdPelicula());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    public Resena[] buscarPorPelicula(int idPelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL buscar_resenas_por_pelicula(?)}")) {
            cs.setInt(1, idPelicula);
            ResultSet rs = cs.executeQuery();

            // Contamos cuántas reseñas hay para crear el array
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();

            Resena[] resenas = new Resena[size];
            int index = 0;

            while (rs.next()) {
                Resena r = new Resena();
                r.setIdReseña(rs.getInt("id_resena"));
                r.setValoracion(rs.getDouble("valoracion"));
                r.setTexto(rs.getString("texto"));
                r.setFecha(rs.getTimestamp("fecha").toLocalDateTime());

                // Creamos objetos Usuario y Pelicula con solo el ID (podrías cargar más si quieres)
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                r.setUsuario(u);

                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                r.setPelicula(p);

                resenas[index++] = r;
            }

            return resenas;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    @Override
    public void modificar(Resena r) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL modificar_resena(?,?,?)}")) {
            cs.setInt(1, r.getIdReseña());
            cs.setDouble(2, r.getValoracion());
            cs.setString(3, r.getTexto());

            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    @Override
    public void eliminar(Resena r) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_resena(?)}")) {
            cs.setInt(1, r.getIdReseña());
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }
}