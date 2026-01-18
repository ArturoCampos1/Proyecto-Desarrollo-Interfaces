package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dao.ListaDao;
import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dto.Usuario;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Controlador de la vista de listas públicas.
 * Permite ver las listas de otros usuarios y sus películas,
 * y regresar al panel del usuario actual.
 *
 * @author Iker Sillero
 */
public class vistaListasPublicas {

    @FXML private ListView<String> listaUsuarios;
    @FXML private ListView<String> listaListas;
    @FXML private ListView<String> listaPeliculas;
    @FXML private Button btnVolver;

    private Usuario usuarioActual;
    private ArrayList<Usuario> otrosUsuarios;
    private ArrayList<Lista> listasSeleccionadas;
    private ArrayList<Pelicula> peliculasSeleccionadas;

    /**
     * Establece el usuario actual y carga los demás usuarios.
     *
     * @param usuario Usuario actual
     * @author Iker Sillero
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarUsuarios();
    }

    /**
     * Inicializa la vista.
     * Configura los eventos de los botones y de los ListView
     * para cargar listas y películas según la selección del usuario.
     *
     * @author Iker Sillero
     */
    @FXML
    private void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());

        listaUsuarios.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                cargarListasDeUsuario();
            }
        });

        listaListas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                cargarPeliculasDeLista();
            }
        });
    }

    /**
     * Carga todos los usuarios registrados excepto el usuario actual
     * y los muestra en el ListView.
     *
     * @author Iker Sillero
     */
    private void cargarUsuarios() {
        try {
            otrosUsuarios = UsuarioDao.getInstance().obtenerUsuariosExcepto(usuarioActual.getNombreUsuario());
            listaUsuarios.getItems().clear();
            for (Usuario u : otrosUsuarios) {
                listaUsuarios.getItems().add(u.getNombreUsuario());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga las listas del usuario seleccionado en el ListView de listas.
     *
     * @author Iker Sillero
     */
    private void cargarListasDeUsuario() {
        int index = listaUsuarios.getSelectionModel().getSelectedIndex();
        if (index == -1) return;

        Usuario seleccionado = otrosUsuarios.get(index);
        try {
            listasSeleccionadas = ListaDao.getInstance().obtenerPorNombreUsuario(seleccionado.getNombreUsuario());
            listaListas.getItems().clear();
            listaPeliculas.getItems().clear();

            for (Lista l : listasSeleccionadas) {
                listaListas.getItems().add(l.getNombreLista());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga las películas de la lista seleccionada
     * y las muestra en el ListView correspondiente.
     *
     * @author Iker Sillero
     */
    private void cargarPeliculasDeLista() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) return;

        Lista lista = listasSeleccionadas.get(index);
        try {
            peliculasSeleccionadas = PeliculaDao.getInstance().obtenerPeliculasDeLista(lista.getIdLista());
            listaPeliculas.getItems().clear();

            int cont = 1;
            for (Pelicula p : peliculasSeleccionadas) {
                listaPeliculas.getItems().add(cont + " ➡ " + p.getTitulo() + " (" + p.getAnioSalida() + "): " + p.getGenero() );
                cont++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve al panel del usuario cargando la vista correspondiente.
     *
     * @author Iker Sillero
     */
    private void volverAlPanelUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Scene scene = new Scene(loader.load());
            vistaPanelUsuario controller = loader.getController();
            controller.setUsuario(usuarioActual);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
