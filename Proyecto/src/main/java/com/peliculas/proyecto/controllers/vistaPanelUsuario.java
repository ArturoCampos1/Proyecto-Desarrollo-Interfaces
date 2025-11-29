package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class vistaPanelUsuario {

    @FXML private ImageView iconUser;
    @FXML private ImageView iconSearch;

    @FXML private Pane paneUser;
    @FXML private Pane paneSearch;

    @FXML private StackPane cardListas;
    @FXML private StackPane cardListasPublicas;   // 🔹 NUEVO
    @FXML private StackPane cardPrestamos;
    @FXML private StackPane cardAlquilar;

    @FXML private Button btnVolver;

    // 🔹 NUEVO: Label para mostrar el nombre del usuario
    @FXML private Label lblUsername;
    @FXML private Label lblCorreo;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsername != null && usuario != null) {
            lblUsername.setText(usuario.getNombreUsuario().toUpperCase()); // muestra el nombre en la vista
            lblCorreo.setText(usuario.getCorreo());
        }
    }

    private void abrirVistaPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPerfilUsuario.fxml"));
            Scene scene = new Scene(loader.load());

            vistaPerfilUsuario controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconUser.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirBuscador() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaBuscadorPeliculas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaBuscadorPeliculas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconSearch.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirVistaListas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaListas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) cardListas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirVistaListasPublicas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListasPublicas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaListasPublicas controller = loader.getController();
            controller.setUsuario(usuario); // pasa el usuario actual

            Stage stage = (Stage) cardListasPublicas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void volverAMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {

        // ICONOS
        iconUser.setOnMouseClicked(e -> abrirVistaPerfil());
        iconSearch.setOnMouseClicked(e -> abrirBuscador());

        // CUADRADOS DETRÁS DE LOS ICONOS
        paneUser.setOnMouseClicked(e -> abrirVistaPerfil());
        paneSearch.setOnMouseClicked(e -> abrirBuscador());

        // TARJETAS
        cardListas.setOnMouseClicked(e -> abrirVistaListas());
        cardListasPublicas.setOnMouseClicked(e -> abrirVistaListasPublicas()); // 🔹 abre Listas Públicas
        cardPrestamos.setOnMouseClicked(e -> {});
        cardAlquilar.setOnMouseClicked(e -> {});

        // CURSORES
        iconUser.setCursor(Cursor.HAND);
        iconSearch.setCursor(Cursor.HAND);
        paneUser.setCursor(Cursor.HAND);
        paneSearch.setCursor(Cursor.HAND);

        cardListas.setCursor(Cursor.HAND);
        cardListasPublicas.setCursor(Cursor.HAND); // 🔹 nuevo
        cardPrestamos.setCursor(Cursor.HAND);
        cardAlquilar.setCursor(Cursor.HAND);
        btnVolver.setCursor(Cursor.HAND);

        // BOTÓN VOLVER
        btnVolver.setOnAction(this::volverAMain);
    }
}
