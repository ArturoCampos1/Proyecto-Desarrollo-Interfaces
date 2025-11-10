package com.peliculas.proyecto.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class vistaMain {

    @FXML
    private Pane paneBusqueda;

    @FXML
    private void initialize() {
        paneBusqueda.setOnMouseClicked(event -> abrirVentana());
    }

    private void abrirVentana() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaBuscadorPeliculas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
