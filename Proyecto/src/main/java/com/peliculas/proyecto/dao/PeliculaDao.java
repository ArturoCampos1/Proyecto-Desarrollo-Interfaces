package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Genero;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDao {

    private static PeliculaDao instance;

    public PeliculaDao() {}

    public static PeliculaDao getInstance() {
        if (instance == null) {
            instance = new PeliculaDao();
        }
        return instance;
    }

    // Crear película
    public void crear(Pelicula p) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

            // Si no existe, llamamos al procedimiento para crear la película
            try (CallableStatement cs = con.prepareCall("{CALL crear_pelicula(?,?,?,?,?,?,?,?,?)}")) {
                cs.setString(1, p.getTitulo());
                cs.setString(2, p.getAnioSalida());
                cs.setString(3, p.getDirector());
                cs.setString(4, p.getResumen());
                cs.setString(5, p.getGenero());
                cs.setString(6, p.getPathBanner());
                cs.setDouble(7, p.getValoracion());
                cs.setInt(8, p.getDisponible());
                cs.setDouble(9, p.getPrecio());

                boolean hasResultSet = cs.execute();

                if (hasResultSet) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            int idGenerado = rs.getInt("id_pelicula");
                            p.setIdPelicula(idGenerado);
                            System.out.println("DEBUG -> Película insertada con id: " + idGenerado);
                        }
                    }
                }
        } finally {
            Conexion.cerrarConexion();
        }
    }

    //Actualizar pelicula
    public boolean actualizar(Pelicula p) {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        String sql = "UPDATE pelicula SET titulo = ?, anio_salida = ?, director = ?, resumen = ?, " +
                "genero = ?, disponible = ?, url_photo = ?, valoracion = ?, precio = ? " +
                "WHERE id_pelicula = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, p.getTitulo());
            pst.setString(2, p.getAnioSalida());
            pst.setString(3, p.getDirector());
            pst.setString(4, p.getResumen());
            pst.setString(5, p.getGenero());
            pst.setInt(6, p.getDisponible());
            pst.setString(7, p.getPathBanner());
            pst.setDouble(8, p.getValoracion());
            pst.setDouble(9, p.getPrecio());

            pst.setInt(10, p.getIdPelicula()); // WHERE

            int rows = pst.executeUpdate();
            con.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Actualizar pelicula
    public boolean actualizarDisponibilidad(Pelicula p, int cantidad) {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        String sql = "UPDATE pelicula SET disponible = ? " +
                "WHERE id_pelicula = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, p.getDisponible() - cantidad);
            pst.setInt(2, p.getIdPelicula()); // WHERE

            int rows = pst.executeUpdate();
            con.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pelicula buscarPorNombre(String nombre) throws SQLException {
        Pelicula p = null;

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "SELECT id_pelicula, titulo, anio_salida, director, resumen, genero, disponible, url_photo, valoracion, precio " +
                "FROM pelicula WHERE titulo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    p = new Pelicula();
                    p.setIdPelicula(rs.getInt("id_pelicula"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setAnioSalida(rs.getString("anio_salida"));
                    p.setDirector(rs.getString("director"));
                    p.setResumen(rs.getString("resumen"));
                    p.setGenero(rs.getString("genero"));
                    p.setDisponible((rs.getInt("disponible")));
                    p.setPathBanner((rs.getString("url_photo")));
                    p.setValoracion((rs.getDouble("valoracion")));
                    p.setPrecio((rs.getDouble("precio")));
                }
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return p;
    }


    public ArrayList<Pelicula> obtenerPeliculas() {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        ArrayList<Pelicula> lista = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_todas_peliculas()}")) {
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setAnioSalida(rs.getString("anio_salida"));
                p.setDirector(rs.getString("director"));
                p.setResumen(rs.getString("resumen"));
                p.setGenero(rs.getString("genero"));
                p.setDisponible(rs.getInt("disponible"));
                p.setPathBanner(rs.getString("url_photo"));
                p.setPrecio(rs.getDouble("precio"));
                p.setValoracion(rs.getDouble("valoracion"));

                lista.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    public ArrayList<Pelicula> obtenerPeliculasDeLista(int idLista) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        ArrayList<Pelicula> lista = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_de_lista(?)}")) {
            cs.setInt(1, idLista);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdPelicula(rs.getInt("id_pelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setAnioSalida(rs.getString("anio_salida"));
                p.setDirector(rs.getString("director"));
                p.setResumen(rs.getString("resumen"));
                p.setGenero(rs.getString("genero"));

                lista.add(p);
            }

        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    // Eliminar película de la lista
    public void eliminar(int idPelicula) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL eliminar_pelicula(?)}")) {
            cs.setInt(1, idPelicula);
            cs.executeUpdate();
        } finally {
            Conexion.cerrarConexion();
        }
    }
}
