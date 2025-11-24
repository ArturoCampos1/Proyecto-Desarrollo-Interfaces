package com.peliculas.proyecto.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class vistaPanelUsuario {

    @FXML private ImageView iconUser;
    @FXML private Button toggleDark;

    @FXML private StackPane cardListas;
    @FXML private StackPane cardPrestamos;
    @FXML private StackPane cardAlquilar;

    @FXML private Button btnVolver;

    // ------------------------
    // MÉTODO PARA CARGAR PÁGINA EN BLANCO (para las tarjetas)
    // ------------------------
    private void cargarPaginaEnBlanco(ActionEvent event) {
        try {
            AnchorPane blank = new AnchorPane(); // vista vacía
            blank.setStyle("-fx-background-color: white;");

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(blank, 1000, 800));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------
    // MÉTODO PARA VOLVER A vistaMain.fxml
    // ------------------------
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
        // Navega a una página en blanco al hacer clic en cada tarjeta
        cardListas.setOnMouseClicked(event -> cargarPaginaEnBlanco(null));
        cardPrestamos.setOnMouseClicked(event -> cargarPaginaEnBlanco(null));
        cardAlquilar.setOnMouseClicked(event -> cargarPaginaEnBlanco(null));

        // Botón volver → ahora abre vistaMain.fxml
        btnVolver.setOnAction(this::volverAMain);

        // Botón modo oscuro (sin función aún)
        toggleDark.setOnAction(e -> {
            System.out.println("Modo oscuro aún no implementado.");
        });
    }
}
