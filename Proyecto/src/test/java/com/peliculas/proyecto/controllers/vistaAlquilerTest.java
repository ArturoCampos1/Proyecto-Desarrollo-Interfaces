package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculaFavorita;
import com.peliculas.proyecto.dto.Usuario;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class vistaAlquilerTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @Test
    void crearTarjetasAlquiler_devuelveTarjetas() throws Exception {
        vistaAlquiler va = new vistaAlquiler();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombreUsuario("usuario1");

        Field usuarioField = vistaAlquiler.class.getDeclaredField("usuarioActual");
        usuarioField.setAccessible(true);
        usuarioField.set(va, usuario);

        Pelicula p1 = new Pelicula();
        p1.setIdPelicula(1);
        p1.setTitulo("Matrix");
        p1.setDirector("Wachowski");
        p1.setResumen("Película de ciencia ficción");
        p1.setValoracion(8.5);
        p1.setDisponible(5);
        p1.setPrecio(3.5);
        p1.setPathBanner("/img/matrix.jpg");

        Pelicula p2 = new Pelicula();
        p2.setIdPelicula(2);
        p2.setTitulo("Inception");
        p2.setDirector("Christopher Nolan");
        p2.setResumen("Sueños dentro de sueños");
        p2.setValoracion(9);
        p2.setDisponible(2);
        p2.setPrecio(4.0);
        p2.setPathBanner("/img/inception.jpg");

        ArrayList<Pelicula> peliculas = new ArrayList<>();
        peliculas.add(p1);
        peliculas.add(p2);

        ArrayList<VBox> tarjetas = va.crearTarjetasAlquiler(peliculas);

        assertNotNull(tarjetas);
        assertEquals(2, tarjetas.size());

        for (VBox vbox : tarjetas) {
            assertTrue(vbox instanceof VBox);
            assertTrue(vbox.getChildren().size() >= 8); // img, labels, botones...
        }
    }
}
