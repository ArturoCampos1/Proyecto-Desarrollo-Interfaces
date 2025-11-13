package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class vistaMain {

    @FXML
    private Pane paneBusqueda;
    @FXML
    private Label lblBienvenida;

    @FXML
    private Button inicioSesion;

    private Usuario usuario;

    @FXML
    private void initialize() {
        paneBusqueda.setOnMouseClicked(event -> abrirVentana());

        if (inicioSesion != null) {
            inicioSesion.setOnAction(event -> abrirVistaLogin());
        }
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

    private void abrirVistaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaLogin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
        if (lblBienvenida != null && usuario != null){
            lblBienvenida.setText("Bienvenida " + usuario.getNombreUsuario());
        }
    }
}