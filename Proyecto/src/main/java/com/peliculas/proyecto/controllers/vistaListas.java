package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.ListaDao;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class vistaListas {

    @FXML private ListView<String> listaListas;
    @FXML private Button btnCrearLista;
    @FXML private Button btnEliminarLista;
    @FXML private Button btnVolver;

    private Usuario usuario;
    private ArrayList<Lista> listasUsuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarListas();
    }

    @FXML
    private void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        btnCrearLista.setOnAction(e -> crearLista());
        btnEliminarLista.setOnAction(e -> eliminarLista());

        listaListas.setCursor(Cursor.HAND);
        listaListas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                abrirListaPeliculas();
            }
        });

        // 🔹 Personalizar las celdas para que se vean más grandes
        listaListas.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        // Texto un poco más grande y con menos padding
                        setStyle("-fx-font-size: 16px; -fx-padding: 8px;");
                    }
                }
            };
            cell.setPrefHeight(40); // 🔹 más grande que lo normal (~24px), pero más pequeño que los 60px anteriores
            return cell;
        });
    }

    private void cargarListas() {
        try {
            listasUsuario = ListaDao.getInstance().obtenerPorNombreUsuario(usuario.getNombreUsuario());
            listaListas.getItems().clear();
            for (Lista l : listasUsuario) {
                listaListas.getItems().add(l.getNombreLista());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearLista() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva lista");
        dialog.setHeaderText("Crear nueva lista");
        dialog.setContentText("Nombre de la lista:");
        dialog.showAndWait().ifPresent(nombre -> {
            try {
                Lista nueva = new Lista();
                nueva.setNombreLista(nombre);
                nueva.setUsuario(usuario);
                ListaDao.getInstance().crear(nueva);
                cargarListas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void eliminarLista() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            mostrarAlerta("Error", "Selecciona una lista para eliminar.", Alert.AlertType.ERROR);
            return;
        }
        Lista lista = listasUsuario.get(index);
        try {
            ListaDao.getInstance().eliminar(lista);
            cargarListas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirListaPeliculas() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) return;
        Lista listaSeleccionada = listasUsuario.get(index);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListaPeliculas.fxml"));
            Scene scene = new Scene(loader.load());
            vistaListaPeliculas controller = loader.getController();
            controller.setData(usuario, listaSeleccionada);
            Stage stage = (Stage) listaListas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void volverAlPanelUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Scene scene = new Scene(loader.load());
            vistaPanelUsuario controller = loader.getController();
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
