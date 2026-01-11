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

import javafx.event.ActionEvent;
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
    @FXML
    private Button darkLight;

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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el buscador");
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de inicio de sesión");
        }
    }

    @FXML
    private void abrirAccesoAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaInicioSesionAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de inicio de sesión admin");
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de registro " + e.getMessage());
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblBienvenida != null && usuario != null) {
            lblBienvenida.setText("Bienvenida " + usuario.getNombreUsuario());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        switch (tipo) {
            case ERROR:
                alert.getDialogPane().getStyleClass().add("alert-error");
                break;
            case INFORMATION:
                alert.getDialogPane().getStyleClass().add("alert-info");
                break;
            default:
                alert.getDialogPane().getStyleClass().add("alert-info");
                break;
        }

        alert.showAndWait();
    }


    public void abrirPanelUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Parent root = loader.load();

            // Pasar usuario al panel si luego lo usas
            vistaPanelUsuario controller = loader.getController();
            // controller.setUsuario(usuario);  // si quieres enviarle el usuario después

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el panel de usuario");
        }
    }

}