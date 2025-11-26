package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class vistaPanelUsuario {

    @FXML private ImageView iconUser;
    @FXML private ImageView iconSearch;

    @FXML private StackPane cardListas;
    @FXML private StackPane cardPrestamos;
    @FXML private StackPane cardAlquilar;

    @FXML private Button btnVolver;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

            System.out.println("Buscador abierto correctamente");

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

    private void volverAMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {

        iconUser.setOnMouseClicked(e -> abrirVistaPerfil());
        iconSearch.setOnMouseClicked(e -> abrirBuscador());
        cardListas.setOnMouseClicked(e -> abrirVistaListas());

        iconUser.setCursor(Cursor.HAND);
        iconSearch.setCursor(Cursor.HAND);
        cardListas.setCursor(Cursor.HAND);
        cardPrestamos.setCursor(Cursor.HAND);
        cardAlquilar.setCursor(Cursor.HAND);
        btnVolver.setCursor(Cursor.HAND);

        cardPrestamos.setOnMouseClicked(e -> {});
        cardAlquilar.setOnMouseClicked(e -> {});

        btnVolver.setOnAction(this::volverAMain);
    }
}
