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

public class vistaListasPublicas {

    @FXML private ListView<String> listaUsuarios;
    @FXML private ListView<String> listaListas;      // 🔹 corregido: coincide con FXML
    @FXML private ListView<String> listaPeliculas;
    @FXML private Button btnVolver;

    private Usuario usuarioActual;
    private ArrayList<Usuario> otrosUsuarios;
    private ArrayList<Lista> listasSeleccionadas;
    private ArrayList<Pelicula> peliculasSeleccionadas;

    // Recibe el usuario actual desde vistaPanelUsuario
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarUsuarios();
    }

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

    // 🔹 Cargar todos los usuarios excepto el actual
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

    // 🔹 Cargar listas del usuario seleccionado
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

    // 🔹 Cargar películas de la lista seleccionada
    private void cargarPeliculasDeLista() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) return;

        Lista lista = listasSeleccionadas.get(index);
        try {
            peliculasSeleccionadas = PeliculaDao.getInstance().obtenerPeliculasDeLista(lista.getIdLista());
            listaPeliculas.getItems().clear();

            for (Pelicula p : peliculasSeleccionadas) {
                listaPeliculas.getItems().add(p.getTitulo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Volver al panel de usuario
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
