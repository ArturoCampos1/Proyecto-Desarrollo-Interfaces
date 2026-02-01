package com.peliculas.proyecto.dao;

import com.peliculas.proyecto.dto.Administrador;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de AdminDao")
public class AdminDaoTest {

    AdministradorDao administradorDao = new AdministradorDao();

    @BeforeEach
    void setUp() {
        System.out.println("Preparando test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finalizado.");
    }

    @Test
    @DisplayName("Prueba de login - Correcto")
    void testLoginCorrecto() throws SQLException {
        String nombreUsuario = "admin";
        String contrasena = "admin";

        Administrador admin = administradorDao.login(nombreUsuario, contrasena);

        assertNotNull(admin);
    }

    @Test
    @DisplayName("Prueba de login - Incorrecto")
    void testLoginIncorrecto() throws SQLException {
        String nombreUsuario = "error";
        String contrasena = "error";

        Administrador admin = administradorDao.login(nombreUsuario, contrasena);

        assertNull(admin);
    }

    @Test
    @DisplayName("Contraseña incorrecta con usuario existente")
    void contrasenaIncorrecta() throws SQLException {

        Administrador admin = administradorDao.login("admin", "mal");

        assertNull(admin);
        assertEquals("contrasena", administradorDao.getUltimoErrorLogin());
    }

    @Test
    @DisplayName("Usuario y contraseña incorrectos")
    void usuarioYContrasenaIncorrectos() throws SQLException {

        Administrador admin = administradorDao.login("noExiste", "mal");

        assertNull(admin);
        assertEquals("ambos", administradorDao.getUltimoErrorLogin());
    }
}
