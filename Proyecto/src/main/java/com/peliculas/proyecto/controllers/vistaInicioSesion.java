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
        String nombreUsuario = campoUsuario.getText().trim();
        String contrasena = campoContraseña.getText().trim();

        // ✅ Validación de campos vacíos
        if (nombreUsuario.isEmpty() && contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Debes rellenar el usuario y la contraseña");
            return;
        }

        if (nombreUsuario.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                    "Debes rellenar el usuario");
            return;
        }

        if (contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                    "Debes rellenar la contraseña");
            return;
        }

        try {
            UsuarioDao usuarioDao = UsuarioDao.getInstance();
            Usuario usuario = usuarioDao.login(nombreUsuario, contrasena);

            // ✅ Login correcto
            if (usuario != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Login correcto",
                        "¡Bienvenido, " + usuario.getNombreUsuario() + "!");
                abrirPanelUsuarioDesdeLogin(usuario);
                return;
            }

            // ✅ Login incorrecto → analizamos el motivo
            String error = usuarioDao.getUltimoErrorLogin();

            switch (error) {

                case "usuario":
                    mostrarAlerta(Alert.AlertType.ERROR, "Login fallido",
                            "Usuario incorrecto");
                    break;

                case "contrasena":
                    mostrarAlerta(Alert.AlertType.ERROR, "Login fallido",
                            "Contraseña incorrecta");
                    break;

                case "ambos":
                    mostrarAlerta(Alert.AlertType.ERROR, "Login fallido",
                            "Usuario y contraseña incorrectos");
                    break;

                default:
                    mostrarAlerta(Alert.AlertType.ERROR, "Login fallido",
                            "Credenciales incorrectos");
                    break;
            }

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD",
                    "No se pudo verificar el usuario");
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
