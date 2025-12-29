package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PeliculasDisponiblesDao {

    public static ArrayList<Pelicula> obtenerPeliculasDispos(){
        ArrayList<Pelicula> peliculasDisponibles = new ArrayList<>();
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_disponibles()}")) {
            // Ejecutamos el stored procedure y obtenemos resultados
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    int idPelicula = rs.getInt("id_pelicula");
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
