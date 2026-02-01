package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Alquiler;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class vistaPrestamosTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX ya estaba iniciado
        }
    }

    @Test
    void tiempoRestante_conDias() {
        vistaPrestamos vp = new vistaPrestamos();

        Timestamp futura = new Timestamp(
                System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000
        );

        String resultado = vp.tiempoRestante(futura);

        assertTrue(resultado.contains("días"));
    }

    @Test
    void tiempoRestante_conHoras() {
        vistaPrestamos vp = new vistaPrestamos();

        Timestamp futura = new Timestamp(
                System.currentTimeMillis() + 3L * 60 * 60 * 1000
        );

        String resultado = vp.tiempoRestante(futura);

        assertTrue(resultado.contains("h"));
    }

    @Test
    void crearTarjetasAlquiladas_devuelveTarjetas() throws Exception {

        vistaPrestamos vp = new vistaPrestamos();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Field usuarioField =
                vistaPrestamos.class.getDeclaredField("usuarioActual");
        usuarioField.setAccessible(true);
        usuarioField.set(vp, usuario);

        Pelicula p = new Pelicula();
        p.setIdPelicula(1);
        p.setTitulo("Matrix");

        ArrayList<Pelicula> peliculas = new ArrayList<>();
        peliculas.add(p);

        Alquiler a = new Alquiler();
        a.setIdPelicula(1);
        a.setFechaDevolucion(
                new Timestamp(System.currentTimeMillis() + 60_000)
        );

        ArrayList<Alquiler> alquileres = new ArrayList<>();
        alquileres.add(a);

        ArrayList<?> resultado =
                vp.crearTarjetasAlquiladas(peliculas, alquileres);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}