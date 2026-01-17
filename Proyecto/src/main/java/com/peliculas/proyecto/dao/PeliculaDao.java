package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO encargado de la gestión de las películas.
 * Permite crear, actualizar, eliminar y consultar películas.
 * Implementa el patrón Singleton para garantizar una única instancia.
 *
 * @author Kevin Mejías y Arturo Campos
 */
public class PeliculaDao {

    /** Instancia única del DAO (Singleton) */
    private static PeliculaDao instance;

    /** Constructor vacío */
    public PeliculaDao() {}

    /**
     * Devuelve la instancia única del DAO.
     *
     * @return instancia de {@code PeliculaDao}
     *
     * @author Kevin Mejías
     */
    public static PeliculaDao getInstance() {
        if (instance == null) {
            instance = new PeliculaDao();
        }
        return instance;
    }

    /**
     * Crea una nueva película usando el procedimiento almacenado {@code crear_pelicula}.
     *
     * @param p Película a crear
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
    public void crear(Pelicula p) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

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
                        p.setIdPelicula(rs.getInt("id_pelicula"));
                        System.out.println("DEBUG -> Película insertada con id: " + p.getIdPelicula());
                    }
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Actualiza los datos de una película existente.
     *
     * @param p Película a actualizar
     * @return {@code true} si se actualizó correctamente, {@code false} en caso contrario
     *
     * @author Arturo Campos
     */
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
            pst.setInt(10, p.getIdPelicula());

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Actualiza la disponibilidad de una película restando la cantidad indicada.
     *
     * @param p Película a actualizar
     * @param cantidad Cantidad a restar de la disponibilidad
     * @return {@code true} si se actualizó correctamente, {@code false} en caso contrario
     *
     * @author Arturo Campos
     */
    public boolean actualizarDisponibilidad(Pelicula p, int cantidad) {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        String sql = "UPDATE pelicula SET disponible = ? WHERE id_pelicula = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, p.getDisponible() - cantidad);
            pst.setInt(2, p.getIdPelicula());

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Busca una película por su título.
     *
     * @param nombre Título de la película
     * @return Objeto {@code Pelicula} si se encuentra, {@code null} si no
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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
                    p.setDisponible(rs.getInt("disponible"));
                    p.setPathBanner(rs.getString("url_photo"));
                    p.setValoracion(rs.getDouble("valoracion"));
                    p.setPrecio(rs.getDouble("precio"));
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return p;
    }

    /**
     * Obtiene todas las películas de la base de datos.
     *
     * @return Lista de películas
     *
     * @author Arturo Campos
     */
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

    /**
     * Obtiene las películas de una lista específica.
     *
     * @param idLista ID de la lista
     * @return Lista de películas
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Elimina una película de la base de datos.
     *
     * @param idPelicula ID de la película a eliminar
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * @author Kevin Mejías
     */
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

    /**
     * Devuelve una película aumentando su disponibilidad en 1.
     *
     * @param idPelicula ID de la película a devolver
     * @return {@code true} si se actualizó correctamente, {@code false} en caso contrario
     *
     * @author Kevin Mejías
     */
    public boolean devolverPelicula(int idPelicula) {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        String sql = "UPDATE pelicula SET disponible = disponible + 1 WHERE id_pelicula = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idPelicula);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.cerrarConexion();
        }
    }
}