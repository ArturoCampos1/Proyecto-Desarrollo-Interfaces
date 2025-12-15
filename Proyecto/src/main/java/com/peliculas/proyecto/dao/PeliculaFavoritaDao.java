package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculaFavorita;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PeliculaFavoritaDao {

    public void añadirPeliculaFav(Usuario u, Pelicula p){
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (PreparedStatement ps = con.prepareCall("INSERT INTO peliculas_favoritas (id_usuario, id_pelicula) VALUES (?, ?)")) {
            ps.setInt(1, u.getIdUsuario());
            ps.setInt(2, p.getIdPelicula());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    public void eliminarPeliculaFav(Usuario u, Pelicula p){
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM peliculas_favoritas WHERE id_usuario = ? AND id_pelicula = ?")) {
            ps.setInt(1, u.getIdUsuario());
            ps.setInt(2, p.getIdPelicula());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar película favorita: " + e.getMessage(), e);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    public ArrayList<PeliculaFavorita> mostrarFavoritos(){
    	ArrayList<PeliculaFavorita> favoritos = new ArrayList<PeliculaFavorita>();
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM peliculas_favoritas")) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PeliculaFavorita peliculaFavorita = new PeliculaFavorita();
                peliculaFavorita.setId_pelicula(rs.getInt("id_usuario"));
                peliculaFavorita.setId_usuario(rs.getInt("id_pelicula"));
                favoritos.add(peliculaFavorita);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener películas favoritas: " + e.getMessage(), e);
        } finally {
            Conexion.cerrarConexion();
        }

        return favoritos;
    }

    public ArrayList<PeliculaFavorita> mostrarFavoritosPorUsuario(int idUsuario) {

        ArrayList<PeliculaFavorita> favoritos = new ArrayList<>();

        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_usuario, id_pelicula FROM peliculas_favoritas WHERE id_usuario = ?"
        )) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PeliculaFavorita peliculaFavorita = new PeliculaFavorita();
                peliculaFavorita.setId_usuario(rs.getInt("id_usuario"));
                peliculaFavorita.setId_pelicula(rs.getInt("id_pelicula"));
                favoritos.add(peliculaFavorita);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error al obtener películas favoritas del usuario " + idUsuario, e
            );
        } finally {
            Conexion.cerrarConexion();
        }

        return favoritos;
    }



}
