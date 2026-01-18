package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculaFavorita;
import com.peliculas.proyecto.dto.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DAO encargado de gestionar las películas favoritas de los usuarios.
 *
 * Permite añadir y eliminar películas favoritas, así como consultar
 * las películas favoritas de todos los usuarios o de uno en concreto.
 *
 * @author Arturo Campos
 */
public class PeliculaFavoritaDao {

    /**
     * Añade una película a la lista de favoritas de un usuario.
     *
     * @param u Usuario que marca la película como favorita
     * @param p Película que se añade a favoritos
     *
     * @author Arturo Campos
     */
    public void añadirPeliculaFav(Usuario u, Pelicula p){
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (PreparedStatement ps = con.prepareCall(
                "INSERT INTO peliculas_favoritas (id_usuario, id_pelicula) VALUES (?, ?)")) {
            ps.setInt(1, u.getIdUsuario());
            ps.setInt(2, p.getIdPelicula());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Elimina una película de la lista de favoritas de un usuario.
     *
     * @param u Usuario al que se le elimina la película favorita
     * @param p Película que se elimina de favoritos
     *
     * @author Arturo Campos
     */
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

    /**
     * Obtiene todas las películas favoritas registradas en el sistema.
     *
     * @return Lista de películas favoritas
     *
     * @author Arturo Campos
     */
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

    /**
     * Obtiene las películas favoritas de un usuario concreto.
     *
     * @param idUsuario Identificador del usuario
     * @return Lista de películas favoritas del usuario
     *
     * @author Arturo Campos
     */
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
