package com.peliculas.proyecto.dao;

import java.sql.SQLException;

public interface CRUD<T> {

        void crear(T obj) throws SQLException;
        void modificar(T obj) throws SQLException;
        void eliminar(T obj) throws SQLException;

    }

