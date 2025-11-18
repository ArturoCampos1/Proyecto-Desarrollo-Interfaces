package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    @FXML
    private Button crearCuenta;

    private Usuario usuario;

    @FXML
    private void initialize() {
        // Click en la tarjeta de búsqueda abre el buscador
        if (paneBusqueda != null) {
            paneBusqueda.setOnMouseClicked(event -> abrirVentana());
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
            mostrarError("No se pudo abrir el buscador");
        }
    }

    @FXML
    private void abrirVistaInicioSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaInicioSesion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo abrir la vista de inicio de sesión");
        }
    }

    @FXML
    private void abrirVistaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaRegistro.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo abrir la vista de registro");
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblBienvenida != null && usuario != null) {
            lblBienvenida.setText("Bienvenida " + usuario.getNombreUsuario());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
