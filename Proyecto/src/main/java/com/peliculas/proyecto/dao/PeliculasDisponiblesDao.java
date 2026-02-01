package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO encargado de gestionar las películas disponibles en la base de datos.
 *
 * Proporciona métodos para obtener todas las películas disponibles
 * y eliminar una película concreta.
 *
 * @author Arturo Campos
 */
public class PeliculasDisponiblesDao {

    /**
     * Obtiene todas las películas disponibles almacenadas en la base de datos.
     *
     * Ejecuta un procedimiento almacenado que devuelve las películas
     * marcadas como disponibles y las transforma en objetos {@link Pelicula}.
     *
     * @return Lista de películas disponibles
     *
     * @author Arturo Campos
     */
    public ArrayList<Pelicula> obtenerPeliculasDispos(){
        ArrayList<Pelicula> peliculasDisponibles = new ArrayList<>();
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_disponibles()}")) {
            // Ejecutamos el stored procedure y obtenemos resultados
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    int idPelicula = rs.getInt("id_pelicula");
                    String fecha = rs.getString("anio_salida");
                    String titulo = rs.getString("titulo");
                    double valoracion = rs.getDouble("valoracion");
                    String genero = rs.getString("genero");
                    String resumen = rs.getString("resumen");
                    String director = rs.getString("director");
                    int disponible = rs.getInt("disponible");
                    String pathBanner = rs.getString("url_photo");
                    double precio = rs.getDouble("precio");

                    Pelicula pelicula = new Pelicula();
                    pelicula.setIdPelicula(idPelicula);
                    pelicula.setTitulo(titulo);
                    pelicula.setAnioSalida(fecha);
                    pelicula.setDirector(director);
                    pelicula.setResumen(resumen);
                    pelicula.setValoracion(valoracion);
                    pelicula.setGenero(genero);
                    pelicula.setDisponible(disponible);
                    pelicula.setPathBanner(pathBanner);
                    pelicula.setPrecio(precio);

                    peliculasDisponibles.add(pelicula);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.cerrarConexion();
        }

        return peliculasDisponibles;
    }

    /**
     * Elimina una película disponible de la base de datos a partir de su ID.
     *
     * @param idPelicula Identificador de la película a eliminar
     * @return true si la película fue eliminada correctamente, false en caso contrario
     *
     * @author Arturo Campos
     */
    public static boolean eliminarPelicula(int idPelicula) {
        boolean eliminado = false;
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        String sql = "DELETE FROM peliculas_disponibles WHERE id_pelicula = ?";

        try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPelicula); // pasamos el ID de la película

            int filasAfectadas = ps.executeUpdate(); // ejecutamos la sentencia
            eliminado = filasAfectadas > 0; // true si eliminó al menos una fila
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.cerrarConexion();
        }

        return eliminado;
    }
}
