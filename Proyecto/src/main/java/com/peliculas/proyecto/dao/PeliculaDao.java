package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.conexion.Conexion;
import com.peliculas.proyecto.dto.Pelicula;

import java.sql.*;
import java.util.ArrayList;

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

        try (CallableStatement cs = con.prepareCall("{CALL crear_pelicula(?,?,?,?,?,?,?)}")) {
            cs.setString(1, p.getTitulo());
            cs.setInt(2, Integer.parseInt(p.getAnioSalida()));
            cs.setString(3, p.getDirector());
            cs.setString(4, p.getResumen());
            cs.setString(5, p.getGenero());
            cs.setBigDecimal(6, new java.math.BigDecimal(String.valueOf(p.getValoracion())));
            cs.setInt(7, p.getDisponible());

            boolean hasResult = cs.execute();

            if (hasResult) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        p.setIdPelicula(rs.getInt("id_pelicula")); // 🔹 asigna el ID generado
                    }
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }
    }

    // Obtener todas las películas disponibles
    public ArrayList<Pelicula> obtenerPeliculas() throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        ArrayList<Pelicula> lista = new ArrayList<>();

        try (CallableStatement cs = con.prepareCall("{CALL obtener_peliculas_disponibles()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPelicula(rs));
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    public ArrayList<Pelicula> buscarPorNombre(String nombre) throws SQLException {
        ArrayList<Pelicula> peliculas = new ArrayList<>();
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;

        try (CallableStatement cs = con.prepareCall("{CALL buscar_peliculas_por_nombre(?)}")) {
            cs.setString(1, nombre);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Pelicula p = new Pelicula();
                    p.setIdPelicula(rs.getInt("id_pelicula"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setAnioSalida(rs.getString("anio_salida"));
                    p.setDirector(rs.getString("director"));
                    p.setResumen(rs.getString("resumen"));
                    p.setGenero(rs.getString("genero"));
                    p.setValoracion(rs.getDouble("valoracion"));
                    p.setDisponible(rs.getInt("disponible"));
                    peliculas.add(p);
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }
        return peliculas;
    }

    private ArrayList<Pelicula> buscarPorCampo(String tipo, String valor) throws SQLException {
        Conexion.abrirConexion();
        Connection con = Conexion.conexion;
        ArrayList<Pelicula> lista = new ArrayList<>();
        String procedimiento;

        switch (tipo) {
            case "nombre": procedimiento = "{CALL buscar_peliculas_por_nombre(?)}"; break;
            case "autor": procedimiento = "{CALL buscar_peliculas_por_autor(?)}"; break;
            case "genero": procedimiento = "{CALL buscar_peliculas_por_genero(?)}"; break;
            default: throw new SQLException("Tipo de búsqueda inválido");
        }

        try (CallableStatement cs = con.prepareCall(procedimiento)) {
            cs.setString(1, valor);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPelicula(rs));
                }
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    // Obtener películas de una lista específica
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

                // ⚠️ No intentes leer columnas que no devuelve el SP
                // Si quieres, puedes inicializar con valores por defecto
                p.setValoracion(0.0);
                p.setDisponible(0);

                lista.add(p);
            }
        } finally {
            Conexion.cerrarConexion();
        }

        return lista;
    }

    // Eliminar película
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

    // Método auxiliar para mapear ResultSet a Pelicula
    private Pelicula mapearPelicula(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setIdPelicula(rs.getInt("id_pelicula"));
        p.setTitulo(rs.getString("titulo"));
        p.setAnioSalida(rs.getString("anio_salida"));
        p.setDirector(rs.getString("director"));
        p.setResumen(rs.getString("resumen"));
        p.setGenero(rs.getString("genero"));
        p.setValoracion(rs.getDouble("valoracion"));
        p.setDisponible(rs.getInt("disponible"));
        return p;
    }
}
