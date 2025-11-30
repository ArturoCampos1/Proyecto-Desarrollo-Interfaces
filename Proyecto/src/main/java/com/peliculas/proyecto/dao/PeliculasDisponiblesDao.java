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
                    String genero = rs.getString("genero");
                    int disponible = rs.getInt("disponible");
                    String pathBanner = rs.getString("url_photo");

                    Pelicula pelicula = new Pelicula();
                    pelicula.setIdPelicula(idPelicula);
                    pelicula.setTitulo(titulo);
                    pelicula.setGenero(genero);
                    pelicula.setDisponible(disponible);
                    pelicula.setPathBanner(pathBanner);

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

}
