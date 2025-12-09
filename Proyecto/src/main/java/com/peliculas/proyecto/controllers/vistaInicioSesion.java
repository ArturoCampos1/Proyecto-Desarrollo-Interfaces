package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class vistaInicioSesion {

    @FXML
    private Pane paneLogin;
    @FXML
    private TextField campoUsuario;
    @FXML
    private PasswordField campoContraseña;
    @FXML
    private Button botonLogin;
    @FXML
    private Button botonVolver;

    @FXML
    private void initialize() {

        botonLogin.setOnMouseClicked(event -> procesarLogin());
        botonVolver.setOnMouseClicked(event -> volverAMain());

        botonLogin.setCursor(Cursor.HAND);
        botonVolver.setCursor(Cursor.HAND);

        botonLogin.getStyleClass().add("btnMorado");
    }

    @FXML
    private void procesarLogin() {
        String nombreUsuario = campoUsuario.getText();
        String contrasena = campoContraseña.getText();

        try {
            UsuarioDao usuarioDao = UsuarioDao.getInstance();
            Usuario usuario = usuarioDao.login(nombreUsuario, contrasena);

            if (usuario != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Login correcto", "¡Bienvenido, " + usuario.getNombreUsuario() + "!");
                abrirPanelUsuarioDesdeLogin(usuario);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Login fallido", "Usuario o contraseña incorrectos");
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudo verificar el usuario");
        }
    }

    private void abrirPanelUsuarioDesdeLogin(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Scene scene = new Scene(loader.load());

            vistaPanelUsuario controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el panel de usuario");
        }
    }

    @FXML
    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver a la pantalla principal");
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
}
