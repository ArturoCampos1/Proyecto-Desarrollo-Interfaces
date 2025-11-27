package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.ListaPeliculaDao;
import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class vistaListaPeliculas {

    @FXML private ListView<String> listaPeliculas;
    @FXML private Button btnAgregar;
    @FXML private Button btnQuitar;
    @FXML private Button btnVolver;
    @FXML private Text txtTituloLista;

    private Lista lista;
    private Usuario usuario;
    private ArrayList<Pelicula> peliculasLista;

    public void setData(Usuario usuario, Lista lista) {
        this.usuario = usuario;
        this.lista = lista;
        txtTituloLista.setText("Películas de: " + lista.getNombreLista());
        cargarPeliculas();
    }

    @FXML
    private void initialize() {
        btnVolver.setOnAction(e -> volverAListas());
        btnAgregar.setOnAction(e -> agregarPelicula());
        btnQuitar.setOnAction(e -> quitarPelicula());
    }

    private void cargarPeliculas() {
        try {
            peliculasLista = PeliculaDao.getInstance().obtenerPeliculasDeLista(lista.getIdLista());
            listaPeliculas.getItems().clear();

            for (Pelicula p : peliculasLista) {
                listaPeliculas.getItems().add(p.getTitulo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarPelicula() {
        try {
            ArrayList<Pelicula> disponibles = PeliculaDao.getInstance().obtenerPeliculas();

            ChoiceDialog<Pelicula> dialog = new ChoiceDialog<>(null, disponibles);
            dialog.setTitle("Añadir película");
            dialog.setHeaderText("Selecciona una película para añadir");
            dialog.setContentText("Película:");

            dialog.showAndWait().ifPresent(pelicula -> {
                try {
                    ListaPeliculaDao.getInstance().agregarPelicula(lista, pelicula);
                    cargarPeliculas();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitarPelicula() {
        int index = listaPeliculas.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            mostrarAlerta("Error", "Selecciona una película para quitar.", Alert.AlertType.ERROR);
            return;
        }

        Pelicula pelicula = peliculasLista.get(index);

        try {
            ListaPeliculaDao.getInstance().quitarPelicula(lista, pelicula);
            cargarPeliculas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void volverAListas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaListas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
